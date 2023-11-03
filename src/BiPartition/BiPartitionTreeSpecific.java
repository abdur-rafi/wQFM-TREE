package src.BiPartition;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

public class BiPartitionTreeSpecific {

    final int REAL_TAXA = 1;
    final int DUMMY_TAXA = 2; 

    // public Partition[] partitions;
    int[] realOrDummyMap;
    Map<Integer, Integer> taxaToDummyTaxaMap;
    // int[][] dummyTaxa; 
    int[] inWhichDummyTaxa;
    int[] dummyTaxaPartitionMap;
    int[] dummyTaxaSizesIndi;
    int[] realTaxaPartitionMap;
    int[] realTaxaPartitionSize;
    int[] dummyTaxaPartitionSize;
    Map<Integer, Integer> localDummyToGlobalDummyTaxaMap;
    Map<Integer, Double> divCoeffs;

    int[] nDummyTaxaInPartition;

    int nDummyTaxa;

    public BiPartitionTreeSpecific(
        ArrayList<Set<Integer>> rp,
        Map<Integer, Integer> taxaToDummyTaxaMap,
        ArrayList<Set<Integer>> dp,
        Map<Integer, Integer> localDummyToGlobalDummyTaxaMap,
        Map<Integer, Double> divCoeffs,
        int leafCount
    ){
        this.taxaToDummyTaxaMap = taxaToDummyTaxaMap;
        this.realOrDummyMap = new int[leafCount];
        this.inWhichDummyTaxa = new int[leafCount];
        this.nDummyTaxa = dp.get(0).size() + dp.get(1).size();
        this.dummyTaxaPartitionMap = new int[this.nDummyTaxa];
        this.dummyTaxaSizesIndi = new int[this.nDummyTaxa];
        this.realTaxaPartitionMap = new int[leafCount];
        this.realTaxaPartitionSize = new int[2];
        this.dummyTaxaPartitionSize = new int[2];
        this.localDummyToGlobalDummyTaxaMap = localDummyToGlobalDummyTaxaMap;
        this.divCoeffs = divCoeffs;
        this.nDummyTaxaInPartition = new int[2];
        this.nDummyTaxaInPartition[0] = dp.get(0).size();
        this.nDummyTaxaInPartition[1] = dp.get(1).size();

        
        for(int j = 0; j < leafCount; ++j){
            if(rp.get(0).contains(j) || rp.get(1).contains(j))
                realOrDummyMap[j] = REAL_TAXA;
            else if(taxaToDummyTaxaMap.containsKey(j)){
                realOrDummyMap[j] = DUMMY_TAXA;
                inWhichDummyTaxa[j] = taxaToDummyTaxaMap.get(j);
            }
        }
        for(var x : this.taxaToDummyTaxaMap.entrySet()){
            this.dummyTaxaSizesIndi[x.getValue()]++;
        }
        for(int i = 0; i < 2; ++i){
            for(var x : rp.get(i)){
                this.realTaxaPartitionMap[x] = i;
            }
            for(var x : dp.get(i)){
                this.dummyTaxaPartitionMap[x] = i;
                this.dummyTaxaPartitionSize[i] += this.dummyTaxaSizeIndividual(x);
            }
            this.realTaxaPartitionSize[i] = rp.get(i).size();
        }

        

    }

    public int nDummyTaxa(){
        return nDummyTaxa;
    }

    public boolean isRealTaxa(int i){
        return realOrDummyMap[i] == REAL_TAXA;
    }

    public int inWhichPartition(int i, boolean real){
        if (real)
            return realTaxaPartitionMap[i];
        return dummyTaxaPartitionMap[i];
    }

    public boolean isDummyTaxa(int i){
        return realOrDummyMap[i] == DUMMY_TAXA;
    }

    public int inWhichDummyTaxa(int index){
        return inWhichDummyTaxa[index];
    }

    public int realPartitionSize(int p){
        return realTaxaPartitionSize[p];
    }

    public int dummyPartitionSize(int p){
        return dummyTaxaPartitionSize[p];
    }

    public int dummyTaxaSizeIndividual(int i){
        return dummyTaxaSizesIndi[i];
    }

    public int[] getDummyTaxaPartitionMap(){
        return dummyTaxaPartitionMap;
    }

    public int totalPartitionSize(int p){
        return realPartitionSize(p) + dummyPartitionSize(p);
    }

    public int globalDummyTaxaIndex(int dti){
        return localDummyToGlobalDummyTaxaMap.get(dti);
    }

    public double getDivCoeff(int index){
        return this.divCoeffs.get(index);
    }

    public int dummyTaxaCountInPartition(int i){
        return this.nDummyTaxaInPartition[i];
    }
}
