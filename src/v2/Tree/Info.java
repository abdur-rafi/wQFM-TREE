package src.v2.Tree;


public class Info {

    public final int LEFT_BRANCH = 0;
    public final int RIGHT_BRANCH = 1;
    public final int TOP_BRANCH = 2;

    public double[] gainsForSubTree;

    public Branch[] branches;

    public Info(Branch[] b){
        this.branches = b;
        this.gainsForSubTree = new double[2];
    }    




    public Info(){
        consensusScore = 0;
    }

    public double consensusScore;

}
