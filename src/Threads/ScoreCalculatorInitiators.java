package src.Threads;

import java.util.ArrayList;

import src.Utility;
import src.PreProcessing.PartitionByTreeNode;

public class ScoreCalculatorInitiators {

    ArrayList<ScoreInitiatorRunnable> initTasks;
    ArrayList<ScoreCalculatorRunnable> scoreTasks;
    ArrayList<GainRunnable> gainTasks;
    ArrayList<SwapDTRunnable> swapTasks;


    private static ScoreCalculatorInitiators instance;

    public ScoreCalculatorInitiators(ArrayList<PartitionByTreeNode> partitions, int nThreads){
        initTasks = new ArrayList<>();
        scoreTasks = new ArrayList<>();
        gainTasks = new ArrayList<>();
        swapTasks = new ArrayList<>();

        int partitionByTreeNodeCount = partitions.size();
        int partitionByTreeNodePerThread = partitionByTreeNodeCount / nThreads;

        for(int i = 0; i < nThreads; ++i){
            int start = i * partitionByTreeNodePerThread;
            int end = (i + 1) * partitionByTreeNodePerThread;
            if(i == nThreads - 1){
                end = partitionByTreeNodeCount;
            }
            initTasks.add(new ScoreInitiatorRunnable(partitions , start, end));
            scoreTasks.add(new ScoreCalculatorRunnable(partitions, start, end));
            gainTasks.add(new GainRunnable(partitions, start, end));
            swapTasks.add(new SwapDTRunnable(partitions, start, end));
            // ThreadPool.getInstance().execute(new ScoreCalculatorInitiators(this.dc.partitionsByTreeNodes, this.taxaPerLevel.dummyTaxonPartition, start, end));
            
        }
    }
    
    public void setDummyTaxaToPartitionMap(int[] dummyTaxaToPartitionMap){
        for(ScoreInitiatorRunnable task : initTasks){
            task.setDummyTaxaToPartitionMap(dummyTaxaToPartitionMap);
        }
    }

    public void runInit(){
        ArrayList<Runnable> tasks = new ArrayList<>();
        for(ScoreInitiatorRunnable task : this.initTasks){
            tasks.add(task);
        }
        ThreadPool.getInstance().execute(tasks);
    }

    public static void setInstance(ArrayList<PartitionByTreeNode> partitions, int nThreads){
        instance = new ScoreCalculatorInitiators(partitions, nThreads);
    }

    public static ScoreCalculatorInitiators getInstance(){
        return instance;
    }

    public double runScore(){
        ArrayList<Runnable> tasks = new ArrayList<>();
        for(ScoreCalculatorRunnable task : this.scoreTasks){
            tasks.add(task);
        }
        ThreadPool.getInstance().execute(tasks);

        double score = 0;
        for(ScoreCalculatorRunnable task : this.scoreTasks){
            score += task.score;
            task.score = 0;
        }

        return score;
    }

    public Utility.Pair<Double, double[]> runGain(int nDummyTaxa){
        ArrayList<Runnable> tasks = new ArrayList<>();
        for(GainRunnable task : this.gainTasks){
            task.initDummyTaxaGains(nDummyTaxa);
            tasks.add(task);
        }
        ThreadPool.getInstance().execute(tasks);
        
        double score = 0;
        double[] gainDummyTaxa = new double[nDummyTaxa];

        for(GainRunnable task : this.gainTasks){
            score += task.score;
            task.score = 0;
            Utility.addArrayToFirst(gainDummyTaxa, task.dummyTaxaGains);
            task.dummyTaxaGains = null;
        }

        return new Utility.Pair<Double,double[]>(score, gainDummyTaxa);
    }


    public void runSwapDT(int index, int partition){
        ArrayList<Runnable> tasks = new ArrayList<>();
        for(SwapDTRunnable task : this.swapTasks){
            task.setIndexAndPartition(index, partition);
            tasks.add(task);
        }
        ThreadPool.getInstance().execute(tasks);
    }
    

    

}
