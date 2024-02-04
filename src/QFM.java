package src;

import java.util.ArrayList;

import src.DSPerLevel.BookKeepingPerLevel;
import src.DSPerLevel.TaxaPerLevelWithPartition;
import src.InitialPartition.IMakePartition;
import src.PreProcessing.GeneTrees;
import src.Taxon.DummyTaxon;
import src.Taxon.RealTaxon;
import src.Tree.Tree;
import src.Tree.TreeNode;

public class QFM {
    
    public RealTaxon[] realTaxa;
    public DummyTaxon[] dummyTaxa;
    public IMakePartition initPartition;
    public GeneTrees geneTrees;
    private int level;
    private int realTaxaCount;

    static double EPS = 1e-5;

    public QFM(GeneTrees trees, RealTaxon[] realTaxa, DummyTaxon[] dts, IMakePartition initPartition, int realTaxaCount){
        this.realTaxa = realTaxa;
        this.initPartition = initPartition;
        this.geneTrees = trees;
        this.dummyTaxa = dts;
        this.realTaxaCount = realTaxaCount;
    }

    public Tree runWQFM(){
        this.level = 0;

        var y = initPartition.makePartition(realTaxa, this.dummyTaxa, true);
        var x = new TaxaPerLevelWithPartition(realTaxa, this.dummyTaxa, y.realTaxonPartition, y.dummyTaxonPartition, this.realTaxaCount);
        BookKeepingPerLevel initialBook = new BookKeepingPerLevel(geneTrees, x, Config.ALLOW_SINGLETON);

        return recurse(initialBook);

    }

    private Tree recurse(
        BookKeepingPerLevel book
    ){

        this.level++;
        int itrCount = 0;

        boolean allowSingleton = Config.ALLOW_SINGLETON;

        // System.out.println("level : " + level);

        if(allowSingleton){
            if(Config.USE_LEVEL_BASED_SINGLETON_THRESHOLD){
                if(this.level > Config.MAX_LEVEL_MULTIPLIER * this.realTaxa.length){
                    System.out.println("Made false");
                    System.out.println("rts : " + book.taxas.realTaxonCount + " dts : " + book.taxas.dummyTaxonCount);
                    System.out.println("part[0]: " + book.taxas.getTaxonCountInPartition(0) + " part[1]: " + book.taxas.getTaxonCountInPartition(1));
                    allowSingleton = false;
                }
            }
            else{
                int maxDepth = 0;
                for(var x : book.taxas.dummyTaxa){
                    maxDepth = Math.max(maxDepth, x.nestedLevel);
                }
                if(maxDepth > Config.SINGLETON_THRESHOLD * this.realTaxa.length){
                    allowSingleton = false;
                }
            }
        }
        
        while(oneInteration(book) ){
            itrCount++;
            if(itrCount > Config.MAX_ITERATION){
                System.out.println("Max iteration reached");
                break;
            }
            
        }

        // System.out.println( "#iterations: " + itrCount);

        Tree[] trees = new Tree[2];

        var x = book.divide(initPartition, allowSingleton);
        int i = 0;
        int[] dummyIds = new int[2];
        
        for(var taxaWPart : x){
            var childBooks = new BookKeepingPerLevel(geneTrees, taxaWPart, allowSingleton);
            if(childBooks.taxas.smallestUnit){
                trees[i] = childBooks.taxas.createStar();
            }
            else{
                trees[i] = recurse(childBooks);
            }
            dummyIds[i++] = childBooks.taxas.dummyTaxa[childBooks.taxas.dummyTaxonCount - 1].id;
        }

        this.level--;

        TreeNode[] dtNodes = new TreeNode[2];

        for(i = 0; i < 2; ++i){
            for(var node : trees[i].nodes){
                if(node.info.dummyTaxonId == dummyIds[i]){
                    dtNodes[i] = node;
                }
            }
            if(dtNodes[i] == null){
                System.out.println("Error: Dummy node not found");
                System.exit(-1);
            }
            if(dtNodes[i].childs != null){
                System.out.println("Error: Dummy node should be leaf");
                System.exit(-1);
            }
        }
        
        trees[0].reRootTree(dtNodes[0]);
        if(dtNodes[0].childs.size() > 1){
            System.out.println("Error: Dummy node should have only one child after reroot");
            System.exit(-1);
        }
        dtNodes[0].childs.get(0).setParent(dtNodes[1].parent);
        dtNodes[1].parent.childs.remove(dtNodes[1]);
        dtNodes[1].parent.childs.add(dtNodes[0].childs.get(0));
        trees[1].nodes.addAll(trees[0].nodes);

        return trees[1];
    }

    static class Swap{
        public int index;
        public boolean isDummy;
        public double gain;

