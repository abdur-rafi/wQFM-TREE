declare -a allowed=("ewqfm-with-resolved-gene-trees-score.txt" "avg-ewqfm-with-resolved-gene-trees-score.txt")
for item in *; do
    if [ -d "$item" ]; then
        # Add your code here to process each directory
        echo "Processing directory: $item"
        cd "./$item"
        for file in *; do
            if [[ ! " ${allowed[@]} " =~ " ${file} " ]]; then
                echo "Deleting $file"
                # Remove the file
                rm "$file"
            fi
        done
        cd ..
    fi
done
