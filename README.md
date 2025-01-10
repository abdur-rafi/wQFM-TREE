# wQFM-TREE

This repository contains the official implementation of 

[**wQFM-TREE: highly accurate and scalable quartet-based species tree inference from gene trees**](https://www.biorxiv.org/content/10.1101/2024.07.30.605630v1)

## Execution dependencies

### Packages, Programming Languages and Operating Systems Requirements

- Java (required to run the main wQFM-TREE application). The jar file was created using **openjdk 11.0.14.1 2022-02-08 LTS**.
- Linux O.S. required to generate consensus tree using paup and run bash scripts.
- Python 3 required to remove branch information from gene trees.
- (Only to run on resolved gene trees) Python, [DendroPy](https://jeetsukumaran.github.io/DendroPy/) are required to generate resolved gene trees.

## Input and output formats

### Input
Input file containing gene trees in [newick](https://en.wikipedia.org/wiki/Newick_format) format, one gene tree per line.
```
((3,((11,(10,((5,6),(9,(7,8))))),4)),2,1);
(3,(2,(4,(11,(9,((7,8),((5,6),10)))))),1);
(((2,(11,((5,6),((7,8),(10,9))))),3),4,1);
(2,((11,(10,((8,7),((6,5),9)))),(4,3)),1);
((11,((10,((7,6),(9,8))),5)),((4,2),3),1);
```

### Output

Output file contains the estimated species tree in newick format.
<!-- ```
(((((1,2),3),4),((((5,6),(7,8)),9),10)),11);
``` -->
## Running the application

Steps
1. Download [this](https://github.com/abdur-rafi/wQFM-TREE/raw/master/wQFM-TREE.zip) zip file.
2. Unzip, this will create a wQFM-TREE folder.
3. Open a terminal inside the wQFM-TREE folder.
4. Execute the following command to run wQFM-TREE. The input gene trees **can contain polytomy** and they **will not be resolved**. Replace geneTreesFilePath with input gene trees file path and similarly, replace outputFilePath with desired path of the output file.
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

## Results reported in wQFM-TREE Paper
The [results](./results/) folder contain all the outputs and results of the experiments reported in the paper.
The [plant](./plant/) folder contains outputs, scripts and files used to analyze the [1kp plant dataset](https://academic.oup.com/gigascience/article/8/10/giz126/5602476). 
The [scripts](./scripts/) folder contain various scripts used to conduct all the experiments. 

More details can be found the README.md files inside these folders. 