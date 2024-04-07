package src.ScoreCalculator;

import src.Tree.Branch;

public class NumSatSQBin implements NumSatSQ{
    // Branch[] branches;
    Branch[] common, uniques;
    Branch uniquesParent;

    int nDummyTaxa;
    int[] dummyTaxaPartition;
    double[][] pairsFromBranch;
    double[][] pairsWithParentBranch;
    double[] pairsLR;

    // double[] pairsABFromBranch;
    // double[] pairsABWithParentBranch;
    

    // double[] pairsFromLeftRightBranch;

    // double pairsABFromLeftRightBranch;

    double EPS = 0.000001;
    
    
    public NumSatSQBin(Branch[] common, Branch[] uniques, Branch uniquesParent, int[] dummyTaxaToPartitionMap){

        this.common = common;
        this.uniques = uniques;
        this.uniquesParent = uniquesParent;
        this.dummyTaxaPartition = dummyTaxaToPartitionMap;
        this.pairsFromBranch = new double[2][2];
        this.pairsWithParentBranch = new double[2][2];
        this.pairsLR = new double[2];
        // this.pairsABFromBranch = new double[2];
        // this.pairsABWithParentBranch = new double[2];

        this.nDummyTaxa = common[0].dummyTaxaWeightsIndividual.length;

        double[] totalParent = new double[2];
        totalParent[0] = uniquesParent.totalTaxaCounts[0] + common[0].totalTaxaCounts[0] + common[1].totalTaxaCounts[0];
        totalParent[1] = uniquesParent.totalTaxaCounts[1] + common[0].totalTaxaCounts[1] + common[1].totalTaxaCounts[1];

        for(int i = 0; i < 2; ++i){
            double[] totalInBranch = new double[2];

            for(int p = 0; p < 2; ++p){

                totalInBranch[p] = common[i].totalTaxaCounts[p] + uniques[i].totalTaxaCounts[p];
                this.pairsFromBranch[i][p] = totalInBranch[p] * totalInBranch[p] - totalInBranch[p];
                this.pairsWithParentBranch[i][p] = totalInBranch[p] * totalParent[p] - common[i].realTaxaCounts[p];
                // this.pairsABFromBranch[i] = totalInBranch[0] * totalInBranch[1];
                
                // this.pairsABWithParentBranch[i] = totalInBranch[0] * totalParent[1] + totalInBranch[1] * totalParent[0];

            }

            for(int j = 0; j < this.nDummyTaxa; ++j){
                int partition = this.dummyTaxaPartition[j];
                double totalWi = common[i].dummyTaxaWeightsIndividual[j] + uniques[i].dummyTaxaWeightsIndividual[j];
                double totalPi = uniquesParent.dummyTaxaWeightsIndividual[j] + common[0].dummyTaxaWeightsIndividual[j] + common[1].dummyTaxaWeightsIndividual[j];

                this.pairsFromBranch[i][partition] -= totalWi * totalWi;
                this.pairsWithParentBranch[i][partition] -= totalWi * totalPi;
            }

            this.pairsFromBranch[i][0] /= 2;
            this.pairsFromBranch[i][1] /= 2;

            // this.pairsABFromBranch[i] = b[i].totalTaxaCounts[0] * b[i].totalTaxaCounts[1];
        }
        for(int p = 0; p < 2; ++p){
            this.pairsLR[p] = (common[0].totalTaxaCounts[p] + uniques[0].totalTaxaCounts[p]) * (common[1].totalTaxaCounts[p] + uniques[1].totalTaxaCounts[p]);
        }
        for(int j = 0; j < this.nDummyTaxa; ++j){

            int partition = this.dummyTaxaPartition[j];
            double totalWi = common[0].dummyTaxaWeightsIndividual[j] + uniques[0].dummyTaxaWeightsIndividual[j];
            double totalWi2 = common[1].dummyTaxaWeightsIndividual[j] + uniques[1].dummyTaxaWeightsIndividual[j];
            this.pairsLR[partition] -= totalWi * totalWi2;
        }
        // this.pairsFromLeftRightBranch[0] = b[0].totalTaxaCounts[0] * b[1].totalTaxaCounts[0];
        // this.pairsFromLeftRightBranch[1] = b[0].totalTaxaCounts[1] * b[1].totalTaxaCounts[1];

        // for(int j = 0; j < this.nDummyTaxa; ++j){
        //     int partition = this.dummyTaxaPartition[j];
        //     this.pairsFromLeftRightBranch[partition] -= b[0].dummyTaxaWeightsIndividual[j] * b[1].dummyTaxaWeightsIndividual[j];
        // }

        // this.pairsABFromLeftRightBranch = b[0].totalTaxaCounts[0] * b[1].totalTaxaCounts[1] + b[0].totalTaxaCounts[1] * b[1].totalTaxaCounts[0];

    }

