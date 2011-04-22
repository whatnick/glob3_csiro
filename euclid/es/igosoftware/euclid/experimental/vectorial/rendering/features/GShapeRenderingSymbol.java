

package es.igosoftware.euclid.experimental.vectorial.rendering.features;

import java.awt.Color;

import es.igosoftware.euclid.IBoundedGeometry;
import es.igosoftware.euclid.bounding.IFiniteBounds;
import es.igosoftware.euclid.colors.IColor;
import es.igosoftware.euclid.experimental.measurement.GArea;
import es.igosoftware.euclid.experimental.measurement.GLength;
import es.igosoftware.euclid.experimental.measurement.IMeasure;
import es.igosoftware.euclid.experimental.vectorial.rendering.context.IVectorial2DDrawer;
import es.igosoftware.euclid.experimental.vectorial.rendering.context.IVectorial2DRenderingScaler;
import es.igosoftware.euclid.experimental.vectorial.rendering.styling.IRenderingStyle;
import es.igosoftware.euclid.features.IGlobeFeature;
import es.igosoftware.euclid.vector.IVector2;
import es.igosoftware.util.GAWTUtils;
import es.igosoftware.util.GAssert;


public abstract class GShapeRenderingSymbol
         extends
            GRenderingFeatureAbstract<IVector2>
         implements
            IRenderingSymbol {


   protected final IVector2 _position;
   protected final IVector2 _extent;
   protected final float    _borderWidth;


   protected GShapeRenderingSymbol(final IVector2 point,
                                   final IMeasure<GArea> pointSize,
                                   final IMeasure<GLength> pointBorderSize,
                                   final IVectorial2DRenderingScaler scaler) {
      GAssert.notNull(point, "point");
      GAssert.notNull(pointSize, "pointSize");
      GAssert.notNull(pointBorderSize, "pointBorderSize");
      GAssert.notNull(scaler, "scaler");

      _extent = calculateExtent(point, pointSize, pointBorderSize, scaler);
      _borderWidth = calculateBorderWidth(point, pointBorderSize, scaler);

      // calculate position last, so _extent is already calculated and can be used to center the symbol
      _position = calculatePosition(point, scaler);
   }


   protected abstract IVector2 calculateExtent(final IVector2 point,
                                               final IMeasure<GArea> pointSize,
                                               final IMeasure<GLength> pointBorderSize,
                                               final IVectorial2DRenderingScaler scaler);


   private float calculateBorderWidth(final IVector2 point,
                                      final IMeasure<GLength> pointBorderSize,
                                      final IVectorial2DRenderingScaler scaler) {
      final double borderLenghtInMeters = pointBorderSize.getValueInReferenceUnits();
      final IVector2 pointPlusBorderSize = scaler.increment(point, borderLenghtInMeters, 0);
      return (float) scaler.scaleExtent(pointPlusBorderSize.sub(point)).x();
   }


   private IVector2 calculatePosition(final IVector2 point,
                                      final IVectorial2DRenderingScaler scaler) {
      final IVector2 scaledPoint = scaler.scaleAndTranslatePoint(point);
      return scaledPoint.sub(_extent.div(2));
   }


   @Override
   public final boolean isBiggerThan(final double lodMinSize) {
      return ((_extent.x() * _extent.y()) > lodMinSize);
   }


   @Override
   protected final void rawDraw(final IVector2 point,
                                final IGlobeFeature<IVector2, ? extends IBoundedGeometry<IVector2, ? extends IFiniteBounds<IVector2, ?>>> feature,
                                final IRenderingStyle renderingStyle,
                                final IVectorial2DRenderingScaler scaler,
                                final IVectorial2DDrawer drawer) {

      final IColor pointColor = renderingStyle.getPointColor(point, feature, scaler);
      final IColor pointBorderColor = renderingStyle.getPointBorderColor(point, feature, scaler);
      final float pointOpacity = renderingStyle.getPointOpacity(point, feature, scaler);

      rawDraw(pointColor.asAWTColor(pointOpacity), pointBorderColor.asAWTColor(pointOpacity), scaler, drawer);
   }


   protected abstract void rawDraw(final Color fillColor,
                                   final Color borderColor,
                                   final IVectorial2DRenderingScaler scaler,
                                   final IVectorial2DDrawer drawer);


   private Color getLODIgnoreColor(final IVector2 point,
                                   final IGlobeFeature<IVector2, ? extends IBoundedGeometry<IVector2, ? extends IFiniteBounds<IVector2, ?>>> feature,
                                   final IRenderingStyle renderingStyle,
                                   final IVectorial2DRenderingScaler scaler) {
      if (renderingStyle.isDebugRendering()) {
         return renderingStyle.getLODColor().asAWTColor();
      }

      final IColor pointColor = renderingStyle.getPointColor(point, feature, scaler);
      final float pointOpacity = renderingStyle.getPointOpacity(point, feature, scaler);

      if (_borderWidth <= 0) {
         return pointColor.asAWTColor(pointOpacity);
      }

      final IColor pointBorderColor = renderingStyle.getPointBorderColor(point, feature, scaler);
      return GAWTUtils.mix(pointColor.asAWTColor(pointOpacity), pointBorderColor.asAWTColor(pointOpacity));
   }


   @Override
   protected final void renderLODIgnore(final IVector2 point,
                                        final IGlobeFeature<IVector2, ? extends IBoundedGeometry<IVector2, ? extends IFiniteBounds<IVector2, ?>>> feature,
                                        final IRenderingStyle renderingStyle,
                                        final IVectorial2DRenderingScaler scaler,
                                        final IVectorial2DDrawer drawer) {
      final Color color = getLODIgnoreColor(point, feature, renderingStyle, scaler);

      drawer.fillRect(_position.x(), _position.y(), _extent.x(), _extent.y(), color);
   }

}
