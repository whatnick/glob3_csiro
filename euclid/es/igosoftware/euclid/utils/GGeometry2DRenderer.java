

package es.igosoftware.euclid.utils;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import javax.imageio.ImageIO;

import es.igosoftware.euclid.IGeometry2D;
import es.igosoftware.euclid.bounding.GAxisAlignedRectangle;
import es.igosoftware.euclid.shape.IPolygonalChain2D;
import es.igosoftware.euclid.vector.GVector2D;
import es.igosoftware.euclid.vector.GVector2I;
import es.igosoftware.euclid.vector.IVector2;
import es.igosoftware.euclid.vector.IVectorI2;
import es.igosoftware.util.GMath;


public class GGeometry2DRenderer {


   private GGeometry2DRenderer() {

   }


   private static final IVector2 scaleAndTranslate(final IVector2 point,
                                                   final GAxisAlignedRectangle bounds,
                                                   final IVector2 scale) {
      return point.sub(bounds._lower).scale(scale);
   }


   public static BufferedImage render(final Collection<? extends IGeometry2D> geometries,
                                      final GAxisAlignedRectangle bounds,
                                      final IVectorI2 imageSize) {

      final IVector2 scale = new GVector2D(imageSize.x(), imageSize.y()).div(bounds.getExtent());

      final BufferedImage image = new BufferedImage(imageSize.x(), imageSize.y(), BufferedImage.TYPE_4BYTE_ABGR_PRE);


      final Graphics2D g2d = image.createGraphics();
      g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
      g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
      g2d.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
      g2d.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
      g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
      g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);


      g2d.setBackground(Color.BLACK);
      g2d.clearRect(0, 0, imageSize.x(), imageSize.y());

      g2d.setColor(Color.WHITE);
      g2d.drawString(bounds.asParseableString(), 0, imageSize.y());

      for (final IGeometry2D geometry : geometries) {
         if (geometry instanceof IVector2) {
            final IVector2 vector = (IVector2) geometry;

            final IVector2 scaledVector = scaleAndTranslate(vector, bounds, scale);
            g2d.setColor(getPointColor());
            g2d.fillOval(GMath.toRoundedInt(scaledVector.x()) - 1, GMath.toRoundedInt(scaledVector.y()) - 1, 2, 2);
         }
         else if (geometry instanceof IPolygonalChain2D) {
            final IPolygonalChain2D polygonalChain = (IPolygonalChain2D) geometry;

            final int[] xPoints = new int[polygonalChain.getPointsCount()];
            final int[] yPoints = new int[polygonalChain.getPointsCount()];

            for (int i = 0; i < polygonalChain.getPointsCount(); i++) {
               final IVector2 scaledPoint = scaleAndTranslate(polygonalChain.getPoint(i), bounds, scale);
               xPoints[i] = GMath.toRoundedInt(scaledPoint.x());
               yPoints[i] = GMath.toRoundedInt(scaledPoint.y());
            }
            g2d.setColor(getPolygonalChainColor());
            g2d.drawPolyline(xPoints, yPoints, xPoints.length);
         }
         else {
            throw new RuntimeException("Geometry type not yet supported (" + geometry.getClass() + ")");
         }
      }

      g2d.dispose();

      return image;
   }


   private static Color getPolygonalChainColor() {
      return new Color(255, 255, 255, 127);
   }


   private static Color getPointColor() {
      return new Color(255, 0, 0, 127);
   }


   public static void main(final String[] args) throws IOException {
      System.out.println("GGeometryRenderer 0.1");
      System.out.println("---------------------");


      final List<GVector2D> points = Arrays.asList(new GVector2D(100, 100), new GVector2D(200, 200), new GVector2D(100, 200),
               new GVector2D(200, 100));

      //      final GAxisAlignedRectangle bounds = GAxisAlignedRectangle.minimumBoundingRectangle(points);
      final GAxisAlignedRectangle bounds = new GAxisAlignedRectangle(new GVector2D(0, 0), new GVector2D(300, 300));

      final BufferedImage image = render(points, bounds, new GVector2I(640, 480));

      ImageIO.write(image, "png", new File("/home/dgd/Escritorio/GGeometryRenderer.png"));
   }


}
