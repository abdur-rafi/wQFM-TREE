package src;


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

    public static double SINGLETON_THRESHOLD = .5; 
    public static boolean ALLOW_SINGLETON = true;

    public static boolean USE_SCORING_IN_CONSENSUS = false;

    // public static double MAX_DEPTH_MULTIPLIER = 2;

    // public static boolean USE_MAX_DEPTH_MULTIPLIER = true;

}
