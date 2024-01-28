# wQFM-TREE

This repository contains the official implementation of wQFM-TREE.

## Execution dependencies

### Packages, Programming Languages and Operating Systems Requirements

- Java (required to run the main wQFM-TREE application). The jar file was created using **openjdk 11.0.14.1 2022-02-08 LTS**.
- Linux O.S. required to generate consensus tree using paup and run bash scripts.
- Python 3 required to remove branch information from gene trees.
- (Only to run on resolved gene trees) Python, DendroPy are required to generate resolved gene trees.

## Input and output formats

### Input
Input file containing gene trees in newick format, one gene tree per line.

### Output

Output file contains the estimated species tree in newick format.

## Running the application

Steps
1. Download [this](https://github.com/abdur-rafi/wQFM-TREE/raw/master/wQFM-TREE.zip) zip file.
2. Unzip, this will create a wQFM-TREE folder.
3. Open a terminal inside the wQFM-TREE folder.
4. Execute the following command to run wQFM-TREE. The input gene trees **can contain polytomy** and the **will not be resolved**. Replace geneTreesFilePath with input gene trees file path and similarly, replace outputFilePath with desired path of the output file.
```
bash run.sh geneTreesFilePath outputFilePath
```
5. Execute the following command to run wQFM-TREE. In this case, polytomies present in the gene trees **will be resolved** before running wQFM-TREE.
```
bash runResolvingPolytomy.sh geneTreesFilePath outputFilePath
```

#### Example 
The wQFM-TREE folder, created after unzipping wQFM-TREE.zip contains a test-data folder. To run wQFM-TREE on one of the sample data, open a terminal inside the wQFM-TREE folder and run 
```
bash run.sh ./test-data/11Tax5Genes.tre out.txt
```

