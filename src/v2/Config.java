package src.v2;


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

    public static ConsensusWeightType CONSENSUS_WEIGHT_TYPE = ConsensusWeightType.FLAT;

}
