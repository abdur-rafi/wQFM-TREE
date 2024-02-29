package src.DSPerLevel;


import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Queue;
import java.util.Set;

import src.Config;
import src.Utility;
import src.InitialPartition.IMakePartition;
import src.PreProcessing.Data;
import src.PreProcessing.DataContainer;
import src.PreProcessing.PartitionByTreeNode;
import src.PreProcessing.PartitionNode;
import src.PreProcessing.PartitionNode.PartitionByTreeNodeWithIndex;
import src.ScoreCalculator.NumSatCalculatorBinaryNodeDC;
import src.ScoreCalculator.NumSatCalculatorNodeEDC;
import src.Taxon.DummyTaxon;
import src.Taxon.RealTaxon;
import src.Tree.Branch;

public class BookKeepingPerLevelDC {

    public DataContainer dc;
    public TaxaPerLevelWithPartition taxaPerLevel;
    BookKeepingPerTreeDC[] bookKeepingPerTreeDCs;

    public BookKeepingPerLevelDC(DataContainer dc, TaxaPerLevelWithPartition taxaPerLevelWithPartition){
        this.dc = dc;
        this.taxaPerLevel = taxaPerLevelWithPartition;
        if(this.taxaPerLevel.smallestUnit)
            return;

        this.initialBookKeeping();
        this.bookKeepingPerTreeDCs = new BookKeepingPerTreeDC[dc.realTaxaInTrees.length];
        for(int i = 0; i < dc.realTaxaInTrees.length; ++i){
            this.bookKeepingPerTreeDCs[i] = new BookKeepingPerTreeDC(dc.realTaxaInTrees[i], this.taxaPerLevel);
        }

    }

    public void initialBookKeeping(){

        for(int i = 0; i < this.dc.realTaxaPartitionNodes.length; ++i){
            PartitionNode p = this.dc.realTaxaPartitionNodes[i];
            p.data = new Data();
            p.data.branch = new Branch(this.taxaPerLevel.dummyTaxonCount);

            if(this.taxaPerLevel.isInDummyTaxa(i)){
                int dtid = this.taxaPerLevel.inWhichDummyTaxa(i);
                int partition = this.taxaPerLevel.inWhichPartitionDummyTaxonByIndex(dtid);
                p.data.branch.dummyTaxaWeightsIndividual[dtid] = this.taxaPerLevel.getWeight(i);
                p.data.branch.totalTaxaCounts[partition] += this.taxaPerLevel.getWeight(i);

            }
            else{
                int partition = this.taxaPerLevel.inWhichPartition(i);
                p.data.branch.realTaxaCounts[partition] = 1;
                p.data.branch.totalTaxaCounts[partition] = 1;
            }

        }

        int sz = this.dc.topSortedPartitionNodes.size();
        for(int i = sz - 1; i >  -1; --i){
            PartitionNode p = this.dc.topSortedPartitionNodes.get(i);
            if(p.isLeaf){
                continue;
            }
            else{
                p.data = new Data();
                p.data.branch = new Branch(this.taxaPerLevel.dummyTaxonCount);
                for(PartitionNode child : p.children){
                    p.data.branch.addToSelf(child.data.branch);
                }
            }
        }

        for(PartitionByTreeNode p : this.dc.partitionsByTreeNodes){
            Branch[] b = new Branch[p.partitionNodes.length];
            for(int i = 0; i < p.partitionNodes.length; ++i){
                b[i] = p.partitionNodes[i].data.branch;
            }
            if(p.partitionNodes.length > 3){
                p.scoreCalculator = new NumSatCalculatorNodeEDC(b,this.taxaPerLevel.dummyTaxonPartition);
            }
            else{
                p.scoreCalculator = new NumSatCalculatorBinaryNodeDC(b, this.taxaPerLevel.dummyTaxonPartition);
            }
        }
    }


    public double calculateScore(){
        double score = 0;
        double totalQuartets = 0;
        for(PartitionByTreeNode p : this.dc.partitionsByTreeNodes){
            score += p.scoreCalculator.score() * p.count;
        }

        for(BookKeepingPerTreeDC bt : this.bookKeepingPerTreeDCs){
            totalQuartets += bt.totalQuartets();
        }
        

        return Config.SCORE_EQN.scoreFromSatAndTotal(score, totalQuartets);
    }

