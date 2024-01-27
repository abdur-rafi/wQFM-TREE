#!/bin/bash

geneTreesInputPath=$1
speciesTreeOutputPath=$2
gtCleaned=$geneTreesInputPath-cleaned
consensusTreePath=$gtCleaned-cons
echo "Gene Trees path $geneTrees, output path $speciesTreeOutputPath"

echo "Cleaning input file"

python ./treeCleaner.py < $geneTreesInputPath > $gtCleaned

echo "Cleaned input file written to $gtCleaned"

echo "Generating consensus tree using paup"

perl run_paup_consensus.pl -i $gtCleaned -o $consensusTreePath 

echo "Running wqfm-TREE"

java -jar E-WQFM.jar $gtCleaned $consensusTreePath.greedy.tree $speciesTreeOutputPath "A"

echo "wqfm-TREE run complete"