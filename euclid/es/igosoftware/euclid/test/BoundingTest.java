package es.igosoftware.euclid.test;

import junit.framework.Assert;

import org.junit.Test;

import es.igosoftware.euclid.bounding.GAxisAlignedBox;
import es.igosoftware.euclid.vector.GVector3D;

public class BoundingTest {

   private static final GVector3D       doubleUnit     = GVector3D.UNIT.scale(2);
   private static final GVector3D       doubleUnitNeg  = GVector3D.UNIT.scale(-2);


   private static final GAxisAlignedBox unitCube       = new GAxisAlignedBox(GVector3D.NEGATIVE_UNIT, GVector3D.UNIT);
   private static final GAxisAlignedBox doubleUnitCube = new GAxisAlignedBox(BoundingTest.doubleUnitNeg, BoundingTest.doubleUnit);
   private static final GAxisAlignedBox cube111222     = new GAxisAlignedBox(GVector3D.UNIT, BoundingTest.doubleUnit);
   private static final GAxisAlignedBox cube111222Neg  = new GAxisAlignedBox(BoundingTest.doubleUnitNeg, GVector3D.NEGATIVE_UNIT);
   private static final GAxisAlignedBox cube000222Neg  = new GAxisAlignedBox(GVector3D.ZERO, BoundingTest.doubleUnit);


   @Test
   public void boxBoxTest() {

      //case inside
      Assert.assertTrue("doubleUnitCube touches unitCube", BoundingTest.doubleUnitCube.touchesWithBox(BoundingTest.unitCube));
      Assert.assertTrue("unitCube touches doubleUnitCube", BoundingTest.unitCube.touchesWithBox(BoundingTest.doubleUnitCube));

      //case equals
      Assert.assertTrue("unitCube touches itself", BoundingTest.unitCube.touchesWithBox(BoundingTest.unitCube));

      //case outside
      Assert.assertFalse("cube111222 does not touch cube111222Neg",
               BoundingTest.cube111222.touchesWithBox(BoundingTest.cube111222Neg));
      Assert.assertFalse("cube111222Neg does not touch cube111222",
               BoundingTest.cube111222.touchesWithBox(BoundingTest.cube111222Neg));

      //case intersect
      Assert.assertTrue("cube000222Neg touches unitCube", BoundingTest.cube000222Neg.touchesWithBox(BoundingTest.unitCube));
      Assert.assertTrue("unitCube touches cube000222Neg", BoundingTest.unitCube.touchesWithBox(BoundingTest.cube000222Neg));

      //case intersect in one point
      Assert.assertTrue("unitCube touches cube111222", BoundingTest.unitCube.touchesWithBox(BoundingTest.cube111222));

   }


   @Test
   public void testContains() {
      testContains(new GAxisAlignedBox(new GVector3D(0, 0, 0), new GVector3D(100, 100, 100)));
      testContains(new GAxisAlignedBox(new GVector3D(-100, -10, -1), new GVector3D(100, 10, 1)));
   }


   private void testContains(final GAxisAlignedBox bounds) {
      final double delta = 0.00001;

      Assert.assertTrue("lower in box", bounds.contains(bounds._lower));
      Assert.assertTrue("upper in box", bounds.contains(bounds._upper));

      Assert.assertFalse("lessThanLower in box", bounds.contains(bounds._lower.sub(delta)));
      Assert.assertFalse("greaterThanUpper in box", bounds.contains(bounds._upper.add(delta)));

      final double step = 0.4;
      for (double x = bounds._lower.x(); x < bounds._upper.x(); x += step) {
         for (double y = bounds._lower.y(); y < bounds._upper.y(); y += step) {
            for (double z = bounds._lower.z(); z < bounds._upper.z(); z += step) {
               final GVector3D point = new GVector3D(x, y, z);
               assertContains(bounds, point);
            }
         }
      }

   }


   private void assertContains(final GAxisAlignedBox bounds,
                               final GVector3D point) {
      final boolean contains = bounds.contains(point);
      if (!contains) {
         Assert.fail(point + " in " + bounds);
      }
   }

}
