

package es.igosoftware.euclid.ntree;

import java.util.Collection;
import java.util.Collections;

import es.igosoftware.euclid.bounding.GAxisAlignedOrthotope;
import es.igosoftware.euclid.vector.IVector;
import es.igosoftware.util.GLoggerObject;


public abstract class GGTNode<VectorT extends IVector<VectorT, ?>, ElementT>
         extends
            GLoggerObject {


   protected final GGTInnerNode<VectorT, ElementT>                     _parent;
   protected final GAxisAlignedOrthotope<VectorT, ?>                   _bounds;
   protected final Collection<GElementGeometryPair<VectorT, ElementT>> _elements;


   protected GGTNode(final GGTInnerNode<VectorT, ElementT> parent,
                     final GAxisAlignedOrthotope<VectorT, ?> bounds,
                     final Collection<GElementGeometryPair<VectorT, ElementT>> elements) {
      _parent = parent;
      _bounds = bounds;
      _elements = elements;
   }


   public final GGTInnerNode<VectorT, ElementT> getParent() {
      return _parent;
   }


   public final GAxisAlignedOrthotope<VectorT, ?> getBounds() {
      return _bounds;
   }


   public final int getDepth() {
      if (_parent == null) {
         return 0;
      }
      return _parent.getDepth() + 1;
   }


   public final GGTInnerNode<VectorT, ElementT> getRoot() {
      if (_parent == null) {
         return (GGTInnerNode<VectorT, ElementT>) this;
      }
      return _parent.getRoot();
   }


   @Override
   public final boolean logVerbose() {
      return getNTree().logVerbose();
   }


   public GGeometryNTree<VectorT, ElementT> getNTree() {
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


   public abstract void depthFirstAcceptVisitor(final IGTDepthFirstVisitor<VectorT, ElementT> visitor)
                                                                                                      throws IGTBreadFirstVisitor.AbortVisiting;


   public abstract int getLeafNodesCount();


   public abstract int getInnerNodesCount();


   public final Collection<GElementGeometryPair<VectorT, ElementT>> getElements() {
      if (_elements == null) {
         return Collections.emptyList();
      }

      return Collections.unmodifiableCollection(_elements);
   }


   public final int getElementsCount() {
      return (_elements == null) ? 0 : _elements.size();
   }


   public abstract Collection<GElementGeometryPair<VectorT, ElementT>> getAllElements();


   public abstract int getAllElementsCount();


   protected abstract void validate();


}
