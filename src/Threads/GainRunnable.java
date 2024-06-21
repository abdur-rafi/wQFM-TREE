package src.Threads;

import java.util.ArrayList;

import src.Utility;
import src.PreProcessing.PartitionByTreeNode;

public class GainRunnable implements Runnable {

    private ArrayList<PartitionByTreeNode> partitions;
    private int start;
    private int end;
    public double score;
    public double[] dummyTaxaGains;
    public int tid;

    public GainRunnable(ArrayList<PartitionByTreeNode> partitions, int start, int end){
        this.partitions = partitions;
        this.start = start;
        this.end = end;
        this.score = 0;
        this.tid = -1;
    }

    public void initDummyTaxaGains(int nDummyTaxa){
        this.dummyTaxaGains = new double[nDummyTaxa];
    }

    public void setTid(int tid){
        this.tid = tid;
    }

    @Override
    public void run() {
        this.score = 0;
        for(int j = start; j < end; j++){
            PartitionByTreeNode p = partitions.get(j);

            double nodeScore = p.scoreCalculator[tid].score();
            double[][] branchGainsForRealTaxa = p.scoreCalculator[tid].gainRealTaxa(nodeScore, p.count);
            
            p.scoreCalculator[tid].gainDummyTaxa(nodeScore, p.count, this.dummyTaxaGains);
            
            nodeScore *= p.count;

            this.score += nodeScore;
            // totalSat += score;

            for(int i = 0; i < p.partitionNodes.length; ++i){
                synchronized (p.partitionNodes[i].data[tid].gainsForSubTree){
                    Utility.addArrayToFirst(p.partitionNodes[i].data[tid].gainsForSubTree, branchGainsForRealTaxa[i]);
                }
                // Utility.addArrayToFirst(p.partitionNodes[i].data.gainsForSubTree, branchGainsForRealTaxa[i]);
            }
        }
        this.tid = -1;
    }
}
