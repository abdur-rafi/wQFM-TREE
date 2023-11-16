

file1="avg-ewqfm-sp.5-c-f-n-f-v2-score.txt"
# file1="avg-ewqfm-%sat-c-f-n-f-v2-score.txt"
file1="avg-ewqfm-%sat-c-f-n-n-v2-score.txt"
file1="avg-ewqfm-%sat-c-n-n-n-v2-score.txt"


file2="avg-ewqfm-c-f-v2-score.txt"
file2="wqfm-v1.2-scores-avg.txt"
file2="avg-ewqfm-c-f-n-f-v2-score.txt"

echo "$file1"
echo "$file2"

for file in $(ls $1)
do
    if [ -d $1/$file ]
    then
        echo $file
        cat $1/$file/$file1 $1/$file/$file2
    fi
done
