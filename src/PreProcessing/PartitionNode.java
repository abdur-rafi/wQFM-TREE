package src.PreProcessing;

import java.util.ArrayList;

public class PartitionNode{

    public static class PartitionByTreeNodeWithIndex{
        public PartitionByTreeNode partitionByTreeNode;
        public int index;
        
        public PartitionByTreeNodeWithIndex(PartitionByTreeNode a, int b){
            this.partitionByTreeNode = a;
            this.index = b;
        }
    }

    public ArrayList<PartitionNode> parents;
    public ArrayList<PartitionNode> children;

    public ArrayList<PartitionByTreeNodeWithIndex> nodePartitions;
    
    public boolean isLeaf;

    public Data data;


    // public PartitionNode(ArrayList<PartitionNode> parents, ArrayList<PartitionNode> children, boolean isLeaf, Data data){
    //     this.parents = parents;
    //     this.children = children;
    //     this.isLeaf = isLeaf;
    //     this.data = data;
        
    // }

    public PartitionNode(boolean isLeaf){
        this.parents = new ArrayList<PartitionNode>();
        this.children = new ArrayList<PartitionNode>();
        this.isLeaf = isLeaf;
        this.nodePartitions = new ArrayList<>();
        this.data = null;
    }
    
    public void addChild(PartitionNode child){
        this.children.add(child);
    }

    public void addParent(PartitionNode parent){
        this.parents.add(parent);
    }

    public void addNodePartitions(PartitionByTreeNode p, int index){
        this.nodePartitions.add(new PartitionByTreeNodeWithIndex(p, index));
    }
}