package src.Tree;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import src.Quartets.QuartestsList;
import src.Quartets.Quartet;
import src.Taxon.RealTaxon;
import src.Utility.Pair;

// Leafs are real taxon

public class Tree {
    
    public ArrayList<TreeNode> nodes;
    public ArrayList<TreeNode> topSortedNodes;

    public TreeNode root;
    public Map<String, RealTaxon> taxaMap;
    public Set<Integer> taxaInTreeIds;
    // in order of id
    // public TreeNode[] leaves;
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

        for(var x : nodes){
            if(x.isLeaf()){
                this.taxaInTreeIds.add(x.taxon.id);
            }
        }
    
        root = nodes.lastElement();
        
        if(root.childs.size() > 2){
            // System.out.println(newickLine);
            // System.out.println("laksjdfljkd");
            balanceRoot();
        }
        
        
        // filterLeaves();
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

    // private void filterLeaves(){
    //     this.leaves = new TreeNode[this.taxaMap.size()];
    //     for(var x : nodes){
    //         if(x.isLeaf()){
    //             this.leaves[x.taxon.id] = x;
    //         }
    //     }
    // }

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
        this.taxaInTreeIds = new HashSet<>();
        parseFromNewick(newickLine);
    }

    public Tree(){
        taxaMap = null;
        this.taxaInTreeIds = new HashSet<>();
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
        return this.taxaInTreeIds.contains(id);
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

    public boolean[] tagUtil(TreeNode node){
        
        boolean[] taxaInSubtree = new boolean[this.taxaMap.size()];

        if(node.isLeaf()){
            taxaInSubtree[node.taxon.id] = true;
            return taxaInSubtree;
        }
        for(var child : node.childs){
            var childTaxa = tagUtil(child);
            for(int i = 0; i < this.taxaMap.size(); ++i){
                if(childTaxa[i]){
                    if(taxaInSubtree[i]){
                        node.dupplicationNode = true;
                    }
                    taxaInSubtree[i] = true;
                }
            }
        }
        return taxaInSubtree;

    }

    public void tag(){
        tagUtil(root);
    }


    private ArrayList<Pair<Integer, Integer>> getPairs(boolean[] c){
        ArrayList<Pair<Integer, Integer>> pairs = new ArrayList<>();
        for(int i = 0; i < c.length; ++i){
            for(int j = i + 1; j < c.length; ++j){
                if(c[i] && c[j]){
                    pairs.add(new Pair<>(i, j));
                }
            }
        }
        return pairs;
    }

    private ArrayList<Pair<Integer, Integer>> getCrossPairs(boolean[] c1, boolean[] c2){
        ArrayList<Pair<Integer, Integer>> pairs = new ArrayList<>();
        boolean[][] pairSet = new boolean[c1.length][c2.length];

        for(int i = 0; i < c1.length; ++i){
            for(int j = 0; j < c2.length; ++j){
                if(!c1[i] || !c2[j]  || i == j)
                    continue;

                int mn = Math.min(i, j);
                int mx = Math.max(i, j);

                if(!pairSet[mn][mx]){
                    pairs.add(new Pair<>(mn, mx));
                    pairSet[mn][mx] = true;
                }
                
            }
        }
        return pairs;
    }

    public QuartestsList qList;
    
    private void generateQuartetsFromPairs(ArrayList<Pair<Integer, Integer>> pairs0,ArrayList<Pair<Integer, Integer>> pairs1, Set<String> qSet){
        for(var p0 : pairs0){
            for(var p1 : pairs1){

                Set<Integer> s = new HashSet<>();
                s.add(p0.first);
                s.add(p0.second);
                s.add(p1.first);
                s.add(p1.second);
                if(s.size() == 4){
                    Quartet q = new Quartet(p0.first, p0.second, p1.first, p1.second);
                    if(!qSet.contains(q.toString())){
                        qSet.add(q.toString());
                        qList.addQuartet(p0.first, p0.second, p1.first, p1.second);
                    }
                }
            }
        }
    }


    private boolean[] getParents(TreeNode node){
        boolean[] parents = new boolean[this.taxaMap.size()];
        while(node.parent != null){
            if(!node.dupplicationNode){
                var otherChild = node.parent.childs.get(0) == node ? node.parent.childs.get(1) : node.parent.childs.get(0);
                for(int i = 0; i < this.taxaMap.size(); ++i){
                    parents[i] |= otherChild.realTaxaInSubtree[i];
                }
            }
            node = node.parent;
        }
        return parents;
    }

    private void generateQuartetsUtil(TreeNode node){


        if(node.isLeaf()){
            return;
        }

        if(!node.dupplicationNode){

            Set<String> qSet = new HashSet<>();

            var c0 = node.childs.get(0).realTaxaInSubtree;
            var c1 = node.childs.get(1).realTaxaInSubtree;
    
    
            var pairsc0 = getPairs(c0);
            var pairsc1 = getPairs(c1);
            
            generateQuartetsFromPairs(pairsc0, pairsc1, qSet);
            
    
            if(node.parent != null ){
                var parent = getParents(node);
                var pairsparentc0 = getCrossPairs(parent, c0);
                var pairsparentc1 = getCrossPairs(parent, c1);
        
                generateQuartetsFromPairs(pairsc0, pairsparentc1, qSet);
        
                generateQuartetsFromPairs(pairsc1, pairsparentc0, qSet);
    
            }
        }

        for(var x : node.childs){
            generateQuartetsUtil(x);
        }
    } 

    public void generateQuartets(QuartestsList quartestsList){

        int[] taxaInTree = new int[this.taxaMap.size()];
        for(var node : topSortedNodes){
            if(node.isLeaf()){
                taxaInTree[node.taxon.id] += 1;
            }
        }

        // for(int i = 0; i < this.taxaMap.size(); ++i){
        //     System.out.println(i + " : " + taxaInTree[i]);
        // }

        this.qList = quartestsList;

        calcRealTaxaInSubTree(root);

        generateQuartetsUtil(root);



    }

    private boolean[] calcRealTaxaInSubTree(TreeNode node){

        boolean[] taxaInSubtree = new boolean[this.taxaMap.size()];

        if(node.isLeaf()){
            taxaInSubtree[node.taxon.id] = true;
            return taxaInSubtree;
        }
        
        boolean[] c0 = calcRealTaxaInSubTree(node.childs.get(0));
        boolean[] c1 = calcRealTaxaInSubTree(node.childs.get(1));

        for(int i = 0; i < this.taxaMap.size(); ++i){
            taxaInSubtree[i] = c0[i] || c1[i];
        }

        node.realTaxaInSubtree = taxaInSubtree;

        return taxaInSubtree;
    } 
    

}
