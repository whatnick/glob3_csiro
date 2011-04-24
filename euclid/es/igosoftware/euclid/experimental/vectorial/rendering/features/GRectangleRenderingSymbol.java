

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


public class GRectangleRenderingSymbol
         extends
            GShapeRenderingSymbol {


   public GRectangleRenderingSymbol(final IVector2 point,
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

      final double extent = GMath.sqrt(areaInSquaredMeters);
      final IVector2 pointPlusExtent = scaler.increment(point, extent, extent);
      return scaler.scaleExtent(pointPlusExtent.sub(point));
   }


   @Override
   protected void rawDraw(final Color fillColor,
                          final Color borderColor,
                          final IVectorial2DRenderingScaler scaler,
                          final IVectorial2DDrawer drawer) {
      // fill point
      drawer.fillRect(_position, _extent, fillColor);

      // render border
      if (_borderWidth > 0) {
         final BasicStroke borderStroke = new BasicStroke(_borderWidth, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER);
         //         drawer.drawRect(_position.x(), _position.y(), _extent.x(), _extent.y(), borderColor, borderStroke);
         drawer.drawRect(_position, _extent, borderColor, borderStroke);
      }
   }


}
