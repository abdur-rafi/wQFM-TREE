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
    }

    public void increaseCount(){
        this.count++;
    }
    


    
}
