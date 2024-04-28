package src.PreProcessing;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

import src.Config;
import src.Taxon.RealTaxon;
import src.Tree.Tree;
import src.Tree.TreeNode;


public class GeneTrees {

    public ArrayList<Tree> geneTrees;
    public String[] taxonIdToLabel;
    public RealTaxon[] taxa;
    public Map<String, TreeNode> triPartitions;
    public Map<String, RealTaxon> taxaMap;
    public int realTaxaCount;
    public String path;


    private void parseTaxa(String newickLine, Set<String> taxaSet){
        newickLine.replaceAll("\\s", "");
    
        int n =  newickLine.length();
    
        int i = 0, j = 0;
    
        while(i < n){
            char curr = newickLine.charAt(i);
            if(curr == '('){
            }
            else if(curr == ')'){
            }
            else if(curr == ',' || curr == ';'){
    
            }
            else{
                StringBuilder taxa = new StringBuilder();
                j = i;
                while(j < n){
                    char curr_j = newickLine.charAt(j);
                    if(curr_j == ')' || curr_j == ','){
                        String label = taxa.toString();
                        taxaSet.add(label);
                        break;
                    }
                    taxa.append(curr_j);
                    ++j;
                }
                if(j == n){
                    String label = taxa.toString();
                    taxaSet.add(label);
                }
                i = j - 1;
            }
            ++i;
        }
    }
    

    public Map<String, RealTaxon> readTaxaNames() throws FileNotFoundException{
        Set<String> taxaSet = new HashSet<>();

        Scanner scanner = new Scanner(new File(this.path));
        while(scanner.hasNextLine()){
            String line = scanner.nextLine();
            if(line.trim().length() == 0) continue;
            parseTaxa(line, taxaSet);
        }
        scanner.close();

        
        this.taxaMap = new HashMap<>();
        for(var x : taxaSet){
            RealTaxon taxon = new RealTaxon(x);
            taxaMap.put(x, taxon);
        }


        this.taxonIdToLabel = new String[this.taxaMap.size()];
        this.taxa = new RealTaxon[this.taxaMap.size()];
        this.realTaxaCount = this.taxaMap.size();

        for(var x : this.taxaMap.entrySet()){
            taxonIdToLabel[x.getValue().id] = x.getKey();
            taxa[x.getValue().id] = x.getValue();
        }

        return taxaMap;
    }

    public void readGeneTrees(double[][] distanceMatrix) throws FileNotFoundException{
        int internalNodesCount = 0;
        

        Scanner scanner = new Scanner(new File(path));

        while (scanner.hasNextLine()) {

            String line = scanner.nextLine();
            // System.out.println(line);
            if(line.trim().length() == 0) continue;
            
            var tree = new Tree(line, this.taxaMap);
            
            if(Config.RESOLVE_POLYTOMY){
                tree.resolveNonBinary(distanceMatrix);
            }

            // System.out.println(tree.root);
            
            // if(tree.checkIfNonBinary()){
            //     continue;
            // }

            tree.calculateFrequencies(triPartitions);
            tree.tag();
            geneTrees.add(tree);


            if(tree.checkIfNonBinary()){
                System.out.println("============ Non binary gt ===================");
                // continue;
            }
            // for(var x : tree.nodes){
            //     x.frequency = 1;
            // }
            // System.out.println(tree.leavesCount);
            internalNodesCount += tree.nodes.size() - tree.leavesCount;
            // if(tree.checkIfNonBinary()){
            //     break;
            //     // System.out.println("non binary tree");
            //     // continue;
            // }
            // System.out.println(tree.root);
            

        }
        
        scanner.close();
        
        


        System.out.println( "taxon count : " + this.taxaMap.size());
        System.out.println("Gene trees count : " + geneTrees.size());
        // System.out.println( "total internal nodes : " + internalNodesCount);
        // System.out.println( "unique partitions : " + triPartitions.size());


        // PartitionGraph partitionGraph = createPartitionGraph();
        // PartitionsByTreeNode partitions = createPartitions(partitionGraph.realTaxaInPartition);


        // System.out.println("Partition graph created");
        // System.out.println("Partition graph nodes count : " + partitionGraph.count);

        // System.out.println("Partitions created");
        // System.out.println("Partitions count : " + partitions.getPartitionCount());
        
        // DataContainer dataContainer = new DataContainer();
        
        // dataContainer.partitionsByTreeNodes = partitions.partitions;
        // dataContainer.topSortedPartitionNodes = partitionGraph.getTopSortedNodes();
        // dataContainer.realTaxaPartitionNodes = partitionGraph.taxaPartitionNodes;
        // dataContainer.realTaxaInTrees = new boolean[geneTrees.size()][];
        // for(int i = 0; i < geneTrees.size(); ++i){
        //     dataContainer.realTaxaInTrees[i] = new boolean[this.realTaxaCount];
        //     for(int j = 0; j < this.realTaxaCount; ++j){
        //         dataContainer.realTaxaInTrees[i][j] = geneTrees.get(i).leaves[j] != null;
        //     }
        // }
        // dataContainer.taxa = this.taxa;

        // return dataContainer;
        
        // if(internalNodesCount == 50000){
        //     System.out.println("No polytomy, skipping");
        //     System.exit(-1);
        // }

    }

