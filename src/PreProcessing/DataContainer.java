package src.PreProcessing;

import java.util.ArrayList;
import java.util.Map;

import src.Taxon.RealTaxon;

public class DataContainer {

    public ArrayList<PartitionByTreeNode> partitionsByTreeNodes;
    public ArrayList<PartitionNode> topSortedPartitionNodes;
    public PartitionNode[] realTaxaPartitionNodes;
    public boolean[][] realTaxaInTrees;
    public RealTaxon[] taxa;

    public PartitionGraph partitionGraph;

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
