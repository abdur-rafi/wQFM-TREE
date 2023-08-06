package wqfm.dsGT;

import java.util.ArrayList;

public class PerLevelDs {
    
    ArrayList<GeneTree> geneTrees;

    
    public PerLevelDs(){
        geneTrees = new ArrayList<>();
    } 

    public void addGeneTree(String s){
        geneTrees.add(new GeneTree(s));
    }

    public void addGeneTree(GeneTree tr){
        geneTrees.add(tr);
    }
    
}
