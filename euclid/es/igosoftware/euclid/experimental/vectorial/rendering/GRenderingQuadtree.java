

package es.igosoftware.euclid.experimental.vectorial.rendering;

import es.igosoftware.euclid.IBoundedGeometry;
import es.igosoftware.euclid.bounding.GAxisAlignedRectangle;
import es.igosoftware.euclid.bounding.IFiniteBounds;
import es.igosoftware.euclid.features.IGlobeFeature;
import es.igosoftware.euclid.ntree.GGeometryNTreeParameters;
import es.igosoftware.euclid.ntree.quadtree.GGeometryQuadtree;
import es.igosoftware.euclid.vector.IVector2;
import es.igosoftware.util.ITransformer;


public class GRenderingQuadtree<

FeatureT extends IGlobeFeature<IVector2<?>, FeatureGeometryT, FeatureBoundsT>,

FeatureGeometryT extends IBoundedGeometry<IVector2<?>, ?, FeatureBoundsT>,

FeatureBoundsT extends IFiniteBounds<IVector2<?>, FeatureBoundsT>

>

         extends
            GGeometryQuadtree<FeatureT, FeatureGeometryT> {


   public GRenderingQuadtree(final String name,
                             final GAxisAlignedRectangle bounds,
                             final Iterable<FeatureT> elements,
                             final ITransformer<FeatureT, FeatureGeometryT> transformer,
                             final GGeometryNTreeParameters parameters) {
      super(name, bounds, elements, transformer, parameters);
   }


}
