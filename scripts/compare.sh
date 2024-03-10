
# cd $1
# list current dir and run a for loop

# filePrefix="ewqfm-%sat-lvs-c-n-n-n-v2"
# filePrefix="ewqfm-%sat-lvs-mxpart-c-n-n-n-v2"
filePrefix="ewqfm-%sat-init-with-singleton-all-flat"
filePrefix="ewqfm-test"
# filePrefix="wqfm-Tree-input-merged"
filePrefix="wqfm-Tree"
filePrefix2="wqfm-Tree-input-merged"


for file in $(ls $1)
do
    if [ -d $1/$file ]
    then
        echo $file

        for file2 in $(ls $1/$file)
        do
            if [ -d $1/$file/$file2 ]
            then
                echo $file2
                python ./rfScoreCalculator/getFpFn.py -t $1/$file/$file2/$filePrefix2-tree.txt -e $1/$file/$file2/$filePrefix-tree.txt
            fi
        done
        echo "$file done"
    fi
done
