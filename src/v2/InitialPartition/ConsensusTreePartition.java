package src.v2.InitialPartition;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import src.v2.Taxon.DummyTaxon;
import src.v2.Taxon.RealTaxon;
import src.v2.Tree.Branch;
import src.v2.Tree.Info;
import src.v2.Tree.Tree;
import src.v2.Tree.TreeNode;

public class ConsensusTreePartition implements IMakePartition {

    Tree consTree;

    public ConsensusTreePartition(String line, Map<String, RealTaxon> taxaMap){
        this.consTree = new Tree(line, taxaMap);

    }

    // private void dfs(TreeNode currNode, RealTaxon[] rts, DummyTaxon[] dts){
    //     currNode.info = new Info();
    //     currNode.info.branches = new Branch[1];
    //     currNode.info.branches[0] = new Branch(dts.length);

    //     for(var x :)
    // }


    private void assignSubTreeToPartition(TreeNode node, short[] rtsp, Map<Integer, Integer> idToIndex){
        if(node.isLeaf()){
            if(idToIndex.containsKey(node.taxon.id)){
                rtsp[idToIndex.get(node.taxon.id)] = 1;
            }
        }
        else{
            for(var child : node.childs){
                assignSubTreeToPartition(child, rtsp,idToIndex);
            }
        }
    }

    @Override
    public MakePartitionReturnType makePartition(RealTaxon[] rts, DummyTaxon[] dts) {
        
        double[] weight = new double[consTree.leavesCount];
        int[] inWhichDummyTaxa = new int[consTree.leavesCount];

        TreeNode minNode = null;
        double minDiff = 0;

        for(var x : rts){
            weight[x.id] = 1;
        }
        // for(var x : weight)
        //     System.out.println(x);

        int i = 0;
        for(var x : dts){
            double sz = x.flattenedTaxonCount;
            for(var y : x.flattenedRealTaxa){
                weight[y.id] = 1./sz;
                inWhichDummyTaxa[y.id] = i;
            }
            ++i;
        }

        for(var node : this.consTree.topSortedNodes){
            node.info = new Info();
            node.info.branches = new Branch[1];
            node.info.branches[0] = new Branch(dts.length);

            var branch = node.info.branches[0];

            if(node.isLeaf()){
                double w = weight[node.taxon.id];
                if(w == 1){
                    branch.realTaxaCounts[0] = 1;
                    branch.totalTaxaCounts[0] = 1;
                }
                else if(w != 0) {
                    branch.totalTaxaCounts[0] = w;
                    branch.dummyTaxaWeightsIndividual[inWhichDummyTaxa[node.taxon.id]] = w;
                }
            }
            else{
                for(var child : node.childs){

                    int partASize = child.info.branches[0].realTaxaCounts[0];
                    int partBSize = rts.length - partASize;


                    for(int j = 0; j < dts.length; ++j){
                        branch.dummyTaxaWeightsIndividual[j] += child.info.branches[0].dummyTaxaWeightsIndividual[j];
                        if(child.info.branches[0].dummyTaxaWeightsIndividual[j] >= .5){
                            partASize++;
                        }
                        else{
                            partBSize++;
                        }
                    }
                    branch.totalTaxaCounts[0] += child.info.branches[0].totalTaxaCounts[0];
                    branch.realTaxaCounts[0] += child.info.branches[0].realTaxaCounts[0];

                    if(partASize > 1 && partBSize > 1){
                        double diff = Math.abs(rts.length + dts.length - child.info.branches[0].totalTaxaCounts[0]);
                        if(minNode == null || diff < minDiff){
                            minNode = child;
                            minDiff = diff;
                        }
                        // else if(diff < minDiff){
                        //     minNode = child;
                        //     minDiff = diff;
                        // }
                    }

                }
            }
        }
        if(minNode == null){
            System.out.println("Min Node null");
            System.exit(-1);
        }
        System.out.println("partition");
        short[] rtsP = new short[rts.length];
        short[] dtsp = new short[dts.length];

        Map<Integer, Integer> idToIndex = new HashMap<>();
        i = 0;
        for(var x : rts){
            idToIndex.put(x.id, i++);
        }
    
        assignSubTreeToPartition(minNode, rtsP, idToIndex);

        for(i = 0; i < dts.length; ++i){
            if(minNode.info.branches[0].dummyTaxaWeightsIndividual[i] >= .5){
                dtsp[i] = 1;
            }
        }

        return new MakePartitionReturnType(rtsP, dtsp);

    }
    
    
}
