

package es.igosoftware.euclid.octree.geometry;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import es.igosoftware.euclid.IBoundedGeometry;
import es.igosoftware.euclid.bounding.GAxisAlignedOrthotope;
import es.igosoftware.euclid.bounding.IFiniteBounds;
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
                final GGeometryNTreeParameters parameters,
                final GProgress progress) {
      super(parent, bounds);

      _children = distributeVertices(geometries, parameters, progress);
   }


   private GGTNode<VectorT, BoundsT, GeometryT>[] distributeVertices(final Collection<GeometryT> geometries,
                                                                     final GGeometryNTreeParameters parameters,
                                                                     final GProgress progress) {
      final GAxisAlignedOrthotope<VectorT, ?>[] childrenBounds = _bounds.subdivideAtCenter();

      final int maxChildrenCount = childrenBounds.length;
      @SuppressWarnings({ "cast", "unchecked" })
      final GGTNode<VectorT, BoundsT, GeometryT>[] result = (GGTNode<VectorT, BoundsT, GeometryT>[]) new GGTNode[maxChildrenCount];

      final List<ArrayList<GeometryT>> geometriesByChild = new ArrayList<ArrayList<GeometryT>>(maxChildrenCount);
      for (int i = 0; i < maxChildrenCount; i++) {
         geometriesByChild.add(new ArrayList<GeometryT>());
      }

      for (final GeometryT geometry : geometries) {
         for (int i = 0; i < maxChildrenCount; i++) {
            final GAxisAlignedOrthotope<VectorT, ?> childBounds = childrenBounds[i];
            if (childBounds.touches(geometry.getBounds().asAxisAlignedOrthotope())) {
               final ArrayList<GeometryT> childGeometries = geometriesByChild.get(i);
               childGeometries.add(geometry);
            }
         }
      }

      for (int i = 0; i < maxChildrenCount; i++) {
         final GAxisAlignedOrthotope<VectorT, ?> childBounds = childrenBounds[i];
         final ArrayList<GeometryT> childGeometries = geometriesByChild.get(i);
         childGeometries.trimToSize();

         result[i] = createNode(childBounds, childGeometries, parameters, progress);
      }

      return GCollections.rtrim(result);
   }


   @SuppressWarnings("unchecked")
   private GGTNode<VectorT, BoundsT, GeometryT> createNode(final GAxisAlignedOrthotope<VectorT, ?> bounds,
                                                           final Collection<GeometryT> geometries,
                                                           final GGeometryNTreeParameters parameters,
                                                           final GProgress progress) {
      final int Diego_at_work;

      if (geometries.isEmpty()) {
         return null;
      }

      if ((geometries.size() < parameters._maxGeometries) || (getDepth() > parameters._maxDepth)) {
         progress.stepsDone(geometries.size());
         return new GGTLeafNode<VectorT, BoundsT, GeometryT>(this, (BoundsT) bounds, geometries);
      }

      return new GGTInnerNode<VectorT, BoundsT, GeometryT>(this, (BoundsT) bounds, geometries, parameters, progress);
   }


   public void breadthFirstAcceptVisitor(final GGeometryNTree.Visitor<VectorT, BoundsT, GeometryT> visitor)
                                                                                                           throws GGeometryNTree.Visitor.AbortVisiting {

      final LinkedList<GGTNode<VectorT, BoundsT, GeometryT>> queue = new LinkedList<GGTNode<VectorT, BoundsT, GeometryT>>();
      queue.addLast(this);

      while (!queue.isEmpty()) {
         final GGTNode current = queue.removeFirst();

         if (current instanceof GGTInnerNode) {
            @SuppressWarnings("unchecked")
            final GGTInnerNode<VectorT, BoundsT, GeometryT> currentInner = (GGTInnerNode<VectorT, BoundsT, GeometryT>) current;

            visitor.visitInnerNode(currentInner);

            for (final GGTNode<VectorT, BoundsT, GeometryT> child : currentInner._children) {
               if (child != null) {
                  queue.addLast(child);
               }
            }
         }
         else if (current instanceof GGTLeafNode) {
            @SuppressWarnings("unchecked")
            final GGTLeafNode<VectorT, BoundsT, GeometryT> currentLeaf = (GGTLeafNode<VectorT, BoundsT, GeometryT>) current;
            visitor.visitLeafNode(currentLeaf);
         }
         else {
            throw new IllegalArgumentException();
         }
      }
   }

}
