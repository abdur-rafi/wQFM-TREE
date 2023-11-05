package src.v2.Tree;

public class Branch {
    public int[] realTaxaCounts;
    public double[] dummyTaxaWeightSums;
    public double[] dummyTaxaWeightsIndividual;
    public double[] totalTaxaCounts;


    public Branch(int[] rtc, double[] dtci, double[] dtct) {
        this.realTaxaCounts = rtc;
        this.dummyTaxaWeightsIndividual = dtci;
        this.dummyTaxaWeightSums = dtct;
        this.totalTaxaCounts = new double[2];
        for(int i = 0; i < 2; ++i){
            this.totalTaxaCounts[i] = this.realTaxaCounts[i] + this.dummyTaxaWeightSums[i];
        }
    }
}
