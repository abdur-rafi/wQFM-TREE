package src.PreProcessing;

public class PartitionByTreeNode {

    public PartitionNode[] partitionNodes;

    public int count;

    public PartitionByTreeNode(PartitionNode[] partitionNodes){
        this.partitionNodes = partitionNodes;
        this.count = 1;
    }

    public void increaseCount(){
        this.count++;
    }

    
}
