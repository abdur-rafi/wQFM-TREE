root=$1
fileSubStr=$2
sentencePrefix=$3
toPath=$4
isInputInMsg=$5

for file in $(ls $root); do 
    # check if file is not a directory
    if [ ! -d $root/$file ]; then
        if [[ $file == *$fileSubStr* ]]; then
            echo $file
            python ./scripts/findLine.py "$sentencePrefix" $5 < $root/$file > $toPath/$file
        fi
    fi
done