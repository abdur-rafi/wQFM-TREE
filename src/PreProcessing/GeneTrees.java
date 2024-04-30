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
import src.Quartets.QuartestsList;
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
        // QuartestsList quartestsList = new QuartestsList(this.taxaMap.size());

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

            // tree.generateQuartets(quartestsList);


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
        
        


        // System.out.println( "taxon count : " + this.taxaMap.size());
        // System.out.println("Gene trees count : " + geneTrees.size());

        // quartestsList.printQuartets(taxa);
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

        // System.exit(-1);

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

    void calculateSpeciationParentComponents(TreeNode node, Component last, ComponentGraph componentGraph){
        if(node.isLeaf()) return;
        
        Component lastc0 = last;
        Component lastc1 = last;


        var c0 = node.childs.get(0);
        var c1 = node.childs.get(1);


        if(!node.dupplicationNode){

            node.speciationParentComponent = last;
            ArrayList<Component> compsc0 = new ArrayList<>();
            compsc0.add(last);
            compsc0.add(c1.childComponent);

            ArrayList<Component> compsc1 = new ArrayList<>();
            compsc1.add(last);
            compsc1.add(c0.childComponent);

            lastc0 = componentGraph.addComponent(compsc0);
            lastc1 = componentGraph.addComponent(compsc1);
        }

        calculateSpeciationParentComponents(c0, lastc0, componentGraph);
        calculateSpeciationParentComponents(c1, lastc1, componentGraph);

    }

    public InternalNodes createPartitionsByTreeNode(ComponentGraph componentGraph){
        InternalNodes internalNodes = new InternalNodes(componentGraph.realTaxaInComponent);

        for(Tree tree : geneTrees){
            for(TreeNode node : tree.topSortedNodes){
                if(node.isLeaf()){
                    node.childComponent = componentGraph.taxaPartitionNodes[node.taxon.id];
                }
                else{
                    ArrayList<Component> childs = new ArrayList<>();
                    for(TreeNode child : node.childs){
                        childs.add(child.childComponent);
                    }
                    node.childComponent = componentGraph.addComponent(childs);
                }
            }

            calculateSpeciationParentComponents(tree.root, componentGraph.getSentinel(), componentGraph);


            for(int i = tree.topSortedNodes.size() - 1; i > -1; --i){
                TreeNode node = tree.topSortedNodes.get(i);
                
                if(node.isLeaf()) continue;

                if(!node.dupplicationNode){
                    Component[] childComps = new Component[node.childs.size()];
                    for(int j = 0; j < node.childs.size(); ++j){
                        childComps[j] = node.childs.get(j).childComponent;
                    }
                    var x = internalNodes.addInternalNode(childComps, node.speciationParentComponent);
                    
                    // System.out.println( "node : " + node.index + " partition: " + x);
                }

                // System.out.println("node index : " + node.index + " tree index : " + treeIndex);
                // System.out.println("common with parent: " + node.commonWithParent);
                // System.out.println( "distict from parent: " + node.distinctWithParent);
                // System.out.println("parent distincts : " + node.parentDistinct);



            }

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


