package src.ScoreCalculator;

import src.Tree.Branch;

public class NumSatSQBin2 implements NumSatSQ{
    // Branch[] branches;
    Branch[] childs;
    Branch parent;

    int nDummyTaxa;
    int[] dummyTaxaPartition;

    double[][] pairsFromBranch;
    double[][] pairsWithParent;
    double[] pairsLR;


    double EPS = 0.000001;
    

    int nodeFreq;


    
    public NumSatSQBin2(Branch[] childs, Branch parent, int[] dummyTaxaToPartitionMap, int nodeFreq){

        this.nodeFreq = nodeFreq;
        this.childs = childs;
        this.parent = parent;
        this.dummyTaxaPartition = dummyTaxaToPartitionMap;
        this.pairsFromBranch = new double[2][2];
        this.pairsWithParent = new double[2][2];
        this.pairsLR = new double[2];
        this.nDummyTaxa = childs[0].dummyTaxaWeightsIndividual.length;

        for(int i = 0; i < 2; ++i){

            for(int p = 0; p < 2; ++p){
                this.pairsFromBranch[i][p] = this.childs[i].totalTaxaCounts[p] * this.childs[i].totalTaxaCounts[p] - this.childs[i].realTaxaCounts[p];
                this.pairsWithParent[i][p] = this.childs[i].totalTaxaCounts[p] * this.parent.totalTaxaCounts[p];
            }

            for(int j = 0; j < this.nDummyTaxa; ++j){
                int p = this.dummyTaxaPartition[j];
                this.pairsFromBranch[i][p] -= this.childs[i].dummyTaxaWeightsIndividual[j] * this.childs[i].dummyTaxaWeightsIndividual[j];
                this.pairsWithParent[i][p] -= this.childs[i].dummyTaxaWeightsIndividual[j] * this.parent.dummyTaxaWeightsIndividual[j];
            }

            this.pairsFromBranch[i][0] /= 2;
            this.pairsFromBranch[i][1] /= 2;

        }
        for(int p = 0; p < 2; ++p){
            this.pairsLR[p] = this.childs[0].totalTaxaCounts[p] * this.childs[1].totalTaxaCounts[p];
        }
        for(int j = 0; j < this.nDummyTaxa; ++j){
            int partition = this.dummyTaxaPartition[j];
            this.pairsLR[partition] -= this.childs[0].dummyTaxaWeightsIndividual[j] * this.childs[1].dummyTaxaWeightsIndividual[j];
        }

    }
    
    @Override
    public double sat(){
        double sat = 0;

        sat += pairsFromBranch[0][0] * pairsFromBranch[1][1];
        sat += pairsFromBranch[0][1] * pairsFromBranch[1][0];

        
        
        sat += pairsFromBranch[0][0] * pairsWithParent[1][1];
        sat += pairsFromBranch[0][1] * pairsWithParent[1][0];
        sat += pairsFromBranch[1][0] * pairsWithParent[0][1];
        sat += pairsFromBranch[1][1] * pairsWithParent[0][0];

        return sat * this.nodeFreq ;

    }

    @Override
    public double vio(){

        double vio = 0;
        vio += (pairsLR[0] * pairsLR[1]);
        vio += pairsLR[0] * ( pairsWithParent[0][1] + pairsWithParent[1][1] );
        vio += pairsLR[1] * ( pairsWithParent[0][0] + pairsWithParent[1][0] );

        return vio * this.nodeFreq;
    }

    // @Override
    // public double score() {
    //     double score = 0;
    //     double[][] pairsWithParentBranch = new double[2][2];

    //     pairsWithParentBranch[0][0] = pairsWithParentCommon[0][0] + pairsWithParentUnique[0][0] + pairsWithParentCommonAndUnique[0][0];
    //     pairsWithParentBranch[0][1] = pairsWithParentCommon[0][1] + pairsWithParentUnique[0][1] + pairsWithParentCommonAndUnique[0][1];
    //     pairsWithParentBranch[1][0] = pairsWithParentCommon[1][0] + pairsWithParentUnique[1][0] + pairsWithParentCommonAndUnique[1][0];
    //     pairsWithParentBranch[1][1] = pairsWithParentCommon[1][1] + pairsWithParentUnique[1][1] + pairsWithParentCommonAndUnique[1][1];


    //     score += pairsFromBranch[0][0] * pairsFromBranch[1][1];
    //     score += pairsFromBranch[0][1] * pairsFromBranch[1][0];
        
