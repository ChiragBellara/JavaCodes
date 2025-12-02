class TokenBucketRateLimit{
  public long capacity;
  public long tokens;
  public long refillPeriod;
  public volatile long lastRefillTime;

  public TokenBucketRateLimit(long cap, long refillPeriod){
    this.capacity = cap;
    this.refillPeriod = refillPeriod;
    this.tokens = cap;
    this.lastRefillTime = System.currentTimeMillis();
  }

  public synchronized boolean isRequestAllowed(){
    long currentTimeMillis = System.currentTimeMillis();
    long elapsedTime = currentTimeMillis - this.lastRefillTime;

    long refill = elapsedTime * this.refillPeriod / 1000;
    if (refill > 0){
      this.tokens = Math.min(this.capacity, this.tokens + refill);
      this.lastRefillTime = currentTimeMillis;
    }

    if(this.tokens > 0){
      this.tokens--;
      return true;
    }
    return false;
  }
}

class TokenBucketRateLimitImpl{
  public static void main(String[] args) throws Exception {
    TokenBucketRateLimit limiter = new TokenBucketRateLimit(10, 10);

    Runnable r = () -> {
        int allowed = 0;
        long end = System.currentTimeMillis() + 1000;
        while (System.currentTimeMillis() < end) {
            if (limiter.isRequestAllowed()) {
                allowed++;
            }
        }
        System.out.println(Thread.currentThread().getName() + " -> " + allowed);
    };

    Thread t1 = new Thread(r, "T1");
    Thread t2 = new Thread(r, "T2");
    Thread t3 = new Thread(r, "T3");

    t1.start(); t2.start(); t3.start();
    t1.join(); t2.join(); t3.join();
  }
}
