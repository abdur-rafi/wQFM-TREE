package wqfm.dsGT;

import java.util.ArrayList;
import java.util.Iterator;

import wqfm.dsGT.TreePrinter.PrintableNode;

public class TreeNode implements TreePrinter.PrintableNode {
    int index;
    String label;
    ArrayList<TreeNode> childs;
    TreeNode parent;
    Info info;

    public TreeNode(int i, String lb, ArrayList<TreeNode> ch, TreeNode pr){
        index = i;
        label = lb;
        childs = ch;
        parent = pr;
        info = null;
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
        if(label == null){
            if(parent == null)
                buffer.append("root");
            else
                buffer.append(" a : " + info.reachableDummyTaxaFromAboveA.size() + " b : " + info.reachableDummyTaxaFromAboveB.size());
        }
            // buffer.append(index);
            // buffer.append(" ");
        else
                // buffer.append(" a : " + info.reachableDummyTaxaA.size() + " b : " + info.reachableDummyTaxaB.size());

            buffer.append(label);

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
