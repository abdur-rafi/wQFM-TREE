package src.Threads;

import java.util.ArrayList;

import src.PreProcessing.PartitionByTreeNode;

public class ScoreCalculatorRunnable implements Runnable{
    private ArrayList<PartitionByTreeNode> partitions;
    private int start;
    private int end;
    public double score;
    public int tid;

    public ScoreCalculatorRunnable(ArrayList<PartitionByTreeNode> partitions, int start, int end){
        this.partitions = partitions;
        this.start = start;
        this.end = end;
        this.score = 0;
        this.tid = -1;
    }

    public void setTid(int tid){
        this.tid = tid;
    }

    @Override
    public void run() {
        for(int j = start; j < end; j++){
            PartitionByTreeNode p = partitions.get(j);
            this.score += p.scoreCalculator[tid].score() * p.count;
        }
        this.tid = -1;
    }
    
}
