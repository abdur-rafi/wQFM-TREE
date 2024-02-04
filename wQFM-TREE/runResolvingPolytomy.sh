geneTreesInputPath=$1
speciesTreeOutputPath=$2
gtCleaned=$geneTreesInputPath-cleaned
consensusTreePath=$gtCleaned-cons

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

echo "Gene Trees path: $geneTreesInputPath, output path: $speciesTreeOutputPath"


echo "Cleaning input file"

python3 ./treeCleaner.py < $geneTreesInputPath > $gtCleaned

echo "Cleaned input file written to $gtCleaned"


resolvedPolytomyPath=$gtCleaned.resolved

echo "Resolving Polytomy"

python3 arb_resolve_polytomies.py $gtCleaned
python ./treeCleaner.py < $resolvedPolytomyPath > $resolvedPolytomyPath-cleaned
rm $resolvedPolytomyPath
echo "Resolved gene trees written to $resolvedPolytomyPath-cleaned"



echo "Generating consensus tree using paup"

perl run_paup_consensus.pl -i $gtCleaned -o $consensusTreePath 

echo " ===================== Running wQFM-TREE ===================== "

java -jar wQFM-TREE.jar $resolvedPolytomyPath-cleaned $consensusTreePath.greedy.tree $speciesTreeOutputPath "A"


