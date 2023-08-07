package wqfm.dsGT;

import wqfm.dsGT.GeneTree.pair;

public class Info {

    int pACount;
    int pBCount;

    boolean[] reachableDummyTaxa;

    int abovepACount;
    int abovepBCount;
    boolean[] reachableDummyTaxaFromAbove;


    pair gainAtoB;
    pair gainBtoA;

    public Info(int a, int b){
        pACount = a;
        pBCount = b;
    }

    // public Info(int a, int b, int dummyTaxaCount){
    //     this(a, b);
    //     abovepADummyTaxa = new boolean[dummyTaxaCount];   
    //     abovepBDummyTaxa = new boolean[dummyTaxaCount];   
    // }

    public void setAboveCount(int a, int b){
        abovepACount = a;
        abovepBCount = b;
    }

    public void setGain(pair aToB, pair bToA){
        gainAtoB = aToB;
        gainBtoA = bToA;
    }

    public void setDummyTaxaFlags(boolean[] a){
        reachableDummyTaxa = a;
    }

    public void setReachableDummyTaxaFromAbove(boolean[] a){
        this.reachableDummyTaxaFromAbove = a;
    }

    
}
