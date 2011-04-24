

package es.igosoftware.euclid.ntree.quadtree;

import es.igosoftware.euclid.IBoundedGeometry2D;
import es.igosoftware.euclid.bounding.IFiniteBounds;
import es.igosoftware.euclid.ntree.IGTBreadFirstVisitor;
import es.igosoftware.euclid.vector.IVector2;


public interface IQuadtreeBreadFirstVisitor<

ElementT,

GeometryT extends IBoundedGeometry2D<? extends IFiniteBounds<IVector2, ?>>

>
         extends
            IGTBreadFirstVisitor<IVector2, ElementT, GeometryT> {

}
