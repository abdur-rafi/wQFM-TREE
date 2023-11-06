package src.v2;

import java.io.FileNotFoundException;

import src.v2.DSPerLevel.BookKeepingPerLevel;
import src.v2.DSPerLevel.TaxaPerLevelWithPartition;
import src.v2.PreProcessing.GeneTrees;
import src.v2.Taxon.DummyTaxon;
import src.v2.Taxon.RealTaxon;

public class main {

    public static void main(String[] args) throws FileNotFoundException {
        // GeneTrees trees = new GeneTrees("../run/15-taxon/100gene-100bp/R1/all_gt_cleaned.tre");
        // GeneTrees trees = new GeneTrees("../run/07.trueGT.cleaned");
        GeneTrees trees = new GeneTrees("./input/gtree_11tax_est_5genes_R1.tre");
        tc5(trees);


        // System.out.println(trees.taxonIdToLabel);
    }

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

        short[] realTaxaPartition = new short[5];
        realTaxaPartition[0] = 0;
        realTaxaPartition[1] = 0;
        realTaxaPartition[2] = 0;
        realTaxaPartition[3] = 0;
        realTaxaPartition[4] = 1;

        short[] dummyTaxaPartition = new short[1];
        dummyTaxaPartition[0] = 1;

        TaxaPerLevelWithPartition taxa = new TaxaPerLevelWithPartition(rt, dt, realTaxaPartition, dummyTaxaPartition, 11);

        BookKeepingPerLevel bookKeepingPerLevel = new BookKeepingPerLevel(trees, taxa);

        System.out.println(bookKeepingPerLevel.calculateScoreAndGains());
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


        short[] realTaxaPartition = new short[4];
        realTaxaPartition[0] = 0;
        realTaxaPartition[1] = 0;
        realTaxaPartition[2] = 1;
        realTaxaPartition[3] = 1;

        short[] dummyTaxaPartition = new short[1];
        dummyTaxaPartition[0] = 1;


        TaxaPerLevelWithPartition taxa = new TaxaPerLevelWithPartition(rt, a, realTaxaPartition, dummyTaxaPartition, 11);

        BookKeepingPerLevel bookKeepingPerLevel = new BookKeepingPerLevel(trees, taxa);

        bookKeepingPerLevel.calculateScoreAndGains();
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

        short[] realTaxaPartition = new short[2];
        realTaxaPartition[0] = 0;
        realTaxaPartition[1] = 0;

        short[] dummyTaxaPartition = new short[2];
        dummyTaxaPartition[0] = 1;
        dummyTaxaPartition[1] = 1;


        TaxaPerLevelWithPartition taxa = new TaxaPerLevelWithPartition(rt, a, realTaxaPartition, dummyTaxaPartition, 11);

        BookKeepingPerLevel bookKeepingPerLevel = new BookKeepingPerLevel(trees, taxa);

        bookKeepingPerLevel.calculateScoreAndGains();
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

        short[] realTaxaPartition = new short[5];
        realTaxaPartition[0] = 0;
        realTaxaPartition[1] = 0;
        realTaxaPartition[2] = 0;
        realTaxaPartition[3] = 1;
        realTaxaPartition[4] = 1;

        short[] dummyTaxaPartition = new short[1];
        dummyTaxaPartition[0] = 0;

        TaxaPerLevelWithPartition taxa = new TaxaPerLevelWithPartition(rt, dt, realTaxaPartition, dummyTaxaPartition, 11);

        BookKeepingPerLevel bookKeepingPerLevel = new BookKeepingPerLevel(trees, taxa);

        bookKeepingPerLevel.calculateScoreAndGains();

        // bookKeepingPerLevel.swapRealTaxon(3);
        // bookKeepingPerLevel.calculateScoreAndGains();
        // bookKeepingPerLevel.swapRealTaxon(3);   
        // bookKeepingPerLevel.calculateScoreAndGains();

        // bookKeepingPerLevel.swapRealTaxon(3);
        // bookKeepingPerLevel.calculateScoreAndGains();
        
        bookKeepingPerLevel.swapDummyTaxon(0);  
        bookKeepingPerLevel.calculateScoreAndGains();
        bookKeepingPerLevel.swapDummyTaxon(0);  
        bookKeepingPerLevel.calculateScoreAndGains();
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


        short[] realTaxaPartition = new short[4];
        realTaxaPartition[0] = 0;
        realTaxaPartition[1] = 0;
        realTaxaPartition[2] = 0;
        realTaxaPartition[3] = 1;

        short[] dummyTaxaPartition = new short[1];
        dummyTaxaPartition[0] = 1;


        TaxaPerLevelWithPartition taxa = new TaxaPerLevelWithPartition(rt, a, realTaxaPartition, dummyTaxaPartition, 11);

        BookKeepingPerLevel bookKeepingPerLevel = new BookKeepingPerLevel(trees, taxa);

        bookKeepingPerLevel.calculateScoreAndGains();
        bookKeepingPerLevel.swapRealTaxon(0);
        bookKeepingPerLevel.calculateScoreAndGains();
    }

    
}
