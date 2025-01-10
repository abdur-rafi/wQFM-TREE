## File Descriptions
The class information of the species in the gene trees are found in [annotations.csv](./annotations.csv) taken from [1kp repository](https://github.com/smirarab/1kp/tree/master/misc). 

This file is parsed through various scripts to generate the following files:
1. clades.txt: Contains clades, the number of taxa under each clade and the taxa under the clade, separated by new line.
2. colors.txt: Color assinged to each taxa according to their clade
3. colorCladesMap.txt: Color assigned to each clade.

These files can be dropped to [itol](https://itol.embl.de/) to easily annotate the species tree by providing different colors to each clade. 