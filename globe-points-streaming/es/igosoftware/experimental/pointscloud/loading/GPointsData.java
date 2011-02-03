

package es.igosoftware.experimental.pointscloud.loading;

import java.io.Serializable;
import java.util.List;

import es.igosoftware.euclid.vector.IVector3;
import es.igosoftware.util.GCollections;


public class GPointsData
         implements
            Serializable {


   private static final long   serialVersionUID = 1L;

   private final IVector3<?>[] _points;
   private final float[]       _intensities;
   private final IVector3<?>[] _normals;
   private final int[]         _colors;


   GPointsData(final List<IVector3<?>> points,
               final List<Float> intensities,
               final List<IVector3<?>> normals,
               final List<Integer> colors) {
      _points = toVectorsArray(points);
      _intensities = GCollections.toFloatArray(intensities);
      _normals = toVectorsArray(normals);
      _colors = GCollections.toIntArray(colors);
   }


   private static IVector3<?>[] toVectorsArray(final List<IVector3<?>> vectors) {
      if (vectors == null) {
         return null;
      }

      return vectors.toArray(new IVector3<?>[vectors.size()]);
   }


   public int pointsCount() {
      return _points.length;
   }


   public IVector3<?>[] getPoints() {
      return _points;
   }


   public float[] getIntensities() {
      return _intensities;
   }


   public IVector3<?>[] getNormals() {
      return _normals;
   }


   public int[] getColors() {
      return _colors;
   }


   @Override
   public String toString() {
      return "GPointsData [points=" + _points.length + "]";
   }

}
