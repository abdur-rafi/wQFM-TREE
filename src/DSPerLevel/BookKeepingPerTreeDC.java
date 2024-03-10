package src.DSPerLevel;

import src.Taxon.RealTaxon;

public class BookKeepingPerTreeDC {
    boolean[] realTaxaInTree;
    TaxaPerLevelWithPartition taxaPerLevel;
    private double[] pairsFromPart;
    private double[] realTaxaCountsInPartitions;
    private double[] dummyTaxonWeightsIndividual;
    public double[] dummyTaxonCountsInPartitions;

    public BookKeepingPerTreeDC(boolean[] realTaxaInTree, TaxaPerLevelWithPartition taxaPerLevel){
        this.realTaxaInTree = realTaxaInTree;
        this.taxaPerLevel = taxaPerLevel;
        this.pairsFromPart = new double[2];
        double[] totalTaxon = new double[2];
        this.realTaxaCountsInPartitions = new double[2];
        this.dummyTaxonWeightsIndividual = new double[taxaPerLevel.dummyTaxonCount];
        this.dummyTaxonCountsInPartitions = new double[2];
        

        
        for(int i = 0; i < this.realTaxaInTree.length; ++i){
            if(this.realTaxaInTree[i]){
                int partition = this.taxaPerLevel.inWhichPartition(i);
                totalTaxon[partition] += this.taxaPerLevel.getWeight(i);
                if(this.taxaPerLevel.isInDummyTaxa(i)){
                    this.dummyTaxonWeightsIndividual[this.taxaPerLevel.inWhichDummyTaxa(i)] += this.taxaPerLevel.getWeight(i);
                }
                else{
                    this.realTaxaCountsInPartitions[partition]++;
                }
            }
        }

        this.pairsFromPart[0] = totalTaxon[0] * totalTaxon[0];
        this.pairsFromPart[1] = totalTaxon[1] * totalTaxon[1];

        for(int i = 0; i < this.taxaPerLevel.dummyTaxonCount; ++i){
            int partition = this.taxaPerLevel.inWhichPartitionDummyTaxonByIndex(i);
            this.pairsFromPart[partition] -= this.dummyTaxonWeightsIndividual[i] * this.dummyTaxonWeightsIndividual[i];
            this.dummyTaxonCountsInPartitions[partition] += this.dummyTaxonWeightsIndividual[i];
            
        }
        this.pairsFromPart[0] -= this.realTaxaCountsInPartitions[0];
        this.pairsFromPart[1] -= this.realTaxaCountsInPartitions[1];

        this.pairsFromPart[0] /= 2;
        this.pairsFromPart[1] /= 2;        

    }


    public double totalQuartets(){
        return this.pairsFromPart[0] * this.pairsFromPart[1];
    }

    public double totalQuartetsAfterDummySwap(int dIndex, int toPartition){
        double a = this.pairsFromPart[1 - toPartition] - this.dummyTaxonWeightsIndividual[dIndex] * (
            this.getTotalTaxon(1-toPartition) - 
            this.getDummyTaxonIndiWeight(dIndex)
        );
        double b = this.pairsFromPart[toPartition] + this.dummyTaxonWeightsIndividual[dIndex] * (this.getTotalTaxon(toPartition));
        return a * b;        
    
    }

    public double getTotalTaxon(int p){
        return this.realTaxaCountsInPartitions[p] + this.dummyTaxonCountsInPartitions[p];
    }

    public double getDummyTaxonIndiWeight(int index){
        return this.dummyTaxonWeightsIndividual[index];
    }

    public double totalQuartetsAfterSwap(int rtId, int toPartition){
        if(!this.realTaxaInTree[rtId]){
            return this.totalQuartets();
        }
        return (this.pairsFromPart[1 - toPartition] - (this.getTotalTaxon(1 - toPartition) - 1)) * 
        (this.pairsFromPart[toPartition] + (this.getTotalTaxon(toPartition)));
    }

    public void swapRealTaxon(int rtId, int partition){
        if(!this.realTaxaInTree[rtId]) return;

        this.pairsFromPart[ 1 - partition] += this.getTotalTaxon(1 - partition);
        this.pairsFromPart[partition] -= this.getTotalTaxon(partition) - 1;
        
        this.realTaxaCountsInPartitions[partition]--;
        this.realTaxaCountsInPartitions[1 - partition]++;

    }

    public void swapDummyTaxon(int index, int partition){

        this.pairsFromPart[ 1 - partition] += this.dummyTaxonWeightsIndividual[index] * this.getTotalTaxon(1 - partition);
        this.pairsFromPart[partition] -= this.dummyTaxonWeightsIndividual[index] * (this.getTotalTaxon(partition) - this.dummyTaxonWeightsIndividual[index]);
        
        this.dummyTaxonCountsInPartitions[partition] -= this.dummyTaxonWeightsIndividual[index];
        this.dummyTaxonCountsInPartitions[1 - partition] += this.dummyTaxonWeightsIndividual[index];

    }

}
