package src.Threads;

import java.util.ArrayList;

import src.PreProcessing.PartitionByTreeNode;

public class ScoreCalculatorRunnable implements Runnable{
    private ArrayList<PartitionByTreeNode> partitions;
    private int start;
    private int end;
    public double score;

    public ScoreCalculatorRunnable(ArrayList<PartitionByTreeNode> partitions, int start, int end){
        this.partitions = partitions;
        this.start = start;
        this.end = end;
        this.score = 0;
    }

    @Override
    public void run() {
        for(int j = start; j < end; j++){
            PartitionByTreeNode p = partitions.get(j);
            this.score += p.scoreCalculator.score() * p.count;
        }
    }
    
}
