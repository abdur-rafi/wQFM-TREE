package src;

import java.io.FileWriter;
import java.io.IOException;

import src.PreProcessing.Preprocess;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;

// 5:18
public class Main {

    public static void main(String[] args) throws IOException {

        String inputFilePath = "";
        String consensusFilePath = "";
        String outputFilePath = "";



        // if(args.length < 3){
        //     System.out.println("Specify all file paths and non quartet type");
        //     System.exit(-1);
        // }

        inputFilePath = args[0];
        // consensusFilePath = args[1];
        // outputFilePath = args[2];

        // inputFilePath = "./input/rootingTest.txt";
        // consensusFilePath = "./input/rootingTest.txt";
        // outputFilePath = "./output.tre";

        // String nonQuartetType = args[3];
        
        // if(nonQuartetType.equals("A")){
        //     Config.NON_QUARTET_TYPE = Config.NonQuartetType.A;
        // }else if(nonQuartetType.equals("B")){
        //     Config.NON_QUARTET_TYPE = Config.NonQuartetType.B;
        // }else{
        //     System.out.println("Specify non quartet type as A or B");
        //     System.exit(-1);
        // }


        // String inputFilePath = "../run/15-taxon/1000gene-1000bp/R1/all_gt_cleaned.tre";
        // String consensusFilePath = "../run/15-taxon/1000gene-1000bp/R1/cons.tre";

        // inputFilePath = "../run/15-taxon/1000gene-100bp/R4/all_gt_cleaned.tre";
        // consensusFilePath = "../run/15-taxon/1000gene-100bp/R4/cons.tre";


        // // inputFilePath = "./input/gt-cleaned-50.resolved.cleaned";
        // // consensusFilePath = "./input/cons-50.tre";
        

        // String outputFilePath = "./outputdc.tre";
        
        // inputFilePath = "./input/gt-cleaned";
        // inputFilePath = "input/gtree_11tax_est_5genes_R1.tre";
        // consensusFilePath = "./input/cons.greedy.tree";
        // consensusFilePath = "./input/gtree_11tax_est_5genes_R1.tre";
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

        // String inputFilePath = "./input/n10ProRooted.cleaned";
        // // String inputFilePath = "./input/t2.txt";
        // String consensusFilePath = "./input/proCons.greedy.tree";
        // // String consensusFilePath = "./input/t2.txt";
        // String outputFilePath = "./proOutput.tre";

        ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
        long time_1 = System.currentTimeMillis(); //calculate starting time
        long cpuTimeBefore = threadMXBean.getCurrentThreadCpuTime();

        // GeneTrees trees = new GeneTrees(inputFilePath);
        // var taxaMap = trees.readTaxaNames();
        // trees.readGeneTrees(null);

        // IMakePartition partitionMaker = new ConsensusTreePartition(consensusFilePath, taxaMap, trees);

        
        
        // RealTaxon.count = 0;
        

        Preprocess.PreprocessReturnType ret = Preprocess.preprocess(inputFilePath);
        // ConsensusTreePartitionDC consensusTreePartitionDC = new ConsensusTreePartitionDC(consensusFilePath, ret.taxaMap, ret.dc);
        // IMakePartition  partitionMakerDC = consensusTreePartitionDC;





        
        // var qfm = new QFM(trees, trees.taxa, new ConsensusTreePartition("((11,(10,((9,(8,7)),(6,5)))),4,(3,(1,2)));", trees.taxaMap));
        // var qfm = new QFM(trees, trees.taxa, new RandPartition());

        // var qfm = new QFM(trees, trees.taxa, partitionMaker);
        // QFMDC qfm = new QFMDC(ret.dc, ret.realTaxa , partitionMakerDC);
        // // var qfm = new QFMTest(trees, trees.taxa, partitionMaker, ret.dc);

        // var spTree = qfm.runWQFM();

        // FileWriter writer = new FileWriter(outputFilePath);

        // writer.write(spTree.getNewickFormat());

        // writer.close();

        // long cpuTimeAfter = threadMXBean.getCurrentThreadCpuTime();

        // long time_del = System.currentTimeMillis() - time_1;
        // long minutes = (time_del / 1000) / 60;
        // long seconds = (time_del / 1000) % 60;
        // System.out.format("\nElapsed Time taken = %d ms ==> %d minutes and %d seconds.\n", time_del, minutes, seconds);

        // long cpuTimeUsed = cpuTimeAfter - cpuTimeBefore;

        // seconds = cpuTimeUsed / 1_000_000_000;
        
        // minutes = seconds / 60;
        // seconds = seconds % 60;

        // System.out.println("CPU time used: " + minutes + " minutes, " + seconds + " seconds");
    
    }

    
}
