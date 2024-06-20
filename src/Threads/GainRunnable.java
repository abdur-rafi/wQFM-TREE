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

    public GainRunnable(ArrayList<PartitionByTreeNode> partitions, int start, int end){
        this.partitions = partitions;
        this.start = start;
        this.end = end;
        this.score = 0;
    }

    public void initDummyTaxaGains(int nDummyTaxa){
        this.dummyTaxaGains = new double[nDummyTaxa];
    }

    @Override
    public void run() {
        this.score = 0;
        for(int j = start; j < end; j++){
            PartitionByTreeNode p = partitions.get(j);

            double nodeScore = p.scoreCalculator.score();
            double[][] branchGainsForRealTaxa = p.scoreCalculator.gainRealTaxa(nodeScore, p.count);
            
            p.scoreCalculator.gainDummyTaxa(nodeScore, p.count, this.dummyTaxaGains);
            
            nodeScore *= p.count;

            this.score += nodeScore;
            // totalSat += score;

            for(int i = 0; i < p.partitionNodes.length; ++i){
                synchronized (p.partitionNodes[i].data.gainsForSubTree){
                    Utility.addArrayToFirst(p.partitionNodes[i].data.gainsForSubTree, branchGainsForRealTaxa[i]);
                }
                // Utility.addArrayToFirst(p.partitionNodes[i].data.gainsForSubTree, branchGainsForRealTaxa[i]);
            }
        }
    }
}
