

package es.igosoftware.euclid.experimental.vectorial.rendering;

import java.awt.Color;

import es.igosoftware.euclid.IBoundedGeometry;
import es.igosoftware.euclid.bounding.IFiniteBounds;
import es.igosoftware.euclid.colors.IColor;
import es.igosoftware.euclid.experimental.measurement.GArea;
import es.igosoftware.euclid.experimental.measurement.IMeasure;
import es.igosoftware.euclid.features.IGlobeFeature;
import es.igosoftware.euclid.vector.GVector2D;
import es.igosoftware.euclid.vector.IVector2;
import es.igosoftware.util.GMath;


public abstract class GRenderingStyleAbstract
         implements
            IRenderingStyle {


   private GSymbol getPointSymbol(final IVector2 scaledPoint,
                                  final IGlobeFeature<IVector2, ? extends IBoundedGeometry<IVector2, ? extends IFiniteBounds<IVector2, ?>>> feature,
                                  final IVector2 point,
                                  final GVectorialRenderingContext rc) {
      final IMeasure<GArea> pointSize = getPointSize(feature);
      final double area = pointSize.getValue() * pointSize.getUnit().convertionFactor();

      final double radiusD = GMath.sqrt(area / Math.PI);
      final IVector2 pointPlusRadius = increment(point, rc._projection, radiusD, radiusD);
      final IVector2 radius = rc.scaleExtent(pointPlusRadius.sub(point));

      final double centerX = scaledPoint.x() - (radius.x() / 2);
      final double centerY = scaledPoint.y() - (radius.y() / 2);

      return new GEllipseSymbol(new GVector2D(centerX, centerY), radius);
   }


   @Override
   public void drawPoint(final IVector2 point,
                         final IGlobeFeature<IVector2, ? extends IBoundedGeometry<IVector2, ? extends IFiniteBounds<IVector2, ?>>> feature,
                         final GVectorialRenderingContext rc) {

      final IVector2 scaledPoint = rc.scaleAndTranslatePoint(point);

      final GSymbol symbol = getPointSymbol(scaledPoint, feature, point, rc);

      final IColor pointColor = getPointColor(feature);
      final float pointOpacity = getPointOpacity(feature);
      final Color fillColor = pointColor.asAWTColor(pointOpacity);
      final Color borderColor = pointColor.muchDarker().asAWTColor(pointOpacity);

      if (!symbol.isBiggerThan(getLODMinSize())) {
         if (isRenderLODIgnores() || isDebugRendering()) {
            final Color color = isDebugRendering() ? Color.RED : fillColor;

            rc.setPixel(scaledPoint, color);
         }

         return;
      }


      final float borderWidth = 1;
      symbol.draw(fillColor, borderWidth, borderColor, rc);
   }


}
