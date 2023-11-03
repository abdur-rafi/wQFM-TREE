package src.ScoreCalculator;


import src.Utility;

public class ScoreCalculatorNode {

    Branch[] branches;
    double[][] subs;
    int nDummyTaxa;
    int[] dummyTaxaToPartitionMap;
    double[] scoresOfBranches;
    // g1. should be double
    double[][] gainsOfBranches;
    double[] dummyTaxaGains;

    public ScoreCalculatorNode(Branch[] b, int[] dummyTaxaToPartitionMap, double[] dummyTaxaGains) {
        this.dummyTaxaToPartitionMap = dummyTaxaToPartitionMap;
        this.branches = b;
        subs = new double[3][2];
        scoresOfBranches = new double[3];
        this.nDummyTaxa = b[0].dummyTaxaCountsIndividual.length;
        for(int i = 0; i < 3; ++i){
            subs[i][0] = 0;
            subs[i][1] = 0;
            for(int j = 0; j < this.nDummyTaxa; ++j){
                int pIndex = this.dummyTaxaToPartitionMap[j];
                if(pIndex == 0)
                    subs[i][0] += b[i].dummyTaxaCountsIndividual[j] * b[(i+1) % 3].dummyTaxaCountsIndividual[j];
                else if(pIndex == 1){
                    // 8. should be inidividual squarred
                    subs[i][1] += (b[i].dummyTaxaCountsIndividual[j] * (b[i].dummyTaxaCountsIndividual[j]) ); 
                }
                else{
                    System.out.println("error");
                }
            }
            subs[i][1] += b[i].realTaxaCountsTotal[1];
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

    public double[][] gain(double originalScore){
        for(int i = 0; i < 3; ++i){
            gainOf1BranchRealTaxa(i, originalScore);
        }
        return this.gainsOfBranches;
    }

    private double satisfiedEqn(double a1, double a2, double b3, double[] subs) {
        // 10. should be b3 squarred, data types should be double
        return (a1 * a2 - subs[0]) * ((( b3 * b3 ) - subs[1] ) / 2);
    }

    private void gainOf1BranchRealTaxa(int i, double originalScore){

        Branch curr = branches[i];
        for(int p = 0; p < 2; ++p){
            if(curr.realTaxaCountsTotal[p] > 0){
                // g3. adjust sub
                curr.totalTaxaCounts[p]--;
                curr.totalTaxaCounts[ ( p + 1) % 2]++;
                
                if(p == 0){
                    this.subs[i][1] += 1;
                }
                else{
                    this.subs[i][1] -= 1;
                }

                gainsOfBranches[i][p] = score() - originalScore;


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

    public void calcDummyTaxaGains(double originalScore){

        for(int i = 0; i < this.nDummyTaxa; ++i){
            int currPartition = this.dummyTaxaToPartitionMap[i];
            int switchedPartition = (currPartition + 1) % 2;
            double currDummyCountCurrBranch, currDummyCountNextBranch;

            for(int j = 0; j < 3; ++j){

                currDummyCountCurrBranch = branches[j].dummyTaxaCountsIndividual[i];
                currDummyCountNextBranch = branches[(j + 1) % 3].dummyTaxaCountsIndividual[i];

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
            this.dummyTaxaGains[i] += newScore - originalScore;

            for(int j = 0; j < 3; ++j){
                currDummyCountCurrBranch = branches[j].dummyTaxaCountsIndividual[i];
                currDummyCountNextBranch = branches[(j + 1) % 3].dummyTaxaCountsIndividual[i];

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

