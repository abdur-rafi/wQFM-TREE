package src;

import src.BiPartition.BiPartitionTreeSpecific;
import src.GeneTree.GeneTree;
import src.ScoreCalculator.ScoreCalculatorTree;

public class Iteration {

    GeneTree tree;
    BiPartitionTreeSpecific partition;

    public Iteration(GeneTree tree, BiPartitionTreeSpecific partition){
        this.tree = tree;
        this.partition = partition;
    }

    public void step(){
        ScoreCalculatorTree calculator = new ScoreCalculatorTree(tree, partition);
        
    }
    
}
