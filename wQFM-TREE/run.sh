#!/bin/bash

geneTreesInputPath=$1
speciesTreeOutputPath=$2

# check if any of the above paths are empty
if [ -z "$geneTreesInputPath" ]
then
    echo "Please provide input path"
    exit 1
fi

# check if the output path is empty
if [ -z "$speciesTreeOutputPath" ]
then
    echo "Please provide output path"
    exit 1
fi

# check if the input file exists
if [ ! -f "$geneTreesInputPath" ]
then
    echo "Input file $geneTreesInputPath does not exist"
    exit 1
fi


gtCleaned=$geneTreesInputPath-cleaned
consensusTreePath=$gtCleaned-cons

echo "Gene Trees path: $geneTreesInputPath, output path: $speciesTreeOutputPath"

echo "Cleaning input file"

python3 ./treeCleaner.py < $geneTreesInputPath > $gtCleaned

echo "Cleaned input file written to $gtCleaned"

echo "Generating consensus tree using paup"

perl run_paup_consensus.pl -i $gtCleaned -o $consensusTreePath 

echo " ===================== Running wQFM-TREE ===================== "

java -jar wQFM-TREE.jar $gtCleaned $consensusTreePath.greedy.tree $speciesTreeOutputPath "A"

