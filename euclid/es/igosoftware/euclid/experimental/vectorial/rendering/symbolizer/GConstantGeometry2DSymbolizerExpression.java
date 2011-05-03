

package es.igosoftware.euclid.experimental.vectorial.rendering.symbolizer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import es.igosoftware.euclid.IBoundedGeometry2D;
import es.igosoftware.euclid.IGeometry2D;
import es.igosoftware.euclid.bounding.GAxisAlignedRectangle;
import es.igosoftware.euclid.bounding.IFinite2DBounds;
import es.igosoftware.euclid.experimental.vectorial.rendering.context.IProjectionTool;
import es.igosoftware.euclid.experimental.vectorial.rendering.context.IVectorial2DDrawer;
import es.igosoftware.euclid.experimental.vectorial.rendering.context.IVectorial2DRenderingScaler;
import es.igosoftware.euclid.experimental.vectorial.rendering.symbols.GSymbol2D;
import es.igosoftware.euclid.features.IGlobeFeature;
import es.igosoftware.euclid.features.IGlobeFeatureCollection;
import es.igosoftware.euclid.vector.IVector2;
import es.igosoftware.euclid.vector.IVectorI2;


public class GConstantGeometry2DSymbolizerExpression<GeometryT extends IGeometry2D>
         implements
            IGeometry2DSymbolizerExpression<GeometryT> {


   private final Collection<? extends GSymbol2D<? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>>> _values;


   public GConstantGeometry2DSymbolizerExpression(final Collection<? extends GSymbol2D<? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>>> values) {
      _values = ((values == null) || values.isEmpty())
                                                      ? null
                                                      : new ArrayList<GSymbol2D<? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>>>(
                                                               values);
   }


   public GConstantGeometry2DSymbolizerExpression(final GSymbol2D<? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>>... values) {
      _values = (values.length == 0) ? null : Arrays.asList(values);
   }


   @Override
   public double getMaximumSizeInMeters(final IVectorial2DRenderingScaler scaler) {
      return 0;
   }


   @Override
   public void preprocessFeatures(final IGlobeFeatureCollection<IVector2, ? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>> features) {

   }


   @Override
   public void preRender(final IVectorI2 renderExtent,
                         final IProjectionTool projectionTool,
                         final GAxisAlignedRectangle viewport,
                         final ISymbolizer2D renderingStyle,
                         final IVectorial2DDrawer drawer) {
   }


   @Override
   public Collection<? extends GSymbol2D<? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>>> evaluate(final GeometryT geometry,
                                                                                                               final IGlobeFeature<IVector2, ? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>> feature,
                                                                                                               final IVectorial2DRenderingScaler scaler) {
      return _values;
   }


   @Override
   public void postRender(final IVectorI2 renderExtent,
                          final IProjectionTool projectionTool,
                          final GAxisAlignedRectangle viewport,
                          final ISymbolizer2D renderingStyle,
                          final IVectorial2DDrawer drawer) {
   }

}
