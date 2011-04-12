

package es.igosoftware.euclid.ntree;

import java.util.Collection;

import es.igosoftware.euclid.IBoundedGeometry;
import es.igosoftware.euclid.bounding.GAxisAlignedOrthotope;
import es.igosoftware.euclid.bounding.IBounds;
import es.igosoftware.euclid.bounding.IFiniteBounds;
import es.igosoftware.euclid.features.IGlobeFeatureCollection;
import es.igosoftware.euclid.ntree.GGTInnerNode.GeometriesDistribution;
import es.igosoftware.euclid.shape.GShape;
import es.igosoftware.euclid.vector.GVectorUtils;
import es.igosoftware.euclid.vector.IVector;
import es.igosoftware.util.GHolder;
import es.igosoftware.util.GLoggerObject;
import es.igosoftware.util.GLongHolder;
import es.igosoftware.util.GMath;
import es.igosoftware.util.GProgress;
import es.igosoftware.util.GStringUtils;
import es.igosoftware.util.ITransformer;


public abstract class GGeometryNTree<VectorT extends IVector<VectorT, ?>, ElementT>
         extends
            GLoggerObject {


   private final String                            _name;
   private final Iterable<? extends ElementT>      _elements;
   private final long                              _elementsCount;

   private final GGeometryNTreeParameters          _parameters;

   private final byte                              _dimensions;
   private final GAxisAlignedOrthotope<VectorT, ?> _geometriesBounds;
   private final GAxisAlignedOrthotope<VectorT, ?> _bounds;

   private GGTInnerNode<VectorT, ElementT>         _root;


   protected GGeometryNTree(final String name,
                            final GAxisAlignedOrthotope<VectorT, ?> bounds,
                            final Iterable<? extends ElementT> elements,
                            final ITransformer<ElementT, ? extends IBoundedGeometry<VectorT, ? extends IFiniteBounds<VectorT, ?>>> transformer,
                            final GGeometryNTreeParameters parameters) {
      _name = name;
      _elements = elements;
      _parameters = parameters;

      final String nameMsg = (_name == null) ? "" : "\"" + _name + "\" ";
      logInfo("Creating " + getTreeName() + " " + nameMsg);

      _elementsCount = countElements(elements);

      final GProgress progress = new GProgress(_elementsCount) {
         @Override
         public void informProgress(final double percent,
                                    final long elapsed,
                                    final long estimatedMsToFinish) {
            if (_parameters._verbose) {
               logInfo("  Creating " + getTreeName() + " " + nameMsg + progressString(percent, elapsed, estimatedMsToFinish));
            }
         }
      };

      _geometriesBounds = GShape.getBounds(elements, transformer);
      _dimensions = _geometriesBounds._extent.dimensions();

      _bounds = initializeBounds(bounds);


      final GeometriesDistribution<VectorT, ElementT> distribution = GGTInnerNode.distributeGeometries(_bounds, elements,
               transformer);

      _root = new GGTInnerNode<VectorT, ElementT>(null, _bounds, distribution.getOwnGeometries(),
               distribution.getGeometriesToDistribute(), transformer, 0, parameters, progress) {
         @Override
         public GGeometryNTree<VectorT, ElementT> getNTree() {
            return GGeometryNTree.this;
         }
      };

      validate();

      if (_parameters._verbose) {
         showStatistics();
      }
   }


   static long countElements(final Iterable<?> elements) {
      if (elements instanceof IGlobeFeatureCollection) {
         return ((IGlobeFeatureCollection) elements).size();
      }
      else if (elements instanceof Collection) {
         return ((Collection) elements).size();
      }
      else {
         long counter = 0;
         for (@SuppressWarnings("unused")
         final Object element : elements) {
            counter++;
         }
         return counter;
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
         throw new IllegalArgumentException("The given bounds is not big enough to hold all the elements");
      }

      return givenBounds;
   }


   private void showStatistics() {
      logInfo("---------------------------------------------------------------");

      if (_name != null) {
         logInfo(" " + getTreeName() + " \"" + _name + "\":");
      }

      logInfo(" ");
      logInfo("  Elements Bounds: " + _geometriesBounds);
      logInfo("  Elements Extent: " + _geometriesBounds._extent);

      logInfo(" ");
      logInfo("  Bounds: " + _bounds);
      logInfo("  Extent: " + _bounds._extent);

      final GLongHolder innerNodesCounter = new GLongHolder(0);
      final GLongHolder leafNodesCounter = new GLongHolder(0);
      final GLongHolder elementsInLeafNodesCounter = new GLongHolder(0);
      final GLongHolder maxElementsCountInLeafNodes = new GLongHolder(0);
      final GLongHolder minElementsCountInLeafNodes = new GLongHolder(Integer.MAX_VALUE);

      final GLongHolder elementsInInnerNodesCounter = new GLongHolder(0);
      final GLongHolder maxElementsCountInInnerNodes = new GLongHolder(0);
      final GLongHolder minElementsCountInInnerNodes = new GLongHolder(Integer.MAX_VALUE);

      final GLongHolder totalDepth = new GLongHolder(0);
      final GLongHolder maxDepth = new GLongHolder(0);
      final GLongHolder minDepth = new GLongHolder(Integer.MAX_VALUE);

      final GHolder<VectorT> totalLeafExtentHolder = new GHolder<VectorT>(null);

      breadthFirstAcceptVisitor(new IGTBreadFirstVisitor<VectorT, ElementT>() {

         @Override
         public void visitOctree(final GGeometryNTree<VectorT, ElementT> octree) {
         }


         @Override
         public void visitInnerNode(final GGTInnerNode<VectorT, ElementT> inner) {
            innerNodesCounter.increment();

            final int elementsCount = inner.getElementsCount();
            elementsInInnerNodesCounter.increment(elementsCount);

            if (elementsCount > maxElementsCountInInnerNodes.get()) {
               maxElementsCountInInnerNodes.set(elementsCount);
            }
            if (elementsCount < minElementsCountInInnerNodes.get()) {
               minElementsCountInInnerNodes.set(elementsCount);
            }
         }


         @Override
         public void visitLeafNode(final GGTLeafNode<VectorT, ElementT> leaf) {
            leafNodesCounter.increment();

            final int elementsCount = leaf.getElementsCount();
            elementsInLeafNodesCounter.increment(elementsCount);

            if (elementsCount > maxElementsCountInLeafNodes.get()) {
               maxElementsCountInLeafNodes.set(elementsCount);
            }
            if (elementsCount < minElementsCountInLeafNodes.get()) {
               minElementsCountInLeafNodes.set(elementsCount);
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
      final long totalNodes = innerNodesCounter.get() + leafNodesCounter.get();
      logInfo("  Nodes: " + totalNodes);
      logInfo("    Inner: " + innerNodesCounter.get() + " (" + GStringUtils.formatPercent(innerNodesCounter.get(), totalNodes)
              + ")");
      logInfo("    Leaf : " + leafNodesCounter.get() + " (" + GStringUtils.formatPercent(leafNodesCounter.get(), totalNodes)
              + ")");


      logInfo(" ");
      final long totalElements = elementsInInnerNodesCounter.get() + elementsInLeafNodesCounter.get();
      final long duplicates = totalElements - _elementsCount;
      logInfo(" Distributed Elements: " + totalElements + "  (duplicates: " + duplicates + " ("
              + GStringUtils.formatPercent(duplicates, totalElements) + "))");


      logInfo(" ");
      logInfo("  Elements in Inners: " + elementsInInnerNodesCounter.get() + " ("
              + GStringUtils.formatPercent(elementsInInnerNodesCounter.get(), totalElements) + ")");
      logInfo("  Elements per Inner: min=" + minElementsCountInInnerNodes.get() + ", max=" + maxElementsCountInInnerNodes.get()
              + ", average=" + ((float) elementsInInnerNodesCounter.get() / innerNodesCounter.get()));

      logInfo(" ");
      logInfo("  Elements in Leafs: " + elementsInLeafNodesCounter.get() + " ("
              + GStringUtils.formatPercent(elementsInLeafNodesCounter.get(), totalElements) + ")");
      logInfo("  Elements per Leaf: min=" + minElementsCountInLeafNodes.get() + ", max=" + maxElementsCountInLeafNodes.get()
              + ", average=" + ((float) elementsInLeafNodesCounter.get() / leafNodesCounter.get()));

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


   public void breadthFirstAcceptVisitor(final IGTBreadFirstVisitor<VectorT, ElementT> visitor) {
      try {
         visitor.visitOctree(this);

         _root.breadthFirstAcceptVisitor(visitor);
      }
      catch (final IGTBreadFirstVisitor.AbortVisiting e) {
         // do nothing
      }
   }


   public void breadthFirstAcceptVisitor(final IBounds<VectorT, ?> region,
                                         final IGTBreadFirstVisitor<VectorT, ElementT> visitor) {
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


   public void depthFirstAcceptVisitor(final IGTDepthFirstVisitor<VectorT, ElementT> visitor) {
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


   public Iterable<? extends ElementT> getElements() {
      return _elements;
   }


   public GGTInnerNode<VectorT, ElementT> getRoot() {
      return _root;
   }

   //   public static void main(final String[] args) {
   //      System.out.println("GeometryNTree 0.1");
   //      System.out.println("-----------------\n");
   //
   //
   //      final Collection<IPolygon2D> elements = new ArrayList<IPolygon2D>();
   //
   //      elements.add(new GTriangle2D(new GVector2D(0, 0), new GVector2D(1, 0), new GVector2D(0, 1)));
   //      elements.add(new GQuad2D(new GVector2D(10, 10), new GVector2D(11, 10), new GVector2D(10, 11), new GVector2D(20, 11)));
   //
   //      //final GAxisAlignedRectangle bounds = new GAxisAlignedRectangle(GVector2D.ZERO, GVector2D.X_UP);
   //      final GAxisAlignedRectangle bounds = null;
   //      final GGeometryQuadtree<IPolygon2D> quadtree = new GGeometryQuadtree<IPolygon2D>("test tree", bounds, elements,
   //               new GGeometryNTreeParameters(true, 10, 10, GGeometryNTreeParameters.BoundsPolicy.MINIMUM));
   //   }


}
