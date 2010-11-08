package es.igosoftware.euclid.bounding;

import es.igosoftware.euclid.shape.GSegment;
import es.igosoftware.euclid.shape.GSegment2D;
import es.igosoftware.euclid.vector.IVector2;
import es.igosoftware.euclid.vector.IVectorTransformer;
import es.igosoftware.util.GMath;

public final class GCapsule2D
         extends
            GNCapsule<IVector2<?>, GCapsule2D>
         implements
            IBoundingArea<GCapsule2D> {

   /**
    * 
    */
   private static final long serialVersionUID = 1L;


   public GCapsule2D(final GSegment<IVector2<?>, ?, ?> segment,
                     final double radius) {
      super(segment, radius);
   }


   @Override
   public GNCapsule<IVector2<?>, GCapsule2D> expandedByDistance(final double delta) {
      return new GCapsule2D(_segment, _radius + delta);
   }


   @Override
   protected String getStringName() {
      return "Capsule 2D";
   }


   @Override
   public boolean touches(final IBoundingArea<?> that) {
      return that.touchesWithCapsule2D(this);
   }


   @Override
   public boolean touchesWithDisk(final GDisk disk) {
      //      final double squareDistanceFrom = _segment._from.squaredDistance(disk._center);
      //      final double squareDistanceTo = _segment._to.squaredDistance(disk._center);
      //      final double squareDistance = GMath.minD(squareDistanceFrom, squareDistanceTo);

      final IVector2<?> closestPoint = _segment.closestPoint(disk._center);
      final double squareDistance = closestPoint.squaredDistance(disk._center);

      final double radius = _radius + disk._radius;

      return GMath.lessOrEquals(squareDistance, radius * radius);
   }


   @Override
   public boolean touchesWithRectangle(final GAxisAlignedRectangle rectangle) {
      return rectangle.touchesWithCapsule2D(this);
   }


   @Override
   public GCapsule2D getBounds() {
      return this;
   }


   @Override
   public GCapsule2D transformedBy(final IVectorTransformer<IVector2<?>> transformer) {
      // TODO: scale/shear radius;
      final GSegment<IVector2<?>, ?, ?> transformedSegment = new GSegment2D(_segment._from.transformedBy(transformer),
               _segment._to.transformedBy(transformer));
      return new GCapsule2D(transformedSegment, _radius);
   }


   @Override
   public boolean touchesWithCapsule2D(final GCapsule2D capsule) {
      final IVector2<?> closestFrom = _segment.closestPoint(capsule._segment._from);
      final IVector2<?> closestTo = _segment.closestPoint(capsule._segment._to);

      final double squareDistanceFrom = capsule._segment.squaredDistance(closestFrom);
      final double squareDistanceTo = capsule._segment.squaredDistance(closestTo);
      final double squareDistance = GMath.minD(squareDistanceFrom, squareDistanceTo);

      final double radius = _radius + capsule._radius;

      return GMath.lessOrEquals(squareDistance, radius * radius);
   }


}
