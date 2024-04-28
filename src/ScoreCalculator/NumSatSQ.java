package src.ScoreCalculator;

public interface NumSatSQ {

    // public double score();

    public RTGainReturnType gainRealTaxa(double originalScore);

    // public void transferRealTaxon(int branchIndex, int currPartition);
    public void transferCommon(int branchIndex, int currPartition);

    public void transferUnique(int branchIndex, int currPartition);
    
    public void transferParentUnique(int currPartition);

    public void transferDummyTaxon(int dummyIndex, int currPartition);

    public void gainDummyTaxa(double originalScore, double[] dummyTaxaGains);

    public void batchTransferRealTaxon(int branchIndex, int netTranser);    

    public static class RTGainReturnType{
        public double[][] commonGains, uniqueGains;
        public double[] uniqueParentGains;
    }

    public double sat();
    public double vio();

    public RTGainReturnType gainSatRealTaxa(double currSat);
    public RTGainReturnType gainVioRealTaxa(double currVio);

    public void gainSatDummyTaxa(double[] a, double currSat);
    public void gainVioDummyTaxa(double[] a, double currVio);

}
