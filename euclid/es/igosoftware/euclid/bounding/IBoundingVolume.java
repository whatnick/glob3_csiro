package es.igosoftware.euclid.bounding;

import es.igosoftware.euclid.shape.GPlane;
import es.igosoftware.euclid.vector.IVector3;

public interface IBoundingVolume<

GeometryT extends IBounds<IVector3<?>, GeometryT>

>
         extends
            IBounds<IVector3<?>, GeometryT> {


   public boolean touches(final IBoundingVolume<?> that);


   public boolean touchesWithBox(final GAxisAlignedBox box);


   public boolean touchesWithPlane(final GPlane plane);


   public boolean touchesWithBall(final GBall ball);


   public boolean touchesWithCapsule3D(final GCapsule3D capsule);

}
