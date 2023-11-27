package src.DSPerLevel;

import java.util.ArrayList;

import src.Config;
import src.Utility;
import src.InitialPartition.IMakePartition;
import src.PreProcessing.GeneTrees;
import src.Taxon.DummyTaxon;
import src.Taxon.RealTaxon;

public class BookKeepingPerLevel {

    public final GeneTrees geneTrees;


    public TaxaPerLevelWithPartition taxas;
    public boolean allowSingleton;

    // public double[][] realTaxaGains;
    // public double[] dummyTaxaGains;


    // public ArrayList<TreeNode> nodesForScore;
    // public ArrayList<TreeNode> nodesForGains;

    public static ArrayList<BookKeepingPerTree> bookKeepingPerTrees;

    public int getTotalTaxon(int p){
        if(Config.SCORE_NORMALIZATION_TYPE == Config.ScoreNormalizationType.NO_NORMALIZATION){
            return taxas.getTaxonCountFlattenedInPartition(p);
        }
        return taxas.getTaxonCountInPartition(p);
    }

    public int getDummyTaxonIndiWeight(int index){
        if(Config.SCORE_NORMALIZATION_TYPE == Config.ScoreNormalizationType.NO_NORMALIZATION){
            return taxas.getFlattenedCount(index);
        }
        return 1;
    }

    public BookKeepingPerLevel(GeneTrees geneTrees, TaxaPerLevelWithPartition taxaPerLevelWithPartition, boolean allowSingleton){

        this.taxas = taxaPerLevelWithPartition;
        this.geneTrees = geneTrees;


        this.allowSingleton = allowSingleton;

        if(taxaPerLevelWithPartition.smallestUnit)
            return;

        // this.nodesForScore = new ArrayList<>();
        // this.nodesForGains = new ArrayList<>();
        BookKeepingPerLevel.bookKeepingPerTrees = new ArrayList<>();

        initialBookKeeping();
    }


    private void initialBookKeeping(){
        for(var gt : geneTrees.geneTrees){
            BookKeepingPerLevel.bookKeepingPerTrees.add(new BookKeepingPerTree(gt, taxas));
        }
    }


    private double gainCalcFromSatWithNorm(double[][] realTaxaGains, double[] dummyTaxaGains, double totalScore){

        double[] dtTotals = new double[this.taxas.dummyTaxonCount];
        double currTotals = 0;
        
        for(var x : BookKeepingPerLevel.bookKeepingPerTrees){
            // totals[0] += x.totalQuartetsAfterSwap(1);
            // totals[1] += x.totalQuartetsAfterSwap(0);
            for(int i = 0;i < this.taxas.dummyTaxonCount; ++i){
                dtTotals[i] += x.totalQuartetsAfterDummySwap(i, 1 - taxas.inWhichPartitionDummyTaxonByIndex(i));
            }

            currTotals += x.totalQuartets();
        }


        for(int i = 0; i < taxas.realTaxonCount; ++i){
            double total = 0;
            int partition = taxas.inWhichPartitionRealTaxonByIndex(i);

            for(var bookTree : BookKeepingPerLevel.bookKeepingPerTrees){
                total += bookTree.totalQuartetsAfterSwap(taxas.realTaxa[i].id, 1 - partition);
            }
            
            // System.out.println("total : " + total);

            // Utility.addArrayToFirst(realTaxaGains[i], this.gainsToAll);
            realTaxaGains[i][partition] += totalScore;
            realTaxaGains[i][partition] = Config.SCORE_EQN.scoreFromSatAndTotal(total, realTaxaGains[i][partition]);
        }

        for(int i = 0; i < taxas.dummyTaxonCount; ++i){
            
            dummyTaxaGains[i] = Config.SCORE_EQN.scoreFromSatAndTotal(
                dtTotals[i],
                dummyTaxaGains[i] + totalScore
            );

        }
        totalScore = Config.SCORE_EQN.scoreFromSatAndTotal(
            currTotals,
            totalScore
        );
        
        for (int i = 0; i < realTaxaGains.length; i++) {
            realTaxaGains[i][taxas.inWhichPartitionRealTaxonByIndex(i)] -= totalScore;
        }
        for (int i = 0; i < dummyTaxaGains.length; i++) {
            dummyTaxaGains[i] -= totalScore;
        }

        return totalScore;
    }

    
    public double calculateScore(){
        double totalScore = 0;
        double currTotals = 0;

        for(var bookTree : BookKeepingPerLevel.bookKeepingPerTrees){
            for(var node : bookTree.nodesForScore){
                totalScore += node.info.scoreCalculator.score() * node.frequency;
            }
            currTotals += bookTree.totalQuartets();
        }

        return Config.SCORE_EQN.scoreFromSatAndTotal(currTotals, totalScore);
    
    }
    

