
# cd $1
# list current dir and run a for loop

# filePrefix="ewqfm-%sat-lvs-c-n-n-n-v2"
# filePrefix="ewqfm-%sat-lvs-mxpart-c-n-n-n-v2"
filePrefix="ewqfm-%sat-init-with-singleton-all-flat"
filePrefix="ewqfm-test"
filePrefix="wqfm-Tree"
# filePrefix="ewqfm-num-sat-new"

# filePrefix="ewqfm-all-n-mxscore-init-singleton"


for file in $(ls $1)
# for file in "1X-500-500"
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
        > $1/$file/$filePrefix-score.txt

        for file2 in $(ls $1/$file)
        do
            # check if file is a directory
            if [ -d $1/$file/$file2 ]
            then
                echo $file2
                gtInput=$1/$file/$file2/all_gt_cleaned.tre
                consOutput=$1/$file/$file2/cons
                consCleaned=$1/$file/$file2/cons.tre
                outPath=$1/$file/$file2/$filePrefix-tree.txt

                
                # perl ./scripts/run_paup_consensus.pl -i $gtInput -o $consOutput > consLog.txt 2>consErr.txt

                consOut=$1/$file/$file2/cons.greedy.tree

                # python ./scripts/consensusCleaner.py < $consOut > $consCleaned


                /usr/bin/env /usr/lib/jvm/java-17-openjdk-amd64/bin/java  -XX:+ShowCodeDetailsInExceptionMessages -cp /home/abdur-rafi/.config/Code/User/workspaceStorage/da91ba3e148e5727246c82da7f9911d2/redhat.java/jdt_ws/E-WQFM_731a4073/bin src.Main $gtInput $consCleaned $outPath A
                python ./rfScoreCalculator/getFpFn.py -t $1/true_tree_trimmed -e $1/$file/$file2/$filePrefix-tree.txt >> $1/$file/$filePrefix-score.txt
            fi
        done
        # python ./rfAverager.py < $1/$file/wqfm-v1.2-scores.txt > $1/$file/wqfm-v1.2-scores-avg.txt
        python ./scripts/rfAverager.py < $1/$file/$filePrefix-score.txt > $1/$file/avg-$filePrefix-score.txt
        echo "$file done"
        # break
    fi
done

# /usr/bin/env /usr/lib/jvm/java-17-openjdk-amd64/bin/java -XX:+ShowCodeDetailsInExceptionMessages -cp /home/abdur-rafi/.config/Code/User/workspaceStorage/da91ba3e148e5727246c82da7f9911d2/redhat.java/jdt_ws/E-WQFM_731a4073/bin src.Main 