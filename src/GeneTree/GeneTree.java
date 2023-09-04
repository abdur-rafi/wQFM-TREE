package src.GeneTree;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

public class GeneTree {
    


    public ArrayList<TreeNode> nodes;
    public Map<String, Integer> taxaMap;
    public TreeNode root;

    public TreeNode addNode(String label, ArrayList<TreeNode> ch, TreeNode pr){
        TreeNode nd = new TreeNode(nodes.size(), label, ch, pr);
//        System.out.println(label);
        nodes.add(nd);
        return nd;
    }

    public TreeNode addInternalNode(ArrayList<TreeNode> arr){
        var nd = addNode(null, arr, null);
        for (var x : arr)
            x.setParent(nd);
        return nd;
    }

    public TreeNode addTaxa(String taxa){
        var x = addNode(taxa, null, null); 
        taxaMap.put(taxa, x.index);
        return x;
    }

    public TreeNode addRealTaxa(String taxa, TreeNode parent){
        var x = addNode(taxa, null, parent);
        if(parent.childs == null){
            parent.childs = new ArrayList<>();
        }
        parent.childs.add(x);
        return x;
    }

    public TreeNode addDummyNode(int dtId, TreeNode parent){
        var x = addNode(null, null, parent);
        if(parent.childs == null){
            parent.childs = new ArrayList<>();
        }
        parent.childs.add(x);
        x.dummyTaxaId = dtId;

        return x;
    }

    public GeneTree(){
        
        taxaMap = new HashMap<>();
        nodes = new ArrayList<>();
        root = addNode(null, null, null);
    }

    public GeneTree(String line){
        taxaMap = new HashMap<>();
        nodes = new ArrayList<>();

        Stack<TreeNode> nodes = new Stack<>();
        line.replaceAll("\\s", "");

        int n =  line.length();

        int i = 0, j = 0;

        while(i < n){
            char curr = line.charAt(i);
            if(curr == '('){
                nodes.push(null);
            }
            else if(curr == ')'){
                ArrayList<TreeNode> arr = new ArrayList<>();
                while( !nodes.isEmpty() && nodes.peek() != null){
                    arr.add(nodes.pop());
                }
                if(!nodes.isEmpty())
                    nodes.pop();
                nodes.push(addInternalNode(arr));
                
            }
            else if(curr == ',' || curr == ';'){

            }
            else{
                StringBuilder taxa = new StringBuilder();
                j = i;
                TreeNode newNode = null;
                while(j < n){
                    char curr_j = line.charAt(j);
                    if(curr_j == ')' || curr_j == ','){
                        newNode = addTaxa(taxa.toString());
                        break;
                    }
                    taxa.append(curr_j);
                    ++j;
                }
                if(j == n){
                    newNode = addTaxa(taxa.toString());
                }
                i = j - 1;
                nodes.push(newNode);
            }
            ++i;
        }

        root = nodes.lastElement();
        if(root.childs.size() > 2)
            balanceRoot();
        bringLeafsToFront();
    }


    public int dfs(TreeNode node, ArrayList<Integer> subTreeNodeCount){
        if(node.childs == null){
            subTreeNodeCount.set(node.index, 1);
            return 1;
        }
        int res = 0;
        for(var x : node.childs){
            res += dfs(x, subTreeNodeCount);
        }
        subTreeNodeCount.set(node.index, res + 1);
        return res + 1;
    }

    private void bringLeafsToFront(){
        int j = 0;
        for(int i = 0; i < nodes.size(); ++i){
            if(nodes.get(i).isLeaf()){
                var tmp = nodes.get(j);
                var curr = nodes.get(i);
                nodes.set(j, curr);
                nodes.set(i, tmp);
                curr.index = j;
                tmp.index = i;
                taxaMap.put(curr.label, j);
                ++j;

            }
        }
    }
    
    public void reRootTree(TreeNode newRootNode){
        TreeNode newRootP = newRootNode.parent;
        if(newRootP == null) return;
        newRootP.childs.remove(newRootNode);

        TreeNode curr = newRootP;
        TreeNode currP, temp;
        currP = curr.parent;
        while(curr != null && currP != null){
            curr.childs.add(currP);
            currP.childs.remove(curr);
            temp = currP;
            currP = currP.parent;
            temp.parent = curr;
            curr = temp;
            // System.out.println(curr.index);
        }
        if(newRootNode.isLeaf())
            newRootNode.childs = new ArrayList<>();
        newRootNode.childs.add(newRootP);
        this.root = newRootNode;
    }

    private void balanceRoot(){

        int n = nodes.size();
        ArrayList<Integer> subTreeNodeCount = new ArrayList<>(n);
        for(int i = 0; i < n; ++i)
            subTreeNodeCount.add(0);
        dfs(root, subTreeNodeCount);
        
        TreeNode closest = root;
        int diff = n;
        int v;
        for(int i = 0; i < n; ++i){
            v = Math.abs(n/2 - subTreeNodeCount.get(i)); 
            if(v < diff){
                diff = v;
                closest = nodes.get(i);
            }
        }
        // System.out.println("diff : " + diff + " node: " + closest.index);
        TreeNode closestP = closest.parent;
        closestP.childs.remove(closest);

        TreeNode curr = closestP;
        TreeNode currP, temp;
        currP = curr.parent;
        while(curr != null && currP != null){
            curr.childs.add(currP);
            currP.childs.remove(curr);
            temp = currP;
            currP = currP.parent;
            temp.parent = curr;
            curr = temp;
        }

        ArrayList<TreeNode> arr = new ArrayList<>();
        arr.add(closest);
        arr.add(closestP);

        root = addInternalNode(arr) ;
        // root = new TreeNode(nodes.size(),null, arr, null);
        // nodes.add(root);

        // System.out.println(root.toString());

    }
    
    


}
