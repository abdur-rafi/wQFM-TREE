package src.BiPartition;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import src.ConsensusTree.IMakeParition;
import src.GeneTree.GeneTree;
import src.GeneTree.TreeNode;



public class BiPartition {
    Map<String, Integer> realTaxaPartitionMap;
    Map<String, Integer> realTaxaToDummyTaxaMap;
    public ArrayList<Set<String>> dummyTaxas;
    Map<Integer, Integer> dummyTaxaPartitionMap;
    int score;
    Map<String, Double> realTaxaGains;
    Map<Integer, Double> dummyTaxaGains;
    int cg = 0;
    Set<String> realTaxaLocked;
    Set<Integer> dummyTaxaLocked;
    int[] partitionSize;
    boolean valid;
    ArrayList<Integer> dummyTaxaIds;
    int dtIdCurrPartition;
    public Set<String> realTaxas;
    // ConsensusTree cTree;
    IMakeParition makeParition;

    Map<String,Double> divCoeffs;

    
    // 1. Add Map of taxa string to its division coefficient

    public BiPartition(
        Set<String> realTaxas, 
        ArrayList<Set<String>> dummyTaxas, 
        Map<String, Integer> realTaxaPartitionMap,
        Map<Integer, Integer> dummyTaxaPartitionMap,
        Map<String, Double> divCoeffs,
        int[] partitionSize
    ) {



        realTaxaGains = new HashMap<>();
        dummyTaxaGains = new HashMap<>();
        realTaxaLocked = new HashSet<>();
        dummyTaxaLocked = new HashSet<>();

        this.partitionSize = partitionSize;
        this.divCoeffs = divCoeffs;

        this.dummyTaxas = dummyTaxas;
        this.dtIdCurrPartition = -1;
        this.realTaxas = realTaxas;

        this.realTaxaPartitionMap = realTaxaPartitionMap;
        this.dummyTaxaPartitionMap = dummyTaxaPartitionMap;


        if(realTaxas.size() + dummyTaxas.size() < 4){
            this.valid = false;

            return;
        }
        else
            this.valid = true;

    }



    public BiPartition(
        Set<String> realTaxas, 
        ArrayList<Set<String>> dummyTaxas, 
        IMakeParition makeParition
    ) {

        // System.out.println("New Call");
        // System.out.println("Real taxas: ");
        // for(var x: realTaxas)
        // {
        //     System.out.println(x);
        // }
        // System.out.println("Artificial:");
        // for(int i=0;i<dummyTaxas.size();i++)
        // {
        //     System.out.println("dummy");
        //     for(var x: dummyTaxas.get(i))
        //     {
        //         System.out.println(x);
        //     }
        // }


        realTaxaGains = new HashMap<>();
        dummyTaxaGains = new HashMap<>();
        realTaxaLocked = new HashSet<>();
        dummyTaxaLocked = new HashSet<>();
        partitionSize = new int[2];
        this.makeParition = makeParition;

        realTaxaPartitionMap = new HashMap<>();
        realTaxaToDummyTaxaMap = new HashMap<>();
        this.dummyTaxas = dummyTaxas;
        dummyTaxaPartitionMap = new HashMap<>();
        this.dtIdCurrPartition = -1;
        this.realTaxas = realTaxas;

        if(realTaxas.size() + dummyTaxas.size() < 4){
            this.valid = false;

            return;
        }
        else
            this.valid = true;
        // System.out.println(partitionSize);

        // ConsensusTree ctr =  new ConsensusTree();
        // ctr.getBiparitionfromConsensus(realTaxas, dummyTaxas, realTaxaPartitionMap, realTaxaToDummyTaxaMap, dummyTaxaPartitionMap, partitionSize);
        // cTree.getBiparitionfromConsensus(realTaxas,dummyTaxas,realTaxaPartitionMap,dummyTaxaPartitionMap,partitionSize);
        // cTree.reset();
        makeParition.makePartition(realTaxas, dummyTaxas, realTaxaPartitionMap, dummyTaxaPartitionMap, partitionSize);
//        var random = new Random(0);
//        for (var x : realTaxas) {
//            if (random.nextDouble() > .5) {
//                realTaxaPartitionMap.put(x, 0);
//                partitionSize[0]++;
//            } else {
//                partitionSize[1]++;
//                realTaxaPartitionMap.put(x, 1);
//            }
//        }
//
//        int i = 0;
//        for (var x : dummyTaxas) {
//            for (var y : x) {
//                realTaxaToDummyTaxaMap.put(y, i);
//            }
//            int p = 0;
//            if (random.nextDouble() > .5) {
//                p = 1;
//            }
//            dummyTaxaPartitionMap.put(i, p);
//            partitionSize[p]++;
//            ++i;
//        }

        // if(partitionSize[0] > 1 && partitionSize[1] > 1){

        // }
        // else{
        //     // System.out.println("==================================================");
        //     for(int j = 0; j < 2; ++j){
        //         while(partitionSize[j] <= 1){
        //             for(var x : realTaxaPartitionMap.entrySet()){
        //                 if(x.getValue() != j){
        //                     swapReal(x.getKey());
        //                     break;
        //                 }
        //             }
        //             if(partitionSize[j] > 1)
        //                 break;
        //             for(var x : dummyTaxaPartitionMap.entrySet()){
        //                 if(x.getValue() != j){
        //                     swapDummy(x.getKey());
        //                     break;
        //                 }
        //             }
        //         }
        //     }
        // }
        // break;

        // System.out.println("Again");
    }

