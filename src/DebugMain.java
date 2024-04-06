package src;

import java.io.FileNotFoundException;
import java.io.IOException;

import src.DSPerLevel.BookKeepingPerLevel;
import src.DSPerLevel.BookKeepingPerLevelDC;
import src.DSPerLevel.TaxaPerLevelWithPartition;
import src.PreProcessing.DataContainer;
import src.PreProcessing.GeneTrees;
import src.PreProcessing.Preprocess;
import src.Taxon.DummyTaxon;
import src.Taxon.RealTaxon;

public class DebugMain {


    static void example() throws FileNotFoundException{
        GeneTrees trees = new GeneTrees("./input/example.tre");
        trees.readTaxaNames();
        trees.readGeneTrees(null);
        
        System.out.println(trees.geneTrees.get(0).root);

        int rtCount = 11;
        int pa = 0;
        int pb = 1 - pa;

        RealTaxon[] rt = new RealTaxon[rtCount];
        for (int i = 0; i < rtCount; i++) {
            rt[i] = trees.taxaMap.get(Integer.toString(i + 1));
        }

        RealTaxon[] rtCurrLevel = new RealTaxon[5];
        rtCurrLevel[0] = rt[0];
        rtCurrLevel[1] = rt[1];
        rtCurrLevel[2] = rt[3];
        rtCurrLevel[3] = rt[5];
        rtCurrLevel[4] = rt[6];

        DummyTaxon[] dtCurrLevel = new DummyTaxon[2];
        RealTaxon[] dt0r = new RealTaxon[1];
        dt0r[0] = rt[4];

        RealTaxon[] rtdt0d = new RealTaxon[2];
        rtdt0d[0] = rt[9];
        rtdt0d[1] = rt[10];
        DummyTaxon dt0d = new DummyTaxon(rtdt0d, new DummyTaxon[0]);

        DummyTaxon dt0 = new DummyTaxon(dt0r, new DummyTaxon[]{dt0d});
        
        RealTaxon[] dt1r = new RealTaxon[3];
        dt1r[0] = rt[2];
        dt1r[1] = rt[7];
        dt1r[2] = rt[8];

        DummyTaxon dt1 = new DummyTaxon(dt1r, new DummyTaxon[0]);

        dtCurrLevel[0] = dt0;
        dtCurrLevel[1] = dt1;

        int[] realTaxaPartition = new int[5];
        realTaxaPartition[0] = pa;
        realTaxaPartition[1] = pb;
        realTaxaPartition[2] = pa;
        realTaxaPartition[3] = pb;
        realTaxaPartition[4] = pb;

        int[] dummyTaxaPartition = new int[2];
        dummyTaxaPartition[0] = pa;
        dummyTaxaPartition[1] = pb;

        TaxaPerLevelWithPartition taxaPerLevel = new TaxaPerLevelWithPartition(rtCurrLevel, dtCurrLevel, realTaxaPartition, dummyTaxaPartition, rtCount);
        BookKeepingPerLevel book = new BookKeepingPerLevel(trees, taxaPerLevel, true);

        System.out.println("score : " + book.calculateScore());





    }

