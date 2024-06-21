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
    int tid;

    public ScoreInitiatorRunnable(ArrayList<PartitionByTreeNode> partitions, int start, int end){
        this.partitions = partitions;
        this.start = start;
        this.end = end;
        this.dummyTaxaToPartitionMap = null;
        this.tid = -1;
    }

    public void setDummyTaxaToPartitionMap(int[] dummyTaxaToPartitionMap){
        this.dummyTaxaToPartitionMap = dummyTaxaToPartitionMap;
    }

    public void setTid(int tid){
        this.tid = tid;
    }
    
    @Override
    public void run() {
        for(int j = start; j < end; j++){
            PartitionByTreeNode p = partitions.get(j);
            
            Branch[] b = new Branch[p.partitionNodes.length];

            for(int i = 0; i < p.partitionNodes.length; ++i){
                b[i] = p.partitionNodes[i].data[tid].branch;
            }
            
            if(p.partitionNodes.length > 3){
                p.scoreCalculator[tid] = new NumSatCalculatorNodeEDC(b, this.dummyTaxaToPartitionMap);
            }
            else{
                p.scoreCalculator[tid] = new NumSatCalculatorBinaryNodeDC(b, this.dummyTaxaToPartitionMap);
            }
        
        }
    }
}
