package src.PreProcessing;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

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
    

    public GeneTrees(String path) throws FileNotFoundException{

        this.geneTrees = new ArrayList<>();
        this.triPartitions = new HashMap<>();

        int internalNodesCount = 0;
        
        Set<String> taxaSet = new HashSet<>();

        Scanner scanner = new Scanner(new File(path));
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
        

        scanner = new Scanner(new File(path));

        while (scanner.hasNextLine()) {

            String line = scanner.nextLine();
            // System.out.println(line);
            if(line.trim().length() == 0) continue;
            
            var tree = new Tree(line, this.taxaMap);

            // System.out.println(tree.root);

            tree.calculateFrequencies(triPartitions);
            geneTrees.add(tree);
            // for(var x : tree.nodes){
            //     x.frequency = 1;
            // }
            // System.out.println(tree.leavesCount);
            internalNodesCount += tree.nodes.size() - tree.leavesCount;
            // System.out.println(tree.root);
            if(tree.checkIfNonBinary()){
                System.out.println("non binary tree");
            }

        }
        
        scanner.close();
        
        this.taxonIdToLabel = new String[this.taxaMap.size()];
        this.taxa = new RealTaxon[this.taxaMap.size()];
        this.realTaxaCount = this.taxaMap.size();

        for(var x : this.taxaMap.entrySet()){
            taxonIdToLabel[x.getValue().id] = x.getKey();
            taxa[x.getValue().id] = x.getValue();
        }


        System.out.println( "taxon count : " + taxaSet.size());
        System.out.println("Gene trees count : " + geneTrees.size());
        System.out.println( "total internal nodes : " + internalNodesCount);
        System.out.println( "unique partitions : " + triPartitions.size());

        // for(var x : triPartitions.entrySet()){
        //     System.out.println(x.getValue().frequency);
        // }

    }
    
} 


