package src;

import src.BiPartition.BiPartition;
import src.GeneTree.GeneTree;
import src.ScoreCalculator.ScoreCalculator;

public class Iteration {

    GeneTree tree;
    BiPartition partition;

    public Iteration(GeneTree tree, BiPartition partition){
        this.tree = tree;
        this.partition = partition;
    }

    public void step(){
        ScoreCalculator calculator = new ScoreCalculator(tree, partition);
        
    }
    
}
