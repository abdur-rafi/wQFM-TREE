package src.v2.Taxon;

public class RealTaxon {

    public static int count = 0;

    public int id;
    public String label;
    
    
    public RealTaxon(int i, String lb){
        id = i;
        label = lb;
    }

    public RealTaxon(String lb){
        id = count++;
        label = lb;
    }

    
}
