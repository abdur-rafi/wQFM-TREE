package src.GeneTree;

import src.ScoreCalculator.ScoreCalculatorNode;

public class Info {

    public int[] realTaxaCountTotal;

    public double[] dummyTaxaCountIndividual;
    
    public double[] dummyTaxaCountTotal;

    public double[] gains;

    public Info(int[] tcp, double[] dtci, double[] dtct, double[] gains){
        this.realTaxaCountTotal = tcp;
        this.dummyTaxaCountIndividual = dtci;
        this.dummyTaxaCountTotal = dtct;
        this.gains = gains;
    }    

    public ScoreCalculatorNode calculator;

    public Info(){
        consensusScore = 0;
    }

    public double consensusScore;

}
