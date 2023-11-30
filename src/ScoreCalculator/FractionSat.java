package src.ScoreCalculator;

public class FractionSat implements ScoreEqn{

    @Override
    public double scoreFromSatAndTotal(double total, double sat) {
        if(total == 0) return 0;
        return sat / total;
    }
    
}
