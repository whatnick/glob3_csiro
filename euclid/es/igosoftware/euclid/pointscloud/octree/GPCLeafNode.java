package es.igosoftware.euclid.pointscloud.octree;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import es.igosoftware.euclid.bounding.GAxisAlignedOrthotope;
import es.igosoftware.euclid.octree.GOTLeafNode;
import es.igosoftware.euclid.vector.IVector3;
import es.igosoftware.util.GCollections;

public class GPCLeafNode
         extends
            GPCNode {

   private static final long                           serialVersionUID = 1L;

   private final String                                _id;
   private final int                                   _pointsCount;
   private int[]                                       _lodIndices;
   private IVector3<?>                                 _referencePoint;
   private final GAxisAlignedOrthotope<IVector3<?>, ?> _minimumBounds;


   GPCLeafNode(final GOTLeafNode node,
               final Map<String, GPCLeafNode> leafNodes) {
      super(node);

      _id = node.getId();
      _pointsCount = node.getVerticesIndexesCount();
      _minimumBounds = node.getVertices().getBounds();

      //      _lodIndices = new int[8];
      //      Arrays.fill(_lodIndices, 0);
      leafNodes.put(_id, this);
   }


   public String getId() {
      return _id;
   }


   public int[] getLodIndices() {
      return _lodIndices;
   }


   public GAxisAlignedOrthotope<IVector3<?>, ?> getMinimumBounds() {
      return _minimumBounds;
   }


   public int getPointsCount() {
      return _pointsCount;
   }


   public IVector3<?> getReferencePoint() {
      return _referencePoint;
   }


   public void setLodIndices(final List<Integer> lodIndices) {
      _lodIndices = GCollections.toArray(lodIndices);
   }


   public void setReferencePoint(final IVector3<?> point) {
      _referencePoint = point;
   }


   @Override
   public String toString() {
      //      return "GPCLeafNode [id=" + _id + ", pointsCount=" + _pointsCount + ", lodIndices=" + Arrays.toString(_lodIndices)
      //      + ", minimumBounds=" + _minimumBounds + "]";
      return "GPCLeafNode [id=" + _id + ", pointsCount=" + _pointsCount + ", lodIndices=" + Arrays.toString(_lodIndices) + "]";
   }


}
