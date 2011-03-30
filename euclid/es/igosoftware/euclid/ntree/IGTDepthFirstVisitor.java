

package es.igosoftware.euclid.ntree;

import es.igosoftware.euclid.bounding.GAxisAlignedOrthotope;
import es.igosoftware.euclid.vector.IVector;


public interface IGTDepthFirstVisitor<

VectorT extends IVector<VectorT, ?>,

BoundsT extends GAxisAlignedOrthotope<VectorT, ?>,

ElementT

>
         extends
            IGTBreadFirstVisitor<VectorT, BoundsT, ElementT> {

   public void finishedInnerNode(final GGTInnerNode<VectorT, BoundsT, ElementT> inner) throws IGTBreadFirstVisitor.AbortVisiting;


   public void finishedOctree(final GGeometryNTree<VectorT, BoundsT, ElementT> octree) throws IGTBreadFirstVisitor.AbortVisiting;

}
