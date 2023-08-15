package src;

import java.io.FileNotFoundException;
public class Main {
    public static void main(String[] args) throws FileNotFoundException{

        // String filePath = "./input/gtree_11tax_est_5genes_R1.tre";
        String filePath = "./input/15_tax_cleaned.txt";

        new QFM(filePath);

        
        // PerLevelDs ds = new PerLevelDs();


        // Scanner scanner = new Scanner(new File("./input/gtree_11tax_est_5genes_R1.tre"));
        
        // while(scanner.hasNextLine()){
        //     String line = scanner.nextLine();
        //     GeneTree tr = new GeneTree(line);

        //     Set<Integer> stA = new HashSet<>();
        //     // stA.add(tr.taxaMap.get("1"));
        //     // stA.add(tr.taxaMap.get("2"));
        //     stA.add(tr.taxaMap.get("3"));
        //     stA.add(tr.taxaMap.get("5"));
        //     // stA.add(tr.taxaMap.get("1"));
        //     // stA.add(tr.taxaMap.get("2"));
        //     // stA.add(tr.taxaMap.get("7"));


        //     Set<Integer> stB = new HashSet<>();
        //     stB.add(tr.taxaMap.get("4"));
        //     // stB.add(tr.taxaMap.get("7"));
        //     stB.add(tr.taxaMap.get("6"));
        //     // stB.add(tr.taxaMap.get("10"));
        //     // stB.add(tr.taxaMap.get("11"));
        //     // stB.add(tr.taxaMap.get("10"));
        //     // stB.add(tr.taxaMap.get("3"));
        //     stB.add(tr.taxaMap.get("8"));
        //     // stB.add(tr.taxaMap.get("6"));
        //     // stB.add(tr.taxaMap.get("4"));




        //     System.out.println(stA);
        //     System.out.println(stB);

        //     // ArrayList<IDummyTaxa> dummyTaxas = new ArrayList<>();
        //     // ArrayList<IDummyTaxa> dummypA = new ArrayList<>();
        //     // ArrayList<IDummyTaxa> dummypB = new ArrayList<>();
            
        //     Map<Integer, Integer> mp = new HashMap<>();
            

        //     mp.put(tr.taxaMap.get("1"), 0);
        //     mp.put(tr.taxaMap.get("7"), 0);
        //     mp.put(tr.taxaMap.get("11"), 0);


        //     mp.put(tr.taxaMap.get("2"), 1);
        //     mp.put(tr.taxaMap.get("9"), 1);

        //     mp.put(tr.taxaMap.get("10"), 2);

        //     Set<Integer> dtA = new HashSet<>();
        //     Set<Integer> dtB = new HashSet<>();
        //     dtB.add(0);
        //     dtA.add(1);
        //     dtB.add(2);
            
        //     ArrayList<Set<Integer>> partition = new ArrayList<>();
        //     partition.add(stA);
        //     partition.add(stB);
            
        //     ArrayList<Set<Integer>> dPartition = new ArrayList<>();
        //     dPartition.add(dtA);
        //     dPartition.add(dtB);

        //     // dummypAIndices.add(1);
        //     // dummypBIndices.add(2);



        //     var calculator = new ScoreCalculatorTree(
        //         tr, 
        //         new BiPartitionTreeSpecific(partition, mp, dPartition, 11)
        //     );

        //     var score = calculator.score();
        //     System.out.println(score);
        //     // System.out.println(tr.score(stA, stB, mp,dummypAIndices, dummypBIndices));

        //     System.out.println(tr.root.toString());

        //     ds.addGeneTree(tr);

        //     // break;
            
        // }

        // scanner.close();

    }
}
