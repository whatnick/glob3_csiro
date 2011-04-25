

package es.igosoftware.euclid.experimental.vectorial.rendering.styledgeometries;


import java.awt.Paint;

import es.igosoftware.euclid.ISurface2D;
import es.igosoftware.euclid.bounding.GAxisAlignedRectangle;
import es.igosoftware.euclid.bounding.IFinite2DBounds;
import es.igosoftware.euclid.experimental.vectorial.rendering.context.IVectorial2DDrawer;
import es.igosoftware.euclid.vector.IVector2;
import es.igosoftware.util.GAssert;


public abstract class GStyledSurface2D<

GeometryT extends ISurface2D<? extends IFinite2DBounds<?>>

>

         extends
            GStyled2DGeometry<GeometryT> {

   protected final ISurface2DStyle _surfaceStyle;
   protected final ICurve2DStyle   _curveStyle;


   protected GStyledSurface2D(final GeometryT geometry,
                              final ISurface2DStyle surfaceStyle,
                              final ICurve2DStyle curveStyle) {
      super(geometry);

      GAssert.notNull(surfaceStyle, "surfaceStyle");
      GAssert.notNull(curveStyle, "curveStyle");

      _surfaceStyle = surfaceStyle;
      _curveStyle = curveStyle;
   }


   @Override
   protected double getSize() {
      final IVector2 extent = _geometry.getBounds().asAxisAlignedOrthotope().getExtent();
      return extent.x() * extent.y();
   }


   @Override
   protected void drawLODIgnore(final IVectorial2DDrawer drawer) {
      final Paint paint = _curveStyle.getLODIgnorePaint();
      if (paint != null) {
         final GAxisAlignedRectangle bounds = _geometry.getBounds().asAxisAlignedOrthotope();
         drawer.fillRect(bounds, paint);
      }
   }


}
