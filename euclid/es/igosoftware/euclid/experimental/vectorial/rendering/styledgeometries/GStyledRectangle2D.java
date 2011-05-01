

package es.igosoftware.euclid.experimental.vectorial.rendering.styledgeometries;


import java.awt.Paint;
import java.awt.Stroke;
import java.util.Collection;

import es.igosoftware.euclid.IBoundedGeometry2D;
import es.igosoftware.euclid.bounding.GAxisAlignedRectangle;
import es.igosoftware.euclid.bounding.IFinite2DBounds;
import es.igosoftware.euclid.experimental.vectorial.rendering.context.IVectorial2DDrawer;
import es.igosoftware.euclid.vector.GVector2D;
import es.igosoftware.euclid.vector.IVector2;


public class GStyledRectangle2D
         extends
            GStyledSurface2D<GAxisAlignedRectangle> {


   public GStyledRectangle2D(final GAxisAlignedRectangle rectangle,
                             final String label,
                             final ISurface2DStyle surfaceStyle,
                             final ICurve2DStyle curveStyle,
                             final int priority) {
      super(rectangle, label, surfaceStyle, curveStyle, priority);

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


   @Override
   protected GStyledRectangle2D getAverageSymbol(final Collection<? extends GStyled2DGeometry<? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>>> group,
                                                 final String label) {
      int maxPriority = Integer.MIN_VALUE;

      GVector2D sumLower = GVector2D.ZERO;
      GVector2D sumExtent = GVector2D.ZERO;
      for (final GStyled2DGeometry<? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>> each : group) {
         final GStyledRectangle2D eachEllipse = (GStyledRectangle2D) each;
         final GAxisAlignedRectangle ellipse = eachEllipse._geometry;
         sumLower = sumLower.add(ellipse._lower);
         sumExtent = sumExtent.add(ellipse._extent);
         maxPriority = Math.max(maxPriority, each.getPriority());
      }

      final GVector2D averageLower = sumLower.div(group.size());
      final GVector2D averageExtent = sumExtent.div(group.size());

      return new GStyledRectangle2D(new GAxisAlignedRectangle(averageLower, averageLower.add(averageExtent)), label,
               _surfaceStyle, _curveStyle, maxPriority);
   }


}
