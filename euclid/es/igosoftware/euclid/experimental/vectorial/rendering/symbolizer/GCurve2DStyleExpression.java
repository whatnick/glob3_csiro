

package es.igosoftware.euclid.experimental.vectorial.rendering.symbolizer;

import es.igosoftware.euclid.IBoundedGeometry2D;
import es.igosoftware.euclid.IGeometry2D;
import es.igosoftware.euclid.bounding.GAxisAlignedRectangle;
import es.igosoftware.euclid.bounding.IFinite2DBounds;
import es.igosoftware.euclid.colors.IColor;
import es.igosoftware.euclid.experimental.vectorial.rendering.context.IProjectionTool;
import es.igosoftware.euclid.experimental.vectorial.rendering.context.IVectorial2DDrawer;
import es.igosoftware.euclid.experimental.vectorial.rendering.context.IVectorial2DRenderingScaler;
import es.igosoftware.euclid.experimental.vectorial.rendering.styling.GCurve2DStyle;
import es.igosoftware.euclid.experimental.vectorial.rendering.styling.ICurve2DStyle;
import es.igosoftware.euclid.features.IGlobeFeature;
import es.igosoftware.euclid.features.IGlobeFeatureCollection;
import es.igosoftware.euclid.vector.IVector2;
import es.igosoftware.euclid.vector.IVectorI2;
import es.igosoftware.util.GAssert;


public class GCurve2DStyleExpression<GeometryT extends IGeometry2D>
         implements
            ICurve2DStyleExpression<GeometryT> {


   private final IExpression<GeometryT, Float>  _borderWidthExpression;
   private final IExpression<GeometryT, IColor> _colorExpression;
   private final IExpression<GeometryT, Float>  _opacityExpression;


   public GCurve2DStyleExpression(final IExpression<GeometryT, Float> borderWidthExpression,
                                  final IExpression<GeometryT, IColor> colorExpression,
                                  final IExpression<GeometryT, Float> opacityExpression) {
      GAssert.notNull(borderWidthExpression, "borderWidthExpression");
      GAssert.notNull(colorExpression, "colorExpression");
      GAssert.notNull(opacityExpression, "opacityExpression");

      _borderWidthExpression = borderWidthExpression;
      _colorExpression = colorExpression;
      _opacityExpression = opacityExpression;
   }


   @Override
   public double getMaximumSizeInMeters(final IVectorial2DRenderingScaler scaler) {
      return _borderWidthExpression.getMaximumSizeInMeters(scaler);
   }


   @Override
   public void preprocessFeatures(final IGlobeFeatureCollection<IVector2, ? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>> features) {
      _borderWidthExpression.preprocessFeatures(features);
      _colorExpression.preprocessFeatures(features);
      _opacityExpression.preprocessFeatures(features);
   }


   @Override
   public void preRender(final IVectorI2 renderExtent,
                         final IProjectionTool projectionTool,
                         final GAxisAlignedRectangle viewport,
                         final ISymbolizer2D renderingStyle,
                         final IVectorial2DDrawer drawer) {
      _borderWidthExpression.preRender(renderExtent, projectionTool, viewport, renderingStyle, drawer);
      _colorExpression.preRender(renderExtent, projectionTool, viewport, renderingStyle, drawer);
      _opacityExpression.preRender(renderExtent, projectionTool, viewport, renderingStyle, drawer);
   }


   @Override
   public void postRender(final IVectorI2 renderExtent,
                          final IProjectionTool projectionTool,
                          final GAxisAlignedRectangle viewport,
                          final ISymbolizer2D renderingStyle,
                          final IVectorial2DDrawer drawer) {
      _borderWidthExpression.postRender(renderExtent, projectionTool, viewport, renderingStyle, drawer);
      _colorExpression.postRender(renderExtent, projectionTool, viewport, renderingStyle, drawer);
      _opacityExpression.postRender(renderExtent, projectionTool, viewport, renderingStyle, drawer);
   }


   @Override
   public ICurve2DStyle evaluate(final GeometryT geometry,
                                 final IGlobeFeature<IVector2, ? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>> feature,
                                 final IVectorial2DRenderingScaler scaler) {
      final float borderWidth = _borderWidthExpression.evaluate(geometry, feature, scaler);
      final IColor color = _colorExpression.evaluate(geometry, feature, scaler);
      final Float opacity = _opacityExpression.evaluate(geometry, feature, scaler);

      return new GCurve2DStyle(borderWidth, color, opacity.floatValue());
   }


}
