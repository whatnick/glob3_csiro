package es.igosoftware.util;

public class GRange<T extends Comparable<T>> {

   public final T _lower;
   public final T _upper;


   public GRange(final T lower,
                 final T upper) {
      GAssert.notNull(lower, "lower");
      GAssert.notNull(upper, "upper");


      final T max;
      final T min;
      if (lower.compareTo(upper) > 0) {
         min = upper;
         max = lower;
      }
      else {
         min = lower;
         max = upper;
      }

      _lower = min;
      _upper = max;
   }


   @Override
   public String toString() {
      return "GRange [" + _lower + " -> " + _upper + "]";
   }


   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((_lower == null) ? 0 : _lower.hashCode());
      result = prime * result + ((_upper == null) ? 0 : _upper.hashCode());
      return result;
   }


   @Override
   public boolean equals(final Object obj) {
      if (this == obj) {
         return true;
      }

      if (obj == null) {
         return false;
      }
      if (getClass() != obj.getClass()) {
         return false;
      }

      @SuppressWarnings("unchecked")
      final GRange<T> other = (GRange<T>) obj;
      if (_lower == null) {
         if (other._lower != null) {
            return false;
         }
      }
      else if (_lower.compareTo(other._lower) != 0) {
         return false;
      }
      if (_upper == null) {
         if (other._upper != null) {
            return false;
         }
      }
      else if (_upper.compareTo(other._upper) != 0) {
         return false;
      }
      return true;
   }


   public T getLower() {
      return _lower;
   }


   public T getUpper() {
      return _upper;
   }


   //   public static void main(final String[] args) {
   //      System.out.println("GRange 0.1");
   //      System.out.println("----------\n");
   //
   //      final GRange<Double> range = new GRange<Double>(1.0, 10.0);
   //      System.out.println(range);
   //   }

}
