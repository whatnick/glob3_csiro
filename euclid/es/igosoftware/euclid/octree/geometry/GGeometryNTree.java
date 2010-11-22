

package es.igosoftware.euclid.octree.geometry;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import es.igosoftware.euclid.IBoundedGeometry;
import es.igosoftware.euclid.bounding.GAxisAlignedOrthotope;
import es.igosoftware.euclid.bounding.IBounds;
import es.igosoftware.euclid.bounding.IFiniteBounds;
import es.igosoftware.euclid.octree.geometry.GGTInnerNode.GeometriesDistribution;
import es.igosoftware.euclid.shape.GShape;
import es.igosoftware.euclid.vector.GVectorUtils;
import es.igosoftware.euclid.vector.IVector;
import es.igosoftware.util.GHolder;
import es.igosoftware.util.GIntHolder;
import es.igosoftware.util.GLoggerObject;
import es.igosoftware.util.GMath;
import es.igosoftware.util.GProgress;
import es.igosoftware.util.GStringUtils;


public abstract class GGeometryNTree<

VectorT extends IVector<VectorT, ?>,

BoundsT extends GAxisAlignedOrthotope<VectorT, ?>,

GeometryT extends IBoundedGeometry<VectorT, ?, ? extends IFiniteBounds<VectorT, ?>>

