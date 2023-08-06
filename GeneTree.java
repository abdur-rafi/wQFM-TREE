package wqfm.dsGT;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

public class GeneTree {
    

    class pair{
        int f, s;
        pair(int a, int b){
            f = a;
            s = b;
        }

        pair sub(pair b){
            return new pair(f - b.f, s - b.s);
        }

        pair add(pair b){
            return new pair(f + b.f, s + b.s);
        }

        @Override
        public String toString(){
            return "" + f + " " + s ;
        }
    }

    ArrayList<TreeNode> nodes;
    Map<String, Integer> taxaMap;
    TreeNode root;
    int sat, vio;
    pair gainaTobAll, gainbToaAll;

    private TreeNode addNode(String label, ArrayList<TreeNode> ch, TreeNode pr){
        TreeNode nd = new TreeNode(nodes.size(), label, ch, pr);
        nodes.add(nd);
        return nd;
    }

    private TreeNode addInternalNode(ArrayList<TreeNode> arr){
        var nd = addNode(null, arr, null);
        for (var x : arr)
            x.setParent(nd);
        return nd;
    }

    private TreeNode addTaxa(String taxa){
        var x = addNode(taxa, null, null); 
        taxaMap.put(taxa, x.index);
        return x;
    }

    public GeneTree(String line){
        taxaMap = new HashMap<>();
        nodes = new ArrayList<>();

        Stack<TreeNode> nodes = new Stack<>();
        line.replaceAll("\\s", "");

        int n =  line.length();

        int i = 0, j = 0;

        while(i < n){
            char curr = line.charAt(i);
            if(curr == '('){
                nodes.push(null);
            }
            else if(curr == ')'){
                ArrayList<TreeNode> arr = new ArrayList<>();
                while( !nodes.isEmpty() && nodes.peek() != null){
                    arr.add(nodes.pop());
                }
                if(!nodes.isEmpty())
                    nodes.pop();
                nodes.push(addInternalNode(arr));
                
            }
            else if(curr == ',' || curr == ';'){

            }
            else{
                StringBuilder taxa = new StringBuilder();
                j = i;
                TreeNode newNode = null;
                while(j < n){
                    char curr_j = line.charAt(j);
                    if(curr_j == ')' || curr_j == ','){
                        newNode = addTaxa(taxa.toString());
                        break;
                    }
                    taxa.append(curr_j);
                    ++j;
                }
                if(j == n){
                    newNode = addTaxa(taxa.toString());
                }
                i = j - 1;
                nodes.push(newNode);
            }
            ++i;
        }

        root = nodes.lastElement();

        balanceRoot();
    }


    public int dfs(TreeNode node, ArrayList<Integer> subTreeNodeCount){
        if(node.childs == null){
            subTreeNodeCount.set(node.index, 1);
            return 1;
        }
        int res = 0;
        for(var x : node.childs){
            res += dfs(x, subTreeNodeCount);
        }
        subTreeNodeCount.set(node.index, res + 1);
        return res + 1;
    }
    

    private void balanceRoot(){

        int n = nodes.size();
        ArrayList<Integer> subTreeNodeCount = new ArrayList<>(n);
        for(int i = 0; i < n; ++i)
            subTreeNodeCount.add(0);
        dfs(root, subTreeNodeCount);
        
        TreeNode closest = root;
        int diff = n;
        int v;
        for(int i = 0; i < n; ++i){
            v = Math.abs(n/2 - subTreeNodeCount.get(i)); 
            if(v < diff){
                diff = v;
                closest = nodes.get(i);
            }
        }
        // System.out.println("diff : " + diff + " node: " + closest.index);
        TreeNode closestP = closest.parent;
        closestP.childs.remove(closest);

        TreeNode curr = closestP;
        TreeNode currP, temp;
        currP = curr.parent;
        while(curr != null && currP != null){
            curr.childs.add(currP);
            currP.childs.remove(curr);
            temp = currP;
            currP = currP.parent;
            temp.parent = curr;
            curr = temp;
        }

        ArrayList<TreeNode> arr = new ArrayList<>();
        arr.add(closest);
        arr.add(closestP);

        root = new TreeNode(nodes.size(),null, arr, null);
        nodes.add(root);

        // System.out.println(root.toString());

    }
    

    private void calcReachableInSubtree(TreeNode node,Set<Integer> pA, Set<Integer> pB){
        int pAScore, pBScore;
        if (node.childs == null){
            pAScore = pA.contains(node.index) ? 1 : 0;
            pBScore = pB.contains(node.index) ? 1 : 0;
        }
        else{
            pAScore = 0;
            pBScore = 0;

            for(var x : node.childs){
                calcReachableInSubtree(x, pA, pB);
                pAScore += x.info.pACount;
                pBScore += x.info.pBCount;
            }
        }
        node.info = new Info(pAScore, pBScore);
    }

    private void flowToSubTree(TreeNode node, int aScore, int bScore){
        node.info.setAboveCount(aScore, bScore);

        // aScore += node.info.pACount;
        // bScore += node.info.pBCount;
        
        if(node.childs != null){
            var z = scoreAtANode(node);
            System.out.println("socre at node : " + node.index + " " + z);
            sat += z.f;
            vio += z.s;
            for(var x : node.childs){
                flowToSubTree(x, aScore + node.info.pACount - x.info.pACount,
                 bScore + node.info.pBCount - x.info.pBCount);
            }
        }
    }

