package es.igosoftware.euclid.ntree.quadtree;

import es.igosoftware.euclid.IBoundedGeometry;
import es.igosoftware.euclid.bounding.GAxisAlignedRectangle;
import es.igosoftware.euclid.bounding.IFiniteBounds;
import es.igosoftware.euclid.ntree.IGTDepthFirstVisitor;
import es.igosoftware.euclid.vector.IVector2;

public interface IQuadtreeDepthFirstVisitor<GeometryT extends IBoundedGeometry<IVector2<?>, ?, ? extends IFiniteBounds<IVector2<?>, ?>>>
         extends
            IGTDepthFirstVisitor<IVector2<?>, GAxisAlignedRectangle, GeometryT> {

}