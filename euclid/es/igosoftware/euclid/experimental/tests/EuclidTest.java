

package es.igosoftware.euclid.experimental.tests;

import java.util.List;

import junit.framework.TestCase;
import es.igosoftware.euclid.shape.GLinesStrip2D;
import es.igosoftware.euclid.shape.GSegment2D;
import es.igosoftware.euclid.shape.GSegment3D;
import es.igosoftware.euclid.shape.IPolygonalChain2D;
import es.igosoftware.euclid.vector.GVector2D;
import es.igosoftware.euclid.vector.GVector3D;
import es.igosoftware.util.GMath;


public class EuclidTest
         extends
            TestCase {

   public void testGetEdges() throws Exception {
      final GVector2D p1 = new GVector2D(0, 0);
      final GVector2D p2 = new GVector2D(2, 0);
      final GVector2D p3 = new GVector2D(2, 5);
      final IPolygonalChain2D geom = new GLinesStrip2D(false, p1, p2, p3);
      final List<GSegment2D> edges = geom.getEdges();
      assertTrue(edges.size() == 2);
      assertTrue(edges.get(0)._from.equals(p1));
      assertTrue(edges.get(0)._to.equals(p2));
      assertTrue(edges.get(1)._from.equals(p2));
      assertTrue(edges.get(1)._to.equals(p3));
   }


   public void testSegment3DDistance() throws Exception {
      final GSegment3D segment = new GSegment3D(new GVector3D(0, 0, 0), new GVector3D(0, 10, 5));
      final GVector3D p = new GVector3D(0.0, 0.8944271909999159, 0.4472135954999579);
      final double distance = segment.squaredDistance(p);
      System.out.println(distance);
      assertTrue(GMath.closeToZero(distance));
   }
}
