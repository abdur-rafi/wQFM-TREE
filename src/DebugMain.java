package src;

import java.io.FileNotFoundException;
import java.io.IOException;

import src.DSPerLevel.BookKeepingPerLevel;
import src.DSPerLevel.TaxaPerLevelWithPartition;
import src.PreProcessing.GeneTrees;
import src.Taxon.DummyTaxon;
import src.Taxon.RealTaxon;

public class DebugMain {

    static void polytomyTc1() throws FileNotFoundException{
        GeneTrees trees = new GeneTrees("./input/custom.tre");
        RealTaxon[] rt = new RealTaxon[8];
        rt[0] = trees.taxaMap.get("1");
        rt[1] = trees.taxaMap.get("2");
        rt[2] = trees.taxaMap.get("3");
        rt[3] = trees.taxaMap.get("4");
        rt[4] = trees.taxaMap.get("5");
        rt[5] = trees.taxaMap.get("6");
        rt[6] = trees.taxaMap.get("7");
        rt[7] = trees.taxaMap.get("8");


        int[] realTaxaPartition = new int[8];
        realTaxaPartition[0] = 0;
        realTaxaPartition[1] = 0;
        realTaxaPartition[2] = 0;
        realTaxaPartition[3] = 0;
        realTaxaPartition[4] = 1;
        realTaxaPartition[5] = 1;        
        realTaxaPartition[6] = 1;        
        realTaxaPartition[7] = 1;        

        TaxaPerLevelWithPartition taxa = new TaxaPerLevelWithPartition(rt, new DummyTaxon[0], realTaxaPartition, new int[0], 8);
        BookKeepingPerLevel bookKeepingPerLevel = new BookKeepingPerLevel(trees, taxa, Config.ALLOW_SINGLETON);
        double[][] rtGains = new double[8][2];
        double[] dtGains = new double[0];
        // System.out.println("score : " + bookKeepingPerLevel.calculateScore());

        System.out.println("score : " + bookKeepingPerLevel.calculateScoreAndGains(rtGains, dtGains));
    }
    
    public static void main(String[] args) throws IOException {
        // polytomyTc1();
        // new GeneTrees("../run/07.trueGT.cleaned");
        // new GeneTrees("./input/gtree_11tax_est_5genes_R1.tre");
        // new GeneTrees("./input/custom.tre");

        // GeneTrees trees = new GeneTrees("../run/15-taxon/100gene-100bp/R1/all_gt_cleaned.tre");
        // GeneTrees trees = new GeneTrees("../run/07.trueGT.cleaned");
        // GeneTrees trees = new GeneTrees("./input/gtree_11tax_est_5genes_R1.tre");


        // IMakePartition  partitionMaker = new ConsensusTreePartition("./input/5genes.raxml.consensusTreeMRE.cleaned", trees.taxaMap, trees);



        
        // var qfm = new QFM(trees, trees.taxa, new ConsensusTreePartition("((11,(10,((9,(8,7)),(6,5)))),4,(3,(1,2)));", trees.taxaMap));
        // var qfm = new QFM(trees, trees.taxa, new RandPartition());

        // var qfm = new QFM(trees, trees.taxa, partitionMaker);
        
        // var spTree = qfm.runWQFM();

        // FileWriter writer = new FileWriter("outsrc.src.tre");

        // writer.write(spTree.getNewickFormat());

        // writer.close();

        // System.out.println(spTree.getNewickFormat());
        GeneTrees trees = new GeneTrees("./input/gtree_11tax_est_5genes_R1.tre");
        tc3(trees);


        // System.out.println(trees.taxonIdToLabel);
    }

    // 30 - 0 ?
    public static void tc1(GeneTrees trees){
        RealTaxon[] rt = new RealTaxon[5];
        rt[0] = trees.taxaMap.get("1");
        rt[1] = trees.taxaMap.get("2");
        rt[2] = trees.taxaMap.get("3");
        rt[3] = trees.taxaMap.get("4");
        rt[4] = trees.taxaMap.get("11");
        

        RealTaxon[] dt0r = new RealTaxon[6];
        dt0r[0] = trees.taxaMap.get("5");
        dt0r[1] = trees.taxaMap.get("6");
        dt0r[2] = trees.taxaMap.get("7");
        dt0r[3] = trees.taxaMap.get("8");
        dt0r[4] = trees.taxaMap.get("9");
        dt0r[5] = trees.taxaMap.get("10");
        

        DummyTaxon[] dt = new DummyTaxon[1];
        dt[0] = new DummyTaxon(dt0r, new DummyTaxon[0]);

        int[] realTaxaPartition = new int[5];
        realTaxaPartition[0] = 0;
        realTaxaPartition[1] = 0;
        realTaxaPartition[2] = 0;
        realTaxaPartition[3] = 0;
        realTaxaPartition[4] = 1;

        int[] dummyTaxaPartition = new int[1];
        dummyTaxaPartition[0] = 1;

        TaxaPerLevelWithPartition taxa = new TaxaPerLevelWithPartition(rt, dt, realTaxaPartition, dummyTaxaPartition, 11);

        BookKeepingPerLevel bookKeepingPerLevel = new BookKeepingPerLevel(trees, taxa, Config.ALLOW_SINGLETON);

        double[][] rtGains = new double[5][2];
        double[] dtGains = new double[1];

        System.out.println("score : " + bookKeepingPerLevel.calculateScoreAndGains(rtGains, dtGains));
    }


