package src.PreProcessing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;

import src.Taxon.RealTaxon;

public class ComponentGraph {
    

    public RealTaxon[] taxa;
    public Component[] taxaPartitionNodes;
    public Map<Component, boolean[]> realTaxaInComponent;
    private Map<String, Component> stringIdToComponent;

    public ArrayList<Component> components;

    private Component sentinel;


    public int count = 0;
    

    public ComponentGraph(RealTaxon[] taxa){
        this.taxa = taxa;
        this.taxaPartitionNodes = new Component[taxa.length];
        this.realTaxaInComponent = new HashMap<>();
        this.stringIdToComponent = new HashMap<>();
        this.components = new ArrayList<>();

        for(int i = 0; i < taxa.length; ++i){
            this.taxaPartitionNodes[i] = new Component(true);
            boolean[] realTaxaInSubTree = new boolean[taxa.length];
            realTaxaInSubTree[i] = true;
            this.realTaxaInComponent.put(this.taxaPartitionNodes[i], realTaxaInSubTree);
            this.stringIdToComponent.put(Utility.getComponentString(realTaxaInSubTree), this.taxaPartitionNodes[i]);
            this.components.add(this.taxaPartitionNodes[i]);
            this.taxaPartitionNodes[i].label = taxa[i].label;
        }

        this.sentinel = new Component(true);
        boolean[] realTaxaInSubTree = new boolean[taxa.length];
        this.realTaxaInComponent.put(this.sentinel, realTaxaInSubTree);
        this.stringIdToComponent.put(Utility.getComponentString(realTaxaInSubTree), this.sentinel);
        this.sentinel.label = "";

        count = taxa.length;

    }

    public Component getSentinel(){
        return this.sentinel;
    }

    public Component getPartitionNode(RealTaxon taxon){
        return this.taxaPartitionNodes[taxon.id];
    }

    private void markChildsForGain(Component p){
        if(p.isLeaf){
            return;
        }
        for(Component child: p.children){
            if(!child.gainPartition){
                child.gainPartition = true;
                markChildsForGain(child);
            }
        }
    }

    public Component addComponent(ArrayList<Component> childs, boolean forGain){
        boolean[] b = new boolean[this.taxa.length];
        for(Component child: childs){
            boolean[] realTaxaInSubTree = this.realTaxaInComponent.get(child);
            for(int i = 0; i < this.taxa.length; ++i){
                b[i] = b[i] || realTaxaInSubTree[i];
            }
        }

        String partitionString = Utility.getComponentString(b);
        if(this.stringIdToComponent.containsKey(partitionString)){
            // System.out.println("-------here-----------");
            Component node = this.stringIdToComponent.get(partitionString);
            if(forGain){
                node.gainPartition = true;
                markChildsForGain(node);
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
            Component partitionNode = new Component(false);
            for(Component child: childs){
                if(child != sentinel){
                    partitionNode.addChild(child);
                    child.addParent(partitionNode);
                }
            }
            this.realTaxaInComponent.put(partitionNode, b);
            this.stringIdToComponent.put(partitionString, partitionNode);
            this.components.add(partitionNode);
            if(forGain){
                partitionNode.onlyGainPartition = true;
                partitionNode.gainPartition = true;
                markChildsForGain(partitionNode);
            }
            count += 1;
            return partitionNode;
        }
        
    }

    public void removeOnlyGainPartitionsFromParent(){
        for(Component partitionNode: this.components){
            ArrayList<Component> filteredParents = new ArrayList<>();
            for(Component parent: partitionNode.parents){
                if(!parent.onlyGainPartition){
                    filteredParents.add(parent);
                }
            }
            partitionNode.parents = filteredParents;
        }
    }

    public ArrayList<Component> getTopSortedNodes(){
        ArrayList<Component> topSortedNodes = new ArrayList<>();
        
        Queue<Component> q = new java.util.LinkedList<>();
        Map<Component, Integer> inDegree = new HashMap<>();
        
        for(Component partitionNode: this.components){
            if(partitionNode.parents.size() == 0){
                q.add(partitionNode);
            }
            else{
                inDegree.put(partitionNode, partitionNode.parents.size());
            }
        }

        while(!q.isEmpty()){
            Component partitionNode = q.poll();
            topSortedNodes.add(partitionNode);
            for(Component child: partitionNode.children){
                inDegree.put(child, inDegree.get(child) - 1);
                if(inDegree.get(child) == 0){
                    q.add(child);
                }
            }
        }
        


        return topSortedNodes;
    }




}
