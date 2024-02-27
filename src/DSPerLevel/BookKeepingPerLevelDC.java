package src.DSPerLevel;


import src.Config;
import src.Utility;
import src.PreProcessing.Data;
import src.PreProcessing.DataContainer;
import src.PreProcessing.PartitionByTreeNode;
import src.PreProcessing.PartitionNode;
import src.ScoreCalculator.NumSatCalculatorBinaryNode;
import src.ScoreCalculator.NumSatCalculatorNodeE;
import src.Tree.Branch;

public class BookKeepingPerLevelDC {

    DataContainer dc;
    TaxaPerLevelWithPartition taxaPerLevel;
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
                p.scoreCalculator = new NumSatCalculatorNodeE(b,this.taxaPerLevel.dummyTaxonPartition);
            }
            else{
                p.scoreCalculator = new NumSatCalculatorBinaryNode(b, this.taxaPerLevel.dummyTaxonPartition);
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

        for(int i = 0; i < this.dc.realTaxaPartitionNodes.length; ++i){
            PartitionNode p = this.dc.realTaxaPartitionNodes[i];
            Utility.addArrayToFirst(realTaxaGains[i], p.data.gainsForSubTree);
            double totalQuartetsAfterTransferringi = 0;
            int partition = this.taxaPerLevel.inWhichPartition(i);
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

}
