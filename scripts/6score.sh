for file in $(ls ./06); do
    echo $file
    python ./rfScoreCalculator/getFpFn.py -e ./06/$file -t ./06/sp-cleaned
done