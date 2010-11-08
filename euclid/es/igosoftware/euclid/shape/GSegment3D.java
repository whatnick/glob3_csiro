package es.igosoftware.euclid.shape;

import es.igosoftware.euclid.bounding.GAxisAlignedBox;
import es.igosoftware.euclid.vector.IVector3;
import es.igosoftware.euclid.vector.IVectorTransformer;

public final class GSegment3D
         extends
            GSegment<IVector3<?>, GSegment3D, GAxisAlignedBox> {

   private static final long serialVersionUID = 1L;


   public GSegment3D(final IVector3<?> fromPoint,
                     final IVector3<?> toPoint) {
      super(fromPoint, toPoint);
   }


   @Override
   public GAxisAlignedBox getBounds() {
      return new GAxisAlignedBox(_from, _to);
   }


   //   @Override
   //   public GAxisAlignedBox getAxisAlignedBoundingBox() {
   //      return getBounds();
   //   }


   @Override
   public GSegment3D transformedBy(final IVectorTransformer<IVector3<?>> transformer) {
      return new GSegment3D(_from.transformedBy(transformer), _to.transformedBy(transformer));
   }


   public IVector3<?> getIntersection(final GPlane plane) {

      return plane.getIntersection(this);
   }

}
