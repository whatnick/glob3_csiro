package es.igosoftware.statistics;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;

import junit.framework.Assert;

import org.junit.Test;

import es.igosoftware.statistics.GHistogram.DataQuantityCalculator;

public class GHistogramTest {

   private static final double[] DOUBLES_0_TO_19 = new double[] { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17,
            18, 19                              };
   private static final double[] DOUBLES_0_TO_9  = new double[] { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9 };


   private GHistogram<Double> createDoubles0To99GHistogram() {
      final ArrayList<Double> data = new ArrayList<Double>();
      for (int i = 0; i < 100; i++) {
         data.add(new Double(i));
      }

      return new GHistogram<Double>(data, 10, true, new DataQuantityCalculator<Double>() {
         @Override
         public double quantity(final Double sample) {
            return sample;
         }
      }, new Comparator<Double>() {
         @Override
         public int compare(final Double value0,
                            final Double value1) {
            return Double.compare(value0, value1);
         }
      });

   }


   private GHistogram<Double> createDoubles0To9GHistogram() {
      final ArrayList<Double> data = new ArrayList<Double>();
      for (int i = 0; i < 10; i++) {
         data.add(new Double(i));
      }

      return new GHistogram<Double>(data, 10, true, new DataQuantityCalculator<Double>() {
         @Override
         public double quantity(final Double sample) {
            return sample;
         }
      }, new Comparator<Double>() {
         @Override
         public int compare(final Double value0,
                            final Double value1) {
            return Double.compare(value0, value1);
         }
      });

   }


   @Test
   public void testDoubles0To9GetData00To10() {
      testDataRange(createDoubles0To9GHistogram(), 0.0, 0.1, new double[] { 0 });
   }


   @Test
   public void testDoubles0To9GetData00To100() {
      testDataRange(createDoubles0To9GHistogram(), 0.0, 1.0, new double[] { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9 });
   }


   @Test
   public void testDoubles0To9GetData00To20() {
      testDataRange(createDoubles0To9GHistogram(), 0.0, 0.2, new double[] { 0, 1 });
   }


   @Test
   public void testDoubles0To9GetData11To30() {
      testDataRange(createDoubles0To9GHistogram(), 0.11, 0.3, new double[] { 1, 2 });
   }


   @Test
   public void testDoubles0To9GetData90To100() {
      testDataRange(createDoubles0To9GHistogram(), 0.9, 1.0, new double[] { 8, 9 });
   }


   @Test
   public void testDoubles0To99GetData00To05() {
      testDataRange(createDoubles0To99GHistogram(), 0.0, 0.05, DOUBLES_0_TO_9);
   }


   @Test
   public void testDoubles0To99GetData00To10() {
      testDataRange(createDoubles0To99GHistogram(), 0.0, 0.1, DOUBLES_0_TO_9);
   }


   @Test
   public void testDoubles0To99GetData00To11() {
      testDataRange(createDoubles0To99GHistogram(), 0.0, 0.11, DOUBLES_0_TO_19);
   }


   @Test
   public void testDoubles0To99GetData05To10() {
      testDataRange(createDoubles0To99GHistogram(), 0.05, 0.10, DOUBLES_0_TO_9);
   }


   @Test
   public void testDoubles0To99GetData05To11() {
      testDataRange(createDoubles0To99GHistogram(), 0.05, 0.11, DOUBLES_0_TO_19);
   }


   @Test
   public void testDoubles0To99GetData04To11() {
      testDataRange(createDoubles0To99GHistogram(), 0.04, 0.11, DOUBLES_0_TO_19);
   }


   private void testDataRange(final GHistogram<Double> histogram,
                              final double from,
                              final double to,
                              final double[] expected) {
      final String msg = (from * 100f) + " to " + (100f * to) + " percent";

      final Collection<Double> dataRange = histogram.getData(from, to);

      boolean error = false;
      if (dataRange.size() != expected.length) {
         error = true;
      }
      else {
         for (int i = 0; i < expected.length; i++) {
            if (!dataRange.contains(expected[i])) {
               error = true;
               break;
            }
         }
      }

      if (error) {
         histogram.showStatistics();
         System.out.println();
         System.out.println(msg + ": expected " + Arrays.toString(expected) + " got " + dataRange);
         System.out.println();
         System.out.println();
      }

      Assert.assertEquals(msg, expected.length, dataRange.size());
      Assert.assertFalse(msg + " invalid values expected=" + Arrays.toString(expected) + " got " + dataRange, error);
   }
}