    public double calculateScoreAndGains(double[][] realTaxaGains, double[] dummyTaxaGains){
        double totalScore = 0;
        
        for(var bookTree : BookKeepingPerLevel.bookKeepingPerTrees){
            double[] gainsToAll = new double[2];

            for(var node : bookTree.nodesForScore){
    
                double score = node.info.scoreCalculator.score();
                var branchGains = node.info.scoreCalculator.gainRealTaxa(score, node.frequency);
                node.info.scoreCalculator.gainDummyTaxa(score, node.frequency, dummyTaxaGains);
                
                score *= node.frequency;
                // System.out.println(score);
                totalScore += score;
    
    
                var childs = node.childs;
                for(int i = 0; i < childs.size(); ++i){
                    Utility.subArrayToFirst(branchGains[i], branchGains[childs.size()]);
                    childs.get(i).info.gainsForSubTree = branchGains[i];
                }
                Utility.addArrayToFirst(gainsToAll, branchGains[childs.size()]);
            }

            for(int i = bookTree.nodesForGains.size() - 1; i > -1; --i){
                var node = bookTree.nodesForGains.get(i);
                for (int j = 0; j < node.childs.size(); j++) {
                    var child = node.childs.get(j);
                    Utility.addArrayToFirst(child.info.gainsForSubTree, node.info.gainsForSubTree);
                }
                
                node.info.gainsForSubTree[0] = 0;
                node.info.gainsForSubTree[1] = 0;
    
            }

            for(var node : bookTree.geneTree.leaves){
                if(node == null) continue;
                if(taxas.isInRealTaxa(node.taxon.id)){
                    // if(!bookTree.geneTree.isTaxonPresent(node.taxon.id)){
                    //     System.out.println("Taxon not present");
                    //     System.exit(-1);
                    // }
                    Utility.addArrayToFirst(
                        realTaxaGains[taxas.getRealTaxonIndex(node.taxon.id)], 
                        node.info.gainsForSubTree
                    );
                }
                node.info.gainsForSubTree[0] = 0;
                node.info.gainsForSubTree[1] = 0;

            }
            
            for(int i = 0; i < this.taxas.realTaxonCount; ++i){
                if(bookTree.geneTree.isTaxonPresent(this.taxas.realTaxa[i].id)){
                    Utility.addArrayToFirst(
                        realTaxaGains[i], 
                        gainsToAll
                    );
                }
            }


        }

        // System.out.println(totalScore);


        // for(var x : this.geneTrees.geneTrees){
        //     for(var node : x.leaves){
        //         if(node == null) continue;
        //         if(taxas.isInRealTaxa(node.taxon.id)){
        //             Utility.addArrayToFirst(
        //                 realTaxaGains[taxas.getRealTaxonIndex(node.taxon.id)], 
        //                 node.info.gainsForSubTree
        //             );
        //         }
        //         node.info.gainsForSubTree[0] = 0;
        //         node.info.gainsForSubTree[1] = 0;

        //     }
        // }
        // System.out.println("My : " + totalScore);
        totalScore = gainCalcFromSatWithNorm(realTaxaGains, dummyTaxaGains, totalScore);

        // if(Config.SCORE_NORMALIZATION_TYPE == Config.ScoreNormalizationType.NO_NORMALIZATION){
        //     totalScore = gainCalcFromSatNoNorm(realTaxaGains, dummyTaxaGains, totalScore);
        // }
        // else{
        //     totalScore = gainCalcFromSatWithNorm(realTaxaGains, dummyTaxaGains, totalScore);
        // }


        // System.out.println("Score : " + totalScore);

        // for (int i = 0; i < realTaxaGains.length; i++) {
        //     var rt = taxas.realTaxa[i];
        //     System.out.println(rt.label + ": " + (realTaxaGains[i][taxas.inWhichPartitionRealTaxonByIndex(i)] + totalScore));
        // }
        
        // for (int i = 0; i < dummyTaxaGains.length; i++) {
        //     var dt = taxas.dummyTaxa[i];
        //     for(var  x : dt.flattenedRealTaxa){
        //         System.out.printf(x.label + " ,");
        //     }
        //     System.out.println(": " + (dummyTaxaGains[i] + totalScore));
        // }

        return totalScore;

    }

    
        

