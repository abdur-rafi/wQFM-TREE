package src.ScoreCalculator;

import src.Utility;

public class ScoreCalculatorNode {

    Branch[] branches;
    int[][] subs;
    int nDummyTaxa;
    int[] dummyTaxaToPartitionMap;
    int[] scoresOfBranches;
    int[][] gainsOfBranches;
    int[] dummyTaxaGains;

    public ScoreCalculatorNode(Branch[] b, int[] dummyTaxaToPartitionMap, int[] dummyTaxaGains) {
        this.dummyTaxaToPartitionMap = dummyTaxaToPartitionMap;
        this.branches = b;
        subs = new int[3][2];
        scoresOfBranches = new int[3];
        this.nDummyTaxa = b[0].dummyTaxaCountsIndividual.length;
        for(int i = 0; i < 3; ++i){
            subs[i][0] = 0;
            subs[i][1] = 0;
            for(int j = 0; j < this.nDummyTaxa; ++j){
                int pIndex = this.dummyTaxaToPartitionMap[j];
                if(pIndex == 0)
                    subs[i][0] += b[i].dummyTaxaCountsIndividual[j] * b[(i+1) % 3].dummyTaxaCountsIndividual[j];
                else if(pIndex == 1)
                    subs[i][1] += (b[i].dummyTaxaCountsIndividual[j] * (b[i].dummyTaxaCountsIndividual[j] - 1) ) / 2; 
                else{
                    System.out.println("error");
                }
            }
        }
        gainsOfBranches = new int[3][2];
        this.dummyTaxaGains = dummyTaxaGains;

    }

    private int scoreOf2Branch(int i) {
        int j = (i + 1) % 3;
        int k = (i + 2) % 3;

        int[] csubs = new int[2];
        csubs[0] = subs[i][0];
        csubs[1] = subs[k][1];

        // int score = satisfiedEqn(s[0][0], s[1][0], s[2][1], csubs);
        int score = satisfiedEqn(
            branches[i].totalTaxaCounts[0], 
            branches[j].totalTaxaCounts[0],
            branches[k].totalTaxaCounts[1], 
            csubs
        );

        return score;

    }


    public int score() {
        int res = 0;

        for (int i = 0; i < 3; ++i) {
            scoresOfBranches[i] = scoreOf2Branch(i);
            res += scoresOfBranches[i];
        }
        // this.score = res;
        return res;
    }

    public int[][] gain(int originalScore){
        for(int i = 0; i < 3; ++i){
            gainOf1BranchRealTaxa(i, originalScore);
        }
        return this.gainsOfBranches;
    }

    private int satisfiedEqn(int a1, int a2, int b3, int[] subs) {
        return (a1 * a2 - subs[0]) * (( b3 * (b3 - 1)) / 2 - subs[1] );
    }

    private void gainOf1BranchRealTaxa(int i, int originalScore){

        Branch curr = branches[i];
        for(int p = 0; p < 2; ++p){
            if(curr.realTaxaCountsTotal[p] > 0){
                curr.totalTaxaCounts[p]--;
                curr.totalTaxaCounts[ ( p + 1) % 2]++;
                gainsOfBranches[i][p] = score() - originalScore;


                curr.totalTaxaCounts[p]++;
                curr.totalTaxaCounts[ ( p + 1) % 2]--;
            }
        }
    }

    public void calcDummyTaxaGains(int originalScore){
        for(int i = 0; i < this.nDummyTaxa; ++i){
            int currPartition = this.dummyTaxaToPartitionMap[i];
            int switchedPartition = (currPartition + 1) % 2;
            int currDummyCountCurrBranch, currDummyCountNextBranch;
            for(int j = 0; j < 3; ++j){
                currDummyCountCurrBranch = branches[j].dummyTaxaCountsIndividual[i];
                currDummyCountNextBranch = branches[(j + 1) % 3].dummyTaxaCountsIndividual[i];

                branches[j].totalTaxaCounts[currPartition] -= currDummyCountCurrBranch;
                branches[j].totalTaxaCounts[switchedPartition] += currDummyCountCurrBranch;
                
                if(switchedPartition == 1){                 
                    this.subs[j][0] -= currDummyCountCurrBranch * currDummyCountNextBranch;
                    this.subs[j][1] += Utility.nc2(currDummyCountCurrBranch);
                }
                else{
                    this.subs[j][0] += currDummyCountCurrBranch * currDummyCountNextBranch;
                    this.subs[j][1] -= Utility.nc2(currDummyCountCurrBranch);
                }

                
            }
            int newScore = score();
            this.dummyTaxaGains[i] += newScore - originalScore;

            for(int j = 0; j < 3; ++j){
                currDummyCountCurrBranch = branches[j].dummyTaxaCountsIndividual[i];
                currDummyCountNextBranch = branches[(j + 1) % 3].dummyTaxaCountsIndividual[i];

                branches[j].totalTaxaCounts[currPartition] += currDummyCountCurrBranch;
                branches[j].totalTaxaCounts[switchedPartition] -= currDummyCountCurrBranch;
                
                if(switchedPartition == 1){                 
                    this.subs[j][0] += currDummyCountCurrBranch * currDummyCountNextBranch;
                    this.subs[j][1] -= Utility.nc2(currDummyCountCurrBranch);
                }
                else{
                    this.subs[j][0] -= currDummyCountCurrBranch * currDummyCountNextBranch;
                    this.subs[j][1] += Utility.nc2(currDummyCountCurrBranch);
                }
            }
        }
    }
}