    @Override
    public double score() {
        double score = 0;
        // score += pairsFromBranch[0][0] * pairsFromBranch[1][1];
        // score += pairsFromBranch[0][1] * pairsFromBranch[1][0];
        
        // score += pairsFromBranch[0][0] * pairsWithParentBranch[1][1];
        // score += pairsFromBranch[0][1] * pairsWithParentBranch[1][0];
        // score += pairsFromBranch[1][0] * pairsWithParentBranch[0][1];
        // score += pairsFromBranch[1][1] * pairsWithParentBranch[0][0];

        score -= pairsLR[0] * pairsLR[1];
        score -= pairsLR[0] * ( pairsWithParentBranch[0][1] + pairsWithParentBranch[1][1] );
        score -= pairsLR[1] * ( pairsWithParentBranch[0][0] + pairsWithParentBranch[1][0] );
        // score -= pairsWithParentBranch[0][0] * pairsWithParentBranch[0][1];
        // score -= pairsWithParentBranch[1][0] * pairsWithParentBranch[1][1];

        
        // score -= pairsABFromBranch[0] * pairsABFromBranch[1];
        // score -= pairsABFromBranch[0] * pairsABWithParentBranch[1];
        // score -= pairsABFromBranch[1] * pairsABWithParentBranch[0];

        System.out.println("node score : " + score);

        // // print common and unique branches
        for(int i = 0; i < 2; ++i){
            System.out.println("branch " + i + " common total taxon count : " + common[i].totalTaxaCounts[0] + " " + common[i].totalTaxaCounts[1]);
            System.out.println("branch " + i + " unique total taxon count : " + uniques[i].totalTaxaCounts[0] + " " + uniques[i].totalTaxaCounts[1]);
            // print pairs
            System.out.println("pairs from branch " + i + " : " + pairsFromBranch[i][0] + " " + pairsFromBranch[i][1]);
            System.out.println("pairs with parent branch " + i + " : " + pairsWithParentBranch[i][0] + " " + pairsWithParentBranch[i][1]);
            
        }

        // for(int i = 0; i < 2; ++i){
        //     System.out.println("branch " + i + " total taxon count : " + branches[i].totalTaxaCounts[0] + " " + branches[i].totalTaxaCounts[1]);
        //     // print pairs
        //     System.out.println("pairs from branch " + i + " : " + pairsFromBranch[i][0] + " " + pairsFromBranch[i][1]);
        //     System.out.println("pairs with parent branch " + i + " : " + pairsWithParentBranch[i][0] + " " + pairsWithParentBranch[i][1]);
            
        // }

        // score += pairsFromLeftRightBranch[0] * pairsFromBranch[2][1];
        // score += pairsFromLeftRightBranch[1] * pairsFromBranch[2][0];

        // score -= pairsABFromBranch[0] * pairsABFromBranch[1];
        // score -= pairsABFromLeftRightBranch * pairsABFromBranch[2];

        return score;
    }

    // [branch index][common, disjoints][partition]
    @Override
    public double[][] gainRealTaxa(double originalScore, double multiplier) {
        double[][] gains = new double[3][2];
        // for(int i = 0; i < 2; ++i){
        //     for(int p = 0; p < 2; ++p){
        //         if(this.branches[i].realTaxaCounts[p] > 0){
        //             this.swapRealTaxon(i, p);
        //             this.branches[i].swapRealTaxa(p);
        //             gains[i][1][p] = multiplier * (this.score() - originalScore);
        //             if(this.branches[2].realTaxaCounts[p] > 0){
        //                 this.swapRealTaxon(2, p);
        //                 this.branches[2].swapRealTaxa(p);
        //                 gains[i][0][p] = multiplier * (this.score() - originalScore);
        //                 this.swapRealTaxon(2, 1 - p);
        //                 this.branches[2].swapRealTaxa(1 - p);
        //             }
        //             this.swapRealTaxon(i, 1 - p);
        //             this.branches[i].swapRealTaxa(1 - p);
        //         }
        //     }
        // }
        // for(int p = 0; p < 2; ++p){
        //     if(this.branches[2].realTaxaCounts[p] > 0){
        //         this.swapRealTaxon(2, p);
        //         this.branches[2].swapRealTaxa(p);
        //         gains[2][1][p] = multiplier * (this.score() - originalScore);
        //         this.swapRealTaxon(2, 1 - p);
        //         this.branches[2].swapRealTaxa(1 - p);
        //     }
        // }
        return gains;
    }

