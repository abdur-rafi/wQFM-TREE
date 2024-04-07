package src;

import java.io.FileNotFoundException;

import src.DSPerLevel.BookKeepingPerLevelDC;
import src.DSPerLevel.TaxaPerLevelWithPartition;
import src.PreProcessing.Preprocess;
import src.Taxon.DummyTaxon;
import src.Taxon.RealTaxon;

public class TestSQ {

    public static void proTC() throws FileNotFoundException{
        String inputFilePath = "./input/t.txt";
        // String inputFilePath = "./input/n10ProRooted.cleaned";
        Preprocess.PreprocessReturnType ret = Preprocess.preprocess(inputFilePath);

        RealTaxon[] rts = ret.dc.taxa;
        DummyTaxon[] dts = new DummyTaxon[0];

        int[] rtp = new int[rts.length];

        int pa = 0;
        int pb = 1 - pa;

        rtp[0] = pb;
        rtp[1] = pb;
        // rtp[3] = pb;
        rtp[4] = pb;
        // rtp[5] = pb;
        rtp[6] = pb;

        int[] dtp = new int[0];

        TaxaPerLevelWithPartition taxa = new TaxaPerLevelWithPartition(
            rts,
            dts,
            rtp,
            dtp,
            rts.length
        );

        BookKeepingPerLevelDC book = new BookKeepingPerLevelDC(
            ret.dc, taxa);
        
        double[][] rtGains = new double[rts.length][2];
        double[] dtGains = new double[0];

        double score = book.calculateScoreAndGains(rtGains, dtGains);
        // double score = book.calculateScore();
        // print gains
        
        System.out.println("Score : " + score);
        for(int i = 0; i < rtGains.length; ++i){
            System.out.println("Real Taxon " + i + " : " + rtGains[i][rtp[i]]);
        }
        for(int i = 0; i < dtGains.length; ++i){
            System.out.println("Dummy Taxon " + i + " : " + dtGains[i]);
        }

        int trId = 4;

        book.transferRealTaxon(trId);

        score = book.calculateScoreAndGains(rtGains, dtGains);
        System.out.println("Score After Transfer : " + score);

        book.transferRealTaxon(trId);

        score = book.calculateScoreAndGains(rtGains, dtGains);

        System.out.println("Score After Transfer back : " + score);

    }

    public static void main(String[] args) throws FileNotFoundException {
        proTC2();
        // System.out.println("Hello, World!");
    }

    public static void proTC2() throws FileNotFoundException{
        String inputFilePath = "./input/t2.txt";
        // String inputFilePath = "./input/n10ProRooted.cleaned";
        Preprocess.PreprocessReturnType ret = Preprocess.preprocess(inputFilePath);

        RealTaxon[] rts = ret.dc.taxa;
        DummyTaxon[] dts = new DummyTaxon[0];

        int[] rtp = new int[rts.length];

        int pa = 0;
        int pb = 1 - pa;

        rtp[0] = pb;
        rtp[1] = pa;
        rtp[2] = pa;
        rtp[3] = pb;
        rtp[4] = pa;
        // rtp[4] = pb;
        // // rtp[5] = pb;
        // rtp[6] = pb;

        // 0, 2, 3, 4, 1

        int[] dtp = new int[0];

        TaxaPerLevelWithPartition taxa = new TaxaPerLevelWithPartition(
            rts,
            dts,
            rtp,
            dtp,
            rts.length
        );

        BookKeepingPerLevelDC book = new BookKeepingPerLevelDC(
            ret.dc, taxa);
        
        double[][] rtGains = new double[rts.length][2];
        double[] dtGains = new double[0];

        // book.transferRealTaxon(0);
        // book.transferRealTaxon(2);
        // book.transferRealTaxon(3);
        // book.transferRealTaxon(4);

        // for(int i = 0; i < rts.length; ++i){
        //     System.out.println("Real Taxon " + i + " : " + rtp[i]);
        // }


        double score = book.calculateScoreAndGains(rtGains, dtGains);
        // double score = book.calculateScore();
        // print gains
        
        System.out.println("Score : " + score);
        for(int i = 0; i < rtGains.length; ++i){
            System.out.println("Real Taxon " + i + " : " + rtGains[i][rtp[i]]);
        }
        for(int i = 0; i < dtGains.length; ++i){
            System.out.println("Dummy Taxon " + i + " : " + dtGains[i]);
        }

        book.transferRealTaxon(1);
        // book.transferRealTaxon(2);
        // book.transferRealTaxon(3);
        // book.transferRealTaxon(4);
        // book.transferRealTaxon(1);

        // int trId = 0;

        // book.transferRealTaxon(trId);

        // score = book.calculateScoreAndGains(rtGains, dtGains);
        // System.out.println("Score After Transfer : " + score);

        // book.transferRealTaxon(trId);

        score = book.calculateScoreAndGains(rtGains, dtGains);

        System.out.println("Score After Transfer back : " + score);

    }
    
    
}
