package es.igosoftware.util;

import java.util.Collection;

public final class GAssert {
   private static final Logger LOGGER = Logger.instance();


   public static void notEmpty(final Collection<?> collection,
                               final String description) {
      if (collection == null) {
         processNull(description);
      }
      else if (collection.isEmpty()) {
         processEmpty(description);
      }
   }


   public static void notNullElements(final Collection<?> collection,
                                      final String description) {
      for (final Object element : collection) {
         if (element == null) {
            processNull(description + " element ");
         }
      }
   }


   public static void notNullElements(final Object[] collection,
                                      final String description) {
      for (final Object element : collection) {
         if (element == null) {
            processNull(description + " element ");
         }
      }
   }


   public static void notEmpty(final Object[] array,
                               final String description) {
      if (array == null) {
         processNull(description);
      }
      else if (array.length == 0) {
         processEmpty(description);
      }
   }


   public static void notEmpty(final Iterable<?> iterable,
                               final String description) {
      if (iterable == null) {
         processNull(description);
      }
      else if (!iterable.iterator().hasNext()) {
         processEmpty(description);
      }
   }


   public static void notNull(final Object object,
                              final String description) {
      if (object != null) {
         return;
      }
      processNull(description);
   }


   private static void processEmpty(final String description) {
      final String msg = description + " is empty";
      throwError(msg);
   }


   private static void processNull(final String description) {
      final String msg = description + " is null";
      throwError(msg);
   }


   private static void throwError(final String msg) {
      LOGGER.severe(msg);
      throw new IllegalArgumentException(msg);
   }


   private GAssert() {}


   public static void isPositiveOrZero(final int value,
                                       final String description) {
      if (value >= 0) {
         return;
      }
      throwError(description + " must be positive or zero (" + value + ")");
   }


   public static void isPositive(final int value,
                                 final String description) {
      if (value > 0) {
         return;
      }
      throwError(description + " must be positive (" + value + ")");
   }


   public static void isPositive(final double value,
                                 final String description) {
      if (value > 0) {
         return;
      }
      throwError(description + " must be positive (" + value + ")");
   }


   public static void isBetween(final int value,
                                final int min,
                                final int max,
                                final String description) {
      if ((value < min) || (value > max)) {
         throwError(description + " (" + value + ") " + " is not between " + min + " and " + max);
      }
   }


   public static void notNan(final float value,
                             final String description) {
      if (Float.isNaN(value)) {
         throwError(description + " is Nan");
      }
   }


   public static void notNan(final double value,
                             final String description) {
      if (Double.isNaN(value)) {
         throwError(description + " is Nan");
      }
   }


   public static void notEmpty(final double[] array,
                               final String description) {
      if (array == null) {
         processNull(description);
      }
      else if (array.length == 0) {
         processEmpty(description);
      }
   }


   public static void notEmpty(final float[] array,
                               final String description) {
      if (array == null) {
         processNull(description);
      }
      else if (array.length == 0) {
         processEmpty(description);
      }
   }


   public static void notEmpty(final int[] array,
                               final String description) {
      if (array == null) {
         processNull(description);
      }
      else if (array.length == 0) {
         processEmpty(description);
      }
   }


   public static void notEmpty(final long[] array,
                               final String description) {
      if (array == null) {
         processNull(description);
      }
      else if (array.length == 0) {
         processEmpty(description);
      }
   }


   private static void processSizeError(final String description,
                                        final int expected,
                                        final int current) {
      final String msg = description + " size is incorrect (expected" + expected + " but got " + current + ")";
      throwError(msg);
   }


   public static void isSize(final double[] array,
                             final int size,
                             final String description) {
      if (array == null) {
         processNull(description);
      }
      else if (array.length != size) {
         processSizeError(description, size, array.length);
      }
   }


   public static void isTrue(final boolean bool,
                             final String description) {
      if (!bool) {
         throwError(description);
      }
   }


   public static void isSame(final Object object0,
                             final Object object1) {
      if (object0 != object1) {
         throwError(object0 + " is not the same object that " + object1);
      }
   }


}
