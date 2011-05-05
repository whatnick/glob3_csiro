

package es.igosoftware.euclid.experimental.algorithms;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import es.igosoftware.euclid.shape.GSimplePolygon2D;
import es.igosoftware.euclid.shape.GTriangle;
import es.igosoftware.euclid.shape.GTriangle2D;
import es.igosoftware.euclid.utils.GShapeUtils;
import es.igosoftware.euclid.vector.IVector2;
import es.igosoftware.util.GAssert;


public class GEarClippingTriangulator2D
         implements
            IAlgorithm<IVector2, GEarClippingTriangulator2D.Parameters, IVector2, GEarClippingTriangulator2D.Result> {


   public static class Parameters
            implements
               IAlgorithmParameters<IVector2> {


      private final GSimplePolygon2D _polygon;


      public Parameters(final GSimplePolygon2D polygon) {
         GAssert.notNull(polygon, "polygon");
         _polygon = polygon;
      }


   }


   public static class Result
            implements
               IAlgorithmResult<IVector2> {

      private final List<GTriangle2D> _triangles;


      private Result(final List<GTriangle2D> triangles) {
         _triangles = triangles;
      }


      public List<GTriangle2D> getTriangles() {
         return _triangles;
      }
   }


   @Override
   public String getName() {
      return "Ear Clipping Triangulator 2D";
   }


   @Override
   public String getDescription() {
      return "See http://en.wikipedia.org/wiki/Polygon_triangulation#Ear_clipping_method";
   }


   public static List<GTriangle2D> triangulate(final GSimplePolygon2D polygon) {
      // from: http://codesuppository.blogspot.com/2009/07/polygon-triangulator-ear-clipping.html

      final List<GTriangle2D> result = new ArrayList<GTriangle2D>();

      final int pointsCount = polygon.getPointsCount();
      final List<IVector2> points = polygon.getPoints();

      if (pointsCount < 3) {
         return Collections.emptyList();
      }

      final int[] indices = new int[pointsCount];
      final boolean flipped;
      if (GShapeUtils.signedArea2(polygon.getPoints()) >= 0) {
         for (int v = 0; v < pointsCount; v++) {
            indices[v] = v;
         }
         flipped = false;
      }
      else {
         for (int v = 0; v < pointsCount; v++) {
            indices[v] = (pointsCount - 1) - v;
         }
         flipped = true;
      }

      int nv = pointsCount;
      int count = 2 * nv;
      for (int m = 0, v = nv - 1; nv > 2;) {
         if (0 >= (count--)) {
            return Collections.emptyList();
         }

         int u = v;
         if (nv <= u) {
            u = 0;
         }
         v = u + 1;
         if (nv <= v) {
            v = 0;
         }
         int w = v + 1;
         if (nv <= w) {
            w = 0;
         }

         if (isEar(u, v, w, nv, indices, points)) {
            int a, b, c, s, t;
            a = indices[u];
            b = indices[v];
            c = indices[w];
            if (flipped) {
               result.add(new GTriangle2D(points.get(a), points.get(b), points.get(c)));
            }
            else {
               result.add(new GTriangle2D(points.get(c), points.get(b), points.get(a)));
            }
            m++;
            for (s = v, t = v + 1; t < nv; s++, t++) {
               indices[s] = indices[t];
            }
            nv--;
            count = 2 * nv;
         }
      }

      return result;
   }

   private static final double EPSILON = 0.00000000001;


   private static boolean isEar(final int u,
                                final int v,
                                final int w,
                                final int n,
                                final int[] indices,
                                final List<IVector2> points) {

      final IVector2 a = points.get(indices[u]);
      final IVector2 b = points.get(indices[v]);
      final IVector2 c = points.get(indices[w]);

      if (EPSILON > ((b.x() - a.x()) * (c.y() - a.y()) - (b.y() - a.y()) * (c.x() - a.x()))) {
         return false;
      }

      for (int p = 0; p < n; p++) {
         if ((p == u) || (p == v) || (p == w)) {
            continue;
         }

         if (GTriangle.isTriangleContainsPoint(a, b, c, points.get(indices[p]))) {
            return false;
         }
      }

      return true;
   }


   @Override
   public GEarClippingTriangulator2D.Result apply(final GEarClippingTriangulator2D.Parameters parameters) {
      final List<GTriangle2D> triangles = triangulate(parameters._polygon);
      return new GEarClippingTriangulator2D.Result(triangles);
   }

}
