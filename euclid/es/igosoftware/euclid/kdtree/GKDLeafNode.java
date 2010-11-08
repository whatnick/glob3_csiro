package es.igosoftware.euclid.kdtree;

import java.util.LinkedList;

import es.igosoftware.euclid.vector.IVector;
import es.igosoftware.euclid.verticescontainer.IVertexContainer;

public abstract class GKDLeafNode<VectorT extends IVector<VectorT, ?>, VertexT extends IVertexContainer.Vertex<VectorT>>
         extends
            GKDNode<VectorT, VertexT> {


   protected GKDLeafNode(final GKDTree<VectorT, VertexT> tree,
                         final GKDInnerNode<VectorT, VertexT> parent) {
      super(tree, parent);
   }


   @Override
   public boolean isLeaf() {
      return true;
   }


   @Override
   public final int getSize() {
      return 1;
   }


   @Override
   protected void breadthFirstAcceptVisitor(final IKDTreeVisitor<VectorT, VertexT> visitor,
                                            final LinkedList<GKDNode<VectorT, VertexT>> queue)
                                                                                              throws IKDTreeVisitor.AbortVisiting {
      visitor.visitLeafNode(this);
   }


   @Override
   protected void depthFirstAcceptVisitor(final IKDTreeVisitor<VectorT, VertexT> visitor) throws IKDTreeVisitor.AbortVisiting {
      visitor.visitLeafNode(this);
   }


   public abstract int[] getVerticesIndexes();


   public abstract IVertexContainer<VectorT, VertexT, ?> getVertices();

}
