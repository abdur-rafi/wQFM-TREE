# copy outputs

# root=$1
# copyTo=./results/wqfm_datasets/outputs/48-taxon
# copyFile=wqfm-Tree-tree.txt
# copyFile=wQFM-v1.2-all.tre

# for modelCondition in $(ls $root); do
#     for replicate in $(ls $root/$modelCondition); do
#         if [ -d $root/$modelCondition/$replicate ]; then
#             # echo $modelCondition $replicate
#             mkdir -p $copyTo/$modelCondition/$replicate
#             cp $root/$modelCondition/$replicate/$copyFile $copyTo/$modelCondition/$replicate/$copyFile
#         fi
#     done
# done

# copy scores

root=$1
copyTo=./results/wqfm_datasets/scores/48-taxon
copyFile=avg-wqfm-Tree-score.txt
# copyFile=avg-wqfm-v1.2-all-scores.txt

for modelCondition in $(ls $root); do
    # for replicate in $(ls $root/$modelCondition); do
        if [ -d $root/$modelCondition ]; then
            # echo $modelCondition $replicate
            mkdir -p $copyTo/$modelCondition
            cp $root/$modelCondition/$copyFile $copyTo/$modelCondition/$copyFile
        fi
    # done
done