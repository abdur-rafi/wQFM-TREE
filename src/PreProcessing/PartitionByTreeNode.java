package src.PreProcessing;

import src.ScoreCalculator.NumSatCalculatorNode;

public class PartitionByTreeNode {

    public PartitionNode[] partitionNodes;

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
    }

    public void increaseCount(){
        this.count++;
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
}
