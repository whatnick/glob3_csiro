

package es.igosoftware.euclid.experimental.vectorial.rendering;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import es.igosoftware.euclid.IBoundedGeometry;
import es.igosoftware.euclid.bounding.IFiniteBounds;
import es.igosoftware.euclid.features.IGlobeFeature;
import es.igosoftware.euclid.multigeometry.GMultiGeometry2D;
import es.igosoftware.euclid.ntree.GGeometryNTreeParameters;
import es.igosoftware.euclid.ntree.quadtree.GGeometryQuadtree;
import es.igosoftware.euclid.vector.IVector2;
import es.igosoftware.util.ITransformer;


public class GRenderingQuadtree<FeatureT extends IGlobeFeature<IVector2, ? extends IBoundedGeometry<IVector2, ? extends IFiniteBounds<IVector2, ?>>>>
         extends
            GGeometryQuadtree<FeatureT> {


   public GRenderingQuadtree(final String name,
                             final Iterable<? extends FeatureT> elements,
                             final GGeometryNTreeParameters parameters) {
      super(name, null, elements,
            new ITransformer<FeatureT, Collection<? extends IBoundedGeometry<IVector2, ? extends IFiniteBounds<IVector2, ?>>>>() {
               @Override
               public Collection<? extends IBoundedGeometry<IVector2, ? extends IFiniteBounds<IVector2, ?>>> transform(final FeatureT feature) {
                  final IBoundedGeometry<IVector2, ? extends IFiniteBounds<IVector2, ?>> geometry = feature.getDefaultGeometry();
                  if (geometry instanceof GMultiGeometry2D) {
                     @SuppressWarnings("unchecked")
                     final GMultiGeometry2D<IBoundedGeometry<IVector2, ? extends IFiniteBounds<IVector2, ?>>> multigeometry = (GMultiGeometry2D<IBoundedGeometry<IVector2, ? extends IFiniteBounds<IVector2, ?>>>) geometry;

                     final List<IBoundedGeometry<IVector2, ? extends IFiniteBounds<IVector2, ?>>> children = multigeometry.getChildren();
                     return children;
                  }
                  return Collections.singleton(geometry);
               }
            }, parameters);
   }

}
