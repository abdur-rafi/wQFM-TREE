package src.PreProcessing;

import java.util.ArrayList;
import java.util.Map;

import src.Taxon.RealTaxon;

public class DataContainer {

    public ArrayList<InternalNode> internalNodes;
    public ArrayList<Component> topSortedComponents;
    // public ArrayList<PartitionNode> topSortedForBranch;
    // public ArrayList<PartitionNode> topSortedForGain;
    public Component[] realTaxaComponents;
    public boolean[][] realTaxaInTrees;
    public RealTaxon[] taxa;

    public ComponentGraph componentGraph;
    public Component sentinel;

    // public DataContainer(
    //     ArrayList<PartitionsByTreeNode> partitionsByTreeNodes, 
    //     ArrayList<PartitionNode> topSortedPartitionNodes, 
    //     PartitionNode[] realTaxaPartitionNodes, 
    //     ArrayList<RealTaxon>[] realTaxonsInTrees, 
    //     RealTaxon[] taxa
    // ){
    //     this.partitionsByTreeNodes = partitionsByTreeNodes;
    //     this.topSortedPartitionNodes = topSortedPartitionNodes;
    //     this.realTaxaPartitionNodes = realTaxaPartitionNodes;
    //     this.realTaxonsInTrees = realTaxonsInTrees;
    //     this.taxa = taxa;
    // }


}
