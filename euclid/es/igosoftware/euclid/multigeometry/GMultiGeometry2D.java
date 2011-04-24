

package es.igosoftware.euclid.multigeometry;

import java.util.List;

import es.igosoftware.euclid.IBoundedGeometry;
import es.igosoftware.euclid.IBoundedGeometry2D;
import es.igosoftware.euclid.bounding.GAxisAlignedRectangle;
import es.igosoftware.euclid.bounding.IFiniteBounds;
import es.igosoftware.euclid.vector.IVector2;


public class GMultiGeometry2D<

ChildrenGeometryT extends IBoundedGeometry<IVector2, ? extends IFiniteBounds<IVector2, ?>>

>
         extends
            GMultiGeometry<IVector2, ChildrenGeometryT, GAxisAlignedRectangle>
         implements
            IBoundedGeometry2D<GAxisAlignedRectangle> {


   private static final long serialVersionUID = 1L;


   public GMultiGeometry2D(final ChildrenGeometryT... children) {
      super(children);
   }


   public GMultiGeometry2D(final List<ChildrenGeometryT> children) {
      super(children);
   }


}
