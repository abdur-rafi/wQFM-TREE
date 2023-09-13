# ./raxml-ng --consense MRE --tree ./input/gtree_11tax_est_5genes_R1.tre --prefix ./input/test
# ./raxml-ng --consense MRE --tree ./run/37-taxon/noscale.25g.500b/R1/all_gt.tre --prefix ./input/test


/usr/bin/env /usr/lib/jvm/java-17-openjdk-amd64/bin/java -XX:+ShowCodeDetailsInExceptionMessages -cp /home/abdur-rafi/.config/Code/User/workspaceStorage/da91ba3e148e5727246c82da7f9911d2/redhat.java/jdt_ws/E-WQFM_731a4073/bin src.Main ./input/gtree_11tax_est_5genes_R1.tre ./input/ConsensusTrees/tree1.txt.raxml.consensustreemre out.txt
python ./output/getFpFn.py -t ./input/11_tax_true.txt -e ./out.txt
