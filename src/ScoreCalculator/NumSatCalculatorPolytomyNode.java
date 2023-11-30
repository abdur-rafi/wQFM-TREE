package src.ScoreCalculator;

import src.Tree.Branch;

public class NumSatCalculatorPolytomyNode implements NumSatCalculatorNode {

    Branch[] branches;
    double[][][] subs;
    double[] subsB;
    int nDummyTaxa;



    int[] dummyTaxaPartition;
    double[][] gainsOfBranches;

    public NumSatCalculatorPolytomyNode(Branch[] b, int[] dummyTaxaToPartitionMap) {

        this.dummyTaxaPartition = dummyTaxaToPartitionMap;
        this.branches = b;
        
        subs = new double[b.length][b.length][2];
        subsB = new double[b.length];

        this.nDummyTaxa = b[0].dummyTaxaWeightsIndividual.length;

        for(int i = 0; i < b.length; ++i){

            for(int j = 0; j < this.nDummyTaxa; ++j){
                int pIndex = this.dummyTaxaPartition[j];
                for(int k = i + 1; k < b.length; ++k){
                    subs[i][k][pIndex] += b[i].dummyTaxaWeightsIndividual[j] * b[k].dummyTaxaWeightsIndividual[j];
                }
                if(pIndex == 1){
                    subsB[i] += (b[i].dummyTaxaWeightsIndividual[j] * (b[i].dummyTaxaWeightsIndividual[j]));
                }
            }
            subsB[i] += b[i].realTaxaCounts[1];
        }
        gainsOfBranches = new double[b.length][2];

    }

    private double scoreOf2Branch(int i, int j) {
        double pairsOfA = (branches[i].totalTaxaCounts[0] * branches[j].totalTaxaCounts[0] - subs[i][j][0]);
        double pairsOfB = 0;
        for(int k = 0; k < this.branches.length; ++k){
            if(k != i && k != j){
                pairsOfB += (branches[k].totalTaxaCounts[1] * branches[k].totalTaxaCounts[1] - subsB[k]) / 2;
            }
        }
        return pairsOfA * pairsOfB;
    }

    @Override
    public double score() {
        double res = 0;

        double sumB = 0;
        for(int i = 0; i < branches.length; ++i){
            for(int j = i + 1; j < branches.length; ++j){
                sumB +=  (branches[i].totalTaxaCounts[1] * branches[j].totalTaxaCounts[1] - subs[i][j][1] ) ;
            }
        }

        for(int i = 0; i < branches.length; ++i){
            for(int j = i + 1; j < branches.length; ++j){
                double pairsOfA = (branches[i].totalTaxaCounts[0] * branches[j].totalTaxaCounts[0] - subs[i][j][0]);
                double pairsOfB = (sumB - (branches[i].totalTaxaCounts[1] * branches[j].totalTaxaCounts[1] - subs[i][j][1] ));

                res += scoreOf2Branch(i, j);
                res += (pairsOfA * pairsOfB) / 2;
            }
        }
        return res;
    }

    
    @Override
    public double[][] gainRealTaxa(double originalScore, double multiplier){
        for(int i = 0; i < branches.length; ++i){
            gainOf1BranchRealTaxa(i, originalScore, multiplier);
        }
        return this.gainsOfBranches;
    }


    @Override
    public void swapRealTaxon(int branchIndex, int currPartition){

        branches[branchIndex].swapRealTaxa(currPartition);

        if(currPartition == 0){
            this.subsB[branchIndex] += 1;
        }
        else{
            this.subsB[branchIndex] -= 1;
        }

    }

    @Override
    public void swapDummyTaxon(int dummyIndex, int currPartition){
            
        int switchedPartition = 1 - currPartition;
        double currDummyCountCurrBranch, currDummyCountNextBranch;

        for(int i = 0; i < this.branches.length; ++i){

            branches[i].swapDummyTaxon(dummyIndex, currPartition);
            currDummyCountCurrBranch = branches[i].dummyTaxaWeightsIndividual[dummyIndex];

            if(switchedPartition == 1){                 
                this.subsB[i] += currDummyCountCurrBranch * currDummyCountCurrBranch;
            }
            else{
                this.subsB[i] -= currDummyCountCurrBranch * currDummyCountCurrBranch;
            }
            for(int j = i + 1; j < this.branches.length; ++j){

                currDummyCountNextBranch = branches[j].dummyTaxaWeightsIndividual[dummyIndex];
                this.subs[i][j][switchedPartition] += currDummyCountCurrBranch * currDummyCountNextBranch;
                this.subs[i][j][currPartition] -= currDummyCountCurrBranch * currDummyCountNextBranch;

                // if(switchedPartition == 1){                 
                //     this.subs[i][j][0] -= currDummyCountCurrBranch * currDummyCountNextBranch;
                // }
                // else{
                //     this.subs[i][j][0] += currDummyCountCurrBranch * currDummyCountNextBranch;
                // }
            }
        }
            
    }


    private void gainOf1BranchRealTaxa(int i, double originalScore, double multiplier){

        Branch curr = branches[i];
        for(int p = 0; p < 2; ++p){
            if(curr.realTaxaCounts[p] > 0){
                // g3. adjust sub
                curr.totalTaxaCounts[p]--;
                curr.totalTaxaCounts[ 1 - p ]++;
                
                if(p == 0){
                    this.subsB[i] += 1;
                }
                else{
                    this.subsB[i] -= 1;
                }

                gainsOfBranches[i][p] = multiplier * (score() - originalScore);


                curr.totalTaxaCounts[p]++;
                curr.totalTaxaCounts[ 1 - p ]--;

                if(p == 0){
                    this.subsB[i] -= 1;
                }
                else{
                    this.subsB[i] += 1;
                }
            }
        }
    }
    
    @Override
    public void gainDummyTaxa(double originalScore, double multiplier, double[] dummyTaxaGains){
        for(int i = 0; i < this.nDummyTaxa; ++i){
            int currPartition = this.dummyTaxaPartition[i];
            int switchedPartition = 1 - currPartition;

            this.swapDummyTaxon(i, currPartition);
                
            double newScore = score();
            dummyTaxaGains[i] +=  multiplier * (newScore - originalScore);
            this.swapDummyTaxon(i, switchedPartition);
        }
    }



}