    //     score += pairsFromBranch[0][0] * pairsWithParentBranch[1][1];
    //     score += pairsFromBranch[0][1] * pairsWithParentBranch[1][0];
    //     score += pairsFromBranch[1][0] * pairsWithParentBranch[0][1];
    //     score += pairsFromBranch[1][1] * pairsWithParentBranch[0][0];

    //     score -= pairsLR[0] * pairsLR[1];
    //     score -= pairsLR[0] * ( pairsWithParentBranch[0][1] + pairsWithParentBranch[1][1] );
    //     score -= pairsLR[1] * ( pairsWithParentBranch[0][0] + pairsWithParentBranch[1][0] );

    //     return score;
    // }

    // [branch index][common, disjoints][partition]
    // @Override
    // public RTGainReturnType gainRealTaxa(double originalScore) {
    //     RTGainReturnType gains = new RTGainReturnType();
    //     gains.commonGains = new double[2][2];
    //     gains.uniqueGains = new double[2][2];
    //     gains.uniqueParentGains = new double[2];

    //     for(int i = 0; i < 2; ++i){
    //         for(int p = 0; p < 2; ++p){
    //             if(this.common[i].realTaxaCounts[p] > 0){
    //                 this.transferCommon(i, p);
    //                 this.common[i].swapRealTaxa(p);
    //                 gains.commonGains[i][p] = (this.score() - originalScore);
    //                 this.transferCommon(i, 1-p);
    //                 this.common[i].swapRealTaxa(1-p);
    //             }

    //             if(this.uniques[i].realTaxaCounts[p] > 0){
    //                 this.transferUnique(i, p);
    //                 this.uniques[i].swapRealTaxa(p);
    //                 gains.uniqueGains[i][p] = (this.score() - originalScore);
    //                 this.transferUnique(i, 1-p);
    //                 this.uniques[i].swapRealTaxa(1-p);
    //             }

    //         }

    //         if(this.uniquesParent.realTaxaCounts[i] > 0){
    //             this.transferParentUnique(i);
    //             this.uniquesParent.swapRealTaxa(i);
    //             gains.uniqueParentGains[i] = (this.score() - originalScore);
    //             this.transferParentUnique(1 - i);
    //             this.uniquesParent.swapRealTaxa(1 - i);
    //         }
    //     }

    //     return gains;
    // }


    // @Override
    // public void transferCommon(int branchIndex, int currPartition){
    //     this.pairsFromBranch[branchIndex][currPartition] -= (
    //         this.common[branchIndex].totalTaxaCounts[currPartition] + this.uniques[branchIndex].totalTaxaCounts[currPartition] - 1
    //     );
    //     this.pairsFromBranch[branchIndex][1 - currPartition] += (
    //         this.common[branchIndex].totalTaxaCounts[1 - currPartition] + this.uniques[branchIndex].totalTaxaCounts[1 - currPartition]
    //     );
        

    //     // this.pairsWithParentBranch[branchIndex][currPartition] -= (
    //     //     this.common[0].totalTaxaCounts[currPartition] + this.common[1].totalTaxaCounts[currPartition] + this.uniquesParent.totalTaxaCounts[currPartition] - 1
    //     // );
        
    //     // this.pairsWithParentBranch[branchIndex][1 - currPartition] += (
    //     //     this.common[0].totalTaxaCounts[1 - currPartition] + this.common[1].totalTaxaCounts[1 - currPartition] + this.uniquesParent.totalTaxaCounts[1 - currPartition]
    //     // );
        
    //     // this.pairsWithParentBranch[branchIndex][currPartition] -= (
    //     //     this.common[branchIndex].totalTaxaCounts[currPartition] + this.uniques[branchIndex].totalTaxaCounts[currPartition] - 1
    //     // );
    //     // this.pairsWithParentBranch[branchIndex][1 - currPartition] += (
    //     //     this.common[branchIndex].totalTaxaCounts[1 - currPartition] + this.uniques[branchIndex].totalTaxaCounts[1 - currPartition]
    //     // );


    //     // this.pairsWithParentBranch[ 1 - branchIndex][currPartition] -= (
    //     //     this.common[ 1 - branchIndex].totalTaxaCounts[currPartition] + this.uniques[1 - branchIndex].totalTaxaCounts[currPartition]
    //     // );
    //     // this.pairsWithParentBranch[1 - branchIndex][1 - currPartition] += (
    //     //     this.common[1 - branchIndex].totalTaxaCounts[1 - currPartition] + this.uniques[1 - branchIndex].totalTaxaCounts[1 - currPartition]
    //     // );

