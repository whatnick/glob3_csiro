

package es.igosoftware.euclid;

import es.igosoftware.euclid.vector.IVector3;


public class GRay3D
         extends
            GRay<IVector3>
         implements
            IGeometry3D {


   private static final long serialVersionUID = 1L;


   public GRay3D(final IVector3 a,
                 final IVector3 b) {
      super(a, b);
   }


   @Override
   public String toString() {
      return "GRay3D [a=" + _a + ", b=" + _b + "]";
   }

}
