outPath=results/Astral_2_datasets/outputs
scorePath=results/Astral_2_datasets/scores-by-model-cond
spPath=results/Astral_2_datasets/true-specis-trees

for modelCond in $(ls $outPath); do
    for replicate in $(ls $outPath/$modelCond); do
        
        for file in $(ls $outPath/$modelCond/$replicate); do
            mkdir -p $scorePath/$modelCond
            python ./rfScoreCalculator/getFpFn.py -e $outPath/$modelCond/$replicate/$file -t $spPath/$modelCond/$replicate/sp-cleaned >> $scorePath/$modelCond/$file
        done
    done
done

