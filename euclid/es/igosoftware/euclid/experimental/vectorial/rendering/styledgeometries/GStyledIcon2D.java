

package es.igosoftware.euclid.experimental.vectorial.rendering.styledgeometries;


import java.awt.Color;
import java.awt.image.BufferedImage;

import es.igosoftware.euclid.experimental.vectorial.rendering.context.IVectorial2DDrawer;
import es.igosoftware.euclid.vector.GVector2D;
import es.igosoftware.euclid.vector.IVector2;
import es.igosoftware.util.GAssert;


public class GStyledIcon2D
         extends
            GStyled2DGeometry<IVector2> {


   private final BufferedImage _icon;
   private final float         _opacity;

   private final float         _percentFilled;
   private final Color         _averageColor;
   private final GVector2D     _iconExtent;


   public GStyledIcon2D(final IVector2 position,
                        final BufferedImage icon,
                        final float opacity) {
      super(position);

      GAssert.notNull(icon, "icon");

      _icon = icon;
      _opacity = opacity;
      _percentFilled = GIconUtils.getPercentFilled(icon);
      _averageColor = GIconUtils.getAverageColor(icon);

      _iconExtent = new GVector2D(_icon.getWidth(), _icon.getHeight());
   }


   @Override
   protected void draw(final IVectorial2DDrawer drawer,
                       final boolean debugRendering) {
      drawer.drawImage(_icon, _geometry, _opacity);
   }


   @Override
   public String toString() {
      return "GStyledIcon2D [icon=" + _icon + ", opacity=" + _opacity + "]";
   }


   @Override
   protected boolean isBigger(final double lodMinSize) {
      return (_iconExtent.length() * _percentFilled) > lodMinSize;
   }


   @Override
   protected void drawLODIgnore(final IVectorial2DDrawer drawer,
                                final boolean debugRendering) {
      drawer.fillRect(_geometry, _iconExtent, debugRendering ? Color.MAGENTA : _averageColor);
   }


}