    public double calculateScoreAndGains(double[][] realTaxaGains, double[] dummyTaxaGains){
        double totalSat = 0;
        
        for(PartitionNode p : this.dc.topSortedPartitionNodes){
            p.data.gainsForSubTree = new double[2];
        }

        for(PartitionByTreeNode p : this.dc.partitionsByTreeNodes){
            double score = p.scoreCalculator.score();
            double[][] branchGainsForRealTaxa = p.scoreCalculator.gainRealTaxa(score, p.count);
            
            p.scoreCalculator.gainDummyTaxa(score, p.count, dummyTaxaGains);
            score *= p.count;

            totalSat += score;

            for(int i = 0; i < p.partitionNodes.length; ++i){
                Utility.addArrayToFirst(p.partitionNodes[i].data.gainsForSubTree, branchGainsForRealTaxa[i]);
            }
        }

        for(PartitionNode p : this.dc.topSortedPartitionNodes){
            for(PartitionNode childs : p.children){
                Utility.addArrayToFirst(childs.data.gainsForSubTree, p.data.gainsForSubTree);
            }
        }

        double currTotalQuartets = 0;
        double[] dtTotals = new double[this.taxaPerLevel.dummyTaxonCount];
        
        for(BookKeepingPerTreeDC bkpt : this.bookKeepingPerTreeDCs){
            currTotalQuartets += bkpt.totalQuartets();
            for(int i = 0;i < this.taxaPerLevel.dummyTaxonCount; ++i){
                dtTotals[i] += bkpt.totalQuartetsAfterDummySwap(i, 1 - this.taxaPerLevel.inWhichPartitionDummyTaxonByIndex(i));
            }
        }

        double totalScore = Config.SCORE_EQN.scoreFromSatAndTotal(currTotalQuartets, totalSat);

        // for(int i = 0; i < this.dc.realTaxaPartitionNodes.length; ++i){
        //     PartitionNode p = this.dc.realTaxaPartitionNodes[i];
        //     Utility.addArrayToFirst(realTaxaGains[i], p.data.gainsForSubTree);
        //     double totalQuartetsAfterTransferringi = 0;
        //     int partition = this.taxaPerLevel.inWhichPartition(i);
        //     for(BookKeepingPerTreeDC bkpt : this.bookKeepingPerTreeDCs){
        //         totalQuartetsAfterTransferringi += bkpt.totalQuartetsAfterSwap(i, 1 - partition);
        //     }
        //     realTaxaGains[i][partition] += totalSat;
        //     realTaxaGains[i][partition] = Config.SCORE_EQN.scoreFromSatAndTotal(totalQuartetsAfterTransferringi, realTaxaGains[i][partition]);
        //     realTaxaGains[i][partition] -= totalScore;   
        // }

        for(int i = 0; i < this.taxaPerLevel.realTaxonCount; ++i){
            RealTaxon rt = this.taxaPerLevel.realTaxa[i];
            int partition = this.taxaPerLevel.inWhichPartitionRealTaxonByIndex(i);
            Utility.addArrayToFirst(realTaxaGains[i], this.dc.realTaxaPartitionNodes[rt.id].data.gainsForSubTree);
            double totalQuartetsAfterTransferringi = 0;
            for(BookKeepingPerTreeDC bkpt : this.bookKeepingPerTreeDCs){
                totalQuartetsAfterTransferringi += bkpt.totalQuartetsAfterSwap(i, 1 - partition);
            }
            realTaxaGains[i][partition] += totalSat;
            realTaxaGains[i][partition] = Config.SCORE_EQN.scoreFromSatAndTotal(totalQuartetsAfterTransferringi, realTaxaGains[i][partition]);
            realTaxaGains[i][partition] -= totalScore;   
        }

        for(int i = 0; i < this.taxaPerLevel.dummyTaxonCount; ++i){
            
            dummyTaxaGains[i] = Config.SCORE_EQN.scoreFromSatAndTotal(
                dtTotals[i],
                dummyTaxaGains[i] + totalSat
            ) - totalScore;


        }



        return totalScore;
    }

    public void swapRealTaxon(int index){
        
        int partition = this.taxaPerLevel.inWhichPartitionRealTaxonByIndex(index);
        this.taxaPerLevel.swapPartitionRealTaxon(index);

        for(BookKeepingPerTreeDC bkpt : this.bookKeepingPerTreeDCs){
            bkpt.swapRealTaxon(index, partition);
        }

        Queue<PartitionNode> q = new ArrayDeque<>();
        // q.add(this.dc.realTaxaPartitionNodes[index]);
        q.addAll(this.dc.realTaxaPartitionNodes[index].parents);

        while(!q.isEmpty()){
            PartitionNode f = q.poll();
            f.data.branch.swapRealTaxa(partition);
            for(PartitionByTreeNodeWithIndex p : f.nodePartitions){
                p.partitionByTreeNode.scoreCalculator.swapRealTaxon(
                    p.index,
                    partition
                );

            }
            q.addAll(f.parents);
        }
    }

