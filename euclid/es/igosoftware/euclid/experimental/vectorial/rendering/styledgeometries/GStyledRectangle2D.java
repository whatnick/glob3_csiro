

package es.igosoftware.euclid.experimental.vectorial.rendering.styledgeometries;


import java.awt.Paint;
import java.awt.Stroke;

import es.igosoftware.euclid.IBoundedGeometry2D;
import es.igosoftware.euclid.bounding.GAxisAlignedRectangle;
import es.igosoftware.euclid.bounding.IFinite2DBounds;
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
   protected void draw(final IVectorial2DDrawer drawer,
                       final boolean debugRendering) {
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


   @Override
   public boolean isGroupableWith(final GStyled2DGeometry<? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>> that) {
      if (that instanceof GStyledRectangle2D) {
         final GStyledRectangle2D thatRectangle = (GStyledRectangle2D) that;
         //         return _geometry.closeTo(thatRectangle._geometry) && _surfaceStyle.isGroupableWith(thatRectangle._surfaceStyle)
         //         && _curveStyle.isGroupableWith(thatRectangle._curveStyle);
         return _surfaceStyle.isGroupableWith(thatRectangle._surfaceStyle)
                && _curveStyle.isGroupableWith(thatRectangle._curveStyle);
      }

      return false;
   }


   @Override
   public String toString() {
      return "GStyledRectangle2D [geometry=" + _geometry + ", surfaceStyle=" + _surfaceStyle + ", curveStyle=" + _curveStyle
             + "]";
   }


   @Override
   public GAxisAlignedRectangle getBounds() {
      return _geometry;
   }


   @Override
   public boolean isGroupable() {
      return true;
   }


}
