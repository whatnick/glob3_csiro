

package es.igosoftware.euclid.experimental.vectorial.rendering.styledgeometries;

import java.awt.Paint;
import java.awt.Stroke;
import java.util.Collection;

import es.igosoftware.euclid.IBoundedGeometry2D;
import es.igosoftware.euclid.bounding.GAxisAlignedRectangle;
import es.igosoftware.euclid.bounding.IFinite2DBounds;
import es.igosoftware.euclid.experimental.vectorial.rendering.context.IVectorial2DDrawer;
import es.igosoftware.euclid.shape.GAxisAlignedOval2D;
import es.igosoftware.euclid.vector.GVector2D;


public class GStyledOval2D
         extends
            GStyledSurface2D<GAxisAlignedOval2D> {


   public GStyledOval2D(final GAxisAlignedOval2D ellipse,
                        final String label,
                        final ISurface2DStyle surfaceStyle,
                        final ICurve2DStyle curveStyle,
                        final int priority) {
      super(ellipse, label, surfaceStyle, curveStyle, priority);
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
      if (that instanceof GStyledOval2D) {
         final GStyledOval2D thatEllipse = (GStyledOval2D) that;
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


   @Override
   protected GStyledOval2D getAverageSymbol(final Collection<? extends GStyled2DGeometry<? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>>> group,
                                            final String label) {

      int maxPriority = Integer.MIN_VALUE;
      GVector2D sumCenter = GVector2D.ZERO;
      GVector2D sumRadius = GVector2D.ZERO;
      for (final GStyled2DGeometry<? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>> each : group) {
         final GStyledOval2D eachEllipse = (GStyledOval2D) each;
         final GAxisAlignedOval2D ellipse = eachEllipse._geometry;
         sumCenter = sumCenter.add(ellipse._center);
         sumRadius = sumRadius.add(ellipse._radius);
         maxPriority = Math.max(maxPriority, each.getPriority());
      }

      final GVector2D averageCenter = sumCenter.div(group.size());
      final GVector2D averageRadius = sumRadius.div(group.size());

      return new GStyledOval2D(new GAxisAlignedOval2D(averageCenter, averageRadius), label, _surfaceStyle, _curveStyle,
               maxPriority);
   }


}
