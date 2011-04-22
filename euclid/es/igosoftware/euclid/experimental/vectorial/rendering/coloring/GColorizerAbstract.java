

package es.igosoftware.euclid.experimental.vectorial.rendering.coloring;

import java.awt.Color;
import java.awt.Graphics2D;

import es.igosoftware.euclid.experimental.vectorial.rendering.IColorizer;


public abstract class GColorizerAbstract
         implements
            IColorizer {


   protected static void drawShadowString(final Graphics2D g2d,
                                          final String str,
                                          final int x,
                                          final int y,
                                          final Color shadowColor,
                                          final Color color) {
      g2d.setColor(shadowColor);
      g2d.drawString(str, x - 1, y - 1);
      g2d.drawString(str, x - 1, y + 1);
      g2d.drawString(str, x + 1, y - 1);
      g2d.drawString(str, x + 1, y + 1);

      g2d.setColor(color);
      g2d.drawString(str, x, y);
   }

}
