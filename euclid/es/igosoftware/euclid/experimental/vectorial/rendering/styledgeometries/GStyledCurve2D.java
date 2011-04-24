

package es.igosoftware.euclid.experimental.vectorial.rendering.styledgeometries;


import es.igosoftware.euclid.ICurve2D;
import es.igosoftware.util.GAssert;


public abstract class GStyledCurve2D<

GeometryT extends ICurve2D<?>

>

         extends
            GStyled2DGeometry<GeometryT> {


   protected final ICurve2DStyle _curveStyle;


   protected GStyledCurve2D(final GeometryT geometry,
                            final ICurve2DStyle curveStyle) {
      super(geometry);

      GAssert.notNull(curveStyle, "curveStyle");

      _curveStyle = curveStyle;
   }


}
