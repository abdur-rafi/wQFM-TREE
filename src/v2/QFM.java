package src.v2;

import java.util.ArrayList;

import src.v2.InitialPartition.IMakePartition;
import src.v2.PreProcessing.GeneTrees;
import src.v2.DSPerLevel.BookKeepingPerLevel;
import src.v2.DSPerLevel.TaxaPerLevelWithPartition;
import src.v2.Taxon.DummyTaxon;
import src.v2.Taxon.RealTaxon;
import src.v2.Tree.Tree;
import src.v2.Tree.TreeNode;

public class QFM {
    
    public RealTaxon[] realTaxa;
    public IMakePartition initPartition;
    public GeneTrees geneTrees;

    double EPS = 1e-5;

    public QFM(GeneTrees trees, RealTaxon[] realTaxa, IMakePartition initPartition){
        this.realTaxa = realTaxa;
        this.initPartition = initPartition;
        this.geneTrees = trees;
    }

    public Tree runWQFM(){
        var y = initPartition.makePartition(realTaxa, new DummyTaxon[0]);
        var x = new TaxaPerLevelWithPartition(realTaxa, new DummyTaxon[0], y.realTaxonPartition, y.dummyTaxonPartition, realTaxa.length);
        BookKeepingPerLevel initialBook = new BookKeepingPerLevel(geneTrees, x);

        return recurse(initialBook);

    }

    private Tree recurse(
        BookKeepingPerLevel book
    ){
        int itrCount = 0;
        
        while(oneInteration(book) ){
            itrCount++;
            System.out.println(itrCount);
            if(itrCount > 20){

            }
        }

        Tree[] trees = new Tree[2];

        var x = book.divide(initPartition);
        int i = 0;
        int[] dummyIds = new int[2];
        
        for(var taxaWPart : x){
            var childBooks = new BookKeepingPerLevel(geneTrees, taxaWPart);
            if(childBooks.taxas.smallestUnit){
                trees[i] = childBooks.taxas.createStar();
            }
            else{
                trees[i] = recurse(childBooks);
            }
            dummyIds[i++] = childBooks.taxas.dummyTaxa[childBooks.taxas.dummyTaxonCount - 1].id;
        }

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

    class Swap{
        public int index;
        public boolean isDummy;
        public double gain;

        public Swap(int i, boolean id, double g){
            this.index = i;
            this.isDummy = id;
            this.gain = g;
        }
    }
    

    public Swap swapMax(BookKeepingPerLevel book, double[][] rtGains, double[] dtGains, boolean[] rtLocked, boolean[] dtLocked){

        int maxGainIndex = -1;
        double maxGain = 0;

        for(int i = 0; i < book.taxas.realTaxonCount; ++i){
            if(rtLocked[i]) continue;
            int partition = book.taxas.inWhichPartitionRealTaxonByIndex(i);
            if(book.taxas.getTaxonCountInPartition(partition) > 2){
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
            if(book.taxas.getTaxonCountInPartition(partition) > 2){
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

    public boolean oneInteration(BookKeepingPerLevel book){
        
        double cg = 0;
        int maxCgIndex = -1;
        double maxCg = 0;

        boolean[] rtLocked = new boolean[book.taxas.realTaxonCount];
        boolean[] dtLocked = new boolean[book.taxas.dummyTaxonCount];
        double[][] rtGains;
        double[] dtGains;

        ArrayList<Swap> swaps = new ArrayList<Swap>();

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
                
                if(cg > maxCg && Math.abs(maxCg - cg) > EPS ){
                    maxCg = cg;
                    maxCgIndex = swaps.size() - 1;
                }
            }
            else{
                break;
            }
            
        }

        // System.out.println("CG Index : " + maxCgIndex + " cg : " + maxCg);

        if(maxCgIndex == -1){
            for(int i = swaps.size() - 1; i >= 0; --i){
                var x = swaps.get(i);
                book.swapTaxon(x.index, x.isDummy);
            }
            return false;
        }
        for(int i = swaps.size() - 1; i > maxCgIndex; --i){
            var x = swaps.get(i);
            book.swapTaxon(x.index, x.isDummy);
        }

        return true;

    }
}
