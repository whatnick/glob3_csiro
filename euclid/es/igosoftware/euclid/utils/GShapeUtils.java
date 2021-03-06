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


package es.igosoftware.euclid.utils;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import es.igosoftware.euclid.IBoundedGeometry;
import es.igosoftware.euclid.bounding.GAxisAlignedOrthotope;
import es.igosoftware.euclid.bounding.IFiniteBounds;
import es.igosoftware.euclid.shape.GLinesStrip2D;
import es.igosoftware.euclid.shape.GQuad2D;
import es.igosoftware.euclid.shape.GQuad3D;
import es.igosoftware.euclid.shape.GSegment2D;
import es.igosoftware.euclid.shape.GSimplePolygon2D;
import es.igosoftware.euclid.shape.GSimplePolygon3D;
import es.igosoftware.euclid.shape.GTriangle2D;
import es.igosoftware.euclid.shape.GTriangle3D;
import es.igosoftware.euclid.shape.IPolygonalChain2D;
import es.igosoftware.euclid.shape.ISimplePolygon2D;
import es.igosoftware.euclid.shape.ISimplePolygon3D;
import es.igosoftware.euclid.vector.GVector3D;
import es.igosoftware.euclid.vector.IVector;
import es.igosoftware.euclid.vector.IVector2;
import es.igosoftware.euclid.vector.IVector3;
import es.igosoftware.util.GCollections;
import es.igosoftware.util.GMath;
import es.igosoftware.util.IFunction;


public final class GShapeUtils {


   private GShapeUtils() {
      // just static methods
   }


   public static IPolygonalChain2D createLine2(final boolean validate,
                                               final IVector2... points) {
      final int pointsCount = points.length;

      if (pointsCount < 2) {
         throw new IllegalArgumentException("Can't create lines with less than 2 points");
      }

      if (pointsCount == 2) {
         return new GSegment2D(points[0], points[1]);
      }

      return new GLinesStrip2D(validate, points);
   }


   public static IPolygonalChain2D createLine2(final boolean validate,
                                               final List<IVector2> points) {
      final int pointsCount = points.size();

      if (pointsCount < 2) {
         throw new IllegalArgumentException("Can't create lines with less than 2 points");
      }

      if (pointsCount == 2) {
         return new GSegment2D(points.get(0), points.get(1));
      }

      return new GLinesStrip2D(validate, points);
   }


   public static ISimplePolygon2D createPolygon(final boolean validate,
                                                final IVector2... points) {
      final int pointsCount = points.length;

      if (pointsCount < 3) {
         throw new IllegalArgumentException("Can't create polygons with less than 3 points");
      }

      if (pointsCount == 3) {
         return new GTriangle2D(points[0], points[1], points[2]);
      }

      if (pointsCount == 4) {
         return new GQuad2D(points[0], points[1], points[2], points[3]);
      }

      return new GSimplePolygon2D(validate, points);
   }


   public static ISimplePolygon3D createPolygon(final boolean validate,
                                                final IVector3... points) {
      final int pointsCount = points.length;

      if (pointsCount < 3) {
         throw new IllegalArgumentException("Can't create polygons with less than 3 points");
      }

      if (pointsCount == 3) {
         return new GTriangle3D(points[0], points[1], points[2]);
      }

      if (pointsCount == 4) {
         return new GQuad3D(points[0], points[1], points[2], points[3]);
      }

      return new GSimplePolygon3D(validate, points);
   }


   public static ISimplePolygon2D createPolygon2(final boolean validate,
                                                 final IVector2... points) {
      final int pointsCount = points.length;

      if (pointsCount < 3) {
         throw new IllegalArgumentException("Can't create polygons with less than 3 points");
      }

      if (pointsCount == 3) {
         return new GTriangle2D(points[0], points[1], points[2]);
      }

      if (pointsCount == 4) {
         return new GQuad2D(points[0], points[1], points[2], points[3]);
      }

      return new GSimplePolygon2D(validate, points);
   }


