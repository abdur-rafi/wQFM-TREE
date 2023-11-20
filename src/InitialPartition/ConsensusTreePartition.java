package src.InitialPartition;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import src.Config;
import src.DSPerLevel.BookKeepingPerLevel;
import src.DSPerLevel.TaxaPerLevelWithPartition;
import src.PreProcessing.GeneTrees;
import src.Taxon.DummyTaxon;
import src.Taxon.RealTaxon;
import src.Tree.Branch;
import src.Tree.Info;
import src.Tree.Tree;
import src.Tree.TreeNode;

public class ConsensusTreePartition implements IMakePartition {

    Tree consTree;

    RandPartition randPartition;

    int taxonCount;
    GeneTrees trees;

    public ConsensusTreePartition(String filePath, Map<String, RealTaxon> taxaMap, GeneTrees trees) throws FileNotFoundException{
        Scanner scanner = new Scanner(new File(filePath));
        String line = scanner.nextLine();
        this.consTree = new Tree(line, taxaMap);
        this.taxonCount = taxaMap.size();
        scanner.close();

        randPartition = new RandPartition();
        this.trees = trees;

    }

    // private void dfs(TreeNode currNode, RealTaxon[] rts, DummyTaxon[] dts){
    //     currNode.info = new Info();
    //     currNode.info.branches = new Branch[1];
    //     currNode.info.branches[0] = new Branch(dts.length);

    //     for(var x :)
    // }


    private void assignSubTreeToPartition(TreeNode node, int[] rtsp, Map<Integer, Integer> idToIndex){
        if(node.isLeaf()){
            if(idToIndex.containsKey(node.taxon.id)){
                rtsp[idToIndex.get(node.taxon.id)] = 1;
            }
        }
        else{
            for(var child : node.childs){
                assignSubTreeToPartition(child, rtsp,idToIndex);
            }
        }
    }

    // private void partitionUsingMaxScore(RealTaxon[] rts, DummyTaxon[] dts){

    //     double[] weight = new double[consTree.leavesCount];
    //     int[] inWhichDummyTaxa = new int[consTree.leavesCount];

    //     TreeNode minNode = null;
    //     double minDiff = 0;

    //     boolean allowSingleton = Config.ALLOW_SINGLETON;

    //     for(var x : rts){
    //         weight[x.id] = 1;
    //     }

    //     int i = 0;

    //     for(var x : dts){
    //         if(Config.CONSENSUS_WEIGHT_TYPE == Config.ConsensusWeightType.NESTED){
    //             x.calcDivCoeffs(Config.ScoreNormalizationType.NESTED_NORMALIZATION, weight, 1);
    //         }
    //         else{
    //             double sz = x.flattenedTaxonCount;
    //             for(var y : x.flattenedRealTaxa){
    //                 weight[y.id] = 1./sz;
    //                 inWhichDummyTaxa[y.id] = i;
    //             }
    //             ++i;
    //         }
    //         if(Config.ALLOW_SINGLETON){
    //             if(x.nestedLevel >= this.taxonCount * Config.SINGLETON_THRESHOLD){
    //                 allowSingleton = false;
    //             }
    //         }
    //     }

    //     if(Config.CONSENSUS_WEIGHT_TYPE == Config.ConsensusWeightType.NESTED){
    //         for(i = 0; i < dts.length; ++i){
    //             if(weight[i] > 1) weight[i] = 1. / weight[i];
    //         }
    //     }
    // }

    double scoreForPartitionByNode(TreeNode node, RealTaxon[] rts, DummyTaxon[] dts, boolean allowSingleton){

        int[] rtsP = new int[rts.length];
        int[] dtsp = new int[dts.length];

        Map<Integer, Integer> idToIndex = new HashMap<>();
        int i = 0;
        for(var x : rts){
            idToIndex.put(x.id, i++);
        }
    
        assignSubTreeToPartition(node, rtsP, idToIndex);

        for(i = 0; i < dts.length; ++i){
            if(node.info.branches[0].dummyTaxaWeightsIndividual[i] >= .5){
                dtsp[i] = 1;
            }
        }
        TaxaPerLevelWithPartition taxas = new TaxaPerLevelWithPartition(rts, dts, rtsP, dtsp, this.taxonCount);

        BookKeepingPerLevel book = new BookKeepingPerLevel(trees, taxas, allowSingleton);
        double[][] rtGains = new double[rts.length][2];
        double[] dtGains = new double[dts.length];

        return book.calculateScoreAndGains(rtGains, dtGains);

        // return new MakePartitionReturnType(rtsP, dtsp);
    }

