

package es.igosoftware.euclid.ntree;

import java.util.Collection;
import java.util.Collections;

import es.igosoftware.euclid.bounding.GAxisAlignedOrthotope;
import es.igosoftware.euclid.vector.IVector;
import es.igosoftware.util.GLoggerObject;


public abstract class GGTNode<

VectorT extends IVector<VectorT, ?>,

BoundsT extends GAxisAlignedOrthotope<VectorT, ?>,

ElementT

>
         extends
            GLoggerObject {


   protected final GGTInnerNode<VectorT, BoundsT, ElementT> _parent;
   protected final BoundsT                                  _bounds;
   protected final Collection<ElementT>                     _elements;


   protected GGTNode(final GGTInnerNode<VectorT, BoundsT, ElementT> parent,
                     final BoundsT bounds,
                     final Collection<ElementT> elements) {
      _parent = parent;
      _bounds = bounds;
      _elements = elements;
   }


   public final GGTInnerNode<VectorT, BoundsT, ElementT> getParent() {
      return _parent;
   }


   public final BoundsT getBounds() {
      return _bounds;
   }


   public final int getDepth() {
      if (_parent == null) {
         return 0;
      }
      return _parent.getDepth() + 1;
   }


   public final GGTInnerNode<VectorT, BoundsT, ElementT> getRoot() {
      if (_parent == null) {
         return (GGTInnerNode<VectorT, BoundsT, ElementT>) this;
      }
      return _parent.getRoot();
   }


   @Override
   public final boolean logVerbose() {
      return getNTree().logVerbose();
   }


   public GGeometryNTree<VectorT, BoundsT, ElementT> getNTree() {
      return _parent.getNTree();
   }


   public final String getId() {
      if (_parent == null) {
         return "";
      }

      final byte myId = _parent.getChildIndex(this);

      final String parentId = _parent.getId();
      if ((parentId == null) || parentId.isEmpty()) {
         return Byte.toString(myId);
      }

      return parentId + "-" + myId;
   }


   public abstract void depthFirstAcceptVisitor(final IGTDepthFirstVisitor<VectorT, BoundsT, ElementT> visitor)
                                                                                                               throws IGTBreadFirstVisitor.AbortVisiting;


   public abstract int getLeafNodesCount();


   public abstract int getInnerNodesCount();


   public final Collection<ElementT> getElements() {
      if (_elements == null) {
         return Collections.emptyList();
      }

      return Collections.unmodifiableCollection(_elements);
   }


   public final int getElementsCount() {
      return (_elements == null) ? 0 : _elements.size();
   }


   public abstract Collection<? extends ElementT> getAllElements();


   public abstract int getAllElementsCount();


   protected abstract void validate();


}
