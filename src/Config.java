package src;

// import src.ScoreCalculator.FractionSat;
import src.ScoreCalculator.SatSubVio;
import src.ScoreCalculator.ScoreEqn;

public class Config {
    
    public enum ScoreNormalizationType{
        FLAT_NORMALIZATION,
        NESTED_NORMALIZATION,
        NO_NORMALIZATION
    }
    
    public static ScoreNormalizationType SCORE_NORMALIZATION_TYPE = ScoreNormalizationType.NESTED_NORMALIZATION;

    public enum ConsensusWeightType{
        FLAT,
        NESTED
    }

    public static ConsensusWeightType CONSENSUS_WEIGHT_TYPE = ConsensusWeightType.NESTED;

    public static boolean ALLOW_SINGLETON = false;
    public static boolean USE_LEVEL_BASED_SINGLETON_THRESHOLD = true;
    public static boolean USE_SCORING_IN_CONSENSUS = true;


    public static double SINGLETON_THRESHOLD = .5; 
    public static double MAX_LEVEL_MULTIPLIER = .5;


    public static ScoreEqn SCORE_EQN = new SatSubVio();
    // public static ScoreEqn SCORE_EQN = new FractionSat();


    public static int MAX_ITERATION = 5;
    // public static boolean USE_MAX_DEPTH_MULTIPLIER = true;

    public static boolean RESOLVE_POLYTOMY = false;

    public enum NonQuartetType{
        A,B
    }

    public static NonQuartetType NON_QUARTET_TYPE = NonQuartetType.A;

    public static double DUMMY_TAXA_PREALLOCATE_MULTIPLIER = 1.5;
    public static int DUMMY_TAXA_INCREMENT = 10;
}
