package src;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

import src.BiPartition.BiPartition;
import src.BiPartition.BiPartitionTreeSpecific;
import src.ConsensusTree.ConsensusTreeFlat;
import src.GeneTree.GeneTree;

public class DebugMain {


    public static void main(String[] args) throws FileNotFoundException{
        String filePath = "input/gtree_11tax_est_5genes_R1.tre";
        Scanner scanner = new Scanner(new File(filePath));


        Set<String> taxaSet;
        ArrayList<GeneTree> geneTrees;



        taxaSet = new HashSet<>();
        geneTrees = new ArrayList<>();

        // System.out.println(consensusFilePath);

        while (scanner.hasNextLine()) {
            
            String line = scanner.nextLine();
            if(line.trim().length() == 0) continue;
            // System.out.println("line : " + line);
            GeneTree tr = new GeneTree(line);
            for(var x : tr.nodes){
                if(x.isLeaf())
                    taxaSet.add(x.label);
            }
            geneTrees.add(tr);

        }
        scanner.close();

        taxaSet = new HashSet<>();
        taxaSet.add("1");
        taxaSet.add("2");
        taxaSet.add("3");
        taxaSet.add("4");
        taxaSet.add("11");

        ArrayList<Set<String>> dtList = new ArrayList<>();
        Set<String> dt1 = new HashSet<>();
        dt1.add("5");
        dt1.add("6");
        dt1.add("7");
        dt1.add("8");
        dt1.add("9");
        dt1.add("10");




        // BiPartition partition = new BiPartition(taxaSet, dtList,);
                // System.out.println(x.getNewickFormat());
    }
    
}
