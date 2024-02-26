package src.ScoreCalculator;

import src.Tree.Branch;

public class NumSatCalculatorBinaryNode implements NumSatCalculatorNode {

    Branch[] branches;
    double[][] subs;
    int nDummyTaxa;
    int[] dummyTaxaPartition;
    double[][] gainsOfBranches;

    double EPS = 0.000001;

    boolean testE = false;
    TestNodeE test;

    class TestNodeE{
        
        
        public NumSatCalculatorNodeE nscn;

        public TestNodeE(Branch[] b, int[] dummyTaxaToPartitionMap){
            
            Branch[] bcs = new Branch[b.length];
            for(int i = 0; i < b.length; ++i){
                bcs[i] = new Branch(b[i]);
            }
            this.nscn = new NumSatCalculatorNodeE(bcs, dummyTaxaToPartitionMap);
        }

        public void testScore(double score){
            double mScore = this.nscn.score();
            if(Math.abs(mScore - score) > EPS){
                System.out.println("error in score");
            }
        }

        public void testRTGain(double[][] rtGains, double originalScore, double multiplier){
            double[][] testGains = this.nscn.gainRealTaxa(originalScore, multiplier);
            for(int i = 0; i < 3; ++i){
                for(int j = 0; j < 2; ++j){
                    if(Math.abs(testGains[i][j] - rtGains[i][j]) > EPS  && this.nscn.branches[i].realTaxaCounts[j] > 0){
                        System.out.println("error in real gain");
                    }
                }
            }
        }
        

    }

    public NumSatCalculatorBinaryNode(Branch[] b, int[] dummyTaxaToPartitionMap) {
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

        if(testE){
            this.test = new TestNodeE(b, dummyTaxaToPartitionMap);
        }

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

        if(testE){
            this.test.testScore(res);
        }
        return res;
    }

    
    @Override
    public double[][] gainRealTaxa(double originalScore, double multiplier){
        for(int i = 0; i < 3; ++i){
            gainOf1BranchRealTaxa(i, originalScore, multiplier);
        }

        if(testE){
            this.test.testRTGain(this.gainsOfBranches, originalScore, multiplier);
        }


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

        if(this.testE){
            this.test.nscn.swapRealTaxon(branchIndex, currPartition);
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

        if(this.testE){
            this.test.nscn.swapDummyTaxon(dummyIndex, currPartition);
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
                
                if(testE){
                    this.test.nscn.swapRealTaxon(i, p);
                }
                

                gainsOfBranches[i][p] = multiplier * (score() - originalScore);

                if(testE){
                    this.test.nscn.swapRealTaxon(i, 1-p );
                }

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

            double newScore = score();

            // if(this.testE){
            //     this.test.nscn.swapDummyTaxon(i, currPartition);
            //     // this.test.testScore(newScore);
            // }

            dummyTaxaGains[i] +=  multiplier * (newScore - originalScore);
            this.swapDummyTaxon(i, switchedPartition);

            // if(this.testE){
            //     this.test.nscn.swapDummyTaxon(i, switchedPartition);
            // }
        }
    }



}

