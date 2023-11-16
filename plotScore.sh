bash printScore.sh $1 > tmp.txt
python plotter.py tmp.txt ./comparison/$2
rm tmp.txt