    public void swapDummyTaxon(int index){
        int partition = this.taxaPerLevel.inWhichPartitionDummyTaxonByIndex(index);
        this.taxaPerLevel.swapPartitionDummyTaxon(index);
        
        for(BookKeepingPerTreeDC bkpt : this.bookKeepingPerTreeDCs){
            bkpt.swapDummyTaxon(index, partition);
        }

        for(PartitionByTreeNode p : this.dc.partitionsByTreeNodes){
            p.scoreCalculator.swapDummyTaxon(index, partition);
        }

        Set<PartitionNode> st = new HashSet<>();
        
        Queue<PartitionNode> q = new ArrayDeque<>();
        DummyTaxon dt = this.taxaPerLevel.dummyTaxa[index];

        for(RealTaxon rt : dt.flattenedRealTaxa){
            for(PartitionNode p : this.dc.realTaxaPartitionNodes[rt.id].parents){
                if(st.add(p)){
                    q.add(p);
                }
            }
        }

        while(!q.isEmpty()){
            PartitionNode f = q.poll();
            f.data.branch.swapDummyTaxon(index, partition);
            for(PartitionNode p : f.parents){
                if(st.add(p)){
                    q.add(p);
                }
            }
        }

    }

    public void swapTaxon(int index, boolean isDummy){
        if(isDummy) this.swapDummyTaxon(index);
        else this.swapRealTaxon(index);
    }

    public TaxaPerLevelWithPartition[] divide(IMakePartition makePartition, boolean allowSingleton){
        RealTaxon[][] rts = new RealTaxon[2][];
        DummyTaxon[][] dts = new DummyTaxon[2][];


        for(int i = 0; i < 2; ++i){
            rts[i] = new RealTaxon[this.taxaPerLevel.getRealTaxonCountInPartition(i)];
            dts[i] = new DummyTaxon[this.taxaPerLevel.getDummyTaxonCountInPartition(i)];
            // var x = makePartition.makePartition(rts[i], dts[i]);
            // rtsPart[i] = x.realTaxonPartition;
            // dtsPart[i] = x.dummyTaxonPartition;
        }

        int[] index = new int[2];

        for(var x : this.taxaPerLevel.realTaxa){
            int part = this.taxaPerLevel.inWhichPartition(x.id);
            rts[part][index[part]++] = x;
        }
        index[0] = 0;
        index[1] = 0;
        int i = 0;
        for(var x : this.taxaPerLevel.dummyTaxa){
            int part = this.taxaPerLevel.inWhichPartitionDummyTaxonByIndex(i++);
            dts[part][index[part]++] = x;
        }


        // ith dummy taxon for ith partition
        DummyTaxon[] newDt = new DummyTaxon[2];
        
        

        // BookKeepingPerLevel[] bookKeepingPerLevels = new BookKeepingPerLevel[2];
        TaxaPerLevelWithPartition[] taxaPerLevelWithPartitions = new TaxaPerLevelWithPartition[2];
        for( i = 0; i < 2; ++i){
            newDt[i] = new DummyTaxon(rts[1 - i], dts[1 - i]);
            
            DummyTaxon[] dtsWithNewDt = new DummyTaxon[dts[i].length + 1];
            for(int j = 0; j < dts[i].length; ++j){
                dtsWithNewDt[j] = dts[i][j];
            }
            dtsWithNewDt[dtsWithNewDt.length - 1] = newDt[i];

            if(rts[i].length + dtsWithNewDt.length > 3){

                var y = makePartition.makePartition(rts[i], dtsWithNewDt, true);
                taxaPerLevelWithPartitions[i] = new TaxaPerLevelWithPartition(
                    rts[i], dtsWithNewDt, 
                    y.realTaxonPartition, 
                    y.dummyTaxonPartition, 
                    this.dc.taxa.length
                );
            }
            else{
                taxaPerLevelWithPartitions[i] = new TaxaPerLevelWithPartition(
                    rts[i], dtsWithNewDt, 
                    null, null,
                    this.dc.taxa.length
                );
            }
            
            // bookKeepingPerLevels[i] = new BookKeepingPerLevel(this.geneTrees,x);
        }

        return taxaPerLevelWithPartitions;
    }



}
