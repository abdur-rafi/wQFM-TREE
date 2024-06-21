package src.Threads;

import java.util.ArrayList;

import src.PreProcessing.PartitionByTreeNode;
import src.ScoreCalculator.NumSatCalculatorBinaryNodeDC;
import src.ScoreCalculator.NumSatCalculatorNodeEDC;
import src.Tree.Branch;

public class SwapDTRunnable implements Runnable{
    private ArrayList<PartitionByTreeNode> partitions;
    private int start;
    private int end;
    private int index;
    private int partition;
    private int tid;



    public SwapDTRunnable(ArrayList<PartitionByTreeNode> partitions, int start, int end){
        this.partitions = partitions;
        this.start = start;
        this.end = end;
        this.index = -1;
        this.partition = -1;
        this.tid = -1;
    }

    public void setIndexAndPartition(int index, int partition){
        this.index = index;
        this.partition = partition;
    }


    public void setTid(int tid){
        this.tid = tid;
    }
    

    @Override
    public void run() {
        for(int j = start; j < end; j++){
            PartitionByTreeNode p = partitions.get(j);
            p.scoreCalculator[tid].swapDummyTaxon(index, partition);
        }
        this.tid = -1;
        
    }

    
}
