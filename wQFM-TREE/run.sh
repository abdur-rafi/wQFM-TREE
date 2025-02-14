#!/bin/bash
set -e  # Exit on any error

function check_inputs() {
    if [ -z "$1" ] || [ -z "$2" ]; then
        echo "Usage: $0 <gene_trees_input_path> <species_tree_output_path>"
        exit 1
    fi

    if [ ! -f "$1" ]; then
        echo "Input file $1 does not exist"
        exit 1
    fi
}

function clean_trees() {
    local input=$1
    local output=$2
    
    echo "Cleaning input file"
    python3 ./treeCleaner.py < "$input" > "$output"
    if [ $? -ne 0 ]; then
        echo "Error: Tree cleaning failed"
        exit 1
    fi
    echo "Cleaned input file written to $output"
}

function generate_consensus() {
    local input=$1
    local output=$2

    echo "Generating consensus tree using paup"
    perl run_paup_consensus.pl -i "$input" -o "$output"
    if [ $? -ne 0 ]; then
        echo "Error: Consensus generation failed"
        exit 1
    fi
}

function run_wqfm() {
    local cleaned_trees=$1
    local consensus_tree=$2
    local output=$3

    echo " ===================== Running wQFM-TREE ===================== "
    java -jar wQFM-TREE.jar "$cleaned_trees" "$consensus_tree.greedy.tree" "$output" "A"
    if [ $? -ne 0 ]; then
        echo "Error: wQFM-TREE execution failed"
        exit 1
    fi
}

function main() {
    local geneTreesInputPath=$1
    local speciesTreeOutputPath=$2

    check_inputs "$geneTreesInputPath" "$speciesTreeOutputPath"

    local gtCleaned="$geneTreesInputPath-cleaned"
    local consensusTreePath="$gtCleaned-cons"

    echo "Gene Trees path: $geneTreesInputPath, output path: $speciesTreeOutputPath"

    clean_trees "$geneTreesInputPath" "$gtCleaned"
    generate_consensus "$gtCleaned" "$consensusTreePath"
    run_wqfm "$gtCleaned" "$consensusTreePath" "$speciesTreeOutputPath"
}

main "$1" "$2"