

package es.igosoftware.euclid.ntree;

import es.igosoftware.euclid.vector.IVector;


public interface IGTDepthFirstVisitor<VectorT extends IVector<VectorT, ?>, ElementT>
         extends
            IGTBreadFirstVisitor<VectorT, ElementT> {

   public void finishedInnerNode(final GGTInnerNode<VectorT, ElementT> inner) throws IGTBreadFirstVisitor.AbortVisiting;


   public void finishedOctree(final GGeometryNTree<VectorT, ElementT> octree) throws IGTBreadFirstVisitor.AbortVisiting;

}
