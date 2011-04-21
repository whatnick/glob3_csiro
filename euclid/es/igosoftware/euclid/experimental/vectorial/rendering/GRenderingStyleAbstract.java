

package es.igosoftware.euclid.experimental.vectorial.rendering;

import es.igosoftware.euclid.IBoundedGeometry;
import es.igosoftware.euclid.bounding.IFiniteBounds;
import es.igosoftware.euclid.experimental.measurement.GArea;
import es.igosoftware.euclid.experimental.measurement.GLength;
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
      final IMeasure<GArea> pointSize = getPointSize(point, feature, rc);
      final IMeasure<GLength> pointBorderSize = getPointBorderSize(point, feature, rc);
      return new GEllipseSymbol(point, pointSize, pointBorderSize, rc);
   }


   @Override
   public void drawPoint(final IVector2 point,
                         final IGlobeFeature<IVector2, ? extends IBoundedGeometry<IVector2, ? extends IFiniteBounds<IVector2, ?>>> feature,
                         final GVectorialRenderingContext rc) {

      final GSymbol symbol = getPointSymbol(point, feature, rc);

      symbol.draw(point, feature, rc);
   }


}
