

package es.igosoftware.euclid.experimental.vectorial.rendering.styledgeometries;

import java.awt.Paint;
import java.awt.Stroke;

import es.igosoftware.euclid.IBoundedGeometry2D;
import es.igosoftware.euclid.bounding.GAxisAlignedRectangle;
import es.igosoftware.euclid.bounding.IFinite2DBounds;
import es.igosoftware.euclid.experimental.vectorial.rendering.context.IVectorial2DDrawer;
import es.igosoftware.euclid.shape.GAxisAlignedEllipse2D;


public class GStyledEllipse2D
         extends
            GStyledSurface2D<GAxisAlignedEllipse2D> {


   public GStyledEllipse2D(final GAxisAlignedEllipse2D ellipse,
                           final ISurface2DStyle surfaceStyle,
                           final ICurve2DStyle curveStyle) {
      super(ellipse, surfaceStyle, curveStyle);
   }


   @Override
   protected void draw(final IVectorial2DDrawer drawer,
                       final boolean debugRendering) {

      final GAxisAlignedRectangle bounds = _geometry.getBounds();

      // render surface
      final Paint fillPaint = _surfaceStyle.getSurfacePaint();
      if (fillPaint != null) {
         drawer.fillOval(bounds._lower, bounds._extent, fillPaint);
      }


      // render border
      final Stroke borderStroke = _curveStyle.getBorderStroke();
      if (borderStroke != null) {
         final Paint borderPaint = _curveStyle.getBorderPaint();
         drawer.drawOval(bounds._lower, bounds._extent, borderPaint, borderStroke);
      }
   }


   @Override
   public boolean isGroupableWith(final GStyled2DGeometry<? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>> that) {
      if (that instanceof GStyledEllipse2D) {
         final GStyledEllipse2D thatEllipse = (GStyledEllipse2D) that;
         //         return _geometry.closeTo(thatEllipse._geometry) && _surfaceStyle.isGroupableWith(thatEllipse._surfaceStyle)
         //         && _curveStyle.isGroupableWith(thatEllipse._curveStyle);
         return _surfaceStyle.isGroupableWith(thatEllipse._surfaceStyle) && _curveStyle.isGroupableWith(thatEllipse._curveStyle);
      }

      return false;
   }


   @Override
   public String toString() {
      return "GStyledEllipse2D [geometry=" + _geometry + ", surfaceStyle=" + _surfaceStyle + ", curveStyle=" + _curveStyle + "]";
   }


   @Override
   public GAxisAlignedRectangle getBounds() {
      return _geometry.getBounds();
   }


   @Override
   public boolean isGroupable() {
      return true;
   }


}
