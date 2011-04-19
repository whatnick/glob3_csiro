

package es.igosoftware.euclid.experimental.vectorial.rendering;

import java.awt.BasicStroke;
import java.awt.Color;

import es.igosoftware.euclid.vector.IVector2;


public class GEllipseSymbol
         extends
            GSymbol {

   private final IVector2 _center;
   private final IVector2 _radius;


   public GEllipseSymbol(final IVector2 center,
                         final IVector2 radius) {
      _center = center;
      _radius = radius;
   }


   @Override
   public boolean isBiggerThan(final double lodMinSize) {
      return ((_radius.x() * _radius.y()) > lodMinSize);
   }


   @Override
   public void draw(final Color fillColor,
                    final float borderWidth,
                    final Color borderColor,
                    final GVectorialRenderingContext rc) {
      final int width = (int) Math.round(_radius.x());
      final int height = (int) Math.round(_radius.y());

      final int centerX = (int) Math.round(_center.x() - (width / 2));
      final int centerY = (int) Math.round(_center.y() - (height / 2));

      // fill point
      rc._g2d.setColor(fillColor);
      rc._g2d.fillOval(centerX, centerY, width, height);

      // render border
      if (borderWidth > 0) {
         final BasicStroke borderStroke = new BasicStroke(borderWidth, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
         rc._g2d.setStroke(borderStroke);

         rc._g2d.setColor(borderColor);
         rc._g2d.drawOval(centerX, centerY, width, height);
      }
   }


}
