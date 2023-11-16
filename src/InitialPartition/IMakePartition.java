package src.InitialPartition;

import src.Taxon.DummyTaxon;
import src.Taxon.RealTaxon;

public interface IMakePartition {
    


    public MakePartitionReturnType makePartition( RealTaxon[] rts, DummyTaxon[] dts );
}
