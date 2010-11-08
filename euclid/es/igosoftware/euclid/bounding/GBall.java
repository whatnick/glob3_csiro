package es.igosoftware.euclid.bounding;

import es.igosoftware.euclid.shape.GPlane;
import es.igosoftware.euclid.vector.IVector3;
import es.igosoftware.euclid.vector.IVectorTransformer;
import es.igosoftware.util.GMath;

public final class GBall
         extends
            GNBall<IVector3<?>, GBall>
         implements
            IBoundingVolume<GBall> {

   private static final long serialVersionUID = 1L;


   public GBall(final IVector3<?> center,
                final double radius) {
      super(center, radius);
   }


   @Override
   public GBall expandedByDistance(final double delta) {
      return new GBall(_center, _radius + delta);
   }


   @Override
   protected String getStringName() {
      return "Ball";
   }


   @Override
   public boolean touches(final IBoundingVolume<?> that) {
      return that.touchesWithBall(this);
   }


   @Override
   public boolean touchesWithBox(final GAxisAlignedBox box) {
      return box.touchesWithBall(this);
   }


   @Override
   public boolean touchesWithBall(final GBall ball) {
      final double radius = _radius + ball._radius;
      return GMath.lessOrEquals(_center.squaredDistance(ball._center), radius * radius);
      //      return (center.squaredDistance(ball.center) <= radiusSquared);
   }


   //   @Override
   //   public boolean touchesWithDisk(final GDisk disk) {
   //      double radiusSquared = radius + disk.radius;
   //      radiusSquared *= radiusSquared;
   //      final GVector2D center2d = new GVector2D(center.getX(), center.getY());
   //      return GMath.lessOrEquals(center2d.squaredDistance(disk.center), radiusSquared);
   //      //return (new GVector2D(center.getX(), center.getY()).squaredDistance(disk.center) <= radiusSquared);
   //   }


   //   @Override
   //   public boolean touchesWithRectangle(final GAxisAlignedRectangle rectangle) {
   //      return rectangle.touchesWithBall(this);
   //   }


   @Override
   public GBall getBounds() {
      return this;
   }


   //   @Override
   //   public GAxisAlignedBox asBox() {
   //      return new GAxisAlignedBox(center.sub(radius), center.add(radius));
   //   }


   @Override
   public boolean touchesWithPlane(final GPlane plane) {
      final double dist = plane.distance(_center);
      return GMath.lessOrEquals(dist, _radius);
   }


   @Override
   public GBall transformedBy(final IVectorTransformer<IVector3<?>> transformer) {
      // TODO: scale/shear radius;
      return new GBall(_center.transformedBy(transformer), _radius);
   }


   @Override
   public boolean touchesWithCapsule3D(final GCapsule3D capsule) {
      return capsule.touchesWithBall(this);
   }


}
