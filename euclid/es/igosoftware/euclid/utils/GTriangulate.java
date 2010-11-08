package es.igosoftware.euclid.utils;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import es.igosoftware.euclid.bounding.GAxisAlignedRectangle;
import es.igosoftware.euclid.vector.GVector2D;
import es.igosoftware.euclid.vector.IVector2;
import es.igosoftware.util.GMath;


public class GTriangulate {

   public static class IndexedTriangle {
      public final int _v0;
      public final int _v1;
      public final int _v2;


      IndexedTriangle(final int v0,
                      final int v1,
                      final int v2) {
         _v0 = v0;
         _v1 = v1;
         _v2 = v2;
      }
   }


   private static final class VectorAndIndex {
      private final IVector2<?> _vector;
      private final int         _originalIndex;


      public VectorAndIndex(final IVector2<?> vector,
                            final int index) {
         _vector = vector;
         _originalIndex = index;
      }
   }


   private static final class IEdge {
      private int from, to;


      IEdge() {
         from = -1;
         to = -1;
      }
   }


   private static final class Circle {
      //      double xc;
      //      double yc;
      private GVector2D center;
      private double    radius;
   }


   /*
     Return TRUE if a point is inside the circumcircle made up
     of the points (x1,y1), (x2,y2), (x3,y3)
     The circumcircle is returned in circle
     NOTE: A point on the edge is inside the circumcircle
   */
   private static boolean CircumCircle(final IVector2<?> point,
                                       final double x1,
                                       final double y1,
                                       final double x2,
                                       final double y2,
                                       final double x3,
                                       final double y3,
                                       final Circle circle) {

      /* Check for coincident points */
      if (GMath.closeTo(y1, y2) && GMath.closeTo(y2, y3)) {
         System.out.println("CircumCircle: Points are coincident.");
         return false;
      }

      double m2;
      double mx2;
      double my2;
      double xc;
      double yc;
      double m1;
      double mx1;
      double my1;

      if (GMath.closeTo(y2, y1)) {
         m2 = -(x3 - x2) / (y3 - y2);
         mx2 = (x2 + x3) / 2.0;
         my2 = (y2 + y3) / 2.0;
         xc = (x2 + x1) / 2.0;
         yc = m2 * (xc - mx2) + my2;
      }
      else if (GMath.closeTo(y3, y2)) {
         m1 = -(x2 - x1) / (y2 - y1);
         mx1 = (x1 + x2) / 2.0;
         my1 = (y1 + y2) / 2.0;
         xc = (x3 + x2) / 2.0;
         yc = m1 * (xc - mx1) + my1;
      }
      else {
         m1 = -(x2 - x1) / (y2 - y1);
         m2 = -(x3 - x2) / (y3 - y2);
         mx1 = (x1 + x2) / 2.0;
         mx2 = (x2 + x3) / 2.0;
         my1 = (y1 + y2) / 2.0;
         my2 = (y2 + y3) / 2.0;
         xc = (m1 * mx1 - m2 * mx2 + my2 - my1) / (m1 - m2);
         yc = m1 * (xc - mx1) + my1;
      }

      double dx = x2 - xc;
      double dy = y2 - yc;
      final double rsqr = dx * dx + dy * dy;
      final double r = Math.sqrt(rsqr);

      dx = point.x() - xc;
      dy = point.y() - yc;
      final double drsqr = dx * dx + dy * dy;

      circle.center = new GVector2D(xc, yc);
      circle.radius = r;

      return (drsqr <= rsqr);
   }


   public static IndexedTriangle[] triangulate(final List<IVector2<?>> points) {
      return triangulate(points.toArray(new IVector2<?>[points.size()]));
   }


