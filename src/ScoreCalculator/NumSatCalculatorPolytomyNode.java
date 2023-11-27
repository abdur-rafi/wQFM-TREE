package src.ScoreCalculator;

import src.Tree.Branch;

public class NumSatCalculatorPolytomyNode implements NumSatCalculatorNode {

    Branch[] branches;
    double[][][] subs;
    int nDummyTaxa;

    int[] dummyTaxaPartition;
    double[][] gainsOfBranches;

    public NumSatCalculatorPolytomyNode(Branch[] b, int[] dummyTaxaToPartitionMap) {

        this.dummyTaxaPartition = dummyTaxaToPartitionMap;
        this.branches = b;
        
        subs = new double[b.length][b.length][2];

        this.nDummyTaxa = b[0].dummyTaxaWeightsIndividual.length;

        for(int i = 0; i < b.length; ++i){
            subs[i][0][1] = 0;
            for(int j = 0; j < this.nDummyTaxa; ++j){
                int pIndex = this.dummyTaxaPartition[j];
                if(pIndex == 0){
                    for(int k = i + 1; k < b.length; ++k){
                        subs[i][k][0] += b[i].dummyTaxaWeightsIndividual[j] * b[k].dummyTaxaWeightsIndividual[j];
                    }
                }
                else if(pIndex == 1){
                    subs[i][0][1] += (b[i].dummyTaxaWeightsIndividual[j] * (b[i].dummyTaxaWeightsIndividual[j])); 
                }
                else{
                    System.out.println("error");
                }
            }
            subs[i][0][1] += b[i].realTaxaCounts[1];
            // 9. subs[i][1] += realTaxaCount in 2nd partition in ith branch
        }
        gainsOfBranches = new double[b.length][2];

    }

    private double scoreOf2Branch(int i, int j) {
        // System.out.println("subs[i][j][0]: " + subs[i][j][0]);
        double pairsOfA = (branches[i].totalTaxaCounts[0] * branches[j].totalTaxaCounts[0] - subs[i][j][0]);
        // System.out.println("pairsOfA: " + pairsOfA);
        double pairsOfB = 0;
        for(int k = 0; k < this.branches.length; ++k){
            if(k != i && k != j){
                pairsOfB += (branches[k].totalTaxaCounts[1] * branches[k].totalTaxaCounts[1] - subs[k][0][1]) / 2;
                // System.out.println( "subs : " + subs[k][0][1]);
            }
        }


        return pairsOfA * pairsOfB;

    }

    @Override
    public double score() {
        double res = 0;

        for(int i = 0; i < branches.length; ++i){
            for(int j = i + 1; j < branches.length; ++j){
                res += scoreOf2Branch(i, j);
            }
            
            // System.out.println("rt: " + branches[i].realTaxaCounts[0] + " " + branches[i].realTaxaCounts[1]);
            // System.out.println("tt: " + branches[i].totalTaxaCounts[0] + " " + branches[i].totalTaxaCounts[1]);

        }
        // System.out.println("sat at polytomy node: " + res);
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
            this.subs[branchIndex][0][1] += 1;
        }
        else{
            this.subs[branchIndex][0][1] -= 1;
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
                this.subs[i][0][1] += currDummyCountCurrBranch * currDummyCountCurrBranch;
            }
            else{
                this.subs[i][0][1] -= currDummyCountCurrBranch * currDummyCountCurrBranch;
            }
            for(int j = i + 1; j < this.branches.length; ++j){

                currDummyCountNextBranch = branches[j].dummyTaxaWeightsIndividual[dummyIndex];
                if(switchedPartition == 1){                 
                    this.subs[i][j][0] -= currDummyCountCurrBranch * currDummyCountNextBranch;
                }
                else{
                    this.subs[i][j][0] += currDummyCountCurrBranch * currDummyCountNextBranch;
                }
            }
            // currDummyCountNextBranch = branches[(i + 1) % 3].dummyTaxaWeightsIndividual[dummyIndex];

            // if(switchedPartition == 1){                 
            //     this.subs[i][0] -= currDummyCountCurrBranch * currDummyCountNextBranch;
            //     // 
            //     this.subs[i][1] += currDummyCountCurrBranch * currDummyCountCurrBranch;
            // }
            // else{
            //     this.subs[i][0] += currDummyCountCurrBranch * currDummyCountNextBranch;
            //     this.subs[i][1] -= currDummyCountCurrBranch * currDummyCountCurrBranch;
            // }

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
                    this.subs[i][0][1] += 1;
                }
                else{
                    this.subs[i][0][1] -= 1;
                }

                gainsOfBranches[i][p] = multiplier * (score() - originalScore);


                curr.totalTaxaCounts[p]++;
                curr.totalTaxaCounts[ 1 - p ]--;

                if(p == 0){
                    this.subs[i][0][1] -= 1;
                }
                else{
                    this.subs[i][0][1] += 1;
                }
            }
        }
    }
    
    @Override
    public void gainDummyTaxa(double originalScore, double multiplier, double[] dummyTaxaGains){

        for(int i = 0; i < this.nDummyTaxa; ++i){
            int currPartition = this.dummyTaxaPartition[i];
            int switchedPartition = 1 - currPartition;
            // double currDummyCountCurrBranch, currDummyCountNextBranch;

            this.swapDummyTaxon(i, currPartition);
            // for(int j = 0; j < 3; ++j){

            //     currDummyCountCurrBranch = branches[j].dummyTaxaWeightsIndividual[i];
            //     currDummyCountNextBranch = branches[(j + 1) % 3].dummyTaxaWeightsIndividual[i];

            //     branches[j].totalTaxaCounts[currPartition] -= currDummyCountCurrBranch;
            //     branches[j].totalTaxaCounts[switchedPartition] += currDummyCountCurrBranch;
            //     // g4. adjust sub
            //     if(switchedPartition == 1){                 
            //         this.subs[j][0] -= currDummyCountCurrBranch * currDummyCountNextBranch;
            //         // 
            //         this.subs[j][1] += currDummyCountCurrBranch * currDummyCountCurrBranch;
            //     }
            //     else{
            //         this.subs[j][0] += currDummyCountCurrBranch * currDummyCountNextBranch;
            //         this.subs[j][1] -= currDummyCountCurrBranch * currDummyCountCurrBranch;
            //     }

                
            // }
            double newScore = score();
            dummyTaxaGains[i] +=  multiplier * (newScore - originalScore);
            
            this.swapDummyTaxon(i, switchedPartition);
            // for(int j = 0; j < 3; ++j){
            //     currDummyCountCurrBranch = branches[j].dummyTaxaWeightsIndividual[i];
            //     currDummyCountNextBranch = branches[(j + 1) % 3].dummyTaxaWeightsIndividual[i];

            //     branches[j].totalTaxaCounts[currPartition] += currDummyCountCurrBranch;
            //     branches[j].totalTaxaCounts[switchedPartition] -= currDummyCountCurrBranch;
                
            //     if(switchedPartition == 1){                 
            //         this.subs[j][0] += currDummyCountCurrBranch * currDummyCountNextBranch;
            //         // 
            //         this.subs[j][1] -= currDummyCountCurrBranch * currDummyCountCurrBranch;
            //     }
            //     else{
            //         this.subs[j][0] -= currDummyCountCurrBranch * currDummyCountNextBranch;
            //         this.subs[j][1] += currDummyCountCurrBranch * currDummyCountCurrBranch;
            //     }
            // }
        }
    }



}

