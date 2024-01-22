root=results/Astral_2_datasets/outputs/model.1000.2000000.0.000001
spPaht=results/Astral_2_datasets/true-specis-trees/model.1000.2000000.0.000001
outPath=results/Astral_2_datasets/scores-by-model-cond/model.1000.2000000.0.000001
for replicate in $(ls $root); do 

    for file in $(ls $root/$replicate); do
        echo $file
        python ./rfScoreCalculator/getFpFn.py -t $spPaht/$replicate/sp-cleaned -e $root/$replicate/$file >> $outPath/$file
    done
    

done


# for file in $(ls $outPath); do

#     # python ./rfScoreCalculator/getFpFn.py -t $spPaht/1/sp-cleaned -e $outPath/$file >> $outPath/$file
# done