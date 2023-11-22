
# filePrefix="ewqfm-%sat-lvs-c-n-n-n-v2"
# filePrefix="ewqfm-%sat-lvs-mxpart-c-n-n-n-v2"
filePrefix="wqfm"
# filePrefix="ewqfm-all-n-mxscore-init-singleton"


path=$1

spFolderPath=$path/species-trees

spFolderCleanedPath=$spFolderPath-cleaned

wqfmFolderPath=$path/wQFM-v1.2-species-trees

>$path/$filePrefix-score.txt

for file in $(ls $wqfmFolderPath)
do
    speciesTree=$spFolderCleanedPath/${file:0:2}
    wqfmTree=$wqfmFolderPath/$file
    python ./rfScoreCalculator/getFpFn.py -t $speciesTree -e $wqfmTree >> $path/$filePrefix-score.txt

done

python ./rfAverager.py < $path/$filePrefix-score.txt > $path/avg-$filePrefix-score.txt
