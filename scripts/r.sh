bash ./scripts/scoreFromOutput.sh ../run/astral2 model.200.500000.0.0000001 
bash ./scripts/scoreFromOutput.sh ../run/astral2 model.200.2000000.0.000001 
bash ./scripts/scoreFromOutput.sh ../run/astral2 model.200.2000000.0.0000001 
bash ./scripts/scoreFromOutput.sh ../run/astral2 model.200.10000000.0.000001 
bash ./scripts/scoreFromOutput.sh ../run/astral2 model.200.10000000.0.0000001 

# bash ./scripts/timeLogsFilter.sh ../timelogs astral "ASTRAL finished in" ../timelogs-cleaned
# bash ./scripts/timeLogsFilter.sh ../timelogs ewqfm "CPU time used" ../timelogs-cleaned
# bash ./scripts/timeLogsFilter.sh ../timelogs resolved.log "CPU time used" ../timelogs-cleaned
# bash ./scripts/timeLogsFilter.sh ../timelogs treeqmc.log "Execution time" ../timelogs-cleaned yes
# root=./results/Astral_2_datasets/outputs/model.1000.2000000.0.000001/22/
# for file in $(ls $root); do 
#     echo $file
#     python ./rfScoreCalculator/getFpFn.py -e $root/$file -t ./results/Astral_2_datasets/true-specis-trees/model.1000.2000000.0.000001/22/sp-cleaned

# done
