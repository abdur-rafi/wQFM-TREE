package src.ConsensusTree;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

public interface IMakeParition {
    
    public void makePartition(
        Set<String> realTaxas, 
        ArrayList<Set<String>> dummyTaxas ,
        Map<String, Integer> realTaxaPartitionMap,
        Map<Integer, Integer> dummyTaxaPartitionMap,
        int[] partitionSize
    );
}
