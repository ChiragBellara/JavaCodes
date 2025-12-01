/*
This is an implementation of a Thread Safe Queue using Mutex Locks and Conditions.
*/

import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.Condition;

class SimpleQueue {
    private final int[] data;
    private int head = 0;
    private int tail = 0;
    private int size = 0;
    private final ReentrantLock mutex = new ReentrantLock();
    private final Condition notEmpty = mutex.newCondition();
    private final Condition notFull = mutex.newCondition();

    public SimpleQueue(int capacity) {
        this.data = new int[capacity];
    }

    public int size() {
        mutex.lock();
        try{
          return size;
        }finally{
          mutex.unlock();
        }
    }

    public void enqueue(int value) throws InterruptedException {
        mutex.lock();
        try{
          while (size == data.length) {
            notFull.await();
          }
          data[tail] = value;
          tail = (tail + 1) % data.length;
          size++;
          notEmpty.signalAll();
        }finally{
          mutex.unlock();
        }
    }

    public int dequeue() throws InterruptedException {
        mutex.lock();
        try{
          while (size == 0) {
            notEmpty.await();
          }
          int value = data[head];
          head = (head + 1) % data.length;
          size--;
          notFull.signalAll();
          return value;
        }finally{
          mutex.unlock(); 
        }
        
    }
}

class Producer extends Thread {
    private final SimpleQueue queue;
    private final int iterations;

    public Producer(SimpleQueue queue, int iterations) {
        this.queue = queue;
        this.iterations = iterations;
    }

    @Override
    public void run() {
        for (int i = 0; i < iterations; i++) {
            try {
                queue.enqueue(i);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
        }
    }
}

class Consumer extends Thread {
    private final SimpleQueue queue;
    private final int iterations;

    public Consumer(SimpleQueue queue, int iterations) {
        this.queue = queue;
        this.iterations = iterations;
    }

    @Override
    public void run() {
        for (int i = 0; i < iterations; i++) {
            try {
                queue.dequeue();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
        }
    }
}

public class Thread_Safe_Queue {
    public static void main(String[] args) throws InterruptedException {
        SimpleQueue queue = new SimpleQueue(1000);

        int iters = 100_000;

        Thread p1 = new Producer(queue, iters);
        Thread p2 = new Producer(queue, iters);
        Thread c1 = new Consumer(queue, iters);
        Thread c2 = new Consumer(queue, iters);

        p1.start();
        p2.start();
        c1.start();
        c2.start();

        p1.join();
        p2.join();
        c1.join();
        c2.join();

        System.out.println("Final queue size = " + queue.size());
        System.out.println("Expected size (roughly) = 0 (producers == consumers)");
    }
}
