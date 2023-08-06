package wqfm.dsGT;

import wqfm.dsGT.GeneTree.pair;

public class Info {

    int pACount;
    int pBCount;

    int abovepACount;
    int abovepBCount;

    pair gainAtoB;
    pair gainBtoA;

    public Info(int a, int b){
        pACount = a;
        pBCount = b;
    }

    public void setAboveCount(int a, int b){
        abovepACount = a;
        abovepBCount = b;
    }

    public void setGain(pair aToB, pair bToA){
        gainAtoB = aToB;
        gainBtoA = bToA;
    }

    
}
