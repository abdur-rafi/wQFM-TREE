package src.ConsensusTree;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

import src.GeneTree.GeneTree;
import src.GeneTree.Info;
import src.GeneTree.TreeNode;

public class ConsensusTreeFlat implements IMakeParition {

    private GeneTree consensusTree;
    private double totalScore;
    double minDiff;

    private int totalRealTaxa;
    private int[] dummyTaxaSizes;

    TreeNode minNode;

    private void dfs(TreeNode node){

        double currScore = node.info.consensusScore;
        int partitionSizeA = 0;
        if(!node.isLeaf()){
            for(var x : node.childs){
                dfs(x);
                currScore += x.info.consensusScore;
                for(int i = 0; i < node.info.dummyTaxaCountIndividual.length; ++i){
                    node.info.dummyTaxaCountIndividual[i] += x.info.dummyTaxaCountIndividual[i];
                }
                node.info.realTaxaCountTotal[0] += x.info.realTaxaCountTotal[0];
            }
            partitionSizeA = node.info.realTaxaCountTotal[0];
            for(int i = 0; i < node.info.dummyTaxaCountIndividual.length; ++i){
                if(2 * node.info.dummyTaxaCountIndividual[i] - dummyTaxaSizes[i] >= 0){
                    partitionSizeA++;
                }
            }
        }
        else{
            partitionSizeA = node.info.realTaxaCountTotal[0];
        }

        int partitionSizeB = totalRealTaxa + dummyTaxaSizes.length - partitionSizeA;

        double diff = Math.abs(currScore - totalScore);
        if(diff < minDiff && partitionSizeA > 1 && partitionSizeB > 1){
            minDiff = diff;
            minNode = node;
        }
    }

    private void dfsPartition(TreeNode node, Map<String, Integer> realTaxaMap, Map<Integer, Integer> dummyTaxaMap ){
        if(node.isLeaf()){
            if(node.info.realTaxaCountTotal[0] == 1){
                realTaxaMap.put(node.label, 0);
            }
        }
        else{
            for(var x : node.childs){
                dfsPartition(x, realTaxaMap, dummyTaxaMap);
            }
        }
    }

    public ConsensusTreeFlat(String line){
        consensusTree = new GeneTree(line);
    }

    
    public void makePartition(
        Set<String> realTaxas, 
        ArrayList<Set<String>> dummyTaxas ,
        Map<String, Integer> realTaxaPartitionMap,
        Map<Integer, Integer> dummyTaxaPartitionMap,
        int[] partitionSize

    ){
        this.totalRealTaxa = realTaxas.size();
        this.dummyTaxaSizes = new int[dummyTaxas.size()];
        this.minNode = null;

        for(var x : consensusTree.nodes){
            x.info = new Info();
            x.info.dummyTaxaCountIndividual = new double[dummyTaxas.size()];
            x.info.realTaxaCountTotal = new int[1];
        }

        this.totalScore = realTaxas.size();

        for(var x : realTaxas){
            int index = consensusTree.taxaMap.get(x);
            consensusTree.nodes.get(index).info.consensusScore += 1;
            consensusTree.nodes.get(index).info.realTaxaCountTotal[0] += 1;

        }

        for(int i = 0; i < dummyTaxas.size(); ++i){
            for(var x : dummyTaxas.get(i)){
                int index = consensusTree.taxaMap.get(x);
                consensusTree.nodes.get(index).info.dummyTaxaCountIndividual[i] += 1;
                consensusTree.nodes.get(index).info.consensusScore += 1. / dummyTaxas.get(i).size();
                // consensusTree.nodes.get(index).info.consensusScore += 1;

            }
            this.dummyTaxaSizes[i] = dummyTaxas.get(i).size();
            this.totalScore += dummyTaxas.get(i).size();
            // this.totalScore += 1;

        }

        this.minDiff = 10000000;


        dfs(consensusTree.root);

        if(minNode == null){
            System.out.println("minNode is null");
            System.exit(0);
        }

        dfsPartition(minNode, realTaxaPartitionMap, dummyTaxaPartitionMap);
        partitionSize[0] = realTaxaPartitionMap.size();

        for(int i = 0; i < minNode.info.dummyTaxaCountIndividual.length; ++i){
            if(2 * minNode.info.dummyTaxaCountIndividual[i] - dummyTaxaSizes[i] >= 0){
                dummyTaxaPartitionMap.put(i, 0);
                partitionSize[0]++;
            }
            else{
                dummyTaxaPartitionMap.put(i, 1);
            }
        }
        partitionSize[1] = realTaxas.size() + dummyTaxas.size() - partitionSize[0];

        for(var x : realTaxas){
            if(!realTaxaPartitionMap.containsKey(x)){
                realTaxaPartitionMap.put(x, 1);
            }
        }


    }

    
}
