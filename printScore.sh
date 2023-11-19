

file1="avg-ewqfm-%sat-singleton-c-f-n-f-v2-score.txt"
# file2="avg-ewqfm-c-f-v2-score.txt"
# file2="wqfm-v1.2-scores-avg.txt"
file2="avg-wqfm-v1.2-all-scores.txt"

# file2="avg-ewqfm-c-f-n-f-v2-score.txt"

echo "file1 : $file1"
echo "file2 : $file2"

for file in $(ls $1)
# for file in "1X-500-500"
do
    # echo $file
    # check if file is a directory
    if [ -d $1/$file ]
    then
        echo $file
        # echo "ewqfm, wqfm"
        cat $1/$file/$file1 $1/$file/$file2
    fi
done

# /usr/bin/env /usr/lib/jvm/java-17-openjdk-amd64/bin/java -XX:+ShowCodeDetailsInExceptionMessages -cp /home/abdur-rafi/.config/Code/User/workspaceStorage/da91ba3e148e5727246c82da7f9911d2/redhat.java/jdt_ws/E-WQFM_731a4073/bin src.Main 