    private void swapRealTaxon(int index){

        int partition = taxas.inWhichPartitionRealTaxonByIndex(index);
        taxas.swapPartitionRealTaxon(index);

        for(var x : BookKeepingPerLevel.bookKeepingPerTrees){
            x.swapRealTaxon(taxas.realTaxa[index], partition);
        }
        
    }

    public void swapTaxon(int index, boolean isDummy){
        if(isDummy) this.swapDummyTaxon(index);
        else this.swapRealTaxon(index);
    }

    private void swapDummyTaxon(int index){
        int partition = taxas.inWhichPartitionDummyTaxonByIndex(index);
        // if(taxas.getTaxonCountInPartition(partition) < 3){
        //     System.out.println("Should not be swapped");
        //     System.exit(-1);
        // }
        taxas.swapPartitionDummyTaxon(index);

        for(var x : BookKeepingPerLevel.bookKeepingPerTrees){
            x.swapDummyTaxon(index, partition);
        }

    }

    public TaxaPerLevelWithPartition[] divide(IMakePartition makePartition, boolean allowSingleton){
        RealTaxon[][] rts = new RealTaxon[2][];
        DummyTaxon[][] dts = new DummyTaxon[2][];

        // int[][] rtsPart = new int[2][];
        // int[][] dtsPart = new int[2][];


        for(int i = 0; i < 2; ++i){
            rts[i] = new RealTaxon[taxas.getRealTaxonCountInPartition(i)];
            dts[i] = new DummyTaxon[taxas.getDummyTaxonCountInPartition(i)];
            // var x = makePartition.makePartition(rts[i], dts[i]);
            // rtsPart[i] = x.realTaxonPartition;
            // dtsPart[i] = x.dummyTaxonPartition;
        }

        int[] index = new int[2];

        for(var x : taxas.realTaxa){
            int part = taxas.inWhichPartition(x.id);
            rts[part][index[part]++] = x;
        }
        index[0] = 0;
        index[1] = 0;
        int i = 0;
        for(var x : taxas.dummyTaxa){
            int part = taxas.inWhichPartitionDummyTaxonByIndex(i++);
            dts[part][index[part]++] = x;
        }


        // ith dummy taxon for ith partition
        DummyTaxon[] newDt = new DummyTaxon[2];
        
        

        // BookKeepingPerLevel[] bookKeepingPerLevels = new BookKeepingPerLevel[2];
        TaxaPerLevelWithPartition[] taxaPerLevelWithPartitions = new TaxaPerLevelWithPartition[2];
        for( i = 0; i < 2; ++i){
            newDt[i] = new DummyTaxon(rts[1 - i], dts[1 - i]);
            
            DummyTaxon[] dtsWithNewDt = new DummyTaxon[dts[i].length + 1];
            for(int j = 0; j < dts[i].length; ++j){
                dtsWithNewDt[j] = dts[i][j];
            }
            dtsWithNewDt[dtsWithNewDt.length - 1] = newDt[i];

            if(rts[i].length + dtsWithNewDt.length > 3){

                var y = makePartition.makePartition(rts[i], dtsWithNewDt, true);
                taxaPerLevelWithPartitions[i] = new TaxaPerLevelWithPartition(
                    rts[i], dtsWithNewDt, 
                    y.realTaxonPartition, 
                    y.dummyTaxonPartition, 
                    this.geneTrees.realTaxaCount
                );
            }
            else{
                taxaPerLevelWithPartitions[i] = new TaxaPerLevelWithPartition(
                    rts[i], dtsWithNewDt, 
                    null, null,
                    this.geneTrees.realTaxaCount
                );
            }
            
            // bookKeepingPerLevels[i] = new BookKeepingPerLevel(this.geneTrees,x);
        }

        return taxaPerLevelWithPartitions;
    }


}
