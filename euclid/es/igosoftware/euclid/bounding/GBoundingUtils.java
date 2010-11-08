package es.igosoftware.euclid.bounding;

import es.igosoftware.euclid.vector.IVector3;


public final class GBoundingUtils {

   private GBoundingUtils() {}


   //Boxes
   public static boolean touchesWithBox(final GAxisAlignedBox box1,
                                        final GAxisAlignedBox box2) {
      // from Real-Time Collision Detection - Christer Ericson
      //   page 79

      final IVector3<?> lower1 = box1._lower;
      final IVector3<?> upper1 = box1._upper;
      final IVector3<?> lower2 = box2._lower;
      final IVector3<?> upper2 = box2._upper;

      // Exit with no intersection if separated along an axis
      if ((upper1.x() < lower2.x()) || (lower1.x() > upper2.x())) {
         return false;
      }
      if ((upper1.y() < lower2.y()) || (lower1.y() > upper2.y())) {
         return false;
      }
      if ((upper1.z() < lower2.z()) || (lower1.z() > upper2.z())) {
         return false;
      }

      // Overlapping on all axes means AABBs are intersecting
      return true;
   }


   //   public static void main(final String[] args) {
   //      test(5.1);
   //      test(10000000000000000000000000000000000000000d);
   //      test(0);
   //      test(1);
   //      test(-1);
   //   }
   //
   //
   //   private static void test(final double d) {
   //      //      final double greater = Math.nextAfter(d, Double.POSITIVE_INFINITY);
   //      //      final double less = Math.nextAfter(d, Double.NEGATIVE_INFINITY);
   //      final double greater = GMath.nextUp(d);
   //      final double less = GMath.previousDown(d);
   //
   //      System.out.println(d + " greater: " + greater + " less: " + less);
   //      if (!(greater > d)) {
   //         System.err.println("greater not greater");
   //      }
   //      if (!(less < d)) {
   //         System.err.println("less not less");
   //      }
   //
   //   }

}
