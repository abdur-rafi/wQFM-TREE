package wqfm.dsGT;

import java.util.ArrayList;

public class PerLevelDs {
    
    ArrayList<GeneTree> geneTrees;
    ArrayList<IDummyTaxa> dummyTaxas;
    
    public PerLevelDs(ArrayList<IDummyTaxa> dt){
        geneTrees = new ArrayList<>();
        dummyTaxas = dt;
    } 

    public void addGeneTree(String s){
        geneTrees.add(new GeneTree(s));
    }

    public void addGeneTree(GeneTree tr){
        geneTrees.add(tr);
    }
    
}
