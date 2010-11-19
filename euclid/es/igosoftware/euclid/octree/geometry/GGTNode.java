

package es.igosoftware.euclid.octree.geometry;

import es.igosoftware.euclid.IBoundedGeometry;
import es.igosoftware.euclid.bounding.GAxisAlignedOrthotope;
import es.igosoftware.euclid.bounding.IFiniteBounds;
import es.igosoftware.euclid.vector.IVector;
import es.igosoftware.util.GLoggerObject;


public abstract class GGTNode<

VectorT extends IVector<VectorT, ?>,

BoundsT extends GAxisAlignedOrthotope<VectorT, ?>,

GeometryT extends IBoundedGeometry<VectorT, ?, ? extends IFiniteBounds<VectorT, ?>>

>
         extends
            GLoggerObject {


   protected final GGTInnerNode<VectorT, BoundsT, GeometryT> _parent;
   protected final BoundsT                                   _bounds;


   protected GGTNode(final GGTInnerNode<VectorT, BoundsT, GeometryT> parent,
                     final BoundsT bounds) {
      _parent = parent;
      _bounds = bounds;
   }


   public GGTInnerNode<VectorT, BoundsT, GeometryT> getParent() {
      return _parent;
   }


   public BoundsT getBounds() {
      return _bounds;
   }


   public final int getDepth() {
      if (_parent == null) {
         return 0;
      }
      return _parent.getDepth() + 1;
   }


   public final GGTInnerNode<VectorT, BoundsT, GeometryT> getRoot() {
      if (_parent == null) {
         return (GGTInnerNode<VectorT, BoundsT, GeometryT>) this;
      }
      return _parent.getRoot();
   }


   @Override
   public final boolean logVerbose() {
      return getNTree().logVerbose();
   }


   public GGeometryNTree<VectorT, BoundsT, GeometryT> getNTree() {
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


   public abstract int getGeometriesCount();


   public abstract void depthFirstAcceptVisitor(final IGTDepthFirstVisitor<VectorT, BoundsT, GeometryT> visitor)
                                                                                                                throws IGTBreadFirstVisitor.AbortVisiting;

}
