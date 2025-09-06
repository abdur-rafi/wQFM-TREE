# wQFM-TREE

This repository contains the official implementation of 

[**wQFM-TREE: highly accurate and scalable quartet-based species tree inference from gene trees**](https://academic.oup.com/bioinformaticsadvances/article/5/1/vbaf053/8075148)

## Execution dependencies

### Packages, Programming Languages and Operating Systems Requirements

- Java (required to run the main wQFM-TREE application). The jar file was created using **openjdk 11.0.14.1 2022-02-08 LTS**.
- Linux O.S. required to generate consensus tree using paup and run bash scripts.
- Python 3 required to remove branch information from gene trees.
- wQFM-TREE is capable of handling polytomies, but this comes with increased runtime. If your input gene trees contain polytomies and you prefer to resolve them prior to running wQFM-TREE, Python, [DendroPy](https://jeetsukumaran.github.io/DendroPy/) are required to generate resolved gene trees.

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
```
((10,(11,((3,4),(1,2)))),(5,6),(9,(7,8)));
``` 
## Running the application

Steps
1. Download [this](https://github.com/abdur-rafi/wQFM-TREE/releases/download/v1.0.1/wQFM-TREE.zip) zip file.
2. Unzip, this will create a wQFM-TREE folder.
3. Open a terminal inside the wQFM-TREE folder.
4. Execute the following command to run wQFM-TREE. 
```
bash run.sh geneTreesFilePath outputFilePath
```
5. If you want to run wQFM-TREE on **resolved gene trees**, execute the following command. In this case, polytomies present in the gene trees will be resolved arbitrarily before running wQFM-TREE.
   wQFM-TREE can handle multifurcating trees, which may increase accuracy slightly but with increased running time. 
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
The [memoryLogs](./memoryLogs/) folder contain the log files of memory tracing. 

More details can be found the README.md files inside these folders. 

## License
The contents of this repository are licensed under the Apache License, Version 2.0.

See [LICENSE.md](./LICENSE.md) for the full license text.

## Citation (BibTeX)
If you use our algorithm or wish to use any part of this repository, please do cite our paper.
```
@article{rafi2025wqfm,
  title={wQFM-TREE: highly accurate and scalable quartet-based species tree inference from gene trees},
  author={Rafi, Abdur and Rumi, Ahmed Mahir Sultan and Hakim, Sheikh Azizul and Sohaib and Tahmid, Md Toki and Momin, Rabib Jahin Ibn and Zaman, Tanjeem Azwad and Reaz, Rezwana and Bayzid, Md Shamsuzzoha},
  journal={Bioinformatics Advances},
  volume={5},
  number={1},
  pages={vbaf053},
  year={2025},
  publisher={Oxford University Press}
}
```

## Bug Report
We are always looking to improve our codebase.

For any issues, please post on [wQFM-TREE issues page](https://github.com/abdur-rafi/wQFM-TREE/issues).

Alternatively, you can email at ```amsrumi@gmail.com``` or ```rafi08236@gmail.com```.