>
         extends
            GLoggerObject {


   private final String                              _name;
   private final Collection<GeometryT>               _geometries;
   private final GGeometryNTreeParameters            _parameters;

   private final byte                                _dimensions;
   private final BoundsT                             _geometriesBounds;
   private final BoundsT                             _bounds;

   private GGTInnerNode<VectorT, BoundsT, GeometryT> _root;


   @SuppressWarnings("unchecked")
   protected GGeometryNTree(final String name,
                            final GAxisAlignedOrthotope<VectorT, ?> bounds,
                            final Collection<GeometryT> geometries,
                            final GGeometryNTreeParameters parameters) {
      _name = name;
      _geometries = new ArrayList<GeometryT>(geometries);
      _parameters = parameters;

      final String nameMsg = (_name == null) ? "" : "\"" + _name + "\" ";
      logInfo("Creating " + getTreeName() + " " + nameMsg);


      final GProgress progress = new GProgress(geometries.size()) {
         @Override
         public void informProgress(final double percent,
                                    final long elapsed,
                                    final long estimatedMsToFinish) {
            if (_parameters._verbose) {
               logInfo("  Creating " + getTreeName() + " " + nameMsg + progressString(percent, elapsed, estimatedMsToFinish));
            }
         }
      };

      _geometriesBounds = (BoundsT) GShape.getBounds(geometries);
      _dimensions = _geometriesBounds._extent.dimensions();

      _bounds = (BoundsT) initializeBounds(bounds);


      final GeometriesDistribution<VectorT, GeometryT> distribution = GGTInnerNode.distributeGeometries(_bounds, geometries);

      _root = new GGTInnerNode<VectorT, BoundsT, GeometryT>(null, _bounds, distribution.getOwnGeometries(),
               distribution.getGeometriesToDistribute(), 0, parameters, progress) {
         @Override
         public GGeometryNTree<VectorT, BoundsT, GeometryT> getNTree() {
            return GGeometryNTree.this;
         }
      };

      validate();

      if (_parameters._verbose) {
         showStatistics();
      }
   }


   private void validate() {
      _root.validate();
   }


   private GAxisAlignedOrthotope<VectorT, ?> initializeBounds(final GAxisAlignedOrthotope<VectorT, ?> givenBounds) {

      if ((givenBounds != null) && (_parameters._boundsPolicy != GGeometryNTreeParameters.BoundsPolicy.GIVEN)) {
         throw new IllegalArgumentException("Can't provide a bounds with a policy other that GIVEN");
      }


      switch (_parameters._boundsPolicy) {
         case GIVEN:
            return returnGivenBounds(givenBounds);


         case MINIMUM:
            return _geometriesBounds;


         case DIMENSIONS_MULTIPLE_OF_SMALLEST:
            return multipleOfSmallestDimention(_geometriesBounds);

         case DIMENSIONS_MULTIPLE_OF_SMALLEST_AND_CENTERED:
            return centerBounds(multipleOfSmallestDimention(_geometriesBounds));


         case REGULAR:
            return calculateRegularBounds(_geometriesBounds);

         case REGULAR_AND_CENTERED:
            return centerBounds(calculateRegularBounds(_geometriesBounds));
      }

      throw new IllegalArgumentException("Must not reach here");
   }


   private GAxisAlignedOrthotope<VectorT, ?> centerBounds(final GAxisAlignedOrthotope<VectorT, ?> bounds) {
      final VectorT delta = bounds.getCenter().sub(_geometriesBounds.getCenter());
      return bounds.translatedBy(delta.negated());
   }


   private GAxisAlignedOrthotope<VectorT, ?> calculateRegularBounds(final GAxisAlignedOrthotope<VectorT, ?> bounds) {
      final VectorT extent = bounds._extent;

      double biggestExtension = Double.NEGATIVE_INFINITY;
      for (byte i = 0; i < _dimensions; i++) {
         final double ext = extent.get(i);
         if (ext > biggestExtension) {
            biggestExtension = ext;
         }
      }

      final VectorT newUpper = bounds._lower.add(biggestExtension);
      return GAxisAlignedOrthotope.create(bounds._lower, newUpper);
   }


   private GAxisAlignedOrthotope<VectorT, ?> multipleOfSmallestDimention(final GAxisAlignedOrthotope<VectorT, ?> bounds) {
      final VectorT extent = bounds._extent;

      double smallestExtension = Double.POSITIVE_INFINITY;
      for (byte i = 0; i < _dimensions; i++) {
         final double ext = extent.get(i);
         if (ext < smallestExtension) {
            smallestExtension = ext;
         }
      }

      final VectorT newExtent = smallestBiggerMultipleOf(extent, smallestExtension);
      final VectorT newUpper = bounds._lower.add(newExtent);
      return GAxisAlignedOrthotope.create(bounds._lower, newUpper);
   }


   @SuppressWarnings("unchecked")
   private static <VectorT extends IVector<VectorT, ?>> VectorT smallestBiggerMultipleOf(final VectorT lower,
                                                                                         final double smallestExtension) {

      final byte dimensionsCount = lower.dimensions();

      final double[] dimensionsValues = new double[dimensionsCount];
      for (byte i = 0; i < dimensionsCount; i++) {
         dimensionsValues[i] = smallestBiggerMultipleOf(lower.get(i), smallestExtension);
      }

      return (VectorT) GVectorUtils.createD(dimensionsValues);
   }


   private static double smallestBiggerMultipleOf(final double value,
                                                  final double multiple) {
      if (GMath.closeTo(value, multiple)) {
         return multiple;
      }

      final int times = (int) (value / multiple);

      double result = times * multiple;
      if (value < 0) {
         if (result > value) {
            result -= multiple;
         }
      }
      else {
         if (result < value) {
            result += multiple;
         }
      }

      return result;
   }


   private GAxisAlignedOrthotope<VectorT, ?> returnGivenBounds(final GAxisAlignedOrthotope<VectorT, ?> givenBounds) {
      if (givenBounds == null) {
         throw new IllegalArgumentException("Can't use policy GIVEN without providing a bounds");
      }

      if (!_geometriesBounds.isFullInside(givenBounds)) {
         throw new IllegalArgumentException("The given bounds is not big enough to hold all the geometries");
      }

      return givenBounds;
   }


   private void showStatistics() {
      logInfo("---------------------------------------------------------------");

      if (_name != null) {
         logInfo(" " + getTreeName() + " \"" + _name + "\":");
      }

      logInfo(" ");
      logInfo("  Geometries Bounds: " + _geometriesBounds);
      logInfo("  Geometries Extent: " + _geometriesBounds._extent);

      logInfo(" ");
      logInfo("  Bounds: " + _bounds);
      logInfo("  Extent: " + _bounds._extent);

      final GIntHolder innerNodesCounter = new GIntHolder(0);
      final GIntHolder leafNodesCounter = new GIntHolder(0);
      final GIntHolder geometriesInLeafNodesCounter = new GIntHolder(0);
      final GIntHolder maxGeometriesCountInLeafNodes = new GIntHolder(0);
      final GIntHolder minGeometriesCountInLeafNodes = new GIntHolder(Integer.MAX_VALUE);

      final GIntHolder geometriesInInnerNodesCounter = new GIntHolder(0);
      final GIntHolder maxGeometriesCountInInnerNodes = new GIntHolder(0);
      final GIntHolder minGeometriesCountInInnerNodes = new GIntHolder(Integer.MAX_VALUE);

      final GIntHolder totalDepth = new GIntHolder(0);
      final GIntHolder maxDepth = new GIntHolder(0);
      final GIntHolder minDepth = new GIntHolder(Integer.MAX_VALUE);

      final GHolder<VectorT> totalLeafExtentHolder = new GHolder<VectorT>(null);

      breadthFirstAcceptVisitor(new IGTBreadFirstVisitor<VectorT, BoundsT, GeometryT>() {

         @Override
         public void visitOctree(final GGeometryNTree<VectorT, BoundsT, GeometryT> octree) {
         }


         @Override
         public void visitInnerNode(final GGTInnerNode<VectorT, BoundsT, GeometryT> inner) {
            innerNodesCounter.increment();

            final int geometriesCount = inner.getGeometriesCount();
            geometriesInInnerNodesCounter.increment(geometriesCount);

            if (geometriesCount > maxGeometriesCountInInnerNodes.get()) {
               maxGeometriesCountInInnerNodes.set(geometriesCount);
            }
            if (geometriesCount < minGeometriesCountInInnerNodes.get()) {
               minGeometriesCountInInnerNodes.set(geometriesCount);
            }
         }


         @Override
         public void visitLeafNode(final GGTLeafNode<VectorT, BoundsT, GeometryT> leaf) {
            leafNodesCounter.increment();

            final int geometriesCount = leaf.getGeometriesCount();
            geometriesInLeafNodesCounter.increment(geometriesCount);

            if (geometriesCount > maxGeometriesCountInLeafNodes.get()) {
               maxGeometriesCountInLeafNodes.set(geometriesCount);
            }
            if (geometriesCount < minGeometriesCountInLeafNodes.get()) {
               minGeometriesCountInLeafNodes.set(geometriesCount);
            }

            final int depth = leaf.getDepth();
            totalDepth.increment(depth);
            if (depth > maxDepth.get()) {
               maxDepth.set(depth);
            }
            if (depth < minDepth.get()) {
               minDepth.set(depth);
            }


            final VectorT leafExtent = leaf.getBounds().getExtent();
            final VectorT totalLeafExtent = totalLeafExtentHolder.get();
            if (totalLeafExtent == null) {
               totalLeafExtentHolder.set(leafExtent);
            }
            else {
               totalLeafExtentHolder.set(totalLeafExtentHolder.get().add(leafExtent));
            }
         }
      });


      logInfo(" ");
      logInfo("  Nodes: " + (innerNodesCounter.get() + leafNodesCounter.get()));
      logInfo("    Inner: " + innerNodesCounter.get());
      logInfo("    Leaf : " + leafNodesCounter.get());


      logInfo(" ");
      final int totalGeometries = geometriesInInnerNodesCounter.get() + geometriesInLeafNodesCounter.get();
      final int duplicates = totalGeometries - _geometries.size();
      logInfo(" Distributed Geometries: " + totalGeometries + "  (duplicates: " + duplicates + " ("
              + GStringUtils.formatPercent(duplicates, totalGeometries) + "))");


      logInfo(" ");
      logInfo("  Geometries in Inners: " + geometriesInInnerNodesCounter.get());
      logInfo("  Geometries per Inner: min=" + minGeometriesCountInInnerNodes.get() + ", max="
              + maxGeometriesCountInInnerNodes.get() + ", average="
              + ((float) geometriesInInnerNodesCounter.get() / innerNodesCounter.get()));

      logInfo(" ");
      logInfo("  Geometries in Leafs: " + geometriesInLeafNodesCounter.get());
      logInfo("  Geometries per Leaf: min=" + minGeometriesCountInLeafNodes.get() + ", max="
              + maxGeometriesCountInLeafNodes.get() + ", average="
              + ((float) geometriesInLeafNodesCounter.get() / leafNodesCounter.get()));

      logInfo("  Average leaf extent: " + totalLeafExtentHolder.get().div(leafNodesCounter.get()));


      logInfo(" ");
      logInfo("  Depth: Max=" + maxDepth.get() + ", Min=" + minDepth.get() + ", Average="
              + ((double) totalDepth.get() / leafNodesCounter.get()));

      logInfo("---------------------------------------------------------------");
   }


   protected abstract String getTreeName();


   @Override
   public boolean logVerbose() {
      return _parameters._verbose;
   }


   public void breadthFirstAcceptVisitor(final IGTBreadFirstVisitor<VectorT, BoundsT, GeometryT> visitor) {
      try {
         visitor.visitOctree(this);

         _root.breadthFirstAcceptVisitor(visitor);
      }
      catch (final IGTBreadFirstVisitor.AbortVisiting e) {
         // do nothing
      }
   }


   public void breadthFirstAcceptVisitor(final IBounds<VectorT, ?> region,
                                         final IGTBreadFirstVisitor<VectorT, BoundsT, GeometryT> visitor) {
      if (!_bounds.touchesBounds(region)) {
         return;
      }

      try {
         visitor.visitOctree(this);

         _root.breadthFirstAcceptVisitor(region, visitor);
      }
      catch (final IGTBreadFirstVisitor.AbortVisiting e) {
         // do nothing
      }
   }


   public void depthFirstAcceptVisitor(final IGTDepthFirstVisitor<VectorT, BoundsT, GeometryT> visitor) {
      try {
         visitor.visitOctree(this);

         _root.depthFirstAcceptVisitor(visitor);

         visitor.finishedOctree(this);
      }
      catch (final IGTBreadFirstVisitor.AbortVisiting e) {
         // do nothing
      }
   }


   public byte getDimensions() {
      return _dimensions;
   }


   public int getLeafNodesCount() {
      return _root.getLeafNodesCount();
   }


   public int getInnerNodesCount() {
      return _root.getInnerNodesCount();
   }


   public Collection<GeometryT> getGeometries() {
      return Collections.unmodifiableCollection(_geometries);
   }


   public GGTInnerNode<VectorT, BoundsT, GeometryT> getRoot() {
      return _root;
   }

   //   public static void main(final String[] args) {
   //      System.out.println("GeometryNTree 0.1");
   //      System.out.println("-----------------\n");
   //
   //
   //      final Collection<IPolygon2D<?>> geometries = new ArrayList<IPolygon2D<?>>();
   //
   //      geometries.add(new GTriangle2D(new GVector2D(0, 0), new GVector2D(1, 0), new GVector2D(0, 1)));
   //      geometries.add(new GQuad2D(new GVector2D(10, 10), new GVector2D(11, 10), new GVector2D(10, 11), new GVector2D(20, 11)));
   //
   //      //final GAxisAlignedRectangle bounds = new GAxisAlignedRectangle(GVector2D.ZERO, GVector2D.X_UP);
   //      final GAxisAlignedRectangle bounds = null;
   //      final GGeometryQuadtree<IPolygon2D<?>> quadtree = new GGeometryQuadtree<IPolygon2D<?>>("test tree", bounds, geometries,
   //               new GGeometryNTreeParameters(true, 10, 10, GGeometryNTreeParameters.BoundsPolicy.MINIMUM));
   //   }


}
