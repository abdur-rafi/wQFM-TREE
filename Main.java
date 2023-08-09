package wqfm.dsGT;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
public class Main {
    public static void main(String[] args) throws FileNotFoundException{
        
        ArrayList<IDummyTaxa> dt = new ArrayList<>();
        PerLevelDs ds = new PerLevelDs(dt);


        Scanner scanner = new Scanner(new File("/home/abdur-rafi/Academic/Thesis/wQFM-2020/WQFM/input_files/gtree_11tax_est_5genes_R1.tre"));
        
        while(scanner.hasNextLine()){
            String line = scanner.nextLine();
            GeneTree tr = new GeneTree(line);

            Set<Integer> stA = new HashSet<>();
            // stA.add(tr.taxaMap.get("1"));
            // stA.add(tr.taxaMap.get("2"));
            stA.add(tr.taxaMap.get("3"));
            stA.add(tr.taxaMap.get("5"));
            // stA.add(tr.taxaMap.get("1"));
            // stA.add(tr.taxaMap.get("2"));
            // stA.add(tr.taxaMap.get("7"));


            Set<Integer> stB = new HashSet<>();
            stB.add(tr.taxaMap.get("4"));
            // stB.add(tr.taxaMap.get("7"));
            stB.add(tr.taxaMap.get("6"));
            // stB.add(tr.taxaMap.get("10"));
            // stB.add(tr.taxaMap.get("11"));
            // stB.add(tr.taxaMap.get("10"));
            // stB.add(tr.taxaMap.get("3"));
            stB.add(tr.taxaMap.get("8"));
            // stB.add(tr.taxaMap.get("6"));
            // stB.add(tr.taxaMap.get("4"));




            System.out.println(stA);
            System.out.println(stB);

            // ArrayList<IDummyTaxa> dummyTaxas = new ArrayList<>();
            // ArrayList<IDummyTaxa> dummypA = new ArrayList<>();
            // ArrayList<IDummyTaxa> dummypB = new ArrayList<>();
            
            Map<Integer, Integer> mp = new HashMap<>();
            

            mp.put(tr.taxaMap.get("1"), 0);
            mp.put(tr.taxaMap.get("7"), 0);
            mp.put(tr.taxaMap.get("11"), 0);


            mp.put(tr.taxaMap.get("2"), 1);
            mp.put(tr.taxaMap.get("9"), 1);

            mp.put(tr.taxaMap.get("10"), 2);

            int[] dummyTaxaToPartitionMap = new int[3];
            dummyTaxaToPartitionMap[0] = 0;
            dummyTaxaToPartitionMap[1] = 0;
            dummyTaxaToPartitionMap[2] = 1;

            int[] dummyTaxaSize = new int[3];
            dummyTaxaSize[0] = 3;
            dummyTaxaSize[1] = 2;
            dummyTaxaSize[2] = 1;

            int[] dummyTaxaPartitionSize = new int[2];
            dummyTaxaPartitionSize[0] = 5;
            dummyTaxaPartitionSize[1] = 1;
            
            ArrayList<Set<Integer>> partition = new ArrayList<>();
            partition.add(stA);
            partition.add(stB);
            

            // dummypAIndices.add(1);
            // dummypBIndices.add(2);

            var calculator = new ScoreCalculator(
                tr, 
                partition, 
                mp, 
                dummyTaxaToPartitionMap, 
                dummyTaxaSize,
                dummyTaxaPartitionSize
            );

            var score = calculator.score();
            System.out.println(score[0] + " " + score[1]);
            // System.out.println(tr.score(stA, stB, mp,dummypAIndices, dummypBIndices));

            System.out.println(tr.root.toString());

            ds.addGeneTree(tr);

            break;
            
        }

        scanner.close();

    }
}
