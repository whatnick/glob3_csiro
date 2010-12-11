

package es.igosoftware.euclid.ntree.quadtree;

import es.igosoftware.euclid.IBoundedGeometry;
import es.igosoftware.euclid.bounding.GAxisAlignedRectangle;
import es.igosoftware.euclid.bounding.IFiniteBounds;
import es.igosoftware.euclid.ntree.IGTBreadFirstVisitor;
import es.igosoftware.euclid.vector.IVector2;


public interface IQuadtreeBreadFirstVisitor<GeometryT extends IBoundedGeometry<IVector2<?>, ?, ? extends IFiniteBounds<IVector2<?>, ?>>>
         extends
            IGTBreadFirstVisitor<IVector2<?>, GAxisAlignedRectangle, GeometryT> {

}