    //     this.pairsWithParentCommon[branchIndex][currPartition] -= (
    //         this.common[branchIndex].totalTaxaCounts[currPartition] - 1
    //     );
    //     this.pairsWithParentCommon[branchIndex][1 - currPartition] += (
    //         this.common[branchIndex].totalTaxaCounts[1 - currPartition]
    //     );

    //     this.pairsWithParentCommonAndUnique[branchIndex][currPartition] -= (
    //         this.uniques[branchIndex].totalTaxaCounts[currPartition] + this.common[1 - branchIndex].totalTaxaCounts[currPartition] + this.uniquesParent.totalTaxaCounts[currPartition]
    //     );
        
    //     this.pairsWithParentCommonAndUnique[branchIndex][1 - currPartition] += (
    //         this.uniques[branchIndex].totalTaxaCounts[1 - currPartition] + this.common[1 - branchIndex].totalTaxaCounts[1 - currPartition] + this.uniquesParent.totalTaxaCounts[1 - currPartition]
    //     );

    //     this.pairsWithParentUnique[1-branchIndex][currPartition] -= (
    //         this.uniques[1-branchIndex].totalTaxaCounts[currPartition]
    //     );
    //     this.pairsWithParentUnique[1-branchIndex][1 - currPartition] += (
    //         this.uniques[1-branchIndex].totalTaxaCounts[1 - currPartition]
    //     );
    //     this.pairsWithParentCommonAndUnique[1-branchIndex][currPartition] -= (
    //         this.common[1-branchIndex].totalTaxaCounts[currPartition]
    //     );
    //     this.pairsWithParentCommonAndUnique[1-branchIndex][1 - currPartition] += (
    //         this.common[1-branchIndex].totalTaxaCounts[1 - currPartition]
    //     );


    //     this.pairsLR[currPartition] -= (
    //         this.common[1 - branchIndex].totalTaxaCounts[currPartition] + this.uniques[1 - branchIndex].totalTaxaCounts[currPartition]
    //     );
    //     this.pairsLR[1 - currPartition] += (
    //         this.common[1 - branchIndex].totalTaxaCounts[1 - currPartition] + this.uniques[1 - branchIndex].totalTaxaCounts[1 - currPartition]
    //     );
    // }

    // @Override
    // public void transferUnique(int branchIndex, int currPartition){
    //     this.pairsFromBranch[branchIndex][currPartition] -= (
    //         this.common[branchIndex].totalTaxaCounts[currPartition] + this.uniques[branchIndex].totalTaxaCounts[currPartition] - 1
    //     );
    //     this.pairsFromBranch[branchIndex][1 - currPartition] += (
    //         this.common[branchIndex].totalTaxaCounts[1 - currPartition] + this.uniques[branchIndex].totalTaxaCounts[1 - currPartition]
    //     );
    //     // this.pairsWithParentBranch[branchIndex][currPartition] -= (
    //     //     this.common[0].totalTaxaCounts[currPartition] + this.common[1].totalTaxaCounts[currPartition] + this.uniquesParent.totalTaxaCounts[currPartition]
    //     // );
    //     // this.pairsWithParentBranch[branchIndex][1 - currPartition] += (
    //     //     this.common[0].totalTaxaCounts[1 - currPartition] + this.common[1].totalTaxaCounts[1 - currPartition] + this.uniquesParent.totalTaxaCounts[1 - currPartition]
    //     // );

    //     this.pairsWithParentUnique[branchIndex][currPartition] -= (
    //         this.common[1-branchIndex].totalTaxaCounts[currPartition] + this.uniquesParent.totalTaxaCounts[currPartition]
    //     );
    //     this.pairsWithParentUnique[branchIndex][1 - currPartition] += (
    //         this.common[1-branchIndex].totalTaxaCounts[1 - currPartition] + this.uniquesParent.totalTaxaCounts[1 - currPartition]
    //     );

    //     this.pairsWithParentCommonAndUnique[branchIndex][currPartition] -= (
    //         this.common[branchIndex].totalTaxaCounts[currPartition]
    //     );
    //     this.pairsWithParentCommonAndUnique[branchIndex][1 - currPartition] += (
    //         this.common[branchIndex].totalTaxaCounts[1 - currPartition]
    //     );



