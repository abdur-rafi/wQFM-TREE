# bash ./scripts/recurse25.sh  ../AstralPro/n25

root=$1
# copyRoot=$2
f1="avg-wqfm_old_scores.txt"
f2="avg-e100_wqfm_old_2.txt"


# for k in $(ls $root); do
for k in k1000 k2500 k10000 ; do
    # for dup in $(ls $root/$k); do 
    for dup in dup.2 dup1 dup2 dup5 ; do 

        if [ ! -d "$root/$k/$dup" ]; then
            continue
        fi

        for loss in $(ls $root/$k/$dup); do
            for ils in $(ls $root/$k/$dup/$loss); do
                echo  "$k $dup $loss $ils"
                # mkdir -p $copyRoot/$k/$dup/$loss/$ils
                bash ./scripts/runDisco.sh $root/$k/$dup/$loss/$ils 1
                bash ./scripts/cleangt.sh $root/$k/$dup/$loss/$ils 1
                # if [ -f "$root/$k/$dup/$loss/$ils/$f2" ]; then
                #     echo $f1
                #     cat $root/$k/$dup/$loss/$ils/$f1
                #     echo $f2
                #     cat $root/$k/$dup/$loss/$ils/$f2
                # fi
                
            done
        done
    done
done