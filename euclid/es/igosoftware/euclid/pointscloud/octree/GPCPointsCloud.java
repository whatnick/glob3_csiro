package es.igosoftware.euclid.pointscloud.octree;

import java.io.Serializable;
import java.util.Map;

import es.igosoftware.euclid.octree.GOTInnerNode;
import es.igosoftware.euclid.projection.GProjection;

public class GPCPointsCloud
         implements
            Serializable {


   private static final long  serialVersionUID = 1L;


   private final GProjection  _projection;
   private final GPCInnerNode _root;
   private final int          _verticesCount;
   private final boolean      _hasColors;
   private final boolean      _hasNormals;
   private final boolean      _hasIntensities;
   private final float        _minIntensity;
   private final float        _maxIntensity;
   private final double       _minElevation;
   private final double       _maxElevation;


   public GPCPointsCloud(final GOTInnerNode root,
                         final Map<String, GPCLeafNode> leafNodes,
                         final GProjection projection,
                         final int verticesCount,
                         final boolean hasIntensities,
                         final boolean hasNormals,
                         final boolean hasColors,
                         final float minIntensity,
                         final float maxIntensity,
                         final double minElevation,
                         final double maxElevation) {
      _projection = projection;
      _verticesCount = verticesCount;
      _root = new GPCInnerNode(root, leafNodes);

      _hasIntensities = hasIntensities;
      _hasNormals = hasNormals;
      _hasColors = hasColors;

      _minIntensity = minIntensity;
      _maxIntensity = maxIntensity;

      _minElevation = minElevation;
      _maxElevation = maxElevation;
   }


   public GProjection getProjection() {
      return _projection;
   }


   public GPCInnerNode getRoot() {
      return _root;
   }


   public int getVerticesCount() {
      return _verticesCount;
   }


   public boolean hasIntensities() {
      return _hasIntensities;
   }


   public boolean hasNormals() {
      return _hasNormals;
   }


   public boolean hasColors() {
      return _hasColors;
   }


   public float getMinIntensity() {
      return _minIntensity;
   }


   public float getMaxIntensity() {
      return _maxIntensity;
   }


   public double getMinElevation() {
      return _minElevation;
   }


   public double getMaxElevation() {
      return _maxElevation;
   }

}