    //     this.pairsLR[currPartition] -= (
    //         this.common[1 - branchIndex].totalTaxaCounts[currPartition] + this.uniques[1 - branchIndex].totalTaxaCounts[currPartition]
    //     );
    //     this.pairsLR[1 - currPartition] += (
    //         this.common[1 - branchIndex].totalTaxaCounts[1 - currPartition] + this.uniques[1 - branchIndex].totalTaxaCounts[1 - currPartition]
    //     );
    // }

    // @Override
    // public void transferParentUnique(int currPartition){

    //     for(int branchIndex = 0; branchIndex < 2; ++branchIndex){
    //         this.pairsWithParentUnique[branchIndex][currPartition] -= (
    //             this.uniques[branchIndex].totalTaxaCounts[currPartition]
    //         );
    //         this.pairsWithParentUnique[branchIndex][1 - currPartition] += (
    //             this.uniques[branchIndex].totalTaxaCounts[1 - currPartition]
    //         );

    //         this.pairsWithParentCommonAndUnique[branchIndex][currPartition] -=(
    //             this.common[branchIndex].totalTaxaCounts[currPartition]
    //         );
    //         this.pairsWithParentCommonAndUnique[branchIndex][1 - currPartition] += (
    //             this.common[branchIndex].totalTaxaCounts[1 - currPartition]
    //         );
    //     }

    //     // this.pairsWithParentBranch[0][currPartition] -= (
    //     //     this.common[0].totalTaxaCounts[currPartition] + this.uniques[0].totalTaxaCounts[currPartition]
    //     // );
    //     // this.pairsWithParentBranch[0][1 - currPartition] += (
    //     //     this.common[0].totalTaxaCounts[1 - currPartition] + this.uniques[0].totalTaxaCounts[1 - currPartition]
    //     // );
    //     // this.pairsWithParentBranch[1][currPartition] -= (
    //     //     this.common[1].totalTaxaCounts[currPartition] + this.uniques[1].totalTaxaCounts[currPartition]
    //     // );
    //     // this.pairsWithParentBranch[1][1 - currPartition] += (
    //     //     this.common[1].totalTaxaCounts[1 - currPartition] + this.uniques[1].totalTaxaCounts[1 - currPartition]
    //     // );
    // }


    // @Override
    // public void transferRealTaxon(int branchIndex, int currPartition) {
    //     // if(branchIndex < 2){
    //     //     this.pairsFromBranch[branchIndex][currPartition] -= this.branches[branchIndex].totalTaxaCounts[currPartition] - 1;
    //     //     this.pairsFromBranch[branchIndex][1 - currPartition] += this.branches[branchIndex].totalTaxaCounts[1 - currPartition];
    //     //     this.pairsABFromBranch[branchIndex] -= this.branches[branchIndex].totalTaxaCounts[1 - currPartition];
    //     //     this.pairsABFromBranch[branchIndex] += this.branches[branchIndex].totalTaxaCounts[currPartition] - 1;
            
    //     //     this.pairsWithParentBranch[branchIndex][currPartition] -= this.branches[2].totalTaxaCounts[currPartition];
    //     //     this.pairsWithParentBranch[branchIndex][1 - currPartition] += this.branches[2].totalTaxaCounts[1 - currPartition];
    //     // }
    //     // else{
    //     //     for(int i = 0; i < 2; ++i){
    //     //         this.pairsWithParentBranch[i][currPartition] -= this.branches[i].totalTaxaCounts[currPartition];
    //     //         this.pairsWithParentBranch[i][1 - currPartition] += this.branches[i].totalTaxaCounts[1 - currPartition];
    //     //     }
    //     // }



    //     // if(branchIndex < 2){
    //     //     this.pairsFromLeftRightBranch[currPartition] -= this.branches[1 - branchIndex].totalTaxaCounts[currPartition];
    //     //     this.pairsFromLeftRightBranch[1 - currPartition] += this.branches[1 - branchIndex].totalTaxaCounts[1 - currPartition];
    //     //     this.pairsABFromLeftRightBranch = (
    //     //         (this.branches[branchIndex].totalTaxaCounts[currPartition] - 1) * this.branches[1 - branchIndex].totalTaxaCounts[1 - currPartition] 
    //     //         + (this.branches[branchIndex].totalTaxaCounts[1 - currPartition] + 1 ) * (this.branches[1 - branchIndex].totalTaxaCounts[currPartition])
    //     //     );
            
