package src.PreProcessing;

import java.io.FileNotFoundException;
import java.util.Map;

import src.Taxon.RealTaxon;

public class Preprocess {

    public static class PreprocessReturnType{
        // public DataContainer dc;
        public Map<String, RealTaxon> taxaMap;
        public RealTaxon[] realTaxa;
        
    }

    public static PreprocessReturnType preprocess(String geneTreePath) throws FileNotFoundException{
        GeneTrees geneTrees = new GeneTrees(geneTreePath);
        PreprocessReturnType ret = new PreprocessReturnType();
        ret.taxaMap = geneTrees.readTaxaNames();
        geneTrees.readGeneTrees(null);
        // ret.dc = geneTrees.createDateContainer();
        // ret.realTaxa = geneTrees.taxa;


        // System.out.println("-------------------");
        // for(var x : ret.dc.partitionsByTreeNodes){
        //     System.out.println(x);
        // }
        // for(var x : ret.dc.topSortedPartitionNodes){
        //     System.out.println(x);
        // }
        return ret;
    }
    
}