        public Swap(int i, boolean id, double g){
            this.index = i;
            this.isDummy = id;
            this.gain = g;
        }
    }
    

    public static Swap swapMax(BookKeepingPerLevel book, double[][] rtGains, double[] dtGains, boolean[] rtLocked, boolean[] dtLocked){

        int maxGainIndex = -1;
        double maxGain = 0;

        for(int i = 0; i < book.taxas.realTaxonCount; ++i){
            if(rtLocked[i]) continue;
            int partition = book.taxas.inWhichPartitionRealTaxonByIndex(i);
            if(book.taxas.getTaxonCountInPartition(partition) > 2 || (book.allowSingleton && book.taxas.getTaxonCountInPartition(partition) > 1) ){
                if(maxGainIndex == -1){
                    maxGain = rtGains[i][partition];
                    maxGainIndex = i;
                }
                else if(maxGain < rtGains[i][partition]){
                    maxGain = rtGains[i][partition];
                    maxGainIndex = i;
                }
            }
        }

        boolean dummyChosen = false;

        for(int i = 0; i < book.taxas.dummyTaxonCount; ++i){
            if(dtLocked[i]) continue;
            int partition = book.taxas.inWhichPartitionDummyTaxonByIndex(i);
            if(book.taxas.getTaxonCountInPartition(partition) > 2 || (book.allowSingleton && book.taxas.getTaxonCountInPartition(partition) > 1)){
                if(maxGainIndex == -1){
                    maxGain = dtGains[i];
                    maxGainIndex = i;
                    dummyChosen = true;
                }
                else if(maxGain < dtGains[i]){
                    maxGain = dtGains[i];
                    maxGainIndex = i;
                    dummyChosen = true;
                }
            }
        }

        if(maxGainIndex == -1) return null;

        book.swapTaxon(maxGainIndex, dummyChosen);
        if(dummyChosen){
            dtLocked[maxGainIndex] = true;
        }
        else{
            rtLocked[maxGainIndex] = true;
        }

        
        return new Swap(maxGainIndex, dummyChosen, maxGain);


    }

    public static boolean oneInteration(BookKeepingPerLevel book){
        
        double cg = 0;
        int maxCgIndex = -1;
        double maxCg = 0;

        boolean singletonPartition = book.taxas.getTaxonCountInPartition(0) == 1  || book.taxas.getTaxonCountInPartition(1) == 1;

        boolean[] rtLocked = new boolean[book.taxas.realTaxonCount];
        boolean[] dtLocked = new boolean[book.taxas.dummyTaxonCount];
        double[][] rtGains;
        double[] dtGains;

        ArrayList<Swap> swaps = new ArrayList<Swap>();

        // ArrayList<Double> cgs = new ArrayList<Double>();

        while(true){
            rtGains = new double[book.taxas.realTaxonCount][2];
            dtGains = new double[book.taxas.dummyTaxonCount];
            
            book.calculateScoreAndGains(rtGains, dtGains);

            var x = swapMax(book, rtGains, dtGains, rtLocked,dtLocked);
            
            if(x != null){
                swaps.add(x);
                
                double gain = x.gain;

                // if(gain < 0) break;

                cg += gain;

                // cgs.add(cg);

                if(singletonPartition){
                    if(maxCgIndex == -1 ){ // && book.taxas.getTaxonCountInPartition(0) > 1 && book.taxas.getTaxonCountInPartition(1) > 1 ){
                        maxCg = cg;
                        maxCgIndex = swaps.size() - 1;
                    }
                }
                
                if(cg > maxCg && Math.abs(maxCg - cg) > EPS ){ // && book.taxas.getTaxonCountInPartition(0) > 1 && book.taxas.getTaxonCountInPartition(1) > 1 ){
                    maxCg = cg;
                    maxCgIndex = swaps.size() - 1;
                }

                // if(cg < 0){
                //     break;
                // }
            }
            else{
                break;
            }
            
        }

        // checkCG(cgs);
        // if(Math.abs(cg) > EPS && swaps.size() == (book.taxas.realTaxonCount + book.taxas.dummyTaxonCount)){
        //     System.out.println(book.taxas.realTaxonCount + book.taxas.dummyTaxonCount);
        //     System.out.println(swaps.size());
        //     System.out.println(cg);
        //     System.out.println("should be zero");
        //     System.exit(-1);
        // }
        // System.out.println("CG Index : " + maxCgIndex + " cg : " + maxCg);





        if(maxCgIndex == -1){
            if(swaps.size() != (book.taxas.realTaxonCount + book.taxas.dummyTaxonCount)){
                for(int i = swaps.size() - 1; i >= 0; --i){
                    var x = swaps.get(i);
                    // book.swapTaxon(x.index, x.isDummy);
                    if(x.isDummy){
                        book.taxas.swapPartitionDummyTaxon(x.index);
                    }
                    else{
                        book.taxas.swapPartitionRealTaxon(x.index);
                    }
                }
            }
            return false;
        }
        for(int i = swaps.size() - 1; i > maxCgIndex; --i){
            var x = swaps.get(i);
            book.swapTaxon(x.index, x.isDummy);
        }

        return true;

    }

