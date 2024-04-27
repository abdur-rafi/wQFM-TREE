package src.DSPerLevel;


import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Queue;
import java.util.Set;

import src.Config;
import src.Utility;
import src.InitialPartition.IMakePartition;
import src.PreProcessing.Data;
import src.PreProcessing.DataContainer;
import src.PreProcessing.InternalNode;
import src.PreProcessing.Component;
import src.PreProcessing.Component.InternalNodeWithIndex;
import src.ScoreCalculator.NumSatCalculatorBinaryNodeDC;
import src.ScoreCalculator.NumSatCalculatorNodeEDC;
import src.ScoreCalculator.NumSatSQ;
import src.ScoreCalculator.NumSatSQBin;
import src.Taxon.DummyTaxon;
import src.Taxon.RealTaxon;
import src.Tree.Branch;

public class BookKeepingPerLevelDC {

    public DataContainer dc;
    public TaxaPerLevelWithPartition taxaPerLevel;
    BookKeepingPerTreeDC[] bookKeepingPerTreeDCs;

    public BookKeepingPerLevelDC(DataContainer dc, TaxaPerLevelWithPartition taxaPerLevelWithPartition){
        this.dc = dc;
        this.taxaPerLevel = taxaPerLevelWithPartition;
        if(this.taxaPerLevel.smallestUnit)
            return;

        this.initialBookKeeping();
        this.bookKeepingPerTreeDCs = new BookKeepingPerTreeDC[dc.realTaxaInTrees.length];
        for(int i = 0; i < dc.realTaxaInTrees.length; ++i){
            this.bookKeepingPerTreeDCs[i] = new BookKeepingPerTreeDC(dc.realTaxaInTrees[i], this.taxaPerLevel);
        }

    }

    public void initialBookKeeping(){

        for(int i = 0; i < this.dc.realTaxaComponents.length; ++i){
            Component p = this.dc.realTaxaComponents[i];
            p.data = new Data();
            p.data.branch = new Branch(this.taxaPerLevel.dummyTaxonCount);

            if(this.taxaPerLevel.isInDummyTaxa(i)){
                int dtid = this.taxaPerLevel.inWhichDummyTaxa(i);
                int partition = this.taxaPerLevel.inWhichPartitionDummyTaxonByIndex(dtid);
                p.data.branch.dummyTaxaWeightsIndividual[dtid] = this.taxaPerLevel.getWeight(i);
                p.data.branch.totalTaxaCounts[partition] += this.taxaPerLevel.getWeight(i);

            }
            else{
                int partition = this.taxaPerLevel.inWhichPartition(i);
                p.data.branch.realTaxaCounts[partition] = 1;
                p.data.branch.totalTaxaCounts[partition] = 1;
            }

        }
        this.dc.sentinel.data = new Data();
        this.dc.sentinel.data.branch = new Branch(this.taxaPerLevel.dummyTaxonCount);

        int sz = this.dc.topSortedComponents.size();
        for(int i = sz - 1; i >  -1; --i){
            Component p = this.dc.topSortedComponents.get(i);
            if(p.isLeaf){
                continue;
            }
            else{
                p.data = new Data();
                p.data.branch = new Branch(this.taxaPerLevel.dummyTaxonCount);
                for(Component child : p.children){
                    p.data.branch.addToSelf(child.data.branch);
                }
            }
        }

        for(InternalNode p : this.dc.internalNodes){
            Branch[] comm = new Branch[p.childCompsCommon.length];
            Branch[] uniq = new Branch[p.childCompsUniques.length];
            for(int i = 0; i < p.childCompsCommon.length; ++i){
                comm[i] = p.childCompsCommon[i].data.branch;
            }
            for(int i = 0; i < p.childCompsUniques.length; ++i){
                uniq[i] = p.childCompsUniques[i].data.branch;
            }

            // Branch[] b = new Branch[p.partitionNodes.length];
            // for(int i = 0; i < p.partitionNodes.length; ++i){
            //     b[i] = p.partitionNodes[i].data.branch;
            // }
            if(p.childCompsCommon.length > 2){
                // p.scoreCalculator = new NumSatCalculatorNodeEDC(b,this.taxaPerLevel.dummyTaxonPartition);
                System.out.println("======================= polytomy ============================");
                System.exit(-1);
            }
            else{
                // p.scoreCalculator = new NumSatCalculatorBinaryNodeDC(b, this.taxaPerLevel.dummyTaxonPartition);
                // p.scoreCalculator = new NumSatSQBin(b, this.taxaPerLevel.dummyTaxonPartition);
                p.scoreCalculator = new NumSatSQBin(comm, uniq, p.parentUniques.data.branch, this.taxaPerLevel.dummyTaxonPartition);
            }
        }
    }


