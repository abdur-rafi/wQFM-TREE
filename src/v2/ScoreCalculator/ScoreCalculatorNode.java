package src.v2.ScoreCalculator;

import src.Utility;
import src.v2.Tree.Branch;

public class ScoreCalculatorNode {

    Branch[] branches;
    double[][] subs;
    int nDummyTaxa;
    short[] dummyTaxaPartition;
    double[] scoresOfBranches;
    // g1. should be double
    double[][] gainsOfBranches;
    double[] dummyTaxaGains;

    public ScoreCalculatorNode(Branch[] b, short[] dummyTaxaToPartitionMap, double[] dummyTaxaGains) {
        this.dummyTaxaPartition = dummyTaxaToPartitionMap;
        this.branches = b;
        subs = new double[3][2];
        scoresOfBranches = new double[3];
        this.nDummyTaxa = b[0].dummyTaxaWeightsIndividual.length;
        for(int i = 0; i < 3; ++i){
            subs[i][0] = 0;
            subs[i][1] = 0;
            for(int j = 0; j < this.nDummyTaxa; ++j){
                int pIndex = this.dummyTaxaPartition[j];
                if(pIndex == 0)
                    subs[i][0] += b[i].dummyTaxaWeightsIndividual[j] * b[(i+1) % 3].dummyTaxaWeightsIndividual[j];
                else if(pIndex == 1){
                    // 8. should be inidividual squarred
                    subs[i][1] += (b[i].dummyTaxaWeightsIndividual[j] * (b[i].dummyTaxaWeightsIndividual[j]) ); 
                }
                else{
                    System.out.println("error");
                }
            }
            subs[i][1] += b[i].realTaxaCounts[1];
            // 9. subs[i][1] += realTaxaCount in 2nd partition in ith branch
        }
        gainsOfBranches = new double[3][2];
        this.dummyTaxaGains = dummyTaxaGains;

    }

    private double scoreOf2Branch(int i) {
        int j = (i + 1) % 3;
        int k = (i + 2) % 3;

        double[] csubs = new double[2];
        csubs[0] = subs[i][0];
        csubs[1] = subs[k][1];

        // int score = satisfiedEqn(s[0][0], s[1][0], s[2][1], csubs);
        double score = satisfiedEqn(
            branches[i].totalTaxaCounts[0], 
            branches[j].totalTaxaCounts[0],
            branches[k].totalTaxaCounts[1], 
            csubs
        );

        return score;

    }


    public double score() {
        double res = 0;

        for (int i = 0; i < 3; ++i) {
            scoresOfBranches[i] = scoreOf2Branch(i);
            res += scoresOfBranches[i];
        }
        // this.score = res;
        return res;
    }

    // g2. shoulde be double

    public double[][] gainRealTaxa(double originalScore, double multiplier){
        for(int i = 0; i < 3; ++i){
            gainOf1BranchRealTaxa(i, originalScore, multiplier);
        }
        return this.gainsOfBranches;
    }

    private double satisfiedEqn(double a1, double a2, double b3, double[] subs) {
        // 10. should be b3 squarred, data types should be double
        return (a1 * a2 - subs[0]) * ((( b3 * b3 ) - subs[1] ) / 2);
    }

    public void swapRealTaxa(int branchIndex, int currPartition){

        branches[branchIndex].swapRealTaxa(currPartition);

        if(currPartition == 0){
            this.subs[branchIndex][1] += 1;
        }
        else{
            this.subs[branchIndex][1] -= 1;
        }

    }


    private void gainOf1BranchRealTaxa(int i, double originalScore, double multiplier){

        Branch curr = branches[i];
        for(int p = 0; p < 2; ++p){
            if(curr.realTaxaCounts[p] > 0){
                // g3. adjust sub
                curr.totalTaxaCounts[p]--;
                curr.totalTaxaCounts[ ( p + 1) % 2]++;
                
                if(p == 0){
                    this.subs[i][1] += 1;
                }
                else{
                    this.subs[i][1] -= 1;
                }

                gainsOfBranches[i][p] = multiplier * (score() - originalScore);


                curr.totalTaxaCounts[p]++;
                curr.totalTaxaCounts[ ( p + 1) % 2]--;

                if(p == 0){
                    this.subs[i][1] -= 1;
                }
                else{
                    this.subs[i][1] += 1;
                }
            }
        }
    }

    public void gainDummyTaxa(double originalScore, double multiplier){

        for(int i = 0; i < this.nDummyTaxa; ++i){
            int currPartition = this.dummyTaxaPartition[i];
            int switchedPartition = (currPartition + 1) % 2;
            double currDummyCountCurrBranch, currDummyCountNextBranch;

            for(int j = 0; j < 3; ++j){

                currDummyCountCurrBranch = branches[j].dummyTaxaWeightsIndividual[i];
                currDummyCountNextBranch = branches[(j + 1) % 3].dummyTaxaWeightsIndividual[i];

                branches[j].totalTaxaCounts[currPartition] -= currDummyCountCurrBranch;
                branches[j].totalTaxaCounts[switchedPartition] += currDummyCountCurrBranch;
                // g4. adjust sub
                if(switchedPartition == 1){                 
                    this.subs[j][0] -= currDummyCountCurrBranch * currDummyCountNextBranch;
                    // 
                    this.subs[j][1] += currDummyCountCurrBranch * currDummyCountCurrBranch;
                }
                else{
                    this.subs[j][0] += currDummyCountCurrBranch * currDummyCountNextBranch;
                    this.subs[j][1] -= currDummyCountCurrBranch * currDummyCountCurrBranch;
                }

                
            }
            double newScore = score();
            this.dummyTaxaGains[i] +=  multiplier * (newScore - originalScore);

            for(int j = 0; j < 3; ++j){
                currDummyCountCurrBranch = branches[j].dummyTaxaWeightsIndividual[i];
                currDummyCountNextBranch = branches[(j + 1) % 3].dummyTaxaWeightsIndividual[i];

                branches[j].totalTaxaCounts[currPartition] += currDummyCountCurrBranch;
                branches[j].totalTaxaCounts[switchedPartition] -= currDummyCountCurrBranch;
                
                if(switchedPartition == 1){                 
                    this.subs[j][0] += currDummyCountCurrBranch * currDummyCountNextBranch;
                    // 
                    this.subs[j][1] -= currDummyCountCurrBranch * currDummyCountCurrBranch;
                }
                else{
                    this.subs[j][0] -= currDummyCountCurrBranch * currDummyCountNextBranch;
                    this.subs[j][1] += currDummyCountCurrBranch * currDummyCountCurrBranch;
                }
            }
        }
    }



}

