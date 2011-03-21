

package es.igosoftware.euclid.ntree;

import es.igosoftware.euclid.IBoundedGeometry;
import es.igosoftware.euclid.bounding.GAxisAlignedOrthotope;
import es.igosoftware.euclid.bounding.IFiniteBounds;
import es.igosoftware.euclid.vector.IVector;


public interface IGTDepthFirstVisitor<

VectorT extends IVector<VectorT, ?, ?>,

BoundsT extends GAxisAlignedOrthotope<VectorT, ?>,

ElementT,

GeometryT extends IBoundedGeometry<VectorT, ?, ? extends IFiniteBounds<VectorT, ?>>

>
         extends
            IGTBreadFirstVisitor<VectorT, BoundsT, ElementT, GeometryT> {

   public void finishedInnerNode(final GGTInnerNode<VectorT, BoundsT, ElementT, GeometryT> inner)
                                                                                                 throws IGTBreadFirstVisitor.AbortVisiting;


   public void finishedOctree(final GGeometryNTree<VectorT, BoundsT, ElementT, GeometryT> octree)
                                                                                                 throws IGTBreadFirstVisitor.AbortVisiting;

}
