package src.DSPerLevel;

import java.util.ArrayList;

import src.Taxon.RealTaxon;
import src.Tree.Branch;
import src.Tree.Info;
import src.Tree.Tree;
import src.Tree.TreeNode;
import src.ScoreCalculator.NumSatCalculatorBinaryNode;
import src.ScoreCalculator.NumSatCalculatorNode;
import src.ScoreCalculator.NumSatCalculatorNodeE;

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
                NumSatCalculatorNode sCalculatorNode = null;
                // sCalculatorNode  = new NumSatCalculatorNodeE(node.info.branches, taxas.dummyTaxonPartition, getTotalTaxon(0), getTotalTaxon(1), dummyTaxonWeightsIndividual);
                if(node.childs.size() == 2){
                    sCalculatorNode = new NumSatCalculatorBinaryNode(node.info.branches, taxas.dummyTaxonPartition);
                }
                else if(node.childs.size() > 2){
                    // System.out.println("klajsdf");
                    sCalculatorNode = new NumSatCalculatorNodeE(node.info.branches, taxas.dummyTaxonPartition);
                }
                else{
                    System.out.println("error, branch length is less than 3");
                    System.exit(-1);
                }
                node.info.scoreCalculator = sCalculatorNode;
                
            }
            this.nodesForGains.add(node);
        }
    }


    private boolean bookKeepingAtANode(
        TreeNode node
        ,TaxaPerLevelWithPartition taxas
        ){

        int childCount = node.childs.size();
        Branch[] branches = new Branch[childCount + 1];
        boolean skip = false;
        int[] nonZeroDummyCount = new int[childCount];
        int[] nonZeroDummyIndex = new int[childCount];

        for(int i = 0; i <= childCount; ++i){
            branches[i] = new Branch(taxas.dummyTaxonCount);

        }
        for(int i = 0; i < childCount; ++i){

            var child = node.childs.get(i);

            if(child.isLeaf()){
                int taxonId = child.taxon.id;
                if(taxas.isInRealTaxa(taxonId)){
                    branches[i].realTaxaCounts[taxas.inWhichPartition(taxonId)]++;
                    branches[i].totalTaxaCounts[taxas.inWhichPartition(taxonId)]++;

                    branches[childCount].realTaxaCounts[taxas.inWhichPartition(taxonId)]++;
                    branches[childCount].totalTaxaCounts[taxas.inWhichPartition(taxonId)]++;

                }
                else if(taxas.isInDummyTaxa(taxonId)){
                    double weight = taxas.getWeight(taxonId);
                    int partition = taxas.inWhichPartition(taxonId);
                    branches[i].dummyTaxaWeightsIndividual[taxas.inWhichDummyTaxa(taxonId)] += weight;
                    branches[i].totalTaxaCounts[partition] += weight;

                    branches[childCount].dummyTaxaWeightsIndividual[taxas.inWhichDummyTaxa(taxonId)] += weight;
                    branches[childCount].totalTaxaCounts[partition] += weight;

                    nonZeroDummyCount[i]++;
                    nonZeroDummyIndex[i] = taxas.inWhichDummyTaxa(taxonId);
                }
            }
            else{
                int branchChildCount = child.childs.size();
                for(int p = 0; p < 2; ++p){
                    // branches[i].realTaxaCounts[p] += taxas.getRealTaxonCountInPartition(p) - child.info.branches[2].realTaxaCounts[p];
                    branches[i].realTaxaCounts[p] += this.realTaxaCountsInPartitions[p] - child.info.branches[branchChildCount].realTaxaCounts[p];

                    branches[i].totalTaxaCounts[p] = this.getTotalTaxon(p) - child.info.branches[branchChildCount].totalTaxaCounts[p];

                    branches[childCount].realTaxaCounts[p] += branches[i].realTaxaCounts[p];
                    branches[childCount].totalTaxaCounts[p] += branches[i].totalTaxaCounts[p];

                }
                for(int j = 0; j < taxas.dummyTaxonCount; ++j){

                    branches[i].dummyTaxaWeightsIndividual[j] += this.getDummyTaxonIndiWeight(j) - child.info.branches[branchChildCount].dummyTaxaWeightsIndividual[j];
                    branches[childCount].dummyTaxaWeightsIndividual[j] += branches[i].dummyTaxaWeightsIndividual[j];
                    
                    if(branches[i].dummyTaxaWeightsIndividual[j] != 0){
                        nonZeroDummyCount[i]++;
                        nonZeroDummyIndex[i] = j;
                    }
                }
            }
            // Utility.addArrayToFirst(branches[childCount].totalTaxaCounts, branches[i].totalTaxaCounts);
            // Utility.addArrayToFirst(branches[childCount].realTaxaCounts, branches[i].realTaxaCounts);
            // Utility.addArrayToFirst(branches[childCount].dummyTaxaWeightsIndividual, branches[i].dummyTaxaWeightsIndividual);
            

        }
        for(int j = 0; j < taxas.dummyTaxonCount; ++j){
            branches[childCount].dummyTaxaWeightsIndividual[j] = this.getDummyTaxonIndiWeight(j) - branches[childCount].dummyTaxaWeightsIndividual[j];
            // branches[2].dummyTaxaWeightsIndividual[j] = this.getDummyTaxonIndiWeight(j) - branches[0].dummyTaxaWeightsIndividual[j] - branches[1].dummyTaxaWeightsIndividual[j];
        }
        for(int p = 0; p < 2; ++p){
            branches[childCount].realTaxaCounts[p] = this.realTaxaCountsInPartitions[p] - branches[childCount].realTaxaCounts[p];
            branches[childCount].totalTaxaCounts[p] = this.getTotalTaxon(p) - branches[childCount].totalTaxaCounts[p];
        }
        
        if(node.frequency == 0){
            skip = true;
        }
        // else if(nonZeroDummyCount[0] == 1) {

        //     // boolean breaked = false;
        //     // int rtCount = branches[0].realTaxaCounts[0] + branches[0].realTaxaCounts[1];
        //     // for(int i = 1; i < childCount; ++i){
                
        //     //     if(nonZeroDummyCount[i] != 1){
        //     //         breaked = true;
        //     //         break;
        //     //     }
        //     //     rtCount += branches[i].realTaxaCounts[0] + branches[i].realTaxaCounts[1];
        //     // }
        //     // if(!breaked && rtCount == 0){
        //     //     skip = true;
        //     // }

        //     // if(nonZeroDummyCount[0] == nonZeroDummyCount[1]){
        //     //     if(nonZeroDummyCount[0] == 1 && nonZeroDummyIndex[0] == nonZeroDummyIndex[1] ){
        //     //         if(branches[0].realTaxaCounts[0] + branches[0].realTaxaCounts[1] + 
        //     //         branches[1].realTaxaCounts[0] + branches[1].realTaxaCounts[1] == 0){
        //     //             skip = true;
        //     //         }
        //     //     }
        //     // }
        // }

        node.info = new Info(branches);
        return skip;
    }


    private void updateTopBranchOnRealTaxonSwap(TreeNode node, int currPartition){
        if(!node.isLeaf()){
            int childCount = node.childs.size();
            if(node.info.scoreCalculator != null){
                node.info.scoreCalculator.swapRealTaxon(childCount, currPartition);
            }
            else{
                node.info.branches[childCount].swapRealTaxa(currPartition);
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
            for (int i = 0; i < parent.childs.size(); i++) {
                var currChild = parent.childs.get(i);
                if(currChild == node){
                    if(parent.info.scoreCalculator == null){
                        parent.info.branches[i].swapRealTaxa(partition);
                    }
                    else{
                        parent.info.scoreCalculator.transferRealTaxon(i, partition);
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
        for(int i = 0; i < parent.childs.size(); ++i){
            if(parent.childs.get(i) != node){
                updateTopBranchOnRealTaxonSwap(parent.childs.get(i), partition);
                // break;
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

    public double totalQuartetsAfterSwap(int rtId, int toPartition){
        if(!this.geneTree.isTaxonPresent(rtId)){
            // return 0;
            return this.totalQuartets();
        }
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
