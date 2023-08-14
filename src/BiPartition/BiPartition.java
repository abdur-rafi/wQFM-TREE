package src.BiPartition;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;


public class BiPartition {
    Map<String, Integer> realTaxaPartitionMap;
    Map<String, Integer> realTaxaToDummyTaxaMap;
    ArrayList<Set<String>> dummyTaxas;
    Map<Integer, Integer> dummyTaxaPartitionMap;
    int score;
    Map<String, Integer> realTaxaGains;
    Map<Integer, Integer> dummyTaxaGains;
    int cg = 0;
    Set<String> realTaxaLocked;
    Set<Integer> dummyTaxaLocked;
    int[] partitionSize;
    boolean valid;

    public BiPartition(Set<String> realTaxas, ArrayList<Set<String>> dummyTaxas) {

        realTaxaGains = new HashMap<>();
        dummyTaxaGains = new HashMap<>();
        realTaxaLocked = new HashSet<>();
        dummyTaxaLocked = new HashSet<>();
        partitionSize = new int[2];

        
        realTaxaPartitionMap = new HashMap<>();
        realTaxaToDummyTaxaMap = new HashMap<>();
        this.dummyTaxas = dummyTaxas;
        dummyTaxaPartitionMap = new HashMap<>();


        if(realTaxas.size() + dummyTaxas.size() < 4){
            this.valid = false;
            return;
        }
        else
            this.valid = true;
        var random = new Random(0);
        for (var x : realTaxas) {
            if (random.nextDouble() > .5) {
                realTaxaPartitionMap.put(x, 0);
                partitionSize[0]++;
            } else {
                partitionSize[1]++;
                realTaxaPartitionMap.put(x, 1);
            }
        }

        int i = 0;
        for (var x : dummyTaxas) {
            for (var y : x) {
                realTaxaToDummyTaxaMap.put(y, i);
            }
            int p = 0;
            if (random.nextDouble() > .5) {
                p = 1;
            }
            dummyTaxaPartitionMap.put(i, p);
            partitionSize[p]++;
            ++i;
        }
        if(partitionSize[0] > 1 && partitionSize[1] > 1){

        }
        else{
            // System.out.println("==================================================");
            for(int j = 0; j < 2; ++j){
                while(partitionSize[j] <= 1){
                    for(var x : realTaxaPartitionMap.entrySet()){
                        if(x.getValue() != j){
                            swapReal(x.getKey());
                            break;
                        }
                    }
                    if(partitionSize[j] > 1)
                        break;
                    for(var x : dummyTaxaPartitionMap.entrySet()){
                        if(x.getValue() != j){
                            swapDummy(x.getKey());
                            break;
                        }
                    }
                }
            }
        }
        // break;

        // System.out.println("Again");
    }

    public void addRealTaxaGain(String taxa, int gain) {
        int v = 0;
        if (realTaxaGains.containsKey(taxa)) {
            v = realTaxaGains.get(taxa);
        }
        realTaxaGains.put(taxa, gain + v);
    }

    public void addDummyTaxaGain(int dtIndex, int gain) {
        int v = 0;
        if (dummyTaxaGains.containsKey(dtIndex)) {
            v = dummyTaxaGains.get(dtIndex);
        }
        dummyTaxaGains.put(dtIndex, gain + v);
    }

    public int getGainRealTaxa(String taxa) {
        return realTaxaGains.get(taxa);
    }

    public void resetGains() {
        realTaxaGains.clear();
        dummyTaxaGains.clear();
    }

    public void resetAll() {
        realTaxaGains.clear();
        dummyTaxaGains.clear();
        realTaxaLocked.clear();
        dummyTaxaLocked.clear();
        cg = 0;
    }

    public boolean isInRealTaxa(String tx) {
        return realTaxaPartitionMap.containsKey(tx);
    }
    private void swapReal(String taxa){
        int p = realTaxaPartitionMap.get(taxa);
        realTaxaPartitionMap.put(taxa, (p + 1) % 2);
        partitionSize[p]--;
        partitionSize[(p + 1) % 2]++;
    }
    
    private void swapDummy(int i){
        int p = dummyTaxaPartitionMap.get(i);
        dummyTaxaPartitionMap.put(i, (p + 1) % 2);
        partitionSize[p]--;
        partitionSize[(p + 1) % 2]++;
    }

