root=$1

for file in $(ls $root); do
    # check if file is not a directory
    if [ ! -d $root/$file ]; then
        echo $file
        # python ./scripts/findLine.py "$sentencePrefix" $5 < $root/$file > $toPath/$file
        python ./scripts/findLine.py "ASTRAL finished in" < $root/$file > ./astralTimes/$file-times
    fi
done