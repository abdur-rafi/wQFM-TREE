# declare -a allowed=("astral2-200-tree.txt" "astral2-50-tree.txt" "ewqfm-50-resolved-tree.txt" "ewqfm-200-resolved-tree.txt" "ewqfm-50-tree.txt" "ewqfm-200-tree.txt" "treeqmc-50-tree.txt" "treeqmc-200-tree.txt" "ewqfm-paup-nq-A-tree.txt"  "ewqfm-with-resolved-gene-trees-tree.txt" "treeqmc-tree.txt" )
# declare -a allowed=("ewqfm-paup-nq-A-tree.txt")
# declare -a allowed=("astral-tree.txt")
declare -a allowed=("sp-cleaned")
root=$1
for folder in $(ls $root); do
    echo $folder
    for folder2 in $(ls $root/$folder); do
        for files in $(ls $root/$folder/$folder2); do
            if [[ ! " ${allowed[@]} " =~ " ${files} " ]]; then
            :
                echo "Deleting $files"
                # Remove the file
                rm "$root/$folder/$folder2/$files"
            fi
            # if [[ $files == "ewqfm-puap-nq-A-tree.txt" ]]; then
            #     echo "Renaming $files"
            #     # Rename the file
            #     mv "$root/$folder/$folder2/$files" "$root/$folder/$folder2/ewqfm-paup-nq-A-tree.txt"
            # fi
            # rename $file
        done
    done
done
# for item in *; do
#     if [ -d "$item" ]; then
#         # Add your code here to process each directory
#         echo "Processing directory: $item"
#         cd "./$item"
#         for file in *; do
#             cd "./$file"
#             for file2 in *;do
#                 if [[ ! " ${allowed[@]} " =~ " ${file2} " ]]; then
#                     echo "Deleting $file2"
#                     # Remove the file
#                     rm "$file2"
#                 fi
#             done
#             cd ..
#             # Add your code here to process each file
#             # echo "Processing file: $file"
#             # rm "$file/astral2-200-tree.txt"
#             echo $file
#         done
#         cd ..
#     fi
# done