   public static ISimplePolygon2D createPolygon2(final boolean validate,
                                                 final List<IVector2> points) {
      final int pointsCount = points.size();

      if (pointsCount < 3) {
         throw new IllegalArgumentException("Can't create polygons with less than 3 points");
      }

      if (pointsCount == 3) {
         return new GTriangle2D(points.get(0), points.get(1), points.get(2));
      }

      if (pointsCount == 4) {
         return new GQuad2D(points.get(0), points.get(1), points.get(2), points.get(3));
      }

      return new GSimplePolygon2D(validate, points);
   }


   public static ISimplePolygon3D createPolygon3(final boolean validate,
                                                 final List<IVector3> points) {
      final int pointsCount = points.size();

      if (pointsCount < 3) {
         throw new IllegalArgumentException("Can't create polygons with less than 3 points");
      }

      if (pointsCount == 3) {
         return new GTriangle3D(points.get(0), points.get(1), points.get(2));
      }

      if (pointsCount == 4) {
         return new GQuad3D(points.get(0), points.get(1), points.get(2), points.get(3));
      }

      return new GSimplePolygon3D(validate, points);
   }


   public static <

   VectorT extends IVector<VectorT, ?>,

   GeometryT extends IBoundedGeometry<VectorT, ? extends IFiniteBounds<VectorT, ?>>> GAxisAlignedOrthotope<VectorT, ?

   > getBounds(final Collection<GeometryT> geometries) {

      if ((geometries == null) || geometries.isEmpty()) {
         return null;
      }

      final Collection<GAxisAlignedOrthotope<VectorT, ?>> bounds = GCollections.collect(geometries,
               new IFunction<GeometryT, GAxisAlignedOrthotope<VectorT, ?>>() {
                  @Override
                  public GAxisAlignedOrthotope<VectorT, ?> apply(final GeometryT element) {
                     return element.getBounds().asAxisAlignedOrthotope();
                  }
               });

      return GAxisAlignedOrthotope.merge(bounds);
   }


   public static <

   VectorT extends IVector<VectorT, ?>,

   ElementT,

   GeometryT extends IBoundedGeometry<VectorT, ? extends IFiniteBounds<VectorT, ?>>> GAxisAlignedOrthotope<VectorT, ?

