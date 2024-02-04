package src.PreProcessing;

import src.Taxon.DummyTaxon;
import src.Taxon.RealTaxon;

public class RtDt {
    public RealTaxon[] rt;
    public DummyTaxon[] dt;

    public RtDt(RealTaxon[] rt, DummyTaxon[] dt) {
        this.rt = rt;
        this.dt = dt;
    }
    
}
