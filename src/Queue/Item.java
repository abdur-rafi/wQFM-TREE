package src.Queue;

import src.DSPerLevel.TaxaPerLevelWithPartition;
import src.SolutionTree.SolutionNode;

public class Item {
    
    TaxaPerLevelWithPartition taxaPerLevelWithPartition;
    SolutionNode solutionNode;
    int level;

    public Item(TaxaPerLevelWithPartition taxaPerLevelWithPartition, SolutionNode solutionNode, int level){
        this.taxaPerLevelWithPartition = taxaPerLevelWithPartition;
        this.solutionNode = solutionNode;
        this.level = level;
    }

}
