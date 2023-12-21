package src.Tree;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.Stack;

import src.Taxon.RealTaxon;

// Leafs are real taxon

public class Tree {
    
    public ArrayList<TreeNode> nodes;
    public ArrayList<TreeNode> topSortedNodes;

    public TreeNode root;
    public Map<String, RealTaxon> taxaMap;
    // in order of id
    public TreeNode[] leaves;
    // leavesCount and size of leaves array may be different
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

        // Map<String, RealTaxon> taxaMap = new HashMap<>();
        int leavesCount = 0;

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
                        // if(this.taxaMap != null){
                            taxon = this.taxaMap.get(taxa.toString());
                        // }
                        // else{
                        //     taxon = new RealTaxon(taxa.toString());
                        //     taxaMap.put(taxon.label, taxon);
                        // }   
                        newNode = addLeaf(taxon);
                        leavesCount++;

                        break;
                    }
                    taxa.append(curr_j);
                    ++j;
                }
                if(j == n){
                    leavesCount++;
                    RealTaxon taxon;
                    // if(this.taxaMap != null){
                        taxon = this.taxaMap.get(taxa.toString());
                    // }
                    // else{
                    //     taxon = new RealTaxon(taxa.toString());
                    //     taxaMap.put(taxon.label, taxon);
                    // }   
                    newNode = addLeaf(taxon);
                }
                i = j - 1;
                nodes.push(newNode);
            }
            ++i;
        }

        // if(this.taxaMap == null)
        //     this.taxaMap = taxaMap;

        // this.leavesCount = this.taxaMap.size();
        this.leavesCount = leavesCount;
    
        root = nodes.lastElement();
    
        if(root.childs.size() > 2)
            balanceRoot();
        
        
        filterLeaves();
        topSort();

        // System.out.println(this.leavesCount);
        // for( i = 0; i < this.leaves.length; ++i){
        //     if(this.leaves[i] == null){
        //         System.out.println("Error: Taxon " + i + " is not present in tree");
        //         System.exit(-1);
        //     }
        //     if(this.leaves[i].taxon.id != i){
        //         System.out.println("Error: Taxon " + i + " not matching");
        //         System.exit(-1);
        //     }
        // }
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

    public ArrayList<Integer> resolveNonBinaryUtil(TreeNode node, double[][] distanceMatrix){
        if(node.isLeaf()){
            return new ArrayList<>(Arrays.asList(node.taxon.id));
        }
        var reachableFromChilds = new ArrayList<ArrayList<Integer>>();
        for(var x : node.childs){
            reachableFromChilds.add(resolveNonBinaryUtil(x, distanceMatrix));
        }
        var allReachableFromChilds = new ArrayList<Integer>();
        for(var x : reachableFromChilds){
            allReachableFromChilds.addAll(x);
        }

        if(node.childs.size() > 2){
            double mxDist = Double.MIN_VALUE;
            int mxIndex = -1;
            for(int i = 0; i < node.childs.size(); ++i){
                double currDist = 0;
                for(var a : allReachableFromChilds){
                    for(var b : reachableFromChilds.get(i)){
                        currDist += distanceMatrix[a][b];
                    }
                }
                if(currDist > mxDist){
                    mxDist = currDist;
                    mxIndex = i;
                }
            }

            var branchI = node.childs.get(mxIndex);
            node.childs.remove(mxIndex);
            
            var newNode = addInternalNode(node.childs);
            newNode.setParent(node);
            node.childs = new ArrayList<>();
            node.childs.add(branchI);
            node.childs.add(newNode);
            resolveNonBinaryUtil(newNode, distanceMatrix);

        }
        return allReachableFromChilds;
    }

    public void resolveNonBinary(double[][] distanceMatrix){
        // System.out.println(root.childs.size());
        resolveNonBinaryUtil(root, distanceMatrix);
        topSort();
    }

    
    
    // public Tree(String newickLine){
    //     taxaMap = null;
    //     parseFromNewick(newickLine);
    // }

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

    public boolean isTaxonPresent(int id){
        return this.leaves[id] != null;
    }
    

    private ArrayList<Integer> getChildrens(TreeNode node, Map<String, TreeNode> triPartitionsMap){
        if(node.isLeaf()){
            ArrayList<Integer> arr = new ArrayList<>();
            arr.add(node.taxon.id);
            return arr;
        }
        ArrayList<ArrayList<Integer>> reachableFromChilds = new ArrayList<>();
        for(var child : node.childs){
            reachableFromChilds.add(getChildrens(child, triPartitionsMap));
        }

        // flag all elems in left and right partition to find elems of third partition
        boolean[] mark = new boolean[this.taxaMap.size()];

        for(var childTaxa : reachableFromChilds){
            for(var x : childTaxa){
                mark[x] = true;
            }
        }
        
        ArrayList<Integer> arr = new ArrayList<>();
        for(int i = 0; i < this.taxaMap.size(); ++i){
            if(!mark[i] && isTaxonPresent(i)){
                arr.add(i);
            }
            // else if(!isTaxonPresent(i)){
            //     System.out.println("No. " + i + " is not present in tree");
            // }
        }

        reachableFromChilds.add(arr);

        String[] partitionStrings = new String[reachableFromChilds.size()];

        for(var x : reachableFromChilds){
            x.sort((a, b) -> a - b);
        }


        for(int i = 0; i < reachableFromChilds.size(); ++i){
            var sb = new StringBuilder();
            reachableFromChilds.get(i).forEach(s -> sb.append(s + '-') );
            partitionStrings[i] = sb.toString();
        }
        
        Arrays.sort(partitionStrings);
        // String key;
        StringBuilder sb = new StringBuilder();
        for(var x : partitionStrings){
            sb.append(x + '|');
        }

        String key = sb.toString();

        if(triPartitionsMap.containsKey(key)){
            triPartitionsMap.get(key).frequency++;
            node.frequency = 0;
        }
        else{
            node.frequency = 1;
            triPartitionsMap.put(key, node);
        }
        reachableFromChilds.remove(reachableFromChilds.size() - 1);
        
        arr = new ArrayList<>();
        for(var childTaxa : reachableFromChilds){
            arr.addAll(childTaxa);
        }
        return arr;
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

    public boolean checkIfNonBinary(){
        for(var x : nodes){
            if( x.childs != null && x.childs.size() > 2)
                return true;
        }
        return false;
    }

}
