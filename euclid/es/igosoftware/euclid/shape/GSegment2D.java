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

import es.igosoftware.euclid.bounding.GAxisAlignedRectangle;
import es.igosoftware.euclid.vector.GMutableVector2;
import es.igosoftware.euclid.vector.GVector2D;
import es.igosoftware.euclid.vector.IVector2;
import es.igosoftware.euclid.vector.IVectorTransformer;
import es.igosoftware.util.GMath;


public final class GSegment2D
         extends
            GSegment<IVector2<?>, GSegment2D, GAxisAlignedRectangle> {

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
                                              final GMutableVector2<IVector2> intersection) {
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
            return IntersectionResult.COINCIDENT;
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


}
