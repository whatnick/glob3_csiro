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

import java.util.Collections;
import java.util.List;

import es.igosoftware.euclid.bounding.GAxisAlignedRectangle;
import es.igosoftware.euclid.vector.GMutableVector2;
import es.igosoftware.euclid.vector.GVector2D;
import es.igosoftware.euclid.vector.IVector2;
import es.igosoftware.euclid.vector.IVectorTransformer;
import es.igosoftware.util.GMath;


public final class GSegment2D
         extends
            GSegment<IVector2<?>, GSegment2D, GAxisAlignedRectangle>
         implements
            IPolygon2D<GSegment2D> {

   private static final long serialVersionUID = 1L;


   public GSegment2D(final IVector2<?> fromPoint,
                     final IVector2<?> toPoint) {
      super(fromPoint, toPoint);
   }


   @Override
   public GAxisAlignedRectangle getBounds() {
      return new GAxisAlignedRectangle(_from, _to);
   }


   //   @Override
   //   public GAxisAlignedBox getAxisAlignedBoundingBox() {
   //      return getBounds().getAxisAlignedBoundingBox();
   //   }


   public static enum IntersectionResult {
      PARALLEL,
      COINCIDENT,
      NOT_INTERSECTING,
      INTERSECTING;
   }


   private IntersectionResult getIntersection(final GSegment2D that,
                                              final GMutableVector2<IVector2<?>> intersection) {
      final double thisFromX = _from.x();
      final double thisFromY = _from.y();

      final double thisToX = _to.x();
      final double thisToY = _to.y();

      final double thatFromX = that._from.x();
      final double thatFromY = that._from.y();

      final double thatToX = that._to.x();
      final double thatToY = that._to.y();

      final double denominator = ((thatToY - thatFromY) * (thisToX - thisFromX))
                                 - ((thatToX - thatFromX) * (thisToY - thisFromY));

      final double numeratorA = ((thatToX - thatFromX) * (thisFromY - thatFromY))
                                - ((thatToY - thatFromY) * (thisFromX - thatFromX));

      final double numeratorB = ((thisToX - thisFromX) * (thisFromY - thatFromY))
                                - ((thisToY - thisFromY) * (thisFromX - thatFromX));

      if (GMath.closeToZero(denominator)) {
         if (GMath.closeToZero(numeratorA) && GMath.closeToZero(numeratorB)) {
            if (contains(that._from) || (contains(that._to))) {
               return IntersectionResult.COINCIDENT;
            }
         }

         return IntersectionResult.PARALLEL;
      }

      final double ua = GMath.clamp(numeratorA / denominator, 0, 1);
      final double ub = GMath.clamp(numeratorB / denominator, 0, 1);

      final double precision = GMath.maxD(precision(), that.precision());
      if (GMath.between(ua, 0, 1, precision) && GMath.between(ub, 0, 1, precision)) {
         if (intersection != null) {
            // Get the intersection point. 
            final double intersectionX = thisFromX + ua * (thisToX - thisFromX);
            final double intersectionY = thisFromY + ua * (thisToY - thisFromY);
            intersection.set(new GVector2D(intersectionX, intersectionY));
         }

         return IntersectionResult.INTERSECTING;
      }

      return IntersectionResult.NOT_INTERSECTING;
   }


   public boolean intersects(final GSegment2D that) {
      //final IntersectionResult intersects = getIntersection(that, new GMutableVector2<IVector2>(GVector2D.ZERO));
      final IntersectionResult intersects = getIntersection(that, null);
      return (intersects == IntersectionResult.COINCIDENT) || (intersects == IntersectionResult.INTERSECTING);
   }


   @Override
   public GSegment2D transformedBy(final IVectorTransformer<IVector2<?>> transformer) {
      return new GSegment2D(_from.transformedBy(transformer), _to.transformedBy(transformer));
   }


   @Override
   public boolean isConvex() {
      return false;
   }


   @Override
   public boolean isSelfIntersected() {
      return false;
   }


   @Override
   public IPolytope<IVector2<?>, GSegment2D, ?, GAxisAlignedRectangle> getHull() {
      return this;
   }


   @Override
   public List<GSegment2D> getEdges() {
      return Collections.singletonList(this);
   }


   @Override
   public boolean closeTo(final IPolytope<IVector2<?>, GSegment2D, ?, GAxisAlignedRectangle> that) {
      if (getClass() == that.getClass()) {
         return closeTo((GSegment2D) that);
      }
      return false;
   }


   @Override
   public List<GTriangle2D> triangulate() {
      return null;
   }


   @Override
   public IPolygon2D<?> createSimplified(final double capsRadiansTolerance) {
      return this;
   }


   @Override
   public GRenderType getRenderType() {
      return GRenderType.POLYLINE;
   }


   public boolean neighborWithSegment(final GSegment2D that) {

      final double epsilon = 0.0001;

      return neighborWithSegment(that, epsilon);

   }


   public boolean neighborWithSegment(final GSegment2D that,
                                      final double epsilon) {

      final GVector2D thisMinX = new GVector2D(_from.x(), _from.y() - epsilon);
      final GVector2D thisMaxX = new GVector2D(_to.x(), _to.y() + epsilon);
      final GVector2D thisMinY = new GVector2D(_from.x() - epsilon, _from.y());
      final GVector2D thisMaxY = new GVector2D(_to.x() + epsilon, _to.y());

      final GVector2D thatMinX = new GVector2D(that._from.x(), that._from.y() - epsilon);
      final GVector2D thatMaxX = new GVector2D(that._to.x(), that._to.y() + epsilon);
      final GVector2D thatMinY = new GVector2D(that._from.x() - epsilon, that._from.y());
      final GVector2D thatMaxY = new GVector2D(that._to.x() + epsilon, that._to.y());

      final IntersectionResult intersection = getIntersection(that, null);

      if ((intersection == IntersectionResult.COINCIDENT) || (intersection == IntersectionResult.PARALLEL)) {

         boolean condition1 = false;
         boolean condition2 = false;
         if (GMath.closeTo(_from.y(), _to.y())) { // parallel to x axis
            condition1 = that._from.between(thisMinX, thisMaxX) || that._to.between(thisMinX, thisMaxX);
            condition2 = _from.between(thatMinX, thatMaxX) || _to.between(thatMinX, thatMaxX);
         }
         else if (GMath.closeTo(_from.x(), _to.x())) { // parallel to y axis
            condition1 = that._from.between(thisMinY, thisMaxY) || that._to.between(thisMinY, thisMaxY);
            condition2 = _from.between(thatMinY, thatMaxY) || _to.between(thatMinY, thatMaxY);
         }
         else {
            condition1 = (that._from.between(thisMinX, thisMaxX) && that._from.between(thisMinY, thisMaxY))
                         || (that._to.between(thisMinX, thisMaxX) && that._to.between(thisMinY, thisMaxY));
            condition2 = (_from.between(thatMinX, thatMaxX) && _from.between(thatMinY, thatMaxY))
                         || (_to.between(thatMinX, thatMaxX) && _to.between(thatMinY, thatMaxY));
         }

         return condition1 || condition2;
      }

      return false;
   }


   //   public static void main(final String[] args) {
   //      System.out.println("Segment2D 0.1");
   //      System.out.println("---------------\n");
   //
   //      final GAxisAlignedRectangle rec1 = new GAxisAlignedRectangle(new GVector2D(0, 0), new GVector2D(4, 4));
   //
   //      final GSegment2D a = new GSegment2D(new GVector2D(0, 0), new GVector2D(4, 0));
   //      final GSegment2D b = new GSegment2D(new GVector2D(0, GMath.nextUp(0)), new GVector2D(4, GMath.nextUp(0)));
   //      final GSegment2D c = new GSegment2D(new GVector2D(GMath.nextUp(4), 0), new GVector2D(8, 0));
   //      final GSegment2D d = new GSegment2D(new GVector2D(4, GMath.nextUp(0)), new GVector2D(8, GMath.nextUp(0)));
   //      final GSegment2D e = new GSegment2D(new GVector2D(GMath.nextUp(4), GMath.nextUp(0)), new GVector2D(GMath.nextUp(8),
   //               GMath.nextUp(0)));
   //
   //      final GSegment2D A = new GSegment2D(new GVector2D(0, 0), new GVector2D(0, 4));
   //      final GSegment2D B = new GSegment2D(new GVector2D(GMath.nextUp(0), 0), new GVector2D(GMath.nextUp(0), 4));
   //      final GSegment2D C = new GSegment2D(new GVector2D(0, GMath.nextUp(4)), new GVector2D(0, 8));
   //      final GSegment2D D = new GSegment2D(new GVector2D(GMath.nextUp(0), 4), new GVector2D(GMath.nextUp(0), 8));
   //      final GSegment2D E = new GSegment2D(new GVector2D(GMath.nextUp(0), GMath.nextUp(4)), new GVector2D(GMath.nextUp(0),
   //               GMath.nextUp(8)));
   //
   //      final GSegment2D f = new GSegment2D(new GVector2D(1, 1), new GVector2D(4, 1));
   //      final GSegment2D g = new GSegment2D(new GVector2D(1, 1.1), new GVector2D(4, 1.1));
   //      final GSegment2D h = new GSegment2D(new GVector2D(GMath.nextUp(4), 1), new GVector2D(8, 1));
   //      final GSegment2D i = new GSegment2D(new GVector2D(4, GMath.nextUp(1)), new GVector2D(8, GMath.nextUp(1)));
   //      final GSegment2D j = new GSegment2D(new GVector2D(GMath.nextUp(4), GMath.nextUp(1)), new GVector2D(GMath.nextUp(8),
   //               GMath.nextUp(1)));
   //      final GSegment2D k = new GSegment2D(new GVector2D(3, 1), new GVector2D(6, 1));
   //
   //      final GSegment2D F = new GSegment2D(new GVector2D(1, 1), new GVector2D(1, 4));
   //      final GSegment2D G = new GSegment2D(new GVector2D(1.1, 1), new GVector2D(1.1, 4));
   //      final GSegment2D H = new GSegment2D(new GVector2D(1, GMath.nextUp(4)), new GVector2D(1, 8));
   //      final GSegment2D I = new GSegment2D(new GVector2D(GMath.nextUp(1), 4), new GVector2D(GMath.nextUp(1), 8));
   //      final GSegment2D J = new GSegment2D(new GVector2D(GMath.nextUp(1), GMath.nextUp(4)), new GVector2D(GMath.nextUp(1),
   //               GMath.nextUp(8)));
   //      final GSegment2D K = new GSegment2D(new GVector2D(1, 3), new GVector2D(1, 6));
   //
   //      final GSegment2D l = new GSegment2D(new GVector2D(1, 1), new GVector2D(3, 3));
   //      final GSegment2D m = new GSegment2D(new GVector2D(GMath.nextUp(1), GMath.nextUp(1)), new GVector2D(GMath.nextUp(3),
   //               GMath.nextUp(3)));
   //      final GSegment2D n = new GSegment2D(new GVector2D(GMath.nextUp(3), GMath.nextUp(3)), new GVector2D(GMath.nextUp(6),
   //               GMath.nextUp(6)));
   //      final GSegment2D o = new GSegment2D(new GVector2D(GMath.nextUp(2), GMath.nextUp(2)), new GVector2D(GMath.nextUp(4),
   //               GMath.nextUp(4)));
   //      final GSegment2D p = new GSegment2D(new GVector2D(0, 0), new GVector2D(4, 4));
   //
   //      // final GVector2D r = new GVector2D(a._from.sub(a._to).x(), a._from.sub(a._to).y());
   //      final GVector2D vf = new GVector2D(f._from.sub(f._to).x(), f._from.sub(f._to).y());
   //      final GVector2D vg = new GVector2D(g._from.sub(g._to).x(), g._from.sub(g._to).y());
   //      final GVector2D vh = new GVector2D(h._from.sub(h._to).x(), h._from.sub(h._to).y());
   //      final GVector2D vi = new GVector2D(i._from.sub(i._to).x(), i._from.sub(i._to).y());
   //      final GVector2D vj = new GVector2D(j._from.sub(j._to).x(), j._from.sub(j._to).y());
   //      final GVector2D vk = new GVector2D(k._from.sub(k._to).x(), k._from.sub(k._to).y());
   //
   //
   //      System.out.println("INTERSECT g: " + f.intersects(g));
   //      System.out.println("INTERSECT h: " + f.intersects(h));
   //      System.out.println("INTERSECT i: " + f.intersects(i));
   //      System.out.println("INTERSECT j: " + f.intersects(j));
   //      System.out.println("INTERSECT k: " + f.intersects(k));
   //      System.out.println();
   //
   //      System.out.println("NEIGHBOR b: " + a.neighborWithSegment(b));
   //      System.out.println("NEIGHBOR c: " + a.neighborWithSegment(c));
   //      System.out.println("NEIGHBOR d: " + a.neighborWithSegment(d));
   //      System.out.println("NEIGHBOR e: " + a.neighborWithSegment(e));
   //      System.out.println();
   //
   //      System.out.println("NEIGHBOR B: " + A.neighborWithSegment(B));
   //      System.out.println("NEIGHBOR C: " + A.neighborWithSegment(C));
   //      System.out.println("NEIGHBOR D: " + A.neighborWithSegment(D));
   //      System.out.println("NEIGHBOR E: " + A.neighborWithSegment(E));
   //      System.out.println();
   //
   //      System.out.println("NEIGHBOR g: " + f.neighborWithSegment(g));
   //      System.out.println("NEIGHBOR h: " + f.neighborWithSegment(h));
   //      System.out.println("NEIGHBOR i: " + f.neighborWithSegment(i));
   //      System.out.println("NEIGHBOR j: " + f.neighborWithSegment(j));
   //      System.out.println("NEIGHBOR k: " + f.neighborWithSegment(k));
   //      System.out.println();
   //
   //      System.out.println("NEIGHBOR G: " + f.neighborWithSegment(g));
   //      System.out.println("NEIGHBOR H: " + f.neighborWithSegment(h));
   //      System.out.println("NEIGHBOR I: " + f.neighborWithSegment(i));
   //      System.out.println("NEIGHBOR J: " + f.neighborWithSegment(j));
   //      System.out.println("NEIGHBOR K: " + f.neighborWithSegment(k));
   //      System.out.println();
   //
   //      System.out.println("NEIGHBOR m: " + l.neighborWithSegment(m));
   //      System.out.println("NEIGHBOR n: " + l.neighborWithSegment(n));
   //      System.out.println("NEIGHBOR o: " + l.neighborWithSegment(o));
   //      System.out.println("NEIGHBOR p: " + l.neighborWithSegment(p));
   //      System.out.println();
   //
   //      //      System.out.println("DISTANCE vk: " + vf.distance(vk));
   //      //      System.out.println("DISTANCE vg: " + vf.distance(vg));
   //      //      System.out.println("DISTANCE vh: " + vf.distance(vh));
   //      //      System.out.println("DISTANCE vi: " + vf.distance(vi));
   //      //      System.out.println("DISTANCE vj: " + vf.distance(vj));
   //
   //
   //   }

}
