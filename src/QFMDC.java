package src;

import java.util.ArrayList;

import src.DSPerLevel.BookKeepingPerLevelDC;
import src.DSPerLevel.TaxaPerLevelWithPartition;
import src.InitialPartition.IMakePartition;
import src.PreProcessing.DataContainer;
import src.Taxon.DummyTaxon;
import src.Taxon.RealTaxon;
import src.Tree.Tree;
import src.Tree.TreeNode;

public class QFMDC {
    
    public RealTaxon[] realTaxa;
    public IMakePartition initPartition;
    public DataContainer dc;
    private int level;

    static double EPS = 1e-5;

    public QFMDC(DataContainer dc, RealTaxon[] realTaxa, IMakePartition initPartition){
        this.dc = dc;
        this.realTaxa = realTaxa;
        this.initPartition = initPartition;
    }

    public Tree runWQFM(){
        this.level = 0;

        var y = initPartition.makePartition(realTaxa, new DummyTaxon[0], true);
        var x = new TaxaPerLevelWithPartition(realTaxa, new DummyTaxon[0], y.realTaxonPartition, y.dummyTaxonPartition, realTaxa.length);
        BookKeepingPerLevelDC initialBook = new BookKeepingPerLevelDC(this.dc, x);

        return recurse(initialBook);

    }

    private Tree recurse(
        BookKeepingPerLevelDC book
    ){

        this.level++;
        int itrCount = 0;

        System.out.println("Level: " + this.level);
        
        while(oneInteration(book) ){
            itrCount++;
            if(itrCount > Config.MAX_ITERATION){
                System.out.println("Max iteration reached");
                break;
            }
            
        }

        System.out.println( "#iterations: " + itrCount);

        Tree[] trees = new Tree[2];

        var x = book.divide(initPartition);
        int i = 0;
        int[] dummyIds = new int[2];
        
        for(var taxaWPart : x){
            var childBooks = new BookKeepingPerLevelDC(this.dc, taxaWPart);
            if(childBooks.taxaPerLevel.smallestUnit){
                trees[i] = childBooks.taxaPerLevel.createStar();
            }
            else{
                trees[i] = recurse(childBooks);
            }
            dummyIds[i++] = childBooks.taxaPerLevel.dummyTaxa[childBooks.taxaPerLevel.dummyTaxonCount - 1].id;
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
    

    public static Swap swapMax(BookKeepingPerLevelDC book, double[][] rtGains, double[] dtGains, boolean[] rtLocked, boolean[] dtLocked){

        int maxGainIndex = -1;
        double maxGain = 0;

        for(int i = 0; i < book.taxaPerLevel.realTaxonCount; ++i){
            if(rtLocked[i]) continue;
            int partition = book.taxaPerLevel.inWhichPartitionRealTaxonByIndex(i);
            if((book.taxaPerLevel.getTaxonCountInPartition(partition) > 2) ){
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

        for(int i = 0; i < book.taxaPerLevel.dummyTaxonCount; ++i){
            if(dtLocked[i]) continue;
            int partition = book.taxaPerLevel.inWhichPartitionDummyTaxonByIndex(i);
            if(book.taxaPerLevel.getTaxonCountInPartition(partition) > 2 ){
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

    public static boolean oneInteration(BookKeepingPerLevelDC book){
        
        double cg = 0;
        int maxCgIndex = -1;
        double maxCg = 0;


        boolean singletonPartition = book.taxaPerLevel.getTaxonCountInPartition(0) == 1  || book.taxaPerLevel.getTaxonCountInPartition(1) == 1;

        boolean[] rtLocked = new boolean[book.taxaPerLevel.realTaxonCount];
        boolean[] dtLocked = new boolean[book.taxaPerLevel.dummyTaxonCount];
        double[][] rtGains;
        double[] dtGains;

        ArrayList<Swap> swaps = new ArrayList<Swap>();

        // ArrayList<Double> cgs = new ArrayList<Double>();

        while(true){
            rtGains = new double[book.taxaPerLevel.realTaxonCount][2];
            dtGains = new double[book.taxaPerLevel.dummyTaxonCount];
            
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

        // System.out.println("Cg : " + cg);
        

        if(maxCgIndex == -1){
            if(swaps.size() != (book.taxaPerLevel.realTaxonCount + book.taxaPerLevel.dummyTaxonCount)){
                for(int i = swaps.size() - 1; i >= 0; --i){
                    var x = swaps.get(i);
                    // book.swapTaxon(x.index, x.isDummy);
                    if(x.isDummy){
                        book.taxaPerLevel.swapPartitionDummyTaxon(x.index);
                    }
                    else{
                        book.taxaPerLevel.swapPartitionRealTaxon(x.index);
                    }
                }
            }
            return false;
        }
        // ArrayList<Integer> rtIds = new ArrayList<Integer>();
        // ArrayList<Integer> dtIds = new ArrayList<Integer>();

        // for(int i = swaps.size() - 1; i > maxCgIndex; --i){
        //     Swap s = swaps.get(i);
        //     if(s.isDummy){
        //         dtIds.add(s.index);
        //     }
        //     else{
        //         rtIds.add(s.index);
        //     }
        // }

        // if(rtIds.size() > 0){
        //     // if(rtIds.size() > 1){
        //     //     book.batchTrasferRealTaxon(rtIds);
        //     // }
        //     // else{
        //     //     book.swapRealTaxon(rtIds.get(0));
        //     // }

        //     for(Integer i : rtIds){
        //         ArrayList<Integer> x = new ArrayList<Integer>();
        //         x.add(i);
        //         // book.batchTrasferRealTaxon(x);
        //         book.swapRealTaxon(i);
        //     }
        // }
        // for(Integer i : dtIds){
        //     book.swapDummyTaxon(i);
        // }

        for(int i = swaps.size() - 1; i > maxCgIndex; --i){
            var x = swaps.get(i);
            book.swapTaxon(x.index, x.isDummy);
        }

        return true;

    }

}
