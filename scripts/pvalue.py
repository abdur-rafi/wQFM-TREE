import pandas as pd
# read command line argument
import sys
from scipy.stats import wilcoxon


fileDir = sys.argv[1]

# Read the CSV file
df = pd.read_csv(fileDir)

modelConds = ['200.500K.6','200.500K.7','200.2M.6','200.2M.7','200.10M.6','200.10M.7','500.2M.6','1000.2M.6']

comps1s = ['wqfm', 'wqfm-resolved']
comps2s = ['astral', 'treeqmc']

for modelCond in modelConds:
    for comp1 in comps1s:
        for comp2 in comps2s:
            # perform wilcoxon test
            c1 = f"{modelCond}-{comp1}"
            c2 = f"{modelCond}-{comp2}"
            v1 = df[c1].dropna()
            v2 = df[c2].dropna()
            # print(v1)
            # print(v2)
            stat, p = wilcoxon(v1, v2)

            print(f"Model: {modelCond:30} {comp1} vs {comp2} p-value: {p}")

# for col in df.columns:
#     print(f"Column: {col:30} Mean:{df[col].mean()}")

# # perform wilcoxon test
# stat, p = wilcoxon(df[comp1], df[comp2])
# print(f"Model: {modelCond:30} {comp1} vs {comp2} p-value: {p}")