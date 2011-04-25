

package es.igosoftware.euclid.experimental.vectorial.rendering.styledgeometries;


import java.awt.Paint;
import java.awt.Stroke;

import es.igosoftware.euclid.bounding.GAxisAlignedRectangle;
import es.igosoftware.euclid.experimental.vectorial.rendering.context.IVectorial2DDrawer;
import es.igosoftware.euclid.vector.IVector2;


public class GStyledRectangle2D
         extends
            GStyledSurface2D<GAxisAlignedRectangle> {


   public GStyledRectangle2D(final GAxisAlignedRectangle rectangle,
                             final ISurface2DStyle surfaceStyle,
                             final ICurve2DStyle curveStyle) {
      super(rectangle, surfaceStyle, curveStyle);

   }


   @Override
   protected void rawDraw(final IVectorial2DDrawer drawer) {
      final IVector2 position = _geometry._lower;
      final IVector2 extent = _geometry._extent;


      // render surface
      final Paint fillPaint = _surfaceStyle.getSurfacePaint();
      if (fillPaint != null) {
         drawer.fillRect(position, extent, fillPaint);
      }


      // render border
      final Stroke borderStroke = _curveStyle.getBorderStroke();
      if (borderStroke != null) {
         final Paint borderPaint = _curveStyle.getBorderPaint();
         drawer.drawRect(position, extent, borderPaint, borderStroke);
      }
   }


}
