package es.igosoftware.euclid;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import es.igosoftware.euclid.vector.IVector;

public abstract class GGeometryAbstract<

VectorT extends IVector<VectorT, ?>,

GeometryT extends GGeometryAbstract<VectorT, GeometryT>

>
         implements
            IGeometry<VectorT, GeometryT> {

   private static final long serialVersionUID = 1L;


   @Override
   public final void save(final String fileName) throws IOException {
      DataOutputStream output = null;

      try {
         output = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(fileName)));

         save(output);
      }
      finally {
         if (output != null) {
            output.close();
         }
      }
   }


   @Override
   public double distance(final VectorT point) {
      return Math.sqrt(squaredDistance(point));
   }


   @Override
   public final Object clone() {
      return this;
   }

}
