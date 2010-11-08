package es.igosoftware.euclid.shape;

import java.util.List;

import es.igosoftware.euclid.vector.IVector2;
import es.igosoftware.euclid.vector.IVector3;
import es.igosoftware.util.GMath;


public final class GShape {


   private GShape() {}

   private static int trianglesCounter = 0;
   private static int quadsCounter     = 0;
   private static int polygonsCounter  = 0;


   public static IPolygon3D<?> createPolygon(final IVector3<?>... points) {
      final int pointsCount = points.length;

      if (pointsCount < 3) {
         throw new IllegalArgumentException("Can't create polygons with less than 3 points");
      }

      if (pointsCount == 3) {
         trianglesCounter++;
         //System.out.println("Creating triangle");
         return new GTriangle3D(points[0], points[1], points[2]);
      }

      if (pointsCount == 4) {
         quadsCounter++;
         //System.out.println("Creating quad");
         return new GQuad3D(points[0], points[1], points[2], points[3]);
      }

      polygonsCounter++;
      return new GSimplePolygon3D(points);
   }


   public static IPolygon2D<?> createPolygon(final IVector2<?>... points) {
      final int pointsCount = points.length;

      if (pointsCount < 3) {
         throw new IllegalArgumentException("Can't create polygons with less than 3 points");
      }

      if (pointsCount == 3) {
         trianglesCounter++;
         //System.out.println("Creating triangle");
         return new GTriangle2D(points[0], points[1], points[2]);
      }

      if (pointsCount == 4) {
         quadsCounter++;
         //System.out.println("Creating quad");
         return new GQuad2D(points[0], points[1], points[2], points[3]);
      }

      polygonsCounter++;
      return new GSimplePolygon2D(points);
   }


   public static IPolygon2D<?> createPolygon2(final List<IVector2<?>> points) {
      final int pointsCount = points.size();

      if (pointsCount < 3) {
         throw new IllegalArgumentException("Can't create polygons with less than 3 points");
      }

      if (pointsCount == 3) {
         trianglesCounter++;
         //System.out.println("Creating triangle");
         return new GTriangle2D(points.get(0), points.get(1), points.get(2));
      }

      if (pointsCount == 4) {
         quadsCounter++;
         //System.out.println("Creating quad");
         return new GQuad2D(points.get(0), points.get(1), points.get(2), points.get(3));
      }

      polygonsCounter++;
      return new GSimplePolygon2D(points);
   }


   public static IPolygon3D<?> createPolygon3(final List<IVector3<?>> points) {
      final int pointsCount = points.size();

      if (pointsCount < 3) {
         throw new IllegalArgumentException("Can't create polygons with less than 3 points");
      }

      if (pointsCount == 3) {
         trianglesCounter++;
         //System.out.println("Creating triangle");
         return new GTriangle3D(points.get(0), points.get(1), points.get(2));
      }

      if (pointsCount == 4) {
         quadsCounter++;
         //System.out.println("Creating quad");
         return new GQuad3D(points.get(0), points.get(1), points.get(2), points.get(3));
      }

      polygonsCounter++;
      return new GSimplePolygon3D(points);
   }


   public static void showStatistics() {
      final int total = trianglesCounter + quadsCounter + polygonsCounter;
      System.out.println("total shapes: " + total);
      System.out.println("triangles: " + format(trianglesCounter, total));
      System.out.println("quads: " + format(quadsCounter, total));
      System.out.println("polygons: " + format(polygonsCounter, total));
   }


   private static String format(final double value,
                                final double total) {
      return value + " (" + GMath.roundTo(100 * value / total, 2) + "%)";
   }


   public static boolean isConvexQuad(final IVector3<?> a,
                                      final IVector3<?> b,
                                      final IVector3<?> c,
                                      final IVector3<?> d) {
      // from Real-Time Collision Detection   (Christer Ericson)
      //    page 60

      final IVector3<?> bda = d.sub(b).cross(a.sub(b));
      final IVector3<?> bdc = d.sub(b).cross(c.sub(b));

      if (GMath.positiveOrZero(bda.dot(bdc))) {
         return false;
      }

      final IVector3<?> acd = c.sub(a).cross(d.sub(a));
      final IVector3<?> acb = c.sub(a).cross(b.sub(a));
      //return acd.dot(acb) < 0.0f; 
      return GMath.negativeOrZero(acd.dot(acb));
   }
}