    public static void tc2(GeneTrees trees){
        RealTaxon[] rt = new RealTaxon[4];
        rt[0] = trees.taxaMap.get("1");
        rt[1] = trees.taxaMap.get("2");
        rt[2] = trees.taxaMap.get("3");
        rt[3] = trees.taxaMap.get("4");
        
        // 11, {5, 6, 7, 8, 9, 10}
        RealTaxon[] dt0r = new RealTaxon[6];
        dt0r[0] = trees.taxaMap.get("5");
        dt0r[1] = trees.taxaMap.get("6");
        dt0r[2] = trees.taxaMap.get("7");
        dt0r[3] = trees.taxaMap.get("8");
        dt0r[4] = trees.taxaMap.get("9");
        dt0r[5] = trees.taxaMap.get("10");

        DummyTaxon internalDT = new DummyTaxon(dt0r, new DummyTaxon[0]);

        dt0r = new RealTaxon[1];
        dt0r[0] = trees.taxaMap.get("11");

        DummyTaxon[] a = new DummyTaxon[1];
        a[0] = internalDT;

        DummyTaxon dt0 = new DummyTaxon(dt0r,a);

        a = new DummyTaxon[1];
        a[0] = dt0;


        int[] realTaxaPartition = new int[4];
        realTaxaPartition[0] = 0;
        realTaxaPartition[1] = 0;
        realTaxaPartition[2] = 1;
        realTaxaPartition[3] = 1;

        int[] dummyTaxaPartition = new int[1];
        dummyTaxaPartition[0] = 1;


        TaxaPerLevelWithPartition taxa = new TaxaPerLevelWithPartition(rt, a, realTaxaPartition, dummyTaxaPartition, 11);

        BookKeepingPerLevel bookKeepingPerLevel = new BookKeepingPerLevel(trees, taxa, Config.ALLOW_SINGLETON);


        double[][] rtGains = new double[4][2];
        double[] dtGains = new double[1];


        System.out.println( "score : " + bookKeepingPerLevel.calculateScoreAndGains(rtGains, dtGains));
    }


    public static void tc3(GeneTrees trees){
        RealTaxon[] rt = new RealTaxon[2];
        rt[0] = trees.taxaMap.get("3");
        rt[1] = trees.taxaMap.get("4");
        
        // 11, {5, 6, 7, 8, 9, 10}
        RealTaxon[] dt0r = new RealTaxon[6];
        dt0r[0] = trees.taxaMap.get("5");
        dt0r[1] = trees.taxaMap.get("6");
        dt0r[2] = trees.taxaMap.get("7");
        dt0r[3] = trees.taxaMap.get("8");
        dt0r[4] = trees.taxaMap.get("9");
        dt0r[5] = trees.taxaMap.get("10");

        DummyTaxon internalDT = new DummyTaxon(dt0r, new DummyTaxon[0]);

        dt0r = new RealTaxon[1];
        dt0r[0] = trees.taxaMap.get("11");

        DummyTaxon[] a = new DummyTaxon[1];
        a[0] = internalDT;

        DummyTaxon dt0 = new DummyTaxon(dt0r,a);

        RealTaxon[] dt1r = new RealTaxon[2];
        dt1r[0] = trees.taxaMap.get("1");
        dt1r[1] = trees.taxaMap.get("2");

        DummyTaxon dt1 = new DummyTaxon(dt1r, new DummyTaxon[0]);

        a = new DummyTaxon[2];
        a[0] = dt0;
        a[1] = dt1;

        int[] realTaxaPartition = new int[2];
        realTaxaPartition[0] = 0;
        realTaxaPartition[1] = 0;

        int[] dummyTaxaPartition = new int[2];
        dummyTaxaPartition[0] = 1;
        dummyTaxaPartition[1] = 1;


        TaxaPerLevelWithPartition taxa = new TaxaPerLevelWithPartition(rt, a, realTaxaPartition, dummyTaxaPartition, 11);

        BookKeepingPerLevel bookKeepingPerLevel = new BookKeepingPerLevel(trees, taxa, Config.ALLOW_SINGLETON);


        double[][] rtGains = new double[2][2];
        double[] dtGains = new double[2];


        System.out.println(bookKeepingPerLevel.calculateScoreAndGains(rtGains, dtGains));
    }


