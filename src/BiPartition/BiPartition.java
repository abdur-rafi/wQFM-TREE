package src.BiPartition;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import src.GeneTree.GeneTree;

public class BiPartition {
    Map<String, Integer> realTaxaPartitionMap;
    Map<String, Integer> realTaxaToDummyTaxaMap;
    ArrayList<Set<String>> dummyTaxas;
    Map<Integer, Integer> dummyTaxaPartitionMap;


    public BiPartition(Set<String> realTaxas, ArrayList<Set<String>> dummyTaxas, ArrayList<GeneTree> gts){
        
        realTaxaPartitionMap = new HashMap<>();
        realTaxaToDummyTaxaMap = new HashMap<>();
        this.dummyTaxas = dummyTaxas;
        dummyTaxaPartitionMap = new HashMap<>();

        var random = new Random(0);
        for(var x : realTaxas){
            if(random.nextDouble() > .5){
                realTaxaPartitionMap.put(x, 0);
            }
            else 
                realTaxaPartitionMap.put(x, 1);
        }

        int i = 0;
        for(var x : dummyTaxas){
            for(var y : x){
                realTaxaToDummyTaxaMap.put(y, i);
            }
            int p = 0;
            if(random.nextDouble() > .5){
                p = 1;
            }
            dummyTaxaPartitionMap.put(i, p);
            ++i;
        }
    }

    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder("");
        for(int i = 0; i < 2; ++i){
            sb.append("r[" + i + "]: ");
            for(var x : realTaxaPartitionMap.entrySet()){
                if(x.getValue() == i){
                    sb.append(x.getKey() + " ");
                }
            }
            sb.append("\n");
        }
        return sb.toString();

    }

    
}
