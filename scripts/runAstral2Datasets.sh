
filePrefix="ewqfm"


rootPath=$1
modelCond=$2
gtFolderPath=$rootPath/true-gene-trees/$modelCond
spFolderPath=$rootPath/true-specis-trees/$modelCond
consFolderPath=$rootPath/true-consensus-trees/$modelCond
outFolderPath=$rootPath/output/$modelCond
scoresFolderPath=$rootPath/scores/$modelCond



geneTreeLabel=truegenetrees
speciesTreeLabel=s_tree.trees
consTreeLabel=cons.tre
geneTreeLabelCleaned=gt-cleaned
speciesTreeLabelCleaned=sp-cleaned

# generate consensus trees

for file in $(ls $gtFolderPath)
do
    mkdir -p $consFolderPath/$file
    ./raxml-ng --redo --consense MRE --tree $gtFolderPath/$file/$geneTreeLabel --prefix $consFolderPath/$file/cons > ./raxml-ng.log
    consOut=$consFolderPath/$file/cons.raxml.consensusTreeMRE
    python ./scripts/consensusCleaner.py < $consOut > $consFolderPath/$file/$consTreeLabel
done

# clean gene and species trees

for file in $(ls $gtFolderPath)
do
    python ./scripts/treeCleaner.py < $gtFolderPath/$file/$geneTreeLabel > $gtFolderPath/$file/$geneTreeLabelCleaned
    python ./scripts/treeCleaner.py < $spFolderPath/$file/$speciesTreeLabel > $spFolderPath/$file/$speciesTreeLabelCleaned
done


mkdir -p $scoresFolderPath

> $scoresFolderPath/$filePrefix-score.txt


for file in $(ls $gtFolderPath)
do 
    gtPath=$gtFolderPath/$file/$geneTreeLabelCleaned
    consPath=$consFolderPath/$file/$consTreeLabel
    spPath=$spFolderPath/$file/$speciesTreeLabelCleaned

    mkdir -p $outFolderPath/$file

    outPath=$outFolderPath/$file/$filePrefix-tree.txt

    /usr/bin/env /usr/lib/jvm/java-17-openjdk-amd64/bin/java  -XX:+ShowCodeDetailsInExceptionMessages -cp /home/abdur-rafi/.config/Code/User/workspaceStorage/da91ba3e148e5727246c82da7f9911d2/redhat.java/jdt_ws/E-WQFM_731a4073/bin src.Main $gtPath $consPath $outPath

    python ./rfScoreCalculator/getFpFn.py -t $spPath -e $outPath >> $scoresFolderPath/$filePrefix-score.txt

done

python ./scripts/rfAverager.py < $scoresFolderPath/$filePrefix-score.txt > $scoresFolderPath/avg-$filePrefix-score.txt


# for file in $(ls $1)
# # for file in "1X-500-500"
# do
#     # echo $file
#     # check if file is a directory
#     if [ -d $1/$file ]
#     then
#         # if directory then run the script recursively
#         echo $file
#         # for loop for files inside the $file directory
#         # > $1/$file/wqfm-v1.2-scores.txt
#         # > $1/$file/ewqfm-cons-flat-norm-flat-score.txt
#         > $1/$file/$filePrefix-score.txt


#         for file2 in $(ls $1/$file)
#         do
#             # check if file is a directory
#             if [ -d $1/$file/$file2 ]
#             then
#                 echo $file2
#                 # if directory then run the script recursively
#                 # echo $file2
#                 # ./raxml-ng --redo --consense MRE --tree $1/$file/$file2/all_gt.tre --prefix $1/$file/$file2/cons >> ./raxml-ng.log
#                 # consOut=$1/$file/$file2/cons.raxml.consensusTreeMRE
#                 # python consensusCleaner.py < $consOut > $1/$file/$file2/cons.tre
#                 # python treeCleaner.py < $1/$file/$file2/all_gt.tre > $1/$file/$file2/all_gt_cleaned.tre 
#                 /usr/bin/env /usr/lib/jvm/java-17-openjdk-amd64/bin/java  -XX:+ShowCodeDetailsInExceptionMessages -cp /home/abdur-rafi/.config/Code/User/workspaceStorage/da91ba3e148e5727246c82da7f9911d2/redhat.java/jdt_ws/E-WQFM_731a4073/bin src.Main $1/$file/$file2/all_gt_cleaned.tre $1/$file/$file2/cons.tre $1/$file/$file2/$filePrefix-tree.txt
#                 python ./rfScoreCalculator/getFpFn.py -t $1/true_tree_trimmed -e $1/$file/$file2/$filePrefix-tree.txt >> $1/$file/$filePrefix-score.txt
#                 # python ./rfScoreCalculator/getFpFn.py -t $1/true_tree_trimmed -e $1/$file/$file2/wQFM-v1.2-best.tre >> $1/$file/wqfm-v1.2-scores.txt
#             fi
#         done
#         # python ./rfAverager.py < $1/$file/wqfm-v1.2-scores.txt > $1/$file/wqfm-v1.2-scores-avg.txt
#         python ./rfAverager.py < $1/$file/$filePrefix-score.txt > $1/$file/avg-$filePrefix-score.txt
#         echo "$file done"
#         # break
#     fi
# done

# /usr/bin/env /usr/lib/jvm/java-17-openjdk-amd64/bin/java -XX:+ShowCodeDetailsInExceptionMessages -cp /home/abdur-rafi/.config/Code/User/workspaceStorage/da91ba3e148e5727246c82da7f9911d2/redhat.java/jdt_ws/E-WQFM_731a4073/bin src.Main 