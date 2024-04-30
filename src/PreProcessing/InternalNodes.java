package src.PreProcessing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class InternalNodes {

    public ArrayList<InternalNode> nodes;
    public Map<Component, boolean[]> realTaxaInPartition;
    public Map<String, InternalNode> stringIdToInternalNode;

    public InternalNodes(Map<Component, boolean[]> realTaxaInPartition){
        this.nodes = new ArrayList<>();
        this.realTaxaInPartition = realTaxaInPartition;
        this.stringIdToInternalNode = new HashMap<>();
    }

    public InternalNode addInternalNode(Component[] childs, Component parent){
        String internalNodeString = InternalNode.convertToString(childs, parent);
        InternalNode internalNode;
        if(this.stringIdToInternalNode.containsKey(internalNodeString)){
            internalNode = this.stringIdToInternalNode.get(internalNodeString);
            internalNode.increaseCount();
        }
        else{
            internalNode = new InternalNode(childs, parent);
            this.nodes.add(internalNode);
            this.stringIdToInternalNode.put(internalNodeString, internalNode);
        }
        return internalNode;
    }

    public int getPartitionCount(){
        return this.nodes.size();
    }
    
    
}
