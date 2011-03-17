

package es.igosoftware.euclid.ntree;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import es.igosoftware.concurrent.GConcurrent;
import es.igosoftware.euclid.IBoundedGeometry;
import es.igosoftware.euclid.bounding.GAxisAlignedOrthotope;
import es.igosoftware.euclid.bounding.IBounds;
import es.igosoftware.euclid.bounding.IFiniteBounds;
import es.igosoftware.euclid.vector.IVector;
import es.igosoftware.util.GCollections;
import es.igosoftware.util.GPair;
import es.igosoftware.util.GProgress;


public class GGTInnerNode<

VectorT extends IVector<VectorT, ?, ?>,

BoundsT extends GAxisAlignedOrthotope<VectorT, ?>,

GeometryT extends IBoundedGeometry<VectorT, ?, ? extends IFiniteBounds<VectorT, ?>>

>
         extends
            GGTNode<VectorT, BoundsT, GeometryT> {


   private final GGTNode<VectorT, BoundsT, GeometryT>[] _children;


   static class GeometriesDistribution<VectorT extends IVector<VectorT, ?, ?>, GeometryT extends IBoundedGeometry<VectorT, ?, ? extends IFiniteBounds<VectorT, ?>>>
            extends
               GPair<Collection<GeometryT>, Collection<GeometryT>> {

      private static final long serialVersionUID = 1L;


      private GeometriesDistribution(final Collection<GeometryT> ownGeometries,
                                     final Collection<GeometryT> geometriesToDistribute) {
         super(ownGeometries, geometriesToDistribute);
      }


      Collection<GeometryT> getOwnGeometries() {
         return _first;
      }


      Collection<GeometryT> getGeometriesToDistribute() {
         return _second;
      }
   }


   static final <

   VectorT extends IVector<VectorT, ?, ?>,

   BoundsT extends GAxisAlignedOrthotope<VectorT, ?>,

   GeometryT extends IBoundedGeometry<VectorT, ?, ? extends IFiniteBounds<VectorT, ?>>

   > GeometriesDistribution<VectorT, GeometryT> distributeGeometries(final BoundsT bounds,
                                                                     final Collection<GeometryT> geometries) {

      final GAxisAlignedOrthotope<VectorT, ?>[] childrenBounds = bounds.subdivideAtCenter();

      final ArrayList<GeometryT> ownGeometries = new ArrayList<GeometryT>();
      final ArrayList<GeometryT> geometriesToDistribute = new ArrayList<GeometryT>();

      for (final GeometryT geometry : geometries) {
         @SuppressWarnings("unchecked")
         final GAxisAlignedOrthotope<VectorT, ?> geometryBounds = (GAxisAlignedOrthotope<VectorT, ?>) geometry.getBounds();
         int geometryInChildrenCounter = 0;
         for (final GAxisAlignedOrthotope<VectorT, ?> childBounds : childrenBounds) {
            if (childBounds.touches(geometryBounds)) {
               geometryInChildrenCounter++;
            }
         }

         if (geometryInChildrenCounter == 0) {
            System.out.println("WARNING >> geometry " + geometry + " don't added!!!!!");
         }
         else if (geometryInChildrenCounter > 1) {
            ownGeometries.add(geometry);
         }
         else {
            geometriesToDistribute.add(geometry);
         }
      }

      ownGeometries.trimToSize();
      geometriesToDistribute.trimToSize();

      if ((ownGeometries.size() + geometriesToDistribute.size()) != geometries.size()) {
         throw new RuntimeException("INVALID DISTRIBUTION");
      }

      return new GeometriesDistribution<VectorT, GeometryT>(ownGeometries, geometriesToDistribute);
   }


   GGTInnerNode(final GGTInnerNode<VectorT, BoundsT, GeometryT> parent,
                final BoundsT bounds,
                final Collection<GeometryT> ownGeometries,
                final Collection<GeometryT> geometriesToDistribute,
                final int depth,
                final GGeometryNTreeParameters parameters,
                final GProgress progress) {
      super(parent, bounds, ownGeometries.isEmpty() ? null : ownGeometries);

      _children = initializeChildren(geometriesToDistribute, depth, parameters, progress);
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

         if (geometryAddedCounter == 0) {
            System.out.println("WARNING >> geometry " + geometry + " don't added!!!!!");
         }
         else if (geometryAddedCounter > 1) {
            System.out.println("WARNING >> geometry " + geometry + " added " + geometryAddedCounter + " times !!!!!");
            progress.incrementSteps(geometryAddedCounter - 1);
         }
      }

      // clear some memory
      // the geometries at this point are splitted into geometriesByChild and it safe to clear the given geometries collection
      geometries.clear();


      final GGTNode<VectorT, BoundsT, GeometryT>[] result;
      if (parameters._multiThread) {
         result = multiThreadChildrenCreation(depth, parameters, progress, childrenBounds, maxChildrenCount, geometriesByChild);
      }
      else {
         result = singleThreadChildrenCreation(depth, parameters, progress, childrenBounds, maxChildrenCount, geometriesByChild);
      }

      return GCollections.rtrim(result);
   }


   private GGTNode<VectorT, BoundsT, GeometryT>[] singleThreadChildrenCreation(final int depth,
                                                                               final GGeometryNTreeParameters parameters,
                                                                               final GProgress progress,
                                                                               final GAxisAlignedOrthotope<VectorT, ?>[] childrenBounds,
                                                                               final int maxChildrenCount,
                                                                               final List<ArrayList<GeometryT>> geometriesByChild) {
      @SuppressWarnings({ "cast", "unchecked" })
      final GGTNode<VectorT, BoundsT, GeometryT>[] result = (GGTNode<VectorT, BoundsT, GeometryT>[]) new GGTNode[maxChildrenCount];

      for (int i = 0; i < maxChildrenCount; i++) {
         final GAxisAlignedOrthotope<VectorT, ?> childBounds = childrenBounds[i];
         final ArrayList<GeometryT> childGeometries = geometriesByChild.get(i);
         childGeometries.trimToSize();

         result[i] = createChildNode(childBounds, childGeometries, depth + 1, parameters, progress);
      }

      return result;
   }


   private GGTNode<VectorT, BoundsT, GeometryT>[] multiThreadChildrenCreation(final int depth,
                                                                              final GGeometryNTreeParameters parameters,
                                                                              final GProgress progress,
                                                                              final GAxisAlignedOrthotope<VectorT, ?>[] childrenBounds,
                                                                              final int maxChildrenCount,
                                                                              final List<ArrayList<GeometryT>> geometriesByChild) {
      final ExecutorService executor = GConcurrent.getDefaultExecutor();

      @SuppressWarnings("unchecked")
      final Future<GGTNode<VectorT, BoundsT, GeometryT>>[] futures = (Future<GGTNode<VectorT, BoundsT, GeometryT>>[]) new Future<?>[maxChildrenCount];

      for (int i = 0; i < maxChildrenCount; i++) {
         final GAxisAlignedOrthotope<VectorT, ?> childBounds = childrenBounds[i];
         final ArrayList<GeometryT> childGeometries = geometriesByChild.get(i);
         childGeometries.trimToSize();

         futures[i] = executor.submit(new Callable<GGTNode<VectorT, BoundsT, GeometryT>>() {
            @Override
            public GGTNode<VectorT, BoundsT, GeometryT> call() {
               return createChildNode(childBounds, childGeometries, depth + 1, parameters, progress);
            }
         });
      }

      @SuppressWarnings({ "cast", "unchecked" })
      final GGTNode<VectorT, BoundsT, GeometryT>[] result = (GGTNode<VectorT, BoundsT, GeometryT>[]) new GGTNode[maxChildrenCount];
      for (int i = 0; i < maxChildrenCount; i++) {
         try {
            result[i] = futures[i].get();
         }
         catch (final InterruptedException e) {
            e.printStackTrace();
         }
         catch (final ExecutionException e) {
            e.printStackTrace();
         }
      }

      return result;
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
         progress.stepsDone(geometries.size());
         return new GGTLeafNode<VectorT, BoundsT, GeometryT>(this, (BoundsT) bounds, geometries);
      }


      final GeometriesDistribution<VectorT, GeometryT> distribution = distributeGeometries(bounds, geometries);

      return new GGTInnerNode<VectorT, BoundsT, GeometryT>(this, (BoundsT) bounds, distribution.getOwnGeometries(),
               distribution.getGeometriesToDistribute(), depth + 1, parameters, progress);

   }


   @SuppressWarnings("unchecked")
   private boolean acceptLeafNodeCreation(final GAxisAlignedOrthotope<VectorT, ?> bounds,
                                          final Collection<GeometryT> geometries,
                                          final int depth,
                                          final GGeometryNTreeParameters parameters) {
      final VectorT nodeExtent = bounds._extent;

      // if the extent if too small, force a leaf creation
      for (byte i = 0; i < nodeExtent.dimensions(); i++) {
         if (nodeExtent.get(i) <= 0.00000001) {
            return true;
         }
      }

      return parameters._acceptLeafNodeCreationPolicy.accept(depth, bounds, geometries);
   }


   public void breadthFirstAcceptVisitor(final IBounds<VectorT, ?> region,
                                         final IGTBreadFirstVisitor<VectorT, BoundsT, GeometryT> visitor)
                                                                                                         throws IGTBreadFirstVisitor.AbortVisiting {

      final LinkedList<GGTNode<VectorT, BoundsT, GeometryT>> queue = new LinkedList<GGTNode<VectorT, BoundsT, GeometryT>>();
      queue.addLast(this);

      while (!queue.isEmpty()) {
         final GGTNode<VectorT, BoundsT, GeometryT> current = queue.removeFirst();

         if ((region != null) && !current.getBounds().touchesBounds(region)) {
            continue;
         }

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


   public void breadthFirstAcceptVisitor(final IGTBreadFirstVisitor<VectorT, BoundsT, GeometryT> visitor)
                                                                                                         throws IGTBreadFirstVisitor.AbortVisiting {

      breadthFirstAcceptVisitor(null, visitor);

      //      final LinkedList<GGTNode<VectorT, BoundsT, GeometryT>> queue = new LinkedList<GGTNode<VectorT, BoundsT, GeometryT>>();
      //      queue.addLast(this);
      //
      //      while (!queue.isEmpty()) {
      //         final GGTNode<VectorT, BoundsT, GeometryT> current = queue.removeFirst();
      //
      //         if (current instanceof GGTInnerNode) {
      //            final GGTInnerNode<VectorT, BoundsT, GeometryT> currentInner = (GGTInnerNode<VectorT, BoundsT, GeometryT>) current;
      //
      //            visitor.visitInnerNode(currentInner);
      //
      //            for (final GGTNode<VectorT, BoundsT, GeometryT> child : currentInner._children) {
      //               if (child != null) {
      //                  queue.addLast(child);
      //               }
      //            }
      //         }
      //         else if (current instanceof GGTLeafNode) {
      //            final GGTLeafNode<VectorT, BoundsT, GeometryT> currentLeaf = (GGTLeafNode<VectorT, BoundsT, GeometryT>) current;
      //            visitor.visitLeafNode(currentLeaf);
      //         }
      //         else {
      //            throw new IllegalArgumentException();
      //         }
      //      }
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
   public void depthFirstAcceptVisitor(final IGTDepthFirstVisitor<VectorT, BoundsT, GeometryT> visitor)
                                                                                                       throws IGTBreadFirstVisitor.AbortVisiting {
      visitor.visitInnerNode(this);

      for (final GGTNode<VectorT, BoundsT, GeometryT> child : _children) {
         if (child != null) {
            child.depthFirstAcceptVisitor(visitor);
         }
      }

      visitor.finishedInnerNode(this);

   }


   @Override
   public final int getLeafNodesCount() {
      int counter = 0;
      for (final GGTNode<VectorT, BoundsT, GeometryT> child : _children) {
         if (child != null) {
            counter += child.getLeafNodesCount();
         }
      }
      return counter;
   }


   @Override
   public final int getInnerNodesCount() {
      int counter = 0;
      for (final GGTNode<VectorT, BoundsT, GeometryT> child : _children) {
         if (child != null) {
            counter += child.getInnerNodesCount();
         }
      }
      return counter + 1;
   }


   @Override
   public final int getAllGeometriesCount() {
      int result = 0;
      for (final GGTNode<VectorT, BoundsT, GeometryT> child : _children) {
         if (child != null) {
            result += child.getAllGeometriesCount();
         }
      }
      return result + getGeometriesCount();
   }


   @Override
   public final Collection<GeometryT> getAllGeometries() {
      final ArrayList<GeometryT> result = new ArrayList<GeometryT>();
      result.addAll(getGeometries());

      for (final GGTNode<VectorT, BoundsT, GeometryT> child : _children) {
         if (child != null) {
            result.addAll(child.getAllGeometries());
         }
      }

      return Collections.unmodifiableCollection(result);
   }


   public List<GGTNode<VectorT, BoundsT, GeometryT>> getChildren() {
      final ArrayList<GGTNode<VectorT, BoundsT, GeometryT>> result = new ArrayList<GGTNode<VectorT, BoundsT, GeometryT>>(
               _children.length);
      for (final GGTNode<VectorT, BoundsT, GeometryT> child : _children) {
         if (child != null) {
            result.add(child);
         }
      }
      return Collections.unmodifiableList(result);
   }


   @Override
   protected void validate() {
      for (final GGTNode<VectorT, BoundsT, GeometryT> child : _children) {
         if (child != null) {
            if (child.getParent() != this) {
               System.err.println("INVALID PARENT");
            }
            child.validate();
         }
      }
   }

}
