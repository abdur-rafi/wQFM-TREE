package src.ScoreCalculator;

public interface NumSatCalculatorNode {

    public double score();

    public double[][] gainRealTaxa(double originalScore, double multiplier);

    public void swapRealTaxon(int branchIndex, int currPartition);

    public void swapDummyTaxon(int dummyIndex, int currPartition);

    public void gainDummyTaxa(double originalScore, double multiplier, double[] dummyTaxaGains);

}