    public static void tc5(GeneTrees trees){
        RealTaxon[] rt = new RealTaxon[5];
        rt[0] = trees.taxaMap.get("1");
        rt[1] = trees.taxaMap.get("2");
        rt[2] = trees.taxaMap.get("3");
        rt[3] = trees.taxaMap.get("4");
        rt[4] = trees.taxaMap.get("11");
        

        RealTaxon[] dt0r = new RealTaxon[6];
        dt0r[0] = trees.taxaMap.get("5");
        dt0r[1] = trees.taxaMap.get("6");
        dt0r[2] = trees.taxaMap.get("7");
        dt0r[3] = trees.taxaMap.get("8");
        dt0r[4] = trees.taxaMap.get("9");
        dt0r[5] = trees.taxaMap.get("10");
        

        DummyTaxon[] dt = new DummyTaxon[1];
        dt[0] = new DummyTaxon(dt0r, new DummyTaxon[0]);

        int[] realTaxaPartition = new int[5];
        realTaxaPartition[0] = 0;
        realTaxaPartition[1] = 0;
        realTaxaPartition[2] = 0;
        realTaxaPartition[3] = 1;
        realTaxaPartition[4] = 1;

        int[] dummyTaxaPartition = new int[1];
        dummyTaxaPartition[0] = 0;

        TaxaPerLevelWithPartition taxa = new TaxaPerLevelWithPartition(rt, dt, realTaxaPartition, dummyTaxaPartition, 11);

        BookKeepingPerLevel bookKeepingPerLevel = new BookKeepingPerLevel(trees, taxa, Config.ALLOW_SINGLETON);


        double[][] rtGains = new double[5][2];
        double[] dtGains = new double[1];


        bookKeepingPerLevel.calculateScoreAndGains(rtGains, dtGains);

        // bookKeepingPerLevel.swapRealTaxon(3);
        // bookKeepingPerLevel.calculateScoreAndGains();
        // bookKeepingPerLevel.swapRealTaxon(3);   
        // bookKeepingPerLevel.calculateScoreAndGains();

        // bookKeepingPerLevel.swapRealTaxon(3);
        // bookKeepingPerLevel.calculateScoreAndGains();

        rtGains = new double[5][2];
        dtGains = new double[1];

        bookKeepingPerLevel.swapTaxon(0, true);  
        bookKeepingPerLevel.calculateScoreAndGains(rtGains, dtGains);

        rtGains = new double[5][2];
        dtGains = new double[1];


        bookKeepingPerLevel.swapTaxon(0, true);  
        bookKeepingPerLevel.calculateScoreAndGains(rtGains, dtGains);
    }


    public static void tc6(GeneTrees trees){
        RealTaxon[] rt = new RealTaxon[4];
        rt[0] = trees.taxaMap.get("1");
        rt[1] = trees.taxaMap.get("2");
        rt[2] = trees.taxaMap.get("3");
        rt[3] = trees.taxaMap.get("4");
        
        // 11, {5, 6, 7, 8, 9, 10}
        RealTaxon[] dt0r = new RealTaxon[6];
        dt0r[0] = trees.taxaMap.get("5");
        dt0r[1] = trees.taxaMap.get("6");
        dt0r[2] = trees.taxaMap.get("7");
        dt0r[3] = trees.taxaMap.get("8");
        dt0r[4] = trees.taxaMap.get("9");
        dt0r[5] = trees.taxaMap.get("10");

        DummyTaxon internalDT = new DummyTaxon(dt0r, new DummyTaxon[0]);

        dt0r = new RealTaxon[1];
        dt0r[0] = trees.taxaMap.get("11");

        DummyTaxon[] a = new DummyTaxon[1];
        a[0] = internalDT;

        DummyTaxon dt0 = new DummyTaxon(dt0r,a);

        a = new DummyTaxon[1];
        a[0] = dt0;


        int[] realTaxaPartition = new int[4];
        realTaxaPartition[0] = 0;
        realTaxaPartition[1] = 0;
        realTaxaPartition[2] = 0;
        realTaxaPartition[3] = 1;

        int[] dummyTaxaPartition = new int[1];
        dummyTaxaPartition[0] = 1;


        TaxaPerLevelWithPartition taxa = new TaxaPerLevelWithPartition(rt, a, realTaxaPartition, dummyTaxaPartition, 11);

        BookKeepingPerLevel bookKeepingPerLevel = new BookKeepingPerLevel(trees, taxa, Config.ALLOW_SINGLETON);

        double[][] rtGains = new double[4][2];
        double[] dtGains = new double[1];
        

        bookKeepingPerLevel.calculateScoreAndGains(rtGains, dtGains);
        
        rtGains = new double[2][2];
        dtGains = new double[2];
        
        bookKeepingPerLevel.swapTaxon(0, false);
        bookKeepingPerLevel.calculateScoreAndGains(rtGains, dtGains);
    }

}
