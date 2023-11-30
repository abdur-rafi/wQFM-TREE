package src.ScoreCalculator;

public class SatSubVio implements ScoreEqn {
    
        @Override
        public double scoreFromSatAndTotal(double total, double sat) {
            // return sat;
            return 2 * sat - total;
        }
    
}
