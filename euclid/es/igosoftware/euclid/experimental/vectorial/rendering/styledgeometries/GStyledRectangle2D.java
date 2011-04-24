

package es.igosoftware.euclid.experimental.vectorial.rendering.styledgeometries;


import java.awt.Paint;
import java.awt.Stroke;
import java.awt.image.BufferedImage;

import es.igosoftware.euclid.bounding.GAxisAlignedRectangle;
import es.igosoftware.euclid.experimental.vectorial.rendering.context.IVectorial2DDrawer;
import es.igosoftware.euclid.vector.GVector2D;
import es.igosoftware.euclid.vector.IVector2;


public class GStyledRectangle2D
         extends
            GStyledSurface2D<GAxisAlignedRectangle> {


   private final BufferedImage _icon;
   private final float         _iconOpacity;


   public GStyledRectangle2D(final GAxisAlignedRectangle rectangle,
                             final ICurve2DStyle curveStyle,
                             final ISurface2DStyle surfaceStyle) {
      super(rectangle, curveStyle, surfaceStyle);

      _icon = null;
      _iconOpacity = 1;
   }


   public GStyledRectangle2D(final IVector2 position,
                             final ICurve2DStyle curveStyle,
                             final ISurface2DStyle surfaceStyle,
                             final BufferedImage icon,
                             final float iconOpacity) {
      super(new GAxisAlignedRectangle(position, new GVector2D(icon.getWidth(), icon.getHeight())), curveStyle, surfaceStyle);

      _icon = icon;
      _iconOpacity = iconOpacity;
   }


   public GStyledRectangle2D(final IVector2 position,
                             final BufferedImage icon,
                             final float iconOpacity) {
      this(position, GNullCurve2DStyle.INSTANCE, GNullSurface2DStyle.INSTANCE, icon, iconOpacity);
   }


   @Override
   public void draw(final IVectorial2DDrawer drawer) {
      final IVector2 position = _geometry._lower;
      final IVector2 extent = _geometry._extent;

      if (_icon != null) {
         drawer.drawImage(_icon, position, _iconOpacity);
      }

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