    public void addRealTaxaGain(String taxa, double gain) {
        double v = 0;
        if (realTaxaGains.containsKey(taxa)) {
            v = realTaxaGains.get(taxa);
        }
        realTaxaGains.put(taxa, gain + v);
    }

    public void addDummyTaxaGain(int dtIndex, double gain) {
        double v = 0;
        if (dummyTaxaGains.containsKey(dtIndex)) {
            v = dummyTaxaGains.get(dtIndex);
        }
        dummyTaxaGains.put(dtIndex, gain + v);
    }

    public double getGainRealTaxa(String taxa) {
        return realTaxaGains.get(taxa);
    }

    public double getGainDummyTaxa(int index){
        return dummyTaxaGains.get(index);
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
        double mxrGain = 0;
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

        double mxdGain = 0;
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
            // System.out.println("=========================================");
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
            swapReal(sp.rt);
            // int p = realTaxaPartitionMap.get(sp.rt);
            // realTaxaPartitionMap.put(sp.rt, (p + 1) % 2);
        } else {
            swapDummy(sp.dti);
            // int p = dummyTaxaPartitionMap.get(sp.dti);
            // dummyTaxaPartitionMap.put(sp.dti, (p + 1) % 2);
        }
    }

    // 2. Calculate division coeffs for left and right partitions

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

        ArrayList<Map<String, Double>> divCoeffs = new ArrayList<>();
        divCoeffs.add(new HashMap<String, Double>());
        divCoeffs.add(new HashMap<String, Double>());



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
            if(rtList.get(i).size() + dtList.get(i).size() < 3){
                System.out.println("SINGLETON PARTITION");
                System.exit(-1);
            }
            biPartitions[i] = new BiPartition(
                    rtList.get(i),
                    dtList.get(i),
                    this.makeParition
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


    public GeneTree createStar(){
        if(valid){
            System.out.println("This should not be called for valid partitions");
            System.exit(-1);
        }
        GeneTree tree = new GeneTree();
        for(var x : realTaxas){
            tree.addRealTaxa(x, tree.root);
        }
        int n = dummyTaxas.size();
        for(int i = 0; i < n;  ++i){
            tree.addDummyNode(dummyTaxaIds.get(i), tree.root);
        }
        return tree;
    }

    public GeneTree mergeTrees(GeneTree[] trs){
        if(this.dtIdCurrPartition == -1){
            System.out.println("dtId not set");
            System.exit(-1);
        }
        TreeNode[] nodes = new TreeNode[2];
        int sIndex = 0;
        for(int i = 0; i < 2; ++i){
            nodes[i] = null;
            for(var x : trs[i].nodes){
                if(x.dummyTaxaId == this.dtIdCurrPartition){
                    nodes[i] = x;
                    break;
                }
            }
            if(nodes[i] == null){
                System.out.println("DT not found");
                System.exit(-1);
            }
        }
        // System.out.println("---------------------------------------------");
        // System.out.println(trs[0].root);
        // System.out.println(trs[1].root);


        if(trs[0].nodes.size() > trs[1].nodes.size()){
            sIndex = 1;
        }
        trs[sIndex].reRootTree(nodes[sIndex]);
        for(var x : nodes[sIndex].childs){
            x.parent = nodes[1-sIndex].parent;
        }
        nodes[1 - sIndex].parent.childs.remove(nodes[1-sIndex]);
        nodes[1-sIndex].parent.childs.addAll(nodes[sIndex].childs);
        // nodes[1-sIndex].childs = nodes[sIndex].childs;
        // nodes[sIndex].parent = nodes[(sIndex + 1) % 2].parent;
        // nodes[(sIndex + 1) % 2].parent.childs.add(nodes[sIndex]);
        // nodes[(sIndex + 1) % 2].parent.childs.remove(nodes[(sIndex + 1) % 2]);

        int offset = trs[(sIndex + 1) % 2].nodes.size();
        for(var x : trs[sIndex].nodes){
            x.index += offset;
        }
        trs[(sIndex + 1) % 2].nodes.addAll(trs[sIndex].nodes);

        // System.out.println(trs[1-sIndex].root);

        // System.out.println("---------------------------------------------");

        return trs[(sIndex + 1) % 2];

    }
}
