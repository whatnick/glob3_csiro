

package es.igosoftware.euclid;

import es.igosoftware.euclid.vector.IVector;
import es.igosoftware.util.GAssert;
import es.igosoftware.util.GMath;


public class GRay<VectorT extends IVector<VectorT, ?>>
         implements
            IGeometry<VectorT> {

   private static final long serialVersionUID = 1L;


   public final VectorT      _a;
   public final VectorT      _b;


   protected GRay(final VectorT a,
                  final VectorT b) {
      GAssert.notNull(a, "a");
      GAssert.notNull(b, "b");

      if (a.closeTo(b)) {
         throw new RuntimeException("Points 'a' and 'b' can't be the same point");
      }

      _a = a;
      _b = b;
   }


   @Override
   public byte dimensions() {
      return _a.dimensions();
   }


   @Override
   public boolean contains(final VectorT point) {
      return GMath.closeToZero(distance(point));
   }


   @Override
   public double squaredDistance(final VectorT point) {
      // from Real-Time Collision Detection - Christer Ericson 
      //   page 130

      final VectorT ab = _b.sub(_a);
      final VectorT pointSubA = point.sub(_a);

      final double e = pointSubA.dot(ab);

      // Handle cases where point projects outside _a
      //      if (e <= 0) {
      if (GMath.negativeOrZero(e)) {
         return pointSubA.dot(pointSubA);
      }
      final double f = ab.dot(ab);

      return pointSubA.dot(pointSubA) - e * e / f;
   }


   @Override
   public double distance(final VectorT point) {
      return GMath.sqrt(squaredDistance(point));
   }


   @Override
   public VectorT closestPoint(final VectorT point) {
      // from Real-Time Collision Detection - Christer Ericson 
      //   page 129

      final VectorT pointMinusFrom = point.sub(_a);
      final VectorT ab = _b.sub(_a);

      // Project c onto ab, but deferring divide by Dot(ab, ab)
      final double t = ab.dot(pointMinusFrom);

      if (GMath.negativeOrZero(t)) {
         return _a;
      }

      final double denom = ab.dot(ab); // Always nonnegative since denom = ||ab||∧2

      // c projects inside the [a,b] interval; must do deferred divide now
      return _a.add(ab.scale(t / denom));
   }


   @Override
   public double precision() {
      return _a.precision();
   }


   @Override
   public GRay<VectorT> clone() {
      return this;
   }


   public VectorT getA() {
      return _a;
   }


   public VectorT getB() {
      return _b;
   }


   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((_a == null) ? 0 : _a.hashCode());
      result = prime * result + ((_b == null) ? 0 : _b.hashCode());
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
      final GRay other = (GRay) obj;
      if (_a == null) {
         if (other._a != null) {
            return false;
         }
      }
      else if (!_a.equals(other._a)) {
         return false;
      }
      if (_b == null) {
         if (other._b != null) {
            return false;
         }
      }
      else if (!_b.equals(other._b)) {
         return false;
      }
      return true;
   }


   @Override
   public VectorT getCentroid() {
      throw new RuntimeException("Rays has not a centroid");
   }


}