    //     // }
    // }

    // @Override
    // public void transferDummyTaxon(int dummyIndex, int currPartition) {
    //     double w0 = this.common[0].dummyTaxaWeightsIndividual[dummyIndex] + this.uniques[0].dummyTaxaWeightsIndividual[dummyIndex];
    //     double w1 = this.common[1].dummyTaxaWeightsIndividual[dummyIndex] + this.uniques[1].dummyTaxaWeightsIndividual[dummyIndex];
    //     double wp = this.uniquesParent.dummyTaxaWeightsIndividual[dummyIndex] + this.common[0].dummyTaxaWeightsIndividual[dummyIndex] + this.common[1].dummyTaxaWeightsIndividual[dummyIndex];

    //     this.pairsFromBranch[0][currPartition] -= (
    //         this.common[0].totalTaxaCounts[currPartition] + this.uniques[0].totalTaxaCounts[currPartition] - w0
    //     ) * w0;
    //     this.pairsFromBranch[0][1 - currPartition] += (
    //         this.common[0].totalTaxaCounts[1 - currPartition] + this.uniques[0].totalTaxaCounts[1 - currPartition]
    //     ) * w0;

    //     this.pairsFromBranch[1][currPartition] -= (
    //         this.common[1].totalTaxaCounts[currPartition] + this.uniques[1].totalTaxaCounts[currPartition] - w1
    //     ) * w1;
    //     this.pairsFromBranch[1][1 - currPartition] += (
    //         this.common[1].totalTaxaCounts[1 - currPartition] + this.uniques[1].totalTaxaCounts[1 - currPartition]
    //     ) * w1;

    //     this.pairsLR[currPartition] -= (
    //         w0 * (this.common[1].totalTaxaCounts[currPartition] + this.uniques[1].totalTaxaCounts[currPartition] - w1) + 
    //         w1 * (this.common[0].totalTaxaCounts[currPartition] + this.uniques[0].totalTaxaCounts[currPartition] - w0)
    //     );
    //     this.pairsLR[1 - currPartition] += (
    //         this.common[0].totalTaxaCounts[1 - currPartition] + this.uniques[0].totalTaxaCounts[1 - currPartition]
    //     ) * w1 + (
    //         this.common[1].totalTaxaCounts[1 - currPartition] + this.uniques[1].totalTaxaCounts[1 - currPartition]
    //     ) * w0;

    //     for(int branchIndex = 0; branchIndex < 2; ++branchIndex){
    //         double wc = this.common[branchIndex].dummyTaxaWeightsIndividual[dummyIndex];
    //         this.pairsWithParentCommon[branchIndex][currPartition] -= (
    //             wc * (this.common[branchIndex].totalTaxaCounts[currPartition] - wc)
    //         );
    //         this.pairsWithParentCommon[branchIndex][1 - currPartition] += (
    //             this.common[branchIndex].totalTaxaCounts[1 - currPartition]
    //         ) * wc;
    //         double wuc = this.uniques[branchIndex].dummyTaxaWeightsIndividual[dummyIndex];
    //         double wpc = this.uniquesParent.dummyTaxaWeightsIndividual[dummyIndex] + this.common[1 - branchIndex].dummyTaxaWeightsIndividual[dummyIndex];
            
    //         this.pairsWithParentUnique[branchIndex][currPartition] -= (
    //             wuc * (this.common[1-branchIndex].totalTaxaCounts[currPartition] + this.uniquesParent.totalTaxaCounts[currPartition] - wpc)+
    //             wpc * (this.uniques[branchIndex].totalTaxaCounts[currPartition] - wuc)
    //         );
    //         this.pairsWithParentUnique[branchIndex][1 - currPartition] += (
    //             this.common[1-branchIndex].totalTaxaCounts[1 - currPartition] + this.uniquesParent.totalTaxaCounts[1 - currPartition]
    //         ) * wuc + (
    //             this.uniques[branchIndex].totalTaxaCounts[1 - currPartition]
    //         ) * wpc;


