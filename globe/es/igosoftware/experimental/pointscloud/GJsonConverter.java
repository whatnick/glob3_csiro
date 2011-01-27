

package es.igosoftware.experimental.pointscloud;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import es.igosoftware.euclid.bounding.GAxisAlignedOrthotope;
import es.igosoftware.euclid.pointscloud.octree.GPCInnerNode;
import es.igosoftware.euclid.pointscloud.octree.GPCLeafNode;
import es.igosoftware.euclid.pointscloud.octree.GPCNode;
import es.igosoftware.euclid.pointscloud.octree.GPCPointsCloud;
import es.igosoftware.euclid.vector.IVector3;


public class GJsonConverter {

   private GJsonConverter() {
   }


   public static Map<String, Object> convertToJSON(final GPCPointsCloud pointsCloud) {
      final Map<String, Object> result = new HashMap<String, Object>();

      result.put("root", convertToJSON(pointsCloud.getRoot()));

      result.put("projection", pointsCloud.getProjection().name());

      result.put("verticesCount", pointsCloud.getVerticesCount());

      result.put("hasColors", pointsCloud.hasColors());
      result.put("hasNormals", pointsCloud.hasNormals());
      result.put("hasIntensities", pointsCloud.hasIntensities());

      result.put("minIntensity", pointsCloud.getMinIntensity());
      result.put("maxIntensity", pointsCloud.getMaxIntensity());

      result.put("minElevation", pointsCloud.getMinElevation());
      result.put("maxElevation", pointsCloud.getMaxElevation());

      return result;
   }


   static private Map<String, Object> convertToJSON(final GPCInnerNode innerNode) {
      final Map<String, Object> result = new HashMap<String, Object>();

      final GPCNode[] children = innerNode.getChildren();

      final List<Map<String, Object>> childrenJSON = new ArrayList<Map<String, Object>>(children.length);

      for (final GPCNode child : children) {
         childrenJSON.add(convertToJSON(child));
      }

      result.put("bounds", convertToJSON(innerNode.getBounds()));
      result.put("children", childrenJSON);

      return result;
   }


   static private Map<String, Object> convertToJSON(final GPCNode node) {
      if (node instanceof GPCInnerNode) {
         return convertToJSON((GPCInnerNode) node);
      }
      else if (node instanceof GPCLeafNode) {
         return convertToJSON((GPCLeafNode) node);
      }
      else {
         throw new RuntimeException("class " + node.getClass() + " not supported");
      }
   }


   static private Map<String, Object> convertToJSON(final GPCLeafNode node) {
      final Map<String, Object> result = new HashMap<String, Object>();

      result.put("bounds", convertToJSON(node.getBounds()));

      result.put("id", node.getId());
      result.put("pointsCount", node.getPointsCount());
      result.put("lodIndices", node.getLodIndices());
      result.put("referencePoint", convertToJSON(node.getReferencePoint()));
      result.put("minimumBounds", convertToJSON(node.getMinimumBounds()));

      return result;
   }


   static private Map<String, Object> convertToJSON(final GAxisAlignedOrthotope<IVector3<?>, ?> box) {
      final Map<String, Object> result = new HashMap<String, Object>();
      result.put("lower", convertToJSON(box._lower));
      result.put("upper", convertToJSON(box._upper));
      return result;
   }


   static private Object convertToJSON(final IVector3<?> vector) {
      final Map<String, Object> result = new HashMap<String, Object>();
      result.put("x", vector.x());
      result.put("y", vector.y());
      result.put("z", vector.z());
      return result;
   }

}
