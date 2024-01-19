root=$1
# astralOutput=astral-v474-p1-halfresolved.genes1000
scoreFolder=results/Astral_2_datasets/scores-by-model-cond
for modelCond in $(ls $root); do

    if [ ! -d "$root/$modelCond" ]; then
        continue
    fi

    for replicate in $(ls $root/$modelCond); do
        if [ ! -d "$root/$modelCond/$replicate" ]; then
            continue
        fi
    
        # cp $root/$modelCond/$replicate/$astralOutput $root/$modelCond/$replicate/astral-tree.txt
        # mv $root/$modelCond/$replicate/astral-tree.txt $root/$modelCond/$replicate/astral2-tree.txt
        # count number of files in the folder
        numFiles=$(ls $root/$modelCond/$replicate | wc -l)
        # print if numFiles != 12
        if [ $numFiles -ne 12 ]; then
            echo "$root/$modelCond/$replicate"
        fi

        # check if any file in the folder is empty
        for file in $(ls $root/$modelCond/$replicate); do
            if [ ! -s "$root/$modelCond/$replicate/$file" ]; then
                echo "$root/$modelCond/$replicate/$file"
            fi
        done

    done


    

done