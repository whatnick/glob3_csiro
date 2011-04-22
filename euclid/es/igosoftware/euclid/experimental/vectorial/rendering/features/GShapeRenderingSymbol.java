

package es.igosoftware.euclid.experimental.vectorial.rendering.features;

import java.awt.Color;

import es.igosoftware.euclid.IBoundedGeometry;
import es.igosoftware.euclid.bounding.IFiniteBounds;
import es.igosoftware.euclid.colors.IColor;
import es.igosoftware.euclid.experimental.measurement.GArea;
import es.igosoftware.euclid.experimental.measurement.GLength;
import es.igosoftware.euclid.experimental.measurement.IMeasure;
import es.igosoftware.euclid.experimental.vectorial.rendering.IVectorial2DRenderingContext;
import es.igosoftware.euclid.experimental.vectorial.rendering.styling.IRenderingStyle;
import es.igosoftware.euclid.features.IGlobeFeature;
import es.igosoftware.euclid.vector.IVector2;
import es.igosoftware.util.GAWTUtils;
import es.igosoftware.util.GAssert;


public abstract class GShapeRenderingSymbol
         extends
            GRenderingSymbol {


   protected final IVector2 _position;
   protected final IVector2 _extent;
   protected final float    _borderWidth;


   protected GShapeRenderingSymbol(final IVector2 point,
                                   final IMeasure<GArea> pointSize,
                                   final IMeasure<GLength> pointBorderSize,
                                   final IRenderingStyle renderingStyle,
                                   final IVectorial2DRenderingContext rc) {
      GAssert.notNull(point, "point");
      GAssert.notNull(pointSize, "pointSize");
      GAssert.notNull(pointBorderSize, "pointBorderSize");
      GAssert.notNull(rc, "rc");

      _extent = calculateExtent(point, pointSize, pointBorderSize, renderingStyle, rc);
      _borderWidth = calculateBorderWidth(point, pointSize, pointBorderSize, renderingStyle, rc);

      // calculate position last, so _extent is already calculated and can be used to center the symbol
      _position = calculatePosition(point, pointSize, pointBorderSize, renderingStyle, rc);
   }


   protected abstract IVector2 calculateExtent(final IVector2 point,
                                               final IMeasure<GArea> pointSize,
                                               final IMeasure<GLength> pointBorderSize,
                                               final IRenderingStyle renderingStyle,
                                               final IVectorial2DRenderingContext rc);


   protected float calculateBorderWidth(final IVector2 point,
                                        @SuppressWarnings("unused") final IMeasure<GArea> pointSize,
                                        final IMeasure<GLength> pointBorderSize,
                                        final IRenderingStyle renderingStyle,
                                        final IVectorial2DRenderingContext rc) {
      final double borderLenghtInMeters = pointBorderSize.getValueInReferenceUnits();
      final IVector2 pointPlusBorderSize = renderingStyle.increment(point, rc.getProjection(), borderLenghtInMeters, 0);
      return (float) rc.scaleExtent(pointPlusBorderSize.sub(point)).x();
   }


   protected IVector2 calculatePosition(final IVector2 point,
                                        @SuppressWarnings("unused") final IMeasure<GArea> pointSize,
                                        @SuppressWarnings("unused") final IMeasure<GLength> pointBorderSize,
                                        @SuppressWarnings("unused") final IRenderingStyle renderingStyle,
                                        final IVectorial2DRenderingContext rc) {
      final IVector2 scaledPoint = rc.scaleAndTranslatePoint(point);
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
                                final IVectorial2DRenderingContext rc) {

      final IColor pointColor = renderingStyle.getPointColor(point, feature, rc);
      final IColor pointBorderColor = renderingStyle.getPointBorderColor(point, feature, rc);
      final float pointOpacity = renderingStyle.getPointOpacity(point, feature, rc);

      final Color fillColor = pointColor.asAWTColor(pointOpacity);
      final Color borderColor = pointBorderColor.asAWTColor(pointOpacity);

      rawDraw(fillColor, borderColor, rc);
   }


   protected abstract void rawDraw(final Color fillColor,
                                   final Color borderColor,
                                   final IVectorial2DRenderingContext rc);


   private Color getLODIgnoreColor(final IVector2 point,
                                   final IGlobeFeature<IVector2, ? extends IBoundedGeometry<IVector2, ? extends IFiniteBounds<IVector2, ?>>> feature,
                                   final IRenderingStyle renderingStyle,
                                   final IVectorial2DRenderingContext rc) {
      if (renderingStyle.isDebugRendering()) {
         return renderingStyle.getLODColor().asAWTColor();
      }

      final IColor pointColor = renderingStyle.getPointColor(point, feature, rc);
      final float pointOpacity = renderingStyle.getPointOpacity(point, feature, rc);

      final Color fillColor = pointColor.asAWTColor(pointOpacity);

      if (_borderWidth <= 0) {
         return fillColor;
      }

      final IColor pointBorderColor = renderingStyle.getPointBorderColor(point, feature, rc);
      final Color borderColor = pointBorderColor.asAWTColor(pointOpacity);
      return GAWTUtils.mix(fillColor, borderColor);
   }


   @Override
   protected final void renderLODIgnore(final IVector2 point,
                                        final IGlobeFeature<IVector2, ? extends IBoundedGeometry<IVector2, ? extends IFiniteBounds<IVector2, ?>>> feature,
                                        final IRenderingStyle renderingStyle,
                                        final IVectorial2DRenderingContext rc) {
      final Color color = getLODIgnoreColor(point, feature, renderingStyle, rc);

      final IVector2 scaledPoint = rc.scaleAndTranslatePoint(point);
      //            rc.setPixel(scaledPoint, color);
      rc.setColor(color);
      rc.fillRect(scaledPoint.x(), scaledPoint.y(), 1, 1);
   }

}
