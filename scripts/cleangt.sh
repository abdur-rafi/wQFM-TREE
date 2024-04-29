# bash ./scripts/cleangt.sh ../astral-pro-10-25/n25/k1000/dup2/loss1/ils70
# bash ./scripts/cleangt.sh ../astral-pro-10-25/n25/k1000/dup2/loss0.5/ils70
# bash ./scripts/cleangt.sh ../astral-pro-10-25/n25/k1000/dup2/loss0.1/ils70
# bash ./scripts/cleangt.sh ../AstralPro/n100/k1000/dup5/loss1/ils70
# bash ./scripts/cleangt.sh ../AstralPro/n250/k1000/dup5/loss1/ils70
# bash ./scripts/cleangt.sh ../AstralPro/n500/k1000/dup5/loss1/ils70

# bash ./scripts/cleangt.sh ../AstralPro/n25/k1000/dup5/loss1/ils70 1

root=$1
n=$2
# copyTo=$2
geneTreeLabel="e${n}00.tre"
geneTreeLabelCleaned="e${n}00-cleaned.tre"
resolved="e${n}00-resolved.tre"
speciesTreeLabel="s_tree.trees"
speciesTreeLabelCleaned="s_tree-cleaned.tre"
apro="e${n}00_apro.tre"
aproCleaned="e${n}00_apro-cleaned.tre"
wqfm="e${n}00-wqfm-tree.tre"
wqfm="e${n}00-wqfm-with-astral.tre"
wqfm="e${n}00-wqfm-rn.tre"
ascores="e${n}00_apro_scores.txt"
wscores="e${n}00_wqfm_scores_rn.txt"
ascoresAvg="avg-$ascores"
wscoresAvg="avg-$wscores"


cleanGT(){
    for file in $(ls $root); do

    # check if $file is a directory
        if [ -d $root/$file ]; then
            echo $file
            python ./scripts/treeCleaner.py < $root/$file/$geneTreeLabel > $root/$file/$geneTreeLabelCleaned
            # python ./scripts/arb_resolve_polytomies.py $root/$file/$geneTreeLabelCleaned
            # python ./scripts/treeCleaner.py < $root/$file/$geneTreeLabelCleaned.resolved > $root/$file/$resolved
            # break
        fi

    done
}

resolveGT(){
    for file in $(ls $root); do

    # check if $file is a directory
        if [ -d $root/$file ]; then
            echo $file
            python ./scripts/arb_resolve_polytomies.py $root/$file/$geneTreeLabelCleaned
            python ./scripts/treeCleaner.py < $root/$file/$geneTreeLabelCleaned.resolved > $root/$file/$resolved
            # break
        fi

    done

}


cleanSP(){
    for file in $(ls $root); do

    # check if $file is a directory
        if [ -d $root/$file ]; then
            echo $file
            python ./scripts/treeCleaner.py < $root/$file/$speciesTreeLabel > $root/$file/$speciesTreeLabelCleaned
            # break
        fi

    done
}

cleanApro(){

    for file in $(ls $root); do

    # check if $file is a directory
        if [ -d $root/$file ]; then
            echo $file
            python ./scripts/treeCleaner.py < $root/$file/$apro > $root/$file/$aproCleaned
            # break
        fi

    done

    # python ./scripts/averager.py < $root/$ascores > $root/$ascoresAvg
}


rfScoreApro(){

    > $root/$ascores
    > $root/$avgAscores

    for file in $(ls $root); do

    # check if $file is a directory
        if [ -d $root/$file ]; then
            # echo $file
            python ./rfScoreCalculator/getFpFn.py -t $root/$file/$aproCleaned -e $root/$file/$speciesTreeLabelCleaned >> $root/$ascores
            # break
        fi

    done

    python ./scripts/rfAverager.py < $root/$ascores > $root/$ascoresAvg
}

rfScoreWqfm(){

    > $root/$wscores
    > $root/$avgWscores
    

    for file in $(ls $root); do

    # check if $file is a directory
        if [ -d $root/$file ]; then
            # echo $file
            python ./rfScoreCalculator/getFpFn.py -e $root/$file/$wqfm -t $root/$file/$speciesTreeLabelCleaned >> $root/$wscores
            # break
        fi

    done

    python ./scripts/rfAverager.py < $root/$wscores > $root/$wscoresAvg
}


copyToDir(){
    for file in $(ls $root); do

    # check if $file is a directory
        if [ -d $root/$file ]; then
            # make the dir in copy dir
            mkdir -p $copyTo/$file
            echo $file
            cp $root/$file/$geneTreeLabelCleaned $copyTo/$file/$geneTreeLabelCleaned
            # cp $root/$file/$resolved $copyTo/$file/$resolved
            cp $root/$file/$speciesTreeLabelCleaned $copyTo/$file/$speciesTreeLabelCleaned
            cp $root/$file/$aproCleaned $copyTo/$file/$aproCleaned
            # cp $root/$file/$wqfm $copyTo/$file/$wqfm
            # break
        fi

    done

}

# cleanGT
# cleanSP
# cleanApro
# rfScoreApro
rfScoreWqfm
# resolveGT
