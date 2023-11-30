
# cd $1
# list current dir and run a for loop

# filePrefix="ewqfm-%sat-c-f-n-f-v2"
for file in $(ls $1)
# for file in "1X-500-500"
do
    if [ -d $1/$file ]
    then
        echo $file
        for file2 in $(ls $1/$file)
        do
            if [ -d $1/$file/$file2 ]
            then
                echo $file2
                ./raxml-ng --redo --consense MRE --tree $1/$file/$file2/all_gt.tre --prefix $1/$file/$file2/cons >> ./raxml-ng.log
                consOut=$1/$file/$file2/cons.raxml.consensusTreeMRE
                python3 ./scripts/consensusCleaner.py < $consOut > $1/$file/$file2/cons.tre
            fi
        done
        echo "$file done"
    fi
done
