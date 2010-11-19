

package es.igosoftware.euclid.octree.geometry.quadtree;

import java.util.Collection;

import es.igosoftware.euclid.IBoundedGeometry;
import es.igosoftware.euclid.bounding.GAxisAlignedRectangle;
import es.igosoftware.euclid.bounding.IFiniteBounds;
import es.igosoftware.euclid.octree.geometry.GGeometryNTree;
import es.igosoftware.euclid.octree.geometry.GGeometryNTreeParameters;
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
