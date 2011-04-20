

package es.igosoftware.euclid.experimental.vectorial.rendering;

import java.awt.Color;

import es.igosoftware.euclid.IBoundedGeometry;
import es.igosoftware.euclid.bounding.IFiniteBounds;
import es.igosoftware.euclid.colors.IColor;
import es.igosoftware.euclid.experimental.measurement.GArea;
import es.igosoftware.euclid.experimental.measurement.IMeasure;
import es.igosoftware.euclid.features.IGlobeFeature;
import es.igosoftware.euclid.vector.IVector2;


public abstract class GRenderingStyleAbstract
         implements
            IRenderingStyle {


   @Override
   public GSymbol getPointSymbol(final IVector2 point,
                                 final IGlobeFeature<IVector2, ? extends IBoundedGeometry<IVector2, ? extends IFiniteBounds<IVector2, ?>>> feature,
                                 final GVectorialRenderingContext rc) {
      final IMeasure<GArea> pointSize = getPointSize(feature);
      return new GEllipseSymbol(point, pointSize, rc);
   }


   @Override
   public void drawPoint(final IVector2 point,
                         final IGlobeFeature<IVector2, ? extends IBoundedGeometry<IVector2, ? extends IFiniteBounds<IVector2, ?>>> feature,
                         final GVectorialRenderingContext rc) {


      final GSymbol symbol = getPointSymbol(point, feature, rc);

      final IColor pointColor = getPointColor(feature);
      final float pointOpacity = getPointOpacity(feature);
      final Color fillColor = pointColor.asAWTColor(pointOpacity);
      final Color borderColor = pointColor.muchDarker().asAWTColor(pointOpacity);

      if (!symbol.isBiggerThan(getLODMinSize())) {
         if (isRenderLODIgnores() || isDebugRendering()) {
            final Color color = isDebugRendering() ? Color.RED : fillColor;

            final IVector2 scaledPoint = rc.scaleAndTranslatePoint(point);
            rc.setPixel(scaledPoint, color);
         }

         return;
      }


      final float borderWidth = 1;
      symbol.draw(fillColor, borderWidth, borderColor, rc);
   }

}