    public Swap swapMax() {
        int mxrGain = 0;
        String taxa = "";
        boolean notSet = true;

        // System.out.println(realTaxaPartitionMap.size() + " pc " + dummyTaxaPartitionMap.size() );
        // System.out.println(realTaxaLocked.size() + " lc " + dummyTaxaLocked.size() );

        if (isAllLocked())
            return null;

        for (var x : realTaxaGains.entrySet()) {
            if (!realTaxaLocked.contains(x.getKey())) {
                if (notSet) {
                    mxrGain = x.getValue();
                    taxa = x.getKey();
                    notSet = false;
                } else if (mxrGain < x.getValue()) {
                    mxrGain = x.getValue();
                    taxa = x.getKey();
                }
            }
        }

        int mxdGain = 0;
        int mxdi = -1;

        for (var x : dummyTaxaGains.entrySet()) {
            if (!dummyTaxaLocked.contains(x.getKey())) {
                if (mxdi == -1) {
                    mxdGain = x.getValue();
                    mxdi = x.getKey();
                } else if (mxdGain < x.getValue()) {
                    mxdi = x.getKey();
                    mxdGain = x.getValue();
                }
            }
        }
        // if(cg + Math.max(mxrGain, mxdGain) <= 0) return false;

        Swap ret;
        if (mxdi != -1 && (mxrGain < mxdGain || notSet)) {
            swapDummy(mxdi);    
            dummyTaxaLocked.add(mxdi);
            cg += mxdGain;
            ret = new Swap(null, mxdi);
        } else if(!notSet) {
            swapReal(taxa);
            realTaxaLocked.add(taxa);
            cg += mxrGain;
            ret = new Swap(taxa, -1);
        }
        else{
            System.out.println("=========================================");
            return null;
        }
        // System.out.println("cg : " + cg + " taxa : " + taxa + "\n\n\n\n");
        return ret;
    }

    public boolean isAllLocked() {
        return (realTaxaLocked.size() == realTaxaPartitionMap.size()
                && dummyTaxaLocked.size() == dummyTaxaPartitionMap.size());
    }

    public int getCg() {
        return this.cg;
    }

    public void swap(Swap sp) {
        if (sp.dti == -1) {
            int p = realTaxaPartitionMap.get(sp.rt);
            realTaxaPartitionMap.put(sp.rt, (p + 1) % 2);
        } else {
            int p = dummyTaxaPartitionMap.get(sp.dti);
            dummyTaxaPartitionMap.put(sp.dti, (p + 1) % 2);
        }
    }

    public BiPartition[] divide() {
        BiPartition[] biPartitions = new BiPartition[2];

        ArrayList<ArrayList<Set<String>>> dtList = new ArrayList<>();
        dtList.add(new ArrayList<>());
        dtList.add(new ArrayList<>());

        ArrayList<Set<String>> rtList = new ArrayList<>();
        rtList.add(new HashSet<>());
        rtList.add(new HashSet<>());

        ArrayList<Set<String>> newDtList = new ArrayList<>();
        newDtList.add(new HashSet<>());
        newDtList.add(new HashSet<>());

        for (var x : realTaxaPartitionMap.entrySet()) {
            rtList.get(x.getValue()).add(x.getKey());
            newDtList.get((x.getValue() + 1) % 2).add(x.getKey());
        }

        for (var x : dummyTaxaPartitionMap.entrySet()) {
            dtList.get(x.getValue()).add(dummyTaxas.get(x.getKey()));
            newDtList.get((x.getValue() + 1) % 2).addAll(dummyTaxas.get(x.getKey()));
        }

        for (int i = 0; i < 2; ++i) {
            dtList.get(i).add(newDtList.get(i));
            biPartitions[i] = new BiPartition(
                    rtList.get(i),
                    dtList.get(i)
                );
        }

        return biPartitions;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("");
        for (int i = 0; i < 2; ++i) {
            sb.append("r[" + i + "]: ");
            for (var x : realTaxaPartitionMap.entrySet()) {
                if (x.getValue() == i) {
                    sb.append(x.getKey() + " ");
                }
            }
            sb.append("\n");
        }
        for (int i = 0; i < 2; ++i) {
            sb.append("dt partition " + i + ": \n");
            for (int j = 0; j < dummyTaxas.size(); ++j) {
                if (dummyTaxaPartitionMap.get(j) == i) {
                    var y = dummyTaxas.get(j);
                    sb.append("dt[" + j + "] : ");
                    for (var x : y) {
                        sb.append(x + " ");
                    }
                    sb.append("\n");
                }
            }
            sb.append("\n");
        }

        return sb.toString();

    }

    public int getTotalSize() {
        return realTaxaPartitionMap.size() + dummyTaxaPartitionMap.size();
    }

    public int[] partitionSize(){
        return partitionSize;
    }

    public boolean isValid(){
        return valid;
    }
}
