package es.igosoftware.euclid.kdtree;

import es.igosoftware.euclid.vector.IVector;
import es.igosoftware.euclid.verticescontainer.IVertexContainer;
import es.igosoftware.util.GProgress;

public class GKDMonoLeafNode<VectorT extends IVector<VectorT, ?>, VertexT extends IVertexContainer.Vertex<VectorT>>
         extends
            GKDLeafNode<VectorT, VertexT> {

   private final int _vertexIndex;


   GKDMonoLeafNode(final GKDTree<VectorT, VertexT> tree,
                   final GKDInnerNode<VectorT, VertexT> parent,
                   final int vertexIndex,
                   final GProgress progress) {
      super(tree, parent);
      _vertexIndex = vertexIndex;
      progress.stepDone();
   }


   @Override
   public String toString() {
      return "GKDMonoLeafNode [key=" + getKeyString() + ", parent=" + getParentKeyString() + ", vertexIndex=" + _vertexIndex
             + ", size=" + getSize() + "]";
   }


   @Override
   public int[] getVerticesIndexes() {
      return new int[] { _vertexIndex };
   }


   @Override
   public IVertexContainer<VectorT, VertexT, ?> getVertices() {
      return getOriginalVertices().asSubContainer(new int[] { _vertexIndex });
   }


   //   @Override
   //   protected WeightedVertex<VectorT> calculateAverageVertex() {
   //      final IVertexContainer<VectorT, ?> vertices = getOriginalVertices();
   //
   //      final VectorT point = vertices.getPoint(_vertexIndex);
   //      final float intentity = vertices.getIntensity(_vertexIndex);
   //      final VectorT normal = vertices.getNormal(_vertexIndex);
   //      final IColor color = vertices.getColor(_vertexIndex);
   //      final int weight = 1;
   //
   //      return new WeightedVertex<VectorT>(point, intentity, normal, color, weight);
   //   }


}
