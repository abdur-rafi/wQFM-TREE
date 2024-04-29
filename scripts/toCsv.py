import pandas as pd

# Read the CSV file
df = pd.read_csv('data.csv', header=None)

# transpose the dataframe
df = df.T
# write the dataframe to csv
# to csv without index column

df.to_csv('data_transposed.csv', header=False, index = False)