    public double calculateScore(){
        double score = 0;

        double sat = 0;
        double vio = 0;
        
        for(InternalNode p : this.dc.internalNodes){
            // score += p.scoreCalculator.score() * p.count;
            sat += p.scoreCalculator.sat() * p.count;
            vio += p.scoreCalculator.vio() * p.count;
        }

        return sat - vio;

        // return score;



    }

    public double calculateScoreAndGains(double[][] realTaxaGains, double[] dummyTaxaGains){
        // double totalScore = 0;
        
        double[] dtSat = new double[dummyTaxaGains.length];
        double[] dtVio = new double[dummyTaxaGains.length];

        double[][] rtSat = new double[realTaxaGains.length][2];
        double[][] rtVio = new double[realTaxaGains.length][2];

        for(Component p : this.dc.topSortedComponents){
            p.gainsForSubTreeSat = new double[2];
            p.gainsForSubTreeVio = new double[2];
        }

        this.dc.sentinel.gainsForSubTreeSat = new double[2];
        this.dc.sentinel.gainsForSubTreeVio = new double[2];

        double sat = 0;
        double vio = 0;

        for(InternalNode p : this.dc.internalNodes){

            sat += p.scoreCalculator.score() * p.count;
            vio += p.scoreCalculator.vio() * p.count;

            NumSatSQ.RTGainReturnType satGain = p.scoreCalculator.gainSatRealTaxa(p.count);
            NumSatSQ.RTGainReturnType vioGain = p.scoreCalculator.gainVioRealTaxa(p.count);

            
            p.scoreCalculator.gainSatDummyTaxa(dtSat);
            p.scoreCalculator.gainVioDummyTaxa(dtVio);


            for(int i = 0; i < p.childCompsCommon.length; ++i){
                // Utility.addArrayToFirst(p.childCompsCommon[i].gainsForSubTree, gains.commonGains[i]);
                // Utility.addArrayToFirst(p.childCompsUniques[i].gainsForSubTree, gains.uniqueGains[i]);

                Utility.addArrayToFirst(p.childCompsCommon[i].gainsForSubTreeSat, satGain.commonGains[i]);
                Utility.addArrayToFirst(p.childCompsCommon[i].gainsForSubTreeVio, vioGain.commonGains[i]);
                Utility.addArrayToFirst(p.childCompsUniques[i].gainsForSubTreeSat, satGain.uniqueGains[i]);
                Utility.addArrayToFirst(p.childCompsUniques[i].gainsForSubTreeVio, vioGain.uniqueGains[i]);
                
            }
            Utility.addArrayToFirst(p.parentUniques.gainsForSubTreeSat, satGain.uniqueParentGains);
            Utility.addArrayToFirst(p.parentUniques.gainsForSubTreeVio, vioGain.uniqueParentGains);

            // for(int i = 0; i < p.partitionNodes.length - 1; ++i){
            //     for(int j = 0; j < 2; ++j){
            //         if(p.gainChildNodes[i][j] != null){
            //             Utility.addArrayToFirst(p.gainChildNodes[i][j].gainsForSubTree, branchGainsForRealTaxa[i][j]);
            //         }
            //     }
            //     // Utility.addArrayToFirst(p.partitionNodes[i].gainsForSubTree, branchGainsForRealTaxa[i]);
            // }
            // int pi = p.partitionNodes.length - 1;
            // if(p.gainParentNode != null){
            //     Utility.addArrayToFirst(p.gainParentNode.gainsForSubTree, branchGainsForRealTaxa[pi][1]);
            // }
        }

        for(Component p : this.dc.topSortedComponents){
            for(Component childs : p.children){
                Utility.addArrayToFirst(childs.gainsForSubTreeSat, p.gainsForSubTreeSat);
                Utility.addArrayToFirst(childs.gainsForSubTreeVio, p.gainsForSubTreeVio);
            }
        }


        for(int i = 0; i < this.taxaPerLevel.realTaxonCount; ++i){
            RealTaxon rt = this.taxaPerLevel.realTaxa[i];
            Utility.addArrayToFirst(rtSat[i], this.dc.realTaxaComponents[rt.id].gainsForSubTreeSat);
            Utility.addArrayToFirst(rtVio[i], this.dc.realTaxaComponents[rt.id].gainsForSubTreeVio);
            realTaxaGains[i] = new double[2];
            for(int j = 0; j < 2; ++j){
                realTaxaGains[i][j] = rtSat[i][j] - rtVio[i][j];
            }
        }

        for(int i = 0; i < dummyTaxaGains.length; ++i){
            dummyTaxaGains[i] = dtSat[i] - dtVio[i];
        }

        return sat - vio;
    }

