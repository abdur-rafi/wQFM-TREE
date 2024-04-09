root=$1
copyRoot=$2

for k in $(ls $root); do
    for dup in $(ls $root/$k); do 
        for loss in $(ls $root/$k/$dup); do
            for ils in $(ls $root/$k/$dup/$loss); do
                mkdir -p $copyRoot/$k/$dup/$loss/$ils
                bash ./scripts/cleangt.sh $root/$k/$dup/$loss/$ils $copyRoot/$k/$dup/$loss/$ils 1
                bash ./scripts/cleangt.sh $root/$k/$dup/$loss/$ils $copyRoot/$k/$dup/$loss/$ils 5
            done
        done
    done
done