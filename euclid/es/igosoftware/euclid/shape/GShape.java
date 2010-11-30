/*

 IGO Software SL  -  info@igosoftware.es

 http://www.glob3.org

-------------------------------------------------------------------------------
 Copyright (c) 2010, IGO Software SL
 All rights reserved.

 Redistribution and use in source and binary forms, with or without
 modification, are permitted provided that the following conditions are met:
     * Redistributions of source code must retain the above copyright
       notice, this list of conditions and the following disclaimer.
     * Redistributions in binary form must reproduce the above copyright
       notice, this list of conditions and the following disclaimer in the
       documentation and/or other materials provided with the distribution.
     * Neither the name of the IGO Software SL nor the
       names of its contributors may be used to endorse or promote products
       derived from this software without specific prior written permission.

 THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 DISCLAIMED. IN NO EVENT SHALL IGO Software SL BE LIABLE FOR ANY
 DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
-------------------------------------------------------------------------------

*/


package es.igosoftware.euclid.shape;

import java.util.Collection;
import java.util.List;

import es.igosoftware.euclid.IBoundedGeometry;
import es.igosoftware.euclid.bounding.GAxisAlignedOrthotope;
import es.igosoftware.euclid.bounding.IFiniteBounds;
import es.igosoftware.euclid.vector.IVector;
import es.igosoftware.euclid.vector.IVector2;
import es.igosoftware.euclid.vector.IVector3;
import es.igosoftware.util.GCollections;
import es.igosoftware.util.GMath;
import es.igosoftware.util.ITransformer;


public final class GShape {


   private GShape() {
   }

   private static int trianglesCounter = 0;
   private static int quadsCounter     = 0;
   private static int polygonsCounter  = 0;


   public static IPolygon3D<?> createPolygon(final boolean validate,
                                             final IVector3<?>... points) {
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
      return new GSimplePolygon3D(validate, points);
   }


   public static IPolygon2D<?> createPolygon(final boolean validate,
                                             final IVector2<?>... points) {
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
      return new GSimplePolygon2D(validate, points);
   }


   public static IPolygon2D<?> createLine2(final boolean validate,
                                           final List<IVector2<?>> points) {
      final int pointsCount = points.size();

      if (pointsCount < 2) {
         throw new IllegalArgumentException("Can't create lines with less than 2 points");
      }

      if (pointsCount == 2) {
         return new GSegment2D(points.get(0), points.get(1));
      }

      return new GLinesStrip2D(validate, points);
   }


   public static IPolygon2D<?> createPolygon2(final boolean validate,
                                              final List<IVector2<?>> points) {
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
      return new GSimplePolygon2D(validate, points);
   }


   public static IPolygon3D<?> createPolygon3(final boolean validate,
                                              final List<IVector3<?>> points) {
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
      return new GSimplePolygon3D(validate, points);
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

      final IVector3<?> dSubB = d.sub(b);
      final IVector3<?> bda = dSubB.cross(a.sub(b));
      final IVector3<?> bdc = dSubB.cross(c.sub(b));

      if (GMath.positiveOrZero(bda.dot(bdc))) {
         return false;
      }

      final IVector3<?> cSubA = c.sub(a);
      final IVector3<?> acd = cSubA.cross(d.sub(a));
      final IVector3<?> acb = cSubA.cross(b.sub(a));

      //return acd.dot(acb) < 0.0f; 
      return GMath.negativeOrZero(acd.dot(acb));
   }


   //   public static <VectorT extends IVector<VectorT, ?>, BoundsT extends IFiniteBounds<VectorT, BoundsT>, GeometryT extends IBoundedGeometry<VectorT, GeometryT, BoundsT>> BoundsT getBounds(final Collection<GeometryT> geometries) {
   //
   //      if ((geometries == null) || geometries.isEmpty()) {
   //         return null;
   //      }
   //
   //      final Iterator<GeometryT> iterator = geometries.iterator();
   //      final GeometryT first = iterator.next();
   //
   //      BoundsT bounds = first.getBounds();
   //
   //      while (iterator.hasNext()) {
   //         final GeometryT current = iterator.next();
   //         bounds = bounds.mergedWith(current.getBounds());
   //      }
   //
   //      return bounds;
   //   }

   public static <

   VectorT extends IVector<VectorT, ?>,

   GeometryT extends IBoundedGeometry<VectorT, ?, ? extends IFiniteBounds<VectorT, ?>>> GAxisAlignedOrthotope<VectorT, ?

   > getBounds(final Collection<GeometryT> geometries) {

      if ((geometries == null) || geometries.isEmpty()) {
         return null;
      }

      final Collection<GAxisAlignedOrthotope<VectorT, ?>> bounds = GCollections.collect(geometries,
               new ITransformer<GeometryT, GAxisAlignedOrthotope<VectorT, ?>>() {
                  @Override
                  public GAxisAlignedOrthotope<VectorT, ?> transform(final GeometryT element) {
                     return element.getBounds().asAxisAlignedOrthotope();
                  }
               });

      return GAxisAlignedOrthotope.merge(bounds);
   }


}
