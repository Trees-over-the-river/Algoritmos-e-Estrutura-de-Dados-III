package utils;

public final class WatchTime {
   
   private final String label;
   private long begin = 0;
   private long end = 0;

   public WatchTime() {
      this(null);
   }

   public WatchTime(String label) {
      this.label = label;
   }

   public String getLabel() {
      return label;
   }

   public void start() {
      if(begin == 0) {
         begin = System.currentTimeMillis();
      } else {
         begin += System.currentTimeMillis() - end;
      }
   }

   public void suspend() {
      end = System.currentTimeMillis();
   }
 
   public void reset() {
      begin = end = 0;
   }

   public long stop() {
      end = System.currentTimeMillis();
      return end - begin;
   }

   public long elapsed() {
      return (end != 0) ? end - begin : System.currentTimeMillis() - begin;
   }

   @Override 
   public String toString() {
      StringBuffer sb = new StringBuffer();
      sb.append(this.elapsed());
      sb.append("ms");
      return sb.toString();
   }

}
