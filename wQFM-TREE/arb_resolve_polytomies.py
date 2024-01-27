#!/lusr/bin/python2.7
'''
Created on Jun 3, 2011

@author: smirarab
'''
import dendropy
import sys

def clean_tree_string(tree_string):
    # Remove '[&R]' prefix
    tree_string = tree_string.replace('[&R]', '')
    return tree_string.strip()

#requires dendropy 3.8

if __name__ == '__main__':

    treeName = sys.argv[1]
    
    #cmd = 'find %s -name "%s" -print' % (treeDir,treeName)
    #print cmd
    #for file in os.popen(cmd).readlines():     # run find command        
    #    name = file[:-1]                       # strip '\n'                
    #    fragmentsFile=name.replace(treeName,"sequence_data/short.alignment");
    resultsFile="%s.resolved" % treeName
    
    trees = dendropy.TreeList.get_from_path(treeName, 'newick',rooting='force-rooted')
    for tree in trees:            
        print(".")
        tree.resolve_polytomies()     
    
    	
#    trees.write(open(resultsFile,'w'),'newick',edge_lengths=True)  # ei line ta chhilo originally. eta te proti ta gene tree er age [&R] prefix ashe..eta bad debar jonno ami nicher tuku disi.


    out = open(resultsFile,'w');   	
    for tree in trees:	      
            s = clean_tree_string(tree.as_string("newick"))
            #s = s[0:s.rfind(")")+1]
            out.write(s)
            out.write("\n")	
    out.close()
