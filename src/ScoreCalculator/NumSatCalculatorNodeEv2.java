package src.ScoreCalculator;

import src.Config;
import src.Tree.Branch;

public class NumSatCalculatorNodeEv2 implements NumSatCalculatorNode {
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

    NonQuartCalculator nonQuartCalculator;


    public interface NonQuartCalculator{
        double calcNonQuartets();
        double changeAmount(int branchIndex);
    }

    class NonQuartCalculatorB implements NonQuartCalculator{
        @Override
        public double calcNonQuartets(){
            double q = 0;
            for(int i = 0; i < branches.length; ++i){
                for(int j = i + 1; j < branches.length; ++j){
                    q += pairs[i][j][0] * (sumPairs[1] - pairs[i][j][1]);
                }
            }
            return q;
        }

        @Override
        public double changeAmount(int branchIndex){
            double q = 0;
            for(int i = 0; i < branches.length; ++i){
                if(i == branchIndex) continue;
                int mni = branchIndex > i ? i : branchIndex;
                int mxi = branchIndex > i ? branchIndex : i;
                q += pairs[mni][mxi][0] * (sumPairs[1] - sumPairsBranch[branchIndex][1]);
                q += pairs[mni][mxi][1] * (sumPairs[0] - pairs[mni][mxi][0]);
            }
            return q;
        }
    }

    class NonQuartCalculatorA implements NonQuartCalculator{
        @Override
        public double calcNonQuartets(){
            double q = 0;
            for(int i = 0; i < branches.length; ++i){
                for(int j = i + 1; j < branches.length; ++j){
                    q += pairs[i][j][0] * (sumPairs[1] - sumPairsBranch[i][1] - sumPairsBranch[j][1] + pairs[i][j][1]);
                }
            }
            return q;
        }

        @Override
        public double changeAmount(int branchIndex){
            double q = 0;
            for(int i = 0; i < branches.length; ++i){
                if(i == branchIndex) continue;
                int mni = branchIndex > i ? i : branchIndex;
                int mxi = branchIndex > i ? branchIndex : i;
                q += pairs[mni][mxi][0] * (sumPairs[1] - sumPairsBranch[mni][1] - sumPairsBranch[mxi][1] + pairs[mni][mxi][1]);
                q += pairs[mni][mxi][1] * (sumPairs[0] - sumPairsBranch[mni][0] - sumPairsBranch[mxi][0] + pairs[mni][mxi][0]);
                
            }
            return q;
        }

    }


    int[] dummyTaxaPartition;

    // double calcNonQuartets(){
    //     double q = 0;
    //     for(int i = 0; i < this.branches.length; ++i){
    //         for(int j = i + 1; j < this.branches.length; ++j){
    //             q += this.pairs[i][j][0] * (this.sumPairs[1] - this.pairs[i][j][1]);
    //             // q += this.pairs[i][j][0] * (this.sumPairs[1] - this.sumPairsBranch[i][1] - this.sumPairsBranch[j][1] + this.pairs[i][j][1]);

    //             // q += this.pairs[i][j][1] * (this.sumPairs[0] - this.sumPairsBranch[i][0] - this.sumPairsBranch[j][0] + this.pairs[i][j][0]);
    //         }
    //     }

    //     return q;
    // }

    public NumSatCalculatorNodeEv2(Branch[] b) {

        this.branches = b;
        this.totalTaxa = new double[2];

        this.nDummyTaxa = b[0].dummyTaxaWeightsIndividual.length;

        pairsBFromSingleBranch = new double[b.length];
        sumPairsBranch = new double[b.length][2];
        this.sumPairs = new double[2];
        this.sumPairsBSingleBranch = 0;
        this.pairs = new double[b.length][b.length][2];
        this.nonQuartets = 0;


        if(Config.NON_QUARTET_TYPE == Config.NonQuartetType.A){
            this.nonQuartCalculator = new NonQuartCalculatorA();
        }
        else{
            this.nonQuartCalculator = new NonQuartCalculatorB();
        }
        this.nonQuartets = this.nonQuartCalculator.calcNonQuartets();

    }

