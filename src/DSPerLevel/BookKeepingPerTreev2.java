package src.DSPerLevel;

import java.util.ArrayList;

import src.Taxon.RealTaxon;
import src.Tree.Branch;
import src.Tree.Info;
import src.Tree.Tree;
import src.Tree.TreeNode;
import src.ScoreCalculator.NumSatCalculatorBinaryNodev2;
import src.ScoreCalculator.NumSatCalculatorNode;
import src.ScoreCalculator.NumSatCalculatorNodeEv2;

public class BookKeepingPerTreev2 {
    
    public double[] dummyTaxonWeightsIndividual;
    public TreeNode[] nodesForScore;
    public TreeNode[] nodesForGains;

    public int[] realTaxaCountsInPartitions;
    public double[] dummyTaxonCountsInPartitions;
    
    public Tree geneTree;

    private double[] pairsFromPart;

    private int allocateForDummy;

    public BookKeepingPerTreev2(Tree geneTree){

        this.geneTree = geneTree;
        double logn = 4 * Math.log(geneTree.leaves.length) / Math.log(2);
        int roundedLogn = (int) Math.ceil(logn);
        
        this.allocateForDummy = roundedLogn;

        this.dummyTaxonWeightsIndividual = new double[this.allocateForDummy];

        this.realTaxaCountsInPartitions = new int[2];
        
        this.dummyTaxonCountsInPartitions = new double[2];
        
        this.pairsFromPart = new double[2];

    

        initMemory();

    }

    public void resetBookkeeping(TaxaPerLevelWithPartition taxas){

        for(int i = 0; i < 2; ++i){
            this.realTaxaCountsInPartitions[i] = 0;
            this.dummyTaxonCountsInPartitions[i] = 0;
        }
        for(int i = 0; i < this.dummyTaxonWeightsIndividual.length; ++i){
            this.dummyTaxonWeightsIndividual[i] = 0;
        }

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

        for(var node : this.nodesForGains){
            resetBookKeepingAtNode(node, taxas);
            node.info.scoreCalculator.initBookkeeping(
                taxas.dummyTaxonPartition, 
                getTotalTaxon(0), 
                getTotalTaxon(1), 
                dummyTaxonWeightsIndividual,
                taxas.dummyTaxonCount
            );
        }

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



    private void resetBookKeepingAtNode(
        TreeNode node,
        TaxaPerLevelWithPartition taxas
    ){
        int childCount = node.childs.size();
        var branches = node.info.branches;

        for(int i = 0; i <= childCount; ++i){
            branches[i].reset();
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
                    
                }
            }

        }

        for(int j = 0; j < taxas.dummyTaxonCount; ++j){
            branches[childCount].dummyTaxaWeightsIndividual[j] = this.getDummyTaxonIndiWeight(j) - branches[childCount].dummyTaxaWeightsIndividual[j];
            // branches[2].dummyTaxaWeightsIndividual[j] = this.getDummyTaxonIndiWeight(j) - branches[0].dummyTaxaWeightsIndividual[j] - branches[1].dummyTaxaWeightsIndividual[j];
        }
        for(int p = 0; p < 2; ++p){
            branches[childCount].realTaxaCounts[p] = this.realTaxaCountsInPartitions[p] - branches[childCount].realTaxaCounts[p];
            branches[childCount].totalTaxaCounts[p] = this.getTotalTaxon(p) - branches[childCount].totalTaxaCounts[p];
        }
        
    }

    private void initMemory(){

        ArrayList<TreeNode> nodesForScore = new ArrayList<>();
        ArrayList<TreeNode> nodesForGains = new ArrayList<>();

        for(var node : geneTree.topSortedNodes){

            if(node.isLeaf()){
                node.info = new Info(null);
                continue;
            }
            else if(node.isRoot()){
                continue;
            }
            if(!initMemoryAtNode(node)){
                nodesForScore.add(node);
                NumSatCalculatorNode sCalculatorNode = null;
                if(node.childs.size() == 2){
                    sCalculatorNode = new NumSatCalculatorBinaryNodev2(node.info.branches);
                }
                else if(node.childs.size() > 2){
                    sCalculatorNode = new NumSatCalculatorNodeEv2(node.info.branches);
                }
                else{
                    System.out.println("error, branch length is less than 3");
                    System.exit(-1);
                }
                node.info.scoreCalculator = sCalculatorNode;
            }
            nodesForGains.add(node);
        }
        this.nodesForGains = nodesForGains.toArray(new TreeNode[nodesForGains.size()]);
        this.nodesForScore = nodesForScore.toArray(new TreeNode[nodesForScore.size()]);
    }


    private boolean initMemoryAtNode(
            TreeNode node
        ){

        int childCount = node.childs.size();
        Branch[] branches = new Branch[childCount + 1];
        for(int i = 0; i <= childCount; ++i){
            branches[i] = new Branch(this.allocateForDummy);
        }
        return node.frequency == 0;
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
