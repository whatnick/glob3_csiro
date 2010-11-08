package es.igosoftware.euclid.shape;

import java.util.List;

import es.igosoftware.euclid.bounding.IBounds;
import es.igosoftware.euclid.vector.IPointsContainer;
import es.igosoftware.euclid.vector.IVector;

public interface IPolytope<

VectorT extends IVector<VectorT, ?>,

SegmentT extends GSegment<VectorT, SegmentT, BoundsT>,

GeometryT extends IPolytope<VectorT, SegmentT, GeometryT, BoundsT>,

BoundsT extends IBounds<VectorT, BoundsT>

>
         extends
            IPointsContainer<VectorT, GeometryT> {


   public boolean isSelfIntersected();


   public IPolytope<VectorT, SegmentT, ?, BoundsT> createSimplified(final double capsRadiansTolerance);


   public IPolytope<VectorT, SegmentT, ?, BoundsT> getHull();


   public List<SegmentT> getEdges();

}