    // public void swapRealTaxon3(int index){
    //     int partition = this.taxaPerLevel.inWhichPartitionRealTaxonByIndex(index);
    //     this.taxaPerLevel.swapPartitionRealTaxon(index);
    //     int rtId = this.taxaPerLevel.realTaxa[index].id;

    //     for(BookKeepingPerTreeDC bkpt : this.bookKeepingPerTreeDCs){
    //         bkpt.swapRealTaxon(rtId, partition);
    //     }

        

    //     for(InternalNode p : this.dc.internalNodes){
    //         boolean f = false;
    //         for(int i = 0; i < p.partitionNodes.length; ++i){
    //             if(this.dc.componentGraph.realTaxaInComponent.get(p.partitionNodes[i])[rtId]){
    //                 p.scoreCalculator.swapRealTaxon(i, partition);
    //                 if(f){
    //                     System.out.println("----------------------------");
    //                     System.exit(-1);
    //                 }
    //                 f = true;
    //             }
    //         }
    //         // p.scoreCalculator.swapRealTaxon(index, partition);
    //     }

    //     for(Component p : this.dc.componentGraph.components){
    //         boolean[] b = this.dc.componentGraph.realTaxaInComponent.get(p);
    //         if(b[rtId]){
    //             p.data.branch.swapRealTaxa(partition);
    //         }
    //     }
    // }


    // public void swapRealTaxon2(int index){
    //     int partition = this.taxaPerLevel.inWhichPartitionRealTaxonByIndex(index);
    //     this.taxaPerLevel.swapPartitionRealTaxon(index);
    //     int rtId = this.taxaPerLevel.realTaxa[index].id;

    //     for(BookKeepingPerTreeDC bkpt : this.bookKeepingPerTreeDCs){
    //         bkpt.swapRealTaxon(rtId, partition);
    //     }

    //     for(Component p : this.dc.topSortedForBranch){
    //         boolean[] b = this.dc.componentGraph.realTaxaInComponent.get(p);
    //         if(b[rtId]){
    //             for(InternalNodeWithIndex pbt : p.partOfInternalNodes){
    //                 if(pbt.internalNode.partitionNodes[pbt.index].data.branch != p.data.branch){
    //                     System.out.println("------------------");
    //                     System.exit(-1);
    //                 }
    //                 pbt.internalNode.scoreCalculator.swapRealTaxon(
    //                     pbt.index,
    //                     partition
    //                     );
    //             }
    //             p.data.branch.swapRealTaxa(partition);

    //         }
    //     }
        
    // }




