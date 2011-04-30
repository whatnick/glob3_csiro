

package es.igosoftware.euclid.experimental.vectorial.rendering.styledgeometries;


import java.awt.Color;

import es.igosoftware.euclid.ICurve2D;
import es.igosoftware.euclid.bounding.GAxisAlignedRectangle;
import es.igosoftware.euclid.bounding.IFinite2DBounds;
import es.igosoftware.euclid.experimental.vectorial.rendering.context.IVectorial2DDrawer;
import es.igosoftware.util.GAssert;


public abstract class GStyledCurve2D<

GeometryT extends ICurve2D<? extends IFinite2DBounds<?>>

>

         extends
            GStyled2DGeometry<GeometryT> {


   protected final ICurve2DStyle         _curveStyle;
   protected final GAxisAlignedRectangle _bounds;


   protected GStyledCurve2D(final GeometryT geometry,
                            final ICurve2DStyle curveStyle) {
      super(geometry, 0);

      GAssert.notNull(curveStyle, "curveStyle");

      _curveStyle = curveStyle;

      _bounds = geometry.getBounds().asAxisAlignedOrthotope();
   }


   @Override
   protected boolean isBigger(final double lodMinSize) {
      return (_bounds.area() > lodMinSize);
   }


   @Override
   protected void drawLODIgnore(final IVectorial2DDrawer drawer,
                                final boolean debugRendering) {
      drawer.fillRect(_bounds, debugRendering ? Color.MAGENTA : _curveStyle.getBorderPaint());
   }


}
