package src;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

import src.BiPartition.BiPartition;
import src.BiPartition.BiPartitionMapper;
import src.BiPartition.BiPartitionTreeSpecific;
import src.BiPartition.Swap;
import src.ConsensusTree.ConsensusTree;
import src.ConsensusTree.ConsensusTreeFlat;
import src.ConsensusTree.IMakeParition;
import src.GeneTree.GeneTree;
import src.ScoreCalculator.ScoreCalculatorTree;

public class QFM {


    private static int dummyIds = 0;

    Set<String> taxaSet;
    ArrayList<GeneTree> geneTrees;
    ArrayList<BiPartitionTreeSpecific> biPartitions;
    int level;
    int mxItrCount = 50;
    // ConsensusTree cTree;
    IMakeParition makeParition;

    public QFM(String inputFilePath, String consensusFilePath, String outputFilePath) throws IOException {
        
        Scanner scanner = new Scanner(new File(inputFilePath));

        taxaSet = new HashSet<>();
        geneTrees = new ArrayList<>();
        biPartitions = new ArrayList<>();
        level = 0;

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

        scanner = new Scanner(new File(consensusFilePath));
        String line = scanner.nextLine();

        makeParition = new ConsensusTreeFlat(line);
        scanner.close();

        BiPartition partition = new BiPartition(taxaSet, new ArrayList<>(), new ArrayList<>(), new HashMap<>(), makeParition);
        

        var x = recurse(partition);

        // System.out.println(x.nodes.size());
        // System.out.println(x.root);

        FileWriter writer = new FileWriter(outputFilePath);
        
        writer.write(x.getNewickFormat());

        writer.close();

        // System.out.println(x.getNewickFormat());
        // output the tree to file
        // try {
        //     BufferedWriter writer = new BufferedWriter(new java.io.FileWriter(outputFilePath));
        //     writer.write(x.getNewickFormat());
        //     writer.close();
        // } catch (Exception e) {
        //     System.out.println("Err");
        // }

        // x.reRootTree(x.nodes.get(12));
        // System.out.println(x.root);
        // System.out.println(x.root);
        // while(oneStep(partition));

        // System.out.println("Old partition");
        // System.out.println(partition);
        // var b = partition.divide();
        // System.out.println("New partition");

        // System.out.println(b[0]);
        // System.out.println(b[1]);

        // while(oneStep(b[0]));
        // while(oneStep(b[1]));

        // System.out.println(b[0]);
        // System.out.println(b[1]);

        // System.out.println("Final Partition: " + partition);

    }

    GeneTree recurse(BiPartition partition){
        System.out.println("level : " + level++);
        // System.out.println("Current Partition : \n" + partition );
        int step = 0;
        int itr = 0;
        while(oneStep(partition)){
            ++itr;
            if(itr > mxItrCount){
                System.out.println("Max Iteration Count Reached");
                break;
            }
            // System.out.println("step : " + step++);
        }
        // System.out.println("Refined Partition: \n" + partition);
        var b = partition.divide();
        // System.out.println(partition.getCg());
        GeneTree[] trs = new GeneTree[2];
        int i = 0;
        for(var x : b){
            if(x.isValid()){
                trs[i++] = recurse(x);
            }
            else{
                trs[i++] = x.createStar();
            }
        }
        return partition.mergeTrees(trs);

    }

    boolean oneStep(BiPartition partition){

        ArrayList<Swap> swaps = new ArrayList<>();
        double mxCg = 0;
        int mxcgi = -1;

        while(true){

            ArrayList<BiPartitionTreeSpecific> biPartitions = new ArrayList<>();
    
            for(var x : geneTrees){
                biPartitions.add(
                    BiPartitionMapper.map(partition, x)
                );
            }
    
            // System.out.println(partition);
    
            for(int i = 0; i < geneTrees.size(); ++i){
                var gt = geneTrees.get(i);
                var bp = biPartitions.get(i);
                ScoreCalculatorTree calc = new ScoreCalculatorTree(gt, bp);
                calc.score();
                for(var x : gt.nodes){
                    if(x.isLeaf() && partition.isInRealTaxa(x.label)){
                        int p = bp.inWhichPartition(x.index, true);
                        partition.addRealTaxaGain(x.label, x.info.gains[p]);
                    }
                }
                for(int j = 0; j < calc.dummyTaxaGains().length; ++j){
                    partition.addDummyTaxaGain(bp.globalDummyTaxaIndex(j), calc.dummyTaxaGains()[j]);
                }
                
                // System.out.println( "score : " + score);
                // System.out.println(geneTrees.get(i).root);
            }
            var swap = partition.swapMax();
            if(swap == null){
                break;
            }
            else{
                swaps.add(swap);
                // System.out.println("swaps size : " + swaps.size());

                var pSize = partition.partitionSize();
                if( pSize[0] > 1 && pSize[1] > 1){
                    if(mxcgi == -1 ){
                        mxCg = partition.getCg();
                        mxcgi = swaps.size() - 1;
                    }
                    else if(mxCg < partition.getCg()){
                        mxCg = partition.getCg();
                        mxcgi = swaps.size() - 1;
                    }
                }
            }
            partition.resetGains();
            // for(var x : taxaSet){
            //     System.out.println(x + " : " + partition.getGainRealTaxa(x));
            // }
            // System.out.println("lOOPING");
            // System.out.println(swap.rt + " " + swap.dti);
        }
        if(mxCg > 0){
            for(int i = mxcgi + 1; i < swaps.size(); ++i){
                partition.swap(swaps.get(i));
            }
        }
        partition.resetAll();
        // System.out.println("mxcgi " + mxcgi + " mxcg : " + mxCg);

        // System.out.println(partition);

        return mxCg > 0;

    }

    public static int getDummyId(){
        return dummyIds++;
    }

}
