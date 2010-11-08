package es.igosoftware.euclid.bounding;

import es.igosoftware.euclid.IBoundedGeometry;
import es.igosoftware.euclid.vector.IVector;


public interface IBounds<

VectorT extends IVector<VectorT, ?>,

GeometryT extends IBounds<VectorT, GeometryT>

>
         extends
            IBoundedGeometry<VectorT, GeometryT, GeometryT> {


   //   public GAxisAlignedBox asBox();


   //   public IBounds<VectorT, ?> combinedWith(final IBounds<VectorT, ?> that);

   public boolean closeTo(final GeometryT that);
}
