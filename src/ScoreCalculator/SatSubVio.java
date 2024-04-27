package src.ScoreCalculator;

public class SatSubVio implements ScoreEqn {
    
        @Override
        public double scoreFromSatAndTotal(double total, double sat) {
            // return sat;
            return 2 * sat - total;
        }
         
        @Override
        public double scoreFromVioAndTotal(double sat, double vio) {
            // return vio;
            return sat - vio;
        }
    
}
