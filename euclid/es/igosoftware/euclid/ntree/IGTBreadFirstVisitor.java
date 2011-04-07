

package es.igosoftware.euclid.ntree;

import es.igosoftware.euclid.vector.IVector;


public interface IGTBreadFirstVisitor<VectorT extends IVector<VectorT, ?>, ElementT> {

   public static class AbortVisiting
            extends
               Exception {
      private static final long serialVersionUID = 1L;
   }


   public void visitOctree(final GGeometryNTree<VectorT, ElementT> octree) throws IGTBreadFirstVisitor.AbortVisiting;


   public void visitInnerNode(final GGTInnerNode<VectorT, ElementT> inner) throws IGTBreadFirstVisitor.AbortVisiting;


   public void visitLeafNode(final GGTLeafNode<VectorT, ElementT> leaf) throws IGTBreadFirstVisitor.AbortVisiting;

}
