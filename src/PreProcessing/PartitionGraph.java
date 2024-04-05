package src.PreProcessing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import src.Taxon.RealTaxon;

public class PartitionGraph {
    

    public RealTaxon[] taxa;
    public PartitionNode[] taxaPartitionNodes;
    public Map<PartitionNode, boolean[]> realTaxaInPartition;
    private Map<String, PartitionNode> stringIdToPartition;

    public ArrayList<PartitionNode> partitionNodes;

    public PartitionNode sentinel;


    public int count = 0;
    

    public PartitionGraph(RealTaxon[] taxa){
        this.taxa = taxa;
        this.taxaPartitionNodes = new PartitionNode[taxa.length];
        this.realTaxaInPartition = new HashMap<>();
        this.stringIdToPartition = new HashMap<>();
        this.partitionNodes = new ArrayList<>();

        for(int i = 0; i < taxa.length; ++i){
            this.taxaPartitionNodes[i] = new PartitionNode(true);
            boolean[] realTaxaInSubTree = new boolean[taxa.length];
            realTaxaInSubTree[i] = true;
            this.realTaxaInPartition.put(this.taxaPartitionNodes[i], realTaxaInSubTree);
            this.stringIdToPartition.put(Utility.getPartitionString(realTaxaInSubTree), this.taxaPartitionNodes[i]);
            this.partitionNodes.add(this.taxaPartitionNodes[i]);
            this.taxaPartitionNodes[i].label = taxa[i].label;
        }

        this.sentinel = new PartitionNode(true);
        boolean[] realTaxaInSubTree = new boolean[taxa.length];
        this.realTaxaInPartition.put(this.sentinel, realTaxaInSubTree);
        this.stringIdToPartition.put(Utility.getPartitionString(realTaxaInSubTree), this.sentinel);
        

        count = taxa.length;

    }

    public PartitionNode getSentinel(){
        return this.sentinel;
    }

    public PartitionNode getPartitionNode(RealTaxon taxon){
        return this.taxaPartitionNodes[taxon.id];
    }

    public PartitionNode addPartition(ArrayList<PartitionNode> childs, boolean forGain){
        boolean[] b = new boolean[this.taxa.length];
        for(PartitionNode child: childs){
            boolean[] realTaxaInSubTree = this.realTaxaInPartition.get(child);
            for(int i = 0; i < this.taxa.length; ++i){
                b[i] = b[i] || realTaxaInSubTree[i];
            }
        }

        String partitionString = Utility.getPartitionString(b);
        if(this.stringIdToPartition.containsKey(partitionString)){
            // System.out.println("-------here-----------");
            PartitionNode node = this.stringIdToPartition.get(partitionString);
            if(forGain){
                node.gainPartition = true;
            }
            // Set<String> nodeChildren = new HashSet<>();
            // for(PartitionNode child: node.children){
            //     nodeChildren.add(Utility.getPartitionString(this.realTaxaInPartition.get(child)));
            // }
            // System.out.println(nodeChildren);
            // for(PartitionNode child: childs){
            //     if(nodeChildren.contains(Utility.getPartitionString(this.realTaxaInPartition.get(child)))){
            //         continue;
            //     }
            //     node.addChild(child);
            //     child.addParent(node);
            //     // if(node.children.contains(child)){
            //     //     System.out.println("continue");
            //     //     continue;
            //     // }
            //     // // System.out.println("Not continue");
            //     // node.addChild(child);
            //     // child.addParent(node);
            // }
            // for(PartitionNode child: childs){
            //     if(child.isLeaf){
            //         if(child.parents.contains(node)){
            //             continue;
            //         }
            //         child.addParent(node);
            //     }  
            // }
            return node;
        }
        else{
            PartitionNode partitionNode = new PartitionNode(false);
            for(PartitionNode child: childs){
                partitionNode.addChild(child);
                child.addParent(partitionNode);
            }
            this.realTaxaInPartition.put(partitionNode, b);
            this.stringIdToPartition.put(partitionString, partitionNode);
            this.partitionNodes.add(partitionNode);
            if(forGain){
                partitionNode.onlyGainPartition = true;
                partitionNode.gainPartition = true;
            }
            count += 1;
            return partitionNode;
        }
        
    }

    public void removeOnlyGainPartitionsFromParent(){
        for(PartitionNode partitionNode: this.partitionNodes){
            ArrayList<PartitionNode> filteredParents = new ArrayList<>();
            for(PartitionNode parent: partitionNode.parents){
                if(!parent.onlyGainPartition){
                    filteredParents.add(parent);
                }
            }
            partitionNode.parents = filteredParents;
        }
    }

    public ArrayList<PartitionNode> getTopSortedNodes(){
        ArrayList<PartitionNode> topSortedNodes = new ArrayList<>();
        
        Queue<PartitionNode> q = new java.util.LinkedList<>();
        Map<PartitionNode, Integer> inDegree = new HashMap<>();
        
        for(PartitionNode partitionNode: this.partitionNodes){
            if(partitionNode.parents.size() == 0){
                q.add(partitionNode);
            }
            else{
                inDegree.put(partitionNode, partitionNode.parents.size());
            }
        }

        while(!q.isEmpty()){
            PartitionNode partitionNode = q.poll();
            topSortedNodes.add(partitionNode);
            for(PartitionNode child: partitionNode.children){
                inDegree.put(child, inDegree.get(child) - 1);
                if(inDegree.get(child) == 0){
                    q.add(child);
                }
            }
        }
        


        return topSortedNodes;
    }




}
