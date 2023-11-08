package src.v2.DSPerLevel;

import java.util.ArrayList;

import src.Utility;
import src.v2.InitialPartition.IMakePartition;
import src.v2.PreProcessing.GeneTrees;
import src.v2.ScoreCalculator.ScoreCalculatorNode;
import src.v2.Taxon.DummyTaxon;
import src.v2.Taxon.RealTaxon;
import src.v2.Tree.Branch;
import src.v2.Tree.Info;
import src.v2.Tree.TreeNode;

public class BookKeepingPerLevel {

    public final GeneTrees geneTrees;


    public TaxaPerLevelWithPartition taxas;

    // public double[][] realTaxaGains;
    // public double[] dummyTaxaGains;
    public double score;

    double[] gainsToAll;

    public ArrayList<TreeNode> nodesForScore;
    public ArrayList<TreeNode> nodesForGains;


    public BookKeepingPerLevel(GeneTrees geneTrees, TaxaPerLevelWithPartition taxaPerLevelWithPartition){

        this.taxas = taxaPerLevelWithPartition;
        this.geneTrees = geneTrees;

        // this.realTaxaGains = new double[taxas.realTaxonCount][2];
        // this.dummyTaxaGains = new double[taxas.dummyTaxonCount];

        if(taxaPerLevelWithPartition.smallestUnit)
            return;

        this.gainsToAll = new double[2];
        this.nodesForScore = new ArrayList<>();
        this.nodesForGains = new ArrayList<>();
        initialBookKeeping();
    }

    private boolean bookKeepingAtANode(TreeNode node){
        Branch[] branches = new Branch[3];
        boolean skip = false;
        int[] nonZeroDummyCount = new int[2];
        int[] nonZeroDummyIndex = new int[2];

        for(int i = 0; i < 3; ++i){
            // branches[i] = new Branch(new int[2], new double[taxas.dummyTaxonCount], new double[2]);
            branches[i] = new Branch(taxas.dummyTaxonCount);

        }
        for(int i = 0; i < 2; ++i){
            var child = node.childs.get(i);
            if(child.isLeaf()){
                int taxonId = child.taxon.id;
                if(taxas.isInRealTaxa(taxonId)){
                    branches[i].realTaxaCounts[taxas.inWhichPartition(taxonId)]++;
                    branches[i].totalTaxaCounts[taxas.inWhichPartition(taxonId)]++;
                }
                else if(taxas.isInDummyTaxa(taxonId)){
                    double weight = taxas.getWeight(taxonId);
                    // System.out.println("weight : " + weight);
                    int partition = taxas.inWhichPartition(taxonId);
                    // branches[i].dummyTaxaWeightSums[partition] += weight;
                    branches[i].dummyTaxaWeightsIndividual[taxas.inWhichDummyTaxa(taxonId)] += weight;
                    branches[i].totalTaxaCounts[partition] += weight;

                    nonZeroDummyCount[i]++;
                    nonZeroDummyIndex[i] = taxas.inWhichDummyTaxa(taxonId);
                }
            }
            else{
                for(int p = 0; p < 2; ++p){
                    branches[i].realTaxaCounts[p] += taxas.getRealTaxonCountInPartition(p) - child.info.branches[2].realTaxaCounts[p];
                    // branches[i].dummyTaxaWeightSums[p] += taxas.getDummyTaxonCountInPartition(p) - child.info.branches[2].dummyTaxaWeightSums[p];
                    // branches[i].totalTaxaCounts[p] += branches[i].realTaxaCounts[p] + branches[i].dummyTaxaWeightSums[p];
                    branches[i].totalTaxaCounts[p] = taxas.getTaxonCountInPartition(p) -  child.info.branches[2].totalTaxaCounts[p];

                }
                for(int j = 0; j < taxas.dummyTaxonCount; ++j){
                    branches[i].dummyTaxaWeightsIndividual[j] += 1. - child.info.branches[2].dummyTaxaWeightsIndividual[j];
                    if(branches[i].dummyTaxaWeightsIndividual[j] != 1){
                        nonZeroDummyCount[i]++;
                        nonZeroDummyIndex[i] = j;
                    }
                }
            }

        }
        for(int j = 0; j < taxas.dummyTaxonCount; ++j){
            branches[2].dummyTaxaWeightsIndividual[j] = 1. - branches[0].dummyTaxaWeightsIndividual[j] - branches[1].dummyTaxaWeightsIndividual[j];
        }
        for(int p = 0; p < 2; ++p){
            branches[2].realTaxaCounts[p] = taxas.getRealTaxonCountInPartition(p) - branches[0].realTaxaCounts[p] - branches[1].realTaxaCounts[p];
            // branches[2].dummyTaxaWeightSums[p] = taxas.getDummyTaxonCountInPartition(p) - branches[0].dummyTaxaWeightSums[p] - branches[1].dummyTaxaWeightSums[p];
            // branches[2].totalTaxaCounts[p] = branches[2].realTaxaCounts[p] + branches[2].dummyTaxaWeightSums[p];
            branches[2].totalTaxaCounts[p] = taxas.getTaxonCountInPartition(p) - branches[0].totalTaxaCounts[p] - branches[1].totalTaxaCounts[p];

        }
        if(node.frequency == 0){
            skip = true;
        }
        else{
            if(nonZeroDummyCount[0] == nonZeroDummyCount[1]){
                if(nonZeroDummyCount[0] == 1 && nonZeroDummyIndex[0] == nonZeroDummyIndex[1] ){
                    if(branches[0].realTaxaCounts[0] + branches[0].realTaxaCounts[1] + 
                    branches[1].realTaxaCounts[0] + branches[1].realTaxaCounts[1] == 0){
                        skip = true;
                    }
                }
            }
        }

        node.info = new Info(branches);
        // node.scoreCalculator = null;

        return skip;
    }

