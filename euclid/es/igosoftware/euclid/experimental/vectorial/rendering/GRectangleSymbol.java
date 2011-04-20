

package es.igosoftware.euclid.experimental.vectorial.rendering;

import java.awt.BasicStroke;
import java.awt.Color;

import es.igosoftware.euclid.experimental.measurement.GArea;
import es.igosoftware.euclid.experimental.measurement.IMeasure;
import es.igosoftware.euclid.vector.IVector2;
import es.igosoftware.util.GMath;


public class GRectangleSymbol
         extends
            GSymbol {

   private final IVector2 _position;
   private final IVector2 _extent;


   public GRectangleSymbol(final IVector2 point,
                           final IMeasure<GArea> pointSize,
                           final GVectorialRenderingContext rc) {
      final double areaInSquaredMeters = pointSize.getValueInReferenceUnits();

      final double extent = GMath.sqrt(areaInSquaredMeters);
      final IVector2 pointPlusExtent = rc._renderingStyle.increment(point, rc._projection, extent, extent);
      _extent = rc.scaleExtent(pointPlusExtent.sub(point));

      final IVector2 scaledPoint = rc.scaleAndTranslatePoint(point);
      _position = scaledPoint.sub(_extent.div(2));
   }


   @Override
   public boolean isBiggerThan(final double lodMinSize) {
      return ((_extent.x() * _extent.y()) > lodMinSize);
   }


   @Override
   public void draw(final Color fillColor,
                    final float borderWidth,
                    final Color borderColor,
                    final GVectorialRenderingContext rc) {
      // fill point
      rc.setColor(fillColor);
      rc.fillRect(_position.x(), _position.y(), _extent.x(), _extent.y());

      // render border
      if (borderWidth > 0) {
         final BasicStroke borderStroke = new BasicStroke(borderWidth, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
         rc.setStroke(borderStroke);

         rc.setColor(borderColor);
         rc.drawRect(_position.x(), _position.y(), _extent.x(), _extent.y());
      }
   }


}
