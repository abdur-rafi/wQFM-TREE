package src.Threads;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadPool {
    // thread pool using Executors
    

    private int nThreads;
    ExecutorService executorService;

    public ThreadPool(int nThreads){
        this.nThreads = nThreads;
        this.executorService = Executors.newFixedThreadPool(nThreads);
    }

    public static ThreadPool threadPool;

    public static void setInstance(int nThreads){
        threadPool = new ThreadPool(nThreads);
    }

    public static ThreadPool getInstance(){
        return threadPool;
    }

    public void execute(Runnable task){
        executorService.execute(task);
    }


    public int getNThreads(){
        return this.nThreads;
    }

    public void execute(List<Runnable> tasks){
        List<Callable<Object>> tods = new java.util.ArrayList<>();
        for(Runnable task : tasks){
            tods.add(Executors.callable(task));
        }
        try {
            executorService.invokeAll(tods);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    

    public void shutdown(){
        executorService.shutdown();
    }
}
