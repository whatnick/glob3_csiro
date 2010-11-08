/**
 * 
 */
package es.igosoftware.euclid.kdtree;

import es.igosoftware.euclid.vector.IVector;
import es.igosoftware.euclid.verticescontainer.IVertexContainer;

public interface IKDTreeVisitor<VectorT extends IVector<VectorT, ?>, VertexT extends IVertexContainer.Vertex<VectorT>> {
   public static class AbortVisiting
            extends
               Exception {
      private static final long serialVersionUID = 1L;
   }


   public void startVisiting(final GKDTree<VectorT, VertexT> kdtree);


   public void visitInnerNode(final GKDInnerNode<VectorT, VertexT> innerNode) throws IKDTreeVisitor.AbortVisiting;


   public void visitLeafNode(final GKDLeafNode<VectorT, VertexT> leafNode) throws IKDTreeVisitor.AbortVisiting;


   public void endVisiting(final GKDTree<VectorT, VertexT> kdtree);
}
