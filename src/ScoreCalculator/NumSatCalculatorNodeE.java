package src.ScoreCalculator;

import src.Tree.Branch;

public class NumSatCalculatorNodeE implements NumSatCalculatorNode {
    Branch[] branches;
    double[] pairsBFromSingleBranch;
    double[][] sumPairsBranch;
    double[] dummyTaxaWeightsIndividual;
    // double totalTaxaA;
    // double totalTaxaB;
    double[] totalTaxa;
    double[][][] pairs;
    double[] sumPairs;

    double nonQuartets;
    // double sumPairsB;
    double sumPairsBSingleBranch;
    

    int nDummyTaxa;



    int[] dummyTaxaPartition;

    public NumSatCalculatorNodeE(Branch[] b, int[] dummyTaxaToPartitionMap,
    double totalTaxaA, double totalTaxaB, double[] dummyTaxaWeightsIndividual) {

        this.dummyTaxaPartition = dummyTaxaToPartitionMap;
        this.branches = b;
        // this.totalTaxaA = totalTaxaA;
        // this.totalTaxaB = totalTaxaB;
        this.dummyTaxaWeightsIndividual = dummyTaxaWeightsIndividual;
        this.totalTaxa = new double[]{totalTaxaA, totalTaxaB};

        this.nDummyTaxa = b[0].dummyTaxaWeightsIndividual.length;

        // double[][] pairsA = new double[b.length][b.length];
        pairsBFromSingleBranch = new double[b.length];
        sumPairsBranch = new double[b.length][2];
        this.sumPairs = new double[2];
        // this.sumPairsA = 0;
        // this.sumPairsB = 0;
        this.sumPairsBSingleBranch = 0;
        this.pairs = new double[b.length][b.length][2];
        this.nonQuartets = 0;

        for(int i = 0; i < b.length; ++i){
            for(int j = i + 1; j < b.length; ++j){
                // double curr = b[i].totalTaxaCounts[0] * b[j].totalTaxaCounts[0];
                this.pairs[i][j][0] = b[i].totalTaxaCounts[0] * b[j].totalTaxaCounts[0];
                this.pairs[i][j][1] = b[i].totalTaxaCounts[1] * b[j].totalTaxaCounts[1];

                for(int k = 0; k < this.nDummyTaxa; ++k){
                    int partition = this.dummyTaxaPartition[k];
                    this.pairs[i][j][partition] -= b[i].dummyTaxaWeightsIndividual[k] * b[j].dummyTaxaWeightsIndividual[k];
                    // if(partition == 0){
                    //     curr -= b[i].dummyTaxaWeightsIndividual[k] * b[j].dummyTaxaWeightsIndividual[k];
                    // }
                }
                sumPairsBranch[i][0] += this.pairs[i][j][0];
                sumPairsBranch[j][0] += this.pairs[i][j][0];
                sumPairsBranch[i][1] += this.pairs[i][j][1];
                sumPairsBranch[j][1] += this.pairs[i][j][1];

                this.sumPairs[0] += this.pairs[i][j][0];
                this.sumPairs[1] += this.pairs[i][j][1];
                
            }

            pairsBFromSingleBranch[i] = b[i].totalTaxaCounts[1] * b[i].totalTaxaCounts[1];
            for(int k = 0; k < this.nDummyTaxa; ++k){
                int partition = this.dummyTaxaPartition[k];
                if(partition == 1){
                    pairsBFromSingleBranch[i] -= b[i].dummyTaxaWeightsIndividual[k] * b[i].dummyTaxaWeightsIndividual[k];
                }
            }
            pairsBFromSingleBranch[i] -= b[i].realTaxaCounts[1];
            pairsBFromSingleBranch[i] /= 2;
            this.sumPairsBSingleBranch += pairsBFromSingleBranch[i];
        }


        for(int i = 0; i < b.length; ++i){
            for(int j = i + 1; j < b.length; ++j){
                this.nonQuartets += this.pairs[i][j][0] * (this.sumPairs[1] - this.sumPairsBranch[i][1] - this.sumPairsBranch[j][1] + this.pairs[i][j][1]);
                // this.nonQuartets += this.pairs[i][j][1] * (this.sumPairs[0] - this.sumPairsBranch[i][0] - this.sumPairsBranch[j][0] + this.pairs[i][j][0]);
            }
        }
        // if(this.branches.length > 3){

        //     System.out.println("nonQuartets: " + this.nonQuartets);
        // }
    }

    @Override
    public double score(){
        double res = 0;
        for(int i = 0; i < this.branches.length; ++i){
            res -=  pairsBFromSingleBranch[i] * sumPairsBranch[i][0];
        }
        res += this.sumPairs[0] * this.sumPairsBSingleBranch;
        res += (this.nonQuartets / 2);
        return res;
    }

