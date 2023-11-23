bash printScore.sh $1 > tmp.txt
python3 plotter.py tmp.txt ./comparison/$2
rm tmp.txt