package src.PreProcessing;

import src.Tree.Branch;

public class Data {
    
    public double[] gainsForSubTree;
    public Branch branch;

    public Data(Branch b){
        this.gainsForSubTree = new double[2];
        this.branch = b;
    }

    public void resetBookkeeping(){
        this.gainsForSubTree[0] = 0;
        this.gainsForSubTree[1] = 0;
    }
    
}
