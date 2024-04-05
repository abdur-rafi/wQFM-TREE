package src.PreProcessing;

import src.ScoreCalculator.NumSatCalculatorNode;

public class PartitionByTreeNode {

    public PartitionNode[] partitionNodes;
    public PartitionNode[][] gainChildNodes;
    public PartitionNode gainParentNode;

    public int[] netTranser;

    public int count;
    public NumSatCalculatorNode scoreCalculator;

    public PartitionByTreeNode(PartitionNode[] partitionNodes, PartitionNode[][] gainChildNodes, PartitionNode gainParentNode){
        this.partitionNodes = partitionNodes;
        this.gainChildNodes = gainChildNodes;
        this.gainParentNode = gainParentNode;

        this.count = 1;
        this.scoreCalculator = null;

        for(int i = 0; i < partitionNodes.length; ++i){
            if(partitionNodes.length > 3){
                System.out.println("polytomy");
            }
            PartitionNode p = partitionNodes[i];
            p.addNodePartitions(this, i);
        }


        this.netTranser = new int[partitionNodes.length];

    }

    public void increaseCount(){
        this.count++;
    }
    
    
    public void batchTransfer(){
        for(int i = 0; i < this.partitionNodes.length; ++i){
            if(this.netTranser[i] != 0){
                this.scoreCalculator.batchTransferRealTaxon(i, this.netTranser[i]);
            }
            netTranser[i] = 0;
        }
        
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


    public void cumulateTransfer(int index, int currPartition){
        // negative if transfering from 1 to 0
        // positive if transfering from 0 to 1
        netTranser[index] += (currPartition == 0 ? 1 : -1);

    }
}
