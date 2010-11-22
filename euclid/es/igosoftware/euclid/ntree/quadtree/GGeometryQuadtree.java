

package es.igosoftware.euclid.ntree.quadtree;

import java.util.Collection;

import es.igosoftware.euclid.IBoundedGeometry;
import es.igosoftware.euclid.bounding.GAxisAlignedRectangle;
import es.igosoftware.euclid.bounding.IFiniteBounds;
import es.igosoftware.euclid.ntree.GGeometryNTree;
import es.igosoftware.euclid.ntree.GGeometryNTreeParameters;
import es.igosoftware.euclid.vector.IVector2;


public class GGeometryQuadtree<GeometryT extends IBoundedGeometry<IVector2<?>, ?, ? extends IFiniteBounds<IVector2<?>, ?>>>
         extends
            GGeometryNTree<IVector2<?>, GAxisAlignedRectangle, GeometryT> {


   public GGeometryQuadtree(final String name,
                            final GAxisAlignedRectangle bounds,
                            final Collection<GeometryT> geometries,
                            final GGeometryNTreeParameters parameters) {
      super(name, bounds, geometries, parameters);
   }


   @Override
   protected String getTreeName() {
      return "Quadtree";
   }

}
