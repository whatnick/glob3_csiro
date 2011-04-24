

package es.igosoftware.euclid.experimental.vectorial.rendering.styledgeometries;


import es.igosoftware.euclid.ISurface2D;
import es.igosoftware.util.GAssert;


public abstract class GStyledSurface2D<

GeometryT extends ISurface2D<?>

>

         extends
            GStyled2DGeometry<GeometryT> {

   protected final ICurve2DStyle   _curveStyle;
   protected final ISurface2DStyle _surfaceStyle;


   protected GStyledSurface2D(final GeometryT geometry,
                              final ICurve2DStyle curveStyle,
                              final ISurface2DStyle surfaceStyle) {
      super(geometry);

      GAssert.notNull(curveStyle, "curveStyle");
      GAssert.notNull(surfaceStyle, "surfaceStyle");

      _curveStyle = curveStyle;
      _surfaceStyle = surfaceStyle;
   }


}
