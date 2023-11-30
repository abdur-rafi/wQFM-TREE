bash ./scripts/printScore.sh $1 > tmp.txt
python ./scripts/plotter.py tmp.txt ./comparison/$2
rm tmp.txt