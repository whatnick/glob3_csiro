

package es.igosoftware.euclid.octree.geometry;

import es.igosoftware.util.GAssert;


public class GGeometryNTreeParameters {
   public static enum BoundsPolicy {
      MINIMUM,
      REGULAR,
      REGULAR_AND_CENTERED;
   }


   final boolean      _verbose;
   final int          _maxDepth;
   final int          _maxGeometriesInLeafs;
   final BoundsPolicy _boundsPolicy;


   public GGeometryNTreeParameters(final boolean verbose,
                                   final int maxDepth,
                                   final int maxGeometriesInLeafs,
                                   final BoundsPolicy boundsPolicy) {
      GAssert.isPositive(maxDepth, "maxDepth");
      GAssert.isPositive(maxGeometriesInLeafs, "maxGeometriesInLeafs");
      GAssert.notNull(boundsPolicy, "boundsPolicy");

      _verbose = verbose;
      _maxDepth = maxDepth;
      _maxGeometriesInLeafs = maxGeometriesInLeafs;
      _boundsPolicy = boundsPolicy;
   }

}
