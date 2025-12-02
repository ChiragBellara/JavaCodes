import java.util.ArrayList;
import java.util.List;

// Implementation of a ReaderWriter Lock Class
class ReaderWriterLock{
  private int readers;
  private int writers;
  private int writeRequests;

  public synchronized void lockReader() throws InterruptedException{
    // Check if we can read
    while(writers > 0 || writeRequests > 0){
      wait();
    }

    // If we are not waiting, we move to performing the read task
    readers++;
  }

  public synchronized void unlockReader() throws InterruptedException{
    // This means that the reading task is done. So notify all the writers that the resource is not free to use.
    readers--;
    notifyAll();
  }

  public synchronized void lockWriter() throws InterruptedException{
    writeRequests++;
    try{
      // Check if we can perform the write task? If not, we wait.
      while(readers > 0 || writers > 0){
        wait();
      }
      // Once the wait is over, perform the write task
      writers++;
    }finally{
      writeRequests--;
    }
  }

  public synchronized void unlockWriter() throws InterruptedException{
    // This means that the writing task is done. So notify all the readers that the resource is not free to use.
    writers--;
    notifyAll();
  }
}


class SharedData {
    private final List<Integer> data = new ArrayList<>();
    private final ReaderWriterLock rwLock = new ReaderWriterLock();

    public void write(int value) throws InterruptedException {
        rwLock.lockWriter();
        try {
            System.out.println(Thread.currentThread().getName() + " writing " + value);
            data.add(value);
            Thread.sleep(50); // simulate work
        } finally {
            rwLock.unlockWriter();
        }
    }

    public int read() throws InterruptedException {
        rwLock.lockReader();
        try {
            int size = data.size();
            System.out.println(Thread.currentThread().getName() + " reading size " + size);
            Thread.sleep(20); // simulate work
            return size;
        } finally {
            rwLock.unlockReader();
        }
    }
}

public class ReadWriteLockImpl {
    public static void main(String[] args) {
        SharedData shared = new SharedData();

        Runnable reader = () -> {
            try {
                for (int i = 0; i < 5; i++) {
                    shared.read();
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        };

        Runnable writer = () -> {
            try {
                for (int i = 0; i < 5; i++) {
                    shared.write(i);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        };

        Thread r1 = new Thread(reader, "Reader-1");
        Thread r2 = new Thread(reader, "Reader-2");
        Thread w1 = new Thread(writer, "Writer-1");

        r1.start();
        r2.start();
        w1.start();
    }
}
