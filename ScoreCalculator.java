package wqfm.dsGT;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ScoreCalculator {
    GeneTree tree;

    ArrayList<Set<Integer>> realTaxaPartition;
    Map<Integer, Integer> taxaToDummyTaxaMap;
    int[] dummyTaxaToPartitionMap;
    int[] dummyTaxaSizeIndividual;
    Set<Integer> realTaxas;
    int[] realTaxaPartitionSize;
    int[] dummyTaxaPartitionSize;
    int[] score;

    public ScoreCalculator(
        GeneTree tree,
        ArrayList<Set<Integer>> partition,
        Map<Integer, Integer> taxaToDummyTaxaMap,
        int[] dummyTaxaToPartitionMap,
        int[] dummyTaxaSize,
        int[] dummyTaxaPartitionSize
    ){
        this.tree = tree;
        this.realTaxaPartition = partition;
        this.taxaToDummyTaxaMap = taxaToDummyTaxaMap;
        this.dummyTaxaToPartitionMap = dummyTaxaToPartitionMap;
        this.dummyTaxaSizeIndividual = dummyTaxaSize;
        this.realTaxas = new HashSet<>();
        this.realTaxas.addAll(this.realTaxaPartition.get(0));
        this.realTaxas.addAll(this.realTaxaPartition.get(1));
        this.realTaxaPartitionSize = new int[2];
        this.realTaxaPartitionSize[0] = partition.get(0).size();
        this.realTaxaPartitionSize[1] = partition.get(1).size();
        this.dummyTaxaPartitionSize = dummyTaxaPartitionSize;
        this.score = new int[2];
    }


    private void calcReachableInSubtree(TreeNode node){
        int[] realTaxaCountsTotal = new int[2];
        int[] dummyTaxaCountTotal = new int[2];
        int[] dummyTaxaCountIndi = new int[this.dummyTaxaSizeIndividual.length];

        if (node.childs == null){
            if(this.realTaxas.contains(node.index)){
                for(int i = 0; i < 2; ++i){
                    if(this.realTaxaPartition.get(i).contains(node.index)){
                        realTaxaCountsTotal[i]++;
                        break;
                    }
                }    
            }
            else if(this.taxaToDummyTaxaMap.containsKey(node.index)){
                int index = this.taxaToDummyTaxaMap.get(node.index);
                int partition = this.dummyTaxaToPartitionMap[index];
                dummyTaxaCountIndi[index]++;
                dummyTaxaCountTotal[partition]++;
            }
        }
        else{
            for(var x : node.childs){
                calcReachableInSubtree(x);
                Utility.addIntArrToFirst(realTaxaCountsTotal, x.info.realTaxaCountTotal);
                Utility.addIntArrToFirst(dummyTaxaCountTotal, x.info.dummyTaxaCountTotal);
                Utility.addIntArrToFirst(dummyTaxaCountIndi, x.info.dummyTaxaCountIndividual);

            }
        }
        node.info = new Info(realTaxaCountsTotal, dummyTaxaCountIndi, dummyTaxaCountTotal, null );
        if(node.childs != null){
            var c1 = node.childs.get(0);
            var c2 = node.childs.get(1);
            Branch[] b = new Branch[3];
            b[0] = new Branch(c1.info.realTaxaCountTotal, c1.info.dummyTaxaCountIndividual, c1.info.dummyTaxaCountTotal, this.dummyTaxaToPartitionMap);
            b[1] = new Branch(c2.info.realTaxaCountTotal, c2.info.dummyTaxaCountIndividual, c2.info.dummyTaxaCountTotal, this.dummyTaxaToPartitionMap);
            int[] realTaxaCountsTotalParent = new int[2];
            int[] dummyTaxaCountsTotalParent = new int[2];
            int[] dummyTaxaCountIndividualParent = new int[this.dummyTaxaSizeIndividual.length];
            
            for(int i = 0; i < 2; ++i){
                realTaxaCountsTotalParent[i] = this.realTaxaPartitionSize[i] - 
                    (b[0].realTaxaCountsTotal[i] + b[1].realTaxaCountsTotal[i]);
                dummyTaxaCountsTotalParent[i] = this.dummyTaxaPartitionSize[i] - (
                    b[0].dummyTaxaCountsTotal[i] + b[1].dummyTaxaCountsTotal[i]);
            }

            for(int i = 0; i < this.dummyTaxaSizeIndividual.length; ++i){
                dummyTaxaCountIndividualParent[i] = this.dummyTaxaSizeIndividual[i] - (
                    b[0].dummyTaxaCountsIndividual[i] + b[1].dummyTaxaCountsIndividual[i]
                );
            }
            b[2] = new Branch(realTaxaCountsTotalParent, dummyTaxaCountIndividualParent, dummyTaxaCountsTotalParent, this.dummyTaxaToPartitionMap);
            node.info.calculator = new ScoreCalculatorNode(b, dummyTaxaToPartitionMap);
            var sc = node.info.calculator.score();
            Utility.addIntArrToFirst(this.score,sc);
            
        }
    }

    int[] score(){
        calcReachableInSubtree(tree.root.childs.get(0));
        calcReachableInSubtree(tree.root.childs.get(1));

        return this.score;
    }
}
