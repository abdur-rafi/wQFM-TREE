package src.GeneTree;

import java.util.ArrayList;
import java.util.Iterator;

import src.GeneTree.TreePrinter.PrintableNode;

public class TreeNode implements TreePrinter.PrintableNode {
    public int index;
    public String label;
    public ArrayList<TreeNode> childs;
    public TreeNode parent;
    public Info info;
    public int dummyTaxaId;

    public double weight;

    public TreeNode(int i, String lb, ArrayList<TreeNode> ch, TreeNode pr){
        index = i;
        label = lb;
        childs = ch;
        parent = pr;
        info = null;
        this.dummyTaxaId = -1;
        weight=0;
    }
    public TreeNode(int i, String lb, ArrayList<TreeNode> ch, TreeNode pr, int dtId){
        this(i, lb, ch, pr);
        this.dummyTaxaId = dtId;
    }

    public void setParent(TreeNode pr){
        this.parent = pr;
    }

    @Override
    public PrintableNode getLeft() {
        if (childs == null) return null;
        return childs.get(0);
    }
    @Override
    public PrintableNode getRight() {
        if(childs == null) return null;
        return childs.get(1);
    }
    @Override
    public String getText() {
        // if(label != null){
        //     return label;
        // }
        return Integer.toString(index);
    }


    public String toString() {
        StringBuilder buffer = new StringBuilder(50);
        print(buffer, "", "");
        return buffer.toString();
    }

    private void print(StringBuilder buffer, String prefix, String childrenPrefix) {
        buffer.append(prefix);
        // buffer.append(" i : " + index + " l : " + label);

        if(label == null){
            // if(parent == null)
            //     buffer.append(index);
            // else{
            //     buffer.append( index);

            //     // buffer.append(" index : " + index + " label : " + label);
            //     // var sc = info.calculator.score();
            //     // buffer.append(" sc : " + sc + " ");

            //     // buffer.append(" da : " + info.realTaxaCountTotal[0] + " db : " + info.realTaxaCountTotal[1]);
            //     // for(int i = 0; i < info.dummyTaxaCountIndividual.length; ++ i){
            //     //     buffer.append(i + " : " + info.dummyTaxaCountIndividual[i] + " ");
            //     // }
            // }
            buffer.append(index);
        }
            // buffer.append(index);
            // buffer.append(" ");
        else
                // buffer.append(" a : " + info.reachableDummyTaxaA.size() + " b : " + info.reachableDummyTaxaB.size());

            buffer.append("l : " + label);

        // if(label != null)
        //     buffer.append(label);
        // else
        //     buffer.append(info.pACount + "-" + info.pBCount + "-" + info.abovepACount + "-" + info.abovepBCount);
        buffer.append('\n');
        ArrayList<TreeNode> chld = childs;
        if(chld == null){
            chld = new ArrayList<>();
        }
        for (Iterator<TreeNode> it = chld.iterator(); it.hasNext();) {
            TreeNode next = it.next();
            if (it.hasNext()) {
                next.print(buffer, childrenPrefix + "├── ", childrenPrefix + "│   ");
            } else {
                next.print(buffer, childrenPrefix + "└── ", childrenPrefix + "    ");
            }
        }
    }

    public boolean isLeaf(){
        return childs == null;
    }
    
    public boolean isRoot(){
        return parent == null;
    }
}