    //         this.pairsWithParentCommonAndUnique[branchIndex][currPartition] -= (
    //             wc * (this.uniques[branchIndex].totalTaxaCounts[currPartition] + this.common[1 - branchIndex].totalTaxaCounts[currPartition] + this.uniquesParent.totalTaxaCounts[currPartition] - wpc - wuc) +
    //             (wpc + wuc) * (this.common[branchIndex].totalTaxaCounts[currPartition] - wc)
    //         );
    //         this.pairsWithParentCommonAndUnique[branchIndex][1 - currPartition] += (
    //             this.uniques[branchIndex].totalTaxaCounts[1 - currPartition] + this.common[1 - branchIndex].totalTaxaCounts[1 - currPartition] + this.uniquesParent.totalTaxaCounts[1 - currPartition]
    //         ) * wc + (
    //             this.common[branchIndex].totalTaxaCounts[1 - currPartition]
    //         ) * (wpc + wuc);
    //     }

    //     // this.pairsWithParentBranch[0][currPartition] -= (
    //     //     w0 * (this.common[0].totalTaxaCounts[currPartition] + this.common[0].totalTaxaCounts[currPartition] + this.uniquesParent.totalTaxaCounts[currPartition] - wp) + 
    //     //     wp * (this.common[0].totalTaxaCounts[currPartition] + this.uniques[0].totalTaxaCounts[currPartition] - w0)
    //     // );
    //     // this.pairsWithParentBranch[0][1 - currPartition] += (
    //     //     this.common[0].totalTaxaCounts[1 - currPartition] + this.uniques[0].totalTaxaCounts[1 - currPartition] + this.uniquesParent.totalTaxaCounts[1 - currPartition]
    //     // ) * w0 + (
    //     //     this.common[0].totalTaxaCounts[1 - currPartition] + this.uniquesParent.totalTaxaCounts[1 - currPartition]
    //     // ) * wp;

    //     // this.pairsWithParentBranch[1][currPartition] -= (
    //     //     w1 * (this.common[1].totalTaxaCounts[currPartition] + this.common[1].totalTaxaCounts[currPartition] + this.uniquesParent.totalTaxaCounts[currPartition] - wp) + 
    //     //     wp * (this.common[1].totalTaxaCounts[currPartition] + this.uniques[1].totalTaxaCounts[currPartition] - w1)
    //     // );
    //     // this.pairsWithParentBranch[1][1 - currPartition] += (
    //     //     this.common[1].totalTaxaCounts[1 - currPartition] + this.uniques[1].totalTaxaCounts[1 - currPartition] + this.uniquesParent.totalTaxaCounts[1 - currPartition]
    //     // ) * w1 + (
    //     //     this.common[1].totalTaxaCounts[1 - currPartition] + this.uniquesParent.totalTaxaCounts[1 - currPartition]
    //     // ) * wp;


    //     // double wp = this.branches[2].dummyTaxaWeightsIndividual[dummyIndex];
    //     // for(int i = 0; i < this.branches.length - 1; ++i){
    //     //     double wi = this.branches[i].dummyTaxaWeightsIndividual[dummyIndex];
    //     //     this.pairsFromBranch[i][currPartition] -= (this.branches[i].totalTaxaCounts[currPartition] - wi) * wi;
    //     //     this.pairsFromBranch[i][1 - currPartition] += (this.branches[i].totalTaxaCounts[1 - currPartition]) * wi;
    //     //     this.pairsABFromBranch[i] -= wi * this.branches[i].totalTaxaCounts[1 - currPartition];
    //     //     this.pairsABFromBranch[i] += wi * (this.branches[i].totalTaxaCounts[currPartition] - wi);

    //     //     this.pairsWithParentBranch[i][currPartition] -= (wi * (this.branches[2].totalTaxaCounts[currPartition] - wp) + wp * (this.branches[i].totalTaxaCounts[currPartition] - wi));
    //     //     this.pairsWithParentBranch[i][1 - currPartition] += this.branches[2].totalTaxaCounts[1 - currPartition] * wi + wp * this.branches[i].totalTaxaCounts[1 - currPartition];
    //     // }
    //     // double wl = this.branches[0].dummyTaxaWeightsIndividual[dummyIndex];
    //     // double wr = this.branches[1].dummyTaxaWeightsIndividual[dummyIndex];

    //     // this.pairsFromLeftRightBranch[currPartition] -= (wl * (this.branches[1].totalTaxaCounts[currPartition] - wr) + wr * (this.branches[0].totalTaxaCounts[currPartition] - wl));
    //     // this.pairsFromLeftRightBranch[1 - currPartition] += (wl * this.branches[1].totalTaxaCounts[1 - currPartition] + wr * this.branches[0].totalTaxaCounts[1 - currPartition]);
        
