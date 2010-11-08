package es.igosoftware.euclid.bounding;

import es.igosoftware.euclid.vector.IVector2;
import es.igosoftware.euclid.vector.IVectorTransformer;
import es.igosoftware.util.GMath;

public final class GDisk
         extends
            GNBall<IVector2<?>, GDisk>
         implements
            IBoundingArea<GDisk> {

   private static final long serialVersionUID = 1L;


   public GDisk(final IVector2<?> center,
                final double radius) {
      super(center, radius);
   }


   @Override
   public GDisk expandedByDistance(final double delta) {
      return new GDisk(_center, _radius + delta);
   }


   @Override
   protected String getStringName() {
      return "Disk";
   }


   @Override
   public boolean touches(final IBoundingArea<?> that) {
      return that.touchesWithDisk(this);
   }


   //   @Override
   //   public boolean touchesWithBall(final GBall ball) {
   //      return ball.touchesWithDisk(this);
   //   }


   @Override
   public boolean touchesWithDisk(final GDisk disk) {
      final double radius = _radius + disk._radius;
      return GMath.lessOrEquals(_center.squaredDistance(disk._center), radius * radius);
      //      return (center.squaredDistance(disk.center) <= radiusSquared);
   }


   @Override
   public boolean touchesWithRectangle(final GAxisAlignedRectangle rectangle) {
      return rectangle.touchesWithDisk(this);
   }


   //   @Override
   //   public boolean touchesWithBox(final GAxisAlignedBox box) {
   //      return box.touchesWithDisk(this);
   //   }


   @Override
   public GDisk getBounds() {
      return this;
   }


   //   @Override
   //   public GAxisAlignedBox asBox() {
   //      return new GAxisAlignedRectangle(center.sub(radius), center.add(radius)).asBox();
   //   }


   //   @Override
   //   public boolean touchesWithPlane(final GPlane plane) {
   //      return plane.touchesWithDisk(this);
   //   }


   @Override
   public GDisk transformedBy(final IVectorTransformer<IVector2<?>> transformer) {
      // TODO: scale/shear radius;
      return new GDisk(_center.transformedBy(transformer), _radius);
   }


   @Override
   public boolean touchesWithCapsule2D(final GCapsule2D capsule) {
      return capsule.touchesWithDisk(this);
   }

}
