
# cd $1
# list current dir and run a for loop

filePrefix="ewqfm-%sat-c-f-n-f-v2"
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
                python treeCleaner.py < $1/$file/$file2/all_gt.tre > $1/$file/$file2/all_gt_cleaned.tre 
                'C:\Program Files\Amazon Corretto\jdk11.0.9_12\bin\java.exe' '-cp' 'C:\Users\Lenovo\AppData\Roaming\Code\User\workspaceStorage\9199226b00b8f584b08bf496d9fcae2d\redhat.java\jdt_ws\E-WQFM_15ce5873\bin' 'src.Main' $1/$file/$file2/all_gt_cleaned.tre $1/$file/$file2/cons.tre $1/$file/$file2/$filePrefix-tree.txt
                python ./rfScoreCalculator/getFpFn.py -t $1/true_tree_trimmed -e $1/$file/$file2/$filePrefix-tree.txt >> $1/$file/$filePrefix-score.txt
            fi
        done
        # python ./rfAverager.py < $1/$file/wqfm-v1.2-scores.txt > $1/$file/wqfm-v1.2-scores-avg.txt
        python ./rfAverager.py < $1/$file/$filePrefix-score.txt > $1/$file/avg-$filePrefix-score.txt
        echo "$file done"
        # break
    fi
done

# /usr/bin/env /usr/lib/jvm/java-17-openjdk-amd64/bin/java -XX:+ShowCodeDetailsInExceptionMessages -cp /home/abdur-rafi/.config/Code/User/workspaceStorage/da91ba3e148e5727246c82da7f9911d2/redhat.java/jdt_ws/E-WQFM_731a4073/bin src.Main 