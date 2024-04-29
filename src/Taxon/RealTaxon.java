package src.Taxon;

// import src.PreProcessing.Component;

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

    @Override
    public String toString(){
        return label;
    }

    // public Component component;

    
}