    //     // this.pairsABFromLeftRightBranch = (
    //     //     this.branches[0].totalTaxaCounts[0] * this.branches[1].totalTaxaCounts[1] +
    //     //     this.branches[0].totalTaxaCounts[1] * this.branches[1].totalTaxaCounts[0]
    //     // );

    // }

    // @Override
    // public void gainDummyTaxa(double originalScore, double[] dummyTaxaGains) {
    //     for(int i = 0; i < this.nDummyTaxa; ++i){
    //         int currPartition = this.dummyTaxaPartition[i];
    //         this.transferDummyTaxon(i, currPartition);
    //         for(int j = 0; j < 2; ++j){
    //             this.common[j].swapDummyTaxon(i, currPartition);
    //             this.uniques[j].swapDummyTaxon(i, currPartition);
    //         }
    //         this.uniquesParent.swapDummyTaxon(i, currPartition);
    //         dummyTaxaGains[i] += (this.score() - originalScore);
            
    //         this.transferDummyTaxon(i, 1 - currPartition);
    //         for(int j = 0; j < 2; ++j){
    //             this.common[j].swapDummyTaxon(i, 1 - currPartition);
    //             this.uniques[j].swapDummyTaxon(i, 1 - currPartition);
    //         }
    //         this.uniquesParent.swapDummyTaxon(i, 1 - currPartition);
            
            
    //     }
    // }

    // @Override
    // public void batchTransferRealTaxon(int branchIndex, int netTranser) {
    //     // TODO Auto-generated method stub
    //     throw new UnsupportedOperationException("Unimplemented method 'batchTransferRealTaxon'");
    // }

    public void transferRealTaxon(int branchIndex, int currPartition){
        if(branchIndex == 2){
            for(int i = 0; i < 2; ++i){
                this.pairsWithParent[i][currPartition] -= this.childs[i].totalTaxaCounts[currPartition];
                this.pairsWithParent[i][1 - currPartition] += this.childs[i].totalTaxaCounts[1 - currPartition];
            }
        }
        else{
            this.pairsFromBranch[branchIndex][currPartition] -= this.childs[branchIndex].totalTaxaCounts[currPartition] - 1;
            this.pairsFromBranch[branchIndex][1 - currPartition] += this.childs[branchIndex].totalTaxaCounts[1 - currPartition];

            this.pairsWithParent[branchIndex][currPartition] -= this.parent.totalTaxaCounts[currPartition];
            this.pairsWithParent[branchIndex][1 - currPartition] += this.parent.totalTaxaCounts[1 - currPartition];

            this.pairsLR[currPartition] -= this.childs[1 - branchIndex].totalTaxaCounts[currPartition];
            this.pairsLR[1 - currPartition] += this.childs[1 - branchIndex].totalTaxaCounts[1 - currPartition];
        }
    }


