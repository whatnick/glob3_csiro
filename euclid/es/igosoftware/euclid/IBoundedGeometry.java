package es.igosoftware.euclid;

import es.igosoftware.euclid.bounding.IBounds;
import es.igosoftware.euclid.vector.IVector;

public interface IBoundedGeometry<

VectorT extends IVector<VectorT, ?>,

GeometryT extends IBoundedGeometry<VectorT, GeometryT, BoundsT>,

BoundsT extends IBounds<VectorT, BoundsT>

>
         extends
            IGeometry<VectorT, GeometryT> {


   //public IBounds<VectorT> getBounds();
   public BoundsT getBounds();


   public boolean containsOnBoundary(final VectorT point);


   public double squaredDistanceToBoundary(final VectorT point);


   public double distanceToBoundary(final VectorT point);


   public VectorT closestPointOnBoundary(final VectorT point);


}
