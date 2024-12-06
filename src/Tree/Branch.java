package src.Tree;

public class Branch {
    public int[] realTaxaCounts;
    // public double[] dummyTaxaWeightSums;
    public double[] dummyTaxaWeightsIndividual;
    public double[] totalTaxaCounts;
    public int dummyTaxonCount;


    // public Branch(int[] rtc, double[] dtci, double[] dtct) {
    //     this.realTaxaCounts = rtc;
    //     this.dummyTaxaWeightsIndividual = dtci;
    //     this.dummyTaxaWeightSums = dtct;
    //     this.totalTaxaCounts = new double[2];
    //     for(int i = 0; i < 2; ++i){
    //         this.totalTaxaCounts[i] = this.realTaxaCounts[i] + this.dummyTaxaWeightSums[i];
    //     }
    // }

    public Branch(int dummyTaxaCount, int allocateForDummy) {
        this.dummyTaxonCount = dummyTaxaCount;
        this.realTaxaCounts = new int[2];
        this.dummyTaxaWeightsIndividual = new double[allocateForDummy];
        this.totalTaxaCounts = new double[2];
    }

    public void swapRealTaxa(int currPartition){
        int switchedPartition = 1 - currPartition;
        this.totalTaxaCounts[currPartition]--;
        this.totalTaxaCounts[switchedPartition]++;
        this.realTaxaCounts[currPartition]--;
        this.realTaxaCounts[switchedPartition]++;
    }

    public void swapDummyTaxon(int index, int currPartition){
        double weight = this.dummyTaxaWeightsIndividual[index];
        int switchedPartition = 1 - currPartition;

        this.totalTaxaCounts[currPartition] -= weight;
        this.totalTaxaCounts[switchedPartition] += weight;
        // this.dummyTaxaWeightSums[currPartition] -= weight;
        // this.dummyTaxaWeightSums[switchedPartition] += weight;
        
    }

    public void reset(int dummyTaxonCount, int allocateForDummy){
        this.dummyTaxonCount = dummyTaxonCount;

        for(int i = 0; i < 2; ++i){
            this.realTaxaCounts[i] = 0;
            this.totalTaxaCounts[i] = 0;
        }
        
        if(this.dummyTaxaWeightsIndividual.length < allocateForDummy){
            this.dummyTaxaWeightsIndividual = new double[allocateForDummy];
        }
        for(int i = 0; i < this.dummyTaxonCount; ++i){
            this.dummyTaxaWeightsIndividual[i] = 0;
        }
    }
}
