package src.Threads;

import java.util.ArrayList;

import src.PreProcessing.PartitionByTreeNode;
import src.ScoreCalculator.NumSatCalculatorBinaryNodeDC;
import src.ScoreCalculator.NumSatCalculatorNodeEDC;
import src.Tree.Branch;

public class ScoreInitiatorRunnable implements Runnable{
    private ArrayList<PartitionByTreeNode> partitions;
    private int start;
    private int end;
    private int[] dummyTaxaToPartitionMap;

    public ScoreInitiatorRunnable(ArrayList<PartitionByTreeNode> partitions, int start, int end){
        this.partitions = partitions;
        this.start = start;
        this.end = end;
        this.dummyTaxaToPartitionMap = null;
    }

    public void setDummyTaxaToPartitionMap(int[] dummyTaxaToPartitionMap){
        this.dummyTaxaToPartitionMap = dummyTaxaToPartitionMap;
    }
    
    @Override
    public void run() {
        for(int j = start; j < end; j++){
            PartitionByTreeNode p = partitions.get(j);
            
            Branch[] b = new Branch[p.partitionNodes.length];

            for(int i = 0; i < p.partitionNodes.length; ++i){
                b[i] = p.partitionNodes[i].data.branch;
            }
            
            if(p.partitionNodes.length > 3){
                p.scoreCalculator = new NumSatCalculatorNodeEDC(b, this.dummyTaxaToPartitionMap);
            }
            else{
                p.scoreCalculator = new NumSatCalculatorBinaryNodeDC(b, this.dummyTaxaToPartitionMap);
            }
        
        }
    }
}