    @Override
    public MakePartitionReturnType makePartition(RealTaxon[] rts, DummyTaxon[] dts, boolean allowSingleton) {
        
        double[] weight = new double[consTree.leavesCount];
        int[] inWhichDummyTaxa = new int[consTree.leavesCount];

        TreeNode minNode = null;
        double minDiff = 0;
        double maxScore = 0;

        // boolean allowSingleton = Config.ALLOW_SINGLETON;

        for(var x : rts){
            weight[x.id] = 1;
        }

        int i = 0;

        for(var x : dts){
            if(Config.CONSENSUS_WEIGHT_TYPE == Config.ConsensusWeightType.NESTED){
                x.calcDivCoeffs(Config.ScoreNormalizationType.NESTED_NORMALIZATION, weight, 1);
            }
            else{
                double sz = x.flattenedTaxonCount;
                for(var y : x.flattenedRealTaxa){
                    weight[y.id] = 1./sz;
                    inWhichDummyTaxa[y.id] = i;
                }
                ++i;
            }
            // if(Config.ALLOW_SINGLETON){
            //     if(x.nestedLevel >= this.taxonCount * Config.SINGLETON_THRESHOLD){
            //         allowSingleton = false;
            //     }
            // }
        }

        if(Config.CONSENSUS_WEIGHT_TYPE == Config.ConsensusWeightType.NESTED){
            for(i = 0; i < dts.length; ++i){
                if(weight[i] > 1) weight[i] = 1. / weight[i];
            }
        }        

        for(var node : this.consTree.topSortedNodes){
            node.info = new Info();
            node.info.branches = new Branch[1];
            node.info.branches[0] = new Branch(dts.length);

            var branch = node.info.branches[0];

            if(node.isLeaf()){
                double w = weight[node.taxon.id];
                if(w == 1){
                    branch.realTaxaCounts[0] = 1;
                    branch.totalTaxaCounts[0] = 1;
                }
                else if(w != 0) {
                    branch.totalTaxaCounts[0] = w;
                    branch.dummyTaxaWeightsIndividual[inWhichDummyTaxa[node.taxon.id]] = w;
                }
            }
            else{
                for(var child : node.childs){

                    int partASize = child.info.branches[0].realTaxaCounts[0];
                    int partBSize = rts.length - partASize;


                    for(int j = 0; j < dts.length; ++j){
                        branch.dummyTaxaWeightsIndividual[j] += child.info.branches[0].dummyTaxaWeightsIndividual[j];
                        if(child.info.branches[0].dummyTaxaWeightsIndividual[j] >= .5){
                            partASize++;
                        }
                        else{
                            partBSize++;
                        }
                    }
                    branch.totalTaxaCounts[0] += child.info.branches[0].totalTaxaCounts[0];
                    branch.realTaxaCounts[0] += child.info.branches[0].realTaxaCounts[0];

                    if(partASize > 1 && partBSize > 1 || (allowSingleton && partASize >= 1 && partBSize >= 1) ){
                        if(Config.USE_SCORING_IN_CONSENSUS){
                            double score = scoreForPartitionByNode(child, rts, dts, allowSingleton);
                            if( minNode == null || score > maxScore){
                                maxScore = score;
                                minNode = child;
                            }
                        }
                        else{
                            double diff = Math.abs(rts.length + dts.length - child.info.branches[0].totalTaxaCounts[0]);
                            if(minNode == null || diff < minDiff){
                                minNode = child;
                                minDiff = diff;
                            }
                        }
                        // double diff = Math.abs(rts.length + dts.length - child.info.branches[0].totalTaxaCounts[0]);
                        // if(minNode == null || diff < minDiff){
                        //     minNode = child;
                        //     minDiff = diff;
                        // }
                        // else if(diff < minDiff){
                        //     minNode = child;
                        //     minDiff = diff;
                        // }
                    }

                }
            }
        }
        if(minNode == null){
            System.out.println("Min Node null");
            return randPartition.makePartition(rts, dts, allowSingleton);
            // System.exit(-1);
        }
        // System.out.println("partition");
        int[] rtsP = new int[rts.length];
        int[] dtsp = new int[dts.length];

        Map<Integer, Integer> idToIndex = new HashMap<>();
        i = 0;
        for(var x : rts){
            idToIndex.put(x.id, i++);
        }
    
        assignSubTreeToPartition(minNode, rtsP, idToIndex);

        for(i = 0; i < dts.length; ++i){
            if(minNode.info.branches[0].dummyTaxaWeightsIndividual[i] >= .5){
                dtsp[i] = 1;
            }
        }

        return new MakePartitionReturnType(rtsP, dtsp);

    }
    
}
