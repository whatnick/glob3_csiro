package es.igosoftware.euclid.vector;

import java.io.Serializable;
import java.util.Comparator;


public interface IVector2<GeometryT extends IVector2<GeometryT>>
         extends
            IVector<IVector2<?>, GeometryT> {

   public static class DefaultComparator
            implements
               Comparator<IVector2<?>>,
               Serializable {
      private static final long serialVersionUID = 1L;


      @Override
      public int compare(final IVector2<?> p1,
                         final IVector2<?> p2) {
         return GVectorUtils.compare(p1, p2);
      }
   }


   public double x();


   public double y();

}
