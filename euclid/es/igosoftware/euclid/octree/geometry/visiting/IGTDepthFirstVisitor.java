

package es.igosoftware.euclid.octree.geometry.visiting;

import es.igosoftware.euclid.IBoundedGeometry;
import es.igosoftware.euclid.bounding.GAxisAlignedOrthotope;
import es.igosoftware.euclid.bounding.IFiniteBounds;
import es.igosoftware.euclid.octree.geometry.GGTInnerNode;
import es.igosoftware.euclid.octree.geometry.GGeometryNTree;
import es.igosoftware.euclid.vector.IVector;


public interface IGTDepthFirstVisitor<

VectorT extends IVector<VectorT, ?>,

BoundsT extends GAxisAlignedOrthotope<VectorT, ?>,

GeometryT extends IBoundedGeometry<VectorT, ?, ? extends IFiniteBounds<VectorT, ?>>

>
         extends
            IGTBreadFirstVisitor<VectorT, BoundsT, GeometryT> {

   public void finishedInnerNode(final GGTInnerNode<VectorT, BoundsT, GeometryT> inner) throws IGTBreadFirstVisitor.AbortVisiting;


   public void finishedOctree(final GGeometryNTree<VectorT, BoundsT, GeometryT> octree) throws IGTBreadFirstVisitor.AbortVisiting;

}
