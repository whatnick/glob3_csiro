

package es.igosoftware.euclid.octree.geometry;

import java.util.Collection;

import es.igosoftware.euclid.IBoundedGeometry;
import es.igosoftware.euclid.bounding.GAxisAlignedOrthotope;
import es.igosoftware.euclid.bounding.IFiniteBounds;
import es.igosoftware.euclid.vector.IVector;


public class GGTLeafNode<

VectorT extends IVector<VectorT, ?>,

BoundsT extends GAxisAlignedOrthotope<VectorT, ?>,

GeometryT extends IBoundedGeometry<VectorT, ?, ? extends IFiniteBounds<VectorT, ?>>

>
         extends
            GGTNode<VectorT, BoundsT, GeometryT> {


   protected GGTLeafNode(final GGTInnerNode<VectorT, BoundsT, GeometryT> parent,
                         final BoundsT bounds,
                         final Collection<GeometryT> geometries) {
      super(parent, bounds, geometries);
   }


   @Override
   public void depthFirstAcceptVisitor(final IGTDepthFirstVisitor<VectorT, BoundsT, GeometryT> visitor)
                                                                                                       throws IGTBreadFirstVisitor.AbortVisiting {
      visitor.visitLeafNode(this);
   }


   @Override
   public int getLeafNodesCount() {
      return 1;
   }


   @Override
   public int getInnerNodesCount() {
      return 0;
   }


   @Override
   public final Collection<? extends GeometryT> getAllGeometries() {
      return getGeometries();
   }


   @Override
   public final int getAllGeometriesCount() {
      return getGeometriesCount();
   }
}