    private int satisfiedEqn(int a1, int a2, int b3){
        return a1 * a2 * (b3 * (b3-1)) / 2;
    }
    private int violatedEqn(int a1, int b2, int a3, int b3){
        return a1 * b2 * a3 * b3;
    }

    private pair scoreFromCounts(int a1, int b1, int a2, int b2, int a3, int b3){
        int sat = 0, vio = 0;
        sat += satisfiedEqn(a1, a2, b3);
        sat += satisfiedEqn(a2, a3, b1);
        sat += satisfiedEqn(a3, a1, b2);

        vio += violatedEqn(a1, b2, a3, b3);
        vio += violatedEqn(a2, b1, a3, b3);

        vio += violatedEqn(a2, b3, a1, b1);
        vio += violatedEqn(a3, b2, a1, b1);

        vio += violatedEqn(a3, b1, a2, b2);
        vio += violatedEqn(a1, b3, a2, b2);
        return new pair(sat, vio);


    }

    private pair scoreAtANode(TreeNode node){

        if(node.childs == null) return new pair(0, 0);
        
        int a1 = node.childs.get(0).info.pACount;
        int b1 = node.childs.get(0).info.pBCount;


        int a2 = node.childs.get(1).info.pACount;
        int b2 = node.childs.get(1).info.pBCount;


        int a3 = node.info.abovepACount;
        int b3 = node.info.abovepBCount;


        pair originalScore = scoreFromCounts(a1, b1, a2, b2, a3, b3);
        
        pair scoreaTob = originalScore;
        pair scorebToa = originalScore;

        if(a3 > 0){
            scoreaTob = scoreFromCounts(a1, b1, a2, b2, a3 - 1, b3 + 1);
        }
        if(b3 > 0){
            scorebToa = scoreFromCounts(a1, b1, a2, b2, a3 + 1, b3 - 1);
        }

        pair aboveaToB = scoreaTob.sub(originalScore);
        pair abovebToA = scorebToa.sub(originalScore);

        // System.out.println("aboves : " + aboveaToB + " " + aboveaToB);
        gainaTobAll = gainaTobAll.add(aboveaToB);
        gainbToaAll =  gainbToaAll.add(abovebToA);
        
        scoreaTob = originalScore;
        if(a1 > 0){
            scoreaTob = scoreFromCounts(a1 - 1, b1 + 1, a2, b2, a3, b3);
        }
        scorebToa = originalScore;
        if(b1 > 0){
            scorebToa = scoreFromCounts(a1 + 1, b1 - 1, a2, b2, a3, b3);
        }
        node.childs.get(0).info.setGain(
            scoreaTob.sub(originalScore).sub(aboveaToB).add(node.info.gainAtoB) , 
            scorebToa.sub(originalScore).sub(abovebToA).add(node.info.gainBtoA)
        );
        
        scoreaTob = originalScore;

        if(a2 > 0){
            scoreaTob = scoreFromCounts(a1, b1, a2 - 1, b2 + 1, a3, b3);
        }
        scorebToa = originalScore;
        if(b2 > 0){
            scorebToa = scoreFromCounts(a1, b1, a2 + 1, b2 - 1, a3, b3);
        }
        node.childs.get(1).info.setGain(
            scoreaTob.sub(originalScore).sub(aboveaToB).add(node.info.gainAtoB) , 
            scorebToa.sub(originalScore).sub(abovebToA).add(node.info.gainBtoA)
        );
        
        return originalScore;
    }

    
    public int score(Set<Integer> pA, Set<Integer> pB){
        sat = 0;
        vio = 0;
        gainaTobAll = new pair(0, 0);
        gainbToaAll = new pair(0, 0);
        calcReachableInSubtree(root, pA, pB);
        var c1 = root.childs.get(0);
        var c2 = root.childs.get(1);
        c1.info.gainAtoB = new pair(0, 0);
        c1.info.gainBtoA = new pair(0, 0);
        c2.info.gainBtoA = new pair(0, 0);
        c2.info.gainAtoB = new pair(0, 0 );
        flowToSubTree(c1, c2.info.pACount, c2.info.pBCount);
        flowToSubTree(c2, c1.info.pACount, c1.info.pBCount);
        System.out.println("globals : " + gainaTobAll + " " + gainbToaAll);
        for(var x : nodes){
            if(x.isLeaf()){
                if(pA.contains(x.index)){
                    x.info.gainAtoB =  x.info.gainAtoB.add(gainaTobAll);
                }
                else if(pB.contains(x.index)){
                    x.info.gainBtoA =  x.info.gainBtoA.add(gainbToaAll);
                }
                System.out.println( x.label + " --" + " From A to B -> " + x.info.gainAtoB + " " + " From B to A -> " + x.info.gainBtoA);
            }
        }

        System.out.println("sat : " + sat + " vio : " + vio);

        return sat - vio / 2;

    }
}
