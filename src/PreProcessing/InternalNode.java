package src.PreProcessing;

import java.util.ArrayList;
import java.util.Collections;

import src.ScoreCalculator.NumSatSQ;

public class InternalNode {

    public Component[] childs;
    public Component parent;

    public int[] netTranser;

    public int count;
    public NumSatSQ scoreCalculator;

    public InternalNode(Component[] childs, Component parent){
        this.childs = childs;
        this.parent = parent;

        this.count = 1;
        this.scoreCalculator = null;



        if(childs.length > 2){
            System.out.println("polytomy");
        }
        for(int i = 0; i < childs.length; ++i){
            childs[i].addInternalNode(this, i);
        }

        parent.addInternalNode(this, 2);

        // this.netTranser = new int[childCompsCommon.length];

    }

    public void increaseCount(){
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
        return convertToString(childs, parent);
    }


    public void cumulateTransfer(int index, int currPartition){
        // negative if transfering from 1 to 0
        // positive if transfering from 0 to 1
        netTranser[index] += (currPartition == 0 ? 1 : -1);

    }

    public static String convertToString(Component[] childs, Component parent){
        ArrayList<String> componentStrings = new ArrayList<>();
        for(Component p : childs){
            componentStrings.add(p.toString());
        }
        componentStrings.add(parent.toString());
        Collections.sort(componentStrings);
        return String.join("|", componentStrings);
    }
}
