package src.ScoreCalculator;

public interface NumSatCalculatorNodeDC {

    public double score();

    public double[][] gainRealTaxa(double originalScore, double multiplier);

    public void swapRealTaxon(int branchIndex, int currPartition, boolean changeBranchInfo);

    public void swapDummyTaxon(int dummyIndex, int currPartition, boolean changeBranchInfo);

    public void gainDummyTaxa(double originalScore, double multiplier, double[] dummyTaxaGains);
    
}