    @Override
    public void transferDummyTaxon(int dummyIndex, int currPartition){
        double wo = this.childs[0].dummyTaxaWeightsIndividual[dummyIndex];
        double w1 = this.childs[1].dummyTaxaWeightsIndividual[dummyIndex];
        double wp = this.parent.dummyTaxaWeightsIndividual[dummyIndex];

        this.pairsFromBranch[0][currPartition] -= (
            this.childs[0].totalTaxaCounts[currPartition] - wo
        ) * wo;
        this.pairsFromBranch[0][1 - currPartition] += (
            this.childs[0].totalTaxaCounts[1 - currPartition]
        ) * wo;

        this.pairsFromBranch[1][currPartition] -= (
            this.childs[1].totalTaxaCounts[currPartition] - w1
        ) * w1;
        this.pairsFromBranch[1][1 - currPartition] += (
            this.childs[1].totalTaxaCounts[1 - currPartition]
        ) * w1;

        this.pairsLR[currPartition] -= (
            wo * (this.childs[1].totalTaxaCounts[currPartition] - w1) + 
            w1 * (this.childs[0].totalTaxaCounts[currPartition] - wo)
        );
        this.pairsLR[1 - currPartition] += (
            this.childs[0].totalTaxaCounts[1 - currPartition] * w1 + 
            this.childs[1].totalTaxaCounts[1 - currPartition] * wo
        );

        this.pairsWithParent[0][currPartition] -= (
            wo * (this.parent.totalTaxaCounts[currPartition] - wp) + 
            wp * (this.childs[0].totalTaxaCounts[currPartition] - wo)
        );
        this.pairsWithParent[0][1 - currPartition] += (
            this.parent.totalTaxaCounts[1 - currPartition] * wo + 
            this.childs[0].totalTaxaCounts[1 - currPartition] * wp
        );

        this.pairsWithParent[1][currPartition] -= (
            w1 * (this.parent.totalTaxaCounts[currPartition] - wp) + 
            wp * (this.childs[1].totalTaxaCounts[currPartition] - w1)
        );
        this.pairsWithParent[1][1 - currPartition] += (
            this.parent.totalTaxaCounts[1 - currPartition] * w1 + 
            this.childs[1].totalTaxaCounts[1 - currPartition] * wp
        );
    }
    
    
    @Override
    public RTGainReturnType gainSatRealTaxa(double currSat){
        RTGainReturnType gains = new RTGainReturnType();
        gains.childGains = new double[2][2];
        gains.parentGain = new double[2];

        for(int i = 0; i < 2; ++i){
            for(int p = 0; p < 2; ++p){
                if(this.childs[i].realTaxaCounts[p] > 0){
                    this.transferRealTaxon(i, p);
                    this.childs[i].swapRealTaxa(p);
                    gains.childGains[i][p] = (this.sat() - currSat);
                    this.transferRealTaxon(i, 1-p);
                    this.childs[i].swapRealTaxa(1-p);
                }

            }

            if(this.parent.realTaxaCounts[i] > 0){
                this.transferRealTaxon(2, i);
                this.parent.swapRealTaxa(i);
                gains.parentGain[i] = (this.sat() - currSat);
                this.transferRealTaxon(2, 1 - i);
                this.parent.swapRealTaxa(1 - i);
            }
        }

        return gains;
    }
    
    @Override
    public RTGainReturnType gainVioRealTaxa(double currVio){
        RTGainReturnType gains = new RTGainReturnType();
        gains.childGains = new double[2][2];
        gains.parentGain = new double[2];

        for(int i = 0; i < 2; ++i){
            for(int p = 0; p < 2; ++p){
                if(this.childs[i].realTaxaCounts[p] > 0){
                    this.transferRealTaxon(i, p);
                    this.childs[i].swapRealTaxa(p);
                    gains.childGains[i][p] = (this.vio() - currVio);
                    this.transferRealTaxon(i, 1-p);
                    this.childs[i].swapRealTaxa(1-p);
                }

            }

            if(this.parent.realTaxaCounts[i] > 0){
                this.transferRealTaxon(2, i);
                this.parent.swapRealTaxa(i);
                gains.parentGain[i] = (this.vio() - currVio);
                this.transferRealTaxon(2, 1 - i);
                this.parent.swapRealTaxa(1 - i);
            }
        }

        return gains;
    }


    @Override
    public void gainSatDummyTaxa(double[] dummyTaxaGains, double currSat) {
        for(int i = 0; i < this.nDummyTaxa; ++i){
            int currPartition = this.dummyTaxaPartition[i];

            this.transferDummyTaxon(i, currPartition);

            this.childs[0].swapDummyTaxon(i, currPartition);
            this.childs[1].swapDummyTaxon(i, currPartition);
            this.parent.swapDummyTaxon(i, currPartition);

            dummyTaxaGains[i] += (this.sat() - currSat);
            
            this.transferDummyTaxon(i, 1 - currPartition);

            this.childs[0].swapDummyTaxon(i, 1 - currPartition);
            this.childs[1].swapDummyTaxon(i, 1 - currPartition);
            this.parent.swapDummyTaxon(i, 1 - currPartition);
        }
    }
    @Override
    public void gainVioDummyTaxa(double[] dummyTaxaGains, double currVio) {
        for(int i = 0; i < this.nDummyTaxa; ++i){
            int currPartition = this.dummyTaxaPartition[i];
            this.transferDummyTaxon(i, currPartition);
            this.childs[0].swapDummyTaxon(i, currPartition);
            this.childs[1].swapDummyTaxon(i, currPartition);
            this.parent.swapDummyTaxon(i, currPartition);

            dummyTaxaGains[i] += (this.vio() - currVio);
            
            this.transferDummyTaxon(i, 1 - currPartition);

            this.childs[0].swapDummyTaxon(i, 1 - currPartition);
            this.childs[1].swapDummyTaxon(i, 1 - currPartition);
            this.parent.swapDummyTaxon(i, 1 - currPartition);
        }
    }
}
