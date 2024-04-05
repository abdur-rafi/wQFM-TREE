package src.PreProcessing;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class PartitionsByTreeNode {

    public ArrayList<PartitionByTreeNode> partitions;
    public Map<PartitionNode, boolean[]> realTaxaInPartition;
    public Map<String, PartitionByTreeNode> stringIdToPartitionByTreeNode;

    public PartitionsByTreeNode(Map<PartitionNode, boolean[]> realTaxaInPartition){
        this.partitions = new ArrayList<>();
        this.realTaxaInPartition = realTaxaInPartition;
        this.stringIdToPartitionByTreeNode = new HashMap<>();
    }

    public void addPartitionByTreeNode(PartitionNode[] partitionNodes, PartitionNode[][] gainChildNodes, PartitionNode gainParentNode){
        ArrayList<String> partitionStrings = new ArrayList<>();        
        for(PartitionNode p : partitionNodes){
            partitionStrings.add(Utility.getPartitionString(this.realTaxaInPartition.get(p)));
        }
        Collections.sort(partitionStrings);
        StringBuilder sb = new StringBuilder();
        for(String s : partitionStrings){
            // System.out.println(s);
            sb.append(s);
        }
        // System.out.println();
        String partitionByTreeNodeString = sb.toString();

        if(this.stringIdToPartitionByTreeNode.containsKey(partitionByTreeNodeString)){
            PartitionByTreeNode partitionByTreeNode = this.stringIdToPartitionByTreeNode.get(partitionByTreeNodeString);
            partitionByTreeNode.increaseCount();
        }
        else{
            PartitionByTreeNode partitionByTreeNode = new PartitionByTreeNode(partitionNodes, gainChildNodes, gainParentNode);
            this.partitions.add(partitionByTreeNode);
            this.stringIdToPartitionByTreeNode.put(partitionByTreeNodeString, partitionByTreeNode);
        }
    }

    public int getPartitionCount(){
        return this.partitions.size();
    }
    
    
}
