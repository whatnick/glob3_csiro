package es.igosoftware.euclid.bounding;

import es.igosoftware.euclid.shape.GPlane;
import es.igosoftware.euclid.shape.GSegment;
import es.igosoftware.euclid.shape.GSegment3D;
import es.igosoftware.euclid.vector.IVector3;
import es.igosoftware.euclid.vector.IVectorTransformer;
import es.igosoftware.util.GMath;

public class GCapsule3D
         extends
            GNCapsule<IVector3<?>, GCapsule3D>
         implements
            IBoundingVolume<GCapsule3D> {

   /**
    * 
    */
   private static final long serialVersionUID = 1L;


   public GCapsule3D(final GSegment<IVector3<?>, ?, ?> segment,
                     final double radius) {
      super(segment, radius);
   }


   @Override
   public GNCapsule<IVector3<?>, GCapsule3D> expandedByDistance(final double delta) {
      return new GCapsule3D(_segment, _radius + delta);
   }


   @Override
   protected String getStringName() {
      return "Capsule 3D";
   }


   @Override
   public boolean touches(final IBoundingVolume<?> that) {
      return that.touchesWithCapsule3D(this);
   }


   @Override
   public boolean touchesWithBall(final GBall ball) {
      //      final double squareDistanceFrom = _segment._from.squaredDistance(ball._center);
      //      final double squareDistanceTo = _segment._to.squaredDistance(ball._center);
      //      final double squareDistance = GMath.minD(squareDistanceFrom, squareDistanceTo);

      final IVector3<?> closestPoint = _segment.closestPoint(ball._center);
      final double squareDistance = closestPoint.squaredDistance(ball._center);

      final double radius = _radius + ball._radius;

      return GMath.lessOrEquals(squareDistance, radius * radius);
   }


   @Override
   public boolean touchesWithBox(final GAxisAlignedBox box) {
      return box.touchesWithCapsule3D(this);
   }


   @Override
   public boolean touchesWithPlane(final GPlane plane) {
      final double distFrom = plane.distance(_segment._from);
      final double distTo = plane.distance(_segment._to);

      return GMath.lessOrEquals(distFrom, _radius) || GMath.lessOrEquals(distTo, _radius);
   }


   @Override
   public GCapsule3D getBounds() {
      return this;
   }


   @Override
   public GCapsule3D transformedBy(final IVectorTransformer<IVector3<?>> transformer) {
      // TODO: scale/shear radius;
      final GSegment<IVector3<?>, ?, ?> transformedSegment = new GSegment3D(_segment._from.transformedBy(transformer),
               _segment._to.transformedBy(transformer));
      return new GCapsule3D(transformedSegment, _radius);
   }


   @Override
   public boolean touchesWithCapsule3D(final GCapsule3D capsule) {
      final IVector3<?> closestFrom = _segment.closestPoint(capsule._segment._from);
      final IVector3<?> closestTo = _segment.closestPoint(capsule._segment._to);

      final double squareDistanceFrom = capsule._segment.squaredDistance(closestFrom);
      final double squareDistanceTo = capsule._segment.squaredDistance(closestTo);
      final double squareDistance = GMath.minD(squareDistanceFrom, squareDistanceTo);

      final double radius = _radius + capsule._radius;

      return GMath.lessOrEquals(squareDistance, radius * radius);
   }


}
