package src.DSPerLevel;

public class BookKeepingPerTreeDC {
    boolean[] realTaxaInTree;
    TaxaPerLevelWithPartition taxaPerLevel;
    private double[] pairsFromPart;
    private double[] totalTaxon;
    private double[] realTaxaCountsInPartitions;
    private double[] dummyTaxonWeightsIndividual;
    public BookKeepingPerTreeDC(boolean[] realTaxaInTree, TaxaPerLevelWithPartition taxaPerLevel){
        this.realTaxaInTree = realTaxaInTree;
        this.taxaPerLevel = taxaPerLevel;
        this.pairsFromPart = new double[2];
        this.totalTaxon = new double[2];
        this.realTaxaCountsInPartitions = new double[2];
        this.dummyTaxonWeightsIndividual = new double[taxaPerLevel.dummyTaxonCount];

        
        for(int i = 0; i < this.realTaxaInTree.length; ++i){
            if(this.realTaxaInTree[i]){
                int partition = this.taxaPerLevel.inWhichPartition(i);
                this.totalTaxon[partition] += this.taxaPerLevel.getWeight(i);
                if(this.taxaPerLevel.isInDummyTaxa(i)){
                    this.dummyTaxonWeightsIndividual[this.taxaPerLevel.inWhichDummyTaxa(i)] += this.taxaPerLevel.getWeight(i);
                }
                else{
                    this.realTaxaCountsInPartitions[partition]++;
                }
            }
        }

        this.pairsFromPart[0] = this.totalTaxon[0] * this.totalTaxon[0];
        this.pairsFromPart[1] = this.totalTaxon[1] * this.totalTaxon[1];

        for(int i = 0; i < this.taxaPerLevel.dummyTaxonCount; ++i){
            int partition = this.taxaPerLevel.inWhichPartitionDummyTaxonByIndex(i);
            this.pairsFromPart[partition] -= this.dummyTaxonWeightsIndividual[i] * this.dummyTaxonWeightsIndividual[i];
        }
        this.pairsFromPart[0] -= this.realTaxaCountsInPartitions[0];
        this.pairsFromPart[1] -= this.realTaxaCountsInPartitions[1];

        this.pairsFromPart[0] /= 2;
        this.pairsFromPart[1] /= 2;        

    }


    public double totalQuartets(){
        return this.pairsFromPart[0] * this.pairsFromPart[1];
    }

}
