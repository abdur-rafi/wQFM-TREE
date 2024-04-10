# bash ./scripts/cleangt.sh ../astral-pro-10-25/n25/k1000/dup2/loss1/ils70
# bash ./scripts/cleangt.sh ../astral-pro-10-25/n25/k1000/dup2/loss0.5/ils70
# bash ./scripts/cleangt.sh ../astral-pro-10-25/n25/k1000/dup2/loss0.1/ils70
# bash ./scripts/cleangt.sh ../AstralPro/n100/k1000/dup5/loss1/ils70
# bash ./scripts/cleangt.sh ../AstralPro/n250/k1000/dup5/loss1/ils70
# bash ./scripts/cleangt.sh ../AstralPro/n500/k1000/dup5/loss1/ils70

root=$1
copyTo=$2
geneTreeLabel="e100.tre"
geneTreeLabelCleaned="e100-cleaned.tre"
resolved="e100-resolved.tre"
speciesTreeLabel="s_tree.trees"
speciesTreeLabelCleaned="s_tree-cleaned.tre"
apro="e100_apro.tre"
aproCleaned="e100_apro-cleaned.tre"
wqfm="e100-wqfm-tree.tre"

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
}


rfScoreApro(){
    for file in $(ls $root); do

    # check if $file is a directory
        if [ -d $root/$file ]; then
            # echo $file
            python ./rfScoreCalculator/getFpFn.py -t $root/$file/$aproCleaned -e $root/$file/$speciesTreeLabelCleaned
            # break
        fi

    done
}

rfScoreWqfm(){
    for file in $(ls $root); do

    # check if $file is a directory
        if [ -d $root/$file ]; then
            # echo $file
            python ./rfScoreCalculator/getFpFn.py -e $root/$file/$wqfm -t $root/$file/$speciesTreeLabelCleaned
            # break
        fi

    done
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
# rfScoreWqfm
resolveGT
