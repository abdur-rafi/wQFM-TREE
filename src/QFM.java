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
    String line;

    public QFM(String filePath) throws FileNotFoundException {
        Scanner scanner = new Scanner(new File(filePath));

        taxaSet = new HashSet<>();
        geneTrees = new ArrayList<>();
        biPartitions = new ArrayList<>();

        while (scanner.hasNextLine()) {
            
            String line = scanner.nextLine();
            this.line = line;
            GeneTree tr = new GeneTree(line);
            for(var x : tr.nodes){
                if(x.isLeaf())
                    taxaSet.add(x.label);
            }
            geneTrees.add(tr);
            break;
        }
        scanner.close();

        BiPartition partition = new BiPartition(taxaSet, new ArrayList<>(), geneTrees);
        
        oneStep(partition);

    }

    void oneStep(BiPartition partition){

        while(true){

            ArrayList<BiPartitionTreeSpecific> biPartitions = new ArrayList<>();
    
            for(var x : geneTrees){
                biPartitions.add(
                    BiPartitionMapper.map(partition, x)
                );
            }
    
            System.out.println(partition);
    
            for(int i = 0; i < geneTrees.size(); ++i){
                var gt = geneTrees.get(i);
                var bp = biPartitions.get(i);
                ScoreCalculatorTree calc = new ScoreCalculatorTree(gt, bp);
                int score = calc.score();
                for(var x : gt.nodes){
                    if(x.isLeaf()){
                        int p = bp.inWhichPartition(x.index, true);
                        partition.addRealTaxaGain(x.label, x.info.gains[p]);
                    }
                }
                for(int j = 0; j < calc.dummyTaxaGains().length; ++j){
                    partition.addDummyTaxaGain(bp.globalDummyTaxaIndex(j), calc.dummyTaxaGains()[j]);
                }
                
                System.out.println( "score : " + score);
                // System.out.println(geneTrees.get(i).root);
            }

            if(!partition.swapMax()){
                break;
                // if(partition.isAllLocked()){
                //     partition.resetAll();
                // }
                // else{
                //     break;
                // }
            }
            partition.resetGains();
            // for(var x : taxaSet){
            //     System.out.println(x + " : " + partition.getGainRealTaxa(x));
            // }
        }


    }

}