    public DataContainer createDateContainer(){
        ComponentGraph compGraph = createPartitionGraph();
        InternalNodes internalNodes = createPartitionsByTreeNode(compGraph);


        // System.out.println("Partition graph created");
        // System.out.println("Partition graph nodes count : " + partitionGraph.count);

        // System.out.println("Partitions created");
        // System.out.println("Partitions count : " + partitions.getPartitionCount());
        
        DataContainer dataContainer = new DataContainer();
        
        dataContainer.internalNodes = internalNodes.nodes;
        dataContainer.topSortedComponents = compGraph.getTopSortedNodes();

        // System.out.println("================ comps ===================");
        // for(var x : compGraph.components){
        //     System.out.println(x);
        // }
        // System.out.println("================ top sorted comps ===================");
        // for(var x : dataContainer.topSortedComponents){
        //     System.out.println(x);
        // }

        // ArrayList<PartitionNode> topSortedPartitionNodes = partitionGraph.getTopSortedNodes();
        // partitionGraph.removeOnlyGainPartitionsFromParent();
        
        // dataContainer.topSortedForBranch = new ArrayList<>();
        // dataContainer.topSortedForGain = new ArrayList<>();

        // for(PartitionNode node : topSortedPartitionNodes){
        //     if(node.gainPartition){
        //         dataContainer.topSortedForGain.add(node);
        //     }
        //     if(!node.onlyGainPartition){
        //         dataContainer.topSortedForBranch.add(node);
        //     }
        // }

        dataContainer.realTaxaComponents = compGraph.taxaPartitionNodes;
        dataContainer.realTaxaInTrees = new boolean[geneTrees.size()][];
        for(int i = 0; i < geneTrees.size(); ++i){
            dataContainer.realTaxaInTrees[i] = new boolean[this.realTaxaCount];
            for(int j = 0; j < this.realTaxaCount; ++j){
                dataContainer.realTaxaInTrees[i][j] = geneTrees.get(i).isTaxonPresent(i);
            }
        }
        dataContainer.taxa = this.taxa;
        dataContainer.sentinel = compGraph.getSentinel();

        System.out.println("Comp graph nodes count : " + compGraph.count);
        System.out.println("Internal nodes count : " + internalNodes.nodes.size());
        // System.out.println("Partition Graph Branch nodes : " + dataContainer.topSortedForBranch.size());
        // System.out.println("Partition Graph Gain nodes : " + dataContainer.topSortedForGain.size());

        // dataContainer.partitionGraph = partitionGraph;

        return dataContainer;
    }

    void printPartition(boolean[] b){
        StringBuffer sb = new StringBuffer();
        for(int i = 0; i < this.taxa.length; ++i){
            if(b[i]){
                sb.append(this.taxa[i].label);
                sb.append(",");
            }
        }
        System.out.println(sb.toString());

    }

    // public PartitionByTreeNode createPartitionByTreeNodeUtil(TreeNode node, int[] freq){

    // }

