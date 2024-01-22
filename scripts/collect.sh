
fileName=wqfm-Tree-score.txt
fileName2=wqfm-v1.2-all-scores.txt
writeTo=./37.txt
root=$1
>$writeTo
for file in $(ls $root)
# for file in "1X-500-500"
do
    if [ -d $root/$file ]
    then
        echo $file

        if [ -d $root/$file ]
        then
            python ./scripts/scoreSep.py < $root/$file/$fileName >> $writeTo
            python ./scripts/scoreSep.py < $root/$file/$fileName2 >> $writeTo
        fi
    fi
done

# /usr/bin/env /usr/lib/jvm/java-17-openjdk-amd64/bin/java -XX:+ShowCodeDetailsInExceptionMessages -cp /home/abdur-rafi/.config/Code/User/workspaceStorage/da91ba3e148e5727246c82da7f9911d2/redhat.java/jdt_ws/E-WQFM_731a4073/bin src.Main 