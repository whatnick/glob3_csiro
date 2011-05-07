

package es.igosoftware.euclid.experimental.vectorial.rendering.symbolizer;

import java.util.Collection;
import java.util.Collections;

import es.igosoftware.euclid.IBoundedGeometry2D;
import es.igosoftware.euclid.bounding.GAxisAlignedRectangle;
import es.igosoftware.euclid.bounding.IFinite2DBounds;
import es.igosoftware.euclid.experimental.vectorial.rendering.context.IProjectionTool;
import es.igosoftware.euclid.experimental.vectorial.rendering.context.IVectorial2DDrawer;
import es.igosoftware.euclid.experimental.vectorial.rendering.context.IVectorial2DRenderingScaler;
import es.igosoftware.euclid.experimental.vectorial.rendering.styling.ICurve2DStyle;
import es.igosoftware.euclid.experimental.vectorial.rendering.styling.ISurface2DStyle;
import es.igosoftware.euclid.experimental.vectorial.rendering.symbols.GPolygon2DSymbol;
import es.igosoftware.euclid.experimental.vectorial.rendering.symbols.GSymbol2D;
import es.igosoftware.euclid.features.IGlobeFeature;
import es.igosoftware.euclid.features.IGlobeFeatureCollection;
import es.igosoftware.euclid.shape.IPolygon2D;
import es.igosoftware.euclid.vector.IVector2;
import es.igosoftware.euclid.vector.IVectorI2;
import es.igosoftware.util.GAssert;


public class GPolygon2DSymbolizerExpression
         implements
            IGeometry2DSymbolizerExpression<IPolygon2D> {


   private final ICurve2DStyleExpression<IPolygon2D>   _curveStyleExpression;
   private final ISurface2DStyleExpression<IPolygon2D> _surfaceStyleExpression;


   public GPolygon2DSymbolizerExpression(final ICurve2DStyleExpression<IPolygon2D> curveStyleExpression,
                                         final ISurface2DStyleExpression<IPolygon2D> surfaceStyleExpression) {
      GAssert.notNull(curveStyleExpression, "curveStyleExpression");
      GAssert.notNull(surfaceStyleExpression, "surfaceStyleExpression");

      _curveStyleExpression = curveStyleExpression;
      _surfaceStyleExpression = surfaceStyleExpression;
   }


   @Override
   public double getMaximumSizeInMeters(final IVectorial2DRenderingScaler scaler) {
      return Math.max(_curveStyleExpression.getMaximumSizeInMeters(scaler),
               _surfaceStyleExpression.getMaximumSizeInMeters(scaler));
   }


   @Override
   public void preprocessFeatures(final IGlobeFeatureCollection<IVector2, ? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>> features) {
      _curveStyleExpression.preprocessFeatures(features);
      _surfaceStyleExpression.preprocessFeatures(features);
   }


   @Override
   public void preRender(final IVectorI2 renderExtent,
                         final IProjectionTool projectionTool,
                         final GAxisAlignedRectangle viewport,
                         final ISymbolizer2D renderingStyle,
                         final IVectorial2DDrawer drawer) {
      _curveStyleExpression.preRender(renderExtent, projectionTool, viewport, renderingStyle, drawer);
      _surfaceStyleExpression.preRender(renderExtent, projectionTool, viewport, renderingStyle, drawer);
   }


   @Override
   public Collection<? extends GSymbol2D<? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>>> evaluate(final IPolygon2D polygon,
                                                                                                               final IGlobeFeature<IVector2, ? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>> feature,
                                                                                                               final IVectorial2DRenderingScaler scaler) {
      final IPolygon2D scaledPolygon = polygon.transform(scaler);
      final ICurve2DStyle curveStyle = _curveStyleExpression.evaluate(polygon, feature, scaler);
      final ISurface2DStyle surfaceStyle = _surfaceStyleExpression.evaluate(polygon, feature, scaler);

      return Collections.singleton(new GPolygon2DSymbol(scaledPolygon, null, surfaceStyle, curveStyle, 10));
   }


   @Override
   public void postRender(final IVectorI2 renderExtent,
                          final IProjectionTool projectionTool,
                          final GAxisAlignedRectangle viewport,
                          final ISymbolizer2D renderingStyle,
                          final IVectorial2DDrawer drawer) {
      _curveStyleExpression.postRender(renderExtent, projectionTool, viewport, renderingStyle, drawer);
   }


}
