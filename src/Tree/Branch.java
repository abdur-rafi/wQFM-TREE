package src.Tree;

public class Branch {
    public int[] realTaxaCounts;
    // public double[] dummyTaxaWeightSums;
    public double[] dummyTaxaWeightsIndividual;
    public double[] totalTaxaCounts;

    public int netTranser;

    // public Branch(int[] rtc, double[] dtci, double[] dtct) {
    //     this.realTaxaCounts = rtc;
    //     this.dummyTaxaWeightsIndividual = dtci;
    //     this.dummyTaxaWeightSums = dtct;
    //     this.totalTaxaCounts = new double[2];
    //     for(int i = 0; i < 2; ++i){
    //         this.totalTaxaCounts[i] = this.realTaxaCounts[i] + this.dummyTaxaWeightSums[i];
    //     }
    // }

    public Branch(int dummyTaxaCount) {
        this.realTaxaCounts = new int[2];
        this.dummyTaxaWeightsIndividual = new double[dummyTaxaCount];
        // this.dummyTaxaWeightSums = dtct;
        this.totalTaxaCounts = new double[2];
        this.netTranser = 0;
        // for(int i = 0; i < 2; ++i){
        //     this.totalTaxaCounts[i] = this.realTaxaCounts[i] + this.dummyTaxaWeightSums[i];
        // }
    }

    public void batchTransferRealTaxon(){
        int currPartition = netTranser > 0 ? 0 : 1;
        netTranser = Math.abs(netTranser);
        this.realTaxaCounts[currPartition] -= netTranser;
        this.realTaxaCounts[1 - currPartition] += netTranser;
        this.totalTaxaCounts[currPartition] -= netTranser;
        this.totalTaxaCounts[1 - currPartition] += netTranser;

        netTranser = 0;
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

    public Branch(Branch b){
        this.realTaxaCounts = new int[b.realTaxaCounts.length];
        this.totalTaxaCounts = new double[b.totalTaxaCounts.length];
        for(int i = 0; i < this.totalTaxaCounts.length; ++i){
            this.totalTaxaCounts[i] = b.totalTaxaCounts[i];
            this.realTaxaCounts[i] = b.realTaxaCounts[i];
        }
        this.dummyTaxaWeightsIndividual = new double[b.dummyTaxaWeightsIndividual.length];
        for(int i = 0; i < this.dummyTaxaWeightsIndividual.length; ++i){
            this.dummyTaxaWeightsIndividual[i] = b.dummyTaxaWeightsIndividual[i];
        }
        // this.realTaxaCounts = b.realTaxaCounts.clone();
        // this.dummyTaxaWeightsIndividual = b.dummyTaxaWeightsIndividual.clone();
        // // this.dummyTaxaWeightSums = b.dummyTaxaWeightSums.clone();
        // this.totalTaxaCounts = b.totalTaxaCounts.clone();
    }

    public void addToSelf(Branch b){
        for(int i = 0; i < this.totalTaxaCounts.length; ++i){
            this.totalTaxaCounts[i] += b.totalTaxaCounts[i];
            this.realTaxaCounts[i] += b.realTaxaCounts[i];
        }
        for(int i = 0; i < this.dummyTaxaWeightsIndividual.length; ++i){
            this.dummyTaxaWeightsIndividual[i] += b.dummyTaxaWeightsIndividual[i];
        }
    }


    public void cumulateTransfer(int currPartition){
        netTranser += currPartition == 0 ? 1 : -1;
    }
    
}
