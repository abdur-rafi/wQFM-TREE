package src.ScoreCalculator;

import src.Tree.Branch;

public class NumSatSQBin implements NumSatCalculatorNode{
    Branch[] branches;
    int nDummyTaxa;
    int[] dummyTaxaPartition;
    double[][] pairsFromBranch;
    double[] pairsAFromLeftRightBranch;

    double EPS = 0.000001;
    
    
    public NumSatSQBin(Branch[] b, int[] dummyTaxaToPartitionMap){

        this.dummyTaxaPartition = dummyTaxaToPartitionMap;
        this.branches = b;
        this.pairsFromBranch = new double[3][2];
        this.nDummyTaxa = b[0].dummyTaxaWeightsIndividual.length;
        this.pairsAFromLeftRightBranch = new double[2];

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
        this.pairsAFromLeftRightBranch[0] = b[0].totalTaxaCounts[0] * b[1].totalTaxaCounts[0];
        this.pairsAFromLeftRightBranch[1] = b[0].totalTaxaCounts[1] * b[1].totalTaxaCounts[1];

        for(int j = 0; j < this.nDummyTaxa; ++j){
            int partition = this.dummyTaxaPartition[j];
            this.pairsAFromLeftRightBranch[partition] -= b[0].dummyTaxaWeightsIndividual[j] * b[1].dummyTaxaWeightsIndividual[j];
        }

    }

    @Override
    public double score() {
        double score = 0;
        score += pairsFromBranch[0][0] * pairsFromBranch[1][1];
        score += pairsFromBranch[0][1] * pairsFromBranch[1][0];
        score += pairsAFromLeftRightBranch[0] * pairsFromBranch[2][1];
        score += pairsAFromLeftRightBranch[1] * pairsFromBranch[2][0];

        return score;
    }

    @Override
    public double[][] gainRealTaxa(double originalScore, double multiplier) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'gainRealTaxa'");
    }

    @Override
    public void swapRealTaxon(int branchIndex, int currPartition) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'swapRealTaxon'");
    }

    @Override
    public void swapDummyTaxon(int dummyIndex, int currPartition) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'swapDummyTaxon'");
    }

    @Override
    public void gainDummyTaxa(double originalScore, double multiplier, double[] dummyTaxaGains) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'gainDummyTaxa'");
    }

    @Override
    public void batchTransferRealTaxon(int branchIndex, int netTranser) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'batchTransferRealTaxon'");
    }

    
    
}
