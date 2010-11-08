package es.igosoftware.util;


public class GKahanSummer {
   private double _sum          = 0;
   private double _compensation = 0;


   /**
    * Return the current corrected value of the running sum.
    * 
    * @return the running sum's value
    */
   public double value() {
      return _sum + _compensation;
   }


   /**
    * Add the value of an addend to the running sum.
    * 
    * @param the
    *           addend value
    */
   public void add(final double addend) {
      // Correct the addend value and add it to the running sum.
      final double correctedAddend = addend + _compensation;
      final double tempSum = _sum + correctedAddend;

      // Compute the next compensation and set the running sum.
      // The parentheses are necessary to compute the high-order
      // bits of the addend.
      _compensation = correctedAddend - (tempSum - _sum);
      _sum = tempSum;
   }


   //   public static void main(final String[] args) {
   //      System.out.println("GKahanSummer 0.1");
   //      System.out.println("----------------\n");
   //
   //      final Random random = new Random();
   //      final double[] values = new double[127000000];
   //      for (int i = 0; i < values.length; i++) {
   //         values[i] = 4000000d + (random.nextDouble() * 60000);
   //      }
   //
   //      System.out.println(GMath.plainSum(values) / values.length);
   //
   //      System.out.println(GMath.kahanSum(values) / values.length);
   //
   //      final GKahanSummer summer = new GKahanSummer();
   //      for (final double value : values) {
   //         summer.add(value);
   //      }
   //      System.out.println(summer.value() / values.length);
   //   }

}
