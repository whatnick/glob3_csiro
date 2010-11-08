package es.igosoftware.euclid.bounding;

import java.io.DataOutputStream;
import java.io.IOException;

import es.igosoftware.euclid.GGeometryAbstract;
import es.igosoftware.euclid.shape.GSegment;
import es.igosoftware.euclid.vector.IVector;
import es.igosoftware.util.GMath;

public abstract class GNCapsule<

VectorT extends IVector<VectorT, ?>,

GeometryT extends GNCapsule<VectorT, GeometryT>>
         extends
            GGeometryAbstract<VectorT, GeometryT>
         implements
            IBounds<VectorT, GeometryT> {

   /**
    * 
    */
   private static final long            serialVersionUID = 1L;

   //   public final VectorT      _startPoint;
   //   public final VectorT      _endPoint;
   public final GSegment<VectorT, ?, ?> _segment;
   public final double                  _radius;


   public GNCapsule(final GSegment<VectorT, ?, ?> segment,
                    final double radius) {
      super();
      _segment = segment;
      _radius = radius;
   }


   @Override
   public final byte dimensions() {
      return _segment.dimensions();
   }


   @Override
   public final double precision() {
      return _segment.precision();
   }


   @Override
   public final String toString() {
      return getStringName() + " [Segment:" + _segment + " Radius: " + _radius + "]";
   }


   protected abstract String getStringName();


   @Override
   public final void save(final DataOutputStream output) throws IOException {
      _segment.save(output);
      output.writeDouble(_radius);
   }


   @Override
   public final boolean contains(final VectorT point) {
      return GMath.lessOrEquals(_segment.squaredDistance(point), (_radius * _radius));

   }


   @Override
   public final boolean containsOnBoundary(final VectorT point) {
      return GMath.closeToZero(distanceToBoundary(point));
   }


   public abstract GNCapsule<VectorT, GeometryT> expandedByDistance(final double delta);


   @Override
   public final double squaredDistance(final VectorT point) {
      final double distance = distance(point);
      return distance * distance;
   }


   @Override
   public final double squaredDistanceToBoundary(final VectorT point) {
      final double distance = distanceToBoundary(point);
      return distance * distance;
   }


   @Override
   public final double distance(final VectorT point) {
      if (contains(point)) {
         return 0;
      }

      return distanceToBoundary(point);
   }


   @Override
   public final double distanceToBoundary(final VectorT point) {
      return _segment.closestPoint(point).distance(point) - _radius;
   }


   @Override
   public final VectorT closestPoint(final VectorT point) {
      if (contains(point)) {
         return point;
      }

      return closestPointOnBoundary(point);
   }


   @Override
   public final VectorT closestPointOnBoundary(final VectorT point) {
      final VectorT closestPoint = _segment.closestPoint(point);
      final VectorT segmentRadiusDirection = closestPoint.sub(point).normalized();

      return closestPoint.add(segmentRadiusDirection.scale(_radius));
   }


   @Override
   public boolean closeTo(final GeometryT that) {
      return _segment.closeTo(that._segment) && GMath.closeTo(_radius, that._radius);
   }


   public boolean isFullInside(final GAxisAlignedOrthotope<VectorT, ?> orthotope) {


      final VectorT lower = _segment.closestPoint(orthotope._lower).sub(_radius);
      final VectorT upper = _segment.closestPoint(orthotope._upper).add(_radius);

      return lower.greaterOrEquals(orthotope._lower) && upper.lessOrEquals(orthotope._upper);
   }

}
