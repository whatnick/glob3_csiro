package es.igosoftware.euclid.shape;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import es.igosoftware.euclid.bounding.IBounds;
import es.igosoftware.euclid.vector.IVector;
import es.igosoftware.util.GAssert;


public abstract class GComplexPolytope<

VectorT extends IVector<VectorT, ?>,

SegmentT extends GSegment<VectorT, SegmentT, BoundsT>,

GeometryT extends GComplexPolytope<VectorT, SegmentT, GeometryT, BoundsT, PolytopeT>,

BoundsT extends IBounds<VectorT, BoundsT>,

PolytopeT extends IPolytope<VectorT, SegmentT, ?, BoundsT>

>
         extends
            GPolytopeAbstract<VectorT, SegmentT, GeometryT, BoundsT> {

   private static final long       serialVersionUID = 1L;


   protected final PolytopeT       _hull;
   protected final List<PolytopeT> _holes;


   public GComplexPolytope(final PolytopeT hull,
                           final List<? extends PolytopeT> holes) {
      GAssert.notNull(hull, "hull");
      GAssert.notEmpty(holes, "holes");
      GAssert.notNullElements(holes, "holes");

      _hull = hull;
      _holes = new ArrayList<PolytopeT>(holes);
      validate();
   }


   @Override
   public final byte dimensions() {
      return _hull.dimensions();
   }


   @Override
   public final double precision() {
      return _hull.precision();
   }


   protected void validate() {
      // TODO: check holes intersections
      // TODO: check holes are inside the hull
      if (_hull == null) {
         throw new IllegalArgumentException("hull can't be null");
      }

      for (final PolytopeT hole : _holes) {
         if (hole == null) {
            throw new IllegalArgumentException("hole can't be null " + _holes);
         }
      }

   }


   @Override
   public final List<VectorT> getPoints() {
      final ArrayList<VectorT> points = new ArrayList<VectorT>();
      points.addAll(_hull.getPoints());
      for (final PolytopeT hole : _holes) {
         points.addAll(hole.getPoints());
      }
      points.trimToSize();
      return Collections.unmodifiableList(points);
   }


   @Override
   public final VectorT getPoint(final int i) {
      return getPoints().get(i);
   }


   @Override
   public final int getPointsCount() {
      int count = _hull.getPointsCount();
      for (final PolytopeT hole : _holes) {
         // System.out.println("hole=" + hole);
         count += hole.getPointsCount();
      }
      return count;
   }


   @Override
   public final Iterator<VectorT> iterator() {
      return getPoints().iterator();
   }


   @Override
   public final String toString() {
      return getStringName() + " (hull=" + _hull + ", holes=" + _holes + ")";
   }


   protected abstract String getStringName();


   @Override
   public final void save(final DataOutputStream output) throws IOException {
      _hull.save(output);
      output.writeInt(_holes.size());
      for (final PolytopeT hole : _holes) {
         hole.save(output);
      }
   }


   @Override
   public abstract boolean isSelfIntersected();


   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((_holes == null) ? 0 : _holes.hashCode());
      result = prime * result + ((_hull == null) ? 0 : _hull.hashCode());
      return result;
   }


   @Override
   public boolean equals(final Object obj) {
      if (this == obj) {
         return true;
      }
      if (obj == null) {
         return false;
      }
      if (getClass() != obj.getClass()) {
         return false;
      }
      final GComplexPolytope other = (GComplexPolytope) obj;
      if (_holes == null) {
         if (other._holes != null) {
            return false;
         }
      }
      else if (!_holes.equals(other._holes)) {
         return false;
      }
      if (_hull == null) {
         if (other._hull != null) {
            return false;
         }
      }
      else if (!_hull.equals(other._hull)) {
         return false;
      }
      return true;
   }


   @Override
   public PolytopeT getHull() {
      return _hull;
   }


   public List<PolytopeT> getHoles() {
      return Collections.unmodifiableList(_holes);
   }

}
