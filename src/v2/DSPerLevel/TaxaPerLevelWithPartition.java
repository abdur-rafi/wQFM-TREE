package src.v2.DSPerLevel;

import src.v2.Taxon.DummyTaxon;
import src.v2.Taxon.RealTaxon;

public class TaxaPerLevelWithPartition {

    public final boolean LEFT = false;
    public final boolean RIGHT = true;
    
    public static final int NO_NORMALIZATION = 0;
    public static final int FLAT_NORMALIZATION = 1;
    public static final int NESTED_NORMALIZATION = 2;

    public final int normalizationType = NESTED_NORMALIZATION;
    public final int allRealTaxaCount;
    public final int divCoeffsType = 0;
    
    public RealTaxon[] realTaxa;
    public DummyTaxon[] dummyTaxa;
    
    // ith elem of realTaxa in which partition
    public short[] realTaxonPartition;
    // ith elem of dummyTaxa in which partition
    public short[] dummyTaxonPartition;

    public int realTaxonCount;
    public int dummyTaxonCount;

    private boolean[] isInRealTaxa;
    private boolean[] isInDummyTaxa;
    private double[] coeffs;
    
    private short[] inWhichPartition;
    private int[] inWhichDummyTaxa;
    private int[] realTaxonIndex;

    private int[] taxonCountsInPartitions;
    private int[] realTaxonCountsInPartitions;
    private int[] dummyTaxonCountsInPartitions;

    public TaxaPerLevelWithPartition( RealTaxon[] rts, DummyTaxon[] dts, short[] rtp, short[] dtp, int rtc){
        this.realTaxa = rts;
        this.dummyTaxa = dts;
        this.realTaxonPartition = rtp;
        this.dummyTaxonPartition = dtp;
        this.realTaxonCount = rts.length;
        this.dummyTaxonCount = dts.length;
        this.allRealTaxaCount = rtc;
        isInRealTaxa = new boolean[this.allRealTaxaCount];
        this.coeffs = new double[this.allRealTaxaCount];
        this.realTaxonIndex = new int[this.allRealTaxaCount];
        this.taxonCountsInPartitions = new int[2];
        this.inWhichDummyTaxa = new int[this.allRealTaxaCount];
        this.isInDummyTaxa = new boolean[this.allRealTaxaCount];
        this.realTaxonCountsInPartitions = new int[2];
        this.dummyTaxonCountsInPartitions = new int[2];

        int i = 0;

        for(var x : realTaxa){
            isInRealTaxa[x.id] = true;
            coeffs[x.id] = 1.0;
            taxonCountsInPartitions[realTaxonPartition[i]]++;
            realTaxonIndex[x.id] = i++;
        }
        this.realTaxonCountsInPartitions[0] = taxonCountsInPartitions[0];
        this.realTaxonCountsInPartitions[1] = taxonCountsInPartitions[1];


        i = 0;
        for(var x : dummyTaxa){
            for(var y : x.flattenedRealTaxa){
                isInDummyTaxa[y.id] = true;
                inWhichDummyTaxa[y.id] = i;
            }
            x.calcDivCoeffs(normalizationType, coeffs, 1.);
            taxonCountsInPartitions[dummyTaxonPartition[i]]++;
            this.dummyTaxonCountsInPartitions[dummyTaxonPartition[i]]++;
            ++i;
        }
        i = 0;
        this.inWhichPartition = new short[this.allRealTaxaCount];
        for(var x : realTaxa){
            this.inWhichPartition[x.id] = realTaxonPartition[i++];
        }
        i = 0;
        for(var x : dummyTaxa){
            for(var y : x.flattenedRealTaxa){
                this.inWhichPartition[y.id] = dummyTaxonPartition[i];
            }
            ++i;
        }
    }

    public boolean isInRealTaxa(int realTaxonId){
        return isInRealTaxa[realTaxonId];
    }

    public double getWeight(int realTaxonId){
        return 1. / this.coeffs[realTaxonId];
    }

    public int inWhichPartition(int realTaxaId){
        return inWhichPartition[realTaxaId];
    }

    public boolean isInDummyTaxa(int realTaxonId){
        return isInDummyTaxa[realTaxonId];
    }
    public int inWhichDummyTaxa(int realTaxonId){
        return this.inWhichDummyTaxa[realTaxonId];
    }
    public int getRealTaxonIndex(int realTaxonId){
        return this.realTaxonIndex[realTaxonId];
    }
    public int getTaxonCountInPartition(int partition){
        return this.taxonCountsInPartitions[partition];
    }

    public short inWhichPartitionRealTaxonByIndex(int index){
        return this.realTaxonPartition[index];
    }
    public short inWhichPartitionDummyTaxonByIndex(int index){
        return this.dummyTaxonPartition[index];
    }
    public int getRealTaxonCountInPartition(int partition){
        return this.realTaxonCountsInPartitions[partition];
    }
    public int getDummyTaxonCountInPartition(int partition){
        return this.dummyTaxonCountsInPartitions[partition];
    }

    
    

}
