# ./raxml-ng --consense MRE --tree ./input/gtree_11tax_est_5genes_R1.tre --prefix ./input/test
# ./raxml-ng --consense MRE --tree ./run/37-taxon/noscale.25g.500b/R1/all_gt.tre --prefix ./input/test

# geneTrees=./input/gtree_11tax_est_5genes_R1.tre
geneTrees=../run/07.trueGT.cleaned
consPrefix=./input/101
consOutputFile=$consPrefix.raxml.consensusTreeMRE
consCleanedFile=$consPrefix.raxml.consensusTreeMRE.cleaned
outputFile=./out.txt
logFile=./log.txt
wqfmOutputFile=./out.txt
wqfmScoreFile=./score.txt

./raxml-ng --redo --consense MRE --tree $geneTrees --prefix $consPrefix >> $logFile

python consensusCleaner.py < $consOutputFile > $consCleanedFile

/usr/bin/env /usr/lib/jvm/java-17-openjdk-amd64/bin/java -XX:+ShowCodeDetailsInExceptionMessages -cp /home/abdur-rafi/.config/Code/User/workspaceStorage/da91ba3e148e5727246c82da7f9911d2/redhat.java/jdt_ws/E-WQFM_731a4073/bin src.v2.Main $geneTrees $consCleanedFile $wqfmOutputFile
python ./rfScoreCalculator/getFpFn.py -t ./input/custom.true.tre -e $wqfmOutputFile > $wqfmScoreFile

# python ./rfScoreCalculator/getFpFn.py -t ./input/gtree_11tax_est_5genes_R1.tre -e ./out.txt
