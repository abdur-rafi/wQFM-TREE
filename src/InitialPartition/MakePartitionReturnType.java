package src.InitialPartition;

import src.DSPerLevel.BookKeepingPerLevelDC;

public class MakePartitionReturnType {

    public int[] realTaxonPartition;
    public int[] dummyTaxonPartition;
    public BookKeepingPerLevelDC book;

    public MakePartitionReturnType(int[] rtp, int[] dtp){
        this.realTaxonPartition = rtp;
        this.dummyTaxonPartition = dtp;
        this.book = null;
    }
    
}
