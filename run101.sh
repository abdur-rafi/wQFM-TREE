
# filePrefix="ewqfm-%sat-lvs-c-n-n-n-v2"
# filePrefix="ewqfm-%sat-lvs-mxpart-c-n-n-n-v2"
filePrefix="ewqfm"
# filePrefix="ewqfm-all-n-mxscore-init-singleton"


path=$1

gtFolderPath=$path/gene-trees-true

gtFolderCleanedPath=$gtFolderPath-cleaned

# mkdir $gtFolderCleanedPath

# for file in $(ls $gtFolderPath)
# do
#     python treeCleaner.py < $gtFolderPath/$file > $gtFolderCleanedPath/${file:0:2}

# done

consFolderPath=$path/consensus-trees
# mkdir $consFolderPath


# for file in $(ls $gtFolderCleanedPath)
# do
#     ./raxml-ng --redo --consense MRE --tree $gtFolderCleanedPath/$file --prefix $consFolderPath/$file >> ./raxml-ng.log
#     consOut=$consFolderPath/$file.raxml.consensusTreeMRE
#     python consensusCleaner.py < $consOut > $consFolderPath/${file:0:2}
# done




spFolderPath=$path/species-trees

spFolderCleanedPath=$spFolderPath-cleaned

# mkdir $spFolderCleanedPath

# for file in $(ls $spFolderPath)
# do
#     python treeCleaner.py < $spFolderPath/$file > $spFolderCleanedPath/${file:0:2}

# done

outputFolderPath=$path/output
mkdir $outputFolderPath

>$path/$filePrefix-score.txt

for file in $(ls $gtFolderCleanedPath)
do
    geneTrees=$gtFolderCleanedPath/$file
    speciesTree=$spFolderCleanedPath/$file
    consTree=$consFolderPath/$file
    output=$outputFolderPath/$file
    # echo ${file:0:2}
    /usr/bin/env /usr/lib/jvm/java-17-openjdk-amd64/bin/java  -XX:+ShowCodeDetailsInExceptionMessages -cp /home/abdur-rafi/.config/Code/User/workspaceStorage/da91ba3e148e5727246c82da7f9911d2/redhat.java/jdt_ws/E-WQFM_731a4073/bin src.Main $geneTrees $consTree $output
    python ./rfScoreCalculator/getFpFn.py -t $speciesTree -e $output >> $path/$filePrefix-score.txt

done

python ./rfAverager.py < $path/$filePrefix-score.txt > $path/avg-$filePrefix-score.txt




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