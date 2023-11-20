package src;

import java.io.FileWriter;
import java.io.IOException;

import src.InitialPartition.ConsensusTreePartition;
import src.InitialPartition.IMakePartition;
import src.PreProcessing.GeneTrees;

public class Main {

    public static void main(String[] args) throws IOException {

        if(args.length < 3){
            System.out.println("Specify all file paths");
            System.exit(-1);
        }
        String inputFilePath = args[0];
        String consensusFilePath = args[1];
        String outputFilePath = args[2];

        // String inputFilePath = "../run/15-taxon/1000gene-1000bp/R10/all_gt_cleaned.tre";
        // String consensusFilePath = "../run/15-taxon/1000gene-1000bp/R10/cons.tre";
        // String outputFilePath = "./output/output.tre";


        // GeneTrees trees = new GeneTrees("../run/15-taxon/100gene-100bp/R1/all_gt_cleaned.tre");
        // GeneTrees trees = new GeneTrees("../run/07.trueGT.cleaned");
        // GeneTrees trees = new GeneTrees("./input/gtree_11tax_est_5genes_R1.tre");
        // GeneTrees trees = new GeneTrees("./input/gtree_11tax_est_5genes_R1.tre");

        GeneTrees trees = new GeneTrees(inputFilePath);

        IMakePartition  partitionMaker = new ConsensusTreePartition(consensusFilePath, trees.taxaMap, trees);



        
        // var qfm = new QFM(trees, trees.taxa, new ConsensusTreePartition("((11,(10,((9,(8,7)),(6,5)))),4,(3,(1,2)));", trees.taxaMap));
        // var qfm = new QFM(trees, trees.taxa, new RandPartition());

        var qfm = new QFM(trees, trees.taxa, partitionMaker);
        
        var spTree = qfm.runWQFM();

        FileWriter writer = new FileWriter(outputFilePath);

        writer.write(spTree.getNewickFormat());

        writer.close();

        // System.out.println(spTree.getNewickFormat());
        // tc5(trees);


        // System.out.println(trees.taxonIdToLabel);
    }

    
}
