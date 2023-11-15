package src.v2.InitialPartition;

public class MakePartitionReturnType {

    public int[] realTaxonPartition;
    public int[] dummyTaxonPartition;

    public MakePartitionReturnType(int[] rtp, int[] dtp){
        this.realTaxonPartition = rtp;
        this.dummyTaxonPartition = dtp;
    }
    
}
