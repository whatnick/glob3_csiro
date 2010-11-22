

package es.igosoftware.euclid.octree.geometry;

import java.util.Collection;
import java.util.Collections;

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
   protected final Collection<GeometryT>                     _geometries;


   protected GGTNode(final GGTInnerNode<VectorT, BoundsT, GeometryT> parent,
                     final BoundsT bounds,
                     final Collection<GeometryT> geometries) {
      _parent = parent;
      _bounds = bounds;
      _geometries = geometries;
   }


   public final GGTInnerNode<VectorT, BoundsT, GeometryT> getParent() {
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


   public abstract void depthFirstAcceptVisitor(final IGTDepthFirstVisitor<VectorT, BoundsT, GeometryT> visitor)
                                                                                                                throws IGTBreadFirstVisitor.AbortVisiting;


   public abstract int getLeafNodesCount();


   public abstract int getInnerNodesCount();


   public final Collection<GeometryT> getGeometries() {
      if (_geometries == null) {
         return Collections.emptyList();
      }

      return Collections.unmodifiableCollection(_geometries);
   }


   public final int getGeometriesCount() {
      return (_geometries == null) ? 0 : _geometries.size();
   }


   public abstract Collection<? extends GeometryT> getAllGeometries();


   public abstract int getAllGeometriesCount();


   protected abstract void validate();


}