    public void batchTrasferRealTaxon(ArrayList<Integer> realTaxonIndices){
        ArrayList<Integer> currPartitions = new ArrayList<>();
        ArrayList<Integer> realTaxonIds = new ArrayList<>();

        for(int i = 0; i < realTaxonIndices.size(); ++i){
            int index = realTaxonIndices.get(i);
            int partition = this.taxaPerLevel.inWhichPartitionRealTaxonByIndex(index);
            currPartitions.add(partition);
            realTaxonIds.add(this.taxaPerLevel.realTaxa[index].id);
        }
        

        for(BookKeepingPerTreeDC bkpt : this.bookKeepingPerTreeDCs){
            bkpt.batchTranserRealTaxon(realTaxonIds, currPartitions);
        }

        Queue<Utility.Pair<Component, Integer>> q = new ArrayDeque<>();

        for(Integer rtId : realTaxonIds){
            q.add(new Utility.Pair<Component,Integer>(this.dc.realTaxaComponents[rtId], this.taxaPerLevel.inWhichPartition(rtId)));
        }

        Set<InternalNode> st = new HashSet<>();

        while(!q.isEmpty()){
            var f = q.poll();
            for(InternalNodeWithIndex p : f.first.partOfInternalNodes){
                p.internalNode.cumulateTransfer(p.index, f.second);
                st.add(p.internalNode);
            }
            for(var x : f.first.parents){
                q.add(new Utility.Pair<Component, Integer>(x, f.second));
            }
            // q.addAll(f.parents);
        }

        for(var x : st){
            x.batchTransfer();
        }

        this.taxaPerLevel.batchTransferRealTaxon(realTaxonIndices);

    }


    public void transferRealTaxon(int index){
        
        int partition = this.taxaPerLevel.inWhichPartitionRealTaxonByIndex(index);
        this.taxaPerLevel.swapPartitionRealTaxon(index);
        int rtId = this.taxaPerLevel.realTaxa[index].id;

        // for(BookKeepingPerTreeDC bkpt : this.bookKeepingPerTreeDCs){
        //     bkpt.swapRealTaxon(rtId, partition);
        // }
        
        // this.dc.realTaxaPartitionNodes[rtId].data.branch.swapRealTaxa(partition);

        Queue<Component> q = new ArrayDeque<>();
        // q.add(this.dc.realTaxaPartitionNodes[index]);
        // System.out.println(this.dc.realTaxaPartitionNodes[rtId].parents);
        // q.addAll(this.dc.realTaxaPartitionNodes[rtId].parents);
        q.add(this.dc.realTaxaComponents[rtId]);

        // Set<PartitionNode> st = new HashSet<>();
        // st.addAll(this.dc.realTaxaPartitionNodes[rtId].parents);
        
        // for(var x : this.dc.partitionsByTreeNodes){
        //     System.out.println(x);
        // }
        // System.out.println();

        while(!q.isEmpty()){
            Component f = q.poll();


            for(InternalNodeWithIndex p : f.partOfInternalNodes){
                // System.out.println(p.partitionByTreeNode);

                // System.out.println("index: " + p.index);
                // if(p.partitionByTreeNode.partitionNodes[p.index] != f){
                //     System.out.println("------------------");
                // }
                // p.internalNode.scoreCalculator.transferRealTaxon(
                //     p.index,
                //     partition
                // );
                if(p.method == InternalNodeWithIndex.Method.COMMON){
                    p.internalNode.scoreCalculator.transferCommon(
                        p.index,
                        partition
                    );
                }
                else if(p.method == InternalNodeWithIndex.Method.UNIQUE){
                    p.internalNode.scoreCalculator.transferUnique(
                        p.index,
                        partition
                    );
                }
                else{
                    p.internalNode.scoreCalculator.transferParentUnique(partition);
                }

            }
            // for(PartitionNode p : f.parents){
            //     if(st.add(p)){
            //         q.add(p);
            //     }
            //     else{
            //         System.out.println("----------------------------");
            //     }
            // }
            q.addAll(f.parents);
            f.data.branch.swapRealTaxa(partition);
        }
    }

