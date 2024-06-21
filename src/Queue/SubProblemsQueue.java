package src.Queue;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import src.QFMDC;
import src.InitialPartition.IMakePartition;
import src.PreProcessing.DataContainer;

public class SubProblemsQueue {
    
    public DataContainer dataContainer;

    public PriorityQueue<Item> items;

    public Queue<Integer> free;

    // public ReentrantLock itemsLock, freeLock;

    // public Condition itemsNotEmpty, freeNotEmpty;

    public ReentrantLock lock;

    public Condition freeAndItemsAv;

    public IMakePartition makePartition;


    public int nFree;


    public SubProblemsQueue(DataContainer dataContainer, int nFree, IMakePartition makePartition){
        this.dataContainer = dataContainer;
        this.items = new PriorityQueue<>();
        this.free = new LinkedList<>();
        // this.itemsLock = new ReentrantLock();
        // this.freeLock = new ReentrantLock();
        this.lock = new ReentrantLock();

        this.freeAndItemsAv = this.lock.newCondition();


        for(int i = 0; i < nFree; i++){
            this.free.add(i);
        }

        this.nFree = nFree;
        this.makePartition = makePartition;

    }

    public void addItem(Item item){
        // System.out.println("Add Item enter");
        // System.out.println("trying for lock");
        this.lock.lock();
        // System.out.println("got lock");
        try{
            this.items.add(item);
            // System.out.println("item size : " + item.taxaPerLevelWithPartition.realTaxonCount + item.taxaPerLevelWithPartition.dummyTaxonCount);

            

            if(free.size() > 0){
                // System.out.println("signaling");
                this.freeAndItemsAv.signal();
            }
        }finally{
            this.lock.unlock();
            // System.out.println("released lock");
        }
        // System.out.println("Add Item exit");

    }

    public void free(int freedId){
        // System.out.println("Free enter");
        // System.out.println("trying for lock");
        this.lock.lock();
        // System.out.println("got lock");
        try{
            this.free.add(freedId);
            // if(items.size() > 0 || free.size() == nFree)
            this.freeAndItemsAv.signal();
        }finally{
            this.lock.unlock();
            // System.out.println("released lock");
        }
        // System.out.println("Free exit");
    }
    

    public void consumeItems(){
        // System.out.println("Consume Items enter");
        
        while(true){
            // System.out.println("consume items - trying for lock");
            this.lock.lock();
            // System.out.println("consume items - got lock");
            boolean exit = false;
            try{
                // if(this.free.size() == this.nFree && this.items.size() == 0){
                //     System.out.println("breaked");
                //     break;
                // }

                while(this.items.size() == 0 || this.free.size() == 0){
                    // System.out.println("Consuming items - going to wait");
                    this.freeAndItemsAv.await();
                    // System.out.println("Consuming items - awake");
                    if(this.items.size() == 0 && this.free.size() == this.nFree){
                        exit = true;
                        break;
                    }
                }
                if(exit){
                    break;
                }

                while(!this.items.isEmpty() && !this.free.isEmpty()){

                    Item item = this.items.poll();
                    int freeId = this.free.poll();
    
                    // int itemsz = item.taxaPerLevelWithPartition.realTaxonCount + item.taxaPerLevelWithPartition.dummyTaxonCount;
    
                    // for(Item i: this.items){
                    //     int csz = i.taxaPerLevelWithPartition.realTaxonCount + i.taxaPerLevelWithPartition.dummyTaxonCount;
                    //     if(csz > itemsz){
                    //         System.out.println("Error");
                    //     }
                    // }
    
                    // launch thread
                    new Thread(new Runnable(){
                        @Override
                        public void run(){
                            QFMDC.recurse(dataContainer, item.taxaPerLevelWithPartition, freeId, item.solutionNode, item.level, makePartition);
                        }
                    }).start();
                }                


                // this.dataContainer.subProblems.get(freeId).set(item);


            }catch(InterruptedException e){
                e.printStackTrace();
            }finally{
                this.lock.unlock();
            }
        }
    }


    public static SubProblemsQueue instance;

    public static void setInstance(DataContainer dataContainer, int nFree, IMakePartition makePartition){
        instance = new SubProblemsQueue(dataContainer, nFree, makePartition);
    }

}
