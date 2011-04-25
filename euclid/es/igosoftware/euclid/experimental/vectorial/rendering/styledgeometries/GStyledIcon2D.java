

package es.igosoftware.euclid.experimental.vectorial.rendering.styledgeometries;


import java.awt.Color;
import java.awt.image.BufferedImage;

import es.igosoftware.euclid.experimental.vectorial.rendering.context.IVectorial2DDrawer;
import es.igosoftware.euclid.vector.IVector2;
import es.igosoftware.util.GAssert;


public class GStyledIcon2D
         extends
            GStyled2DGeometry<IVector2> {


   private final BufferedImage _icon;
   private final float         _opacity;
   private final float         _percentFilled;
   private final Color         _averageColor;


   public GStyledIcon2D(final IVector2 position,
                        final BufferedImage icon,
                        final float opacity) {
      super(position);

      GAssert.notNull(icon, "icon");

      _icon = icon;
      _opacity = opacity;
      _percentFilled = GIconUtils.getPercentFilled(icon);
      _averageColor = GIconUtils.getAverageColor(icon);
   }


   @Override
   protected void rawDraw(final IVectorial2DDrawer drawer) {
      drawer.drawImage(_icon, _geometry, _opacity);
   }


   @Override
   public String toString() {
      return "GStyledIcon2D [icon=" + _icon + ", opacity=" + _opacity + "]";
   }


   @Override
   protected double getSize() {
      return _icon.getWidth() * _icon.getHeight() * _percentFilled;
   }


   @Override
   protected void drawLODIgnore(final IVectorial2DDrawer drawer) {
      drawer.fillRect(_geometry.x(), _geometry.y(), _icon.getWidth(), _icon.getHeight(), _averageColor);
   }


}
