
filePrefix="ewqfm-score-testing"


rootPath=$1
modelCond=$2
copyDir=/home/abdur-rafi/Academic/Thesis/run/astral2Cleaned/estimated-gene-trees/$2
gtFolderPath=$rootPath/estimated-gene-trees/$modelCond
spFolderPath=$rootPath/true-specis-trees/$modelCond
consFolderPath=$rootPath/estimated-consensus-trees/$modelCond
outFolderPath=$rootPath/output-estimated-gt/$modelCond
scoresFolderPath=$rootPath/scores-estimated-gt/$modelCond



geneTreeLabel=estimatedgenetre
speciesTreeLabel=s_tree.trees
consTreeLabel=cons-paup.tre
geneTreeLabelCleaned=gt-cleaned
speciesTreeLabelCleaned=sp-cleaned



# for file in $(ls $gtFolderPath)
# do
#     fp=$gtFolderPath/$file/$geneTreeLabel.gz
#     if [ -f $fp ];then
#         gzip -d $gtFolderPath/$file/$geneTreeLabel.gz
#     fi
# done

# # # clean gene and species trees

# for file in $(ls $gtFolderPath)
# do
#     python ./scripts/treeCleaner.py < $gtFolderPath/$file/$geneTreeLabel > $gtFolderPath/$file/$geneTreeLabelCleaned
#     python ./scripts/treeCleaner.py < $spFolderPath/$file/$speciesTreeLabel > $spFolderPath/$file/$speciesTreeLabelCleaned
# done


# for file in $(ls $gtFolderPath)
# do
#     echo "consensus tree for $file"

#     mkdir -p $consFolderPath/$file
#     # ./iqtree -t $gtFolderPath/$file/$geneTreeLabel -con >> consLog.txt 2>>consErr.txt
#     perl ./scripts/run_paup_consensus.pl -i $gtFolderPath/$file/$geneTreeLabelCleaned -o $consFolderPath/$file/cons > consLog.txt 2>consErr.txt
#     # bash ./scripts/phylip.sh $gtFolderPath/$file/$geneTreeLabel > consLog.txt 2>consErr.txt
#     # consOut=$consFolderPath/$file/cons.iqtree
#     consOut=$consFolderPath/$file/cons.greedy.tree

#     # mv ./outtree $consOut
#     # ./raxml-ng --redo --consense MRE --tree $gtFolderPath/$file/$geneTreeLabel --prefix $consFolderPath/$file/cons > ./raxml-ng.log
#     python ./scripts/consensusCleaner.py < $consOut > $consFolderPath/$file/$consTreeLabel
# done




# for file in $(ls $gtFolderPath)
# do
#     python ./rfScoreCalculator/getFpFn.py -t $consFolderPath/$file/$consTreeLabel -e $consFolderPath/$file/cons.iqtree
#     # cat $consFolderPath/$file/$consTreeLabel
# done



runOne() {

    file=$1
    gtPath=$gtFolderPath/$file/$geneTreeLabelCleaned
    consPath=$consFolderPath/$file/$consTreeLabel
    spPath=$spFolderPath/$file/$speciesTreeLabelCleaned

    mkdir -p $outFolderPath/$file

    outPath=$outFolderPath/$file/$filePrefix-tree.txt

    echo $file
    mkdir -p $copyDir/$file

    cp $gtPath $copyDir/$file/$geneTreeLabelCleaned


}

if [ "$#" -gt 2 ]; then
    for (( i=3; i<= $#; i++ )); do
        runOne ${!i}
    done
else
    for file in $(ls $gtFolderPath)
    do 
        runOne $file
    done
fi



