package es.unex.meigas.core.parameters;

public class DistributionStats {

   public double sum   = 0;
   public double mean  = 0;
   public double count = 0;


   public void addValue(final double value) {

      sum += value;
      count++;
      mean = sum / count;

   }


   public void addStats(final DistributionStats stats) {

      sum += stats.sum;
      count += stats.count;
      mean = sum / count;

   }

}
