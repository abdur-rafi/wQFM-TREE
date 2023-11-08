package src.v2.Taxon;

import src.v2.Config;
import src.v2.DSPerLevel.TaxaPerLevelWithPartition;

public class DummyTaxon {

    private static int idCounter = 0;

    public RealTaxon[] realTaxa;

    public DummyTaxon[] dummyTaxa;
    
    public RealTaxon[] flattenedRealTaxa;

    public int taxonCount;

    public int realTaxonCount;

    public int flattenedTaxonCount;

    public int id;


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

        this.id = idCounter++;

    }

    public void calcDivCoeffs(Config.ScoreNormalizationType normalizationType, double[] coeffs, double multiplier){
        
        if(normalizationType == Config.ScoreNormalizationType.FLAT_NORMALIZATION){
            for(var x : this.flattenedRealTaxa)
                coeffs[x.id] = this.flattenedTaxonCount;
        }
        else if(normalizationType == Config.ScoreNormalizationType.NESTED_NORMALIZATION){
            double sz = this.realTaxonCount + this.dummyTaxa.length;
            for(var x : this.realTaxa)
                coeffs[x.id] = sz * multiplier;
            for(var x : this.dummyTaxa){
                x.calcDivCoeffs(normalizationType, coeffs, sz * multiplier);
            }
        }
    }
}
