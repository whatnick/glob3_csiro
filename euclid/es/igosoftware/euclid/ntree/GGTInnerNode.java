

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


public class GGTInnerNode<VectorT extends IVector<VectorT, ?>, ElementT>
         extends
            GGTNode<VectorT, ElementT> {


   private final GGTNode<VectorT, ElementT>[] _children;


   static class GeometriesDistribution<VectorT extends IVector<VectorT, ?>, ElementT>
            extends
               GPair<Collection<GElementGeometryPair<VectorT, ElementT>>, Collection<GElementGeometryPair<VectorT, ElementT>>> {

      private static final long serialVersionUID = 1L;


      private GeometriesDistribution(final Collection<GElementGeometryPair<VectorT, ElementT>> ownElements,
                                     final Collection<GElementGeometryPair<VectorT, ElementT>> elementsToDistribute) {
         super(ownElements, elementsToDistribute);
      }


      Collection<GElementGeometryPair<VectorT, ElementT>> getOwnGeometries() {
         return _first;
      }


      Collection<GElementGeometryPair<VectorT, ElementT>> getGeometriesToDistribute() {
         return _second;
      }
   }


   static final <

   VectorT extends IVector<VectorT, ?>,

   BoundsT extends GAxisAlignedOrthotope<VectorT, ?>,

   ElementT,

   GeometryT extends IBoundedGeometry<VectorT, ? extends IFiniteBounds<VectorT, ?>>

   > GeometriesDistribution<VectorT, ElementT> distributeGeometries(final BoundsT bounds,
                                                                    final Collection<GElementGeometryPair<VectorT, ElementT>> pairs) {

      final GAxisAlignedOrthotope<VectorT, ?>[] childrenBounds = bounds.subdividedAtCenter();

      final ArrayList<GElementGeometryPair<VectorT, ElementT>> ownElements = new ArrayList<GElementGeometryPair<VectorT, ElementT>>();
      final ArrayList<GElementGeometryPair<VectorT, ElementT>> elementsToDistribute = new ArrayList<GElementGeometryPair<VectorT, ElementT>>();


      for (final GElementGeometryPair<VectorT, ElementT> pair : pairs) {
         final GAxisAlignedOrthotope<VectorT, ?> geometryBounds = pair.getBounds();
         int geometryInChildrenCounter = 0;
         for (final GAxisAlignedOrthotope<VectorT, ?> childBounds : childrenBounds) {
            if (childBounds.touches(geometryBounds)) {
               geometryInChildrenCounter++;
            }
         }

         if (geometryInChildrenCounter == 0) {
            System.out.println("WARNING >> element " + pair + " don't added!!!!!");
         }
         else if (geometryInChildrenCounter == 1) {
            elementsToDistribute.add(pair);
         }
         else {
            ownElements.add(pair);
         }
      }

      if ((ownElements.size() + elementsToDistribute.size()) != pairs.size()) {
         throw new RuntimeException("Invalid Distribution: ownElements=" + ownElements.size() + ", elementsToDistribute="
                                    + elementsToDistribute.size() + ", totalGeometries=" + pairs.size());
      }

      ownElements.trimToSize();
      elementsToDistribute.trimToSize();

      return new GeometriesDistribution<VectorT, ElementT>(ownElements, elementsToDistribute);
   }


   GGTInnerNode(final GGTInnerNode<VectorT, ElementT> parent,
                final GAxisAlignedOrthotope<VectorT, ?> bounds,
                final Collection<GElementGeometryPair<VectorT, ElementT>> ownGeometries,
                final Collection<GElementGeometryPair<VectorT, ElementT>> geometriesToDistribute,
                final int depth,
                final GGeometryNTreeParameters parameters,
                final GProgress progress) {
      super(parent, bounds, ownGeometries.isEmpty() ? null : ownGeometries);

      _children = initializeChildren(geometriesToDistribute, depth, parameters, progress);
   }


   private static class Division<VectorT extends IVector<VectorT, ?>, ElementT> {

      private final GAxisAlignedOrthotope<VectorT, ?>                  _bounds;
      private final ArrayList<GElementGeometryPair<VectorT, ElementT>> _elements = new ArrayList<GElementGeometryPair<VectorT, ElementT>>();


      private Division(final GAxisAlignedOrthotope<VectorT, ?> bounds) {
         _bounds = bounds;
      }


      private void addElement(final GElementGeometryPair<VectorT, ElementT> element) {
         _elements.add(element);
      }

   }


   private Division<VectorT, ElementT>[] createDivisionsByBounds(final GAxisAlignedOrthotope<VectorT, ?>[] bounds) {
      @SuppressWarnings({ "unchecked" })
      final Division<VectorT, ElementT>[] result = (Division<VectorT, ElementT>[]) new Division<?, ?>[bounds.length];

      for (int i = 0; i < bounds.length; i++) {
         result[i] = new Division<VectorT, ElementT>(bounds[i]);
      }

      return result;
   }


   private GGTNode<VectorT, ElementT>[] initializeChildren(final Collection<GElementGeometryPair<VectorT, ElementT>> elements,
                                                           final int depth,
                                                           final GGeometryNTreeParameters parameters,
                                                           final GProgress progress) {

      final Division<VectorT, ElementT>[] divisions = createDivisionsByBounds(_bounds.subdividedAtCenter());

      for (final GElementGeometryPair<VectorT, ElementT> pair : elements) {
         final GAxisAlignedOrthotope<VectorT, ?> geometryBounds = pair.getBounds();

         int geometryAddedCounter = 0;
         for (final Division<VectorT, ElementT> division : divisions) {
            if (division._bounds.touches(geometryBounds)) {
               division.addElement(pair);
               geometryAddedCounter++;
            }
         }

         if (geometryAddedCounter == 0) {
            System.out.println("WARNING >> element " + pair + " don't added!!!!!");
         }
         else if (geometryAddedCounter > 1) {
            System.out.println("WARNING >> element " + pair + " added " + geometryAddedCounter + " times !!!!!");
            progress.incrementSteps(geometryAddedCounter - 1);
         }
      }

      // clear some memory: the geometries at this point was split into divisions and it safe to clear the given geometries collection
      elements.clear();


      final GGTNode<VectorT, ElementT>[] result;
      if (parameters._multiThread) {
         result = multiThreadChildrenCreation(depth, parameters, progress, divisions);
      }
      else {
         result = singleThreadChildrenCreation(depth, parameters, progress, divisions);
      }

      return GCollections.rtrim(result);
   }


   private GGTNode<VectorT, ElementT>[] singleThreadChildrenCreation(final int depth,
                                                                     final GGeometryNTreeParameters parameters,
                                                                     final GProgress progress,
                                                                     final Division<VectorT, ElementT>[] divisions) {
      @SuppressWarnings({ "unchecked" })
      final GGTNode<VectorT, ElementT>[] result = (GGTNode<VectorT, ElementT>[]) new GGTNode<?, ?>[divisions.length];

      for (int i = 0; i < divisions.length; i++) {
         final GAxisAlignedOrthotope<VectorT, ?> childBounds = divisions[i]._bounds;
         final ArrayList<GElementGeometryPair<VectorT, ElementT>> childElements = divisions[i]._elements;
         childElements.trimToSize();

         result[i] = createChildNode(childBounds, childElements, depth + 1, parameters, progress);
      }

      return result;
   }


   private GGTNode<VectorT, ElementT>[] multiThreadChildrenCreation(final int depth,
                                                                    final GGeometryNTreeParameters parameters,
                                                                    final GProgress progress,
                                                                    final Division<VectorT, ElementT>[] divisions) {
      final ExecutorService executor = GConcurrent.getDefaultExecutor();

      @SuppressWarnings("unchecked")
      final Future<GGTNode<VectorT, ElementT>>[] futures = (Future<GGTNode<VectorT, ElementT>>[]) new Future<?>[divisions.length];

      for (int i = 0; i < divisions.length; i++) {
         final GAxisAlignedOrthotope<VectorT, ?> childBounds = divisions[i]._bounds;
         final ArrayList<GElementGeometryPair<VectorT, ElementT>> childElements = divisions[i]._elements;
         childElements.trimToSize();

         futures[i] = executor.submit(new Callable<GGTNode<VectorT, ElementT>>() {
            @Override
            public GGTNode<VectorT, ElementT> call() {
               return createChildNode(childBounds, childElements, depth + 1, parameters, progress);
            }
         });
      }

      @SuppressWarnings({ "unchecked" })
      final GGTNode<VectorT, ElementT>[] result = (GGTNode<VectorT, ElementT>[]) new GGTNode<?, ?>[futures.length];
      for (int i = 0; i < futures.length; i++) {
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


   private GGTNode<VectorT, ElementT> createChildNode(final GAxisAlignedOrthotope<VectorT, ?> bounds,
                                                      final Collection<GElementGeometryPair<VectorT, ElementT>> elements,
                                                      final int depth,
                                                      final GGeometryNTreeParameters parameters,
                                                      final GProgress progress) {

      if (elements.isEmpty()) {
         return null;
      }

      if ((depth > 15) || acceptLeafNodeCreation(bounds, elements, depth, parameters)) {
         return createLeafNode(bounds, elements, progress);
      }

      final GeometriesDistribution<VectorT, ElementT> distribution = distributeGeometries(bounds, elements);

      if (distribution.getGeometriesToDistribute().isEmpty()) {
         return createLeafNode(bounds, elements, progress);
      }

      return new GGTInnerNode<VectorT, ElementT>(this, bounds, distribution.getOwnGeometries(),
               distribution.getGeometriesToDistribute(), depth, parameters, progress);

   }


   private GGTNode<VectorT, ElementT> createLeafNode(final GAxisAlignedOrthotope<VectorT, ?> bounds,
                                                     final Collection<GElementGeometryPair<VectorT, ElementT>> elements,
                                                     final GProgress progress) {
      progress.stepsDone(elements.size());
      return new GGTLeafNode<VectorT, ElementT>(this, bounds, elements);
   }


   @SuppressWarnings("unchecked")
   private boolean acceptLeafNodeCreation(final GAxisAlignedOrthotope<VectorT, ?> bounds,
                                          final Collection<GElementGeometryPair<VectorT, ElementT>> elements,
                                          final int depth,
                                          final GGeometryNTreeParameters parameters) {
      // if the bounds extent is too small, force a leaf creation to avoid problems trying to subdivide the bounds
      final VectorT boundsExtent = bounds._extent;
      for (byte i = 0; i < boundsExtent.dimensions(); i++) {
         if (boundsExtent.get(i) <= 0.00000001) {
            return true;
         }
      }

      return parameters._acceptLeafNodeCreationPolicy.acceptLeafNodeCreation(depth, bounds, elements);
   }


   public void breadthFirstAcceptVisitor(final IBounds<VectorT, ?> region,
                                         final IGTBreadFirstVisitor<VectorT, ElementT> visitor)
                                                                                               throws IGTBreadFirstVisitor.AbortVisiting {

      final LinkedList<GGTNode<VectorT, ElementT>> queue = new LinkedList<GGTNode<VectorT, ElementT>>();
      queue.addLast(this);

      while (!queue.isEmpty()) {
         final GGTNode<VectorT, ElementT> current = queue.removeFirst();

         if ((region != null) && !current.getBounds().touchesBounds(region)) {
            continue;
         }

         if (current instanceof GGTInnerNode) {
            final GGTInnerNode<VectorT, ElementT> currentInner = (GGTInnerNode<VectorT, ElementT>) current;

            visitor.visitInnerNode(currentInner);

            for (final GGTNode<VectorT, ElementT> child : currentInner._children) {
               if (child != null) {
                  queue.addLast(child);
               }
            }
         }
         else if (current instanceof GGTLeafNode) {
            final GGTLeafNode<VectorT, ElementT> currentLeaf = (GGTLeafNode<VectorT, ElementT>) current;
            visitor.visitLeafNode(currentLeaf);
         }
         else {
            throw new IllegalArgumentException();
         }
      }
   }


   public void breadthFirstAcceptVisitor(final IGTBreadFirstVisitor<VectorT, ElementT> visitor)
                                                                                               throws IGTBreadFirstVisitor.AbortVisiting {

      breadthFirstAcceptVisitor(null, visitor);

   }


   final byte getChildIndex(final GGTNode<VectorT, ElementT> node) {
      for (byte i = 0; i < _children.length; i++) {
         if (node == _children[i]) {
            return i;
         }
      }
      return -1;
   }


   @Override
   public void depthFirstAcceptVisitor(final IGTDepthFirstVisitor<VectorT, ElementT> visitor)
                                                                                             throws IGTBreadFirstVisitor.AbortVisiting {
      visitor.visitInnerNode(this);

      for (final GGTNode<VectorT, ElementT> child : _children) {
         if (child != null) {
            child.depthFirstAcceptVisitor(visitor);
         }
      }

      visitor.finishedInnerNode(this);

   }


   @Override
   public final int getLeafNodesCount() {
      int counter = 0;
      for (final GGTNode<VectorT, ElementT> child : _children) {
         if (child != null) {
            counter += child.getLeafNodesCount();
         }
      }
      return counter;
   }


   @Override
   public final int getInnerNodesCount() {
      int counter = 0;
      for (final GGTNode<VectorT, ElementT> child : _children) {
         if (child != null) {
            counter += child.getInnerNodesCount();
         }
      }
      return counter + 1;
   }


   @Override
   public final int getAllElementsCount() {
      int result = 0;
      for (final GGTNode<VectorT, ElementT> child : _children) {
         if (child != null) {
            result += child.getAllElementsCount();
         }
      }
      return result + getElementsCount();
   }


   @Override
   public final Collection<GElementGeometryPair<VectorT, ElementT>> getAllElements() {
      final ArrayList<GElementGeometryPair<VectorT, ElementT>> result = new ArrayList<GElementGeometryPair<VectorT, ElementT>>();
      result.addAll(getElements());

      for (final GGTNode<VectorT, ElementT> child : _children) {
         if (child != null) {
            result.addAll(child.getAllElements());
         }
      }

      return Collections.unmodifiableCollection(result);
   }


   public List<GGTNode<VectorT, ElementT>> getChildren() {
      final ArrayList<GGTNode<VectorT, ElementT>> result = new ArrayList<GGTNode<VectorT, ElementT>>(_children.length);
      for (final GGTNode<VectorT, ElementT> child : _children) {
         if (child != null) {
            result.add(child);
         }
      }
      return Collections.unmodifiableList(result);
   }


   @Override
   protected void validate() {
      if (isRoot()) {
         if (_parent != null) {
            System.err.println("The root inner node has a parent");
         }
      }
      else {
         if (_parent == null) {
            System.err.println("A non-root inner node has not a parent");
         }
      }

      for (final GGTNode<VectorT, ElementT> child : _children) {
         if (child == null) {
            continue;
         }

         if (child.getParent() != this) {
            System.err.println("INVALID PARENT");
         }
         child.validate();
      }
   }


   public boolean isRoot() {
      return false;
   }


   @Override
   public String toString() {
      return "GGTInnerNode [id=" + getId() + ", depth=" + getDepth() + ", bounds=" + getBounds() + ", elements="
             + getElementsCount() + "]";
   }
}