    @Override
    public void swapRealTaxon(int branchIndex, int currPartition){
        
        for(int i = 0; i < this.branches.length; ++i){
            if(i == branchIndex) continue;
            int mni = branchIndex > i ? i : branchIndex;
            int mxi = branchIndex > i ? branchIndex : i;
            
            this.nonQuartets -= this.pairs[mni][mxi][0] * (this.sumPairs[1] - this.sumPairsBranch[mni][1] - this.sumPairsBranch[mxi][1] + this.pairs[mni][mxi][1]);
            this.nonQuartets -= this.pairs[mni][mxi][1] * (this.sumPairs[0] - this.sumPairsBranch[mni][0] - this.sumPairsBranch[mxi][0] + this.pairs[mni][mxi][0]);

        }
        for(int i = 0; i < this.branches.length; ++i){
            if(branchIndex == i){
                this.sumPairsBranch[i][1 - currPartition] += (this.totalTaxa[1-currPartition] - this.branches[i].totalTaxaCounts[1-currPartition]);
                this.sumPairsBranch[i][currPartition] -= (this.totalTaxa[currPartition] - this.branches[i].totalTaxaCounts[currPartition]);

                this.sumPairs[1 - currPartition] += (this.totalTaxa[1-currPartition] - this.branches[i].totalTaxaCounts[1-currPartition]);
                this.sumPairs[currPartition] -= (this.totalTaxa[currPartition] - this.branches[i].totalTaxaCounts[currPartition]);

            }
            else{
                int mni = branchIndex > i ? i : branchIndex;
                int mxi = branchIndex > i ? branchIndex : i;


                this.pairs[mni][mxi][currPartition] -= this.branches[i].totalTaxaCounts[currPartition];
                this.pairs[mni][mxi][1 - currPartition] += this.branches[i].totalTaxaCounts[1 - currPartition];


                this.sumPairsBranch[i][currPartition] -= this.branches[i].totalTaxaCounts[currPartition];
                this.sumPairsBranch[i][1 - currPartition] += this.branches[i].totalTaxaCounts[1 - currPartition];

                // this.nonQuartets += this.pairs[mni][mxi][0] * (this.sumPairs[1] - this.sumPairsBranch[mni][1] - this.sumPairsBranch[mxi][1] + this.pairs[mni][mxi][1]);

            }
            

        }

        if(currPartition == 1){
            pairsBFromSingleBranch[branchIndex] -= this.branches[branchIndex].totalTaxaCounts[1] - 1;
            this.sumPairsBSingleBranch -= this.branches[branchIndex].totalTaxaCounts[1] - 1;                
        }
        else{
            pairsBFromSingleBranch[branchIndex] += this.branches[branchIndex].totalTaxaCounts[1];
            this.sumPairsBSingleBranch += this.branches[branchIndex].totalTaxaCounts[1];                
        }
        this.totalTaxa[currPartition] -= 1;
        this.totalTaxa[1 - currPartition] += 1;


        for(int i = 0; i < this.branches.length; ++i){
            if(i == branchIndex) continue;
            int mni = branchIndex > i ? i : branchIndex;
            int mxi = branchIndex > i ? branchIndex : i;
            this.nonQuartets += this.pairs[mni][mxi][0] * (this.sumPairs[1] - this.sumPairsBranch[mni][1] - this.sumPairsBranch[mxi][1] + this.pairs[mni][mxi][1]);
            this.nonQuartets += this.pairs[mni][mxi][1] * (this.sumPairs[0] - this.sumPairsBranch[mni][0] - this.sumPairsBranch[mxi][0] + this.pairs[mni][mxi][0]);
        }


        // if(this.branches.length > 3){
        //     // System.out.println("branch Index: " + branchIndex + " currPartition: " + currPartition);
        //     // System.out.println("nonQuartets: " + this.nonQuartets);
        //     double t = 0;
        //     for(int i = 0; i < this.branches.length; ++i){
        //         for(int j = i + 1; j < this.branches.length; ++j){
        //             t += this.pairs[i][j][0] * (this.sumPairs[1] - this.sumPairsBranch[i][1] - this.sumPairsBranch[j][1] + this.pairs[i][j][1]);
        //         }
        //     }
        //     if(t != this.nonQuartets){
        //         System.out.println("not equal");
        //     }
        //     else{
        //         System.out.println("equal");
        //     }
        //     // this.nonQuartets = t;
        // }

        // if(currPartition == 1){
        //     for(int i = 0; i < this.branches.length; ++i){
        //         if(branchIndex == i){
        //             this.sumPairsBranch[i][0] += (this.totalTaxa[0] - this.branches[i].totalTaxaCounts[0]);
        //         }
        //         else{

        //             int mni = branchIndex > i ? i : branchIndex;
        //             int mxi = branchIndex > i ? branchIndex : i;


        //             this.pairs[mni][mxi][0] += this.branches[i].totalTaxaCounts[0];
        //             this.pairs[mni][mxi][1] -= this.branches[i].totalTaxaCounts[1];
                    
                    
        //             this.sumPairs[1] -= this.branches[i].totalTaxaCounts[1];

        //             this.sumPairsBranch[i][0] += this.branches[i].totalTaxaCounts[0];
        //             this.sumPairs[0] += this.branches[i].totalTaxaCounts[0];
        //         }
        //     }
        //     pairsBFromSingleBranch[branchIndex] -= this.branches[branchIndex].totalTaxaCounts[1] - 1;
        //     this.sumPairsBSingleBranch -= this.branches[branchIndex].totalTaxaCounts[1] - 1;
        //     this.totalTaxa[0] += 1;
        //     this.totalTaxa[1] -= 1;
        // }
        // else{
        //     for(int i = 0; i < this.branches.length; ++i){
        //         if(branchIndex == i){
        //             this.sumPairsBranch[i][0] -= (this.totalTaxa[0] - this.branches[i].totalTaxaCounts[0]);
        //             // continue;
        //         }
        //         else{
        //             int mni = branchIndex > i ? i : branchIndex;
        //             int mxi = branchIndex > i ? branchIndex : i;


        //             this.pairs[mni][mxi][0] -= this.branches[i].totalTaxaCounts[0];
        //             this.pairs[mni][mxi][1] += this.branches[i].totalTaxaCounts[1];

                    
        //             this.sumPairs[1] += this.branches[i].totalTaxaCounts[1];

        //             this.sumPairsBranch[i][0] -= this.branches[i].totalTaxaCounts[0];
        //             this.sumPairs[0] -= this.branches[i].totalTaxaCounts[0];
        //         }
        //     }
        //     pairsBFromSingleBranch[branchIndex] += this.branches[branchIndex].totalTaxaCounts[1];
        //     this.sumPairsBSingleBranch += this.branches[branchIndex].totalTaxaCounts[1];
        //     this.totalTaxa[0] -= 1;
        //     this.totalTaxa[1] += 1;
        // }

        branches[branchIndex].swapRealTaxa(currPartition);

    }
    @Override
    public void swapDummyTaxon(int dummyIndex, int currPartition){
        

        for(int i = 0; i < this.branches.length; ++i){
            double wi = this.branches[i].dummyTaxaWeightsIndividual[dummyIndex];

            for(int j = i + 1; j < this.branches.length; ++j){
                
                double wj = this.branches[j].dummyTaxaWeightsIndividual[dummyIndex];
                double inc = wi * this.branches[j].totalTaxaCounts[1 - currPartition] + wj * this.branches[i].totalTaxaCounts[1-currPartition];
                this.pairs[i][j][1 - currPartition] += inc;
                
                double dec = wi * (this.branches[j].totalTaxaCounts[currPartition] - wj) + wj * (this.branches[i].totalTaxaCounts[currPartition] - wi);
                this.pairs[i][j][currPartition] -= dec;
                
                this.sumPairsBranch[i][1 - currPartition] += inc;
                this.sumPairsBranch[j][1 - currPartition] += inc;
                this.sumPairsBranch[i][currPartition] -= dec;
                this.sumPairsBranch[j][currPartition] -= dec;

                this.sumPairs[1 - currPartition] += inc;
                this.sumPairs[currPartition] -= dec;
            }

            if(currPartition == 1){
                this.pairsBFromSingleBranch[i] -= (this.branches[i].totalTaxaCounts[1] - wi) * wi;
                this.sumPairsBSingleBranch -= (this.branches[i].totalTaxaCounts[1] - wi) * wi; 

            }
            else{

                this.pairsBFromSingleBranch[i] += (this.branches[i].totalTaxaCounts[1]) * wi;
                this.sumPairsBSingleBranch += (this.branches[i].totalTaxaCounts[1]) * wi;                     
            }
        }

        this.totalTaxa[1 - currPartition] += this.dummyTaxaWeightsIndividual[dummyIndex];
        this.totalTaxa[currPartition] -= this.dummyTaxaWeightsIndividual[dummyIndex];




        
        // if(currPartition == 1){
        //     double incrA = 0;
        //     for(int i = 0; i < this.branches.length; ++i){
        //         double wi = this.branches[i].dummyTaxaWeightsIndividual[dummyIndex];
        //         // double wAllOther = this.dummyTaxaWeightsIndividual[dummyIndex] - wi;
        //         // double totAllOther = this.totalTaxaA - this.branches[i].totalTaxaCounts[0];
        //         // double inc = (wi * totAllOther) + (wAllOther * this.branches[i].totalTaxaCounts[0]);
        //         // this.sumOfPairsA[i] += inc;
        //         // incrA += wi * totAllOther;
        //         this.pairsBFromSingleBranch[i] -= (this.branches[i].totalTaxaCounts[1] - wi) * wi;
        //         this.sumPairsBSingleBranch -= (this.branches[i].totalTaxaCounts[1] - wi) * wi; 

        //         for(int j = i + 1; j < this.branches.length; ++j){
        //             // double wi = this.branches[i].dummyTaxaWeightsIndividual[dummyIndex];
        //             double wj = this.branches[j].dummyTaxaWeightsIndividual[dummyIndex];
        //             double inc = wi * this.branches[j].totalTaxaCounts[0] + wj * this.branches[i].totalTaxaCounts[0];
        //             // pairs[i][j][0] +=  ;
        //             // pairs[i][j][0] += ;

        //             pairs[i][j][1] -= wi * (this.branches[j].totalTaxaCounts[1] - wj);
        //             pairs[i][j][1] -= wj * (this.branches[i].totalTaxaCounts[1] - wi);
        //         }

        //     }
        //     this.sumPairsA += incrA;
        //     this.totalTaxaA += dummyTaxaWeightsIndividual[dummyIndex];
        //     this.totalTaxaB -= dummyTaxaWeightsIndividual[dummyIndex];
        // }
        // else{
        //     // System.out.println("kjsndfjkhk");
        //     double decrA = 0;
        //     for(int i = 0; i < this.branches.length; ++i){
        //         double wCurr = this.branches[i].dummyTaxaWeightsIndividual[dummyIndex];
        //         double wAllOther = this.dummyTaxaWeightsIndividual[dummyIndex] - wCurr;
        //         double totAllOther = this.totalTaxaA - this.branches[i].totalTaxaCounts[0] - wAllOther;
        //         double dec = wCurr * totAllOther + wAllOther * (this.branches[i].totalTaxaCounts[0] - wCurr );
        //         this.sumOfPairsA[i] -= dec;
        //         decrA += wCurr * totAllOther;
        //         this.pairsBFromSingleBranch[i] += (this.branches[i].totalTaxaCounts[1]) * wCurr;
        //         this.sumPairsBSingleBranch += (this.branches[i].totalTaxaCounts[1]) * wCurr; 

        //         for(int j = i + 1; j < this.branches.length; ++j){
        //             pairs[i][j][0] += this.branches[i].dummyTaxaWeightsIndividual[dummyIndex] * this.branches[j].totalTaxaCounts[0];
        //             pairs[i][j][0] += this.branches[j].dummyTaxaWeightsIndividual[dummyIndex] * this.branches[i].totalTaxaCounts[0];
        //         }

        //     }
        //     this.sumPairsA -= decrA ;
        //     this.totalTaxaA -= dummyTaxaWeightsIndividual[dummyIndex];
        //     this.totalTaxaB += dummyTaxaWeightsIndividual[dummyIndex];
        // }

        for(int i = 0; i < this.branches.length; ++i){
            branches[i].swapDummyTaxon(dummyIndex, currPartition);
        }

        this.nonQuartets = 0;
        for(int i = 0; i < this.branches.length; ++i){
            for(int j = i + 1; j < this.branches.length; ++j){
                this.nonQuartets += this.pairs[i][j][0] * (this.sumPairs[1] - this.sumPairsBranch[i][1] - this.sumPairsBranch[j][1] + this.pairs[i][j][1]);
            }
        }
        
    }


    @Override
    public double[][] gainRealTaxa(double originalScore, double multiplier) {
        double[][] gainsOfBranches = new double[this.branches.length][2];
        for(int i = 0; i < branches.length; ++i){
            for(int p = 0; p < 2; ++p){
                if(this.branches[i].realTaxaCounts[p] > 0){
                    this.swapRealTaxon(i, p);
                    gainsOfBranches[i][p] = multiplier * (this.score() - originalScore);
                    this.swapRealTaxon(i, 1 - p);
                }
            }
        }
        return gainsOfBranches;
    }


    @Override
    public void gainDummyTaxa(double originalScore, double multiplier, double[] dummyTaxaGains) {
        for(int i = 0; i < this.nDummyTaxa; ++i){
            int currPartition = this.dummyTaxaPartition[i];
            this.swapDummyTaxon(i, currPartition);
            dummyTaxaGains[i] += multiplier * (this.score() - originalScore);
            this.swapDummyTaxon(i, 1 - currPartition);
        }
    }

    


    
}
