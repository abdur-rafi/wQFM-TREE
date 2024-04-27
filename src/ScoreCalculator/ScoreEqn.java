package src.ScoreCalculator;

public interface ScoreEqn {
    double scoreFromSatAndTotal(double total, double sat);
    double scoreFromSatAndVio(double sat, double vio);
}
