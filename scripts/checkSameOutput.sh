
fileName=wqfm-Tree-tree.txt
fileName2=wQFM-v1.2-all.tre
root=$1
for file in $(ls $root)
# for file in "1X-500-500"
do
    if [ -d $root/$file ]
    then
        echo $file

        if [ -d $root/$file ]
        then

            for file2 in $(ls $root/$file)
            do
                if [ -d $root/$file/$file2 ]
                then
                    echo $file2

                    python ./rfScoreCalculator/getFpFn.py -t $root/$file/$file2/$fileName -e $root/$file/$file2/$fileName2 
                fi
            done

        fi
    fi
done
