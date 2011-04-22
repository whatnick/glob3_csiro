

package es.igosoftware.euclid.experimental.vectorial.rendering.features;

import java.awt.BasicStroke;
import java.awt.Color;

import es.igosoftware.euclid.experimental.measurement.GArea;
import es.igosoftware.euclid.experimental.measurement.GLength;
import es.igosoftware.euclid.experimental.measurement.IMeasure;
import es.igosoftware.euclid.experimental.vectorial.rendering.IVectorial2DRenderingContext;
import es.igosoftware.euclid.experimental.vectorial.rendering.styling.IRenderingStyle;
import es.igosoftware.euclid.vector.IVector2;
import es.igosoftware.util.GMath;


public class GEllipseRenderingSymbol
         extends
            GShapeRenderingSymbol {


   public GEllipseRenderingSymbol(final IVector2 point,
                                  final IMeasure<GArea> pointSize,
                                  final IMeasure<GLength> pointBorderSize,
                                  final IRenderingStyle renderingStyle,
                                  final IVectorial2DRenderingContext rc) {
      super(point, pointSize, pointBorderSize, renderingStyle, rc);
   }


   @Override
   protected IVector2 calculateExtent(final IVector2 point,
                                      final IMeasure<GArea> pointSize,
                                      final IMeasure<GLength> pointBorderSize,
                                      final IRenderingStyle renderingStyle,
                                      final IVectorial2DRenderingContext rc) {
      final double areaInSquaredMeters = pointSize.getValueInReferenceUnits();

      final double radius = GMath.sqrt(areaInSquaredMeters / Math.PI);
      final IVector2 pointPlusRadius = renderingStyle.increment(point, rc.getProjection(), radius, radius);
      return rc.scaleExtent(pointPlusRadius.sub(point)).scale(2); // radius times 2 (for extent)
   }


   @Override
   protected void rawDraw(final Color fillColor,
                          final Color borderColor,
                          final IVectorial2DRenderingContext rc) {
      // fill point
      rc.setColor(fillColor);
      rc.fillOval(_position.x(), _position.y(), _extent.x(), _extent.y());

      // render border
      if (_borderWidth > 0) {
         final BasicStroke borderStroke = new BasicStroke(_borderWidth, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER);
         rc.setStroke(borderStroke);

         rc.setColor(borderColor);
         rc.drawOval(_position.x(), _position.y(), _extent.x(), _extent.y());
      }
   }


}
