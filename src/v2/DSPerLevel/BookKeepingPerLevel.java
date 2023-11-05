package src.v2.DSPerLevel;

import src.Utility;
import src.v2.PreProcessing.GeneTrees;
import src.v2.ScoreCalculator.ScoreCalculatorNode;
import src.v2.Tree.Branch;
import src.v2.Tree.Info;
import src.v2.Tree.TreeNode;

public class BookKeepingPerLevel {

    public final GeneTrees geneTrees;


    public TaxaPerLevelWithPartition taxas;

    public double[][] realTaxaGains;
    public double[] dummyTaxaGains;
    public double score;

    double[] gainsToAll;

    public BookKeepingPerLevel(GeneTrees geneTrees, TaxaPerLevelWithPartition taxaPerLevelWithPartition){

        this.taxas = taxaPerLevelWithPartition;
        this.geneTrees = geneTrees;

        this.realTaxaGains = new double[taxas.realTaxonCount][2];
        this.dummyTaxaGains = new double[taxas.dummyTaxonCount];

        this.gainsToAll = new double[2];

    }

    private void bookKeepingAtANode(TreeNode node){
        Branch[] branches = new Branch[3];
        for(int i = 0; i < 3; ++i){
            branches[i] = new Branch(new int[2], new double[taxas.dummyTaxonCount], new double[2]);
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
                    branches[i].dummyTaxaWeightSums[partition] += weight;
                    branches[i].dummyTaxaWeightsIndividual[taxas.inWhichDummyTaxa(taxonId)] += weight;
                    branches[i].totalTaxaCounts[partition] += weight;
                }
            }
            else{
                for(int p = 0; p < 2; ++p){
                    branches[i].realTaxaCounts[p] += taxas.getRealTaxonCountInPartition(p) - child.info.branches[2].realTaxaCounts[p];
                    branches[i].dummyTaxaWeightSums[p] += taxas.getDummyTaxonCountInPartition(p) - child.info.branches[2].dummyTaxaWeightSums[p];
                    branches[i].totalTaxaCounts[p] += branches[i].realTaxaCounts[p] + branches[i].dummyTaxaWeightSums[p];
                }
                for(int j = 0; j < taxas.dummyTaxonCount; ++j){
                    branches[i].dummyTaxaWeightsIndividual[j] += 1. - child.info.branches[2].dummyTaxaWeightsIndividual[j];
                }
            }
        }
        for(int j = 0; j < taxas.dummyTaxonCount; ++j){
            branches[2].dummyTaxaWeightsIndividual[j] = 1. - branches[0].dummyTaxaWeightsIndividual[j] - branches[1].dummyTaxaWeightsIndividual[j];
        }
        for(int p = 0; p < 2; ++p){
            branches[2].realTaxaCounts[p] = taxas.getRealTaxonCountInPartition(p) - branches[0].realTaxaCounts[p] - branches[1].realTaxaCounts[p];
            branches[2].dummyTaxaWeightSums[p] = taxas.getDummyTaxonCountInPartition(p) - branches[0].dummyTaxaWeightSums[p] - branches[1].dummyTaxaWeightSums[p];
            branches[2].totalTaxaCounts[p] = branches[2].realTaxaCounts[p] + branches[2].dummyTaxaWeightSums[p];
        }

        node.info = new Info(branches);
    }

    public double calculateScoreAndGains(){
        double totalScore = 0;
        for(var gt : geneTrees.geneTrees){
            for(var node : gt.topSortedNodes){
                if(node.isLeaf()){
                    node.info = new Info(null);
                    continue;
                }
                else if(node.isRoot()){
                    continue;
                }
                bookKeepingAtANode(node);
                if(node.frequency == 0){
                    continue;
                }   
                node.scoreCalculator = new ScoreCalculatorNode(node.info.branches, taxas.dummyTaxonPartition, this.dummyTaxaGains);
                double score = node.scoreCalculator.score();
                var branchGains = node.scoreCalculator.gain(score, node.frequency);
                node.scoreCalculator.calcDummyTaxaGains(score, node.frequency);
                
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
            for (int i = gt.topSortedNodes.size() - 1; i > -1; i--) {
                var node = gt.topSortedNodes.get(i);
                if(node.isLeaf() && taxas.isInRealTaxa(node.taxon.id)){

                    Utility.addArrayToFirst(
                        this.realTaxaGains[taxas.getRealTaxonIndex(node.taxon.id)], 
                        node.info.gainsForSubTree
                    );
                    continue;
                }
                else if(!node.isLeaf() && !node.isRoot()){

                    for (int j = 0; j < 2; j++) {
                        Utility.addArrayToFirst(node.childs.get(j).info.gainsForSubTree, node.info.gainsForSubTree);
                    }
                }
            }
        }

        long[] p = new long[2];
        for(int i = 0; i < 2; ++i){
            p[i] = taxas.getTaxonCountInPartition(i);
        }

        long[] totals = new long[2];
        totals[1] = geneTrees.geneTrees.size() *  Utility.nc2(p[0] + 1) * Utility.nc2(p[1] - 1) ;
        totals[0] =  geneTrees.geneTrees.size() * Utility.nc2(p[0] - 1) * Utility.nc2(p[1] + 1) ;

        for(int i = 0; i < taxas.realTaxonCount; ++i){
            short partition = taxas.inWhichPartitionRealTaxonByIndex(i);
            Utility.addArrayToFirst(this.realTaxaGains[i], this.gainsToAll);
            this.realTaxaGains[i][partition] += totalScore;
            this.realTaxaGains[i][partition] = 2 * this.realTaxaGains[i][partition] - totals[partition];
        }

        for(int i = 0; i < taxas.dummyTaxonCount; ++i){
            this.dummyTaxaGains[i] = 2 * (this.dummyTaxaGains[i] + totalScore) - totals[taxas.inWhichPartitionDummyTaxonByIndex(i)];
        }

        totalScore = 2 * totalScore - geneTrees.geneTrees.size() * Utility.nc2(p[0]) * Utility.nc2(p[1]);
        
        for (int i = 0; i < this.realTaxaGains.length; i++) {
            this.realTaxaGains[i][taxas.inWhichPartitionRealTaxonByIndex(i)] -= totalScore;
        }
        for (int i = 0; i < this.dummyTaxaGains.length; i++) {
            this.dummyTaxaGains[i] -= totalScore;
        }

        this.score = totalScore;

        System.out.println("Score : " + totalScore);

        for (int i = 0; i < realTaxaGains.length; i++) {
            var rt = taxas.realTaxa[i];
            System.out.println(rt.label + ": " + (realTaxaGains[i][taxas.inWhichPartitionRealTaxonByIndex(i)] + totalScore));
        }
        
        for (int i = 0; i < dummyTaxaGains.length; i++) {
            var dt = taxas.dummyTaxa[i];
            for(var  x : dt.flattenedRealTaxa){
                System.out.printf(x.label + " ,");
            }
            System.out.println(": " + (dummyTaxaGains[i] + totalScore));
        }

        return totalScore;

    }    


}
