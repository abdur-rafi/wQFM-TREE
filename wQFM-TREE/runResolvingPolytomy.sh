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


resolvedPolytomyPath=$geneTreesInputPath.resolved

echo "Resolving Polytomy"

python arb_resolve_polytomies.py $geneTreesInputPath

echo "Resolved gene trees written to $resolvedPolytomyPath"

echo "Running run.sh script"

bash run.sh $resolvedPolytomyPath $speciesTreeOutputPath

