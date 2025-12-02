import java.util.concurrent.locks.ReentrantLock;

class Counter{
  public int counter = 0;
  public ReentrantLock mutex = new ReentrantLock();

  public int increment(){
    mutex.lock();
    try{
      counter++;
      return counter;
    }finally{
      mutex.unlock();
    }
  }
  public int decrement(){
    mutex.lock();
    try{
      counter--;
      return counter;
    }finally{
      mutex.unlock();
    }
  }
}

class MyThread extends Thread{
  Counter counter;
  public MyThread(Counter c){
    this.counter = c;
  }
  public void run(){
    for(int i = 0; i<1000; i++){
      counter.increment();
      counter.decrement();
    }
  }
}

public class ThreadSafeCounterUsingMutex{
  public static void main(String[] args) throws InterruptedException{
    Counter c = new Counter();
    MyThread th1 = new MyThread(c);
    MyThread th2 = new MyThread(c);
    MyThread th3 = new MyThread(c);

    th1.start();
    th2.start();
    th3.start();

    th1.join();
    th2.join();
    th3.join();

    System.out.println("Counter = " + c.counter);
  }
}
