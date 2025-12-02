import java.util.ArrayDeque;
import java.util.Deque;
import java.util.concurrent.ConcurrentHashMap;

class SlidingWindowIP{
  private final long windowSizeMillis;
  private final int maxRequestsPerWindow;
  private final ConcurrentHashMap<String, Deque<Long>> ipRequestLog = new ConcurrentHashMap<>();

  public SlidingWindowIP(long windowSize, int maxRequests){
    this.windowSizeMillis = windowSize;
    this.maxRequestsPerWindow = maxRequests;
  }

  public boolean isRequestAllowed(String ip){
    long currentTime = System.currentTimeMillis();
    Deque<Long> que = ipRequestLog.computeIfAbsent(ip, k -> new ArrayDeque<Long>());

    synchronized(que){
      // Remove values that lie outside the boundary
      long boundary = currentTime - this.windowSizeMillis;
      while(!que.isEmpty() && que.peekFirst() < boundary){
        que.pollFirst();
      }

      // Check how many requests are left
      if(que.size() < this.maxRequestsPerWindow){
        que.addLast(currentTime);
        return true;
      }
      return false;
    }
  }
}

public class SlidingWindowIPRateLimit {
    public static void main(String[] args) throws InterruptedException {
        // Allow up to 5 requests per 9 seconds per IP
        SlidingWindowIP limiter =
                new SlidingWindowIP(9_000, 5);

        String ip = "192.168.1.10";

        System.out.println("=== First burst (should allow 5, then block) ===");
        for (int i = 1; i <= 7; i++) {
            boolean allowed = limiter.isRequestAllowed(ip);
            System.out.println("Request " + i + " -> " + allowed);
        }

        System.out.println("\nSleeping 9.5 seconds...");
        Thread.sleep(9500);

        System.out.println("\n=== Second burst (some requests should be freed up) ===");
        for (int i = 8; i <= 18; i++) {
            boolean allowed = limiter.isRequestAllowed(ip);
            System.out.println("Request " + i + " -> " + allowed);
        }
    }
}
