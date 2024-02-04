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
import src.Taxon.DummyTaxon;
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

        return taxaMap;
    }

    public RtDt readGeneTrees(double[][] distanceMatrix) throws FileNotFoundException{
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
            geneTrees.add(tree);
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
        
        this.taxonIdToLabel = new String[this.taxaMap.size()];
        this.taxa = new RealTaxon[this.taxaMap.size()];
        this.realTaxaCount = this.taxaMap.size();

        for(var x : this.taxaMap.entrySet()){
            taxonIdToLabel[x.getValue().id] = x.getKey();
            taxa[x.getValue().id] = x.getValue();
        }


        System.out.println( "taxon count : " + this.taxaMap.size());
        System.out.println("Gene trees count : " + geneTrees.size());
        System.out.println( "total internal nodes : " + internalNodesCount);
        System.out.println( "unique partitions : " + triPartitions.size());

        return reduceTaxon(taxa);


        // if(internalNodesCount == 50000){
        //     System.out.println("No polytomy, skipping");
        //     System.exit(-1);
        // }

    }   

    public RtDt reduceTaxon(RealTaxon[] rts){
        ArrayList<RealTaxon> fRts = new ArrayList<>();
        ArrayList<DummyTaxon> newDts = new ArrayList<>();
        Set<String> seen = new HashSet<>();
        Map<String, ArrayList<RealTaxon>> dups = new HashMap<>();
        for(var rt : rts){
            var name = rt.label.split("_")[0];
            if(seen.contains(name)){
                dups.get(name).add(rt);
            }
            else{
                seen.add(name);
                ArrayList<RealTaxon> list = new ArrayList<>();
                list.add(rt);
                dups.put(name, list);
            }
        }
        for(var dt : dups.entrySet()){
            if(dt.getValue().size() > 1){
                RealTaxon[] rs = dt.getValue().toArray(new RealTaxon[dt.getValue().size()]);
                var newDt = new DummyTaxon(rs, new DummyTaxon[0], dt.getKey());
                newDts.add(newDt);
            }
            else{
                dt.getValue().get(0).label = dt.getKey();
                fRts.add(dt.getValue().get(0));
            }
        }

        return new RtDt(fRts.toArray(new RealTaxon[fRts.size()]), newDts.toArray(new DummyTaxon[newDts.size()]));

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


