package src.BiPartition;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import src.GeneTree.GeneTree;

public class BiPartitionMapper {


    public static BiPartitionTreeSpecific map(BiPartition partition, GeneTree tree){

        ArrayList<Set<Integer>> rp = new ArrayList<>();
        rp.add(new HashSet<>());
        rp.add(new HashSet<>());

        Map<Integer, Integer> taxaToDummyTaxaMap = new HashMap<>();
        ArrayList<Set<Integer>> dp = new ArrayList<>();
        dp.add(new HashSet<>());
        dp.add(new HashSet<>());

        Map<Integer, Integer> localToGlobalMap = new HashMap<>();

        int leafCount = 0;


        for(var x : partition.realTaxaPartitionMap.entrySet()){
            if(tree.taxaMap.containsKey(x.getKey())){
                rp.get(x.getValue()).add(tree.taxaMap.get(x.getKey()));
            }
        }
        int i = 0;
        ArrayList<Set<Integer>> arr = new ArrayList<>();
        for(var x : partition.dummyTaxas){
            Set<Integer> dt = new HashSet<>();
            for(var y : x){
                if(tree.taxaMap.containsKey(y)){
                    dt.add(tree.taxaMap.get(y));
                }
            }
            if(dt.size() > 0){
                localToGlobalMap.put(arr.size(), i);
                arr.add(dt);
            }
            
            int p = partition.dummyTaxaPartitionMap.get(i);
            if(dt.size() > 0){
                dp.get(p).add(arr.size() - 1);
            }
            ++i;
        }

        i = 0;
        for(var x : arr){
            for(var y : x){
                taxaToDummyTaxaMap.put(y, i);
            }
            ++i;
        }

        for(var x : tree.nodes){
            if(x.isLeaf()) ++leafCount;
        }

        return new BiPartitionTreeSpecific(rp, taxaToDummyTaxaMap, dp, localToGlobalMap, leafCount);

    }
}   
