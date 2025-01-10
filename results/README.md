## Astral_2_datasets Folder Structure
**NOTE1: wQFM-TREE is often refered to as ewqfm in the result files**

**NOTE2: Astral-2 results are included. But Astral-3 results have not been included yet. On a side note, Astral-3 does not perform better than Astral-2 in any model condition**

**Note-3: if gene tree count is omitted in file name, then it is for 1000 genes.**

* outputs: contains the output species trees of the analyzed methods. 
    * model condition (eg. model.200.500000.0.000001)
        * Replicate
            * [Method]-[gene-tree-count]-tree.txt (eg. treeqmc-50-tree.txt)
* scores-by-model-cond: contains the rf scores of the species trees
    * model condition
        * [method]-[gene-tree-count]-score.txt
        * avg-[method]-[gene-tree-count]-score.txt
* scores-sep-by-gts: contain the same information as the scores-by-model-cond folder, but organized in different way. 
* true-species-trees: Contain the true species trees. 


## wqfm_datasets Folder Structure

Contains the results of the 37 and 48 taxon datasets. The folder structure is similar to Astral_2_datasets folder. Only wQFM-TREE and wqfm was run on these datasets. 
