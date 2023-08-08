package wqfm.dsGT;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
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
    Set<Integer> dummypA,dummypB;
    ArrayList<IDummyTaxa> dummyTaxas;

    private Set<Integer> pA, pB;
    private Map<Integer, Integer> taxaToDummyTaxaMap;
    int dummyTaxaCount;
    int[] dummyScore;
    pair[] dummyTaxaGains;

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
    

    private void calcReachableInSubtree(TreeNode node){
        int pAScore, pBScore;
        Set<Integer> reachableDummyTaxaA = new HashSet<>();
        Set<Integer> reachableDummyTaxaB = new HashSet<>();

        if (node.childs == null){
            pAScore = pA.contains(node.index) ? 1 : 0;
            pBScore = pB.contains(node.index) ? 1 : 0;
            if(this.taxaToDummyTaxaMap.containsKey(node.index)){
                int dummyTaxaId = this.taxaToDummyTaxaMap.get(node.index);
                if(this.dummypA.contains(dummyTaxaId)){
                    reachableDummyTaxaA.add(dummyTaxaId);
                }
                else{
                    reachableDummyTaxaB.add(dummyTaxaId);
                }
            }
            // pADummyTaxa[this.taxaToDummyTaxaMap.get(node.index)] = true;
        }
        else{
            pAScore = 0;
            pBScore = 0;

            for(var x : node.childs){
                calcReachableInSubtree(x);
                reachableDummyTaxaA.addAll(x.info.reachableDummyTaxaA);
                reachableDummyTaxaB.addAll(x.info.reachableDummyTaxaB);
                pAScore += x.info.pACount;
                pBScore += x.info.pBCount;
            }
        }
        node.info = new Info(pAScore, pBScore);
        node.info.setDummyTaxaFlags(reachableDummyTaxaA, reachableDummyTaxaB);
    }

    private void flowToSubTree(
        TreeNode node,
        int aboveACount, 
        int aboveBCount, 
        Set<Integer> reachableFromAboveA,
        Set<Integer> reachableFromAboveB
    ){
        node.info.setAboveCount(aboveACount, aboveBCount);
        node.info.setReachableDummyTaxaFromAbove(reachableFromAboveA, reachableFromAboveB);
        
        if(node.childs != null){
            var z = scoreAtANode2(node);
            // System.out.println("socre at node : " + node.index + " " + z);
            sat += z.f;
            vio += z.s;
            int n = node.childs.size();
            for(int l = 0; l < n ; ++l){
                var x = node.childs.get(l);
                Set<Integer> reachableDummyFromAboveAx = new HashSet<>();
                Set<Integer> reachableDummyFromAboveBx = new HashSet<>();

                reachableDummyFromAboveAx.addAll(reachableFromAboveA);
                reachableDummyFromAboveBx.addAll(reachableFromAboveB);

                for(int j = 0; j < n; ++j){
                    var y = node.childs.get(j);
                    if(j == l){
                        continue;
                    }

                    reachableDummyFromAboveAx.addAll(y.info.reachableDummyTaxaA);
                    reachableDummyFromAboveBx.addAll(y.info.reachableDummyTaxaB);                    
                }
                flowToSubTree(x, aboveACount + node.info.pACount - x.info.pACount,
                 aboveBCount + node.info.pBCount - x.info.pBCount, reachableDummyFromAboveAx, reachableDummyFromAboveBx);
            }
        }
    }


    class Branch{
        int pA, pB, pADummy, pBDummy;
        Set<Integer> dummyA, dummyB;
        int a_t, b_t;
        Branch(int a, int b, Set<Integer> sa, Set<Integer> sb){
            pA = a;
            pB = b;
            pADummy = sa.size();
            pBDummy = sb.size();
            dummyA = sa;
            dummyB = sb;
            a_t = pA + pADummy;
            b_t = pB + pBDummy;
        }
    }
    class ScoreCalculator{
        
        Branch[] branches;
        ArrayList<Set<Integer>> commonA;
        ArrayList<Set<Integer>> commonB;
        int[] commonSzA;
        int[] commonSzB;

        Set<Integer> calculated;
        pair[] dummyTaxaGains;

        //  values of gains calculated in gainof1branch function
        pair aToBGain, bToAGain;
        // gains of 3 branches
        pair[] aToBGains, bToAGains;
        // current score calcualted in scoreAndGain function
        pair currScore;

        ScoreCalculator(Branch[] b,pair[] dummyTaxaGains){
            branches = b;
            this.commonA = new ArrayList<>(3);
            this.commonB = new ArrayList<>(3);
            commonSzA = new int[3];
            commonSzB = new int[3];
            int j;
            for(int i = 0; i < 3; ++i){
                j = (i + 1) % 3;
                commonA.add(getIntersection(b[i].dummyA, b[j].dummyA));
                commonB.add(getIntersection(b[i].dummyB, b[j].dummyB));
                commonSzA[i] = commonA.get(i).size();
                commonSzB[i] = commonB.get(i).size();
            }
            calculated = new HashSet<>();
            this.dummyTaxaGains = dummyTaxaGains;
        }

        pair scoreOf2Branch(int i){
            int j = (i + 1) % 3;
            int k = (i + 2) % 3;
            int sat = satisfiedEqn(branches[i].a_t, branches[j].a_t, branches[k].b_t, commonSzA[i]);
            int vio = violatedEqn(branches[i].a_t, branches[j].b_t, branches[k].a_t, branches[k].b_t, commonSzA[k],commonSzB[j]);
            vio += violatedEqn(branches[j].a_t, branches[i].b_t, branches[k].a_t, branches[k].b_t, commonSzA[j],commonSzB[k]);

            return new pair(sat, vio);
        }
        void gainOf1Branch(int i, pair originalScore){
            int j = (i + 1) % 3;
            int k = (i + 2) % 3;
            this.aToBGain = new pair(0, 0);
            this.bToAGain = new pair(0, 0);

            // A to B
            if(branches[i].a_t > 0){
                branches[i].a_t--;
                branches[i].b_t++;
                boolean c1, c2, c3, c4;
                this.aToBGain = score().sub(originalScore);
                for(var x : branches[i].dummyA){
                    if(this.calculated.contains(x))
                        continue;
                    c1 = commonA.get(i).contains(x);
                    c2 = commonA.get(k).contains(x);
                    c3 = branches[j].dummyA.contains(x);
                    c4 = branches[k].dummyA.contains(x);
                    if(c1){
                        this.commonSzA[i]--;
                    }
                    if(c2){
                        this.commonSzA[k]--;
                    }
                    if(c3){
                        branches[j].b_t++;
                        this.commonSzB[i]++;
                    }
                    if(c4){
                        branches[k].b_t++;
                        this.commonSzB[k]++;
                    }

                    var aToBScoreDummy = score();
                    var gain = aToBScoreDummy.sub(originalScore);
                    this.dummyTaxaGains[x] = this.dummyTaxaGains[x].add(gain);
                    this.calculated.add(x);

                    if(c1){
                        this.commonSzA[i]++;
                    }
                    if(c2){
                        this.commonSzA[k]++;
                    }
                    if(c3){
                        branches[j].b_t--;
                        this.commonSzB[i]--;
                    }
                    if(c4){
                        branches[k].b_t--;
                        this.commonSzB[k]--;
                    }
                }

                branches[i].a_t++;
                branches[i].b_t--;
            }
            // B to A 
            if(branches[i].b_t > 0){
                branches[i].b_t--;
                branches[i].a_t++;
                boolean c1, c2, c3, c4;
                this.bToAGain = score().sub(originalScore);
                for(var x : branches[i].dummyB){
                    if(this.calculated.contains(x))
                        continue;
                    c1 = commonB.get(i).contains(x);
                    c2 = commonB.get(k).contains(x);
                    c3 = branches[j].dummyB.contains(x);
                    c4 = branches[k].dummyB.contains(x);
                    if(c1){
                        this.commonSzB[i]--;
                    }
                    if(c2){
                        this.commonSzB[k]--;
                    }
                    if(c3){
                        branches[j].a_t++;
                        this.commonSzA[i]++;
                    }
                    if(c4){
                        branches[k].a_t++;
                        this.commonSzA[k]++;
                    }

                    var bToAScoreDummy = score();
                    var gain = bToAScoreDummy.sub(originalScore);
                    this.dummyTaxaGains[x] = this.dummyTaxaGains[x].add(gain);
                    this.calculated.add(x);

                    if(c1){
                        this.commonSzB[i]++;
                    }
                    if(c2){
                        this.commonSzB[k]++;
                    }
                    if(c3){
                        branches[j].a_t--;
                        this.commonSzA[i]--;
                    }
                    if(c4){
                        branches[k].a_t--;
                        this.commonSzA[k]--;
                    }
                }

                branches[i].b_t++;
                branches[i].a_t--;
            }
            

        }
        
        
        pair scoreAndGain(){
            aToBGains = new pair[3];
            bToAGains = new pair[3];

            currScore = score();

            for(int i = 0; i < 3; ++i){
                this.gainOf1Branch(i, currScore);
                aToBGains[i] = aToBGain;
                bToAGains[i] = bToAGain;
            }

            return currScore;
        }

        pair score(){
            pair res = new pair(0, 0);
            for(int i = 0; i < 3; ++i){
                res = res.add(scoreOf2Branch(i));
            }
            return res;
        }

        private Set<Integer> getIntersection(Set<Integer> a, Set<Integer> b){
            var x = copySet(a);
            x.retainAll(b);
            return x;
        }
        
        private int satisfiedEqn(int a1, int a2, int b3, int common12){
            return (a1 * a2 - common12) * (b3 * (b3-1)) / 2;
        }
        private int violatedEqn(int a1, int b2, int a3, int b3, int commona1a3, int commonb2b3){
            return (a1 * a3 - commona1a3) * (b2 * b3 - commonb2b3 );
        }
    }


    private pair scoreAtANode2(TreeNode node){
        if(node.childs == null) return new pair(0, 0);
        
        var c1 = node.childs.get(0);
        var c2 = node.childs.get(1);

        Branch b1 = new Branch(c1.info.pACount, c1.info.pBCount, c1.info.reachableDummyTaxaA, c1.info.reachableDummyTaxaB);
        Branch b2 = new Branch(c2.info.pACount, c2.info.pBCount, c2.info.reachableDummyTaxaA, c2.info.reachableDummyTaxaB);
        Branch b3 = new Branch(node.info.abovepACount, node.info.abovepBCount, node.info.reachableDummyTaxaFromAboveA, node.info.reachableDummyTaxaB);

        Branch[] barr = new Branch[3];
        barr[0] = b1;barr[1] =  b2; barr[2] = b3;
        var calc = new ScoreCalculator(barr, this.dummyTaxaGains);

        var score = calc.scoreAndGain();
        
        var aboveaToBGain = calc.aToBGains[2];
        var abovebToAGain = calc.bToAGains[2];

        this.gainaTobAll = this.gainaTobAll.add(aboveaToBGain);
        this.gainbToaAll = this.gainbToaAll.add(abovebToAGain);
        for(int i = 0; i < 2; ++i){
            node.childs.get(i).info.setGain(
                calc.aToBGains[i].sub(aboveaToBGain).add(node.info.gainAtoB) , 
                calc.bToAGains[i].sub(abovebToAGain).add(node.info.gainBtoA)
            );
        }

        return score;
        // return new ScoreCalculator(barr,this.dummyTaxaGains).scoreAndGain();

    }


    Set<Integer> copySet(Set<Integer> st){
        Set<Integer> a = new HashSet<>();
        a.addAll(st);
        return a;
    }

    // taxaToDummyTaxaMap : maps taxa's index to dummy taxa's index
    public int score(
        Set<Integer> pA, 
        Set<Integer> pB, 
        Map<Integer, Integer> taxaToDummyTaxaMap,
        Set<Integer> dummypAIndices,
        Set<Integer> dummypBIndices
    ){
        this.taxaToDummyTaxaMap = taxaToDummyTaxaMap;
        this.pA = pA;
        this.pB = pB;
        this.dummypA = dummypAIndices;
        this.dummypB = dummypBIndices;
        this.dummyTaxaCount = dummypAIndices.size() + dummypBIndices.size();
        this.dummyScore = new int[this.dummyTaxaCount];
        this.dummyTaxaGains = new pair[this.dummyTaxaCount];
        for(int i = 0; i < this.dummyTaxaCount; ++i){
            this.dummyTaxaGains[i] = new pair(0, 0);
        }
        System.out.println(taxaToDummyTaxaMap);
        System.out.println(dummypAIndices);
        System.out.println(dummypBIndices);

        sat = 0;
        vio = 0;
        gainaTobAll = new pair(0, 0);
        gainbToaAll = new pair(0, 0);
        calcReachableInSubtree(root);
        var c1 = root.childs.get(0);
        var c2 = root.childs.get(1);
        
        c1.info.gainAtoB = new pair(0, 0);
        c1.info.gainBtoA = new pair(0, 0);
        c2.info.gainBtoA = new pair(0, 0);
        c2.info.gainAtoB = new pair(0, 0 );

        flowToSubTree(c1, c2.info.pACount, c2.info.pBCount, copySet(c2.info.reachableDummyTaxaA),copySet(c2.info.reachableDummyTaxaB));
        flowToSubTree(c2, c1.info.pACount, c1.info.pBCount, copySet(c1.info.reachableDummyTaxaA),copySet(c1.info.reachableDummyTaxaB));
        
        System.out.println("globals : " + gainaTobAll + " " + gainbToaAll);
        
        for(var x : nodes){
            if(x.isLeaf()){
                if(pA.contains(x.index)){
                    x.info.gainAtoB =  x.info.gainAtoB.add(gainaTobAll);
                    System.out.println( x.label + " --" + " From A to B -> " + x.info.gainAtoB + " " + " From B to A -> " + x.info.gainBtoA);

                }
                else if(pB.contains(x.index)){
                    x.info.gainBtoA =  x.info.gainBtoA.add(gainbToaAll);
                    System.out.println( x.label + " --" + " From A to B -> " + x.info.gainAtoB + " " + " From B to A -> " + x.info.gainBtoA);
                }
            }
        }

        for(int i = 0; i < this.dummyTaxaCount; ++i){
            System.out.println(this.dummyTaxaGains[i]);
        }

        System.out.println("sat : " + sat + " vio : " + vio);

        return sat - vio / 2;

    }
}
