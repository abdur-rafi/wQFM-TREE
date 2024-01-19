bash ./scripts/printScore.sh $1 > 48.txt
python ./scripts/plotter.py 48.txt ./img.png
# rm tmp.txt