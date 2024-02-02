outPath=results/Astral_2_datasets/outputs
scorePath=results/Astral_2_datasets/scores-by-model-cond
spPath=results/Astral_2_datasets/true-specis-trees

for modelCond in $(ls $outPath); do
    > $scorePath/$modelCond/astral3-50-score.txt
    > $scorePath/$modelCond/astral3-200-score.txt
    > $scorePath/$modelCond/astral3-score.txt

    for replicate in $(ls $outPath/$modelCond); do
        echo "$modelCond $replicate"
        python ./rfScoreCalculator/getFpFn.py -e $outPath/$modelCond/$replicate/astral3-50-tree.txt -t $spPath/$modelCond/$replicate/sp-cleaned >> $scorePath/$modelCond/astral3-50-score.txt
        python ./rfScoreCalculator/getFpFn.py -e $outPath/$modelCond/$replicate/astral3-200-tree.txt -t $spPath/$modelCond/$replicate/sp-cleaned >> $scorePath/$modelCond/astral3-200-score.txt
        python ./rfScoreCalculator/getFpFn.py -e $outPath/$modelCond/$replicate/astral3-tree.txt -t $spPath/$modelCond/$replicate/sp-cleaned >> $scorePath/$modelCond/astral3-score.txt

        # for file in $(ls $outPath/$modelCond/$replicate); do
        #     mkdir -p $scorePath/$modelCond
        #     python ./rfScoreCalculator/getFpFn.py -e $outPath/$modelCond/$replicate/$file -t $spPath/$modelCond/$replicate/sp-cleaned >> $scorePath/$modelCond/$file
        # done
    done
done

