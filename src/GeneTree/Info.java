package src.GeneTree;

import src.ScoreCalculator.ScoreCalculatorNode;

public class Info {

    public int[] realTaxaCountTotal;

    public int[] dummyTaxaCountIndividual;
    
    public int[] dummyTaxaCountTotal;

    public int[] gains;

    public Info(int[] tcp, int[] dtci, int[] dtct, int[] gains){
        this.realTaxaCountTotal = tcp;
        this.dummyTaxaCountIndividual = dtci;
        this.dummyTaxaCountTotal = dtct;
        this.gains = gains;
    }    

    public ScoreCalculatorNode calculator;
}
