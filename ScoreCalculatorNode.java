package wqfm.dsGT;

import java.util.HashSet;
import java.util.Set;

public class ScoreCalculatorNode {

    Branch[] branches;
    int[][] subs;
    int nDummyTaxa;
    int[] dummyTaxaToPartitionMap;

    ScoreCalculatorNode(Branch[] b, int[] dummyTaxaToPartitionMap) {
        this.dummyTaxaToPartitionMap = dummyTaxaToPartitionMap;
        this.branches = b;
        subs = new int[3][];
        this.nDummyTaxa = b[0].dummyTaxaCountsIndividual.length;
        for(int i = 0; i < 3; ++i){
            subs[i] = new int[3];
            subs[i][0] = 0;
            subs[i][1] = 0;
            subs[i][2] = 0;
            for(int j = 0; j < this.nDummyTaxa; ++j){
                int pIndex = this.dummyTaxaToPartitionMap[j];
                subs[i][pIndex] += b[i].dummyTaxaCountsIndividual[j] * b[(i+1) % 3].dummyTaxaCountsIndividual[j];
                subs[i][2] += (b[i].dummyTaxaCountsIndividual[j] * (b[i].dummyTaxaCountsIndividual[j] - 1) ) / 2; 
            }
        }

    }

    int[] scoreOf2Branch(int i) {
        int j = (i + 1) % 3;
        int k = (i + 2) % 3;
        // var br1 = branches[i];
        // var br2 = branches[j];
        // var br3 = branches[k];

        int[][] s = new int[3][2];
        for(int l = 0; l < 3; ++l){
            int cIndex = (i + l) % 3;
            for(int m = 0; m < 2; ++m){
                s[cIndex][m] = branches[cIndex].realTaxaCountsTotal[m] + branches[cIndex].dummyTaxaCountsTotal[m];
            }
        }

        int[] csubs = new int[2];
        csubs[0] = subs[i][0];
        csubs[1] = subs[k][1];

        int[] score = new int[2];
        score[0] = satisfiedEqn(s[i][0], s[j][0], s[k][1], csubs);
        csubs[0] = subs[k][0];
        csubs[1] = subs[j][1];
        score[1] = violatedEqn(s[i][0], s[j][1], s[k][0], s[k][1], csubs);
        csubs[0] = subs[j][0];
        csubs[1] = subs[k][1];
        score[1] += violatedEqn(s[i][1], s[j][0], s[k][0], s[k][1], csubs);

        return score;

    }


    int[] score() {
        int[] res = new int[2];

        for (int i = 0; i < 3; ++i) {
            Utility.addIntArrToFirst(res, scoreOf2Branch(i));
        }
        return res;
    }

    Set<Integer> copySet(Set<Integer> st) {
        Set<Integer> a = new HashSet<>();
        a.addAll(st);
        return a;
    }

    private Set<Integer> getIntersection(Set<Integer> a, Set<Integer> b) {
        var x = copySet(a);
        x.retainAll(b);
        return x;
    }

    private int satisfiedEqn(int a1, int a2, int b3, int[] subs) {
        return (a1 * a2 - subs[0]) * (( b3 * (b3 - 1)) / 2 - subs[1] );
    }

    private int violatedEqn(int a1, int b2, int a3, int b3,int[] subs) {
        return (a1 * a3 - subs[0]) * (b2 * b3 - subs[1]);
    }
}

















    // void gainOf1Branch(int i, pair originalScore) {
    //     int j = (i + 1) % 3;
    //     int k = (i + 2) % 3;
    //     this.aToBGain = new pair(0, 0);
    //     this.bToAGain = new pair(0, 0);

    //     // A to B
    //     if (branches[i].a_t > 0) {
    //         branches[i].a_t--;
    //         branches[i].b_t++;
    //         boolean c1, c2, c3, c4;
    //         this.aToBGain = score().sub(originalScore);
    //         for (var x : branches[i].dummyA) {
    //             if (this.calculated.contains(x))
    //                 continue;
    //             c1 = commonA.get(i).contains(x);
    //             c2 = commonA.get(k).contains(x);
    //             c3 = branches[j].dummyA.contains(x);
    //             c4 = branches[k].dummyA.contains(x);
    //             if (c1) {
    //                 this.commonSzA[i]--;
    //             }
    //             if (c2) {
    //                 this.commonSzA[k]--;
    //             }
    //             if (c3) {
    //                 branches[j].b_t++;
    //                 this.commonSzB[i]++;
    //             }
    //             if (c4) {
    //                 branches[k].b_t++;
    //                 this.commonSzB[k]++;
    //             }

    //             var aToBScoreDummy = score();
    //             var gain = aToBScoreDummy.sub(originalScore);
    //             this.dummyTaxaGains[x] = this.dummyTaxaGains[x].add(gain);
    //             this.calculated.add(x);

    //             if (c1) {
    //                 this.commonSzA[i]++;
    //             }
    //             if (c2) {
    //                 this.commonSzA[k]++;
    //             }
    //             if (c3) {
    //                 branches[j].b_t--;
    //                 this.commonSzB[i]--;
    //             }
    //             if (c4) {
    //                 branches[k].b_t--;
    //                 this.commonSzB[k]--;
    //             }
    //         }

    //         branches[i].a_t++;
    //         branches[i].b_t--;
    //     }
    //     // B to A
    //     if (branches[i].b_t > 0) {
    //         branches[i].b_t--;
    //         branches[i].a_t++;
    //         boolean c1, c2, c3, c4;
    //         this.bToAGain = score().sub(originalScore);
    //         for (var x : branches[i].dummyB) {
    //             if (this.calculated.contains(x))
    //                 continue;
    //             c1 = commonB.get(i).contains(x);
    //             c2 = commonB.get(k).contains(x);
    //             c3 = branches[j].dummyB.contains(x);
    //             c4 = branches[k].dummyB.contains(x);
    //             if (c1) {
    //                 this.commonSzB[i]--;
    //             }
    //             if (c2) {
    //                 this.commonSzB[k]--;
    //             }
    //             if (c3) {
    //                 branches[j].a_t++;
    //                 this.commonSzA[i]++;
    //             }
    //             if (c4) {
    //                 branches[k].a_t++;
    //                 this.commonSzA[k]++;
    //             }

    //             var bToAScoreDummy = score();
    //             var gain = bToAScoreDummy.sub(originalScore);
    //             this.dummyTaxaGains[x] = this.dummyTaxaGains[x].add(gain);
    //             this.calculated.add(x);

    //             if (c1) {
    //                 this.commonSzB[i]++;
    //             }
    //             if (c2) {
    //                 this.commonSzB[k]++;
    //             }
    //             if (c3) {
    //                 branches[j].a_t--;
    //                 this.commonSzA[i]--;
    //             }
    //             if (c4) {
    //                 branches[k].a_t--;
    //                 this.commonSzA[k]--;
    //             }
    //         }

    //         branches[i].b_t++;
    //         branches[i].a_t--;
    //     }

    // }

    // pair scoreAndGain() {
    //     aToBGains = new pair[3];
    //     bToAGains = new pair[3];

    //     currScore = score();

    //     for (int i = 0; i < 3; ++i) {
    //         this.gainOf1Branch(i, currScore);
    //         aToBGains[i] = aToBGain;
    //         bToAGains[i] = bToAGain;
    //     }

    //     return currScore;
    // }
