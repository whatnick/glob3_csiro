

package es.igosoftware.euclid.experimental.vectorial.rendering.features;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;

import es.igosoftware.euclid.IBoundedGeometry;
import es.igosoftware.euclid.bounding.IFiniteBounds;
import es.igosoftware.euclid.colors.IColor;
import es.igosoftware.euclid.experimental.measurement.GLength;
import es.igosoftware.euclid.experimental.measurement.IMeasure;
import es.igosoftware.euclid.experimental.vectorial.rendering.context.IVectorial2DRenderingContext;
import es.igosoftware.euclid.experimental.vectorial.rendering.styling.IRenderingStyle;
import es.igosoftware.euclid.features.IGlobeFeature;
import es.igosoftware.euclid.shape.IPolygon2D;
import es.igosoftware.euclid.vector.IVector2;
import es.igosoftware.util.GAWTUtils;


public class GPolygonRenderingShape
         extends
            GRenderingFeatureAbstract<IPolygon2D>
         implements
            IRenderingShape<IPolygon2D> {


   private final float       _borderWidth;
   private final Rectangle2D _bounds;
   private final Shape       _awtShape;


   public GPolygonRenderingShape(final IPolygon2D polygon,
                                 final Shape awtShape,
                                 final IMeasure<GLength> surfaceBorderSize,
                                 final IRenderingStyle renderingStyle,
                                 final IVectorial2DRenderingContext rc) {
      final IVector2 point = polygon.getCentroid();

      final double borderLenghtInMeters = surfaceBorderSize.getValueInReferenceUnits();
      final IVector2 pointPlusBorderSize = renderingStyle.increment(point, rc.getProjection(), borderLenghtInMeters, 0);
      _borderWidth = (float) rc.scaleExtent(pointPlusBorderSize.sub(point)).x();

      _bounds = awtShape.getBounds2D();

      _awtShape = awtShape;
   }


   @Override
   public boolean isBiggerThan(final double lodMinSize) {
      return (_bounds.getWidth() * _bounds.getHeight()) >= lodMinSize;
   }


   private Color getLODIgnoreColor(final IPolygon2D polygon,
                                   final IGlobeFeature<IVector2, ? extends IBoundedGeometry<IVector2, ? extends IFiniteBounds<IVector2, ?>>> feature,
                                   final IRenderingStyle renderingStyle,
                                   final IVectorial2DRenderingContext rc) {
      if (renderingStyle.isDebugRendering()) {
         return renderingStyle.getLODColor().asAWTColor();
      }

      final IColor pointColor = renderingStyle.getSurfaceColor(polygon, feature, rc);
      final float pointOpacity = renderingStyle.getSurfaceOpacity(polygon, feature, rc);

      final Color fillColor = pointColor.asAWTColor(pointOpacity);

      if (_borderWidth <= 0) {
         return fillColor;
      }

      final IColor pointBorderColor = renderingStyle.getSurfaceBorderColor(polygon, feature, rc);
      final Color borderColor = pointBorderColor.asAWTColor(pointOpacity);
      return GAWTUtils.mix(fillColor, borderColor);
   }


   @Override
   public final void renderLODIgnore(final IPolygon2D polygon,
                                     final IGlobeFeature<IVector2, ? extends IBoundedGeometry<IVector2, ? extends IFiniteBounds<IVector2, ?>>> feature,
                                     final IRenderingStyle renderingStyle,
                                     final IVectorial2DRenderingContext rc) {
      final Color color = getLODIgnoreColor(polygon, feature, renderingStyle, rc);

      final IVector2 scaledPoint = rc.scaleAndTranslatePoint(polygon.getCentroid());
      //            rc.setPixel(scaledPoint, color);
      rc.setColor(color);
      rc.fillRect(scaledPoint.x(), scaledPoint.y(), 1, 1);
   }


   @Override
   public final void rawDraw(final IPolygon2D polygon,
                             final IGlobeFeature<IVector2, ? extends IBoundedGeometry<IVector2, ? extends IFiniteBounds<IVector2, ?>>> feature,
                             final IRenderingStyle renderingStyle,
                             final IVectorial2DRenderingContext rc) {


      final IColor polygonColor = renderingStyle.getSurfaceColor(polygon, feature, rc);
      final float polygonOpacity = renderingStyle.getSurfaceOpacity(polygon, feature, rc);

      final Color fillColor = polygonColor.asAWTColor(polygonOpacity);


      // fill polygon
      rc.setColor(fillColor);
      rc.fill(_awtShape);


      // render border
      if (_borderWidth > 0) {
         final IColor polygonBorderColor = renderingStyle.getSurfaceBorderColor(polygon, feature, rc);
         final Color borderColor = polygonBorderColor.asAWTColor(polygonOpacity);

         final BasicStroke borderStroke = new BasicStroke(_borderWidth, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);

         rc.setStroke(borderStroke);
         rc.setColor(borderColor);
         rc.draw(_awtShape);
      }
   }


}
