# bash ./scripts/recurse25.sh  ../AstralPro/n25

root=$1
# copyRoot=$2


# for k in $(ls $root); do
for k in k1000 k2500 k10000 ; do
    for dup in $(ls $root/$k); do 
        for loss in $(ls $root/$k/$dup); do
            for ils in $(ls $root/$k/$dup/$loss); do
                # mkdir -p $copyRoot/$k/$dup/$loss/$ils
                # bash ./scripts/cleangt.sh $root/$k/$dup/$loss/$ils 1
                # bash ./scripts/cleangt.sh $root/$k/$dup/$loss/$ils 5
                # ls $root/$k/$dup/$loss/$ils/e100-wqfm-updated.txt
                echo "$k-$dup-$loss-$ils-100bp " >> 25.astral.scores
                python scripts/scoreSep.py < $root/$k/$dup/$loss/$ils/e100-Astral-scores.txt >> 25.astral.scores
                # exit
            done
        done
    done
done