

package es.igosoftware.euclid.experimental.vectorial.rendering;

import java.awt.BasicStroke;
import java.awt.Color;

import es.igosoftware.euclid.experimental.measurement.GArea;
import es.igosoftware.euclid.experimental.measurement.GLength;
import es.igosoftware.euclid.experimental.measurement.IMeasure;
import es.igosoftware.euclid.vector.IVector2;
import es.igosoftware.util.GMath;


public class GEllipseSymbol
         extends
            GShapeSymbol {


   public GEllipseSymbol(final IVector2 point,
                         final IMeasure<GArea> pointSize,
                         final IMeasure<GLength> pointBorderSize,
                         final GVectorialRenderingContext rc) {
      super(point, pointSize, pointBorderSize, rc);
   }


   @Override
   protected IVector2 calculateExtent(final IVector2 point,
                                      final IMeasure<GArea> pointSize,
                                      final IMeasure<GLength> pointBorderSize,
                                      final GVectorialRenderingContext rc) {
      final double areaInSquaredMeters = pointSize.getValueInReferenceUnits();

      final double radius = GMath.sqrt(areaInSquaredMeters / Math.PI);
      final IVector2 pointPlusRadius = rc._renderingStyle.increment(point, rc._projection, radius, radius);
      return rc.scaleExtent(pointPlusRadius.sub(point)).scale(2); // radius times 2 (for extent)
   }


   @Override
   protected void rawDraw(final Color fillColor,
                          final Color borderColor,
                          final GVectorialRenderingContext rc) {
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
