package src.v2.Tree;

import src.v2.ScoreCalculator.ScoreCalculatorNode;

public class Info {

    public final int LEFT_BRANCH = 0;
    public final int RIGHT_BRANCH = 1;
    public final int TOP_BRANCH = 2;
    

    public double[] gainsForSubTree;

    public Branch[] branches;

    public ScoreCalculatorNode scoreCalculator;


    public Info(Branch[] b){
        this.branches = b;
        this.gainsForSubTree = new double[2];
        this.scoreCalculator = null;
    }    




    // for consensus tree
    public Info(){
        
    }



    // for constructing species tree
    public int dummyTaxonId;

    public Info(int dummyTaxonId){
        this.dummyTaxonId = dummyTaxonId;
    }

}