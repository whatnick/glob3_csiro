package es.igosoftware.euclid.kdtree;

import java.util.Arrays;

import es.igosoftware.euclid.vector.IVector;
import es.igosoftware.euclid.verticescontainer.IVertexContainer;
import es.igosoftware.util.GProgress;

public class GKDMultipleLeafNode<VectorT extends IVector<VectorT, ?>, VertexT extends IVertexContainer.Vertex<VectorT>>
         extends
            GKDLeafNode<VectorT, VertexT> {

   private final int[] _verticesIndexes;


   GKDMultipleLeafNode(final GKDTree<VectorT, VertexT> tree,
                       final GKDInnerNode<VectorT, VertexT> parent,
                       final int[] verticesIndexes,
                       final GProgress progress) {
      super(tree, parent);

      final int verticesCount = verticesIndexes.length;
      _verticesIndexes = Arrays.copyOf(verticesIndexes, verticesCount);
      progress.stepsDone(verticesCount);
   }


   @Override
   public int[] getVerticesIndexes() {
      return Arrays.copyOf(_verticesIndexes, _verticesIndexes.length);
   }


   @Override
   public String toString() {
      return "GKDMultipleLeafNode [key=" + getKeyString() + ", parent=" + getParentKeyString() + ", verticesIndexes="
             + Arrays.toString(_verticesIndexes) + ", size=" + getSize() + "]";
   }


   @Override
   public IVertexContainer<VectorT, VertexT, ?> getVertices() {
      return getOriginalVertices().asSubContainer(_verticesIndexes);
   }


   //   @Override
   //   protected WeightedVertex<VectorT> calculateAverageVertex() {
   //      final IVertexContainer<VectorT, ?> vertices = getVertices().createSubContainer(_verticesIndexes);
   //
   //      return vertices.getAverage();
   //   }
}
