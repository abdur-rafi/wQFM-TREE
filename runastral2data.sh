
filePrefix="ewqfm"



for file in $(ls $1)
# for file in "1X-500-500"
do
    echo $file
    # check if file is a directory
        # for file2 in $(ls $1/$file)
        # do
        #     # check if file is a directory
        #     if [ -d $1/$file/$file2 ]
        #     then
        #         echo $file2
        #         # if directory then run the script recursively
        #         # echo $file2
        #         # ./raxml-ng --redo --consense MRE --tree $1/$file/$file2/all_gt.tre --prefix $1/$file/$file2/cons >> ./raxml-ng.log
        #         # consOut=$1/$file/$file2/cons.raxml.consensusTreeMRE
        #         # python consensusCleaner.py < $consOut > $1/$file/$file2/cons.tre
        #         python3 treeCleaner.py < $1/$file/$file2/all_gt.tre > $1/$file/$file2/all_gt_cleaned.tre 
        #         /usr/bin/env /usr/lib/jvm/java-17-openjdk-amd64/bin/java -XX:+ShowCodeDetailsInExceptionMessages -cp /home/rumi/.vscode-server/data/User/workspaceStorage/35a68d4449bc6a8fcddb29edcf76836a/redhat.java/jdt_ws/EWQFM_3ae3de11/bin src.Main $1/$file/$file2/all_gt_cleaned.tre $1/$file/$file2/cons.tre $1/$file/$file2/$filePrefix-tree.txt
        #         python3 ./rfScoreCalculator/getFpFn.py -t $1/true_tree_trimmed -e $1/$file/$file2/$filePrefix-tree.txt >> $1/$file/$filePrefix-score.txt
        #         python3 ./rfScoreCalculator/getFpFn.py -t $1/true_tree_trimmed -e $1/$file/$file2/wQFM-v1.2-best.tre >> $1/$file/wqfm-v1.2-scores.txt
        #     fi
        # done
        # python3 ./rfAverager.py < $1/$file/wqfm-v1.2-scores.txt > $1/$file/wqfm-v1.2-scores-avg.txt
        # python3 ./rfAverager.py < $1/$file/$filePrefix-score.txt > $1/$file/avg-$filePrefix-score.txt
        # echo "$file done"
        # # break
    
done

# /usr/bin/env /usr/lib/jvm/java-17-openjdk-amd64/bin/java -XX:+ShowCodeDetailsInExceptionMessages -cp /home/abdur-rafi/.config/Code/User/workspaceStorage/da91ba3e148e5727246c82da7f9911d2/redhat.java/jdt_ws/E-WQFM_731a4073/bin src.Main 