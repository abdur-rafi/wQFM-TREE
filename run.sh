
# cd $1
# list current dir and run a for loop

# for file in $(ls $1)
for file in "noscale.400g.500b"
do
    # echo $file
    # check if file is a directory
    if [ -d $1/$file ]
    then
        # if directory then run the script recursively
        echo $file
        # for loop for files inside the $file directory
        # > $1/$file/wqfm-v1.2-scores.txt
        # > $1/$file/ewqfm-cons-flat-norm-flat-score.txt
        > $1/$file/ewqfm-cons-flat-norm-nested-score.txt


        for file2 in $(ls $1/$file)
        do
            echo $file2
            # check if file is a directory
            if [ -d $1/$file/$file2 ]
            then
                # if directory then run the script recursively
                # echo $file2
                ./raxml-ng --redo --consense MRE --tree $1/$file/$file2/all_gt.tre --prefix $1/$file/$file2/cons >> ./raxml-ng.log
                consOut=$1/$file/$file2/cons.raxml.consensusTreeMRE
                python consensusCleaner.py < $consOut > $1/$file/$file2/cons.tre
                python treeCleaner.py < $1/$file/$file2/all_gt.tre > $1/$file/$file2/all_gt_cleaned.tre 
                /usr/bin/env /usr/lib/jvm/java-17-openjdk-amd64/bin/java  -XX:+ShowCodeDetailsInExceptionMessages -cp /home/abdur-rafi/.config/Code/User/workspaceStorage/da91ba3e148e5727246c82da7f9911d2/redhat.java/jdt_ws/E-WQFM_731a4073/bin src.Main $1/$file/$file2/all_gt_cleaned.tre $1/$file/$file2/cons.tre $1/$file/$file2/ewqfm-out-cons-flat-norm-nested.txt
                python ./rfScoreCalculator/getFpFn.py -t $1/true_tree_trimmed -e $1/$file/$file2/ewqfm-out-cons-flat-norm-nested.txt >> $1/$file/ewqfm-cons-flat-norm-nested-score.txt
                # python ./rfScoreCalculator/getFpFn.py -t $1/true_tree_trimmed -e $1/$file/$file2/wQFM-v1.2-best.tre >> $1/$file/wqfm-v1.2-scores.txt
            fi
        done
        # python ./rfAverager.py < $1/$file/wqfm-v1.2-scores.txt > $1/$file/wqfm-v1.2-scores-avg.txt
        python ./rfAverager.py < $1/$file/ewqfm-cons-flat-norm-nested-score.txt > $1/$file/avg-ewqfm-cons-flat-norm-nested-score.txt
        echo "$file done"
        # break
    fi
done

# /usr/bin/env /usr/lib/jvm/java-17-openjdk-amd64/bin/java -XX:+ShowCodeDetailsInExceptionMessages -cp /home/abdur-rafi/.config/Code/User/workspaceStorage/da91ba3e148e5727246c82da7f9911d2/redhat.java/jdt_ws/E-WQFM_731a4073/bin src.Main 