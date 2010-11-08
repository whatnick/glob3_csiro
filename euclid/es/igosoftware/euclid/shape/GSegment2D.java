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
