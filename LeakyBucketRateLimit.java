import java.util.concurrent.atomic.AtomicLong;

class LeakyBucketRateLimit{
  public long capacity;
  public long ratePerSecond;
  public AtomicLong currentBucketSize;
  public AtomicLong lastRequestTime;

  public LeakyBucketRateLimit(long cap, long rate){
    this.capacity = cap;
    this.ratePerSecond = rate;
    this.currentBucketSize = new AtomicLong(0);
    this.lastRequestTime = new AtomicLong(System.currentTimeMillis());
  }

  public boolean isRequestAllowed(){
    long currentTimeMillis = System.currentTimeMillis();
    long timeSinceLastRequest = currentTimeMillis - this.lastRequestTime.getAndSet(currentTimeMillis);

    long leakedTokenCount = timeSinceLastRequest * ratePerSecond / 1000;
    this.currentBucketSize.updateAndGet(bucketSize -> Math.max(0, bucketSize - leakedTokenCount));

    if(this.currentBucketSize.get() < this.capacity){
      this.currentBucketSize.incrementAndGet();
      return true;
    }

    return false;
  }
}

class LeakyBucketRateLimit{
  public static void main(String[] args) throws InterruptedException{
    LeakyBucketRateLimit rateLimiter = new LeakyBucketRateLimit(5, 2);
    for(int i = 1; i < 15; i++){
      System.out.println(rateLimiter.isRequestAllowed());
    }
    
    Thread.sleep(1000);

    for(int i = 1; i < 15; i++){
      System.out.println(rateLimiter.isRequestAllowed());
    }
  }
}
