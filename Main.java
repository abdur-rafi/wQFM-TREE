package wqfm.dsGT;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeSet;
public class Main {
    public static void main(String[] args) throws FileNotFoundException{
        
        ArrayList<IDummyTaxa> dt = new ArrayList<>();
        PerLevelDs ds = new PerLevelDs(dt);


        Scanner scanner = new Scanner(new File("/home/abdur-rafi/Academic/Thesis/wQFM-2020/WQFM/input_files/gtree_11tax_est_5genes_R1.tre"));
        
        while(scanner.hasNextLine()){
            String line = scanner.nextLine();
            GeneTree tr = new GeneTree(line);

            Set<Integer> stA = new TreeSet<>();
            stA.add(tr.taxaMap.get("1"));
            // stA.add(tr.taxaMap.get("2"));
            stA.add(tr.taxaMap.get("3"));
            stA.add(tr.taxaMap.get("5"));
            // stA.add(tr.taxaMap.get("1"));
            // stA.add(tr.taxaMap.get("2"));
            stA.add(tr.taxaMap.get("7"));


            Set<Integer> stB = new TreeSet<>();
            stB.add(tr.taxaMap.get("4"));
            // stB.add(tr.taxaMap.get("5"));
            stB.add(tr.taxaMap.get("6"));
            stB.add(tr.taxaMap.get("10"));
            stB.add(tr.taxaMap.get("11"));
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
            Set<Integer> dummypAIndices = new HashSet<>();
            Set<Integer> dummypBIndices = new HashSet<>();

            System.out.println(tr.score(stA, stB, mp,dummypAIndices, dummypBIndices));

            System.out.println(tr.root.toString());

            ds.addGeneTree(tr);

            break;
            
        }

        scanner.close();

    }
}
