root=$1
csvPath=$2
> $csvPath
# for file in model.200.10000000.0.0000001 model.200.10000000.0.000001 model.200.2000000.0.000001 model.200.2000000.0.0000001 
# for file in model.200.500000.0.000001
for file in model.1000.2000000.0.000001
do
    echo $file 
    for file2 in $(ls $root/$file)
    do
        if [[ $file2 == *"avg"* ]]; then
            :
            # echo "The filename contains 'avg'"
        elif [[ $file2 == *"200"* ]]; then
            :
        elif [[ $file2 == *"50"* ]]; then
            :
        else
            echo $file2 
            python ./scripts/scoreSep.py < $root/$file/$file2 >> $csvPath
            
        # else
            # echo "The filename does not contain 'avg'"
        fi
    done
    # if [[ $file == *"avg"* ]]; then
    #     :
    #     # echo "The filename contains 'avg'"
    # else
    #     echo $file
    #     python ./scripts/scoreSep.py < $root/$file >> $csvPath
    #     # echo "The filename does not contain 'avg'"
    # fi
done
