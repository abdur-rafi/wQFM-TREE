package src.DSPerLevel;

import java.util.ArrayList;

import src.ScoreCalculator.NumSatCalculatorNode;
import src.Taxon.RealTaxon;
import src.Tree.Branch;
import src.Tree.Info;
import src.Tree.Tree;
import src.Tree.TreeNode;

public class BookKeepingPerTree {
    
    public double[] dummyTaxonWeightsIndividual;
    public ArrayList<TreeNode> nodesForScore;
    public ArrayList<TreeNode> nodesForGains;

    public int[] realTaxaCountsInPartitions;
    public double[] dummyTaxonCountsInPartitions;
    
    public Tree geneTree;

    private double[] pairsFromPart;

    public BookKeepingPerTree(Tree geneTree, TaxaPerLevelWithPartition taxas){

        this.geneTree = geneTree;
        this.nodesForScore = new ArrayList<>();
        this.nodesForGains = new ArrayList<>();
        this.dummyTaxonWeightsIndividual = new double[taxas.dummyTaxonCount];
        this.realTaxaCountsInPartitions = new int[2];
        this.dummyTaxonCountsInPartitions = new double[2];
        this.pairsFromPart = new double[2];

        for(var x : taxas.realTaxa){
            if(geneTree.isTaxonPresent(x.id)){
                this.realTaxaCountsInPartitions[taxas.inWhichPartition(x.id)]++;
            }
        }
        int i = 0;
        for(var x : taxas.dummyTaxa){
            for(var y : x.flattenedRealTaxa){
                if(geneTree.isTaxonPresent(y.id)){
                    this.dummyTaxonWeightsIndividual[i] += taxas.getWeight(y.id);
                }
            }
            this.dummyTaxonCountsInPartitions[taxas.inWhichPartitionDummyTaxonByIndex(i)] += this.dummyTaxonWeightsIndividual[i];
            i++;
        }

        initialBookKeeping(taxas);
        this.pairsFromPart[0] = this.getTotalTaxon(0) * this.getTotalTaxon(0);
        this.pairsFromPart[1] = this.getTotalTaxon(1) * this.getTotalTaxon(1);

        for(i = 0; i < taxas.dummyTaxonCount; ++i){
            int partition = taxas.inWhichPartitionDummyTaxonByIndex(i);
            this.pairsFromPart[partition] -= this.dummyTaxonWeightsIndividual[i] * this.dummyTaxonWeightsIndividual[i]; ;
        }



        this.pairsFromPart[0] -= this.realTaxaCountsInPartitions[0];
        this.pairsFromPart[1] -= this.realTaxaCountsInPartitions[1];

        this.pairsFromPart[0] /= 2;
        this.pairsFromPart[1] /= 2;

    }

    private void initialBookKeeping(TaxaPerLevelWithPartition taxas){

        for(var node : geneTree.topSortedNodes){

            if(node.isLeaf()){
                node.info = new Info(null);
                continue;
            }
            else if(node.isRoot()){
                continue;
            }
            if(!bookKeepingAtANode(node, taxas)){
                this.nodesForScore.add(node);
                node.info.scoreCalculator = new NumSatCalculatorNode(node.info.branches,taxas.dummyTaxonPartition);
            }
            this.nodesForGains.add(node);
        }
    }


