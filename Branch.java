package wqfm.dsGT;

class Branch {
    int[] realTaxaCountsTotal;
    int[] dummyTaxaCountsTotal;
    int[] dummyTaxaCountsIndividual;
    int[] dummyTaxaToPartitionMap;


    Branch(int[] rtc, int[] dtci, int[] dtct, int[] dtpm) {
        this.realTaxaCountsTotal = rtc;
        this.dummyTaxaCountsIndividual = dtci;
        this.dummyTaxaCountsTotal = dtct;
        this.dummyTaxaToPartitionMap = dtpm;
    }
}
