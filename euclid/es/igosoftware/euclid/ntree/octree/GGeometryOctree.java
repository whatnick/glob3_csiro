

package es.igosoftware.euclid.ntree.octree;

import es.igosoftware.euclid.IBoundedGeometry;
import es.igosoftware.euclid.bounding.GAxisAlignedBox;
import es.igosoftware.euclid.bounding.IFiniteBounds;
import es.igosoftware.euclid.ntree.GGeometryNTree;
import es.igosoftware.euclid.ntree.GGeometryNTreeParameters;
import es.igosoftware.euclid.vector.IVector3;
import es.igosoftware.util.ITransformer;


public class GGeometryOctree<

ElementT,

GeometryT extends IBoundedGeometry<IVector3<?>, ?, ? extends IFiniteBounds<IVector3<?>, ?>>

>
         extends
            GGeometryNTree<IVector3<?>, GAxisAlignedBox, ElementT, GeometryT> {


   public GGeometryOctree(final String name,
                          final GAxisAlignedBox bounds,
                          final Iterable<ElementT> elements,
                          final ITransformer<ElementT, GeometryT> transformer,
                          final GGeometryNTreeParameters parameters) {
      super(name, bounds, elements, transformer, parameters);
   }


   @Override
   protected String getTreeName() {
      return "Octree";
   }

}
