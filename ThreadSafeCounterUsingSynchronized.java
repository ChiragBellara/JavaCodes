// Implementation of a Synchronized Block to create a Thread Safe Counter.
class Counter{
  public int counter = 0;
  public Object mutex = new Object();

  public int increment(){
    synchronized(mutex){
      counter++;
    }
    return counter;
  }
  public int decrement(){
    synchronized(mutex){
      counter--;
    }
    return counter;
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

public class ThreadSafeCounterUsingSynchronized{
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
