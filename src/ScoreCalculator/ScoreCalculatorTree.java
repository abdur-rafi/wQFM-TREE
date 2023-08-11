package src.ScoreCalculator;

import src.Utility;
import src.BiPartition.BiPartitionTreeSpecific;
import src.GeneTree.GeneTree;
import src.GeneTree.Info;
import src.GeneTree.TreeNode;

public class ScoreCalculatorTree {
    GeneTree tree;
    BiPartitionTreeSpecific bp;
    // ArrayList<Set<Integer>> realTaxaPartition;
    // Map<Integer, Integer> taxaToDummyTaxaMap;
    // int[] dummyTaxaToPartitionMap;
    // int[] dummyTaxaSizeIndividual;
    // Set<Integer> realTaxas;
    // int[] realTaxaPartitionSize;
    // int[] dummyTaxaPartitionSize;
    int score;
    int[] globalGains;
    int[] dummyTaxaGains;

    public ScoreCalculatorTree(
        GeneTree tree,
        BiPartitionTreeSpecific bp
    ){
        this.tree = tree;
        this.bp = bp;
        this.score = 0;
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
            this.score += sc;
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

    // private int[] getTotalSatOrVioQuartet(){
    //     int[] p = new int[2];
    //     for(int i = 0; i < 2; ++i){
    //         p[i] = Utility.nc2(this.bp.totalPartitionSize(i));
    //     }
    //     for(int i = 0; i < this.bp.nDummyTaxa(); ++i){
    //         int inWhichPartition = this.bp.inWhichPartition(i, false);
    //         p[inWhichPartition] -= Utility.nc2(this.bp.dummyTaxaSizeIndividual(i));
    //     }

    //     return p;
    // }

    public int score(){
        calcReachableInSubtree(tree.root);
        tree.root.info.gains = new int[2];
        collectGains(tree.root);

        int[] p = new int[2];
        for(int i = 0; i < this.bp.nDummyTaxa(); ++i){
            int inWhichPartition = this.bp.inWhichPartition(i, false);
            p[inWhichPartition] -= Utility.nc2(this.bp.dummyTaxaSizeIndividual(i));
        }

        // B to A Real Taxa
        p[0] += Utility.nc2(this.bp.totalPartitionSize(0) + 1);
        p[1] += Utility.nc2(this.bp.totalPartitionSize(1) - 1);


        int[] totals = new int[2];
        totals[1] = p[0] * p[1];

        p[0] -= Utility.nc2(this.bp.totalPartitionSize(0) + 1);
        p[1] -= Utility.nc2(this.bp.totalPartitionSize(1) - 1);

        // A to B Real Taxa
        p[0] += Utility.nc2(this.bp.totalPartitionSize(0) - 1);
        p[1] += Utility.nc2(this.bp.totalPartitionSize(1) + 1);

        totals[0] = p[0] * p[1];



        for(var x : tree.nodes){
            if(x.isLeaf() && this.bp.isRealTaxa(x.index)){
                Utility.addIntArrToFirst(x.info.gains, globalGains);
                int part = this.bp.inWhichPartition(x.index, true);
                x.info.gains[part] += this.score ;
                x.info.gains[part] = 2 * x.info.gains[part] - totals[part];
                // System.out.println(x.label + " : A-> B: " + x.info.gains[0] + " B->A: " + x.info.gains[1] + "\n");
            }
        }

        p[0] -= Utility.nc2(this.bp.totalPartitionSize(0) - 1);
        p[1] -= Utility.nc2(this.bp.totalPartitionSize(1) + 1);


        for(int i = 0; i < this.bp.nDummyTaxa(); ++i){
            int inWhichPartition = this.bp.inWhichPartition(i, false);
            p[inWhichPartition] += Utility.nc2(this.bp.dummyTaxaSizeIndividual(i));
            p[(inWhichPartition + 1) % 2] -= Utility.nc2(this.bp.dummyTaxaSizeIndividual(i));
            p[inWhichPartition] += Utility.nc2(this.bp.totalPartitionSize(inWhichPartition) - this.bp.dummyTaxaSizeIndividual(i));
            p[(inWhichPartition + 1) % 2] += Utility.nc2(this.bp.totalPartitionSize((inWhichPartition + 1) % 2) + this.bp.dummyTaxaSizeIndividual(i));
            this.dummyTaxaGains[i] = 2 * (this.dummyTaxaGains[i] + this.score) - p[0] * p[1];
            p[inWhichPartition] -= Utility.nc2(this.bp.dummyTaxaSizeIndividual(i));
            p[(inWhichPartition + 1) % 2] += Utility.nc2(this.bp.dummyTaxaSizeIndividual(i));
            p[inWhichPartition] -= Utility.nc2(this.bp.totalPartitionSize(inWhichPartition) - this.bp.dummyTaxaSizeIndividual(i));
            p[(inWhichPartition + 1) % 2] -= Utility.nc2(this.bp.totalPartitionSize((inWhichPartition + 1) % 2) + this.bp.dummyTaxaSizeIndividual(i));
            // System.out.println("Dummy Taxa: " + i + ": " + this.dummyTaxaGains[i]);

        }

        p[0] += Utility.nc2(this.bp.totalPartitionSize(0));
        p[1] += Utility.nc2(this.bp.totalPartitionSize(1));

        // score[1] = (p[0] * p[1]) - score[0];
        this.score = 2 * this.score - p[0] * p[1];
        for(int i = 0; i < this.bp.nDummyTaxa(); ++i){
            this.dummyTaxaGains[i] -= this.score;
            System.out.println("Dummy Taxa: " + i + ": " + this.dummyTaxaGains[i]);

        }

        return this.score;
    }
}
