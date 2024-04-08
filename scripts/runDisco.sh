# bash ./scripts/runDisco.sh ../astral-pro-10-25/n25/k1000/dup2/loss1/ils70

root=$1
geneTreeLabelCleaned="e100-resolved.tre"
discoOutput="e100-disco-decomp.tre"
discoCleaned="e100-disco-decomp-cleaned.tre"
discoNoDecomp="e100-disco-rooted.tre"
discoNoDecompCleaned="e100-disco-rooted-cleaned.tre"
consensusTree="e100-consensus"
outputFile="e100-wqfm-tree.tre"


# create a function
runDiscoAndCreateCons() {
    for file in $(ls $root); do
        if [ -d $root/$file ]; then
            echo $file
            python ./scripts/disco.py -i $root/$file/$geneTreeLabelCleaned -o $root/$file/$discoOutput -d _
            python ./scripts/treeCleaner.py < $root/$file/$discoOutput > $root/$file/$discoCleaned
            perl ./scripts/run_paup_consensus.pl -i $root/$file/$discoCleaned -o $root/$file/$consensusTree > consLog.txt 2>consErr.txt
            python ./scripts/disco.py -i $root/$file/$geneTreeLabelCleaned -o $root/$file/$discoNoDecomp -d _ --no-decomp
            python ./scripts/treeCleaner.py < $root/$file/$discoNoDecomp > $root/$file/$discoNoDecompCleaned


            # break
        fi

    done
}

# runDiscoAndCreateCons


runWqfmTree(){
    for file in $(ls $root); do
        if [ -d $root/$file ]; then
            echo $file
            /usr/bin/env /usr/lib/jvm/java-17-openjdk-amd64/bin/java -XX:+ShowCodeDetailsInExceptionMessages -cp /home/abdur-rafi/.config/Code/User/workspaceStorage/da91ba3e148e5727246c82da7f9911d2/redhat.java/jdt_ws/E-WQFM_731a4073/bin src.Main $root/$file/$discoNoDecompCleaned $root/$file/$consensusTree.greedy.tree $root/$file/$outputFile 

            # /usrodeDetailsInExceptionMessages -cp /home/abdur-rafi/.config/Code/User/workspaceStorage/da91ba3e148e5727246c82da7f9911d2/redhat.java/jdt_ws/E-WQFM_731a4073/bin src.Main $root/$file/$geneTreeLabelCleaned $root/$file/$consensusTree $root/$file/$outputFile 
            # break;
        fi
    done
}

runWqfmTree

