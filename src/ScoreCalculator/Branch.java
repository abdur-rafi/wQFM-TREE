package src.ScoreCalculator;

class Branch {
    int[] realTaxaCountsTotal;
    double[] dummyTaxaCountsTotal;
    double[] dummyTaxaCountsIndividual;
    int[] dummyTaxaToPartitionMap;
    double[] totalTaxaCounts;


    Branch(int[] rtc, double[] dtci, double[] dtct, int[] dtpm) {
        this.realTaxaCountsTotal = rtc;
        this.dummyTaxaCountsIndividual = dtci;
        this.dummyTaxaCountsTotal = dtct;
        this.dummyTaxaToPartitionMap = dtpm;
        this.totalTaxaCounts = new double[2];
        for(int i = 0; i < 2; ++i){
            this.totalTaxaCounts[i] = this.realTaxaCountsTotal[i] + this.dummyTaxaCountsTotal[i];
        }
    }
}
