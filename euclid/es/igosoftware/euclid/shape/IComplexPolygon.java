

package es.igosoftware.euclid.shape;

import es.igosoftware.euclid.bounding.IBounds;
import es.igosoftware.euclid.vector.IVector;


public interface IComplexPolygon<

VectorT extends IVector<VectorT, ?>,

SegmentT extends GSegment<VectorT, SegmentT, ?>,

BoundsT extends IBounds<VectorT, BoundsT>

>
         extends
            IPolygon<VectorT, SegmentT, BoundsT>,
            IComplexPolytope<VectorT, SegmentT, BoundsT> {

   @Override
   public ISimplePolygon<VectorT, SegmentT, BoundsT> getHull();

}
