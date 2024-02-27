package src.ScoreCalculator;

import src.Config;
import src.Tree.Branch;

public class NumSatCalculatorNodeEDC implements NumSatCalculatorNode {
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



    public double calcNonQuartets(){
        double q = 0;
        for(int i = 0; i < branches.length; ++i){
            for(int j = i + 1; j < branches.length; ++j){
                q += pairs[i][j][0] * (sumPairs[1] - pairs[i][j][1]);
            }
        }
        return q;
    }

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

    int[] dummyTaxaPartition;

    public NumSatCalculatorNodeEDC(Branch[] b, int[] dummyTaxaToPartitionMap) {

        this.dummyTaxaPartition = dummyTaxaToPartitionMap;
        this.branches = b;
        this.dummyTaxaWeightsIndividual = new double[b[0].dummyTaxaWeightsIndividual.length];
        this.totalTaxa = new double[2];

        this.nDummyTaxa = b[0].dummyTaxaWeightsIndividual.length;

        pairsBFromSingleBranch = new double[b.length];
        sumPairsBranch = new double[b.length][2];
        this.sumPairs = new double[2];
        this.sumPairsBSingleBranch = 0;
        this.pairs = new double[b.length][b.length][2];
        this.nonQuartets = 0;

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
                this.dummyTaxaWeightsIndividual[k] += b[i].dummyTaxaWeightsIndividual[k];
                if(partition == 1){
                    pairsBFromSingleBranch[i] -= b[i].dummyTaxaWeightsIndividual[k] * b[i].dummyTaxaWeightsIndividual[k];
                }
            }
            pairsBFromSingleBranch[i] -= b[i].realTaxaCounts[1];
            pairsBFromSingleBranch[i] /= 2;
            this.sumPairsBSingleBranch += pairsBFromSingleBranch[i];

            this.totalTaxa[0] += b[i].totalTaxaCounts[0];
            this.totalTaxa[1] += b[i].totalTaxaCounts[1];
            
        }

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
        
        this.nonQuartets -= changeAmount(branchIndex);
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
        this.nonQuartets += changeAmount(branchIndex);
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

        this.nonQuartets = calcNonQuartets();
        
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