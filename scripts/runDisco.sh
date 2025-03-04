# bash ./scripts/runDisco.sh ../astral-pro-10-25/n25/k1000/dup2/loss1/ils70
# bash ./scripts/runDisco.sh ../AstralPro/n25/k1000/dup5/loss1/ils70 1
# bash ./scripts/runDisco.sh ../AstralPro/n25/k1000/dup0/loss0/ils0 1
# bash ./scripts/runDisco.sh ../AstralPro/n10/k1000/dup5/loss1/ils70 1
# bash ./scripts/runDisco.sh ../AstralPro/n500/k1000/dup5/loss1/ils70 5

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
outputFile="e${n}00-updated.tre"
wqfm2020OutputFile="e${n}00-wqfm-2020-norm.tre"
quartetFile=sqQuartetsNormed.txt
# outputFile="e${n}00-wqfm-with-sp.tre"
wscores="e${n}00_wqfm_scores_updated.txt"
# wscores="e${n}00_wqfm_scores.txt"
w2020scores="e${n}00_wqfm_2020_norm_scores.txt"
wscoresAvg="avg-$wscores"
w2020scoresAvg="avg-$w2020scores"


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
        if [[ -d $root/$file ]]; then
            echo $file
            
            if [ "$file" -lt 4 ]; then
                continue
            fi
            # /usr/bin/env /usr/lib/jvm/java-11-openjdk-amd64/bin/java @/tmp/cp_8ydn8rvu0b5v0bzf1np8e9l5p.argfile src.Main \
            # /usr/bin/env /usr/lib/jvm/java-17-openjdk-amd64/bin/java -XX:+ShowCodeDetailsInExceptionMessages \
            # -cp /home/abdur-rafi/.config/Code/User/workspaceStorage/da91ba3e148e5727246c82da7f9911d2/redhat.java/jdt_ws/E-WQFM_731a4073/bin \
            # src.Main \
            /usr/bin/env /usr/lib/jvm/java-11-openjdk-amd64/bin/java @/tmp/cp_6k5sgswqypnoi0yc4hoco19yx.argfile src.Main \
            $root/$file/$discoNoDecompCleaned \
            $root/$file/$consensusTree \
            $root/$file/$outputFile 

            # break;
        fi
    done
}

runWqfm2020(){
    for file in $(ls $root); do
        if [ -d $root/$file ]; then
            echo $file
            
            # if [ $file -lt 21 ]; then
            #     continue
            # fi
            java -jar wQFM-v1.4.jar -i $root/$file/$quartetFile -o $root/$file/$wqfm2020OutputFile

            # break;
        fi
    done
}

rfScoreWqfm(){

    > $root/$wscores
    > $root/$wscoresAvg
    

    for file in $(ls $root); do

    # check if $file is a directory
        if [ -d $root/$file ]; then
            echo $file
            python ./rfScoreCalculator/getFpFn.py -e $root/$file/$outputFile -t $root/$file/$speciesTreeLabelCleaned >> $root/$wscores
            # break
        fi

    done

    python ./scripts/rfAverager.py < $root/$wscores > $root/$wscoresAvg
}

rfScoreWqfm2020(){

    > $root/$w2020scores
    > $root/$w2020scoresAvg
    

    for file in $(ls $root); do

    # check if $file is a directory
        if [ -d $root/$file ]; then
            # echo $file
            python ./rfScoreCalculator/getFpFn.py -e $root/$file/$wqfm2020OutputFile -t $root/$file/$speciesTreeLabelCleaned >> $root/$w2020scores
            # break
        fi

    done

    python ./scripts/rfAverager.py < $root/$w2020scores > $root/$w2020scoresAvg
    python ./scripts/rfAverager.py < $root/$w2020scores
}

generateSqQuartets(){
    for file in $(ls $root); do

    # check if $file is a directory
        if [ -d $root/$file ]; then
            echo $file
            # python ./scripts/arb_resolve_polytomies.py $root/$file/$geneTreeLabelCleaned
            # python ./scripts/treeCleaner.py < $root/$file/$geneTreeLabelCleaned.resolved > $root/$file/$resolved
            java -jar -Xmx60g genSQNorm-v2.jar $root/$file/$discoNoDecompCleaned > $root/$file/$quartetFile
            # rm $root/$file/$quartetFile
            # break
        fi

    done
}

# runWqfmTree
generateSqQuartets
runWqfm2020
rfScoreWqfm2020
# runDiscoAndCreateCons
# runWqfmTree
# rfScoreWqfm