    private boolean bookKeepingAtANode(
        TreeNode node
        ,TaxaPerLevelWithPartition taxas
        ){

        Branch[] branches = new Branch[3];
        boolean skip = false;
        int[] nonZeroDummyCount = new int[2];
        int[] nonZeroDummyIndex = new int[2];

        for(int i = 0; i < 3; ++i){
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
                    int partition = taxas.inWhichPartition(taxonId);
                    branches[i].dummyTaxaWeightsIndividual[taxas.inWhichDummyTaxa(taxonId)] += weight;
                    branches[i].totalTaxaCounts[partition] += weight;

                    nonZeroDummyCount[i]++;
                    nonZeroDummyIndex[i] = taxas.inWhichDummyTaxa(taxonId);
                }
            }
            else{
                for(int p = 0; p < 2; ++p){
                    // branches[i].realTaxaCounts[p] += taxas.getRealTaxonCountInPartition(p) - child.info.branches[2].realTaxaCounts[p];
                    branches[i].realTaxaCounts[p] += this.realTaxaCountsInPartitions[p] - child.info.branches[2].realTaxaCounts[p];

                    branches[i].totalTaxaCounts[p] = this.getTotalTaxon(p) - child.info.branches[2].totalTaxaCounts[p];

                }
                for(int j = 0; j < taxas.dummyTaxonCount; ++j){

                    branches[i].dummyTaxaWeightsIndividual[j] += this.getDummyTaxonIndiWeight(j) - child.info.branches[2].dummyTaxaWeightsIndividual[j];
                    if(branches[i].dummyTaxaWeightsIndividual[j] != 0){
                        nonZeroDummyCount[i]++;
                        nonZeroDummyIndex[i] = j;
                    }
                }
            }

        }
        for(int j = 0; j < taxas.dummyTaxonCount; ++j){
            branches[2].dummyTaxaWeightsIndividual[j] = this.getDummyTaxonIndiWeight(j) - branches[0].dummyTaxaWeightsIndividual[j] - branches[1].dummyTaxaWeightsIndividual[j];
        }
        for(int p = 0; p < 2; ++p){
            branches[2].realTaxaCounts[p] = this.realTaxaCountsInPartitions[p] - branches[0].realTaxaCounts[p] - branches[1].realTaxaCounts[p];
            branches[2].totalTaxaCounts[p] = this.getTotalTaxon(p) - branches[0].totalTaxaCounts[p] - branches[1].totalTaxaCounts[p];
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
        return skip;
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
        

    public void swapRealTaxon(RealTaxon taxon, int partition){
        var rt = taxon;
        int rtId = rt.id;

        if(!this.geneTree.isTaxonPresent(rtId)){
            return;
        }


        this.pairsFromPart[ 1 - partition] += this.getTotalTaxon(1 - partition);
        this.pairsFromPart[partition] -= this.getTotalTaxon(partition) - 1;
        
        this.realTaxaCountsInPartitions[partition]--;
        this.realTaxaCountsInPartitions[1 - partition]++;

        
        

        var node = this.geneTree.leaves[rtId];
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


    public void swapDummyTaxon(int index, int partition){

        this.pairsFromPart[ 1 - partition] += this.dummyTaxonWeightsIndividual[index] * this.getTotalTaxon(1 - partition);
        this.pairsFromPart[partition] -= this.dummyTaxonWeightsIndividual[index] * (this.getTotalTaxon(partition) - this.dummyTaxonWeightsIndividual[index]);
        
        this.dummyTaxonCountsInPartitions[partition] -= this.dummyTaxonWeightsIndividual[index];
        this.dummyTaxonCountsInPartitions[1 - partition] += this.dummyTaxonWeightsIndividual[index];

        for(var x : this.nodesForScore){
            x.info.scoreCalculator.swapDummyTaxon(index, partition);
        }

    }


    public double getTotalTaxon(int p){
        return this.realTaxaCountsInPartitions[p] + this.dummyTaxonCountsInPartitions[p];
    }

    public double getDummyTaxonIndiWeight(int index){
        return this.dummyTaxonWeightsIndividual[index];
    }

    public double totalQuartets(){
        return this.pairsFromPart[0] * this.pairsFromPart[1];
    }

    public double totalQuartetsAfterSwap(int toPartition){
        return (this.pairsFromPart[1 - toPartition] - (this.getTotalTaxon(1 - toPartition) - 1)) * 
        (this.pairsFromPart[toPartition] + (this.getTotalTaxon(toPartition)));
    }

    public double totalQuartetsAfterDummySwap(int dIndex, int toPartition){
        double a = this.pairsFromPart[1 - toPartition] - this.dummyTaxonWeightsIndividual[dIndex] * (
            this.getTotalTaxon(1-toPartition) - 
            this.getDummyTaxonIndiWeight(dIndex));
        double b = this.pairsFromPart[toPartition] + this.dummyTaxonWeightsIndividual[dIndex] * (this.getTotalTaxon(toPartition));
        return a * b;
    }
}
