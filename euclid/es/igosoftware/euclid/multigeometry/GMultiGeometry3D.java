

package es.igosoftware.euclid.multigeometry;

import java.util.List;

import es.igosoftware.euclid.IBoundedGeometry;
import es.igosoftware.euclid.IBoundedGeometry3D;
import es.igosoftware.euclid.bounding.GAxisAlignedBox;
import es.igosoftware.euclid.bounding.IFiniteBounds;
import es.igosoftware.euclid.vector.IVector3;


public class GMultiGeometry3D<

ChildrenGeometryT extends IBoundedGeometry<IVector3, ? extends IFiniteBounds<IVector3, ?>>

>
         extends
            GMultiGeometry<IVector3, ChildrenGeometryT, GAxisAlignedBox>
         implements
            IBoundedGeometry3D<GAxisAlignedBox> {


   private static final long serialVersionUID = 1L;


   public GMultiGeometry3D(final ChildrenGeometryT... children) {
      super(children);
   }


   public GMultiGeometry3D(final List<ChildrenGeometryT> children) {
      super(children);
   }


}
