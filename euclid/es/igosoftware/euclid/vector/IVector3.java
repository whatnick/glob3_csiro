package es.igosoftware.euclid.vector;

import java.io.Serializable;
import java.util.Comparator;


//public interface IVector3
//         extends
//            IVector<IVector3>,
//            IGeometry<IVector3, IVector3> {
public interface IVector3<GeometryT extends IVector3<GeometryT>>
         extends
            IVector<IVector3<?>, GeometryT> {

   public static class DefaultComparator
            implements
               Comparator<IVector3<?>>,
               Serializable {

      private static final long serialVersionUID = 1L;


      @Override
      public int compare(final IVector3<?> p1,
                         final IVector3<?> p2) {
         return GVectorUtils.compare(p1, p2);
      }
   }


   public double x();


   public double y();


   public double z();


   @Override
   public IVector2<?> asVector2();


   public IVector3<?> cross(final IVector3<?> that);


}
