package src.v2.DSPerLevel;

import java.util.ArrayList;

import src.v2.Config;
import src.v2.Taxon.DummyTaxon;
import src.v2.Taxon.RealTaxon;
import src.v2.Tree.Info;
import src.v2.Tree.Tree;
import src.v2.Tree.TreeNode;

public class TaxaPerLevelWithPartition {

    public final boolean LEFT = false;
    public final boolean RIGHT = true;
    

    public final int allRealTaxaCount;
    public final int divCoeffsType = 0;
    
    public RealTaxon[] realTaxa;
    public DummyTaxon[] dummyTaxa;
    
    // ith elem of realTaxa in which partition
    public int[] realTaxonPartition;
    // ith elem of dummyTaxa in which partition
    public int[] dummyTaxonPartition;

    public int realTaxonCount;
    public int dummyTaxonCount;

    private boolean[] isInRealTaxa;
    private boolean[] isInDummyTaxa;
    private double[] coeffs;
    
    private int[] inWhichPartition;
    private int[] inWhichDummyTaxa;
    private int[] realTaxonIndex;

    private int[] taxonCountsInPartitions;
    private int[] realTaxonCountsInPartitions;
    private int[] dummyTaxonCountsInPartitions;
    private int[] dummyTaxonCountsFlattenedInPartitions;

    public boolean smallestUnit;

    public TaxaPerLevelWithPartition( RealTaxon[] rts, DummyTaxon[] dts, int[] rtp, int[] dtp, int rtc){
        this.realTaxa = rts;
        this.dummyTaxa = dts;
        this.realTaxonPartition = rtp;
        this.dummyTaxonPartition = dtp;
        this.realTaxonCount = rts.length;
        this.dummyTaxonCount = dts.length;
        this.allRealTaxaCount = rtc;

        if(rts.length + dts.length < 4){
            this.smallestUnit = true;
            return;
        }
        this.smallestUnit = false;
        
        this.isInRealTaxa = new boolean[this.allRealTaxaCount];
        this.coeffs = new double[this.allRealTaxaCount];
        this.realTaxonIndex = new int[this.allRealTaxaCount];
        this.taxonCountsInPartitions = new int[2];
        this.inWhichDummyTaxa = new int[this.allRealTaxaCount];
        this.isInDummyTaxa = new boolean[this.allRealTaxaCount];
        this.realTaxonCountsInPartitions = new int[2];
        this.dummyTaxonCountsInPartitions = new int[2];

        this.dummyTaxonCountsFlattenedInPartitions = new int[2];

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
            x.calcDivCoeffs(Config.SCORE_NORMALIZATION_TYPE, coeffs, 1.);
            taxonCountsInPartitions[dummyTaxonPartition[i]]++;
            this.dummyTaxonCountsInPartitions[dummyTaxonPartition[i]]++;
            this.dummyTaxonCountsFlattenedInPartitions[dummyTaxonPartition[i]] += x.flattenedTaxonCount;
            
            ++i;
        }
        i = 0;
        this.inWhichPartition = new int[this.allRealTaxaCount];
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

    public int inWhichPartitionRealTaxonByIndex(int index){
        return this.realTaxonPartition[index];
    }
    public int inWhichPartitionDummyTaxonByIndex(int index){
        return this.dummyTaxonPartition[index];
    }
    public int getRealTaxonCountInPartition(int partition){
        return this.realTaxonCountsInPartitions[partition];
    }
    public int getDummyTaxonCountInPartition(int partition){
        return this.dummyTaxonCountsInPartitions[partition];
    }

    public int getDummyTaxonCountFlattenedInPartition(int partition){
        return this.dummyTaxonCountsFlattenedInPartitions[partition];
    }
    public int getTaxonCountFlattenedInPartition(int partition){
        return (this.realTaxonCountsInPartitions[partition] + this.dummyTaxonCountsFlattenedInPartitions[partition]);
    }

    public int getFlattenedCount(int index){
        return this.dummyTaxa[index].flattenedTaxonCount;
    }

    public void swapPartitionRealTaxon(int index){
        int currPartition = this.realTaxonPartition[index];
        int switchedPartition = (int) (1 - currPartition);

        this.realTaxonPartition[index] = switchedPartition;
        this.inWhichPartition[this.realTaxa[index].id] = switchedPartition;
        this.taxonCountsInPartitions[currPartition]--;
        this.taxonCountsInPartitions[switchedPartition]++;
        this.realTaxonCountsInPartitions[currPartition]--;
        this.realTaxonCountsInPartitions[switchedPartition]++;
    }
    

    public void swapPartitionDummyTaxon(int index){
        int currPartition = this.dummyTaxonPartition[index];
        int switchedPartition = (1 - currPartition);

        this.dummyTaxonPartition[index] = switchedPartition;
        
        for(var x : this.dummyTaxa[index].flattenedRealTaxa){
            this.inWhichPartition[x.id] = switchedPartition;
        }
        
        this.taxonCountsInPartitions[currPartition]--;
        this.taxonCountsInPartitions[switchedPartition]++;
        this.dummyTaxonCountsInPartitions[currPartition]--;
        this.dummyTaxonCountsInPartitions[switchedPartition]++;

        this.dummyTaxonCountsFlattenedInPartitions[currPartition] -= this.dummyTaxa[index].flattenedTaxonCount;
        this.dummyTaxonCountsFlattenedInPartitions[switchedPartition] += this.dummyTaxa[index].flattenedTaxonCount;

    }


    public Tree createStar(){
        if(!smallestUnit){
            System.out.println("Create Star should be called only on smallest unit\n");
            System.exit(-1);
        }

        Tree t = new Tree();
        ArrayList<TreeNode> childs = new ArrayList<>();
        for(var x : this.realTaxa){
            childs.add(t.addLeaf(x).setInfo(new Info(-1)));
        }

        for(var x : this.dummyTaxa){
            childs.add(t.addLeaf(null).setInfo(new Info(x.id)));
        }

        t.root = t.addInternalNode(childs).setInfo(new Info(-1));

        return t;
    }

}