    static void cgTest1() throws FileNotFoundException{
        // String modelCond = "model.10.2000000.0.000001";
        // String inputFilePath = "../run/astral2/estimated-gene-trees/" + modelCond + "/11/gt-cleaned";
        
        GeneTrees trees = new GeneTrees("./input/custom4.tre");
        System.out.println(trees.geneTrees.get(0).root);

        int rtCount = 11;

        RealTaxon[] rt = new RealTaxon[rtCount];
        for (int i = 0; i < rtCount; i++) {
            rt[i] = trees.taxaMap.get(Integer.toString(i + 1));
        }

        // int[] realTaxaPartition = new int[rtCount];
        int pa = 0;
        int pb = 1;
        // for (int i = 0; i < rtCount; i++) {
        //     realTaxaPartition[i] = pa;
        // }
        // // realTaxaPartition[0] = pb;
        // realTaxaPartition[1] = pb;
        // // realTaxaPartition[2] = pb;
        // // realTaxaPartition[4] = pb;
        // realTaxaPartition[3] = pb;
        // // realTaxaPartition[6] = pb;
        // realTaxaPartition[7] = pb;
        // realTaxaPartition[8] = pb;

        RealTaxon[] rtb = new RealTaxon[1];
        DummyTaxon[] dtb = new DummyTaxon[3];

        rtb[0] = rt[9];
        
        RealTaxon[] dt0r = new RealTaxon[5];
        dt0r[0] = rt[0];
        dt0r[1] = rt[1];
        dt0r[2] = rt[2];
        dt0r[3] = rt[3];
        dt0r[4] = rt[10];

        dtb[0] = new DummyTaxon(dt0r, new DummyTaxon[0]);

        RealTaxon[] dt1r = new RealTaxon[3];
        dt1r[0] = rt[6];
        dt1r[1] = rt[7];
        dt1r[2] = rt[8];

        dtb[1] = new DummyTaxon(dt1r, new DummyTaxon[0]);

        RealTaxon[] dt2r = new RealTaxon[2];
        dt2r[0] = rt[4];
        dt2r[1] = rt[5];

        dtb[2] = new DummyTaxon(dt2r, new DummyTaxon[0]);

        int[] realTaxaPartition = new int[1];
        realTaxaPartition[0] = pa;

        int[] dummyTaxaPartition = new int[3];
        dummyTaxaPartition[0] = pa;
        dummyTaxaPartition[1] = pb;
        dummyTaxaPartition[2] = pb;
        
        // TaxaPerLevelWithPartition taxaPerLevel = new TaxaPerLevelWithPartition(rt, new DummyTaxon[0], realTaxaPartition, new int[0], rtCount);
        TaxaPerLevelWithPartition taxaPerLevel = new TaxaPerLevelWithPartition(rtb, dtb, realTaxaPartition, dummyTaxaPartition, rtCount);
        BookKeepingPerLevel book = new BookKeepingPerLevel(trees, taxaPerLevel, true);

        System.out.println("score : " + book.calculateScore());
        // book.swapTaxon(6, false);
        // System.out.println("score after swap : " + book.calculateScore());
        // book.swapTaxon(6, false);
        // System.out.println("score after swap again : " + book.calculateScore());

        // realTaxaPartition[9] = 0;


        // double[][] rtGains = new double[1][2];
        // double[] dtGains = new double[3];

        // System.out.println("score : " + book.calculateScoreAndGains(rtGains, dtGains));

        // System.out.println("rtGains:");
        // for (int i = 0; i < rtGains.length; i++) {
        //     System.out.println("Taxon " + (i) + ": " + rtGains[i][0] + ", " + rtGains[i][1]);
        // }
        // for( int i = 0; i < dtGains.length; i++){
        //     System.out.println("Dummy " + (i) + ": " + dtGains[i]);
        // }
    }


    static void polytomyTc2() throws FileNotFoundException{
        // String modelCond = "model.10.2000000.0.000001";
        // String inputFilePath = "../run/astral2/estimated-gene-trees/" + modelCond + "/11/gt-cleaned";
        
        GeneTrees trees = new GeneTrees("./input/custom2.tre");
        // System.out.println(trees.geneTrees.get(0).root);

        int rtCount = 11;

        RealTaxon[] rt = new RealTaxon[rtCount];
        for (int i = 0; i < rtCount; i++) {
            rt[i] = trees.taxaMap.get(Integer.toString(i));
        }
        int[] realTaxaPartition = new int[rtCount];
        int pa = 0;
        int pb = 1 - pa;
        for (int i = 0; i < rtCount; i++) {
            realTaxaPartition[i] = pa;
        }
        realTaxaPartition[0] = pb;
        realTaxaPartition[1] = pb;
        realTaxaPartition[2] = pb;
        realTaxaPartition[3] = pb;
        realTaxaPartition[4] = pb;
        realTaxaPartition[5] = pb;
        // realTaxaPartition[7] = pb;
        // realTaxaPartition[8] = pb;

        
        TaxaPerLevelWithPartition taxaPerLevel = new TaxaPerLevelWithPartition(rt, new DummyTaxon[0], realTaxaPartition, new int[0], rtCount);
        BookKeepingPerLevel book = new BookKeepingPerLevel(trees, taxaPerLevel, true);

        // System.out.println("score before swap : " + book.calculateScore());
        // book.swapTaxon(6, false);
        // System.out.println("score after swap : " + book.calculateScore());
        // book.swapTaxon(6, false);
        // System.out.println("score after swap again : " + book.calculateScore());

        // realTaxaPartition[9] = 0;


        double[][] rtGains = new double[rtCount][2];
        double[] dtGains = new double[0];

        System.out.println("score : " + book.calculateScoreAndGains(rtGains, dtGains));

        System.out.println("rtGains:");
        for (int i = 0; i < rtGains.length; i++) {
            System.out.println("Taxon " + (i) + ": " + rtGains[i][0] + ", " + rtGains[i][1]);
        }
    }

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

