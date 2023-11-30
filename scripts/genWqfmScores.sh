
# cd $1
# list current dir and run a for loop

for file in $(ls $1)
# for file in "1X-500-500"
do
    # echo $file
    # check if file is a directory
    if [ -d $1/$file ]
    then
        echo $file
        > $1/$file/wqfm-v1.2-all-scores.txt
        for file2 in $(ls $1/$file)
        do
            if [ -d $1/$file/$file2 ]
            then
                echo $file2
                python ./rfScoreCalculator/getFpFn.py -t $1/true_tree_trimmed -e $1/$file/$file2/wQFM-v1.2-all.tre >> $1/$file/wqfm-v1.2-all-scores.txt
            fi
        done
        python ./scripts/rfAverager.py < $1/$file/wqfm-v1.2-all-scores.txt > $1/$file/avg-wqfm-v1.2-all-scores.txt
        echo "$file done"
        # break
    fi
done
