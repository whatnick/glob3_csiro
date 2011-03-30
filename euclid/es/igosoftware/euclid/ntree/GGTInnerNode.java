

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
import es.igosoftware.util.ITransformer;


public class GGTInnerNode<

VectorT extends IVector<VectorT, ?>,

BoundsT extends GAxisAlignedOrthotope<VectorT, ?>,

ElementT

>
         extends
            GGTNode<VectorT, BoundsT, ElementT> {


   private final GGTNode<VectorT, BoundsT, ElementT>[] _children;


   static class GeometriesDistribution<

   VectorT extends IVector<VectorT, ?>,

   ElementT

   >
            extends
               GPair<Collection<ElementT>, Collection<ElementT>> {

      private static final long serialVersionUID = 1L;


      private GeometriesDistribution(final Collection<ElementT> ownElements,
                                     final Collection<ElementT> elementsToDistribute) {
         super(ownElements, elementsToDistribute);
      }


      Collection<ElementT> getOwnGeometries() {
         return _first;
      }


      Collection<ElementT> getGeometriesToDistribute() {
         return _second;
      }
   }


   static final <

   VectorT extends IVector<VectorT, ?>,

   BoundsT extends GAxisAlignedOrthotope<VectorT, ?>,

   ElementT,

   GeometryT extends IBoundedGeometry<VectorT, ? extends IFiniteBounds<VectorT, ?>>

   > GeometriesDistribution<VectorT, ElementT> distributeGeometries(final BoundsT bounds,
                                                                    final Iterable<? extends ElementT> elements,
                                                                    final ITransformer<ElementT, GeometryT> transformer) {

      final GAxisAlignedOrthotope<VectorT, ?>[] childrenBounds = bounds.subdivideAtCenter();

      final ArrayList<ElementT> ownElements = new ArrayList<ElementT>();
      final ArrayList<ElementT> elementsToDistribute = new ArrayList<ElementT>();

      for (final ElementT element : elements) {

         final GeometryT geometry = transformer.transform(element);

         @SuppressWarnings("unchecked")
         final GAxisAlignedOrthotope<VectorT, ?> geometryBounds = (GAxisAlignedOrthotope<VectorT, ?>) geometry.getBounds();
         int geometryInChildrenCounter = 0;
         for (final GAxisAlignedOrthotope<VectorT, ?> childBounds : childrenBounds) {
            if (childBounds.touches(geometryBounds)) {
               geometryInChildrenCounter++;
            }
         }

         if (geometryInChildrenCounter == 0) {
            System.out.println("WARNING >> element " + element + " don't added!!!!!");
         }
         else if (geometryInChildrenCounter > 1) {
            ownElements.add(element);
         }
         else {
            elementsToDistribute.add(element);
         }
      }

      ownElements.trimToSize();
      elementsToDistribute.trimToSize();

      final long elementsCount = GGeometryNTree.countElements(elements);
      if ((ownElements.size() + elementsToDistribute.size()) != elementsCount) {
         throw new RuntimeException("Invalid Distribution: ownElements=" + ownElements.size() + ", elementsToDistribute="
                                    + elementsToDistribute.size() + ", elements=" + elementsCount);
      }

      return new GeometriesDistribution<VectorT, ElementT>(ownElements, elementsToDistribute);
   }


   GGTInnerNode(final GGTInnerNode<VectorT, BoundsT, ElementT> parent,
                final BoundsT bounds,
                final Collection<ElementT> ownElements,
                final Collection<ElementT> elementsToDistribute,
                final ITransformer<ElementT, ? extends IBoundedGeometry<VectorT, ? extends IFiniteBounds<VectorT, ?>>> transformer,
                final int depth,
                final GGeometryNTreeParameters parameters,
                final GProgress progress) {
      super(parent, bounds, ownElements.isEmpty() ? null : ownElements);

      _children = initializeChildren(elementsToDistribute, transformer, depth, parameters, progress);
   }


   private GGTNode<VectorT, BoundsT, ElementT>[] initializeChildren(final Collection<ElementT> elements,
                                                                    final ITransformer<ElementT, ? extends IBoundedGeometry<VectorT, ? extends IFiniteBounds<VectorT, ?>>> transformer,
                                                                    final int depth,
                                                                    final GGeometryNTreeParameters parameters,
                                                                    final GProgress progress) {
      final GAxisAlignedOrthotope<VectorT, ?>[] childrenBounds = _bounds.subdivideAtCenter();

      final int maxChildrenCount = childrenBounds.length;

      final List<ArrayList<ElementT>> elementsByChild = new ArrayList<ArrayList<ElementT>>(maxChildrenCount);
      for (int i = 0; i < maxChildrenCount; i++) {
         elementsByChild.add(new ArrayList<ElementT>());
      }

      for (final ElementT element : elements) {
         final IBoundedGeometry<VectorT, ? extends IFiniteBounds<VectorT, ?>> geometry = transformer.transform(element);

         final GAxisAlignedOrthotope<VectorT, ?> geometryBounds = geometry.getBounds().asAxisAlignedOrthotope();
         int geometryAddedCounter = 0;

         for (int i = 0; i < maxChildrenCount; i++) {
            final GAxisAlignedOrthotope<VectorT, ?> childBounds = childrenBounds[i];
            if (childBounds.touches(geometryBounds)) {
               final ArrayList<ElementT> childGeometries = elementsByChild.get(i);
               childGeometries.add(element);
               geometryAddedCounter++;
            }
         }

         if (geometryAddedCounter == 0) {
            System.out.println("WARNING >> element " + element + " don't added!!!!!");
         }
         else if (geometryAddedCounter > 1) {
            System.out.println("WARNING >> element " + element + " added " + geometryAddedCounter + " times !!!!!");
            progress.incrementSteps(geometryAddedCounter - 1);
         }
      }

      // clear some memory
      // the geometries at this point are splitted into geometriesByChild and it safe to clear the given geometries collection
      elements.clear();


      final GGTNode<VectorT, BoundsT, ElementT>[] result;
      if (parameters._multiThread) {
         result = multiThreadChildrenCreation(depth, parameters, progress, childrenBounds, maxChildrenCount, elementsByChild,
                  transformer);
      }
      else {
         result = singleThreadChildrenCreation(depth, parameters, progress, childrenBounds, maxChildrenCount, elementsByChild,
                  transformer);
      }

      return GCollections.rtrim(result);
   }


   private GGTNode<VectorT, BoundsT, ElementT>[] singleThreadChildrenCreation(final int depth,
                                                                              final GGeometryNTreeParameters parameters,
                                                                              final GProgress progress,
                                                                              final GAxisAlignedOrthotope<VectorT, ?>[] childrenBounds,
                                                                              final int maxChildrenCount,
                                                                              final List<ArrayList<ElementT>> elementsByChild,
                                                                              final ITransformer<ElementT, ? extends IBoundedGeometry<VectorT, ? extends IFiniteBounds<VectorT, ?>>> transformer) {
      @SuppressWarnings({ "cast", "unchecked" })
      final GGTNode<VectorT, BoundsT, ElementT>[] result = (GGTNode<VectorT, BoundsT, ElementT>[]) new GGTNode[maxChildrenCount];

      for (int i = 0; i < maxChildrenCount; i++) {
         final GAxisAlignedOrthotope<VectorT, ?> childBounds = childrenBounds[i];
         final ArrayList<ElementT> childElements = elementsByChild.get(i);
         childElements.trimToSize();

         result[i] = createChildNode(childBounds, childElements, transformer, depth + 1, parameters, progress);
      }

      return result;
   }


   private GGTNode<VectorT, BoundsT, ElementT>[] multiThreadChildrenCreation(final int depth,
                                                                             final GGeometryNTreeParameters parameters,
                                                                             final GProgress progress,
                                                                             final GAxisAlignedOrthotope<VectorT, ?>[] childrenBounds,
                                                                             final int maxChildrenCount,
                                                                             final List<ArrayList<ElementT>> elementsByChild,
                                                                             final ITransformer<ElementT, ? extends IBoundedGeometry<VectorT, ? extends IFiniteBounds<VectorT, ?>>> transformer) {
      final ExecutorService executor = GConcurrent.getDefaultExecutor();

      @SuppressWarnings("unchecked")
      final Future<GGTNode<VectorT, BoundsT, ElementT>>[] futures = (Future<GGTNode<VectorT, BoundsT, ElementT>>[]) new Future<?>[maxChildrenCount];

      for (int i = 0; i < maxChildrenCount; i++) {
         final GAxisAlignedOrthotope<VectorT, ?> childBounds = childrenBounds[i];
         final ArrayList<ElementT> childElements = elementsByChild.get(i);
         childElements.trimToSize();

         futures[i] = executor.submit(new Callable<GGTNode<VectorT, BoundsT, ElementT>>() {
            @Override
            public GGTNode<VectorT, BoundsT, ElementT> call() {
               return createChildNode(childBounds, childElements, transformer, depth + 1, parameters, progress);
            }
         });
      }

      @SuppressWarnings({ "cast", "unchecked" })
      final GGTNode<VectorT, BoundsT, ElementT>[] result = (GGTNode<VectorT, BoundsT, ElementT>[]) new GGTNode[maxChildrenCount];
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
   private GGTNode<VectorT, BoundsT, ElementT> createChildNode(final GAxisAlignedOrthotope<VectorT, ?> bounds,
                                                               final Collection<ElementT> elements,
                                                               final ITransformer<ElementT, ? extends IBoundedGeometry<VectorT, ? extends IFiniteBounds<VectorT, ?>>> transformer,
                                                               final int depth,
                                                               final GGeometryNTreeParameters parameters,
                                                               final GProgress progress) {

      if (elements.isEmpty()) {
         return null;
      }


      if (acceptLeafNodeCreation(bounds, elements, depth, parameters)) {
         progress.stepsDone(elements.size());
         return new GGTLeafNode<VectorT, BoundsT, ElementT>(this, (BoundsT) bounds, elements);
      }


      final GeometriesDistribution<VectorT, ElementT> distribution = distributeGeometries(bounds, elements, transformer);

      return new GGTInnerNode<VectorT, BoundsT, ElementT>(this, (BoundsT) bounds, distribution.getOwnGeometries(),
               distribution.getGeometriesToDistribute(), transformer, depth + 1, parameters, progress);

   }


   @SuppressWarnings("unchecked")
   private boolean acceptLeafNodeCreation(final GAxisAlignedOrthotope<VectorT, ?> bounds,
                                          final Collection<ElementT> elements,
                                          final int depth,
                                          final GGeometryNTreeParameters parameters) {
      final VectorT nodeExtent = bounds._extent;

      // if the extent if too small, force a leaf creation
      for (byte i = 0; i < nodeExtent.dimensions(); i++) {
         if (nodeExtent.get(i) <= 0.00000001) {
            return true;
         }
      }

      return parameters._acceptLeafNodeCreationPolicy.accept(depth, bounds, elements);
   }


   public void breadthFirstAcceptVisitor(final IBounds<VectorT, ?> region,
                                         final IGTBreadFirstVisitor<VectorT, BoundsT, ElementT> visitor)
                                                                                                        throws IGTBreadFirstVisitor.AbortVisiting {

      final LinkedList<GGTNode<VectorT, BoundsT, ElementT>> queue = new LinkedList<GGTNode<VectorT, BoundsT, ElementT>>();
      queue.addLast(this);

      while (!queue.isEmpty()) {
         final GGTNode<VectorT, BoundsT, ElementT> current = queue.removeFirst();

         if ((region != null) && !current.getBounds().touchesBounds(region)) {
            continue;
         }

         if (current instanceof GGTInnerNode) {
            final GGTInnerNode<VectorT, BoundsT, ElementT> currentInner = (GGTInnerNode<VectorT, BoundsT, ElementT>) current;

            visitor.visitInnerNode(currentInner);

            for (final GGTNode<VectorT, BoundsT, ElementT> child : currentInner._children) {
               if (child != null) {
                  queue.addLast(child);
               }
            }
         }
         else if (current instanceof GGTLeafNode) {
            final GGTLeafNode<VectorT, BoundsT, ElementT> currentLeaf = (GGTLeafNode<VectorT, BoundsT, ElementT>) current;
            visitor.visitLeafNode(currentLeaf);
         }
         else {
            throw new IllegalArgumentException();
         }
      }
   }


   public void breadthFirstAcceptVisitor(final IGTBreadFirstVisitor<VectorT, BoundsT, ElementT> visitor)
                                                                                                        throws IGTBreadFirstVisitor.AbortVisiting {

      breadthFirstAcceptVisitor(null, visitor);

      //      final LinkedList<GGTNode<VectorT, BoundsT, ElementT, GeometryT>> queue = new LinkedList<GGTNode<VectorT, BoundsT, ElementT, GeometryT>>();
      //      queue.addLast(this);
      //
      //      while (!queue.isEmpty()) {
      //         final GGTNode<VectorT, BoundsT, ElementT, GeometryT> current = queue.removeFirst();
      //
      //         if (current instanceof GGTInnerNode) {
      //            final GGTInnerNode<VectorT, BoundsT, ElementT, GeometryT> currentInner = (GGTInnerNode<VectorT, BoundsT, ElementT, GeometryT>) current;
      //
      //            visitor.visitInnerNode(currentInner);
      //
      //            for (final GGTNode<VectorT, BoundsT, ElementT, GeometryT> child : currentInner._children) {
      //               if (child != null) {
      //                  queue.addLast(child);
      //               }
      //            }
      //         }
      //         else if (current instanceof GGTLeafNode) {
      //            final GGTLeafNode<VectorT, BoundsT, ElementT, GeometryT> currentLeaf = (GGTLeafNode<VectorT, BoundsT, ElementT, GeometryT>) current;
      //            visitor.visitLeafNode(currentLeaf);
      //         }
      //         else {
      //            throw new IllegalArgumentException();
      //         }
      //      }
   }


   final byte getChildIndex(final GGTNode<VectorT, BoundsT, ElementT> node) {
      for (byte i = 0; i < _children.length; i++) {
         if (node == _children[i]) {
            return i;
         }
      }
      return -1;
   }


   @Override
   public void depthFirstAcceptVisitor(final IGTDepthFirstVisitor<VectorT, BoundsT, ElementT> visitor)
                                                                                                      throws IGTBreadFirstVisitor.AbortVisiting {
      visitor.visitInnerNode(this);

      for (final GGTNode<VectorT, BoundsT, ElementT> child : _children) {
         if (child != null) {
            child.depthFirstAcceptVisitor(visitor);
         }
      }

      visitor.finishedInnerNode(this);

   }


   @Override
   public final int getLeafNodesCount() {
      int counter = 0;
      for (final GGTNode<VectorT, BoundsT, ElementT> child : _children) {
         if (child != null) {
            counter += child.getLeafNodesCount();
         }
      }
      return counter;
   }


   @Override
   public final int getInnerNodesCount() {
      int counter = 0;
      for (final GGTNode<VectorT, BoundsT, ElementT> child : _children) {
         if (child != null) {
            counter += child.getInnerNodesCount();
         }
      }
      return counter + 1;
   }


   @Override
   public final int getAllElementsCount() {
      int result = 0;
      for (final GGTNode<VectorT, BoundsT, ElementT> child : _children) {
         if (child != null) {
            result += child.getAllElementsCount();
         }
      }
      return result + getElementsCount();
   }


   @Override
   public final Collection<ElementT> getAllElements() {
      final ArrayList<ElementT> result = new ArrayList<ElementT>();
      result.addAll(getElements());

      for (final GGTNode<VectorT, BoundsT, ElementT> child : _children) {
         if (child != null) {
            result.addAll(child.getAllElements());
         }
      }

      return Collections.unmodifiableCollection(result);
   }


   public List<GGTNode<VectorT, BoundsT, ElementT>> getChildren() {
      final ArrayList<GGTNode<VectorT, BoundsT, ElementT>> result = new ArrayList<GGTNode<VectorT, BoundsT, ElementT>>(
               _children.length);
      for (final GGTNode<VectorT, BoundsT, ElementT> child : _children) {
         if (child != null) {
            result.add(child);
         }
      }
      return Collections.unmodifiableList(result);
   }


   @Override
   protected void validate() {
      for (final GGTNode<VectorT, BoundsT, ElementT> child : _children) {
         if (child != null) {
            if (child.getParent() != this) {
               System.err.println("INVALID PARENT");
            }
            child.validate();
         }
      }
   }

}
