
# Python script to generate a PhyloNet Nexus script for consensus tree from gene trees file

gene_trees_file = "all_gt_cleaned.tre"  # Replace with your gene trees file name
output_nexus_script = "phylonet_script.nex"

# Read gene trees from file
with open(gene_trees_file, "r") as gene_trees_file:
    gene_trees = gene_trees_file.readlines()

# Generate Nexus script
nexus_script = """#NEXUS

BEGIN NETWORKS;\n"""

# Append gene trees to the Nexus script
for i, gene_tree in enumerate(gene_trees, start=1):
    network_tree = f"Network g{i} = {gene_tree.strip()}"
    nexus_script += network_tree + "\n"
    if i>=100: break

# Close the NETWORKS block and start the PHYLONET block
nexus_script += "\nEND;\n\nBEGIN PHYLONET;\n"

# Apply Infer_ST_MC to each gene tree
infer_command = "Infer_ST_MC("
infer_command += ", ".join([f"g{i}" for i in range(1, 100 + 1)])
infer_command += ");"
nexus_script += infer_command + "\n"

# Close the PHYLONET block
nexus_script += "\nEND;"

# Write the Nexus script to a file
with open(output_nexus_script, "w") as nexus_file:
    nexus_file.write(nexus_script)

print(f"PhyloNet Nexus script generated: {output_nexus_script}")
