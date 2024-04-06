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
        System.out.println( "total internal nodes : " + internalNodesCount);
        System.out.println( "unique partitions : " + triPartitions.size());


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
        PartitionGraph partitionGraph = createPartitionGraph();
        PartitionsByTreeNode partitions = createPartitionsByTreeNode(partitionGraph);


        // System.out.println("Partition graph created");
        // System.out.println("Partition graph nodes count : " + partitionGraph.count);

        // System.out.println("Partitions created");
        // System.out.println("Partitions count : " + partitions.getPartitionCount());
        
        DataContainer dataContainer = new DataContainer();
        
        dataContainer.partitionsByTreeNodes = partitions.partitions;
        ArrayList<PartitionNode> topSortedPartitionNodes = partitionGraph.getTopSortedNodes();
        partitionGraph.removeOnlyGainPartitionsFromParent();
        
        dataContainer.topSortedForBranch = new ArrayList<>();
        dataContainer.topSortedForGain = new ArrayList<>();

        for(PartitionNode node : topSortedPartitionNodes){
            if(node.gainPartition){
                dataContainer.topSortedForGain.add(node);
            }
            if(!node.onlyGainPartition){
                dataContainer.topSortedForBranch.add(node);
            }
        }

        dataContainer.realTaxaPartitionNodes = partitionGraph.taxaPartitionNodes;
        dataContainer.realTaxaInTrees = new boolean[geneTrees.size()][];
        for(int i = 0; i < geneTrees.size(); ++i){
            dataContainer.realTaxaInTrees[i] = new boolean[this.realTaxaCount];
            for(int j = 0; j < this.realTaxaCount; ++j){
                dataContainer.realTaxaInTrees[i][j] = geneTrees.get(i).isTaxonPresent(i);
            }
        }
        dataContainer.taxa = this.taxa;
        dataContainer.sentinel = partitionGraph.getSentinel();

        System.out.println("Partition graph nodes count : " + partitionGraph.count);
        System.out.println("Partition Graph Branch nodes : " + dataContainer.topSortedForBranch.size());
        System.out.println("Partition Graph Gain nodes : " + dataContainer.topSortedForGain.size());

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

    public PartitionsByTreeNode createPartitionsByTreeNode(PartitionGraph partitionGraph){
        PartitionsByTreeNode partitions = new PartitionsByTreeNode(partitionGraph.realTaxaInPartition);
        // int j = 0;
        for(Tree tree : geneTrees){
            // j++;
            int[] fre = new int[this.taxaMap.size()];
            for(TreeNode node : tree.nodes){
                if(node.isLeaf()){
                    fre[node.taxon.id]++;
                }
            }
            for(TreeNode node : tree.topSortedNodes){
                if(node.isLeaf()){
                    node.gainChildNodes = new PartitionNode[2];
                    int index = 0;
                    if(fre[node.taxon.id] == 1){
                        index = 1;
                    }
                    node.gainChildNodes[index] = node.partitionNode;
                }
                else{
                    if(node.isRoot()) continue;
                    node.gainChildNodes = new PartitionNode[2];
                    ArrayList<PartitionNode> gainNodesCommon = new ArrayList<>();
                    ArrayList<PartitionNode> gainNodesDistinct = new ArrayList<>();
                    for(TreeNode child : node.childs){
                        if(child.gainChildNodes[0] != null){
                            gainNodesCommon.add(child.gainChildNodes[0]);
                        }
                        if(child.gainChildNodes[1] != null){
                            gainNodesDistinct.add(child.gainChildNodes[1]);
                        }
                    }
                    if(gainNodesCommon.size() == 1){
                        node.gainChildNodes[0] = gainNodesCommon.get(0);
                    }
                    else{
                        node.gainChildNodes[0] = partitionGraph.addPartition(gainNodesCommon, true);
                    }

                    if(gainNodesDistinct.size() == 1){
                        node.gainChildNodes[1] = gainNodesDistinct.get(0);
                    }
                    else{
                        node.gainChildNodes[1] = partitionGraph.addPartition(gainNodesDistinct, true);
                    }
                }
                
                
                // if(node.isLeaf() || node.isRoot() || node.dupplicationNode ) continue;
                // ArrayList<PartitionNode> ps = new ArrayList<>();
                // for(TreeNode child : node.childs){
                //     ps.add(child.partitionNode);
                // }
                // ps.add(node.parentPartitionNode);
                // PartitionNode[] p = new PartitionNode[ps.size()];
                // for(int i = 0; i < ps.size(); ++i){
                //     p[i] = ps.get(i);
                // }
                // partitions.addPartitionByTreeNode(p);
                // System.out.println("=============Partition=================");
                // for(int i = 0; i < ps.size(); ++i)
                //     printPartition(realTaxaInPartition.get(p[i]));
            }
            tree.root.childs.get(0).gainParentNode = tree.root.childs.get(1).gainChildNodes[1];
            tree.root.childs.get(1).gainParentNode = tree.root.childs.get(0).gainChildNodes[1];

            for(int i = tree.topSortedNodes.size() - 2; i > -1; --i){
                TreeNode node = tree.topSortedNodes.get(i);
                node.gainParentNode = null;
                if(node.isLeaf()) continue;
                if(node.parent != tree.root){
                    ArrayList<PartitionNode> ps = new ArrayList<>();
                    for(TreeNode child : node.parent.childs){
                        if(child == node) continue;
                        if(child.gainChildNodes[1] != null){
                            ps.add(child.gainChildNodes[1]);
                        }
                    }
                    if(node.parent.gainParentNode != null){
                        ps.add(node.parent.gainParentNode);
                    }
                    if(ps.size() == 1){
                        node.gainParentNode = ps.get(0);
                    }
                    else{
                        node.gainParentNode = partitionGraph.addPartition(ps, true);
                    }
                }

                if(!node.dupplicationNode){
                    PartitionNode[][] gainNodes = new PartitionNode[node.childs.size()][];
                    PartitionNode[] p = new PartitionNode[node.childs.size() + 1];
                    int k = 0;
                    for(TreeNode child : node.childs){
                        p[k] = child.partitionNode;
                        gainNodes[k++] = child.gainChildNodes;
                    }
                    p[k] = node.parentPartitionNode;

                    partitions.addPartitionByTreeNode(p, gainNodes, node.gainParentNode);
                }
            }

            if(!tree.root.dupplicationNode){
                TreeNode rootNode = tree.root;
                PartitionNode[] ps = new PartitionNode[rootNode.childs.size() + 1];
                PartitionNode[][] gainNodes = new PartitionNode[rootNode.childs.size()][];
                int k = 0;
                for(TreeNode child : rootNode.childs){
                    ps[k] = child.partitionNode;
                    gainNodes[k++] = child.gainChildNodes;
                }
                ps[k] = partitionGraph.getSentinel();
    
                partitions.addPartitionByTreeNode(ps, gainNodes,partitionGraph.getSentinel());
            }


        }

        return partitions;
    }

    public PartitionGraph createPartitionGraph(){
        PartitionGraph partitionGraph = new PartitionGraph(this.taxa);
        for(Tree tree : geneTrees){
            for(TreeNode node : tree.nodes){
                if(node.isLeaf()){
                    node.partitionNode = partitionGraph.getPartitionNode(node.taxon);
                }
            }

            for (TreeNode node : tree.topSortedNodes) {
                if(node.isRoot() || node.isLeaf()) continue;
                ArrayList<PartitionNode> childs = new ArrayList<>();
                for(TreeNode child : node.childs){
                    childs.add(child.partitionNode);
                }
                node.partitionNode = partitionGraph.addPartition(childs, false);
            }
            tree.root.childs.get(0).parentPartitionNode = tree.root.childs.get(1).partitionNode;
            tree.root.childs.get(1).parentPartitionNode = tree.root.childs.get(0).partitionNode;
            
            int sz = tree.topSortedNodes.size();
            for(int i = sz - 2; i > -1; --i){
                TreeNode node = tree.topSortedNodes.get(i);
                if(node.isLeaf() || node.parent == tree.root) continue;

                ArrayList<PartitionNode> childs = new ArrayList<>();
                for(TreeNode child : node.parent.childs){
                    if(child == node) continue;
                    childs.add(child.partitionNode);
                }
                childs.add(node.parent.parentPartitionNode);
                node.parentPartitionNode = partitionGraph.addPartition(childs, false);
            }
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


