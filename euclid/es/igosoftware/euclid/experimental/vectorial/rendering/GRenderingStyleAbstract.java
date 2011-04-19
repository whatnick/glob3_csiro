

package es.igosoftware.euclid.experimental.vectorial.rendering;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import es.igosoftware.euclid.IBoundedGeometry;
import es.igosoftware.euclid.bounding.GAxisAlignedRectangle;
import es.igosoftware.euclid.bounding.IFiniteBounds;
import es.igosoftware.euclid.colors.IColor;
import es.igosoftware.euclid.experimental.measurement.GArea;
import es.igosoftware.euclid.experimental.measurement.IMeasure;
import es.igosoftware.euclid.features.IGlobeFeature;
import es.igosoftware.euclid.projection.GProjection;
import es.igosoftware.euclid.vector.IVector2;
import es.igosoftware.euclid.vector.IVectorI2;
import es.igosoftware.util.GMath;


public abstract class GRenderingStyleAbstract
         implements
            IRenderingStyle {


   private static Color mix(final Color colorA,
                            final Color colorB) {

      final int r = average(colorA.getRed(), colorB.getRed());
      final int g = average(colorA.getGreen(), colorB.getGreen());
      final int b = average(colorA.getBlue(), colorB.getBlue());
      //      final int a = average(colorA.getAlpha(), colorB.getAlpha());
      final int a = Math.max(colorA.getAlpha(), colorB.getAlpha());
      return new Color(r, g, b, a);
   }


   private static int average(final int a,
                              final int b) {
      return (a + b) / 2;
   }


   private static void setPixel(final BufferedImage renderedImage,
                                final IVectorI2 point,
                                final Color color) {

      final int x = point.x();
      final int y = point.y();

      if ((x >= 0) && (y >= 0)) {
         final int imageWidth = renderedImage.getWidth();
         final int imageHeight = renderedImage.getHeight();

         if ((x < imageWidth) && (y < imageHeight)) {
            final int rotatedImageY = imageHeight - 1 - y;

            final int oldRGB = renderedImage.getRGB(x, rotatedImageY);
            if (oldRGB == 0) {
               renderedImage.setRGB(x, rotatedImageY, color.getRGB());
            }
            else {
               final Color oldColor = new Color(oldRGB);
               final Color mixed = mix(oldColor, color);
               renderedImage.setRGB(x, rotatedImageY, mixed.getRGB());
            }
         }
      }

   }


   @Override
   public void drawPoint(final Graphics2D g2d,
                         final BufferedImage renderedImage,
                         final IVectorI2 projectedPoint,
                         final IVector2 point,
                         final IGlobeFeature<IVector2, ? extends IBoundedGeometry<IVector2, ? extends IFiniteBounds<IVector2, ?>>> feature,
                         final GAxisAlignedRectangle region,
                         final IVector2 scale,
                         final GProjection projection) {
      //      final float borderWidth = (float) getPointSize().getValue();

      final IMeasure<GArea> pointSize = getPointSize(feature);
      final double area = pointSize.getValue() * pointSize.getUnit().convertionFactor();
      final double radiusD = GMath.sqrt(area / Math.PI);
      final IVector2 pointPlusRadius = increment(point, projection, radiusD, radiusD);
      final IVector2 radius = pointPlusRadius.sub(point).scale(scale);

      //      System.out.println(radius);
      final int width = (int) Math.round(radius.x());
      final int height = (int) Math.round(radius.y());

      if (height * width < getLODMinSize()) {
         if (isRenderLODIgnores() || isDebugRendering()) {
            final Color color = isDebugRendering() ? Color.RED : getPointColor(feature).asAWTColor();

            setPixel(renderedImage, projectedPoint, color);
         }

         return;
      }

      final int centerX = projectedPoint.x() - (width / 2);
      final int centerY = projectedPoint.y() - (height / 2);

      final IColor pointColor = getPointColor(feature);
      final float pointOpacity = getPointOpacity(feature);
      final Color fillColor = pointColor.asAWTColor(pointOpacity);
      final Color borderColor = pointColor.muchDarker().asAWTColor(pointOpacity);


      // fill point
      g2d.setColor(fillColor);
      g2d.fillOval(centerX, centerY, width, height);

      final float borderWidth = 0.5f;
      // render border
      if (borderWidth > 0) {
         final BasicStroke borderStroke = new BasicStroke(borderWidth, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
         g2d.setStroke(borderStroke);

         g2d.setColor(borderColor);
         g2d.drawOval(centerX, centerY, width, height);
      }
   }

}
