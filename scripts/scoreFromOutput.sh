
filePrefix="astral2-200"
# filePrefix="ewqfm-A-200"


rootPath=$1
modelCond=$2
spFolderPath=$rootPath/true-specis-trees/$modelCond
scoresFolderPath=$rootPath/scores-estimated-gt-200/$modelCond

speciesTreeLabel=s_tree.trees
speciesTreeLabelCleaned=sp-cleaned
# astralOutputLabel=astral-v474-p1-halfresolved.genes1000
# astralOutputLabel=astral-v474-p1-halfresolved.genes200
# astralOutputLabel2=astral2-200-tree.txt
outputFolderPath=$rootPath/output-estimated-gt-200/$modelCond
outputLabel=ewqfm-A-200-tree.txt
outputLabel=astral2-200-tree.txt
# for file in $(ls $spFolderPath)
# do
#     python ./scripts/treeCleaner.py < $spFolderPath/$file/$speciesTreeLabel > $spFolderPath/$file/$speciesTreeLabelCleaned
# done



mkdir -p $scoresFolderPath

> $scoresFolderPath/$filePrefix-score.txt


for file in $(ls $spFolderPath)
do 
    spPath=$spFolderPath/$file/$speciesTreeLabelCleaned
    outPath=$outputFolderPath/$file/$outputLabel

    echo $file
    python ./rfScoreCalculator/getFpFn.py -t $spPath -e $outPath >> $scoresFolderPath/$filePrefix-score.txt

done

python ./scripts/rfAverager.py < $scoresFolderPath/$filePrefix-score.txt > $scoresFolderPath/avg-$filePrefix-score.txt

