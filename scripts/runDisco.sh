# bash ./scripts/runDisco.sh ../astral-pro-10-25/n25/k1000/dup2/loss1/ils70
# bash ./scripts/runDisco.sh ../AstralPro/n25/k1000/dup5/loss1/ils70 1

root=$1
n=$2
speciesTreeLabelCleaned="s_tree-cleaned.tre"
astralOutputFile="e${n}00_apro-cleaned.tre"
geneTreeLabelCleaned="e${n}00-resolved.tre"
discoOutput="e${n}00-disco-decomp.tre"
discoCleaned="e${n}00-disco-decomp-cleaned.tre"
discoNoDecomp="e${n}00-disco-rooted.tre"
discoNoDecompCleaned="e${n}00-disco-rooted-cleaned.tre"
consensusTreePrefix="e${n}00-consensus"
consensusTree=$consensusTreePrefix.greedy.tree
# consensusTree=$astralOutputFile
# consensusTree=$speciesTreeLabelCleaned
outputFile="e${n}00-wqfm-4.tre"
# outputFile="e${n}00-wqfm-with-sp.tre"


# create a function
runDiscoAndCreateCons() {
    for file in $(ls $root); do
        if [ -d $root/$file ]; then
            echo $file
            # continue if file less than 31
            
            python ./scripts/disco.py -i $root/$file/$geneTreeLabelCleaned -o $root/$file/$discoOutput -d _
            python ./scripts/treeCleaner.py < $root/$file/$discoOutput > $root/$file/$discoCleaned
            perl ./scripts/run_paup_consensus.pl -i $root/$file/$discoCleaned -o $root/$file/$consensusTreePrefix > consLog.txt 2>consErr.txt
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
            
            # if [ $file -lt 20 ]; then
            #     continue
            # fi
            /usr/bin/env /usr/lib/jvm/java-17-openjdk-amd64/bin/java -XX:+ShowCodeDetailsInExceptionMessages \
            -cp /home/abdur-rafi/.config/Code/User/workspaceStorage/da91ba3e148e5727246c82da7f9911d2/redhat.java/jdt_ws/E-WQFM_731a4073/bin \
            src.Main $root/$file/$discoNoDecompCleaned \
            $root/$file/$consensusTree \
            $root/$file/$outputFile 

            # break;
        fi
    done
}

runWqfmTree

