

package es.igosoftware.euclid.ntree.octree;

import es.igosoftware.euclid.bounding.GAxisAlignedBox;
import es.igosoftware.euclid.ntree.IGTDepthFirstVisitor;
import es.igosoftware.euclid.vector.IVector3;


public interface IOctreeDepthFirstVisitor<

ElementT

>
         extends
            IGTDepthFirstVisitor<IVector3, GAxisAlignedBox, ElementT> {

}
