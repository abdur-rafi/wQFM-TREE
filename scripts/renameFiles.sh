#!/bin/bash

# Check if a directory is provided
root=results/Astral_2_datasets/scores-by-model-cond
# for model in $(ls $root); do
for model in model.1000.2000000.0.000001 ; do
    for file in $(ls $root/$model); do
        new_filename="${file/-tree.txt/-score.txt}"

        echo "Renaming $file to $new_filename"
        mv $root/$model/$file $root/$model/$new_filename
    done
done

# The directory is the first argument
# directory=$1

# # Loop through files ending with '-tree.txt' in the specified directory
# for file in "$directory"/*-tree.txt; do
#     # Check if the file exists to avoid errors in case of no matching files
#     if [ -e "$file" ]; then
#         # Replace '-tree.txt' with '-score.txt' in the filename
#         new_filename="${file/-tree.txt/-score.txt}"

#         # Rename the file
#         mv "$file" "$new_filename"
#     fi
# done

# echo "Renaming complete."
