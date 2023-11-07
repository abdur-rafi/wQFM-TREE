package src.v2.Tree;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import src.v2.Taxon.RealTaxon;

// Leafs are real taxon

public class Tree {
    
    public ArrayList<TreeNode> nodes;
    public ArrayList<TreeNode> topSortedNodes;

    public TreeNode root;
    public Map<String, RealTaxon> taxaMap;
    // in order of id
    public TreeNode[] leaves;
    public int leavesCount;
    

    public TreeNode addNode(ArrayList<TreeNode> children, TreeNode parent){

        TreeNode nd = new TreeNode().setIndex(nodes.size()).setChilds(children).setParent(parent);
        nodes.add(nd);
        return nd;
    }


    public TreeNode addInternalNode(ArrayList<TreeNode> children){
        var nd = addNode(children, null);
        for (var x : children)
            x.setParent(nd);
        return nd;
    }

    public TreeNode addLeaf(RealTaxon taxon){
        var nd = addNode(null, null).setTaxon(taxon);
        return nd;
    }


    private void parseFromNewick(String newickLine){

        Map<String, RealTaxon> taxaMap = new HashMap<>();

        nodes = new ArrayList<>();
    
        Stack<TreeNode> nodes = new Stack<>();
        newickLine.replaceAll("\\s", "");
    
        int n =  newickLine.length();
    
        int i = 0, j = 0;
    
        while(i < n){
            char curr = newickLine.charAt(i);
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
                    char curr_j = newickLine.charAt(j);
                    if(curr_j == ')' || curr_j == ','){
                        RealTaxon taxon;
                        if(this.taxaMap != null){
                            taxon = this.taxaMap.get(taxa.toString());
                        }
                        else{
                            taxon = new RealTaxon(taxa.toString());
                            taxaMap.put(taxon.label, taxon);
                        }   
                        newNode = addLeaf(taxon);
                        break;
                    }
                    taxa.append(curr_j);
                    ++j;
                }
                if(j == n){
                    RealTaxon taxon;
                    if(this.taxaMap != null){
                        taxon = this.taxaMap.get(taxa.toString());
                    }
                    else{
                        taxon = new RealTaxon(taxa.toString());
                        taxaMap.put(taxon.label, taxon);
                    }   
                    newNode = addLeaf(taxon);
                }
                i = j - 1;
                nodes.push(newNode);
            }
            ++i;
        }

        if(this.taxaMap == null)
            this.taxaMap = taxaMap;

        this.leavesCount = this.taxaMap.size();
    
        root = nodes.lastElement();
    
        if(root.childs.size() > 2)
            balanceRoot();
        
        
        filterLeaves();
        topSort();
        // bringLeafsToFront();



    }

    private void filterLeaves(){
        this.leaves = new TreeNode[this.taxaMap.size()];
        for(var x : nodes){
            if(x.isLeaf()){
                this.leaves[x.taxon.id] = x;
            }
        }
    }

    
    
    public Tree(String newickLine){
        taxaMap = null;
        parseFromNewick(newickLine);
    }

    public Tree(String newickLine, Map<String, RealTaxon> taxaMap){
        this.taxaMap = taxaMap;
        parseFromNewick(newickLine);
    }

    public Tree(){
        taxaMap = null;
        nodes = new ArrayList<>();
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

    // private void bringLeafsToFront(){
    //     for(int i = 0; i < nodes.size(); ++i){
    //         if(nodes.get(i).isLeaf()){

    //             var curr = nodes.get(i);
    //             var tmp = nodes.get(curr.taxon.id);
                
    //             nodes.set(curr.taxon.id, curr);
    //             nodes.set(i, tmp);
    //             curr.index = curr.taxon.id;
    //             tmp.index = i;
    //         }
    //     }
    // }
    
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
    
    
    private String newickFormatUitl(TreeNode node){
        if(node.isLeaf()){
            return node.taxon.label;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("(");
        for(int i = 0; i < node.childs.size(); ++i){
            sb.append(newickFormatUitl(node.childs.get(i)));
            if(i != node.childs.size() - 1)
                sb.append(",");
        }
        sb.append(")");
        return sb.toString();
    }


    public String getNewickFormat(){
        return newickFormatUitl(root) + ";";
    }
    

    private ArrayList<Integer> getChildrens(TreeNode node, Map<String, TreeNode> triPartitionsMap){
        if(node.isLeaf()){
            ArrayList<Integer> arr = new ArrayList<>();
            arr.add(node.taxon.id);
            return arr;
        }
        var c1 = node.childs.get(0);
        var c2 = node.childs.get(1);
        var arr1 = getChildrens(c1, triPartitionsMap);
        var arr2 = getChildrens(c2, triPartitionsMap);

        // flag all elems in left and right partition to find elems of third partition
        boolean[] mark = new boolean[this.leavesCount];
        for(var x : arr1){
            mark[x] = true;
        }
        for(var x : arr2){
            mark[x] = true;
        }
        ArrayList<Integer> arr3 = new ArrayList<>();
        for(int i = 0; i < this.leavesCount; ++i){
            if(!mark[i]){
                arr3.add(i);
            }
        }

        String[] triPartition = new String[3];

        var sb = new StringBuilder();
        arr1.forEach(s -> sb.append(s + '-') );

        triPartition[0] = sb.toString();
        sb.setLength(0);


        arr2.forEach(s -> sb.append(s + '-') );
        triPartition[1] = sb.toString();
        sb.setLength(0);

        arr3.forEach(s -> sb.append(s + '-') );
        triPartition[2] = sb.toString();
        
        Arrays.sort(triPartition);

        var key = triPartition[0] + '|' + triPartition[1];

        if(triPartitionsMap.containsKey(key)){
            triPartitionsMap.get(key).frequency++;
            node.frequency = 0;
        }
        else{
            node.frequency = 1;
            triPartitionsMap.put(key, node);
        }

        arr1.addAll(arr2);

        return arr1;
    }

    public void calculateFrequencies(Map<String, TreeNode> triPartitions){
        getChildrens(root, triPartitions);
    }

    private void topSortUtil(TreeNode node, ArrayList<TreeNode> topSort){
        if(node.isLeaf()){
        }
        else{
            for(var x : node.childs){
                topSortUtil(x, topSort);
            }
        }
        topSort.add(node);

    }

    public void topSort(){
        ArrayList<TreeNode> topSort = new ArrayList<>();
        topSortUtil(root, topSort);
        this.topSortedNodes = topSort;
    }

}
