#!/bin/bash

# Check the number of arguments
if [ "$#" -lt 3 ]; then
    echo "Less than three arguments provided."
    start=1
    end=50
else
    start=$(printf "%02d" $3)
    end=$(printf "%02d" $(( $# - 1 )))s
fi

# Loop starting from the determined start value
for (( i=start; i<=end; i++ )); do
    echo "Argument $(printf "%02d" $i): ${!i}"
done