    public void initBookkeeping(
        int[] dummyTaxaToPartitionMap,
        double totalTaxaA, 
        double totalTaxaB, 
        double[] dummyTaxaWeightsIndividual,
        int nDummyTaxa
    ){
        this.dummyTaxaPartition = dummyTaxaToPartitionMap;
        this.dummyTaxaWeightsIndividual = dummyTaxaWeightsIndividual;
        this.totalTaxa[0] = totalTaxaA;
        this.totalTaxa[1] = totalTaxaB;
        
        this.nDummyTaxa = nDummyTaxa;
        for(int i = 0; i < this.branches.length; ++i){
            this.pairsBFromSingleBranch[i] = 0;
            this.sumPairsBranch[i][0] = 0;
            this.sumPairsBranch[i][1] = 0;
        }
        this.sumPairs[0] = 0;
        this.sumPairs[1] = 0;
        this.sumPairsBSingleBranch = 0;

        var b = this.branches;
        
        for(int i = 0; i < b.length; ++i){
            for(int j = i + 1; j < b.length; ++j){
                this.pairs[i][j][0] = b[i].totalTaxaCounts[0] * b[j].totalTaxaCounts[0];
                this.pairs[i][j][1] = b[i].totalTaxaCounts[1] * b[j].totalTaxaCounts[1];
                for(int k = 0; k < this.nDummyTaxa; ++k){
                    int partition = this.dummyTaxaPartition[k];
                    this.pairs[i][j][partition] -= b[i].dummyTaxaWeightsIndividual[k] * b[j].dummyTaxaWeightsIndividual[k];
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

        if(Config.NON_QUARTET_TYPE == Config.NonQuartetType.A){
            this.nonQuartCalculator = new NonQuartCalculatorA();
        }
        else{
            this.nonQuartCalculator = new NonQuartCalculatorB();
        }
        this.nonQuartets = this.nonQuartCalculator.calcNonQuartets();
    }

    @Override
    public double score(){
        double res = 0;
        for(int i = 0; i < this.branches.length; ++i){
            res -=  pairsBFromSingleBranch[i] * sumPairsBranch[i][0];
        }
        res += this.sumPairs[0] * this.sumPairsBSingleBranch;

        // double res2 = 0;
        // for(int i = 0; i < this.branches.length; ++i){
        //     for(int j = i + 1; j < this.branches.length; ++j ){
        //         res2 += (this.pairs[i][j][0] * (this.sumPairsBSingleBranch - this.pairsBFromSingleBranch[i] - this.pairsBFromSingleBranch[j]));
        //     }
        // }

        // if(Math.abs(res - res2) > .00001){
        //     System.out.println("not equal. diff : " + (res - res2));
        // }
        // else{
        //     // System.out.println("equal");
        // }

        res += (this.nonQuartets / 2);
        return res;
    }

    @Override
    public void swapRealTaxon(int branchIndex, int currPartition){
        
        this.nonQuartets -= this.nonQuartCalculator.changeAmount(branchIndex);
        // for(int i = 0; i < this.branches.length; ++i){
        //     if(i == branchIndex) continue;
        //     int mni = branchIndex > i ? i : branchIndex;
        //     int mxi = branchIndex > i ? branchIndex : i;
            
        //     // this.nonQuartets -= this.pairs[mni][mxi][0] * (this.sumPairs[1] - this.sumPairsBranch[mni][1] - this.sumPairsBranch[mxi][1] + this.pairs[mni][mxi][1]);
        //     // this.nonQuartets -= this.pairs[mni][mxi][1] * (this.sumPairs[0] - this.sumPairsBranch[mni][0] - this.sumPairsBranch[mxi][0] + this.pairs[mni][mxi][0]);

        //     // this.nonQuartets -= this.pairs[mni][mxi][0] * (this.sumPairs[1] - this.sumPairsBranch[branchIndex][1] + this.pairs[mni][mxi][1]);
        //     this.nonQuartets -= this.pairs[mni][mxi][0] * (this.sumPairs[1] - this.sumPairsBranch[branchIndex][1]);

        //     // this.nonQuartets -= this.pairs[mni][mxi][0] * (this.sumPairs[1] - this.pairs[mni][mxi][1]);
        //     this.nonQuartets -= this.pairs[mni][mxi][1] * (this.sumPairs[0] - this.pairs[mni][mxi][0]);


        // }
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

        // this.nonQuartets = this.calcNonQuartets();
        this.nonQuartets += this.nonQuartCalculator.changeAmount(branchIndex);
        // for(int i = 0; i < this.branches.length; ++i){
        //     if(i == branchIndex) continue;
        //     int mni = branchIndex > i ? i : branchIndex;
        //     int mxi = branchIndex > i ? branchIndex : i;
        //     // this.nonQuartets += this.pairs[mni][mxi][0] * (this.sumPairs[1] - this.sumPairsBranch[mni][1] - this.sumPairsBranch[mxi][1] + this.pairs[mni][mxi][1]);
        //     // this.nonQuartets += this.pairs[mni][mxi][1] * (this.sumPairs[0] - this.sumPairsBranch[mni][0] - this.sumPairsBranch[mxi][0] + this.pairs[mni][mxi][0]);

        //     // this.nonQuartets += this.pairs[mni][mxi][0] * (this.sumPairs[1] - this.sumPairsBranch[branchIndex][1] + this.pairs[mni][mxi][1]);
        //     this.nonQuartets += this.pairs[mni][mxi][0] * (this.sumPairs[1] - this.sumPairsBranch[branchIndex][1]);
            
        //     // this.nonQuartets -= this.pairs[mni][mxi][0] * (this.sumPairs[1] - this.pairs[mni][mxi][1]);
        //     this.nonQuartets += this.pairs[mni][mxi][1] * (this.sumPairs[0] - this.pairs[mni][mxi][0]);            
            
        //     // this.nonQuartets += this.pairs[mni][mxi][0] * (this.sumPairs[1] - this.pairs[mni][mxi][1]);
        //     // this.nonQuartets += this.pairs[mni][mxi][1] * (this.sumPairs[0] - this.pairs[mni][mxi][0]);        
        // }
        // double diff = Math.abs(this.calcNonQuartets() - this.nonQuartets);
        // if(diff > .00001){
        //     System.out.println("not equal.diff : " + diff);
        // }
        // else{
        //     // System.out.println("equal");
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


        for(int i = 0; i < this.branches.length; ++i){
            branches[i].swapDummyTaxon(dummyIndex, currPartition);
        }

        this.nonQuartets = this.nonQuartCalculator.calcNonQuartets();
        // this.nonQuartets = this.calcNonQuartets();
        
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
