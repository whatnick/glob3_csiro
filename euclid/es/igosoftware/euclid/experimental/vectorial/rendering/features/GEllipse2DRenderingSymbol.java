

package es.igosoftware.euclid.experimental.vectorial.rendering.features;

import java.awt.BasicStroke;
import java.awt.Color;

import es.igosoftware.euclid.experimental.measurement.GArea;
import es.igosoftware.euclid.experimental.measurement.GLength;
import es.igosoftware.euclid.experimental.measurement.IMeasure;
import es.igosoftware.euclid.experimental.vectorial.rendering.context.IVectorial2DDrawer;
import es.igosoftware.euclid.experimental.vectorial.rendering.context.IVectorial2DRenderingScaler;
import es.igosoftware.euclid.vector.IVector2;
import es.igosoftware.util.GMath;


public class GEllipse2DRenderingSymbol
         extends
            GShape2DRenderingSymbol {


   public GEllipse2DRenderingSymbol(final IVector2 point,
                                    final IMeasure<GArea> pointSize,
                                    final IMeasure<GLength> pointBorderSize,
                                    final IVectorial2DRenderingScaler scaler) {
      super(point, pointSize, pointBorderSize, scaler);
   }


   @Override
   protected IVector2 calculateExtent(final IVector2 point,
                                      final IMeasure<GArea> pointSize,
                                      final IMeasure<GLength> pointBorderSize,
                                      final IVectorial2DRenderingScaler scaler) {
      final double areaInSquaredMeters = pointSize.getValueInReferenceUnits();

      final double radius = GMath.sqrt(areaInSquaredMeters / Math.PI);
      final IVector2 pointPlusRadius = scaler.increment(point, radius, radius);
      return scaler.scaleExtent(pointPlusRadius.sub(point)).scale(2); // radius times 2 (for extent)
   }


   @Override
   protected void rawDraw(final Color fillColor,
                          final Color borderColor,
                          final IVectorial2DRenderingScaler scaler,
                          final IVectorial2DDrawer drawer) {
      // fill point
      drawer.fillOval(_position, _extent, fillColor);

      // render border
      if (_borderWidth > 0) {
         final BasicStroke borderStroke = new BasicStroke(_borderWidth, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER);
         drawer.drawOval(_position, _extent, borderColor, borderStroke);
      }
   }


}
