package src.v2.Taxon;

import java.util.ArrayList;

import src.v2.DSPerLevel.TaxaPerLevelWithPartition;

public class DummyTaxon {

    public RealTaxon[] realTaxa;

    public DummyTaxon[] dummyTaxa;
    
    public RealTaxon[] flattenedRealTaxa;

    public int taxonCount;

    public int realTaxonCount;

    public int flattenedTaxonCount;


    public DummyTaxon(RealTaxon[] rts, DummyTaxon[] dts){
        this.realTaxonCount = rts.length;
        this.taxonCount = rts.length + dts.length;
        this.realTaxa = rts;
        this.dummyTaxa = dts;
        for(var x : dts){
            this.flattenedTaxonCount += x.flattenedTaxonCount;
        }
        
        int i = 0;
        this.flattenedTaxonCount += this.realTaxonCount;
        this.flattenedRealTaxa = new RealTaxon[this.flattenedTaxonCount];
        i = 0;
        for(var x : realTaxa){
            this.flattenedRealTaxa[i] = x;
            i++;
        }
        for(var x : dummyTaxa){
            for(var y : x.flattenedRealTaxa){
                this.flattenedRealTaxa[i] = y;
                i++;
            }
        }

    }

    public void calcDivCoeffs(int normalizationType, double[] coeffs, double multiplier){
        
        if(normalizationType == TaxaPerLevelWithPartition.FLAT_NORMALIZATION){
            for(var x : this.flattenedRealTaxa)
                coeffs[x.id] = this.flattenedTaxonCount;
        }
        else if(normalizationType == TaxaPerLevelWithPartition.NESTED_NORMALIZATION){
            double sz = this.realTaxonCount + this.dummyTaxa.length;
            for(var x : this.realTaxa)
                coeffs[x.id] = sz * multiplier;
            for(var x : this.dummyTaxa){
                x.calcDivCoeffs(normalizationType, coeffs, sz * multiplier);
            }
        }
    }
}
