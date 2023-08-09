package wqfm.dsGT;


public class Info {

    int[] realTaxaCountTotal;

    int[] dummyTaxaCountIndividual;
    
    int[] dummyTaxaCountTotal;

    int[] gains;

    public Info(int[] tcp, int[] dtci, int[] dtct, int[] gains){
        this.realTaxaCountTotal = tcp;
        this.dummyTaxaCountIndividual = dtci;
        this.dummyTaxaCountTotal = dtct;
        this.gains = gains;
    }    

    ScoreCalculatorNode calculator;
}
