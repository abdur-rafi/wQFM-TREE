package src.PreProcessing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import src.Taxon.RealTaxon;

public class PartitionGraph {
    

    public RealTaxon[] taxa;
    public PartitionNode[] taxaPartitionNodes;
    public Map<PartitionNode, boolean[]> realTaxaInPartition;
    private Map<String, PartitionNode> stringIdToPartition;



    public int count = 0;
    

    public PartitionGraph(RealTaxon[] taxa){
        this.taxa = taxa;
        this.taxaPartitionNodes = new PartitionNode[taxa.length];
        this.realTaxaInPartition = new HashMap<>();
        this.stringIdToPartition = new HashMap<>();

        for(int i = 0; i < taxa.length; ++i){
            this.taxaPartitionNodes[i] = new PartitionNode(true);
            boolean[] realTaxaInSubTree = new boolean[taxa.length];
            realTaxaInSubTree[i] = true;
            this.realTaxaInPartition.put(this.taxaPartitionNodes[i], realTaxaInSubTree);
            this.stringIdToPartition.put(Utility.getPartitionString(realTaxaInSubTree), this.taxaPartitionNodes[i]);
        }

        count = taxa.length;

    }

    public PartitionNode getPartitionNode(RealTaxon taxon){
        return this.taxaPartitionNodes[taxon.id];
    }

    public PartitionNode addPartition(ArrayList<PartitionNode> childs){
        boolean[] b = new boolean[this.taxa.length];
        for(PartitionNode child: childs){
            boolean[] realTaxaInSubTree = this.realTaxaInPartition.get(child);
            for(int i = 0; i < this.taxa.length; ++i){
                b[i] = b[i] || realTaxaInSubTree[i];
            }
        }

        String partitionString = Utility.getPartitionString(b);
        if(this.stringIdToPartition.containsKey(partitionString)){
            return this.stringIdToPartition.get(partitionString);
        }
        else{
            PartitionNode partitionNode = new PartitionNode(false);
            for(PartitionNode child: childs){
                partitionNode.addChild(child);
                child.addParent(partitionNode);
            }
            this.realTaxaInPartition.put(partitionNode, b);
            this.stringIdToPartition.put(partitionString, partitionNode);
            count += 1;
            return partitionNode;
        }
        
    }



}
