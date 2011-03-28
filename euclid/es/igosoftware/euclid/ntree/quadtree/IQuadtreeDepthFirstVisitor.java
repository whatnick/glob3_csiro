

package es.igosoftware.euclid.ntree.quadtree;

import es.igosoftware.euclid.bounding.GAxisAlignedRectangle;
import es.igosoftware.euclid.ntree.IGTDepthFirstVisitor;
import es.igosoftware.euclid.vector.IVector2;


public interface IQuadtreeDepthFirstVisitor<

ElementT

>
         extends
            IGTDepthFirstVisitor<IVector2<?>, GAxisAlignedRectangle, ElementT> {

}
