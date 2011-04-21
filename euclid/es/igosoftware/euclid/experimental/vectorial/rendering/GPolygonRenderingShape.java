

package es.igosoftware.euclid.experimental.vectorial.rendering;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;

import es.igosoftware.euclid.IBoundedGeometry;
import es.igosoftware.euclid.bounding.IFiniteBounds;
import es.igosoftware.euclid.colors.IColor;
import es.igosoftware.euclid.experimental.measurement.GLength;
import es.igosoftware.euclid.experimental.measurement.IMeasure;
import es.igosoftware.euclid.features.IGlobeFeature;
import es.igosoftware.euclid.shape.IPolygon2D;
import es.igosoftware.euclid.vector.IVector2;
import es.igosoftware.util.GAWTUtils;


public class GPolygonRenderingShape
         extends
            GRenderingShape<IPolygon2D> {


   private final float       _borderWidth;
   private final Rectangle2D _bounds;
   private final Shape       _awtShape;


   public GPolygonRenderingShape(final IPolygon2D polygon,
                                 final Shape awtShape,
                                 final IMeasure<GLength> surfaceBorderSize,
                                 final GVectorialRenderingContext rc) {
      final IVector2 point = polygon.getCentroid();

      final double borderLenghtInMeters = surfaceBorderSize.getValueInReferenceUnits();
      final IVector2 pointPlusBorderSize = rc._renderingStyle.increment(point, rc._projection, borderLenghtInMeters, 0);
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
                                   final GVectorialRenderingContext rc) {
      if (rc._renderingStyle.isDebugRendering()) {
         return rc._renderingStyle.getLODColor().asAWTColor();
      }

      final IColor pointColor = rc._renderingStyle.getSurfaceColor(polygon, feature, rc);
      final float pointOpacity = rc._renderingStyle.getSurfaceOpacity(polygon, feature, rc);

      final Color fillColor = pointColor.asAWTColor(pointOpacity);

      if (_borderWidth <= 0) {
         return fillColor;
      }

      final IColor pointBorderColor = rc._renderingStyle.getSurfaceBorderColor(polygon, feature, rc);
      final Color borderColor = pointBorderColor.asAWTColor(pointOpacity);
      return GAWTUtils.mix(fillColor, borderColor);
   }


   @Override
   protected final void renderLODIgnore(final IPolygon2D polygon,
                                        final IGlobeFeature<IVector2, ? extends IBoundedGeometry<IVector2, ? extends IFiniteBounds<IVector2, ?>>> feature,
                                        final GVectorialRenderingContext rc) {
      final Color color = getLODIgnoreColor(polygon, feature, rc);

      final IVector2 scaledPoint = rc.scaleAndTranslatePoint(polygon.getCentroid());
      //            rc.setPixel(scaledPoint, color);
      rc.setColor(color);
      rc.fillRect(scaledPoint.x(), scaledPoint.y(), 1, 1);
   }


   @Override
   protected void rawDraw(final IPolygon2D polygon,
                          final IGlobeFeature<IVector2, ? extends IBoundedGeometry<IVector2, ? extends IFiniteBounds<IVector2, ?>>> feature,
                          final GVectorialRenderingContext rc) {


      final IColor polygonColor = rc._renderingStyle.getSurfaceColor(polygon, feature, rc);
      final float polygonOpacity = rc._renderingStyle.getSurfaceOpacity(polygon, feature, rc);

      final Color fillColor = polygonColor.asAWTColor(polygonOpacity);


      // fill polygon
      rc.setColor(fillColor);
      rc.fill(_awtShape);


      // render border
      if (_borderWidth > 0) {
         final IColor polygonBorderColor = rc._renderingStyle.getSurfaceBorderColor(polygon, feature, rc);
         final Color borderColor = polygonBorderColor.asAWTColor(polygonOpacity);

         final BasicStroke borderStroke = new BasicStroke(_borderWidth, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);

         rc.setStroke(borderStroke);
         rc.setColor(borderColor);
         rc.draw(_awtShape);
      }
   }


}
