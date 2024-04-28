package src.PreProcessing;

import java.util.ArrayList;
import java.util.Collections;

import src.ScoreCalculator.NumSatSQ;

public class InternalNode {

    public Component[] childCompsCommon;
    public Component[] childCompsUniques;
    public Component parentUniques;

    public int[] netTranser;

    public int count;
    public int parentSpeciationCount;
    public NumSatSQ scoreCalculator;

    public InternalNode(Component[] childCompsCommon, Component[] childCompsUniques, Component parentUniques, boolean isParentSpeciationNode){
        this.childCompsCommon = childCompsCommon;
        this.childCompsUniques = childCompsUniques;
        this.parentUniques = parentUniques;

        this.count = 1;
        this.parentSpeciationCount = 0;
        this.scoreCalculator = null;


        if(isParentSpeciationNode){
            this.parentSpeciationCount = 1;
        }

        if(childCompsCommon.length > 2){
            System.out.println("polytomy");
        }
        for(int i = 0; i < childCompsCommon.length; ++i){
            childCompsCommon[i].addInternalNode(this, i, Component.InternalNodeWithIndex.Method.COMMON);
            childCompsUniques[i].addInternalNode(this, i, Component.InternalNodeWithIndex.Method.UNIQUE);
        }

        parentUniques.addInternalNode(this, 0, Component.InternalNodeWithIndex.Method.PARENT);

        this.netTranser = new int[childCompsCommon.length];

    }

    public void increaseCount(boolean isParentSpeciationNode){
        if(isParentSpeciationNode){
            this.parentSpeciationCount++;
        }
        this.count++;
    }
    
    
    public void batchTransfer(){
        // for(int i = 0; i < this.partitionNodes.length; ++i){
        //     if(this.netTranser[i] != 0){
        //         this.scoreCalculator.batchTransferRealTaxon(i, this.netTranser[i]);
        //     }
        //     netTranser[i] = 0;
        // }
        
    }


    @Override
    public String toString(){
        return convertToString(childCompsCommon, childCompsUniques, parentUniques);
    }


    public void cumulateTransfer(int index, int currPartition){
        // negative if transfering from 1 to 0
        // positive if transfering from 0 to 1
        netTranser[index] += (currPartition == 0 ? 1 : -1);

    }

    public static String convertToString(Component[] childCompsCommon, Component[] childCompsUniques, Component parentUniques){
        ArrayList<String> componentStrings = new ArrayList<>();
        for(Component p : childCompsCommon){
            componentStrings.add(p.toString());
        }
        for(Component p : childCompsUniques){
            componentStrings.add(p.toString());
        }
        componentStrings.add(parentUniques.toString());
        Collections.sort(componentStrings);
        return String.join("", componentStrings);
    }
}
