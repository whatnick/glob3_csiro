

package es.igosoftware.euclid.octree.geometry;

public class GGeometryNTreeParameters {
   public static enum BoundsPolicy {
      MINIMUM,
      REGULAR,
      REGULAR_AND_CENTERED;
   }


   final boolean      _verbose;
   final int          _maxDepth;
   final int          _maxGeometries;
   final BoundsPolicy _boundsPolicy;


   public GGeometryNTreeParameters(final boolean verbose,
                                   final int maxDepth,
                                   final int maxGeometries,
                                   final BoundsPolicy boundsPolicy) {
      _verbose = verbose;
      _maxDepth = maxDepth;
      _maxGeometries = maxGeometries;
      _boundsPolicy = boundsPolicy;
   }

}
