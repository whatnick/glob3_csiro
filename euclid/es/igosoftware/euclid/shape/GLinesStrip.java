

package es.igosoftware.euclid.shape;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import es.igosoftware.euclid.bounding.IBounds;
import es.igosoftware.euclid.vector.GVectorUtils;
import es.igosoftware.euclid.vector.IVector;
import es.igosoftware.util.GAssert;


public abstract class GLinesStrip<

VectorT extends IVector<VectorT, ?>,

SegmentT extends GSegment<VectorT, BoundsT>,

BoundsT extends IBounds<VectorT, BoundsT>

>
         extends
            GPolytopeAbstract<VectorT, SegmentT, BoundsT> {


   private static final long   serialVersionUID = 1L;

   private final List<VectorT> _points;


   protected GLinesStrip(final boolean validate,
                         final VectorT... points) {
      GAssert.notEmpty(points, "points");
      GAssert.notNullElements(points, "points");

      _points = new ArrayList<VectorT>(points.length);
      for (final VectorT point : points) {
         _points.add(point);
      }
      if (validate) {
         validate();
      }
   }


   protected GLinesStrip(final boolean validate,
                         final List<VectorT> points) {
      GAssert.notEmpty(points, "points");
      GAssert.notNullElements(points, "points");

      _points = new ArrayList<VectorT>(points);
      if (validate) {
         validate();
      }
   }


   protected void validate() {
      if (_points.size() < 2) {
         throw new IllegalArgumentException("A LineStrip must have at least 2 points");
      }


      for (int i = 0; i < _points.size(); i++) {
         final VectorT current = _points.get(i);

         final int nextI = (i + 1) % _points.size();
         final VectorT next = _points.get(nextI);

         if (current.closeTo(next)) {
            //         if (current.equals(next)) {
            throw new IllegalArgumentException("Two consecutive points (#" + i + "/#" + nextI + ") can't be the same");
         }
      }


      if (isSelfIntersected()) {
         //throw new IllegalArgumentException("Can't create a self-intersected polygon " + this);
         throw new IllegalArgumentException("Can't create a self-intersected polygon");
      }

   }


   @Override
   public abstract boolean isSelfIntersected();


   @Override
   public final List<VectorT> getPoints() {
      return Collections.unmodifiableList(_points);
   }


   @Override
   public VectorT getPoint(final int i) {
      return _points.get(i);
   }


   @Override
   public double precision() {
      return _points.get(0).precision();
   }


   @Override
   public byte dimensions() {
      return _points.get(0).dimensions();
   }


   @Override
   public int getPointsCount() {
      return _points.size();
   }


   @Override
   public boolean contains(final VectorT point) {
      for (final SegmentT edge : getEdges()) {
         if (edge.contains(point)) {
            return true;
         }
      }
      return false;
   }


   @Override
   public double squaredDistance(final VectorT point) {
      double shortestDistance = Double.POSITIVE_INFINITY;

      for (final SegmentT edge : getEdges()) {
         final double currentDistance = edge.squaredDistance(point);
         if (currentDistance < shortestDistance) {
            shortestDistance = currentDistance;
         }
      }

      return shortestDistance;
   }


   @Override
   public VectorT closestPointOnBoundary(final VectorT point) {
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


   @Override
   public Iterator<VectorT> iterator() {
      return getPoints().iterator();
   }


   @Override
   public final void save(final DataOutputStream output) throws IOException {
      output.writeInt(_points.size());
      for (final VectorT point : _points) {
         point.save(output);
      }
   }


   public boolean isConvex() {
      return false;
   }


   @Override
   public VectorT getCentroid() {
      return GVectorUtils.getAverage(_points);
   }


   @Override
   public IPolytope<VectorT, SegmentT, BoundsT> getHull() {
      return this;
   }

}
