

package es.igosoftware.euclid.octree.geometry;

import java.util.Collection;

import es.igosoftware.euclid.IBoundedGeometry;
import es.igosoftware.euclid.bounding.GAxisAlignedOrthotope;
import es.igosoftware.euclid.bounding.IFiniteBounds;
import es.igosoftware.euclid.shape.GShape;
import es.igosoftware.euclid.vector.IVector;
import es.igosoftware.util.GIntHolder;
import es.igosoftware.util.GLoggerObject;
import es.igosoftware.util.GProgress;


public abstract class GGeometryNTree<

VectorT extends IVector<VectorT, ?>,

BoundsT extends GAxisAlignedOrthotope<VectorT, ?>,

GeometryT extends IBoundedGeometry<VectorT, ?, ? extends IFiniteBounds<VectorT, ?>>

>
         extends
            GLoggerObject {


   public static interface Visitor<

   VectorT extends IVector<VectorT, ?>,

   BoundsT extends GAxisAlignedOrthotope<VectorT, ?>,

   GeometryT extends IBoundedGeometry<VectorT, ?, ? extends IFiniteBounds<VectorT, ?>>

   > {

      public static class AbortVisiting
               extends
                  Exception {
         private static final long serialVersionUID = 1L;
      }


      public void visitOctree(final GGeometryNTree<VectorT, BoundsT, GeometryT> octree) throws Visitor.AbortVisiting;


      public void visitInnerNode(final GGTInnerNode<VectorT, BoundsT, GeometryT> inner) throws Visitor.AbortVisiting;


      public void visitLeafNode(final GGTLeafNode<VectorT, BoundsT, GeometryT> leaf) throws Visitor.AbortVisiting;

   }


   private final String                              _name;
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


      _root = new GGTInnerNode<VectorT, BoundsT, GeometryT>(null, _bounds, geometries, parameters, progress) {
         @Override
         public GGeometryNTree<VectorT, BoundsT, GeometryT> getNTree() {
            return GGeometryNTree.this;
         }
      };


      if (_parameters._verbose) {
         showStatistics();
      }
   }


   private GAxisAlignedOrthotope<VectorT, ?> initializeBounds(final GAxisAlignedOrthotope<VectorT, ?> bounds) {
      if (bounds != null) {
         if (_geometriesBounds.isFullInside(bounds)) {
            return bounds;
         }
         throw new IllegalArgumentException("The given bounds is not big enough to hold all the vertices");
      }

      if ((_parameters._boundsPolicy == GGeometryNTreeParameters.BoundsPolicy.REGULAR)
          || (_parameters._boundsPolicy == GGeometryNTreeParameters.BoundsPolicy.REGULAR_AND_CENTERED)) {

         final VectorT extent = _geometriesBounds._extent;

         double biggestExtension = Double.NEGATIVE_INFINITY;
         for (byte i = 0; i < _dimensions; i++) {
            final double ext = extent.get(i);
            if (ext > biggestExtension) {
               biggestExtension = ext;
            }
         }

         final VectorT newUpper = _geometriesBounds._lower.add(biggestExtension);
         final GAxisAlignedOrthotope<VectorT, ?> regularBox = GAxisAlignedOrthotope.create(_geometriesBounds._lower, newUpper);

         if (_parameters._boundsPolicy == GGeometryNTreeParameters.BoundsPolicy.REGULAR_AND_CENTERED) {
            final VectorT delta = regularBox.getCenter().sub(_geometriesBounds.getCenter());
            final GAxisAlignedOrthotope<VectorT, ?> regularAndCenteredBox = regularBox.translatedBy(delta.negated());
            return regularAndCenteredBox;
         }

         return regularBox;
      }


      return _geometriesBounds;
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
      final GIntHolder geometrisInleafNodesCounter = new GIntHolder(0);

      final GIntHolder totalDepth = new GIntHolder(0);
      final GIntHolder maxDepth = new GIntHolder(0);
      final GIntHolder minDepth = new GIntHolder(Integer.MAX_VALUE);

      breadthFirstAcceptVisitor(new Visitor<VectorT, BoundsT, GeometryT>() {

         @Override
         public void visitOctree(final GGeometryNTree<VectorT, BoundsT, GeometryT> octree) {
         }


         @Override
         public void visitInnerNode(final GGTInnerNode<VectorT, BoundsT, GeometryT> inner) {
            innerNodesCounter.increment();
         }


         @Override
         public void visitLeafNode(final GGTLeafNode<VectorT, BoundsT, GeometryT> leaf) {
            leafNodesCounter.increment();
            geometrisInleafNodesCounter.increment(leaf.getGeometriesCount());

            final int depth = leaf.getDepth();
            totalDepth.increment(depth);
            if (depth > maxDepth.get()) {
               maxDepth.set(depth);
            }
            if (depth < minDepth.get()) {
               minDepth.set(depth);
            }
         }
      });

      logInfo(" ");
      logInfo("  Nodes: " + (innerNodesCounter.get() + leafNodesCounter.get()));
      logInfo("    Inner: " + innerNodesCounter.get());
      logInfo("    Leaf : " + leafNodesCounter.get());

      logInfo(" ");
      logInfo("  Average Geometries per Leaf : " + ((float) geometrisInleafNodesCounter.get() / leafNodesCounter.get()));

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


   public void breadthFirstAcceptVisitor(final Visitor<VectorT, BoundsT, GeometryT> visitor) {
      try {
         visitor.visitOctree(this);

         _root.breadthFirstAcceptVisitor(visitor);
      }
      catch (final Visitor.AbortVisiting e) {
         // do nothing
      }
   }


   public byte getDimensions() {
      return _dimensions;
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
   //               new GGeometryNTreeParameters(true, 10, 10, GGeometryNTreeParameters.BoundsPolicy.REGULAR_AND_CENTERED));
   //   }


}