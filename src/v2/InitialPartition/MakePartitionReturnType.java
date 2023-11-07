package src.v2.InitialPartition;

public class MakePartitionReturnType {

    public short[] realTaxonPartition;
    public short[] dummyTaxonPartition;

    public MakePartitionReturnType(short[] rtp, short[] dtp){
        this.realTaxonPartition = rtp;
        this.dummyTaxonPartition = dtp;
    }
    
}
