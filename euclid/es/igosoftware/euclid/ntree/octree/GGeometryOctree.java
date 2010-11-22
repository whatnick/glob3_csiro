

package es.igosoftware.euclid.ntree.octree;

import java.util.Collection;

import es.igosoftware.euclid.IBoundedGeometry;
import es.igosoftware.euclid.bounding.GAxisAlignedBox;
import es.igosoftware.euclid.bounding.IFiniteBounds;
import es.igosoftware.euclid.ntree.GGeometryNTree;
import es.igosoftware.euclid.ntree.GGeometryNTreeParameters;
import es.igosoftware.euclid.vector.IVector3;


public class GGeometryOctree<GeometryT extends IBoundedGeometry<IVector3<?>, ?, ? extends IFiniteBounds<IVector3<?>, ?>>>
         extends
            GGeometryNTree<IVector3<?>, GAxisAlignedBox, GeometryT> {


   public GGeometryOctree(final String name,
                          final GAxisAlignedBox bounds,
                          final Collection<GeometryT> geometries,
                          final GGeometryNTreeParameters parameters) {
      super(name, bounds, geometries, parameters);
   }


   @Override
   protected String getTreeName() {
      return "Octree";
   }

}
