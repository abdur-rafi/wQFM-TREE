package src.v2.PreProcessing;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import src.v2.Taxon.RealTaxon;
import src.v2.Tree.Tree;
import src.v2.Tree.TreeNode;


public class GeneTrees {

    public ArrayList<Tree> geneTrees;
    public String[] taxonIdToLabel;
    public RealTaxon[] taxa;
    public Map<String, TreeNode> triPartitions;
    public Map<String, RealTaxon> taxaMap;
    public int realTaxaCount;

    public GeneTrees(String path) throws FileNotFoundException{

        this.geneTrees = new ArrayList<>();
        this.triPartitions = new HashMap<>();

        int internalNodesCount = 0;

        Scanner scanner = new Scanner(new File(path));

        String line = scanner.nextLine();
        var tree = new Tree(line);
        var mp = tree.taxaMap;
        this.taxaMap = mp;
        this.geneTrees.add(tree);  
        tree.calculateFrequencies(triPartitions);
        internalNodesCount += tree.nodes.size() - tree.leavesCount;

        // System.out.println(tree.root);

        // for(var x : tree.nodes){
        //     System.out.printf(x.index + ", ");
        // }
        // System.out.println();

        // System.out.println(tree.taxaMap);


        while (scanner.hasNextLine()) {
            
            line = scanner.nextLine();
            if(line.trim().length() == 0) continue;
            
            tree = new Tree(line, mp);
            tree.calculateFrequencies(triPartitions);
            geneTrees.add(tree);
            internalNodesCount += tree.nodes.size() - tree.leavesCount;
            // System.out.println(tree.root);

        }
        scanner.close();
        
        this.taxonIdToLabel = new String[mp.size()];
        this.taxa = new RealTaxon[mp.size()];
        this.realTaxaCount = mp.size();

        for(var x : mp.entrySet()){
            taxonIdToLabel[x.getValue().id] = x.getKey();
            taxa[x.getValue().id] = x.getValue();
        }



        System.out.println(internalNodesCount);
        System.out.println(triPartitions.size());
        // for(var x : triPartitions.entrySet()){
        //     System.out.println(x.getValue().frequency);
        // }

    }
    
} 