   public static IndexedTriangle[] triangulate(final IVector2<?>... originalPoints) {
      final int pointsCount = originalPoints.length;

      if (pointsCount < 3) {
         throw new IllegalArgumentException("Insufficient points, 3 or more points are needed");
      }

      final IndexedTriangle triangles[] = new IndexedTriangle[pointsCount * 3];
      //      for (int i = 0; i < triangles.length; i++) {
      //         triangles[i] = new IndexedTriangle();
      //      }

      // save original indexes before sorting
      final VectorAndIndex[] points = new VectorAndIndex[pointsCount + 3];
      for (int i = 0; i < pointsCount; i++) {
         points[i] = new VectorAndIndex(originalPoints[i], i);
      }

      final IVector2.DefaultComparator vectorComparator = new IVector2.DefaultComparator();
      Arrays.sort(points, new Comparator<VectorAndIndex>() {
         @Override
         public int compare(final VectorAndIndex o1,
                            final VectorAndIndex o2) {
            if (o1 == null) {
               return 1;
            }
            if (o2 == null) {
               return -1;
            }
            return vectorComparator.compare(o1._vector, o2._vector);
         }
      });


      int nedge = 0;
      int emax = pointsCount;

      int ntri = 0;

      /* Allocate memory for the completeness list, flag for each triangle */
      final int trimax = 4 * pointsCount;
      final boolean[] complete = new boolean[trimax];
      for (int ic = 0; ic < trimax; ic++) {
         complete[ic] = false;
      }

      /* Allocate memory for the edge list */
      IEdge[] edges = new IEdge[emax];
      for (int ie = 0; ie < emax; ie++) {
         edges[ie] = new IEdge();
      }

      /*
      Find the maximum and minimum vertex bounds.
      This is to allow calculation of the bounding triangle
      */
      final GAxisAlignedRectangle bounds = GAxisAlignedRectangle.minimumBoundingRectangle(originalPoints);
      final IVector2<?> extent = bounds.getExtent();
      final double dmax = Math.max(extent.x(), extent.y());
      final IVector2<?> center = bounds.getCenter();

      /*
       Set up the supertriangle
       This is a triangle which encompasses all the sample points.
       The supertriangle coordinates are added to the end of the
       vertex list. The supertriangle is the first triangle in
       the triangle list.
      */
      points[pointsCount + 0] = new VectorAndIndex(new GVector2D(center.x() - 2.0 * dmax, center.y() - dmax), pointsCount + 0);
      points[pointsCount + 1] = new VectorAndIndex(new GVector2D(center.x(), center.y() + 2.0 * dmax), pointsCount + 1);
      points[pointsCount + 2] = new VectorAndIndex(new GVector2D(center.x() + 2.0 * dmax, center.y() - dmax), pointsCount + 2);

      //      triangles[0].v0 = pointsCount;
      //      triangles[0].v1 = pointsCount + 1;
      //      triangles[0].v2 = pointsCount + 2;
      triangles[0] = new IndexedTriangle(pointsCount, pointsCount + 1, pointsCount + 2);
      complete[0] = false;
      ntri = 1;


      /*
              Include each point one at a time into the existing mesh
      */
      for (int i = 0; i < pointsCount; i++) {
         final IVector2<?> vector = points[i]._vector;

         nedge = 0;

         /*
           Set up the edge buffer.
           If the point lies inside the circumcircle then the
           three edges of that triangle are added to the edge buffer
           and that triangle is removed.
         */
         for (int j = 0; j < ntri; j++) {
            if (complete[j]) {
               continue;
            }

            final double x1 = points[triangles[j]._v0]._vector.x();
            final double y1 = points[triangles[j]._v0]._vector.y();

            final double x2 = points[triangles[j]._v1]._vector.x();
            final double y2 = points[triangles[j]._v1]._vector.y();

            final double x3 = points[triangles[j]._v2]._vector.x();
            final double y3 = points[triangles[j]._v2]._vector.y();

            final Circle circle = new Circle();
            final boolean inside = CircumCircle(vector, x1, y1, x2, y2, x3, y3, circle);
            if (circle.center._x + circle.radius < vector.x()) {
               complete[j] = true;
            }
            if (inside) {
               /* Check that we haven't exceeded the edge list size */
               if (nedge + 3 >= emax) {
                  emax += 100;
                  final IEdge[] edges_n = new IEdge[emax];
                  for (int ie = 0; ie < emax; ie++) {
                     edges_n[ie] = new IEdge();
                  }
                  System.arraycopy(edges, 0, edges_n, 0, edges.length);
                  edges = edges_n;
               }

               edges[nedge + 0].from = triangles[j]._v0;
               edges[nedge + 0].to = triangles[j]._v1;
               edges[nedge + 1].from = triangles[j]._v1;
               edges[nedge + 1].to = triangles[j]._v2;
               edges[nedge + 2].from = triangles[j]._v2;
               edges[nedge + 2].to = triangles[j]._v0;
               nedge += 3;

               //               triangles[j].v0 = triangles[ntri - 1].v0;
               //               triangles[j].v1 = triangles[ntri - 1].v1;
               //               triangles[j].v2 = triangles[ntri - 1].v2;
               triangles[j] = new IndexedTriangle(triangles[ntri - 1]._v0, triangles[ntri - 1]._v1, triangles[ntri - 1]._v2);
               complete[j] = complete[ntri - 1];
               ntri--;
               j--;
            }
         }

         /*
                 Tag multiple edges
                 Note: if all triangles are specified anticlockwise then all
                 interior edges are opposite pointing in direction.
         */
         for (int j = 0; j < nedge - 1; j++) {
            //if ( !(edges[j].p1 < 0 && edges[j].p2 < 0) )
            for (int k = j + 1; k < nedge; k++) {
               if ((edges[j].from == edges[k].to) && (edges[j].to == edges[k].from)) {
                  edges[j].from = -1;
                  edges[j].to = -1;
                  edges[k].from = -1;
                  edges[k].to = -1;
               }

               /* Shouldn't need the following, see note above */
               if ((edges[j].from == edges[k].from) && (edges[j].to == edges[k].to)) {
                  edges[j].from = -1;
                  edges[j].to = -1;
                  edges[k].from = -1;
                  edges[k].to = -1;
               }
            }
         }

         /*
                 Form new triangles for the current point
                 Skipping over any tagged edges.
                 All edges are arranged in clockwise order.
         */
         for (int j = 0; j < nedge; j++) {
            if ((edges[j].from == -1) || (edges[j].to == -1)) {
               continue;
            }
            if (ntri >= trimax) {
               return null;
            }
            //            triangles[ntri].v0 = edges[j].from;
            //            triangles[ntri].v1 = edges[j].to;
            //            triangles[ntri].v2 = i;
            triangles[ntri] = new IndexedTriangle(edges[j].from, edges[j].to, i);
            complete[ntri] = false;
            ntri++;
         }
      }


      /*
              Remove triangles with supertriangle vertices
              These are triangles which have a vertex number greater than nv
      */
      for (int i = 0; i < ntri; i++) {
         if ((triangles[i]._v0 >= pointsCount) || (triangles[i]._v1 >= pointsCount) || (triangles[i]._v2 >= pointsCount)) {
            triangles[i] = triangles[ntri - 1];
            ntri--;
            i--;
         }
      }

      // convert the result to indexes of the original (before sorting) points
      final IndexedTriangle[] result = new IndexedTriangle[ntri];
      for (int i = 0; i < ntri; i++) {
         //         final IndexedTriangle resultTriangle = new IndexedTriangle();
         //         resultTriangle.v0 = points[triangles[i].v0]._originalIndex;
         //         resultTriangle.v1 = points[triangles[i].v1]._originalIndex;
         //         resultTriangle.v2 = points[triangles[i].v2]._originalIndex;
         //         result[i] = resultTriangle;

         final int v0 = points[triangles[i]._v0]._originalIndex;
         final int v1 = points[triangles[i]._v1]._originalIndex;
         final int v2 = points[triangles[i]._v2]._originalIndex;
         result[i] = new IndexedTriangle(v0, v1, v2);
      }

      return result;
   }


