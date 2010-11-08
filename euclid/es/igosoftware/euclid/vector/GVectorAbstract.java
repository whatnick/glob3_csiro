package es.igosoftware.euclid.vector;

import es.igosoftware.euclid.GGeometryAbstract;
import es.igosoftware.util.GMath;


public abstract class GVectorAbstract<

VectorT extends IVector<VectorT, ?>,

GeometryT extends GVectorAbstract<VectorT, GeometryT>

>
         extends
            GGeometryAbstract<VectorT, GeometryT>
         implements
            IVector<VectorT, GeometryT> {

   private static final long serialVersionUID = 1L;


   @Override
   public boolean isNormalized() {
      return (GMath.closeTo(squaredLength(), 1));
   }


   @Override
   public double length() {
      return Math.sqrt(squaredLength());
   }


   @Override
   public final boolean contains(final VectorT point) {
      return closeTo(point);
   }


   @Override
   public final double angle(final VectorT that) {
      //      // scale the vectors to reduce the floats problems
      //      final VectorT thisScaled = scale(10);
      //      final VectorT thatScaled = that.scale(10);
      //
      //      final double dot = thisScaled.dot(thatScaled) / (thisScaled.length() * thatScaled.length());
      //      final double campledDot = GMath.clamp(dot, -1, 1);
      //      return Math.acos(campledDot);

      final double dot = dot(that) / (length() * that.length());
      final double campledDot = GMath.clamp(dot, -1, 1);
      return Math.acos(campledDot);
   }


   @Override
   public final VectorT closestPoint(final VectorT point) {
      return point;
   }


   @Override
   public final VectorT clamp(final VectorT min,
                              final VectorT max) {
      return max(min).min(max);
   }


   @Override
   public abstract boolean equals(final Object that);

   //   @Override
   //   public boolean isZero() {
   //      return closeToZero();
   //   }

}
