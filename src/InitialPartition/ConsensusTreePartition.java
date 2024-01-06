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
    BookKeepingPerLevel book;
    double score;

    public double[][] dist;
    Map<String, RealTaxon> taxaMap;
    

    public ConsensusTreePartition(String filePath, Map<String, RealTaxon> taxaMap, GeneTrees trees) throws FileNotFoundException{
        Scanner scanner = new Scanner(new File(filePath));
        String line = scanner.nextLine();
        this.consTree = new Tree(line, taxaMap);
        this.taxonCount = taxaMap.size();
        scanner.close();

        randPartition = new RandPartition();
        this.trees = trees;
        dist = new double[this.taxonCount][this.taxonCount];
        this.taxaMap = taxaMap;

        calculateDistanceMatrix();
        // for(var x : this.taxaMap.entrySet()){
        //     System.out.println(x.getKey() + " " + x.getValue().id);
        // }
        // printDistanceMatrix();
        
    }

    public void printDistanceMatrix() {
        
        for (int i = 0; i < taxonCount; i++) {
            for (int j = 0; j < taxonCount; j++) {
                System.out.print(dist[i][j] + " ");
            }
            System.out.println();
        }
    }

    

    private void distToLeaves(TreeNode node, int from, double dist){
        if(node.isLeaf()){
            this.dist[from][node.taxon.id] = dist;
        }
        else{
            for(var child : node.childs){
                distToLeaves(child, from, dist + 1);
            }
        }
    }

    
    private void distFromTaxon(RealTaxon rt){
        var leafNode = this.consTree.leaves[rt.id];
        var parent = leafNode.parent;
        int from = rt.id;
        double dist = 1;
        while(parent != null){
            for(var child : parent.childs){
                if(child != leafNode){
                    distToLeaves(child, from, dist + 1);
                }
            }
            leafNode = parent;
            parent = parent.parent;
            dist++;
        }
    }

    private void calculateDistanceMatrix(){
        for(var x : this.taxaMap.entrySet()){
            distFromTaxon(x.getValue());    
        }
    }



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

        if(this.book == null){
            TaxaPerLevelWithPartition taxas = new TaxaPerLevelWithPartition(rts, dts, rtsP, dtsp, this.taxonCount);
            this.book = new BookKeepingPerLevel(trees, taxas, allowSingleton);
        }
        else{
            int rtCount = 0;
            int dtCount = 0;
            boolean changed = false;
            for(i = 0; i < rts.length; ++i){
                if(rtsP[i] != this.book.taxas.inWhichPartitionRealTaxonByIndex(i)){
                    changed = true;
                    rtCount++;
                    // book.swapTaxon(i, false);
                }
            }

            for(i = 0; i < dts.length; ++i){
                if(dtsp[i] != this.book.taxas.inWhichPartitionDummyTaxonByIndex(i)){
                    changed = true;
                    dtCount++;
                    // book.swapTaxon(i, true);
                }
            }
            if(!changed){
                // System.out.println("Not changed");
                return this.score;
            }
            else{
                if(rtCount + 9 * dtCount + 5 > dts.length){
                    TaxaPerLevelWithPartition taxas = new TaxaPerLevelWithPartition(rts, dts, rtsP, dtsp, this.taxonCount);
                    this.book = new BookKeepingPerLevel(trees, taxas, allowSingleton);

                }
                else{
                    for(i = 0; i < rts.length; ++i){
                        if(rtsP[i] != this.book.taxas.inWhichPartitionRealTaxonByIndex(i)){
                            book.swapTaxon(i, false);
                        }
                    }

                    for(i = 0; i < dts.length; ++i){
                        if(dtsp[i] != this.book.taxas.inWhichPartitionDummyTaxonByIndex(i)){
                            book.swapTaxon(i, true);
                        }
                    }
                }
            }
            
        }
        this.score = this.book.calculateScore();

        return this.score;
    }

    @Override
    public MakePartitionReturnType makePartition(RealTaxon[] rts, DummyTaxon[] dts, boolean allowSingleton) {
        
        this.book = null;

        double[] weight = new double[consTree.leavesCount];
        int[] inWhichDummyTaxa = new int[consTree.leavesCount];

        TreeNode minNode = null;
        double minDiff = 0;
        double maxScore = 0;

        boolean[] isRealTaxon = new boolean[consTree.leavesCount];

        // boolean allowSingleton = Config.ALLOW_SINGLETON;

        for(var x : rts){
            weight[x.id] = 1;
            isRealTaxon[x.id] = true;
        }

        int i = 0;

        for(var x : dts){
            if(Config.CONSENSUS_WEIGHT_TYPE == Config.ConsensusWeightType.NESTED){
                x.calcDivCoeffs(Config.ScoreNormalizationType.NESTED_NORMALIZATION, weight, 1);
                for(var y : x.flattenedRealTaxa){
                    weight[y.id] = 1 / weight[y.id];
                }
            }
            else{
                double sz = x.flattenedTaxonCount;
                for(var y : x.flattenedRealTaxa){
                    weight[y.id] += 1. / sz;
                }
            }
            
            for(var y : x.flattenedRealTaxa){
                inWhichDummyTaxa[y.id] = i;
            }
            ++i;
        }

        // if(Config.CONSENSUS_WEIGHT_TYPE == Config.ConsensusWeightType.NESTED){
        //     // for(i = 0; i < dts.length; ++i){
        //     //     if(weight[i] > 1) weight[i] = 1. / weight[i];
        //     // }
        // }        

        for(var node : this.consTree.topSortedNodes){
            node.info = new Info();
            node.info.branches = new Branch[1];
            node.info.branches[0] = new Branch(dts.length);

            var branch = node.info.branches[0];

            if(node.isLeaf()){
                double w = weight[node.taxon.id];
                if(isRealTaxon[node.taxon.id]){
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

                    if((partASize > 1 && partBSize > 1) || (allowSingleton && partASize >= 1 && partBSize >= 1) ){
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
