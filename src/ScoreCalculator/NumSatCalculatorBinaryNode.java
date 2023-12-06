package src.ScoreCalculator;

import src.Tree.Branch;

public class NumSatCalculatorBinaryNode implements NumSatCalculatorNode {

    Branch[] branches;
    double[][] subs;
    int nDummyTaxa;
    int[] dummyTaxaPartition;
    double[][] gainsOfBranches;

    double EPS = 0.000001;
    // NumSatCalculatorNodeE nscn;

    public NumSatCalculatorBinaryNode(Branch[] b, int[] dummyTaxaToPartitionMap, double totalTaxaA, double totalTaxaB, double[] dummyTaxaWeightsIndividual) {
        this.dummyTaxaPartition = dummyTaxaToPartitionMap;
        this.branches = b;
        subs = new double[3][2];
        this.nDummyTaxa = b[0].dummyTaxaWeightsIndividual.length;
        for(int i = 0; i < 3; ++i){
            subs[i][0] = 0;
            subs[i][1] = 0;
            for(int j = 0; j < this.nDummyTaxa; ++j){
                int pIndex = this.dummyTaxaPartition[j];
                if(pIndex == 0)
                    subs[i][0] += b[i].dummyTaxaWeightsIndividual[j] * b[(i+1) % 3].dummyTaxaWeightsIndividual[j];
                else if(pIndex == 1){
                    subs[i][1] += (b[i].dummyTaxaWeightsIndividual[j] * (b[i].dummyTaxaWeightsIndividual[j]) ); 
                }
                else{
                    System.out.println("error");
                }
            }
            subs[i][1] += b[i].realTaxaCounts[1];
        }
        gainsOfBranches = new double[3][2];

        // Branch[] bcs = new Branch[branches.length];
        // for(int i = 0; i < branches.length; ++i){
        //     bcs[i] = new Branch(branches[i]);
        // }
        // this.nscn = new NumSatCalculatorNodeE(bcs, dummyTaxaToPartitionMap, totalTaxaA, totalTaxaB,dummyTaxaWeightsIndividual);
    }

    private double scoreOf2Branch(int i) {
        int j = (i + 1) % 3;
        int k = (i + 2) % 3;

        double[] csubs = new double[2];
        csubs[0] = subs[i][0];
        csubs[1] = subs[k][1];

        double score = satisfiedEqn(
            branches[i].totalTaxaCounts[0], 
            branches[j].totalTaxaCounts[0],
            branches[k].totalTaxaCounts[1], 
            csubs
        );

        

        return score;

    }

    @Override
    public double score() {
        double res = 0;

        for (int i = 0; i < 3; ++i) {
            res += scoreOf2Branch(i);
        }

        // double nscnScore = this.nscn.score();
        
        // if(Math.abs(nscnScore - res) > EPS){
        //     System.out.println("error in score. diff : " + (nscnScore - res));
            
        // }
        return res;
    }

    
    @Override
    public double[][] gainRealTaxa(double originalScore, double multiplier){
        for(int i = 0; i < 3; ++i){
            gainOf1BranchRealTaxa(i, originalScore, multiplier);
        }

        // var x = this.nscn.gainRealTaxa(originalScore, multiplier);
        // for(int i = 0; i < x.length; ++i){
        //     for(int j = 0; j < 2; ++j){
        //         if(x[i][j] != this.gainsOfBranches[i][j]){
        //             System.out.println("error in real gain");
        //         }
        //     }
        // }

        return this.gainsOfBranches;
    }

    private double satisfiedEqn(double a1, double a2, double b3, double[] subs) {
        return (a1 * a2 - subs[0]) * ((( b3 * b3 ) - subs[1] ) / 2);
    }

    @Override
    public void swapRealTaxon(int branchIndex, int currPartition){

        branches[branchIndex].swapRealTaxa(currPartition);

        if(currPartition == 0){
            this.subs[branchIndex][1] += 1;
        }
        else{
            this.subs[branchIndex][1] -= 1;
        }

        // this.nscn.swapRealTaxon(branchIndex, currPartition);
    }

    @Override
    public void swapDummyTaxon(int dummyIndex, int currPartition){
            
        int switchedPartition = 1 - currPartition;
        double currDummyCountCurrBranch, currDummyCountNextBranch;

        for(int i = 0; i < 3; ++i){

            branches[i].swapDummyTaxon(dummyIndex, currPartition);
            currDummyCountCurrBranch = branches[i].dummyTaxaWeightsIndividual[dummyIndex];
            currDummyCountNextBranch = branches[(i + 1) % 3].dummyTaxaWeightsIndividual[dummyIndex];

            if(switchedPartition == 1){                 
                this.subs[i][0] -= currDummyCountCurrBranch * currDummyCountNextBranch;
                // 
                this.subs[i][1] += currDummyCountCurrBranch * currDummyCountCurrBranch;
            }
            else{
                this.subs[i][0] += currDummyCountCurrBranch * currDummyCountNextBranch;
                this.subs[i][1] -= currDummyCountCurrBranch * currDummyCountCurrBranch;
            }

        }

        // this.nscn.swapDummyTaxon(dummyIndex, currPartition);
            
    }


    private void gainOf1BranchRealTaxa(int i, double originalScore, double multiplier){

        Branch curr = branches[i];
        for(int p = 0; p < 2; ++p){
            if(curr.realTaxaCounts[p] > 0){
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
    
    @Override
    public void gainDummyTaxa(double originalScore, double multiplier, double[] dummyTaxaGains){

        for(int i = 0; i < this.nDummyTaxa; ++i){
            int currPartition = this.dummyTaxaPartition[i];
            int switchedPartition = 1 - currPartition;
            this.swapDummyTaxon(i, currPartition);

            // this.nscn.swapDummyTaxon(i, currPartition);
            // double nscnScore =  this.nscn.score();

            // if(nscnScore != this.score()){
            //     System.out.println("error in score in dt gain");
            // }

            double newScore = score();
            dummyTaxaGains[i] +=  multiplier * (newScore - originalScore);
            this.swapDummyTaxon(i, switchedPartition);

            // this.nscn.swapDummyTaxon(i, switchedPartition);
        }
    }



}

