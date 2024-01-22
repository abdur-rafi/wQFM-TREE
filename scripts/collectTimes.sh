root=$1
writeTo=$2
>$writeTo
for file in $(ls $root); do 
    # check if file is not a directory
    if [ ! -d $root/$file ]; then
        if [[ ! $file == *"1000."* ]]; then
            continue;
        fi
        if [[ $file == *"0g"* ]]; then
            continue;
        fi
        # if [[ $file == *"g"* ]]; then
        #     echo $file
        #     python ./scripts/collectTimes.py < $root/$file >> $writeTo
        #     # python ./scripts/scoreSep.py < $root/$file > $root/$file
        # fi

        echo $file
        python ./scripts/collectTimes.py < $root/$file >> $writeTo
    fi
done