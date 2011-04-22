

package es.igosoftware.euclid.experimental.vectorial.rendering;

import java.awt.BasicStroke;
import java.awt.Color;

import es.igosoftware.euclid.experimental.measurement.GArea;
import es.igosoftware.euclid.experimental.measurement.GLength;
import es.igosoftware.euclid.experimental.measurement.IMeasure;
import es.igosoftware.euclid.vector.IVector2;
import es.igosoftware.util.GMath;


public class GRectangleRenderingSymbol
         extends
            GShapeRenderingSymbol {


   public GRectangleRenderingSymbol(final IVector2 point,
                                    final IMeasure<GArea> pointSize,
                                    final IMeasure<GLength> pointBorderSize,
                                    final IRenderingStyle renderingStyle,
                                    final IVectorialRenderingContext rc) {
      super(point, pointSize, pointBorderSize, renderingStyle, rc);
   }


   @Override
   protected IVector2 calculateExtent(final IVector2 point,
                                      final IMeasure<GArea> pointSize,
                                      final IMeasure<GLength> pointBorderSize,
                                      final IRenderingStyle renderingStyle,
                                      final IVectorialRenderingContext rc) {
      final double areaInSquaredMeters = pointSize.getValueInReferenceUnits();

      final double extent = GMath.sqrt(areaInSquaredMeters);
      final IVector2 pointPlusExtent = renderingStyle.increment(point, rc.getProjection(), extent, extent);
      return rc.scaleExtent(pointPlusExtent.sub(point));
   }


   @Override
   protected void rawDraw(final Color fillColor,
                          final Color borderColor,
                          final IVectorialRenderingContext rc) {
      // fill point
      rc.setColor(fillColor);
      rc.fillRect(_position.x(), _position.y(), _extent.x(), _extent.y());

      // render border
      if (_borderWidth > 0) {
         final BasicStroke borderStroke = new BasicStroke(_borderWidth, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER);
         rc.setStroke(borderStroke);

         rc.setColor(borderColor);
         rc.drawRect(_position.x(), _position.y(), _extent.x(), _extent.y());
      }
   }


}
