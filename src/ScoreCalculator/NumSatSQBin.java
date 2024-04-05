package src.ScoreCalculator;

import src.Tree.Branch;

public class NumSatSQBin implements NumSatSQ{
    Branch[] branches;
    int nDummyTaxa;
    int[] dummyTaxaPartition;
    double[][] pairsFromBranch;
    double[] pairsFromLeftRightBranch;

    double EPS = 0.000001;
    
    
    public NumSatSQBin(Branch[] b, int[] dummyTaxaToPartitionMap){

        this.dummyTaxaPartition = dummyTaxaToPartitionMap;
        this.branches = b;
        this.pairsFromBranch = new double[3][2];
        this.nDummyTaxa = b[0].dummyTaxaWeightsIndividual.length;
        this.pairsFromLeftRightBranch = new double[2];

        for(int i = 0; i < 3; ++i){
            this.pairsFromBranch[i][0] = b[i].totalTaxaCounts[0] * b[i].totalTaxaCounts[0] - b[i].realTaxaCounts[0];
            this.pairsFromBranch[i][1] = b[i].totalTaxaCounts[1] * b[i].totalTaxaCounts[1] - b[i].realTaxaCounts[1];
            for(int j = 0; j < this.nDummyTaxa; ++j){
                int partition = this.dummyTaxaPartition[j];
                this.pairsFromBranch[i][partition] -= b[i].dummyTaxaWeightsIndividual[j] * b[i].dummyTaxaWeightsIndividual[j];
            }

            this.pairsFromBranch[i][0] /= 2;
            this.pairsFromBranch[i][1] /= 2;
        }
        this.pairsFromLeftRightBranch[0] = b[0].totalTaxaCounts[0] * b[1].totalTaxaCounts[0];
        this.pairsFromLeftRightBranch[1] = b[0].totalTaxaCounts[1] * b[1].totalTaxaCounts[1];

        for(int j = 0; j < this.nDummyTaxa; ++j){
            int partition = this.dummyTaxaPartition[j];
            this.pairsFromLeftRightBranch[partition] -= b[0].dummyTaxaWeightsIndividual[j] * b[1].dummyTaxaWeightsIndividual[j];
        }

    }

    @Override
    public double score() {
        double score = 0;
        score += pairsFromBranch[0][0] * pairsFromBranch[1][1];
        score += pairsFromBranch[0][1] * pairsFromBranch[1][0];
        score += pairsFromLeftRightBranch[0] * pairsFromBranch[2][1];
        score += pairsFromLeftRightBranch[1] * pairsFromBranch[2][0];

        return score;
    }

    // [branch index][partition][common, disjoints]
    @Override
    public double[][][] gainRealTaxa(double originalScore, double multiplier) {
        double[][][] gains = new double[3][2][2];
        for(int i = 0; i < 2; ++i){
            for(int p = 0; p < 2; ++p){
                if(this.branches[i].realTaxaCounts[p] > 0){
                    this.swapRealTaxon(i, p);
                    this.branches[i].swapRealTaxa(p);
                    gains[i][p][1] = multiplier * (this.score() - originalScore);
                    if(this.branches[2].realTaxaCounts[p] > 0){
                        this.swapRealTaxon(2, p);
                        this.branches[2].swapRealTaxa(p);
                        gains[i][p][0] = multiplier * (this.score() - originalScore);
                        this.swapRealTaxon(2, 1 - p);
                        this.branches[2].swapRealTaxa(1 - p);
                    }
                    this.swapRealTaxon(i, 1 - p);
                    this.branches[i].swapRealTaxa(1 - p);
                }
            }
        }
        for(int p = 0; p < 2; ++p){
            if(this.branches[2].realTaxaCounts[p] > 0){
                this.swapRealTaxon(2, p);
                this.branches[2].swapRealTaxa(p);
                gains[2][p][1] = multiplier * (this.score() - originalScore);
                this.swapRealTaxon(2, 1 - p);
                this.branches[2].swapRealTaxa(1 - p);
            }
        }
        return gains;
    }

    @Override
    public void swapRealTaxon(int branchIndex, int currPartition) {
        this.pairsFromBranch[branchIndex][currPartition] -= this.branches[branchIndex].totalTaxaCounts[currPartition] - 1;
        this.pairsFromBranch[branchIndex][1 - currPartition] += this.branches[branchIndex].totalTaxaCounts[1 - currPartition];

        if(branchIndex < 2){
            this.pairsFromLeftRightBranch[currPartition] -= this.branches[1 - branchIndex].totalTaxaCounts[currPartition];
            this.pairsFromLeftRightBranch[1 - currPartition] += this.branches[1 - branchIndex].totalTaxaCounts[1 - currPartition];
        }
    }

    @Override
    public void swapDummyTaxon(int dummyIndex, int currPartition) {
        for(int i = 0; i < this.branches.length; ++i){
            double wi = this.branches[i].dummyTaxaWeightsIndividual[dummyIndex];
            this.pairsFromBranch[i][currPartition] -= (this.branches[i].totalTaxaCounts[currPartition] - wi) * wi;
            this.pairsFromBranch[i][1 - currPartition] += (this.branches[i].totalTaxaCounts[1 - currPartition]) * wi;
        }
        double wl = this.branches[0].dummyTaxaWeightsIndividual[dummyIndex];
        double wr = this.branches[1].dummyTaxaWeightsIndividual[dummyIndex];

        this.pairsFromLeftRightBranch[currPartition] -= (wl * (this.branches[1].totalTaxaCounts[currPartition] - wr) + wr * (this.branches[0].totalTaxaCounts[currPartition] - wl));
        this.pairsFromLeftRightBranch[1 - currPartition] += (wl * this.branches[1].totalTaxaCounts[1 - currPartition] + wr * this.branches[0].totalTaxaCounts[1 - currPartition]);
    }

    @Override
    public void gainDummyTaxa(double originalScore, double multiplier, double[] dummyTaxaGains) {
        for(int i = 0; i < this.nDummyTaxa; ++i){
            int currPartition = this.dummyTaxaPartition[i];
            this.swapDummyTaxon(i, currPartition);

            for(int j = 0; j < this.branches.length; ++j){
                this.branches[j].swapDummyTaxon(i, currPartition);
            }
            dummyTaxaGains[i] += multiplier * (this.score() - originalScore);
            
            this.swapDummyTaxon(i, 1 - currPartition);
            for(int j = 0; j < this.branches.length; ++j){
                this.branches[j].swapDummyTaxon(i, 1 - currPartition);
            }
        }
    }

    @Override
    public void batchTransferRealTaxon(int branchIndex, int netTranser) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'batchTransferRealTaxon'");
    }

    
    
}