    public InternalNodes createPartitionsByTreeNode(ComponentGraph componentGraph){
        InternalNodes internalNodes = new InternalNodes(componentGraph.realTaxaInComponent);
        int treeIndex = 0;
        for(Tree tree : geneTrees){
            int[] fre = new int[this.taxaMap.size()];
            for(TreeNode node : tree.nodes){
                if(node.isLeaf()){
                    fre[node.taxon.id]++;
                }
            }
            for(TreeNode node : tree.topSortedNodes){
                if(node.isLeaf()){
                    node.commonWithParent = componentGraph.getSentinel();
                    node.distinctWithParent = componentGraph.getSentinel();

                    // node.childComponents = new Component[2];
                    // node.childComponents[0] = componentGraph.getSentinel();
                    // node.childComponents[1] = componentGraph.getSentinel();
                    // int index = 0;
                    if(fre[node.taxon.id] == 1){
                        // index = 1;
                        node.distinctWithParent = componentGraph.taxaPartitionNodes[node.taxon.id];
                    }
                    else{
                        node.commonWithParent = componentGraph.taxaPartitionNodes[node.taxon.id];
                    }
                    // node.childComponents[index] = componentGraph.taxaPartitionNodes[node.taxon.id];
                }
                else{
                    // node.childComponents = new Component[2];
                    ArrayList<Component> gainNodesCommon = new ArrayList<>();
                    ArrayList<Component> gainNodesDistinct = new ArrayList<>();
                    for(TreeNode child : node.childs){
                        gainNodesCommon.add(child.commonWithParent);
                        gainNodesDistinct.add(child.distinctWithParent);
                    }
                    node.commonWithParent = componentGraph.addComponent(gainNodesCommon, true);
                    node.distinctWithParent = componentGraph.addComponent(gainNodesDistinct, true);
                
                }
            }
            tree.root.childs.get(0).parentDistinct = tree.root.childs.get(1).distinctWithParent;
            tree.root.childs.get(1).parentDistinct = tree.root.childs.get(0).distinctWithParent;

            for(int i = tree.topSortedNodes.size() - 2; i > -1; --i){
                TreeNode node = tree.topSortedNodes.get(i);
                // node.parentDistinct = componentGraph.getSentinel();
                
                if(node.isLeaf()) continue;

                if(node.parent != tree.root){

                    boolean[] commonInSubtree = componentGraph.realTaxaInComponent.get(node.commonWithParent);
                    boolean[] distinctInSubtree = componentGraph.realTaxaInComponent.get(node.distinctWithParent);
                    boolean[] realTaxaInSubTree = new boolean[commonInSubtree.length];
                    for(int j = 0; j < commonInSubtree.length; ++j){
                        realTaxaInSubTree[j] = commonInSubtree[j] || distinctInSubtree[j];
                    }
                    

                    ArrayList<Component> ps = new ArrayList<>();
                    for(TreeNode child : node.parent.childs){
                        if(child == node) continue;
                        
                        ArrayList<Component> childComps = new ArrayList<>();
                        childComps.add(child.distinctWithParent);
                        childComps.add(child.commonWithParent);

                        Component merged = componentGraph.addComponent(childComps, true);
                        
                        if(node.parent.dupplicationNode){
                            
                            boolean[] realTaxaInMerged = componentGraph.realTaxaInComponent.get(merged);
    
                            Set<Integer> st = new HashSet<>();
    
                            for(int j = 0; j < this.realTaxaCount; ++j){
                                if(realTaxaInMerged[j] && realTaxaInSubTree[j]){
                                    st.add(j);
                                }
                            }

                            merged = componentGraph.removeTaxa(merged, st);
                        }


                        // ps.add(child.distinctWithParent);
                        // ps.add(child.commonWithParent);
                        ps.add(merged);
                    }

                    ps.add(node.parent.parentDistinct);
                    node.parentDistinct = componentGraph.addComponent(ps, true);
                }

                if(!node.dupplicationNode){
                    
                    // Component[][] gainNodes = new Component[node.childs.size()][];
                    // Component[] p = new Component[node.childs.size() + 1];
                    // int k = 0;
                    // for(TreeNode child : node.childs){
                    //     p[k] = child.subTreeComponent;
                    //     gainNodes[k++] = child.childComponents;
                    // }
                    // p[k] = node.parentComponent;

                    Component[] childCompsCommon = new Component[node.childs.size()];
                    Component[] childCompsUniques = new Component[node.childs.size()];
                    for(int j = 0; j < node.childs.size(); ++j){
                        childCompsCommon[j] = node.childs.get(j).commonWithParent;
                        childCompsUniques[j] = node.childs.get(j).distinctWithParent;
                    }
                    

                    
                    internalNodes.addInternalNode(childCompsCommon, childCompsUniques, node.parentDistinct, !node.parent.dupplicationNode);
                    // internalNodes.addPartitionByTreeNode(p, gainNodes, node.gainParentNode);
                }

                // System.out.println("node index : " + node.index + " tree index : " + treeIndex);
                // System.out.println("common with parent: " + node.commonWithParent);
                // System.out.println( "distict from parent: " + node.distinctWithParent);
                // System.out.println("parent distincts : " + node.parentDistinct);



            }

            if(!tree.root.dupplicationNode){
                
                TreeNode node = tree.root;
                // Component[] ps = new Component[rootNode.childs.size() + 1];
                // Component[][] gainNodes = new Component[rootNode.childs.size()][];
                // int k = 0;
                // for(TreeNode child : rootNode.childs){
                //     ps[k] = child.subTreeComponent;
                //     gainNodes[k++] = child.childComponents;
                // }
                // ps[k] = componentGraph.getSentinel();

                Component[] childCompsCommon = new Component[node.childs.size()];
                Component[] childCompsUniques = new Component[node.childs.size()];
                for(int j = 0; j < node.childs.size(); ++j){
                    childCompsCommon[j] = node.childs.get(j).commonWithParent;
                    childCompsUniques[j] = node.childs.get(j).distinctWithParent;
                }
                internalNodes.addInternalNode(childCompsCommon, childCompsUniques,componentGraph.getSentinel(), false);
                // partitions.addPartitionByTreeNode(ps, gainNodes,partitionGraph.getSentinel());
            }
            treeIndex++;

        }

        return internalNodes;
    }

