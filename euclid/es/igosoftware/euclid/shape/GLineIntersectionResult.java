

package es.igosoftware.euclid.shape;

import es.igosoftware.euclid.vector.IVector;
import es.igosoftware.util.GAssert;


public class GLineIntersectionResult<VectorT extends IVector<VectorT, ?>> {

   public static enum Type {
      PARALLEL,
      COINCIDENT,
      NOT_INTERSECTING,
      INTERSECTING;
   }


   private final GLineIntersectionResult.Type _type;
   private final VectorT                      _point;


   public GLineIntersectionResult(final GLineIntersectionResult.Type type,
                                  final VectorT point) {
      GAssert.notNull(type, "type");

      if (type == GLineIntersectionResult.Type.INTERSECTING) {
         GAssert.notNull(point, "point");
      }

      _type = type;
      _point = point;
   }


   public GLineIntersectionResult.Type getType() {
      return _type;
   }


   public VectorT getPoint() {
      return _point;
   }


   @Override
   public String toString() {
      return "GLineIntersectionResult [type=" + _type + ", point=" + _point + "]";
   }


}
