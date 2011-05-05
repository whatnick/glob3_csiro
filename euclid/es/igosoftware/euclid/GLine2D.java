

package es.igosoftware.euclid;

import es.igosoftware.euclid.vector.IVector2;


public class GLine2D
         extends
            GLine<IVector2>
         implements
            IGeometry2D {


   private static final long serialVersionUID = 1L;


   public GLine2D(final IVector2 a,
                  final IVector2 b) {
      super(a, b);
   }


   @Override
   public String toString() {
      return "GLine2D [a=" + _a + ", b=" + _b + "]";
   }


}
