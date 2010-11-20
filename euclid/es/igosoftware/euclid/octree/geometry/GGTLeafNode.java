

package es.igosoftware.euclid.octree.geometry;

import java.util.Collection;
import java.util.Collections;

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


   private final Collection<GeometryT> _geometries;


   protected GGTLeafNode(final GGTInnerNode<VectorT, BoundsT, GeometryT> parent,
                         final BoundsT bounds,
                         final Collection<GeometryT> geometries) {
      super(parent, bounds);

      _geometries = geometries;
   }


   public Collection<GeometryT> getGeometries() {
      return Collections.unmodifiableCollection(_geometries);
   }


   @Override
   public int getGeometriesCount() {
      return _geometries.size();
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
}
