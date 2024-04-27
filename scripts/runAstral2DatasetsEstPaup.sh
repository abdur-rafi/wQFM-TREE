
filePrefix="wqfm-sp"


rootPath=$1
modelCond=$2
nonQuartType="A"
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

# # clean gene and species trees

# for file in $(ls $gtFolderPath)
# do
#     python ./scripts/treeCleaner.py < $gtFolderPath/$file/$geneTreeLabel > $gtFolderPath/$file/$geneTreeLabelCleaned
#     python ./scripts/treeCleaner.py < $spFolderPath/$file/$speciesTreeLabel > $spFolderPath/$file/$speciesTreeLabelCleaned
# done

>consLog.txt
>consErr.txt
# generate consensus trees


# for file in $(ls $gtFolderPath)
# do
#     echo "consensus tree for $file"

#     mkdir -p $consFolderPath/$file
#     # ./iqtree -t $gtFolderPath/$file/$geneTreeLabel -con >> consLog.txt 2>>consErr.txt
#     bash ./scripts/phylip.sh $gtFolderPath/$file/$geneTreeLabel > consLog.txt 2>consErr.txt
#     # consOut=$consFolderPath/$file/cons.iqtree
#     consOut=$consFolderPath/$file/cons.phyliptree

#     mv ./outtree $consOut
#     # ./raxml-ng --redo --consense MRE --tree $gtFolderPath/$file/$geneTreeLabel --prefix $consFolderPath/$file/cons > ./raxml-ng.log
#     python ./scripts/consensusCleaner.py < $consOut > $consFolderPath/$file/$consTreeLabel
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



mkdir -p $scoresFolderPath

> $scoresFolderPath/$filePrefix-score.txt

runOne() {

    file=$1
    gtPath=$gtFolderPath/$file/$geneTreeLabelCleaned
    consPath=$consFolderPath/$file/$consTreeLabel
    spPath=$spFolderPath/$file/$speciesTreeLabelCleaned

    mkdir -p $outFolderPath/$file

    outPath=$outFolderPath/$file/$filePrefix-tree.txt

    echo $file

    # /usr/bin/env /usr/lib/jvm/java-17-openjdk-amd64/bin/java -XX:+ShowCodeDetailsInExceptionMessages -cp \
    # /home/abdur-rafi/.config/Code/User/workspaceStorage/da91ba3e148e5727246c82da7f9911d2/redhat.java/jdt_ws/E-WQFM_731a4073/bin \
    # src.Main $gtPath $spPath $outPath $nonQuartType

    java -jar wQFM-Tree.jar $gtPath $spPath $outPath $nonQuartType

    python ./rfScoreCalculator/getFpFn.py -t $spPath -e $outPath >> $scoresFolderPath/$filePrefix-score.txt

    python ./scripts/rfAverager.py < $scoresFolderPath/$filePrefix-score.txt > $scoresFolderPath/avg-$filePrefix-score.txt

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



