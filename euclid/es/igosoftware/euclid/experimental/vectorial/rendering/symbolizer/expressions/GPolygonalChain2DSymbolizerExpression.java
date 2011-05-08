

package es.igosoftware.euclid.experimental.vectorial.rendering.symbolizer.expressions;

import java.util.Collection;
import java.util.Collections;

import es.igosoftware.euclid.IBoundedGeometry2D;
import es.igosoftware.euclid.bounding.GAxisAlignedRectangle;
import es.igosoftware.euclid.bounding.IFinite2DBounds;
import es.igosoftware.euclid.experimental.vectorial.rendering.context.IProjectionTool;
import es.igosoftware.euclid.experimental.vectorial.rendering.context.IVectorial2DDrawer;
import es.igosoftware.euclid.experimental.vectorial.rendering.context.IVectorial2DRenderingScaler;
import es.igosoftware.euclid.experimental.vectorial.rendering.styling.ICurve2DStyle;
import es.igosoftware.euclid.experimental.vectorial.rendering.symbolizer.ISymbolizer2D;
import es.igosoftware.euclid.experimental.vectorial.rendering.symbols.GPolygonalChain2DSymbol;
import es.igosoftware.euclid.experimental.vectorial.rendering.symbols.GSymbol2D;
import es.igosoftware.euclid.features.IGlobeFeature;
import es.igosoftware.euclid.features.IGlobeFeatureCollection;
import es.igosoftware.euclid.shape.IPolygonalChain2D;
import es.igosoftware.euclid.vector.IVector2;
import es.igosoftware.euclid.vector.IVectorI2;
import es.igosoftware.util.GAssert;


public class GPolygonalChain2DSymbolizerExpression
         implements
            IGeometry2DSymbolizerExpression<IPolygonalChain2D> {


   private final IExpression<IPolygonalChain2D, ICurve2DStyle> _curveStyleExpression;


   public GPolygonalChain2DSymbolizerExpression(final IExpression<IPolygonalChain2D, ICurve2DStyle> curveStyleExpression) {
      GAssert.notNull(curveStyleExpression, "curveStyleExpression");

      _curveStyleExpression = curveStyleExpression;
   }


   @Override
   public double getMaximumSizeInMeters(final IVectorial2DRenderingScaler scaler) {
      return _curveStyleExpression.getMaximumSizeInMeters(scaler);
   }


   @Override
   public void preprocessFeatures(final IGlobeFeatureCollection<IVector2, ? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>> features) {
      _curveStyleExpression.preprocessFeatures(features);
   }


   @Override
   public void preRender(final IVectorI2 renderExtent,
                         final IProjectionTool projectionTool,
                         final GAxisAlignedRectangle viewport,
                         final ISymbolizer2D renderingStyle,
                         final IVectorial2DDrawer drawer) {
      _curveStyleExpression.preRender(renderExtent, projectionTool, viewport, renderingStyle, drawer);
   }


   @Override
   public Collection<? extends GSymbol2D<? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>>> evaluate(final IPolygonalChain2D polygonalChain,
                                                                                                               final IGlobeFeature<IVector2, ? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>> feature,
                                                                                                               final IVectorial2DRenderingScaler scaler) {
      final IPolygonalChain2D scaledPolygonalChain = polygonalChain.transform(scaler);

      final ICurve2DStyle curveStyle = _curveStyleExpression.evaluate(polygonalChain, feature, scaler);

      return Collections.singleton(new GPolygonalChain2DSymbol(scaledPolygonalChain, null, curveStyle, 10));
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
