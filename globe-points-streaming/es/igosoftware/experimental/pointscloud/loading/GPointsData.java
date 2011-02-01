

package es.igosoftware.experimental.pointscloud.loading;

import java.io.Serializable;
import java.util.List;

import es.igosoftware.euclid.vector.IVector3;


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
      _intensities = toFloatArray(intensities);
      _normals = toVectorsArray(normals);
      _colors = toIntArray(colors);
   }


   private int[] toIntArray(final List<Integer> ints) {
      if ((ints == null) || ints.isEmpty()) {
         return null;
      }

      final int[] result = new int[ints.size()];

      for (int i = 0; i < result.length; i++) {
         result[i] = ints.get(i);
      }

      return result;
   }


   private float[] toFloatArray(final List<Float> floats) {
      if ((floats == null) || floats.isEmpty()) {
         return null;
      }

      final float[] result = new float[floats.size()];

      for (int i = 0; i < result.length; i++) {
         result[i] = floats.get(i);
      }

      return result;
   }


   private IVector3<?>[] toVectorsArray(final List<IVector3<?>> vectors) {
      if ((vectors == null) || vectors.isEmpty()) {
         return null;
      }

      return vectors.toArray(new IVector3<?>[] {});
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
