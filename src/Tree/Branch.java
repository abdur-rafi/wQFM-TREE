package src.Tree;

public class Branch {
    public int[] realTaxaCounts;
    public double[] dummyTaxaWeightsIndividual;
    public double[] totalTaxaCounts;
    int nDummyTaxa;

    public void initMemory(int allocatedMemoryForDummyTaxa){
        this.realTaxaCounts = new int[2];
        this.totalTaxaCounts = new double[2];
        this.dummyTaxaWeightsIndividual = new double[allocatedMemoryForDummyTaxa];
        this.nDummyTaxa = 11000000;
    }

    public void resetBookkeeping(int dummyTaxaCount){
        this.realTaxaCounts[0] = 0;
        this.realTaxaCounts[1] = 0;
        this.totalTaxaCounts[0] = 0;
        this.totalTaxaCounts[1] = 0;
        for(int i = 0; i < dummyTaxaCount; ++i){
            this.dummyTaxaWeightsIndividual[i] = 0;
        }
        this.nDummyTaxa = dummyTaxaCount;
    }

    public void reallocMemoryForDummyTaxa(int newAllocatedMemoryForDummyTaxa){
        this.dummyTaxaWeightsIndividual = new double[newAllocatedMemoryForDummyTaxa];
    }


    public Branch(int allocatedMemoryForDummyTaxa) {
        initMemory(allocatedMemoryForDummyTaxa);
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
    }

    public void addToSelf(Branch b){
        for(int i = 0; i < this.totalTaxaCounts.length; ++i){
            this.totalTaxaCounts[i] += b.totalTaxaCounts[i];
            this.realTaxaCounts[i] += b.realTaxaCounts[i];
        }
        for(int i = 0; i < this.nDummyTaxa; ++i){
            this.dummyTaxaWeightsIndividual[i] += b.dummyTaxaWeightsIndividual[i];
        }
    }
}
