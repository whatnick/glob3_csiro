

package es.igosoftware.euclid.experimental.vectorial.rendering.styledgeometries;


import java.awt.Paint;

import es.igosoftware.euclid.ICurve2D;
import es.igosoftware.euclid.bounding.GAxisAlignedRectangle;
import es.igosoftware.euclid.bounding.IFinite2DBounds;
import es.igosoftware.euclid.experimental.vectorial.rendering.context.IVectorial2DDrawer;
import es.igosoftware.euclid.vector.IVector2;
import es.igosoftware.util.GAssert;


public abstract class GStyledCurve2D<

GeometryT extends ICurve2D<? extends IFinite2DBounds<?>>

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
