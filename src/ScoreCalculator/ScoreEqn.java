package src.ScoreCalculator;

public interface ScoreEqn {
    double scoreFromSatAndTotal(double total, double sat);
    double scoreFromVioAndTotal(double total, double vio);
}
