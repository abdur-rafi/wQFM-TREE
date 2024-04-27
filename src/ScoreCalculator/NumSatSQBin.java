package src.ScoreCalculator;

import src.Tree.Branch;

public class NumSatSQBin implements NumSatSQ{
    // Branch[] branches;
    Branch[] common, uniques;
    Branch uniquesParent;

    int nDummyTaxa;
    int[] dummyTaxaPartition;
    double[][] pairsFromBranch;
    // double[][] pairsWithParentBranch;
    double[][] pairsWithParentCommon, pairsWithParentUnique, pairsWithParentCommonAndUnique;
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
        // this.pairsWithParentBranch = new double[2][2];
        this.pairsWithParentCommon = new double[2][2];
        this.pairsWithParentUnique = new double[2][2];
        this.pairsWithParentCommonAndUnique = new double[2][2];

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
                this.pairsFromBranch[i][p] = totalInBranch[p] * totalInBranch[p] - (
                    this.common[i].realTaxaCounts[p] + this.uniques[i].realTaxaCounts[p]
                );
                // this.pairsWithParentBranch[i][p] = totalInBranch[p] * totalParent[p] - common[i].realTaxaCounts[p];

                this.pairsWithParentCommon[i][p] = common[i].totalTaxaCounts[p] * common[i].totalTaxaCounts[p] - common[i].realTaxaCounts[p];
                this.pairsWithParentUnique[i][p] = uniques[i].totalTaxaCounts[p] * (
                    uniquesParent.totalTaxaCounts[p] + common[1 - i].totalTaxaCounts[p]
                );
                this.pairsWithParentCommonAndUnique[i][p] = common[i].totalTaxaCounts[p] * (
                    uniquesParent.totalTaxaCounts[p] + common[1 - i].totalTaxaCounts[p] + uniques[i].totalTaxaCounts[p]
                );

            }

            for(int j = 0; j < this.nDummyTaxa; ++j){
                int partition = this.dummyTaxaPartition[j];
                double totalWi = common[i].dummyTaxaWeightsIndividual[j] + uniques[i].dummyTaxaWeightsIndividual[j];
                double totalPi = uniquesParent.dummyTaxaWeightsIndividual[j] + common[0].dummyTaxaWeightsIndividual[j] + common[1].dummyTaxaWeightsIndividual[j];

                this.pairsFromBranch[i][partition] -= totalWi * totalWi;
                // this.pairsWithParentBranch[i][partition] -= totalWi * totalPi;

                this.pairsWithParentCommon[i][partition] -= common[i].dummyTaxaWeightsIndividual[j] * common[i].dummyTaxaWeightsIndividual[j];
                this.pairsWithParentUnique[i][partition] -= uniques[i].dummyTaxaWeightsIndividual[j] * (
                    uniquesParent.dummyTaxaWeightsIndividual[j] + common[1 - i].dummyTaxaWeightsIndividual[j]
                );
                this.pairsWithParentCommonAndUnique[i][partition] -= common[i].dummyTaxaWeightsIndividual[j] * (
                    uniquesParent.dummyTaxaWeightsIndividual[j] + common[1 - i].dummyTaxaWeightsIndividual[j] + uniques[i].dummyTaxaWeightsIndividual[j]
                );
            }

            this.pairsFromBranch[i][0] /= 2;
            this.pairsFromBranch[i][1] /= 2;

            this.pairsWithParentCommon[i][0] /= 2;
            this.pairsWithParentCommon[i][1] /= 2;

            // this.pairsWithParentBranch[i][0] = pairsWithParentCommon[i][0] + pairsWithParentUnique[i][0] + pairsWithParentCommonAndUnique[i][0];
            // this.pairsWithParentBranch[i][1] = pairsWithParentCommon[i][1] + pairsWithParentUnique[i][1] + pairsWithParentCommonAndUnique[i][1];

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
    public double sat(){
        double sat = 0;
        double[][] pairsWithParentBranch = new double[2][2];

        pairsWithParentBranch[0][0] = pairsWithParentCommon[0][0] + pairsWithParentUnique[0][0] + pairsWithParentCommonAndUnique[0][0];
        pairsWithParentBranch[0][1] = pairsWithParentCommon[0][1] + pairsWithParentUnique[0][1] + pairsWithParentCommonAndUnique[0][1];
        pairsWithParentBranch[1][0] = pairsWithParentCommon[1][0] + pairsWithParentUnique[1][0] + pairsWithParentCommonAndUnique[1][0];
        pairsWithParentBranch[1][1] = pairsWithParentCommon[1][1] + pairsWithParentUnique[1][1] + pairsWithParentCommonAndUnique[1][1];


        sat += pairsFromBranch[0][0] * pairsFromBranch[1][1];
        sat += pairsFromBranch[0][1] * pairsFromBranch[1][0];
        
        sat += pairsFromBranch[0][0] * pairsWithParentBranch[1][1];
        sat += pairsFromBranch[0][1] * pairsWithParentBranch[1][0];
        sat += pairsFromBranch[1][0] * pairsWithParentBranch[0][1];
        sat += pairsFromBranch[1][1] * pairsWithParentBranch[0][0];

        return sat;

    }

    @Override
    public double vio(){
        double[][] pairsWithParentBranch = new double[2][2];

        pairsWithParentBranch[0][0] = pairsWithParentCommon[0][0] + pairsWithParentUnique[0][0] + pairsWithParentCommonAndUnique[0][0];
        pairsWithParentBranch[0][1] = pairsWithParentCommon[0][1] + pairsWithParentUnique[0][1] + pairsWithParentCommonAndUnique[0][1];
        pairsWithParentBranch[1][0] = pairsWithParentCommon[1][0] + pairsWithParentUnique[1][0] + pairsWithParentCommonAndUnique[1][0];
        pairsWithParentBranch[1][1] = pairsWithParentCommon[1][1] + pairsWithParentUnique[1][1] + pairsWithParentCommonAndUnique[1][1];

        double vio = 0;
        vio += pairsLR[0] * pairsLR[1];
        vio += pairsLR[0] * ( pairsWithParentBranch[0][1] + pairsWithParentBranch[1][1] );
        vio += pairsLR[1] * ( pairsWithParentBranch[0][0] + pairsWithParentBranch[1][0] );

        return vio;
    }

    @Override
    public double score() {
        double score = 0;
        double[][] pairsWithParentBranch = new double[2][2];

        pairsWithParentBranch[0][0] = pairsWithParentCommon[0][0] + pairsWithParentUnique[0][0] + pairsWithParentCommonAndUnique[0][0];
        pairsWithParentBranch[0][1] = pairsWithParentCommon[0][1] + pairsWithParentUnique[0][1] + pairsWithParentCommonAndUnique[0][1];
        pairsWithParentBranch[1][0] = pairsWithParentCommon[1][0] + pairsWithParentUnique[1][0] + pairsWithParentCommonAndUnique[1][0];
        pairsWithParentBranch[1][1] = pairsWithParentCommon[1][1] + pairsWithParentUnique[1][1] + pairsWithParentCommonAndUnique[1][1];


        score += pairsFromBranch[0][0] * pairsFromBranch[1][1];
        score += pairsFromBranch[0][1] * pairsFromBranch[1][0];
        
        score += pairsFromBranch[0][0] * pairsWithParentBranch[1][1];
        score += pairsFromBranch[0][1] * pairsWithParentBranch[1][0];
        score += pairsFromBranch[1][0] * pairsWithParentBranch[0][1];
        score += pairsFromBranch[1][1] * pairsWithParentBranch[0][0];

        score -= pairsLR[0] * pairsLR[1];
        score -= pairsLR[0] * ( pairsWithParentBranch[0][1] + pairsWithParentBranch[1][1] );
        score -= pairsLR[1] * ( pairsWithParentBranch[0][0] + pairsWithParentBranch[1][0] );
        // score -= pairsWithParentBranch[0][0] * pairsWithParentBranch[0][1];
        // score -= pairsWithParentBranch[1][0] * pairsWithParentBranch[1][1];

        
        // score -= pairsABFromBranch[0] * pairsABFromBranch[1];
        // score -= pairsABFromBranch[0] * pairsABWithParentBranch[1];
        // score -= pairsABFromBranch[1] * pairsABWithParentBranch[0];

        // print pairs with parent brnahces
        // for(int i = 0; i < 2; ++i){
        //     System.out.println("pairs with parent branch " + i + " : " + pairsWithParentBranch[i][0] + " " + pairsWithParentBranch[i][1]);
        // }
        // System.out.println("node score : " + score);

        // // // // print common and unique branches
        // for(int i = 0; i < 2; ++i){
        //     System.out.println("branch " + i + " common total taxon count : " + common[i].totalTaxaCounts[0] + " " + common[i].totalTaxaCounts[1]);
        //     System.out.println("branch " + i + " unique total taxon count : " + uniques[i].totalTaxaCounts[0] + " " + uniques[i].totalTaxaCounts[1]);
        //     // print pairs
        //     System.out.println("pairs from branch " + i + " : " + pairsFromBranch[i][0] + " " + pairsFromBranch[i][1]);
        //     System.out.println("pairs with parent branch " + i + " : " + pairsWithParentBranch[i][0] + " " + pairsWithParentBranch[i][1]);
        // }
        // System.out.println("parents unique total taxon count : " + uniquesParent.totalTaxaCounts[0] + " " + uniquesParent.totalTaxaCounts[1]);

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
    public RTGainReturnType gainRealTaxa(double originalScore, double multiplier) {
        RTGainReturnType gains = new RTGainReturnType();
        gains.commonGains = new double[2][2];
        gains.uniqueGains = new double[2][2];
        gains.uniqueParentGains = new double[2];

        for(int i = 0; i < 2; ++i){
            for(int p = 0; p < 2; ++p){
                if(this.common[i].realTaxaCounts[p] > 0){
                    this.transferCommon(i, p);
                    this.common[i].swapRealTaxa(p);
                    gains.commonGains[i][p] = multiplier * (this.score() - originalScore);
                    this.transferCommon(i, 1-p);
                    this.common[i].swapRealTaxa(1-p);
                }

                if(this.uniques[i].realTaxaCounts[p] > 0){
                    this.transferUnique(i, p);
                    this.uniques[i].swapRealTaxa(p);
                    gains.uniqueGains[i][p] = multiplier * (this.score() - originalScore);
                    this.transferUnique(i, 1-p);
                    this.uniques[i].swapRealTaxa(1-p);
                }

            }

            if(this.uniquesParent.realTaxaCounts[i] > 0){
                this.transferParentUnique(i);
                this.uniquesParent.swapRealTaxa(i);
                gains.uniqueParentGains[i] = multiplier * (this.score() - originalScore);
                this.transferParentUnique(1 - i);
                this.uniquesParent.swapRealTaxa(1 - i);
            }
        }

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
    public void transferCommon(int branchIndex, int currPartition){
        this.pairsFromBranch[branchIndex][currPartition] -= (
            this.common[branchIndex].totalTaxaCounts[currPartition] + this.uniques[branchIndex].totalTaxaCounts[currPartition] - 1
        );
        this.pairsFromBranch[branchIndex][1 - currPartition] += (
            this.common[branchIndex].totalTaxaCounts[1 - currPartition] + this.uniques[branchIndex].totalTaxaCounts[1 - currPartition]
        );
        

        // this.pairsWithParentBranch[branchIndex][currPartition] -= (
        //     this.common[0].totalTaxaCounts[currPartition] + this.common[1].totalTaxaCounts[currPartition] + this.uniquesParent.totalTaxaCounts[currPartition] - 1
        // );
        
        // this.pairsWithParentBranch[branchIndex][1 - currPartition] += (
        //     this.common[0].totalTaxaCounts[1 - currPartition] + this.common[1].totalTaxaCounts[1 - currPartition] + this.uniquesParent.totalTaxaCounts[1 - currPartition]
        // );
        
        // this.pairsWithParentBranch[branchIndex][currPartition] -= (
        //     this.common[branchIndex].totalTaxaCounts[currPartition] + this.uniques[branchIndex].totalTaxaCounts[currPartition] - 1
        // );
        // this.pairsWithParentBranch[branchIndex][1 - currPartition] += (
        //     this.common[branchIndex].totalTaxaCounts[1 - currPartition] + this.uniques[branchIndex].totalTaxaCounts[1 - currPartition]
        // );


        // this.pairsWithParentBranch[ 1 - branchIndex][currPartition] -= (
        //     this.common[ 1 - branchIndex].totalTaxaCounts[currPartition] + this.uniques[1 - branchIndex].totalTaxaCounts[currPartition]
        // );
        // this.pairsWithParentBranch[1 - branchIndex][1 - currPartition] += (
        //     this.common[1 - branchIndex].totalTaxaCounts[1 - currPartition] + this.uniques[1 - branchIndex].totalTaxaCounts[1 - currPartition]
        // );

        this.pairsWithParentCommon[branchIndex][currPartition] -= (
            this.common[branchIndex].totalTaxaCounts[currPartition] - 1
        );
        this.pairsWithParentCommon[branchIndex][1 - currPartition] += (
            this.common[branchIndex].totalTaxaCounts[1 - currPartition]
        );

        this.pairsWithParentCommonAndUnique[branchIndex][currPartition] -= (
            this.uniques[branchIndex].totalTaxaCounts[currPartition] + this.common[1 - branchIndex].totalTaxaCounts[currPartition] + this.uniquesParent.totalTaxaCounts[currPartition]
        );
        
        this.pairsWithParentCommonAndUnique[branchIndex][1 - currPartition] += (
            this.uniques[branchIndex].totalTaxaCounts[1 - currPartition] + this.common[1 - branchIndex].totalTaxaCounts[1 - currPartition] + this.uniquesParent.totalTaxaCounts[1 - currPartition]
        );

        this.pairsWithParentUnique[1-branchIndex][currPartition] -= (
            this.uniques[1-branchIndex].totalTaxaCounts[currPartition]
        );
        this.pairsWithParentUnique[1-branchIndex][1 - currPartition] += (
            this.uniques[1-branchIndex].totalTaxaCounts[1 - currPartition]
        );
        this.pairsWithParentCommonAndUnique[1-branchIndex][currPartition] -= (
            this.common[1-branchIndex].totalTaxaCounts[currPartition]
        );
        this.pairsWithParentCommonAndUnique[1-branchIndex][1 - currPartition] += (
            this.common[1-branchIndex].totalTaxaCounts[1 - currPartition]
        );


        this.pairsLR[currPartition] -= (
            this.common[1 - branchIndex].totalTaxaCounts[currPartition] + this.uniques[1 - branchIndex].totalTaxaCounts[currPartition]
        );
        this.pairsLR[1 - currPartition] += (
            this.common[1 - branchIndex].totalTaxaCounts[1 - currPartition] + this.uniques[1 - branchIndex].totalTaxaCounts[1 - currPartition]
        );
    }

    @Override
    public void transferUnique(int branchIndex, int currPartition){
        this.pairsFromBranch[branchIndex][currPartition] -= (
            this.common[branchIndex].totalTaxaCounts[currPartition] + this.uniques[branchIndex].totalTaxaCounts[currPartition] - 1
        );
        this.pairsFromBranch[branchIndex][1 - currPartition] += (
            this.common[branchIndex].totalTaxaCounts[1 - currPartition] + this.uniques[branchIndex].totalTaxaCounts[1 - currPartition]
        );
        // this.pairsWithParentBranch[branchIndex][currPartition] -= (
        //     this.common[0].totalTaxaCounts[currPartition] + this.common[1].totalTaxaCounts[currPartition] + this.uniquesParent.totalTaxaCounts[currPartition]
        // );
        // this.pairsWithParentBranch[branchIndex][1 - currPartition] += (
        //     this.common[0].totalTaxaCounts[1 - currPartition] + this.common[1].totalTaxaCounts[1 - currPartition] + this.uniquesParent.totalTaxaCounts[1 - currPartition]
        // );

        this.pairsWithParentUnique[branchIndex][currPartition] -= (
            this.common[1-branchIndex].totalTaxaCounts[currPartition] + this.uniquesParent.totalTaxaCounts[currPartition]
        );
        this.pairsWithParentUnique[branchIndex][1 - currPartition] += (
            this.common[1-branchIndex].totalTaxaCounts[1 - currPartition] + this.uniquesParent.totalTaxaCounts[1 - currPartition]
        );

        this.pairsWithParentCommonAndUnique[branchIndex][currPartition] -= (
            this.common[branchIndex].totalTaxaCounts[currPartition]
        );
        this.pairsWithParentCommonAndUnique[branchIndex][1 - currPartition] += (
            this.common[branchIndex].totalTaxaCounts[1 - currPartition]
        );



        this.pairsLR[currPartition] -= (
            this.common[1 - branchIndex].totalTaxaCounts[currPartition] + this.uniques[1 - branchIndex].totalTaxaCounts[currPartition]
        );
        this.pairsLR[1 - currPartition] += (
            this.common[1 - branchIndex].totalTaxaCounts[1 - currPartition] + this.uniques[1 - branchIndex].totalTaxaCounts[1 - currPartition]
        );
    }

    @Override
    public void transferParentUnique(int currPartition){

        for(int branchIndex = 0; branchIndex < 2; ++branchIndex){
            this.pairsWithParentUnique[branchIndex][currPartition] -= (
                this.uniques[branchIndex].totalTaxaCounts[currPartition]
            );
            this.pairsWithParentUnique[branchIndex][1 - currPartition] += (
                this.uniques[branchIndex].totalTaxaCounts[1 - currPartition]
            );

            this.pairsWithParentCommonAndUnique[branchIndex][currPartition] -=(
                this.common[branchIndex].totalTaxaCounts[currPartition]
            );
            this.pairsWithParentCommonAndUnique[branchIndex][1 - currPartition] += (
                this.common[branchIndex].totalTaxaCounts[1 - currPartition]
            );
        }

        // this.pairsWithParentBranch[0][currPartition] -= (
        //     this.common[0].totalTaxaCounts[currPartition] + this.uniques[0].totalTaxaCounts[currPartition]
        // );
        // this.pairsWithParentBranch[0][1 - currPartition] += (
        //     this.common[0].totalTaxaCounts[1 - currPartition] + this.uniques[0].totalTaxaCounts[1 - currPartition]
        // );
        // this.pairsWithParentBranch[1][currPartition] -= (
        //     this.common[1].totalTaxaCounts[currPartition] + this.uniques[1].totalTaxaCounts[currPartition]
        // );
        // this.pairsWithParentBranch[1][1 - currPartition] += (
        //     this.common[1].totalTaxaCounts[1 - currPartition] + this.uniques[1].totalTaxaCounts[1 - currPartition]
        // );
    }


    // @Override
    // public void transferRealTaxon(int branchIndex, int currPartition) {
    //     // if(branchIndex < 2){
    //     //     this.pairsFromBranch[branchIndex][currPartition] -= this.branches[branchIndex].totalTaxaCounts[currPartition] - 1;
    //     //     this.pairsFromBranch[branchIndex][1 - currPartition] += this.branches[branchIndex].totalTaxaCounts[1 - currPartition];
    //     //     this.pairsABFromBranch[branchIndex] -= this.branches[branchIndex].totalTaxaCounts[1 - currPartition];
    //     //     this.pairsABFromBranch[branchIndex] += this.branches[branchIndex].totalTaxaCounts[currPartition] - 1;
            
    //     //     this.pairsWithParentBranch[branchIndex][currPartition] -= this.branches[2].totalTaxaCounts[currPartition];
    //     //     this.pairsWithParentBranch[branchIndex][1 - currPartition] += this.branches[2].totalTaxaCounts[1 - currPartition];
    //     // }
    //     // else{
    //     //     for(int i = 0; i < 2; ++i){
    //     //         this.pairsWithParentBranch[i][currPartition] -= this.branches[i].totalTaxaCounts[currPartition];
    //     //         this.pairsWithParentBranch[i][1 - currPartition] += this.branches[i].totalTaxaCounts[1 - currPartition];
    //     //     }
    //     // }



    //     // if(branchIndex < 2){
    //     //     this.pairsFromLeftRightBranch[currPartition] -= this.branches[1 - branchIndex].totalTaxaCounts[currPartition];
    //     //     this.pairsFromLeftRightBranch[1 - currPartition] += this.branches[1 - branchIndex].totalTaxaCounts[1 - currPartition];
    //     //     this.pairsABFromLeftRightBranch = (
    //     //         (this.branches[branchIndex].totalTaxaCounts[currPartition] - 1) * this.branches[1 - branchIndex].totalTaxaCounts[1 - currPartition] 
    //     //         + (this.branches[branchIndex].totalTaxaCounts[1 - currPartition] + 1 ) * (this.branches[1 - branchIndex].totalTaxaCounts[currPartition])
    //     //     );
            
    //     // }
    // }

    @Override
    public void transferDummyTaxon(int dummyIndex, int currPartition) {
        double w0 = this.common[0].dummyTaxaWeightsIndividual[dummyIndex] + this.uniques[0].dummyTaxaWeightsIndividual[dummyIndex];
        double w1 = this.common[1].dummyTaxaWeightsIndividual[dummyIndex] + this.uniques[1].dummyTaxaWeightsIndividual[dummyIndex];
        double wp = this.uniquesParent.dummyTaxaWeightsIndividual[dummyIndex] + this.common[0].dummyTaxaWeightsIndividual[dummyIndex] + this.common[1].dummyTaxaWeightsIndividual[dummyIndex];

        this.pairsFromBranch[0][currPartition] -= (
            this.common[0].totalTaxaCounts[currPartition] + this.uniques[0].totalTaxaCounts[currPartition] - w0
        ) * w0;
        this.pairsFromBranch[0][1 - currPartition] += (
            this.common[0].totalTaxaCounts[1 - currPartition] + this.uniques[0].totalTaxaCounts[1 - currPartition]
        ) * w0;

        this.pairsFromBranch[1][currPartition] -= (
            this.common[1].totalTaxaCounts[currPartition] + this.uniques[1].totalTaxaCounts[currPartition] - w1
        ) * w1;
        this.pairsFromBranch[1][1 - currPartition] += (
            this.common[1].totalTaxaCounts[1 - currPartition] + this.uniques[1].totalTaxaCounts[1 - currPartition]
        ) * w1;

        this.pairsLR[currPartition] -= (
            w0 * (this.common[1].totalTaxaCounts[currPartition] + this.uniques[1].totalTaxaCounts[currPartition] - w1) + 
            w1 * (this.common[0].totalTaxaCounts[currPartition] + this.uniques[0].totalTaxaCounts[currPartition] - w0)
        );
        this.pairsLR[1 - currPartition] += (
            this.common[0].totalTaxaCounts[1 - currPartition] + this.uniques[0].totalTaxaCounts[1 - currPartition]
        ) * w1 + (
            this.common[1].totalTaxaCounts[1 - currPartition] + this.uniques[1].totalTaxaCounts[1 - currPartition]
        ) * w0;

        for(int branchIndex = 0; branchIndex < 2; ++branchIndex){
            double wc = this.common[branchIndex].dummyTaxaWeightsIndividual[dummyIndex];
            this.pairsWithParentCommon[branchIndex][currPartition] -= (
                wc * (this.common[branchIndex].totalTaxaCounts[currPartition] - wc)
            );
            this.pairsWithParentCommon[branchIndex][1 - currPartition] += (
                this.common[branchIndex].totalTaxaCounts[1 - currPartition]
            ) * wc;
            double wuc = this.uniques[branchIndex].dummyTaxaWeightsIndividual[dummyIndex];
            double wpc = this.uniquesParent.dummyTaxaWeightsIndividual[dummyIndex] + this.common[1 - branchIndex].dummyTaxaWeightsIndividual[dummyIndex];
            
            this.pairsWithParentUnique[branchIndex][currPartition] -= (
                wuc * (this.common[1-branchIndex].totalTaxaCounts[currPartition] + this.uniquesParent.totalTaxaCounts[currPartition] - wpc)+
                wpc * (this.uniques[branchIndex].totalTaxaCounts[currPartition] - wuc)
            );
            this.pairsWithParentUnique[branchIndex][1 - currPartition] += (
                this.common[1-branchIndex].totalTaxaCounts[1 - currPartition] + this.uniquesParent.totalTaxaCounts[1 - currPartition]
            ) * wuc + (
                this.uniques[branchIndex].totalTaxaCounts[1 - currPartition]
            ) * wpc;


            this.pairsWithParentCommonAndUnique[branchIndex][currPartition] -= (
                wc * (this.uniques[branchIndex].totalTaxaCounts[currPartition] + this.common[1 - branchIndex].totalTaxaCounts[currPartition] + this.uniquesParent.totalTaxaCounts[currPartition] - wpc - wuc) +
                (wpc + wuc) * (this.common[branchIndex].totalTaxaCounts[currPartition] - wc)
            );
            this.pairsWithParentCommonAndUnique[branchIndex][1 - currPartition] += (
                this.uniques[branchIndex].totalTaxaCounts[1 - currPartition] + this.common[1 - branchIndex].totalTaxaCounts[1 - currPartition] + this.uniquesParent.totalTaxaCounts[1 - currPartition]
            ) * wc + (
                this.common[branchIndex].totalTaxaCounts[1 - currPartition]
            ) * (wpc + wuc);
        }

        // this.pairsWithParentBranch[0][currPartition] -= (
        //     w0 * (this.common[0].totalTaxaCounts[currPartition] + this.common[0].totalTaxaCounts[currPartition] + this.uniquesParent.totalTaxaCounts[currPartition] - wp) + 
        //     wp * (this.common[0].totalTaxaCounts[currPartition] + this.uniques[0].totalTaxaCounts[currPartition] - w0)
        // );
        // this.pairsWithParentBranch[0][1 - currPartition] += (
        //     this.common[0].totalTaxaCounts[1 - currPartition] + this.uniques[0].totalTaxaCounts[1 - currPartition] + this.uniquesParent.totalTaxaCounts[1 - currPartition]
        // ) * w0 + (
        //     this.common[0].totalTaxaCounts[1 - currPartition] + this.uniquesParent.totalTaxaCounts[1 - currPartition]
        // ) * wp;

        // this.pairsWithParentBranch[1][currPartition] -= (
        //     w1 * (this.common[1].totalTaxaCounts[currPartition] + this.common[1].totalTaxaCounts[currPartition] + this.uniquesParent.totalTaxaCounts[currPartition] - wp) + 
        //     wp * (this.common[1].totalTaxaCounts[currPartition] + this.uniques[1].totalTaxaCounts[currPartition] - w1)
        // );
        // this.pairsWithParentBranch[1][1 - currPartition] += (
        //     this.common[1].totalTaxaCounts[1 - currPartition] + this.uniques[1].totalTaxaCounts[1 - currPartition] + this.uniquesParent.totalTaxaCounts[1 - currPartition]
        // ) * w1 + (
        //     this.common[1].totalTaxaCounts[1 - currPartition] + this.uniquesParent.totalTaxaCounts[1 - currPartition]
        // ) * wp;


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
        for(int i = 0; i < this.nDummyTaxa; ++i){
            int currPartition = this.dummyTaxaPartition[i];
            this.transferDummyTaxon(i, currPartition);
            for(int j = 0; j < 2; ++j){
                this.common[j].swapDummyTaxon(i, currPartition);
                this.uniques[j].swapDummyTaxon(i, currPartition);
            }
            this.uniquesParent.swapDummyTaxon(i, currPartition);
            dummyTaxaGains[i] += multiplier * (this.score() - originalScore);
            
            this.transferDummyTaxon(i, 1 - currPartition);
            for(int j = 0; j < 2; ++j){
                this.common[j].swapDummyTaxon(i, 1 - currPartition);
                this.uniques[j].swapDummyTaxon(i, 1 - currPartition);
            }
            this.uniquesParent.swapDummyTaxon(i, 1 - currPartition);
            
            
        }
    }

    @Override
    public void batchTransferRealTaxon(int branchIndex, int netTranser) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'batchTransferRealTaxon'");
    }

    
    
    @Override
    public RTGainReturnType gainSatRealTaxa(int fre){
        RTGainReturnType gains = new RTGainReturnType();
        gains.commonGains = new double[2][2];
        gains.uniqueGains = new double[2][2];
        gains.uniqueParentGains = new double[2];

        for(int i = 0; i < 2; ++i){
            for(int p = 0; p < 2; ++p){
                if(this.common[i].realTaxaCounts[p] > 0){
                    this.transferCommon(i, p);
                    this.common[i].swapRealTaxa(p);
                    gains.commonGains[i][p] = this.sat() * fre;
                    this.transferCommon(i, 1-p);
                    this.common[i].swapRealTaxa(1-p);
                }

                if(this.uniques[i].realTaxaCounts[p] > 0){
                    this.transferUnique(i, p);
                    this.uniques[i].swapRealTaxa(p);
                    gains.uniqueGains[i][p] = this.sat() * fre;
                    this.transferUnique(i, 1-p);
                    this.uniques[i].swapRealTaxa(1-p);
                }

            }

            if(this.uniquesParent.realTaxaCounts[i] > 0){
                this.transferParentUnique(i);
                this.uniquesParent.swapRealTaxa(i);
                gains.uniqueParentGains[i] = this.sat() * fre;
                this.transferParentUnique(1 - i);
                this.uniquesParent.swapRealTaxa(1 - i);
            }
        }

        return gains;
    }
    @Override
    public RTGainReturnType gainVioRealTaxa(int fre){
        RTGainReturnType gains = new RTGainReturnType();
        gains.commonGains = new double[2][2];
        gains.uniqueGains = new double[2][2];
        gains.uniqueParentGains = new double[2];

        for(int i = 0; i < 2; ++i){
            for(int p = 0; p < 2; ++p){
                if(this.common[i].realTaxaCounts[p] > 0){
                    this.transferCommon(i, p);
                    this.common[i].swapRealTaxa(p);
                    gains.commonGains[i][p] = this.vio() * fre;
                    this.transferCommon(i, 1-p);
                    this.common[i].swapRealTaxa(1-p);
                }

                if(this.uniques[i].realTaxaCounts[p] > 0){
                    this.transferUnique(i, p);
                    this.uniques[i].swapRealTaxa(p);
                    gains.uniqueGains[i][p] = this.vio() * fre;
                    this.transferUnique(i, 1-p);
                    this.uniques[i].swapRealTaxa(1-p);
                }

            }

            if(this.uniquesParent.realTaxaCounts[i] > 0){
                this.transferParentUnique(i);
                this.uniquesParent.swapRealTaxa(i);
                gains.uniqueParentGains[i] = this.vio() * fre;
                this.transferParentUnique(1 - i);
                this.uniquesParent.swapRealTaxa(1 - i);
            }
        }

        return gains;
    }


    @Override
    public void gainSatDummyTaxa(double[] dummyTaxaGains, int fre) {
        for(int i = 0; i < this.nDummyTaxa; ++i){
            int currPartition = this.dummyTaxaPartition[i];
            this.transferDummyTaxon(i, currPartition);
            for(int j = 0; j < 2; ++j){
                this.common[j].swapDummyTaxon(i, currPartition);
                this.uniques[j].swapDummyTaxon(i, currPartition);
            }
            this.uniquesParent.swapDummyTaxon(i, currPartition);
            dummyTaxaGains[i] += this.sat() * fre;
            
            this.transferDummyTaxon(i, 1 - currPartition);
            for(int j = 0; j < 2; ++j){
                this.common[j].swapDummyTaxon(i, 1 - currPartition);
                this.uniques[j].swapDummyTaxon(i, 1 - currPartition);
            }
            this.uniquesParent.swapDummyTaxon(i, 1 - currPartition);
            
            
        }
    }
    @Override
    public void gainVioDummyTaxa(double[] dummyTaxaGains, int fre) {
        for(int i = 0; i < this.nDummyTaxa; ++i){
            int currPartition = this.dummyTaxaPartition[i];
            this.transferDummyTaxon(i, currPartition);
            for(int j = 0; j < 2; ++j){
                this.common[j].swapDummyTaxon(i, currPartition);
                this.uniques[j].swapDummyTaxon(i, currPartition);
            }
            this.uniquesParent.swapDummyTaxon(i, currPartition);
            dummyTaxaGains[i] += this.vio() * fre;
            
            this.transferDummyTaxon(i, 1 - currPartition);
            for(int j = 0; j < 2; ++j){
                this.common[j].swapDummyTaxon(i, 1 - currPartition);
                this.uniques[j].swapDummyTaxon(i, 1 - currPartition);
            }
            this.uniquesParent.swapDummyTaxon(i, 1 - currPartition);
            
            
        }
    }
}
