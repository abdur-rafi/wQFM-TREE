scorePath=results/Astral_2_datasets/scores-by-model-cond

for modelCond in $(ls $scorePath); do
    for file in $(ls $scorePath/$modelCond); do
        echo $modelCond $file
        python ./scripts/rfAverager.py < $scorePath/$modelCond/$file > $scorePath/$modelCond/avg-$file
    done
done