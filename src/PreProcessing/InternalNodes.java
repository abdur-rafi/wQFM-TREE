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

    public void addInternalNode(Component[] childCompsCommon, Component[] childCompsUniques, Component parentUniques){
        String internalNodeString = InternalNode.convertToString(childCompsCommon, childCompsUniques, parentUniques);

        if(this.stringIdToInternalNode.containsKey(internalNodeString)){
            InternalNode internalNode = this.stringIdToInternalNode.get(internalNodeString);
            internalNode.increaseCount();
        }
        else{
            InternalNode internalNode = new InternalNode(childCompsCommon, childCompsUniques, parentUniques);
            this.nodes.add(internalNode);
            this.stringIdToInternalNode.put(internalNodeString, internalNode);
        }
    }

    public int getPartitionCount(){
        return this.nodes.size();
    }
    
    
}
