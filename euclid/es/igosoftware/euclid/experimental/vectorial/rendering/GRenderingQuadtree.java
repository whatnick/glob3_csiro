

package es.igosoftware.euclid.experimental.vectorial.rendering;

import es.igosoftware.euclid.IBoundedGeometry;
import es.igosoftware.euclid.bounding.GAxisAlignedRectangle;
import es.igosoftware.euclid.features.IGlobeFeature;
import es.igosoftware.euclid.ntree.GGeometryNTreeParameters;
import es.igosoftware.euclid.ntree.quadtree.GGeometryQuadtree;
import es.igosoftware.euclid.vector.IVector2;
import es.igosoftware.util.ITransformer;


public class GRenderingQuadtree
         extends
            GGeometryQuadtree<IGlobeFeature<IVector2<?>, GAxisAlignedRectangle>, IBoundedGeometry<IVector2<?>, ?, GAxisAlignedRectangle>> {

   public GRenderingQuadtree(final String name,
                             final GAxisAlignedRectangle bounds,
                             final Iterable<IGlobeFeature<IVector2<?>, GAxisAlignedRectangle>> elements,
                             final ITransformer<IGlobeFeature<IVector2<?>, GAxisAlignedRectangle>, IBoundedGeometry<IVector2<?>, ?, GAxisAlignedRectangle>> transformer,
                             final GGeometryNTreeParameters parameters) {
      super(name, bounds, elements, transformer, parameters);
   }

}