   public static void main(final String[] args) {
      final IVector2<?>[] points = new IVector2<?>[3200];

      for (int i = 0; i < points.length; i++) {
         points[i] = new GVector2D(1280 * Math.random(), 1024 * Math.random());
      }
      //      points[0] = new GVector2D(1, 0);
      //      points[1] = new GVector2D(2, 0);
      //      points[2] = new GVector2D(3, 0);
      //      points[3] = new GVector2D(4, 0);
      //      points[4] = new GVector2D(5, 0);
      //      points[5] = new GVector2D(6, 0);

      //Utils.delay(20000);

      final long start = System.currentTimeMillis();
      IndexedTriangle[] triangles = null;
      //final int count = 20000000;
      //for (int i = 0; i < count; i++) {
      triangles = triangulate(points);
      //}
      final long elapsed = System.currentTimeMillis() - start;
      System.out.println("Triangulation (" + triangles.length + " triangles) of " + points.length + " points done in " + elapsed
                         + "ms ");

      //      GCollections.concurrentEvaluate(array, evaluator)

      //      /*
      //        copy-paste the following output into free processing:
      //        http://processing.org/
      //      */
      //      System.out.println("size(1280, 1024); noFill();");
      //
      //      for (final IVector2<?> point : points) {
      //         System.out.println("rect(" + (point.x() - 1.5) + "," + (point.y() - 1.5) + ", 3, 3);");
      //      }
      //
      //      System.out.println("beginShape(TRIANGLES);");
      //      for (final IndexedTriangle triangle : triangles) {
      //         System.out.println("vertex" + points[triangle._v0] + ";");
      //         System.out.println("vertex" + points[triangle._v1] + ";");
      //         System.out.println("vertex" + points[triangle._v2] + ";");
      //      }
      //      System.out.println("endShape();");

   }
}
