

package es.igosoftware.euclid.ntree;

import java.util.Collection;

import es.igosoftware.euclid.IBoundedGeometry;
import es.igosoftware.euclid.bounding.GAxisAlignedOrthotope;
import es.igosoftware.euclid.bounding.IFiniteBounds;
import es.igosoftware.euclid.vector.IVector;
import es.igosoftware.euclid.vector.IVector2;
import es.igosoftware.euclid.vector.IVector3;
import es.igosoftware.util.GAssert;


public class GGeometryNTreeParameters {
   public static enum BoundsPolicy {
      GIVEN,

      MINIMUM,

      REGULAR,
      REGULAR_AND_CENTERED,

      DIMENSIONS_MULTIPLE_OF_SMALLEST,
      DIMENSIONS_MULTIPLE_OF_SMALLEST_AND_CENTERED;
   }


   public static interface AcceptLeafNodeCreationPolicy<

   VectorT extends IVector<VectorT, ?, ?>,

   ElementT,

   GeometryT extends IBoundedGeometry<VectorT, ?, ? extends IFiniteBounds<VectorT, ?>>

   > {
      public boolean accept(final int depth,
                            final GAxisAlignedOrthotope<VectorT, ?> bounds,
                            final Collection<ElementT> elements);
   }


   public static interface Accept3DLeafNodeCreationPolicy<

   ElementT,

   GeometryT extends IBoundedGeometry<IVector3<?>, ?, ? extends IFiniteBounds<IVector3<?>, ?>>

   >
            extends
               AcceptLeafNodeCreationPolicy<IVector3<?>, ElementT, GeometryT> {


   }


   public static interface Accept2DLeafNodeCreationPolicy<

   ElementT,

   GeometryT extends IBoundedGeometry<IVector2<?>, ?, ? extends IFiniteBounds<IVector2<?>, ?>>

   >
            extends
               AcceptLeafNodeCreationPolicy<IVector2<?>, ElementT, GeometryT> {

   }


   private static class DefaultAcceptLeafNodeCreationPolicy<

   VectorT extends IVector<VectorT, ?, ?>,

   ElementT,

   GeometryT extends IBoundedGeometry<VectorT, ?, ? extends IFiniteBounds<VectorT, ?>>

   >
            implements
               AcceptLeafNodeCreationPolicy<VectorT, ElementT, GeometryT> {

      private final int _maxDepth;
      private final int _maxElementsInLeafs;


      private DefaultAcceptLeafNodeCreationPolicy(final int maxDepth,
                                                  final int maxElementsInLeafs) {
         _maxDepth = maxDepth;
         _maxElementsInLeafs = maxElementsInLeafs;
      }


      @Override
      public boolean accept(final int depth,
                            final GAxisAlignedOrthotope<VectorT, ?> bounds,
                            final Collection<ElementT> elements) {
         if ((elements.size() <= _maxElementsInLeafs) || (depth >= _maxDepth + 1)) {
            return true;
         }

         return false;
      }
   }


   final boolean                      _verbose;
   final AcceptLeafNodeCreationPolicy _acceptLeafNodeCreationPolicy;
   final BoundsPolicy                 _boundsPolicy;
   final boolean                      _multiThread;


   public GGeometryNTreeParameters(final boolean verbose,
                                   final AcceptLeafNodeCreationPolicy acceptLeafNodeCreationPolicy,
                                   final BoundsPolicy boundsPolicy,
                                   final boolean multiThread) {
      GAssert.notNull(acceptLeafNodeCreationPolicy, "acceptLeafNodeCreationPolicy");
      GAssert.notNull(boundsPolicy, "boundsPolicy");

      _verbose = verbose;
      _acceptLeafNodeCreationPolicy = acceptLeafNodeCreationPolicy;
      _boundsPolicy = boundsPolicy;
      _multiThread = multiThread;
   }


   public GGeometryNTreeParameters(final boolean verbose,
                                   final int maxDepth,
                                   final int maxElementsInLeafs,
                                   final BoundsPolicy boundsPolicy,
                                   final boolean multiThread) {
      GAssert.isPositive(maxDepth, "maxDepth");
      GAssert.isPositive(maxElementsInLeafs, "maxElementsInLeafs");
      GAssert.notNull(boundsPolicy, "boundsPolicy");

      _verbose = verbose;
      _acceptLeafNodeCreationPolicy = new DefaultAcceptLeafNodeCreationPolicy(maxDepth, maxElementsInLeafs);
      _boundsPolicy = boundsPolicy;
      _multiThread = multiThread;
   }

}
