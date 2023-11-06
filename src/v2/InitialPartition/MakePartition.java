package src.v2.InitialPartition;

import src.v2.Taxon.DummyTaxon;
import src.v2.Taxon.RealTaxon;

public interface MakePartition {
    


    public MakePartitionReturnType makePartition( RealTaxon[] rts, DummyTaxon[] dts );
}
