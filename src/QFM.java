package src;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

import src.BiPartition.BiPartition;
import src.BiPartition.BiPartitionMapper;
import src.BiPartition.BiPartitionTreeSpecific;
import src.GeneTree.GeneTree;
import src.ScoreCalculator.ScoreCalculatorTree;

public class QFM {

    Set<String> taxaSet;
    ArrayList<GeneTree> geneTrees;
    ArrayList<BiPartitionTreeSpecific> biPartitions;

    public QFM(String filePath) throws FileNotFoundException {
        Scanner scanner = new Scanner(new File(filePath));

        taxaSet = new HashSet<>();
        geneTrees = new ArrayList<>();
        biPartitions = new ArrayList<>();

        while (scanner.hasNextLine()) {
            
            String line = scanner.nextLine();
            GeneTree tr = new GeneTree(line);
            for(var x : tr.nodes){
                if(x.isLeaf())
                    taxaSet.add(x.label);
            }
            geneTrees.add(tr);

        }
        scanner.close();

        BiPartition partition = new BiPartition(taxaSet, new ArrayList<>(), geneTrees);
        for(var x : this.geneTrees){
            biPartitions.add(
                BiPartitionMapper.map(partition, x)
            );
        }

        System.out.println(partition);

        for(int i = 0; i < geneTrees.size(); ++i){
            ScoreCalculatorTree calc = new ScoreCalculatorTree(geneTrees.get(i), biPartitions.get(i));
            int score = calc.score();
            System.out.println(score);
            System.out.println(geneTrees.get(i).root);
        }



    }

}
