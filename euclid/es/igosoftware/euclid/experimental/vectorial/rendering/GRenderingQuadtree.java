

package es.igosoftware.euclid.experimental.vectorial.rendering;

import es.igosoftware.euclid.IBoundedGeometry;
import es.igosoftware.euclid.bounding.IFiniteBounds;
import es.igosoftware.euclid.features.IGlobeFeature;
import es.igosoftware.euclid.ntree.GGeometryNTreeParameters;
import es.igosoftware.euclid.ntree.quadtree.GGeometryQuadtree;
import es.igosoftware.euclid.vector.IVector2;
import es.igosoftware.util.ITransformer;


public class GRenderingQuadtree<

FeatureT extends IGlobeFeature<IVector2, ? extends IBoundedGeometry<IVector2, ? extends IFiniteBounds<IVector2, ?>>>

>
         extends
            GGeometryQuadtree<FeatureT> {


   public GRenderingQuadtree(final String name,
                             final Iterable<? extends FeatureT> elements,
                             final GGeometryNTreeParameters parameters) {
      super(name, null, elements, new ITransformer<FeatureT, IBoundedGeometry<IVector2, ? extends IFiniteBounds<IVector2, ?>>>() {
         @Override
         public IBoundedGeometry<IVector2, ? extends IFiniteBounds<IVector2, ?>> transform(final FeatureT feature) {
            return feature.getDefaultGeometry();
         }
      }, parameters);
   }


}
