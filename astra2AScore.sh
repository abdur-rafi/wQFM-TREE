
filePrefix="astral2"


rootPath=$1
modelCond=$2
spFolderPath=$rootPath/true-specis-trees/$modelCond
scoresFolderPath=$rootPath/scores-estimated-gt/$modelCond
astralFolderPath=$1/astral/$modelCond


speciesTreeLabel=s_tree.trees
speciesTreeLabelCleaned=sp-cleaned
astralOutputLabel=astral-v474-p1-halfresolved.genes1000

for file in $(ls $spFolderPath)
do
    python treeCleaner.py < $spFolderPath/$file/$speciesTreeLabel > $spFolderPath/$file/$speciesTreeLabelCleaned
done



mkdir -p $scoresFolderPath

> $scoresFolderPath/$filePrefix-score.txt


for file in $(ls $spFolderPath)
do 
    spPath=$spFolderPath/$file/$speciesTreeLabelCleaned
    astralOutPath=$astralFolderPath/$file/$astralOutputLabel

    echo $file

    python ./rfScoreCalculator/getFpFn.py -t $spPath -e $astralOutPath >> $scoresFolderPath/$filePrefix-score.txt

done

python ./rfAverager.py < $scoresFolderPath/$filePrefix-score.txt > $scoresFolderPath/avg-$filePrefix-score.txt