    private void initialBookKeeping(){
        for(var gt : geneTrees.geneTrees){
            for(var node : gt.topSortedNodes){

                if(node.isLeaf()){
                    node.info = new Info(null);
                    continue;
                }
                else if(node.isRoot()){
                    continue;
                }
                if(!bookKeepingAtANode(node)){
                    this.nodesForScore.add(node);
                    node.info.scoreCalculator = new ScoreCalculatorNode(node.info.branches,taxas.dummyTaxonPartition);
                }
                this.nodesForGains.add(node);
            }
        }
    }

    public double calculateScoreAndGains(double[][] realTaxaGains, double[] dummyTaxaGains){
        double totalScore = 0;
        this.gainsToAll = new double[2];
        
        // realTaxaGains = new double[taxas.realTaxonCount][2];
        // dummyTaxaGains = new double[taxas.dummyTaxonCount];


        for(var node : this.nodesForScore){

            // node.info.scoreCalculator = new ScoreCalculatorNode(node.info.branches, taxas.dummyTaxonPartition);
            double score = node.info.scoreCalculator.score();
            var branchGains = node.info.scoreCalculator.gainRealTaxa(score, node.frequency);
            node.info.scoreCalculator.gainDummyTaxa(score, node.frequency, dummyTaxaGains);
            
            score *= node.frequency;
            // System.out.println(score);
            totalScore += score;


            var childs = node.childs;
            for(int i = 0; i < 2; ++i){
                Utility.subArrayToFirst(branchGains[i], branchGains[2]);
                childs.get(i).info.gainsForSubTree = branchGains[i];
            }
            Utility.addArrayToFirst(this.gainsToAll, branchGains[2]);
        }
        for(int i = this.nodesForGains.size() - 1; i > -1; --i){
            var node = this.nodesForGains.get(i);
            for (int j = 0; j < 2; j++) {
                var child = node.childs.get(j);
                Utility.addArrayToFirst(child.info.gainsForSubTree, node.info.gainsForSubTree);
            }
            node.info.gainsForSubTree[0] = 0;
            node.info.gainsForSubTree[1] = 0;

        }

        for(var x : this.geneTrees.geneTrees){
            for(var node : x.leaves){
                if(taxas.isInRealTaxa(node.taxon.id)){
                    Utility.addArrayToFirst(
                        realTaxaGains[taxas.getRealTaxonIndex(node.taxon.id)], 
                        node.info.gainsForSubTree
                    );
                }
                node.info.gainsForSubTree[0] = 0;
                node.info.gainsForSubTree[1] = 0;

            }
        }

        // for (int i = gt.topSortedNodes.size() - 1; i > -1; i--) {
        //     var node = gt.topSortedNodes.get(i);
        //     if(node.isLeaf() && taxas.isInRealTaxa(node.taxon.id)){

        //         Utility.addArrayToFirst(
        //             this.realTaxaGains[taxas.getRealTaxonIndex(node.taxon.id)], 
        //             node.info.gainsForSubTree
        //         );
        //         continue;
        //     }
        //     else if(!node.isLeaf() && !node.isRoot()){

        //         for (int j = 0; j < 2; j++) {
        //             Utility.addArrayToFirst(node.childs.get(j).info.gainsForSubTree, node.info.gainsForSubTree);
        //         }
        //     }
        // }

        long[] p = new long[2];
        for(int i = 0; i < 2; ++i){
            p[i] = taxas.getTaxonCountInPartition(i);
        }

        long[] totals = new long[2];
        totals[1] = geneTrees.geneTrees.size() *  Utility.nc2(p[0] + 1) * Utility.nc2(p[1] - 1) ;
        totals[0] =  geneTrees.geneTrees.size() * Utility.nc2(p[0] - 1) * Utility.nc2(p[1] + 1) ;

        for(int i = 0; i < taxas.realTaxonCount; ++i){
            short partition = taxas.inWhichPartitionRealTaxonByIndex(i);
            Utility.addArrayToFirst(realTaxaGains[i], this.gainsToAll);
            realTaxaGains[i][partition] += totalScore;
            realTaxaGains[i][partition] = 2 * realTaxaGains[i][partition] - totals[partition];
        }

        for(int i = 0; i < taxas.dummyTaxonCount; ++i){
            dummyTaxaGains[i] = 2 * (dummyTaxaGains[i] + totalScore) - totals[taxas.inWhichPartitionDummyTaxonByIndex(i)];
        }

        totalScore = 2 * totalScore - geneTrees.geneTrees.size() * Utility.nc2(p[0]) * Utility.nc2(p[1]);
        
        for (int i = 0; i < realTaxaGains.length; i++) {
            realTaxaGains[i][taxas.inWhichPartitionRealTaxonByIndex(i)] -= totalScore;
        }
        for (int i = 0; i < dummyTaxaGains.length; i++) {
            dummyTaxaGains[i] -= totalScore;
        }

        this.score = totalScore;

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

    private void updateTopBranchOnRealTaxonSwap(TreeNode node, int currPartition){
        if(!node.isLeaf()){
            if(node.info.scoreCalculator != null){
                node.info.scoreCalculator.swapRealTaxon(2, currPartition);
            }
            else{
                node.info.branches[2].swapRealTaxa(currPartition);
            }
            for(var x : node.childs){
                updateTopBranchOnRealTaxonSwap(x, currPartition);
            }
        }
    }
        

    private void swapRealTaxon(int index){
        int partition = taxas.inWhichPartitionRealTaxonByIndex(index);
        if(taxas.getTaxonCountInPartition(partition) < 3){
            System.out.println("Should not be swapped");
            System.exit(-1);
        }

        taxas.swapPartitionRealTaxon(index);

        var rt = taxas.realTaxa[index];
        int rtId = rt.id;
        // System.out.println(rt.label);
        for(var tree : this.geneTrees.geneTrees){
            var node = tree.leaves[rtId];
            var parent = node.parent;
            while(!parent.isRoot()){
                for (int i = 0; i < 2; i++) {
                    var currChild = parent.childs.get(i);
                    if(currChild == node){
                        if(parent.info.scoreCalculator == null){
                            parent.info.branches[i].swapRealTaxa(partition);
                        }
                        else{
                            parent.info.scoreCalculator.swapRealTaxon(i, partition);
                        }
                    }
                    else{
                        if(!currChild.isLeaf()){
                            updateTopBranchOnRealTaxonSwap(currChild, partition);
                        }
                    }
                }
                node = parent;
                parent = node.parent;
            }
            for(int i = 0; i < 2; ++i){
                if(parent.childs.get(i) != node){
                    updateTopBranchOnRealTaxonSwap(parent.childs.get(i), partition);
                    break;
                }
            }

        }

        
    }

    public void swapTaxon(int index, boolean isDummy){
        if(isDummy) this.swapDummyTaxon(index);
        else this.swapRealTaxon(index);
    }

    private void swapDummyTaxon(int index){
        int partition = taxas.inWhichPartitionDummyTaxonByIndex(index);
        if(taxas.getTaxonCountInPartition(partition) < 3){
            System.out.println("Should not be swapped");
            System.exit(-1);
        }
        taxas.swapPartitionDummyTaxon(index);
        for(var x : this.nodesForScore){
            x.info.scoreCalculator.swapDummyTaxon(index, partition);
        }

    }

    public TaxaPerLevelWithPartition[] divide(IMakePartition makePartition){
        RealTaxon[][] rts = new RealTaxon[2][];
        DummyTaxon[][] dts = new DummyTaxon[2][];

        // short[][] rtsPart = new short[2][];
        // short[][] dtsPart = new short[2][];


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

                var y = makePartition.makePartition(rts[i], dtsWithNewDt);
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