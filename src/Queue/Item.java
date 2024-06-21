package src.Queue;

import src.DSPerLevel.TaxaPerLevelWithPartition;
import src.SolutionTree.SolutionNode;

public class Item implements Comparable<Item> {
    
    TaxaPerLevelWithPartition taxaPerLevelWithPartition;
    SolutionNode solutionNode;
    int level;

    public Item(TaxaPerLevelWithPartition taxaPerLevelWithPartition, SolutionNode solutionNode, int level){
        this.taxaPerLevelWithPartition = taxaPerLevelWithPartition;
        this.solutionNode = solutionNode;
        this.level = level;
    }

    @Override
    public int compareTo(Item other) {
        // Implement comparison logic based on priority
        return -Integer.compare(
            this.taxaPerLevelWithPartition.realTaxonCount + this.taxaPerLevelWithPartition.dummyTaxonCount,
            other.taxaPerLevelWithPartition.realTaxonCount + other.taxaPerLevelWithPartition.dummyTaxonCount 
        );
    }

}
