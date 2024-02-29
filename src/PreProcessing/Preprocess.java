package src.PreProcessing;

import java.io.FileNotFoundException;
import java.util.Map;

import src.Taxon.RealTaxon;

public class Preprocess {

    public static class PreprocessReturnType{
        public DataContainer dc;
        public Map<String, RealTaxon> taxaMap;
        public RealTaxon[] realTaxa;
        
    }

    public static PreprocessReturnType preprocess(String geneTreePath) throws FileNotFoundException{
        GeneTrees geneTrees = new GeneTrees(geneTreePath);
        PreprocessReturnType ret = new PreprocessReturnType();
        ret.taxaMap = geneTrees.readTaxaNames();
        ret.dc = geneTrees.readGeneTrees(null);
        ret.realTaxa = geneTrees.taxa;
        return ret;
    }
    
}
