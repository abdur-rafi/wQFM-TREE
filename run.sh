
# cd $1
# list current dir and run a for loop
for file in $(ls $1)
do
    # echo $file
    # check if file is a directory
    if [ -d $1/$file ]
    then
        # if directory then run the script recursively
        echo $file
        # for loop for files inside the $file directory
        for file2 in $(ls $1/$file)
        do
            # echo $file2
            # check if file is a directory
            if [ -d $1/$file/$file2 ]
            then
                # if directory then run the script recursively
                echo $file2
                /usr/bin/env /usr/lib/jvm/java-17-openjdk-amd64/bin/java -XX:+ShowCodeDetailsInExceptionMessages -cp /home/abdur-rafi/.config/Code/User/workspaceStorage/da91ba3e148e5727246c82da7f9911d2/redhat.java/jdt_ws/E-WQFM_731a4073/bin src.Main < $1/$file/$file2/all_gt.tre > $1/$file/$file2/ewqfm-out.txt
                python ./output/getFpFn.py -t $1/true_tree_trimmed -e $1/$file/$file2/ewqfm-out.txt > $1/$file/$file2/ewqfm-score.txt
            fi
        done
    fi
done

# /usr/bin/env /usr/lib/jvm/java-17-openjdk-amd64/bin/java -XX:+ShowCodeDetailsInExceptionMessages -cp /home/abdur-rafi/.config/Code/User/workspaceStorage/da91ba3e148e5727246c82da7f9911d2/redhat.java/jdt_ws/E-WQFM_731a4073/bin src.Main 