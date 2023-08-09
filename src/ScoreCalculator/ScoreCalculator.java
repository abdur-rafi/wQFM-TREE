package src.ScoreCalculator;

import src.Utility;
import src.BiPartition.BiPartition;
import src.GeneTree.GeneTree;
import src.GeneTree.Info;
import src.GeneTree.TreeNode;

public class ScoreCalculator {
    GeneTree tree;
    BiPartition bp;
    // ArrayList<Set<Integer>> realTaxaPartition;
    // Map<Integer, Integer> taxaToDummyTaxaMap;
    // int[] dummyTaxaToPartitionMap;
    // int[] dummyTaxaSizeIndividual;
    // Set<Integer> realTaxas;
    // int[] realTaxaPartitionSize;
    // int[] dummyTaxaPartitionSize;
    int[] score;
    int[] globalGains;
    int[] dummyTaxaGains;

    public ScoreCalculator(
        GeneTree tree,
        BiPartition bp
        // ArrayList<Set<Integer>> partition,
        // Map<Integer, Integer> taxaToDummyTaxaMap,
        // int[] dummyTaxaToPartitionMap,
        // int[] dummyTaxaSize,
        // int[] dummyTaxaPartitionSize
    ){
        this.tree = tree;
        this.bp = bp;
        // this.realTaxaPartition = partition;
        // this.taxaToDummyTaxaMap = taxaToDummyTaxaMap;
        // this.dummyTaxaToPartitionMap = dummyTaxaToPartitionMap;
        // this.dummyTaxaSizeIndividual = dummyTaxaSize;
        // this.realTaxas = new HashSet<>();
        // this.realTaxas.addAll(this.realTaxaPartition.get(0));
        // this.realTaxas.addAll(this.realTaxaPartition.get(1));
        // this.realTaxaPartitionSize = new int[2];
        // this.realTaxaPartitionSize[0] = partition.get(0).size();
        // this.realTaxaPartitionSize[1] = partition.get(1).size();
        // this.dummyTaxaPartitionSize = dummyTaxaPartitionSize;
        this.score = new int[2];
        this.globalGains = new int[2];
        this.dummyTaxaGains = new int[this.bp.nDummyTaxa()];
    }


    private void calcReachableInSubtree(TreeNode node){
        int[] realTaxaCountsTotal = new int[2];
        int[] dummyTaxaCountTotal = new int[2];
        int[] dummyTaxaCountIndi = new int[this.bp.nDummyTaxa()];

        if (node.childs == null){
            if(this.bp.isRealTaxa(node.index)){
                realTaxaCountsTotal[this.bp.inWhichPartition(node.index, true)]++;
            }
            else if(this.bp.isDummyTaxa(node.index)){

                int index = this.bp.inWhichDummyTaxa(node.index);
                int partition = this.bp.inWhichPartition(index, false);
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
            b[0] = new Branch(c1.info.realTaxaCountTotal, c1.info.dummyTaxaCountIndividual, c1.info.dummyTaxaCountTotal, this.bp.getDummyTaxaPartitionMap());
            b[1] = new Branch(c2.info.realTaxaCountTotal, c2.info.dummyTaxaCountIndividual, c2.info.dummyTaxaCountTotal, this.bp.getDummyTaxaPartitionMap());
            int[] realTaxaCountsTotalParent = new int[2];
            int[] dummyTaxaCountsTotalParent = new int[2];
            int[] dummyTaxaCountIndividualParent = new int[this.bp.nDummyTaxa()];
            
            for(int i = 0; i < 2; ++i){
                realTaxaCountsTotalParent[i] = this.bp.realPartitionSize(i) - 
                    (b[0].realTaxaCountsTotal[i] + b[1].realTaxaCountsTotal[i]);
                dummyTaxaCountsTotalParent[i] = this.bp.dummyPartitionSize(i) - (
                    b[0].dummyTaxaCountsTotal[i] + b[1].dummyTaxaCountsTotal[i]);
            }

            for(int i = 0; i < this.bp.nDummyTaxa(); ++i){
                dummyTaxaCountIndividualParent[i] = this.bp.dummyTaxaSizeIndividual(i) - (
                    b[0].dummyTaxaCountsIndividual[i] + b[1].dummyTaxaCountsIndividual[i]
                );
            }
            b[2] = new Branch(realTaxaCountsTotalParent, dummyTaxaCountIndividualParent, dummyTaxaCountsTotalParent, this.bp.getDummyTaxaPartitionMap());
            node.info.calculator = new ScoreCalculatorNode(b, this.bp.getDummyTaxaPartitionMap(), this.dummyTaxaGains);
            var sc = node.info.calculator.score();
            this.score[0] += sc;
            var gains = node.info.calculator.gain(sc);
            node.info.calculator.calcDummyTaxaGains(sc);
            var childs = node.childs;
            for(int i = 0; i < 2; ++i){
                Utility.subIntArrToFirst(gains[i], gains[2]);
                childs.get(i).info.gains = gains[i];
            }
            Utility.addIntArrToFirst(globalGains, gains[2]);
            
        }
    }

    void collectGains(TreeNode node){
        if(node.childs != null){
            for(int i = 0; i < 2; ++i){
                Utility.addIntArrToFirst(node.childs.get(i).info.gains, node.info.gains);
            }
            for(var x : node.childs)
                collectGains(x);
            
        }
    }

    public int[] score(){
        calcReachableInSubtree(tree.root);
        tree.root.info.gains = new int[2];
        collectGains(tree.root);
        for(var x : tree.nodes){
            if(x.isLeaf() && this.bp.isRealTaxa(x.index)){
                Utility.addIntArrToFirst(x.info.gains, globalGains);
                System.out.println(x.label + " : A-> B: " + x.info.gains[0] + " B->A: " + x.info.gains[1] + "\n");
            }
        }
        // calcReachableInSubtree(tree.root.childs.get(0));
        // calcReachableInSubtree(tree.root.childs.get(1));
        for(int i = 0; i < this.bp.nDummyTaxa(); ++i){
            System.out.println("Dummy Taxa: " + i + ": " + this.dummyTaxaGains[i]);
        }
        int[] p = new int[2];
        for(int i = 0; i < 2; ++i){
            p[i] = Utility.nc2(this.bp.realPartitionSize(i) + this.bp.dummyPartitionSize(i));
        }
        for(int i = 0; i < this.bp.nDummyTaxa(); ++i){
            int inWhichPartition = this.bp.inWhichPartition(i, false);
            p[inWhichPartition] -= Utility.nc2(this.bp.dummyTaxaSizeIndividual(i));
        }
        score[1] = (p[0] * p[1]) - score[0];
        return this.score;
    }
}