    // public static void checkCG(ArrayList<Double> cg){
    //     if(cg.size() < 3) return;
    //     // for(var g : cg){
    //     //     System.out.printf("%.5f ", g);
    //     // }
    //     // System.out.println();

    //     var x = checkNegativeToPositive(cg);
    //     if(!x.equalsIgnoreCase("Neither")){
    //         for(var g : cg){
    //             System.out.printf("%.5f ", g);
    //         }
    //         System.out.println();
    //         System.out.println("Error: CG negative to positive");
    //         // System.exit(-1);
    //     }
        
    //     // boolean inc = cg.get(0) < cg.get(1);

    //     // if(inc){
    //     //     var x = checkIncreaseThenDecrease(cg);
    //     //     if(x.equalsIgnoreCase("Neither")){
    //     //         System.out.println("Error: CG not increasing then decreasing");
    //     //         // System.exit(-1);
    //     //     }
    //     // }
    //     // else{
    //     //     var x = checkDecreaseThenIncrease(cg);
    //     //     if(x.equalsIgnoreCase("Neither")){
    //     //         System.out.println("Error: CG not decreasing then increasing");
    //     //         // System.exit(-1);
    //     //     }
    //     // }
        
    //     // for(int i = 1; i < cg.size(); ++i){
    //     //     if(inc && cg.get(i) < cg.get(i - 1)){
    //     //         System.out.println("Error: CG decreasing");
    //     //         // System.exit(-1);
    //     //     }
    //     //     if(!inc && cg.get(i) > cg.get(i - 1)){
    //     //         System.out.println("Error: CG increasing");
    //     //         // System.exit(-1);
    //     //     }
    //     // }
    // }

    // public static String checkNegativeToPositive(ArrayList<Double> arr) {
    //     int size = arr.size();
    //     if (size <= 0) {
    //         return "Neither"; // Empty arrays can't exhibit a clear pattern
    //     }

    //     boolean negativeFound = false;

    //     for (double num : arr) {
    //         if (num < 0) {
    //             negativeFound = true;
    //         } else if (negativeFound && num > EPS) {
    //             return "Negative to Positive";
    //         }
    //     }

    //     return "Neither"; // No change from negative to positive found
    // }

    // public static String checkIncreaseThenDecrease(ArrayList<Double> arr) {
    //     int size = arr.size();
    //     if (size <= 2) {
    //         return "Neither"; // Arrays with 2 or fewer elements can't exhibit a clear pattern
    //     }

    //     boolean increasing = true;
    //     int increasingIndex = -1;

    //     // Check if increasing
    //     for (int i = 1; i < size; i++) {
    //         if (arr.get(i) <= arr.get(i - 1)) {
    //             increasing = false;
    //             increasingIndex = i - 1;
    //             break;
    //         }
    //     }

    //     // Check if decreasing after increasing
    //     if (increasing && increasingIndex < size - 1) {
    //         for (int i = increasingIndex + 1; i < size; i++) {
    //             if (arr.get(i) >= arr.get(i - 1)) {
    //                 return "Neither"; // Continuation of increasing or not strictly decreasing
    //             }
    //         }
    //         return "Increase then Decrease";
    //     }

    //     return "Neither"; // No increasing followed by decreasing pattern found
    // }

    // public static String checkDecreaseThenIncrease(ArrayList<Double> arr) {
    //     int size = arr.size();
    //     if (size <= 2) {
    //         return "Neither"; // Arrays with 2 or fewer elements can't exhibit a clear pattern
    //     }

    //     boolean decreasing = true;
    //     int decreasingIndex = -1;

    //     // Check if decreasing
    //     for (int i = 1; i < size; i++) {
    //         if (arr.get(i) >= arr.get(i - 1)) {
    //             decreasing = false;
    //             decreasingIndex = i - 1;
    //             break;
    //         }
    //     }

    //     // Check if increasing after decreasing
    //     if (decreasing && decreasingIndex < size - 1) {
    //         for (int i = decreasingIndex + 1; i < size; i++) {
    //             if (arr.get(i) <= arr.get(i - 1)) {
    //                 return "Neither"; // Continuation of decreasing or not strictly increasing
    //             }
    //         }
    //         return "Decrease then Increase";
    //     }

    //     return "Neither"; // No decreasing followed by increasing pattern found
    // }


}
