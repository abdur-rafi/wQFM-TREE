package src;

import java.io.FileWriter;
import java.io.IOException;

import src.InitialPartition.ConsensusTreePartition;
import src.InitialPartition.IMakePartition;
import src.PreProcessing.GeneTrees;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;

// 5:18
public class Main {

    public static void main(String[] args) throws IOException {

        if(args.length < 4){
            System.out.println("Specify all file paths and non quartet type");
            System.exit(-1);
        }
        String inputFilePath = args[0];
        String consensusFilePath = args[1];
        String outputFilePath = args[2];

        String nonQuartetType = args[3];
        
        if(nonQuartetType.equals("A")){
            Config.NON_QUARTET_TYPE = Config.NonQuartetType.A;
        }else if(nonQuartetType.equals("B")){
            Config.NON_QUARTET_TYPE = Config.NonQuartetType.B;
        }else{
            System.out.println("Specify non quartet type as A or B");
            System.exit(-1);
        }


        // String inputFilePath = "../run/15-taxon/1000gene-1000bp/R10/all_gt_cleaned.tre";
        // String consensusFilePath = "../run/15-taxon/1000gene-1000bp/R10/cons.tre";
        // String outputFilePath = "./output.tre";
        
        // inputFilePath = "./input/custom2.tre";
        // consensusFilePath = inputFilePath;
        // inputFilePath = "../run/07.trueGT.cleaned";
        // consensusFilePath = "../run/100.raxml.consensusTreeMRE";
        // outputFilePath = "./output.tre";

        // inputFilePath = "./input/200.500k.gtree";
        // consensusFilePath = "./input/200.500k.cons.tre";
        // outputFilePath = "./output.tre";
        
        // inputFilePath = "./input/gtree_11tax_est_5genes_R1.tre";
        // consensusFilePath = "./input/5genes.raxml.consensusTreeMRE.cleaned";
        // outputFilePath = "./output.tre";
        // String modelCond = "model.50.2000000.0.000001";
        // inputFilePath = "../run/astral2/estimated-gene-trees/" + modelCond + "/03/gt-cleaned";
        // consensusFilePath = "../run/astral2/estimated-consensus-trees/" + modelCond + "/03/cons.tre";
        
        // // consensusFilePath = "./input/5genes.raxml.consensusTreeMRE.cleaned";
        // outputFilePath = "./output.tre";        

        // GeneTrees trees = new GeneTrees("../run/15-taxon/100gene-100bp/R1/all_gt_cleaned.tre");
        // GeneTrees trees = new GeneTrees("../run/07.trueGT.cleaned");
        // GeneTrees trees = new GeneTrees("./input/gtree_11tax_est_5genes_R1.tre");
        // GeneTrees trees = new GeneTrees("./input/gtree_11tax_est_5genes_R1.tre");
        ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
        long time_1 = System.currentTimeMillis(); //calculate starting time
        long cpuTimeBefore = threadMXBean.getCurrentThreadCpuTime();

        GeneTrees trees = new GeneTrees(inputFilePath);
        var taxaMap = trees.readTaxaNames();

        ConsensusTreePartition consensusTreePartition = new ConsensusTreePartition(consensusFilePath, taxaMap, trees);
        
        trees.readGeneTrees(consensusTreePartition.dist);

        IMakePartition  partitionMaker = consensusTreePartition;





        
        // var qfm = new QFM(trees, trees.taxa, new ConsensusTreePartition("((11,(10,((9,(8,7)),(6,5)))),4,(3,(1,2)));", trees.taxaMap));
        // var qfm = new QFM(trees, trees.taxa, new RandPartition());

        var qfm = new QFM2(trees, trees.taxa, partitionMaker);
        
        var spTree = qfm.runWQFM();

        FileWriter writer = new FileWriter(outputFilePath);

        writer.write(spTree.getNewickFormat());

        writer.close();
        long cpuTimeAfter = threadMXBean.getCurrentThreadCpuTime();

        long time_del = System.currentTimeMillis() - time_1;
        long minutes = (time_del / 1000) / 60;
        long seconds = (time_del / 1000) % 60;
        System.out.format("\nElapsed Time taken = %d ms ==> %d minutes and %d seconds.\n", time_del, minutes, seconds);

        long cpuTimeUsed = cpuTimeAfter - cpuTimeBefore;

        seconds = cpuTimeUsed / 1_000_000_000;
        
        // Calculate remaining nanoseconds after converting to seconds
        // long remainingNanos = cpuTimeUsed % 1_000_000_000;

        // Convert remaining nanoseconds to milliseconds
        // long milliseconds = remainingNanos / 1_000_000;

        // Calculate minutes
        minutes = seconds / 60;
        seconds = seconds % 60;

        System.out.println("CPU time used: " + minutes + " minutes, " + seconds + " seconds");
        // System.out.println(spTree.getNewickFormat());
        // tc5(trees);


        // System.out.println(trees.taxonIdToLabel);
    }

    
}
