package es.igosoftware.euclid.shape;

import java.util.Collections;
import java.util.List;

import es.igosoftware.euclid.GGeometryAbstract;
import es.igosoftware.euclid.bounding.IBounds;
import es.igosoftware.euclid.vector.IVector;
import es.igosoftware.util.GAssert;

public abstract class GPolytopeAbstract<

VectorT extends IVector<VectorT, ?>,

SegmentT extends GSegment<VectorT, SegmentT, BoundsT>,

GeometryT extends GPolytopeAbstract<VectorT, SegmentT, GeometryT, BoundsT>,

BoundsT extends IBounds<VectorT, BoundsT>

>
         extends
            GGeometryAbstract<VectorT, GeometryT>
         implements
            IPolytope<VectorT, SegmentT, GeometryT, BoundsT> {

   private static final long serialVersionUID = 1L;


   private List<SegmentT>    _edges;


   @Override
   public final List<SegmentT> getEdges() {
      if (_edges == null) {
         final List<SegmentT> initialEdges = initializeEdges();
         GAssert.notEmpty(initialEdges, "edges");
         _edges = Collections.unmodifiableList(initialEdges);
      }
      return _edges;
   }


   protected abstract List<SegmentT> initializeEdges();


   @Override
   public VectorT closestPoint(final VectorT point) {
      GAssert.notNull(point, "point");

      if (contains(point)) {
         return point;
      }

      return closestPointOnBoundary(point);
   }


   private VectorT closestPointOnBoundary(final VectorT point) {
      GAssert.notNull(point, "point");

      double minDistance = Double.POSITIVE_INFINITY;
      VectorT closestPoint = null;

      for (final SegmentT edge : getEdges()) {
         final VectorT currentPoint = edge.closestPointOnBoundary(point);
         final double currentDistance = currentPoint.squaredDistance(point);

         if (currentDistance <= minDistance) {
            minDistance = currentDistance;
            closestPoint = currentPoint;
         }
      }

      return closestPoint;
   }


}
