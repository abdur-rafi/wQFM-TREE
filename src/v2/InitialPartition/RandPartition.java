package src.v2.InitialPartition;

import src.v2.Taxon.DummyTaxon;
import src.v2.Taxon.RealTaxon;

public class RandPartition implements IMakePartition {
    
    public MakePartitionReturnType makePartition( RealTaxon[] rts, DummyTaxon[] dts ){
        int pa = (rts.length + dts.length) / 2;

        int[] rtPart = new int[rts.length];
        for(int i = 0; i < rtPart.length; ++i){
            if(pa > 0){
                rtPart[i] = 0;
                --pa;
            }
            else{
                rtPart[i] = 1;
            }
        }
        int[] dtPart = new int[dts.length];
        for(int i = 0; i < dtPart.length; ++i){
            if(pa > 0){
                dtPart[i] = 0;
                --pa;
            }
            else{
                dtPart[i] = 1;
            }
        }

        return new MakePartitionReturnType(rtPart, dtPart);
    }

}
