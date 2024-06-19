package src.PreProcessing;

import java.util.Set;

import src.ScoreCalculator.NumSatCalculatorNode;

public class PartitionByTreeNode {

    public PartitionNode[] partitionNodes;
    // public int[] netTranser;

    public int count;
    public NumSatCalculatorNode scoreCalculator;

    public PartitionByTreeNode(PartitionNode[] partitionNodes){
        this.partitionNodes = partitionNodes;
        this.count = 1;
        this.scoreCalculator = null;

        for(int i = 0; i < partitionNodes.length; ++i){
            PartitionNode p = partitionNodes[i];
            p.addNodePartitions(this, i);
        }

        // this.netTranser = new int[partitionNodes.length];
    }

    public void increaseCount(){
        this.count++;
    }
    
    public void batchTransfer(int i, int netTransfer){
        // int cnt = 0;
        if(netTransfer != 0){
            // ++cnt;
            this.scoreCalculator.batchTransferRealTaxon(i, netTransfer);
            // this.scoreCalculator.swapRealTaxon(i, this.netTranser[i] > 0 ? 0 : 1);
            // this.partitionNodes[i].data.branch.swapRealTaxa(this.netTranser[i] > 0 ? 0 : 1);
            // System.out.println("net transfer: " + this.netTranser[i]);
            // if(!updatedBranches.contains(this.partitionNodes[i])){
            //     this.partitionNodes[i].data.branch.batchTransferRealTaxon(netTranser[i]);
            //     updatedBranches.add(this.partitionNodes[i]);
            // }
        }
        // netTranser[i] = 0;

        // System.out.println("cnt : " + cnt);
        
    }
    
    public void batchTransfer(Set<PartitionNode> updatedBranches){
        // int cnt = 0;
        // for(int i = 0; i < this.partitionNodes.length; ++i){
        //     if(this.netTranser[i] != 0){
        //         // ++cnt;
        //         this.scoreCalculator.batchTransferRealTaxon(i, this.netTranser[i]);
        //         // this.scoreCalculator.swapRealTaxon(i, this.netTranser[i] > 0 ? 0 : 1);
        //         // this.partitionNodes[i].data.branch.swapRealTaxa(this.netTranser[i] > 0 ? 0 : 1);
        //         // System.out.println("net transfer: " + this.netTranser[i]);
        //         // if(!updatedBranches.contains(this.partitionNodes[i])){
        //         //     this.partitionNodes[i].data.branch.batchTransferRealTaxon(netTranser[i]);
        //         //     updatedBranches.add(this.partitionNodes[i]);
        //         // }
        //     }
        //     netTranser[i] = 0;
        // }

        // System.out.println("cnt : " + cnt);
        
    }


    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        for(PartitionNode p : this.partitionNodes){
            sb.append(p.toString());
            sb.append("|");
        }
        return sb.toString();
    }


    // public void cumulateTransfer(int index, int currPartition){
    //     // negative if transfering from 1 to 0
    //     // positive if transfering from 0 to 1
    //     netTranser[index] += (currPartition == 0 ? 1 : -1);

    // }
}
