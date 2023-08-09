package wqfm.dsGT;

class Branch {
    int[] realTaxaCountsTotal;
    int[] dummyTaxaCountsTotal;
    int[] dummyTaxaCountsIndividual;
    int[] dummyTaxaToPartitionMap;
    int[] totalTaxaCounts;


    Branch(int[] rtc, int[] dtci, int[] dtct, int[] dtpm) {
        this.realTaxaCountsTotal = rtc;
        this.dummyTaxaCountsIndividual = dtci;
        this.dummyTaxaCountsTotal = dtct;
        this.dummyTaxaToPartitionMap = dtpm;
        this.totalTaxaCounts = new int[2];
        for(int i = 0; i < 2; ++i){
            this.totalTaxaCounts[i] = this.realTaxaCountsTotal[i] + this.dummyTaxaCountsTotal[i];
        }
    }
}