    public ComponentGraph createPartitionGraph(){
        ComponentGraph partitionGraph = new ComponentGraph(this.taxa);
        for(Tree tree : geneTrees){
            // for(TreeNode node : tree.nodes){
            //     if(node.isLeaf()){
            //         node.subTreeComponent = partitionGraph.getPartitionNode(node.taxon);
            //     }
            // }

            // for (TreeNode node : tree.topSortedNodes) {
            //     if(node.isRoot() || node.isLeaf()) continue;
            //     ArrayList<PartitionNode> childs = new ArrayList<>();
            //     for(TreeNode child : node.childs){
            //         childs.add(child.partitionNode);
            //     }
            //     node.partitionNode = partitionGraph.addPartition(childs, false);
            // }
            // tree.root.childs.get(0).parentPartitionNode = tree.root.childs.get(1).partitionNode;
            // tree.root.childs.get(1).parentPartitionNode = tree.root.childs.get(0).partitionNode;
            
            // int sz = tree.topSortedNodes.size();
            // for(int i = sz - 2; i > -1; --i){
            //     TreeNode node = tree.topSortedNodes.get(i);
            //     if(node.isLeaf() || node.parent == tree.root) continue;

            //     ArrayList<PartitionNode> childs = new ArrayList<>();
            //     for(TreeNode child : node.parent.childs){
            //         if(child == node) continue;
            //         childs.add(child.partitionNode);
            //     }
            //     childs.add(node.parent.parentPartitionNode);
            //     node.parentPartitionNode = partitionGraph.addPartition(childs, false);
            // }
        }

        return partitionGraph;
        
    }

    public GeneTrees(String path) throws FileNotFoundException{

        this.geneTrees = new ArrayList<>();
        this.triPartitions = new HashMap<>();
        this.path = path;
    }


    public GeneTrees(String path, Map<String, RealTaxon> taxaMap) throws FileNotFoundException{

        this.geneTrees = new ArrayList<>();
        this.triPartitions = new HashMap<>();
        this.path = path;
        this.taxaMap = taxaMap;

        this.readGeneTrees(null);
    }
    
} 


