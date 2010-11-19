

package es.igosoftware.euclid.octree.geometry;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import es.igosoftware.euclid.IBoundedGeometry;
import es.igosoftware.euclid.bounding.GAxisAlignedOrthotope;
import es.igosoftware.euclid.bounding.IFiniteBounds;
import es.igosoftware.euclid.octree.geometry.GGeometryNTree.IDepthFirstVisitor;
import es.igosoftware.euclid.vector.IVector;
import es.igosoftware.util.GCollections;
import es.igosoftware.util.GProgress;


public class GGTInnerNode<

VectorT extends IVector<VectorT, ?>,

BoundsT extends GAxisAlignedOrthotope<VectorT, ?>,

GeometryT extends IBoundedGeometry<VectorT, ?, ? extends IFiniteBounds<VectorT, ?>>

>
         extends
            GGTNode<VectorT, BoundsT, GeometryT> {


   private final GGTNode<VectorT, BoundsT, GeometryT>[] _children;


   GGTInnerNode(final GGTInnerNode<VectorT, BoundsT, GeometryT> parent,
                final BoundsT bounds,
                final Collection<GeometryT> geometries,
                final int depth,
                final GGeometryNTreeParameters parameters,
                final GProgress progress) {
      super(parent, bounds);

      _children = initializeChildren(geometries, depth, parameters, progress);
   }


   private GGTNode<VectorT, BoundsT, GeometryT>[] initializeChildren(final Collection<GeometryT> geometries,
                                                                     final int depth,
                                                                     final GGeometryNTreeParameters parameters,
                                                                     final GProgress progress) {
      final GAxisAlignedOrthotope<VectorT, ?>[] childrenBounds = _bounds.subdivideAtCenter();

      final int maxChildrenCount = childrenBounds.length;

      final List<ArrayList<GeometryT>> geometriesByChild = new ArrayList<ArrayList<GeometryT>>(maxChildrenCount);
      for (int i = 0; i < maxChildrenCount; i++) {
         geometriesByChild.add(new ArrayList<GeometryT>());
      }

      for (final GeometryT geometry : geometries) {
         final GAxisAlignedOrthotope<VectorT, ?> geometryBounds = geometry.getBounds().asAxisAlignedOrthotope();
         int geometryAddedCounter = 0;

         for (int i = 0; i < maxChildrenCount; i++) {
            final GAxisAlignedOrthotope<VectorT, ?> childBounds = childrenBounds[i];
            if (childBounds.touches(geometryBounds)) {
               final ArrayList<GeometryT> childGeometries = geometriesByChild.get(i);
               childGeometries.add(geometry);
               geometryAddedCounter++;
            }
         }

         //         if (geometryAddedCounter != 1) {
         //            System.out.println(">> geometry " + geometry + " added " + geometryAddedCounter + " times");
         //         }
         if (geometryAddedCounter == 0) {
            System.out.println("WARNING >> geometry " + geometry + " don't added!!!!!");
         }
         if (geometryAddedCounter > 1) {
            progress.incrementSteps(geometryAddedCounter - 1);
         }
      }

      // clear some memory
      // the geometries at this point are splitted into geometriesByChild and it safe to clear the given geometries collection
      geometries.clear();

      @SuppressWarnings({ "cast", "unchecked" })
      final GGTNode<VectorT, BoundsT, GeometryT>[] result = (GGTNode<VectorT, BoundsT, GeometryT>[]) new GGTNode[maxChildrenCount];

      for (int i = 0; i < maxChildrenCount; i++) {
         final GAxisAlignedOrthotope<VectorT, ?> childBounds = childrenBounds[i];
         final ArrayList<GeometryT> childGeometries = geometriesByChild.get(i);
         childGeometries.trimToSize();

         result[i] = createChildNode(childBounds, childGeometries, depth + 1, parameters, progress);
      }

      return GCollections.rtrim(result);
   }


   @SuppressWarnings("unchecked")
   private GGTNode<VectorT, BoundsT, GeometryT> createChildNode(final GAxisAlignedOrthotope<VectorT, ?> bounds,
                                                                final Collection<GeometryT> geometries,
                                                                final int depth,
                                                                final GGeometryNTreeParameters parameters,
                                                                final GProgress progress) {

      if (geometries.isEmpty()) {
         return null;
      }


      if (acceptLeafNodeCreation(bounds, geometries, depth, parameters)) {
         return createLeafNode(bounds, geometries, progress);
      }


      final GGTInnerNode<VectorT, BoundsT, GeometryT> innerNode = new GGTInnerNode<VectorT, BoundsT, GeometryT>(this,
               (BoundsT) bounds, geometries, depth, parameters, progress);

      //      boolean anyChildHasSameGeometries = false;
      //      for (final GGTNode<VectorT, BoundsT, GeometryT> child : innerNode._children) {
      //         if (child != null) {
      //            if (child.getGeometriesCount() == geometries.size()) {
      //               anyChildHasSameGeometries = true;
      //            }
      //         }
      //      }
      //      if (anyChildHasSameGeometries) {
      //         return createLeafNode(bounds, geometries, progress);
      //      }

      return innerNode;
   }


   @SuppressWarnings("unchecked")
   private GGTNode<VectorT, BoundsT, GeometryT> createLeafNode(final GAxisAlignedOrthotope<VectorT, ?> bounds,
                                                               final Collection<GeometryT> geometries,
                                                               final GProgress progress) {
      progress.stepsDone(geometries.size());
      return new GGTLeafNode<VectorT, BoundsT, GeometryT>(this, (BoundsT) bounds, geometries);
   }


   @SuppressWarnings("unchecked")
   private boolean acceptLeafNodeCreation(final GAxisAlignedOrthotope<VectorT, ?> bounds,
                                          final Collection<GeometryT> geometries,
                                          final int depth,
                                          final GGeometryNTreeParameters parameters) {
      final VectorT nodeExtent = bounds._extent;

      // if the extent if too small, force a leaf creation
      for (byte i = 0; i < nodeExtent.dimensions(); i++) {
         if (nodeExtent.get(i) <= 0.000001) {
            return true;
         }
      }

      return parameters._acceptLeafNodeCreationPolicy.accept(depth, bounds, geometries);
   }


   public void breadthFirstAcceptVisitor(final GGeometryNTree.IVisitor<VectorT, BoundsT, GeometryT> visitor)
                                                                                                            throws GGeometryNTree.IVisitor.AbortVisiting {

      final LinkedList<GGTNode<VectorT, BoundsT, GeometryT>> queue = new LinkedList<GGTNode<VectorT, BoundsT, GeometryT>>();
      queue.addLast(this);

      while (!queue.isEmpty()) {
         final GGTNode<VectorT, BoundsT, GeometryT> current = queue.removeFirst();

         if (current instanceof GGTInnerNode) {
            final GGTInnerNode<VectorT, BoundsT, GeometryT> currentInner = (GGTInnerNode<VectorT, BoundsT, GeometryT>) current;

            visitor.visitInnerNode(currentInner);

            for (final GGTNode<VectorT, BoundsT, GeometryT> child : currentInner._children) {
               if (child != null) {
                  queue.addLast(child);
               }
            }
         }
         else if (current instanceof GGTLeafNode) {
            final GGTLeafNode<VectorT, BoundsT, GeometryT> currentLeaf = (GGTLeafNode<VectorT, BoundsT, GeometryT>) current;
            visitor.visitLeafNode(currentLeaf);
         }
         else {
            throw new IllegalArgumentException();
         }
      }
   }


   @Override
   public int getGeometriesCount() {
      int result = 0;
      for (final GGTNode<VectorT, BoundsT, GeometryT> child : _children) {
         if (child != null) {
            result += child.getGeometriesCount();
         }
      }
      return result;
   }


   final byte getChildIndex(final GGTNode<VectorT, BoundsT, GeometryT> node) {
      for (byte i = 0; i < _children.length; i++) {
         if (node == _children[i]) {
            return i;
         }
      }
      return -1;
   }


   @Override
   public void depthFirstAcceptVisitor(final IDepthFirstVisitor<VectorT, BoundsT, GeometryT> visitor)
                                                                                                     throws GGeometryNTree.IVisitor.AbortVisiting {
      visitor.visitInnerNode(this);

      for (final GGTNode<VectorT, BoundsT, GeometryT> child : _children) {
         if (child != null) {
            child.depthFirstAcceptVisitor(visitor);
         }
      }

      visitor.finishedInnerNode(this);

   }

}
