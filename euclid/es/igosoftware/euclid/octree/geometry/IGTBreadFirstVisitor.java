

package es.igosoftware.euclid.octree.geometry;

import es.igosoftware.euclid.IBoundedGeometry;
import es.igosoftware.euclid.bounding.GAxisAlignedOrthotope;
import es.igosoftware.euclid.bounding.IFiniteBounds;
import es.igosoftware.euclid.vector.IVector;


public interface IGTBreadFirstVisitor<

VectorT extends IVector<VectorT, ?>,

BoundsT extends GAxisAlignedOrthotope<VectorT, ?>,

GeometryT extends IBoundedGeometry<VectorT, ?, ? extends IFiniteBounds<VectorT, ?>>

> {

   public static class AbortVisiting
            extends
               Exception {
      private static final long serialVersionUID = 1L;
   }


   public void visitOctree(final GGeometryNTree<VectorT, BoundsT, GeometryT> octree) throws IGTBreadFirstVisitor.AbortVisiting;


   public void visitInnerNode(final GGTInnerNode<VectorT, BoundsT, GeometryT> inner) throws IGTBreadFirstVisitor.AbortVisiting;


   public void visitLeafNode(final GGTLeafNode<VectorT, BoundsT, GeometryT> leaf) throws IGTBreadFirstVisitor.AbortVisiting;

}
