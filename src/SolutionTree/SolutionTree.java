package src.SolutionTree;

import src.Tree.Tree;
import src.Tree.TreeNode;

public class SolutionTree {

    public SolutionNode root;


    public SolutionTree(SolutionNode root){
        this.root = root;
    }


    public Tree createTree(){
        return dfs(root);
    }

    private Tree dfs(SolutionNode solnNode){
        if(solnNode.tree != null){
            return solnNode.tree;
        }
        else{
            Tree[] trees = new Tree[2];
            trees[0] = dfs(solnNode.left);
            trees[1] = dfs(solnNode.right);

            int[] dummyIds = new int[2];
            dummyIds[0] = solnNode.leftDTid;
            dummyIds[1] = solnNode.rightDTid;

            TreeNode[] dtNodes = new TreeNode[2];


            for(int i = 0; i < 2; ++i){
                for(var node : trees[i].nodes){
                    if(node.info.dummyTaxonId == dummyIds[i]){
                        dtNodes[i] = node;
                    }
                }
                if(dtNodes[i] == null){
                    System.out.println("Error: Dummy node not found");
                    System.exit(-1);
                }
                if(dtNodes[i].childs != null){
                    System.out.println("Error: Dummy node should be leaf");
                    System.exit(-1);
                }
            }
            
            trees[0].reRootTree(dtNodes[0]);
            if(dtNodes[0].childs.size() > 1){
                System.out.println("Error: Dummy node should have only one child after reroot");
                System.exit(-1);
            }
            dtNodes[0].childs.get(0).setParent(dtNodes[1].parent);
            dtNodes[1].parent.childs.remove(dtNodes[1]);
            dtNodes[1].parent.childs.add(dtNodes[0].childs.get(0));
            trees[1].nodes.addAll(trees[0].nodes);
    
            return trees[1];
            
        }
    }

    
}