    public void swapDummyTaxon(int index){
        int partition = this.taxaPerLevel.inWhichPartitionDummyTaxonByIndex(index);
        this.taxaPerLevel.swapPartitionDummyTaxon(index);
        
        // for(BookKeepingPerTreeDC bkpt : this.bookKeepingPerTreeDCs){
        //     bkpt.swapDummyTaxon(index, partition);
        // }

        for(InternalNode p : this.dc.internalNodes){
            p.scoreCalculator.transferDummyTaxon(index, partition);
        }

        Set<Component> st = new HashSet<>();
        
        Queue<Component> q = new ArrayDeque<>();

        DummyTaxon dt = this.taxaPerLevel.dummyTaxa[index];

        for(RealTaxon rt : dt.flattenedRealTaxa){
            this.dc.realTaxaComponents[rt.id].data.branch.swapDummyTaxon(index, partition);
            for(Component p : this.dc.realTaxaComponents[rt.id].parents){
                if(st.add(p)){
                    q.add(p);
                }
            }
        }

        while(!q.isEmpty()){
            Component f = q.poll();
            f.data.branch.swapDummyTaxon(index, partition);
            for(Component p : f.parents){
                if(st.add(p)){
                    q.add(p);
                }
            }
        }

    }

    public void swapTaxon(int index, boolean isDummy){
        if(isDummy) this.swapDummyTaxon(index);
        else this.transferRealTaxon(index);
    }

    public TaxaPerLevelWithPartition[] divide(IMakePartition makePartition){
        RealTaxon[][] rts = new RealTaxon[2][];
        DummyTaxon[][] dts = new DummyTaxon[2][];


        for(int i = 0; i < 2; ++i){
            rts[i] = new RealTaxon[this.taxaPerLevel.getRealTaxonCountInPartition(i)];
            dts[i] = new DummyTaxon[this.taxaPerLevel.getDummyTaxonCountInPartition(i)];
            // var x = makePartition.makePartition(rts[i], dts[i]);
            // rtsPart[i] = x.realTaxonPartition;
            // dtsPart[i] = x.dummyTaxonPartition;
        }

        int[] index = new int[2];

        for(var x : this.taxaPerLevel.realTaxa){
            int part = this.taxaPerLevel.inWhichPartition(x.id);
            rts[part][index[part]++] = x;
        }
        index[0] = 0;
        index[1] = 0;
        int i = 0;
        for(var x : this.taxaPerLevel.dummyTaxa){
            int part = this.taxaPerLevel.inWhichPartitionDummyTaxonByIndex(i++);
            dts[part][index[part]++] = x;
        }


        // ith dummy taxon for ith partition
        DummyTaxon[] newDt = new DummyTaxon[2];
        
        

        // BookKeepingPerLevel[] bookKeepingPerLevels = new BookKeepingPerLevel[2];
        TaxaPerLevelWithPartition[] taxaPerLevelWithPartitions = new TaxaPerLevelWithPartition[2];
        for( i = 0; i < 2; ++i){
            newDt[i] = new DummyTaxon(rts[1 - i], dts[1 - i]);
            
            DummyTaxon[] dtsWithNewDt = new DummyTaxon[dts[i].length + 1];
            for(int j = 0; j < dts[i].length; ++j){
                dtsWithNewDt[j] = dts[i][j];
            }
            dtsWithNewDt[dtsWithNewDt.length - 1] = newDt[i];

            if(rts[i].length + dtsWithNewDt.length > 3){

                var y = makePartition.makePartition(rts[i], dtsWithNewDt, true);
                taxaPerLevelWithPartitions[i] = new TaxaPerLevelWithPartition(
                    rts[i], dtsWithNewDt, 
                    y.realTaxonPartition, 
                    y.dummyTaxonPartition, 
                    this.dc.taxa.length
                );
            }
            else{
                taxaPerLevelWithPartitions[i] = new TaxaPerLevelWithPartition(
                    rts[i], dtsWithNewDt, 
                    null, null,
                    this.dc.taxa.length
                );
            }
            
            // bookKeepingPerLevels[i] = new BookKeepingPerLevel(this.geneTrees,x);
        }

        return taxaPerLevelWithPartitions;
    }



}