        int pa = 0;
        int pb = 1 - pa;

        int[] realTaxaPartition = new int[8];
        realTaxaPartition[0] = pa;
        realTaxaPartition[1] = pa;
        realTaxaPartition[2] = pa;
        realTaxaPartition[3] = pa;
        realTaxaPartition[4] = pa;
        realTaxaPartition[5] = pb;        
        realTaxaPartition[6] = pb;        
        realTaxaPartition[7] = pb;        

        TaxaPerLevelWithPartition taxa = new TaxaPerLevelWithPartition(rt, new DummyTaxon[0], realTaxaPartition, new int[0], 8);
        BookKeepingPerLevel bookKeepingPerLevel = new BookKeepingPerLevel(trees, taxa, Config.ALLOW_SINGLETON);
        double[][] rtGains = new double[8][2];
        double[] dtGains = new double[0];
        // System.out.println("score : " + bookKeepingPerLevel.calculateScore());

        System.out.println("score : " + bookKeepingPerLevel.calculateScoreAndGains(rtGains, dtGains));
        System.out.println("rtGains:");
        for (int i = 0; i < rtGains.length; i++) {
            System.out.println("Taxon " + (i+1) + ": " + rtGains[i][0] + ", " + rtGains[i][1]);
        }
    }

    public void proTC() throws FileNotFoundException{
        String inputFilePath = "./input/t.txt";
        Preprocess.PreprocessReturnType ret = Preprocess.preprocess(inputFilePath);

    }
    
    public static void main(String[] args) throws IOException {
        // cgTest1();
        // GeneTrees trees = new GeneTrees("./input/gtree_11tax_est_5genes_R1.tre");
        // trees.readTaxaNames();
        // trees.readGeneTrees(null);

        // tc1();
        tc3();
        // example();
        // polytomyTc2();
        // polytomyTc2();
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
        // GeneTrees trees = new GeneTrees("./input/gtree_11tax_est_5genes_R1.tre");
        // tc4(trees);


        // System.out.println(trees.taxonIdToLabel);
    }

    public static void tc4(GeneTrees trees){
        RealTaxon[] rt = new RealTaxon[11];
        rt[0] = trees.taxaMap.get("1");
        rt[1] = trees.taxaMap.get("2");
        rt[2] = trees.taxaMap.get("3");
        rt[3] = trees.taxaMap.get("4");
        rt[4] = trees.taxaMap.get("5");
        rt[5] = trees.taxaMap.get("6");
        rt[6] = trees.taxaMap.get("7");
        rt[7] = trees.taxaMap.get("8");
        rt[8] = trees.taxaMap.get("9");
        rt[9] = trees.taxaMap.get("10");
        rt[10] = trees.taxaMap.get("11");
        


        int[] realTaxaPartition = new int[11];
        realTaxaPartition[0] = 0;
        realTaxaPartition[1] = 0;
        realTaxaPartition[2] = 0;
        realTaxaPartition[3] = 0;
        realTaxaPartition[4] = 0;
        realTaxaPartition[5] = 1;
        realTaxaPartition[6] = 1;
        realTaxaPartition[7] = 1;
        realTaxaPartition[8] = 1;
        realTaxaPartition[9] = 1;
        realTaxaPartition[10] = 0;


        int[] dummyTaxaPartition = new int[1];
        dummyTaxaPartition[0] = 1;

        TaxaPerLevelWithPartition taxa = new TaxaPerLevelWithPartition(rt, new DummyTaxon[0], realTaxaPartition, dummyTaxaPartition, 11);

        BookKeepingPerLevel bookKeepingPerLevel = new BookKeepingPerLevel(trees, taxa, Config.ALLOW_SINGLETON);

        double[][] rtGains = new double[11][2];
        double[] dtGains = new double[0];
        // System.out.println("score : " + bookKeepingPerLevel.calculateScore());

        System.out.println("score : " + bookKeepingPerLevel.calculateScoreAndGains(rtGains, dtGains));
        for(int i = 0; i < rtGains.length; ++i){
            System.out.println("Taxon " + (i+1) + ": " + rtGains[i][0] + ", " + rtGains[i][1]);
        }
        for(int i = 0; i < dtGains.length; ++i){
            System.out.println("Dummy " + (i+1) + ": " + dtGains[i]);
        }

    }


    // 30 - 0 ?
    public static void tc1() throws FileNotFoundException{
        GeneTrees trees = new GeneTrees("./input/gtree_11tax_est_5genes_R1.tre");
        trees.readTaxaNames();
        DataContainer dc = trees.readGeneTrees(null);

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
        dummyTaxaPartition[0] = 1;

        TaxaPerLevelWithPartition taxa = new TaxaPerLevelWithPartition(rt, dt, realTaxaPartition, dummyTaxaPartition, 11);

        BookKeepingPerLevel bookKeepingPerLevel = new BookKeepingPerLevel(trees, taxa, Config.ALLOW_SINGLETON);

        double[][] rtGains = new double[5][2];
        double[] dtGains = new double[1];
        // System.out.println("score : " + bookKeepingPerLevel.calculateScore());

        bookKeepingPerLevel.swapTaxon(1, false);
        // bookKeepingPerLevel.swapTaxon(0, true);

        System.out.println("score : " + bookKeepingPerLevel.calculateScoreAndGains(rtGains, dtGains));
        for(int i = 0; i < rtGains.length; ++i){
            System.out.println("Taxon " + (i+1) + ": " + rtGains[i][0] + ", " + rtGains[i][1]);
        }
        for(int i = 0; i < dtGains.length; ++i){
            System.out.println("Dummy " + (i+1) + ": " + dtGains[i]);
        }

        bookKeepingPerLevel.swapTaxon(1, false);
        // bookKeepingPerLevel.swapTaxon(0, true);

        BookKeepingPerLevelDC bookDc = new BookKeepingPerLevelDC(dc, taxa);

        rtGains = new double[5][2];
        dtGains = new double[1];
        

        bookDc.swapTaxon(1, false);
        // bookDc.swapTaxon(0, true);
        System.out.println("score : " + bookDc.calculateScoreAndGains(rtGains, dtGains));

        for(int i = 0; i < rtGains.length; ++i){
            System.out.println("Taxon " + (i+1) + ": " + rtGains[i][0] + ", " + rtGains[i][1]);
        }

        for(int i = 0; i < dtGains.length; ++i){
            System.out.println("Dummy " + (i+1) + ": " + dtGains[i]);
        }

    }

    // -1
    public static void tc2() throws FileNotFoundException{
        GeneTrees trees = new GeneTrees("./input/gtree_11tax_est_5genes_R1.tre");
        trees.readTaxaNames();
        DataContainer dc = trees.readGeneTrees(null);

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

        // System.out.println( "scorea  : " + bookKeepingPerLevel.calculateScore());

        System.out.println( "score : " + bookKeepingPerLevel.calculateScoreAndGains(rtGains, dtGains));

        for(int i = 0; i < rtGains.length; ++i){
            System.out.println("Taxon " + (i+1) + ": " + rtGains[i][0] + ", " + rtGains[i][1]);
        }
        for(int i = 0; i < dtGains.length; ++i){
            System.out.println("Dummy " + (i+1) + ": " + dtGains[i]);
        }

        rtGains = new double[4][2];
        dtGains = new double[1];

        BookKeepingPerLevelDC bookDc = new BookKeepingPerLevelDC(dc, taxa);

        System.out.println( "score : " + bookDc.calculateScoreAndGains(rtGains, dtGains));

        for(int i = 0; i < rtGains.length; ++i){
            System.out.println("Taxon " + (i+1) + ": " + rtGains[i][0] + ", " + rtGains[i][1]);
        }

        for(int i = 0; i < dtGains.length; ++i){
            System.out.println("Dummy " + (i+1) + ": " + dtGains[i]);
        }

    }


    public static void tc3() throws FileNotFoundException{
        GeneTrees trees = new GeneTrees("./input/gtree_11tax_est_5genes_R1.tre");
        trees.readTaxaNames();
        DataContainer dc = trees.readGeneTrees(null);

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
        for(int i = 0; i < rtGains.length; ++i){
            System.out.println("Taxon " + (i+1) + ": " + rtGains[i][0] + ", " + rtGains[i][1]);
        }
        for(int i = 0; i < dtGains.length; ++i){
            System.out.println("Dummy " + (i+1) + ": " + dtGains[i]);
        }

        rtGains = new double[2][2];
        dtGains = new double[2];

        BookKeepingPerLevelDC bookDc = new BookKeepingPerLevelDC(dc, taxa);

        System.out.println( "score : " + bookDc.calculateScoreAndGains(rtGains, dtGains));
        for(int i = 0; i < rtGains.length; ++i){
            System.out.println("Taxon " + (i+1) + ": " + rtGains[i][0] + ", " + rtGains[i][1]);
        }
        for(int i = 0; i < dtGains.length; ++i){
            System.out.println("Dummy " + (i+1) + ": " + dtGains[i]);
        }
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
