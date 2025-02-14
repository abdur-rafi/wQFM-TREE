package src.Tree;

public class Branch {
    public int[] realTaxaCounts;
    // public double[] dummyTaxaWeightSums;
    public double[] dummyTaxaWeightsIndividual;
    public double[] totalTaxaCounts;

    public int dummyTaxaCount;

    public int netTranser;


    public void reset(int dummyTaxonCount, int allocateSpaceSize){
        // reset without allocating new memory if allocateSpaceSize equals to the current size
        this.realTaxaCounts[0] = 0;
        this.realTaxaCounts[1] = 0;
        this.totalTaxaCounts[0] = 0;
        this.totalTaxaCounts[1] = 0;
        this.netTranser = 0;
        this.dummyTaxaCount = dummyTaxonCount;
        if(this.dummyTaxaWeightsIndividual.length < allocateSpaceSize){
            this.dummyTaxaWeightsIndividual = new double[allocateSpaceSize];
        }
        
        for(int i = 0; i < this.dummyTaxaCount; ++i){
            this.dummyTaxaWeightsIndividual[i] = 0;
        }
    }

    public Branch(int dummyTaxaCount, int allocateSpaceSize) {
        this.realTaxaCounts = new int[2];
        this.dummyTaxaWeightsIndividual = new double[allocateSpaceSize];
        this.totalTaxaCounts = new double[2];
        this.netTranser = 0;
        this.dummyTaxaCount = dummyTaxaCount;
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


    public void addToSelf(Branch b){
        for(int i = 0; i < this.totalTaxaCounts.length; ++i){
            this.totalTaxaCounts[i] += b.totalTaxaCounts[i];
            this.realTaxaCounts[i] += b.realTaxaCounts[i];
        }
        for(int i = 0; i < this.dummyTaxaCount; ++i){
            this.dummyTaxaWeightsIndividual[i] += b.dummyTaxaWeightsIndividual[i];
        }
    }


    public void cumulateTransfer(int currPartition){
        netTranser += currPartition == 0 ? 1 : -1;
    }
    
}
