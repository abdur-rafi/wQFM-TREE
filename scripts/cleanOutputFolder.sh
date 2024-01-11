declare -a allowed=("astral2-200-tree.txt" "ewqfm-A-200-tree.txt")
for item in *; do
    if [ -d "$item" ]; then
        # Add your code here to process each directory
        echo "Processing directory: $item"
        cd "./$item"
        for file in *; do
            cd "./$file"
            for file2 in *;do
                if [[ ! " ${allowed[@]} " =~ " ${file2} " ]]; then
                    echo "Deleting $file2"
                    # Remove the file
                    rm "$file2"
                fi
            done
            cd ..
            # Add your code here to process each file
            # echo "Processing file: $file"
            # rm "$file/astral2-200-tree.txt"
            echo $file
        done
        cd ..
    fi
done
