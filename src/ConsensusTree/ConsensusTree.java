package src.ConsensusTree;

import com.sun.source.tree.Tree;
import src.GeneTree.GeneTree;
import src.GeneTree.TreeNode;
import src.GeneTree.TreePrinter;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static java.lang.Math.abs;

public class ConsensusTree {

    static String filepath="./input/ConsensusTrees/tree1.txt.raxml.consensustreemre";
    GeneTree g;

    private TreeNode selectednode;
    private double  min_diff=1000000;

    Map<String, Integer> selectedsubtreeMapper;



    Map<String, Double> weightara;


    void DFS(TreeNode node)
    {
        if(node.isLeaf())
        {
            node.weight=weightara.get(node.label);
        }
        else
        {
            for(int i=0;i<node.childs.size();i++)
            {
                DFS(node.childs.get((i)));
                node.weight+=node.childs.get(i).weight;
            }
        }
    }

    void DFS_selectednode(TreeNode node)
    {
        if(abs(g.root.weight-2*node.weight)<min_diff && node!=g.root)
        {
            min_diff=abs(g.root.weight-2*node.weight);
            selectednode=node;
        }

        if(node.childs == null)return;
        for(int i=0;i<node.childs.size();i++)
        {
            DFS_selectednode(node.childs.get((i)));
        }
    }

    void DFS_mapbipartition(TreeNode node)
    {
        if(node.isLeaf())
        {
            selectedsubtreeMapper.put(node.label,0);
        }
        for(int i=0;i<node.childs.size();i++)
        {
            DFS_selectednode(node.childs.get((i)));
        }
    }

    public void getBiparitionfromConsensus( Set<String> realTaxas, ArrayList<Set<String>> dummyTaxas,Map<String, Integer> realTaxaPartitionMap,Map<String, Integer> realTaxaToDummyTaxaMap,Map<Integer, Integer> dummyTaxaPartitionMap,int[] partitionSize)
    {
        createweightTree(realTaxas,dummyTaxas);

        DFS(g.root);

        DFS_selectednode(g.root);
        System.out.println(11);

        selectedsubtreeMapper.clear();

        DFS_mapbipartition(selectednode);
        System.out.println(11);


        for(var x: realTaxas)
        {
            if(selectedsubtreeMapper.get(x)==null)
            {
                realTaxaPartitionMap.put(x,1);
                partitionSize[1]++;
            }
            else
            {
                realTaxaPartitionMap.put(x,0);
                partitionSize[0]++;
            }
        }

        System.out.println(11);

        int i = 0;
        for (var x : dummyTaxas) {

            double weight=0;
            for (var y : x) {
                realTaxaToDummyTaxaMap.put(y, i);
                if(selectedsubtreeMapper.get(y)==null)
                {
                    weight-=weightara.get(y);
                }
                else
                {
                    weight+=weightara.get(y);
                }
            }

            if(weight>=0)
            {
                dummyTaxaPartitionMap.put(i, 0);
                partitionSize[0]++;
            }
            else
            {
                dummyTaxaPartitionMap.put(i, 1);
                partitionSize[1]++;
            }


            ++i;
        }

        System.out.println(11);



    }

    public void createweightTree(Set<String> realTaxas, ArrayList<Set<String>> dummyTaxas)
    {
        weightara.clear();

        for(var x: realTaxas)
        {
            weightara.put(x,1.0);
        }

        HashMap<Integer,Integer>temp=new HashMap<>();
        HashMap<String,String>parent=new HashMap<>();
        int[] sz=new int[dummyTaxas.size()];

//        for(var x: realTaxas)
//        {
//            parent.put(x,x);
//        }

        int[] ara=new int[dummyTaxas.size()];
        for(int i=0;i<dummyTaxas.size();i++)
        {
            ara[i]=i;
            temp.put(i,dummyTaxas.get(i).size());
        }

        for(int i=0;i<ara.length;i++)
        {
            for(int j=0;j<ara.length;j++)
            {
                if(temp.get(ara[i])>temp.get(ara[j]))
                {
                    int t=ara[i];
                    ara[i]=ara[j];
                    ara[j]=t;
                }
            }
        }

        System.out.println(1);

//        ArrayList<Set<String> > dummyTaxasActual=new ArrayList<>();
//        for(int i=0;i<ara.length;i++)
//        {
//            dummyTaxasActual.add(new Set<String>());
//;        }

        int[] l=new int[ara.length];
        System.out.println(ara.length);
        for(int i=0;i<ara.length;i++)
        {
            int cnt=0;
            for(var x:dummyTaxas.get(ara[i]))
            {
                HashMap<String,Integer>tempmap=new HashMap<String, Integer>();
                if(parent.get(x)==null)
                {
                    cnt++;
                }
                else
                {
                    if(tempmap.get(parent.get(x)) == null)cnt++;
                    tempmap.put(("dummy"+Integer.toString(ara[i])),1);
                }

//                if(parent.get(x)==null)
//                {
//                    parent.put(x,"dummy"+Integer.toString(ara[i]));
//                    l[ara[i]]++;
//                }
//                else if(parent.get(parent.get(x))==null)
//                {
//                    parent.put(parent.get(x),"dummy"+Integer.toString(ara[i]));
//                    l[ara[i]]++;
//                }

            }
            sz[ara[i]]=cnt;

            for(var x:dummyTaxas.get(ara[i]))
            {
                parent.put(x,"dummy"+Integer.toString(ara[i]));
            }
        }



        System.out.println(1);

        for(int i=0;i<ara.length;i++)
        {
            for(var x:dummyTaxas.get(i))
            {
                if(weightara.get(x)==null)
                {

                    String s=parent.get(x);
                    double weight=1;
                    for(int j=0;j<ara.length;j++)
                    {
                        if(dummyTaxas.get(j).contains(x))weight=weight*1.0/sz[j];
                        System.out.println("size = "+sz[j]);
                    }
//                    while(s!=null)
//                    {
////                        System.out.println(s);
//                        weight=weight*1.0/l[Integer.parseInt(s.substring(5))];
//                        s=parent.get(s);
//                    }
                    weightara.put(x,weight);
                }


            }
        }

        for(var x: weightara.keySet())
        {
            System.out.println(x+" "+weightara.get(x));
        }


    }





    public static String readAndPrintFile(String filePath) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                return line;
            }
        }

        return "";
    }

    public ConsensusTree()
    {
        try {
            g=new GeneTree(readAndPrintFile(filepath));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        weightara=new HashMap<>();
        selectedsubtreeMapper=new HashMap<>();


    }



    public static void main(String[] args) {

        String consensustree;

        try {
            consensustree=readAndPrintFile(filepath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


        GeneTree g=new GeneTree(consensustree);
//        TreePrinter.print(g.root);
//        System.out.println(g.nodes.get(g.taxaMap.get("7")).parent.index);
//        System.out.println(g.nodes.get(g.taxaMap.get("9")).parent.index);



    }




}