   > getBounds(final Iterable<? extends ElementT> elements,
               final IFunction<ElementT, GeometryT> transformer) {

      if (elements == null) {
         return null;
      }

      final Iterator<? extends ElementT> iterator = elements.iterator();
      if (!iterator.hasNext()) {
         return null;
      }

      VectorT lower = null;
      VectorT upper = null;
      while (iterator.hasNext()) {
         final ElementT element = iterator.next();
         final GeometryT geometry = transformer.apply(element);

         final GAxisAlignedOrthotope<VectorT, ?> bounds = geometry.getBounds().asAxisAlignedOrthotope();

         lower = (lower == null) ? bounds._lower : lower.min(bounds._lower);
         upper = (upper == null) ? bounds._upper : upper.max(bounds._upper);
      }

      return GAxisAlignedOrthotope.create(lower, upper);
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

   public static boolean isClockWise(final IVector2... points) {
      return GShapeUtils.signedArea(points) < 0;
   }


   public static boolean isClockWise(final IVector2 a,
                                     final IVector2 b,
                                     final IVector2 c) {
      return GShapeUtils.signedArea(a, b, c) < 0;
   }


   public static boolean isClockWise(final IVector3... points) {
      return GShapeUtils.signedArea(points) < 0;
   }


   public static boolean isClockWise(final IVector3 a,
                                     final IVector3 b,
                                     final IVector3 c) {
      return GShapeUtils.signedArea(a, b, c) < 0;
   }


   public static boolean isClockWise2(final List<IVector2> points) {
      return GShapeUtils.signedArea2(points) > 0;
   }


   public static boolean isClockWise3(final List<IVector3> points) {
      return GShapeUtils.signedArea3(points) > 0;
   }


   public static boolean isConvexQuad(final IVector3 a,
                                      final IVector3 b,
                                      final IVector3 c,
                                      final IVector3 d) {
      // from Real-Time Collision Detection   (Christer Ericson)
      //    page 60

      final IVector3 dSubB = d.sub(b);
      final IVector3 bda = dSubB.cross(a.sub(b));
      final IVector3 bdc = dSubB.cross(c.sub(b));

      if (GMath.positiveOrZero(bda.dot(bdc))) {
         return false;
      }

      final IVector3 cSubA = c.sub(a);
      final IVector3 acd = cSubA.cross(d.sub(a));
      final IVector3 acb = cSubA.cross(b.sub(a));

      //return acd.dot(acb) < 0.0f; 
      return GMath.negativeOrZero(acd.dot(acb));
   }


   public static boolean isCounterClockWise(final IVector2... points) {
      return GShapeUtils.signedArea(points) > 0;
   }


   public static boolean isCounterClockWise(final IVector2 a,
                                            final IVector2 b,
                                            final IVector2 c) {
      return GShapeUtils.signedArea(a, b, c) > 0;
   }


   public static boolean isCounterClockWise(final IVector3... points) {
      return GShapeUtils.signedArea(points) > 0;
   }


   public static boolean isCounterClockWise(final IVector3 a,
                                            final IVector3 b,
                                            final IVector3 c) {
      return GShapeUtils.signedArea(a, b, c) > 0;
   }


   public static boolean isCounterClockWise2(final List<IVector2> points) {
      return GShapeUtils.signedArea2(points) < 0;
   }


   public static boolean isCounterClockWise3(final List<IVector3> points) {
      return GShapeUtils.signedArea3(points) < 0;
   }


   // from  http://paulbourke.net/geometry/polyarea/
   public static double signedArea(final IVector2... points) {
      final int pointsCount = points.length;

      double area = 0;
      for (int i = 0; i < pointsCount; i++) {
         final IVector2 pointI = points[i];
         final IVector2 pointJ = points[(i + 1) % pointsCount];

         area += pointI.x() * pointJ.y();
         area -= pointJ.x() * pointI.y();
      }
      area /= 2.0;

      return area;
   }


   // Returns  the signed triangle area. The result is positive if
   // abc is ccw, negative if abc is cw, zero if abc is degenerate.
   public static double signedArea(final IVector2 a,
                                   final IVector2 b,
                                   final IVector2 c) {
      return (a.x() - c.x()) * (b.y() - c.y()) - (a.y() - c.y()) * (b.x() - c.x()) / 2;
   }


   public static double signedArea(final IVector3... points) {
      final int pointsCount = points.length;

      GVector3D n = GVector3D.ZERO;

      for (int i = 0; i < pointsCount; ++i) {
         final IVector3 currentPoint = points[i];
         final IVector3 nextPoint = points[(i + 1) % pointsCount];

         n = n.add(currentPoint.cross(nextPoint));
      }

      return n.length() / 2;
   }


   // from  http://paulbourke.net/geometry/polyarea/
   public static double signedArea2(final List<IVector2> points) {
      final int pointsCount = points.size();

      double area = 0;
      for (int i = 0; i < pointsCount; i++) {
         final IVector2 currentPoint = points.get(i);
         final IVector2 nextPoint = points.get((i + 1) % pointsCount);

         area += currentPoint.x() * nextPoint.y();
         area -= nextPoint.x() * currentPoint.y();
      }
      area /= 2.0;

      return area;
   }


   public static double signedArea3(final List<IVector3> points) {
      final int pointsCount = points.size();

      GVector3D n = GVector3D.ZERO;

      for (int i = 0; i < pointsCount; i++) {
         final IVector3 currentPoint = points.get(i);
         final IVector3 nextPoint = points.get((i + 1) % pointsCount);

         n = n.add(currentPoint.cross(nextPoint));
      }

      return n.length() / 2;
   }


}
