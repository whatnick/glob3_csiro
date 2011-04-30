

package es.igosoftware.euclid.experimental.vectorial.rendering.styledgeometries;


import java.awt.Color;

import es.igosoftware.euclid.ISurface2D;
import es.igosoftware.euclid.bounding.GAxisAlignedRectangle;
import es.igosoftware.euclid.bounding.IFinite2DBounds;
import es.igosoftware.euclid.experimental.vectorial.rendering.context.IVectorial2DDrawer;
import es.igosoftware.util.GAssert;


public abstract class GStyledSurface2D<

GeometryT extends ISurface2D<? extends IFinite2DBounds<?>>

>

         extends
            GStyled2DGeometry<GeometryT> {

   protected final ISurface2DStyle       _surfaceStyle;
   protected final ICurve2DStyle         _curveStyle;
   protected final GAxisAlignedRectangle _bounds;


   protected GStyledSurface2D(final GeometryT geometry,
                              final ISurface2DStyle surfaceStyle,
                              final ICurve2DStyle curveStyle) {
      super(geometry, 0);

      GAssert.notNull(surfaceStyle, "surfaceStyle");
      GAssert.notNull(curveStyle, "curveStyle");

      _surfaceStyle = surfaceStyle;
      _curveStyle = curveStyle;

      _bounds = _geometry.getBounds().asAxisAlignedOrthotope();
   }


   @Override
   protected boolean isBigger(final double lodMinSize) {
      return (_bounds.area() > lodMinSize);
   }


   @Override
   protected void drawLODIgnore(final IVectorial2DDrawer drawer,
                                final boolean debugRendering) {
      drawer.fillRect(_bounds, debugRendering ? Color.MAGENTA : _surfaceStyle.getSurfacePaint());
   }


}
