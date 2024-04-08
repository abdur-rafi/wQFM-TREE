package src.PreProcessing;

import java.util.ArrayList;
import java.util.Collections;

public class Component{

    public static class InternalNodeWithIndex{
        public InternalNode internalNode;
        public int index;
        public enum Method{
            COMMON,
            UNIQUE,
            PARENT
        }

        public Method method;
        
        public InternalNodeWithIndex(InternalNode a, int b, Method method){
            this.internalNode = a;
            this.index = b;
            this.method = method;
        }
    }


    public ArrayList<Component> parents;
    public ArrayList<Component> children;

    public ArrayList<InternalNodeWithIndex> partOfInternalNodes;
    
    public boolean isLeaf;
    public String label;



    public Data data;

    // public boolean gainPartition;
    // public boolean onlyGainPartition;
    public double[] gainsForSubTree;

    public int nodeCount;

    // public PartitionNode(ArrayList<PartitionNode> parents, ArrayList<PartitionNode> children, boolean isLeaf, Data data){
    //     this.parents = parents;
    //     this.children = children;
    //     this.isLeaf = isLeaf;
    //     this.data = data;
        
    // }

    public Component(boolean isLeaf){
        this.parents = new ArrayList<Component>();
        this.children = new ArrayList<Component>();
        this.isLeaf = isLeaf;
        this.partOfInternalNodes = new ArrayList<>();
        this.data = null;
        // this.gainPartition = false;
        // this.onlyGainPartition = false;
        this.nodeCount = 0;
        
    }
    
    public void addChild(Component child){
        this.children.add(child);
    }

    public void addParent(Component parent){
        this.parents.add(parent);
    }

    public void addInternalNode(InternalNode p, int index, InternalNodeWithIndex.Method method){
        this.partOfInternalNodes.add(new InternalNodeWithIndex(p, index, method));
    }


    @Override
    public String toString(){
        ArrayList<String> members = this.members();
        Collections.sort(members);

        StringBuilder sb = new StringBuilder();
        for(String s: members){
            sb.append(s);
            sb.append(",");
        }
        return sb.toString();

    }

    public ArrayList<String> members(){
        ArrayList<String> members = new ArrayList<>();
        if(this.isLeaf){
            members.add(this.label);
        }
        else{
            for(Component child: this.children){
                members.addAll(child.members());
            }
        }
        return members;
    }
}