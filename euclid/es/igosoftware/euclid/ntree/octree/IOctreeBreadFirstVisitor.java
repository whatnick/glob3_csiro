

package es.igosoftware.euclid.ntree.octree;

import es.igosoftware.euclid.IBoundedGeometry;
import es.igosoftware.euclid.bounding.GAxisAlignedBox;
import es.igosoftware.euclid.bounding.IFiniteBounds;
import es.igosoftware.euclid.ntree.IGTBreadFirstVisitor;
import es.igosoftware.euclid.vector.IVector3;


public interface IOctreeBreadFirstVisitor<GeometryT extends IBoundedGeometry<IVector3<?>, ?, ? extends IFiniteBounds<IVector3<?>, ?>>>
         extends
            IGTBreadFirstVisitor<IVector3<?>, GAxisAlignedBox, GeometryT> {

}