    @Override
    public void swapRealTaxon(int branchIndex, int currPartition) {
        // if(branchIndex < 2){
        //     this.pairsFromBranch[branchIndex][currPartition] -= this.branches[branchIndex].totalTaxaCounts[currPartition] - 1;
        //     this.pairsFromBranch[branchIndex][1 - currPartition] += this.branches[branchIndex].totalTaxaCounts[1 - currPartition];
        //     this.pairsABFromBranch[branchIndex] -= this.branches[branchIndex].totalTaxaCounts[1 - currPartition];
        //     this.pairsABFromBranch[branchIndex] += this.branches[branchIndex].totalTaxaCounts[currPartition] - 1;
            
        //     this.pairsWithParentBranch[branchIndex][currPartition] -= this.branches[2].totalTaxaCounts[currPartition];
        //     this.pairsWithParentBranch[branchIndex][1 - currPartition] += this.branches[2].totalTaxaCounts[1 - currPartition];
        // }
        // else{
        //     for(int i = 0; i < 2; ++i){
        //         this.pairsWithParentBranch[i][currPartition] -= this.branches[i].totalTaxaCounts[currPartition];
        //         this.pairsWithParentBranch[i][1 - currPartition] += this.branches[i].totalTaxaCounts[1 - currPartition];
        //     }
        // }



        // if(branchIndex < 2){
        //     this.pairsFromLeftRightBranch[currPartition] -= this.branches[1 - branchIndex].totalTaxaCounts[currPartition];
        //     this.pairsFromLeftRightBranch[1 - currPartition] += this.branches[1 - branchIndex].totalTaxaCounts[1 - currPartition];
        //     this.pairsABFromLeftRightBranch = (
        //         (this.branches[branchIndex].totalTaxaCounts[currPartition] - 1) * this.branches[1 - branchIndex].totalTaxaCounts[1 - currPartition] 
        //         + (this.branches[branchIndex].totalTaxaCounts[1 - currPartition] + 1 ) * (this.branches[1 - branchIndex].totalTaxaCounts[currPartition])
        //     );
            
        // }
    }

    @Override
    public void swapDummyTaxon(int dummyIndex, int currPartition) {
        // double wp = this.branches[2].dummyTaxaWeightsIndividual[dummyIndex];
        // for(int i = 0; i < this.branches.length - 1; ++i){
        //     double wi = this.branches[i].dummyTaxaWeightsIndividual[dummyIndex];
        //     this.pairsFromBranch[i][currPartition] -= (this.branches[i].totalTaxaCounts[currPartition] - wi) * wi;
        //     this.pairsFromBranch[i][1 - currPartition] += (this.branches[i].totalTaxaCounts[1 - currPartition]) * wi;
        //     this.pairsABFromBranch[i] -= wi * this.branches[i].totalTaxaCounts[1 - currPartition];
        //     this.pairsABFromBranch[i] += wi * (this.branches[i].totalTaxaCounts[currPartition] - wi);

        //     this.pairsWithParentBranch[i][currPartition] -= (wi * (this.branches[2].totalTaxaCounts[currPartition] - wp) + wp * (this.branches[i].totalTaxaCounts[currPartition] - wi));
        //     this.pairsWithParentBranch[i][1 - currPartition] += this.branches[2].totalTaxaCounts[1 - currPartition] * wi + wp * this.branches[i].totalTaxaCounts[1 - currPartition];
        // }
        // double wl = this.branches[0].dummyTaxaWeightsIndividual[dummyIndex];
        // double wr = this.branches[1].dummyTaxaWeightsIndividual[dummyIndex];

        // this.pairsFromLeftRightBranch[currPartition] -= (wl * (this.branches[1].totalTaxaCounts[currPartition] - wr) + wr * (this.branches[0].totalTaxaCounts[currPartition] - wl));
        // this.pairsFromLeftRightBranch[1 - currPartition] += (wl * this.branches[1].totalTaxaCounts[1 - currPartition] + wr * this.branches[0].totalTaxaCounts[1 - currPartition]);
        
        // this.pairsABFromLeftRightBranch = (
        //     this.branches[0].totalTaxaCounts[0] * this.branches[1].totalTaxaCounts[1] +
        //     this.branches[0].totalTaxaCounts[1] * this.branches[1].totalTaxaCounts[0]
        // );

    }

    @Override
    public void gainDummyTaxa(double originalScore, double multiplier, double[] dummyTaxaGains) {
        // for(int i = 0; i < this.nDummyTaxa; ++i){
        //     int currPartition = this.dummyTaxaPartition[i];
        //     this.swapDummyTaxon(i, currPartition);

        //     for(int j = 0; j < this.branches.length; ++j){
        //         this.branches[j].swapDummyTaxon(i, currPartition);
        //     }
        //     dummyTaxaGains[i] += multiplier * (this.score() - originalScore);
            
        //     this.swapDummyTaxon(i, 1 - currPartition);
        //     for(int j = 0; j < this.branches.length; ++j){
        //         this.branches[j].swapDummyTaxon(i, 1 - currPartition);
        //     }
        // }
    }

    @Override
    public void batchTransferRealTaxon(int branchIndex, int netTranser) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'batchTransferRealTaxon'");
    }

    
    
}
