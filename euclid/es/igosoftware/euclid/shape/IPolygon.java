package es.igosoftware.euclid.shape;

import es.igosoftware.euclid.bounding.IBounds;
import es.igosoftware.euclid.vector.IVector;

public interface IPolygon<

VectorT extends IVector<VectorT, ?>,

SegmentT extends GSegment<VectorT, SegmentT, BoundsT>,

GeometryT extends IPolygon<VectorT, SegmentT, GeometryT, BoundsT>,

BoundsT extends IBounds<VectorT, BoundsT>

>
         extends
            IPolytope<VectorT, SegmentT, GeometryT, BoundsT> {


   @Override
   public IPolygon<VectorT, SegmentT, ?, BoundsT> createSimplified(final double capsRadiansTolerance);


   public BoundsT getBounds();


   @Override
   public IPolygon<VectorT, SegmentT, ?, BoundsT> getHull();

}
