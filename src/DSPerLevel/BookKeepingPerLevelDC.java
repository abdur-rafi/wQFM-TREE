package src.DSPerLevel;

import java.util.ArrayList;

import src.Config;
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
}
