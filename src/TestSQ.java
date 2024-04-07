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
    }

    public static void main(String[] args) throws FileNotFoundException {
        proTC();
        // System.out.println("Hello, World!");
    }
    
}
