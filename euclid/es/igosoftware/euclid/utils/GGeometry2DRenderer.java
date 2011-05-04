

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
import es.igosoftware.euclid.shape.IPolygon2D;
import es.igosoftware.euclid.shape.IPolygonalChain2D;
import es.igosoftware.euclid.vector.GVector2D;
import es.igosoftware.euclid.vector.GVector2I;
import es.igosoftware.euclid.vector.IPointsContainer;
import es.igosoftware.euclid.vector.IVector2;
import es.igosoftware.euclid.vector.IVectorI2;
import es.igosoftware.io.GFileName;
import es.igosoftware.util.GMath;


public class GGeometry2DRenderer {


   private GGeometry2DRenderer() {

   }


   private static final IVector2 scaleAndTranslate(final IVector2 point,
                                                   final GAxisAlignedRectangle bounds,
                                                   final IVector2 scale,
                                                   final IVectorI2 imageSize) {
      //      return point.sub(bounds._lower).scale(scale);

      final IVector2 scaled = point.sub(bounds._lower).scale(scale);
      return new GVector2D(scaled.x(), imageSize.y() - scaled.y());

   }


   private static class AWTPoints {
      private final int[] _xPoints;
      private final int[] _yPoints;


      private AWTPoints(final int[] xPoints,
                        final int[] yPoints) {
         super();
         _xPoints = xPoints;
         _yPoints = yPoints;
      }
   }


   private static AWTPoints getTranslatedAWTPoints(final IPointsContainer<IVector2> container,
                                                   final GAxisAlignedRectangle bounds,
                                                   final IVector2 scale,
                                                   final IVectorI2 imageSize) {
      final int[] xPoints = new int[container.getPointsCount()];
      final int[] yPoints = new int[container.getPointsCount()];

      for (int i = 0; i < container.getPointsCount(); i++) {
         final IVector2 scaledPoint = scaleAndTranslate(container.getPoint(i), bounds, scale, imageSize);
         xPoints[i] = GMath.toRoundedInt(scaledPoint.x());
         yPoints[i] = GMath.toRoundedInt(scaledPoint.y());
      }

      return new AWTPoints(xPoints, yPoints);
   }


   private static void drawVertices(final Graphics2D g2d,
                                    final IPointsContainer<IVector2> container,
                                    final GAxisAlignedRectangle bounds,
                                    final IVector2 scale,
                                    final IVectorI2 imageSize) {
      g2d.setColor(Color.WHITE);

      for (final IVector2 vector : container) {
         final IVector2 scaledVector = scaleAndTranslate(vector, bounds, scale, imageSize);
         g2d.fillOval(GMath.toRoundedInt(scaledVector.x() - 2), GMath.toRoundedInt(scaledVector.y() - 2), 4, 4);
      }
   }


   public static BufferedImage render(final Collection<? extends IGeometry2D> geometries,
                                      final boolean drawVertices,
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
      g2d.drawString("bounds: " + bounds.asParseableString(), 0, imageSize.y());


      //      final AffineTransform transform = new AffineTransform();
      //      transform.concatenate(AffineTransform.getScaleInstance(1, -1));
      //      transform.concatenate(AffineTransform.getTranslateInstance(0, -imageSize.y()));
      //      g2d.setTransform(transform);


      for (final IGeometry2D geometry : geometries) {


         if (geometry instanceof IVector2) {
            final IVector2 vector = (IVector2) geometry;

            final IVector2 scaledVector = scaleAndTranslate(vector, bounds, scale, imageSize);
            g2d.setColor(getPointColor());
            g2d.fillOval(GMath.toRoundedInt(scaledVector.x() - 1), GMath.toRoundedInt(scaledVector.y() - 1), 2, 2);
         }
         else if (geometry instanceof IPolygonalChain2D) {
            final IPolygonalChain2D polygonalChain = (IPolygonalChain2D) geometry;

            g2d.setColor(getPolygonalChainColor());

            final AWTPoints awtPoints = getTranslatedAWTPoints(polygonalChain, bounds, scale, imageSize);
            g2d.drawPolyline(awtPoints._xPoints, awtPoints._yPoints, awtPoints._xPoints.length);
         }
         else if (geometry instanceof IPolygon2D) {
            final IPolygon2D polygon = (IPolygon2D) geometry;
            g2d.setColor(getPolygonColor());

            final AWTPoints awtPoints = getTranslatedAWTPoints(polygon, bounds, scale, imageSize);
            g2d.fillPolygon(awtPoints._xPoints, awtPoints._yPoints, awtPoints._xPoints.length);

            g2d.setColor(getPolygonColor().darker().darker().darker());
            g2d.drawPolygon(awtPoints._xPoints, awtPoints._yPoints, awtPoints._xPoints.length);
         }
         else if (geometry instanceof GAxisAlignedRectangle) {
            final GAxisAlignedRectangle rectangle = (GAxisAlignedRectangle) geometry;

            g2d.setColor(getRectangleColor());

            final IVector2 scaledLower = scaleAndTranslate(rectangle._lower, bounds, scale, imageSize);
            final IVector2 scaledUpper = scaleAndTranslate(rectangle._upper, bounds, scale, imageSize);
            g2d.fillRect(//
                     GMath.toRoundedInt(scaledLower.x()), GMath.toRoundedInt(scaledLower.y()), //
                     GMath.toRoundedInt(scaledUpper.x() - scaledLower.x()), GMath.toRoundedInt(scaledUpper.y() - scaledLower.y()) //
            );


            g2d.setColor(getRectangleColor().darker().darker().darker());
            g2d.drawRect(//
                     GMath.toRoundedInt(scaledLower.x()), GMath.toRoundedInt(scaledLower.y()), //
                     GMath.toRoundedInt(scaledUpper.x() - scaledLower.x()), GMath.toRoundedInt(scaledUpper.y() - scaledLower.y()) //
            );
         }
         else {
            throw new RuntimeException("Geometry type not yet supported (" + geometry.getClass() + ")");
         }


         if (geometry instanceof IPointsContainer) {
            if (drawVertices) {
               @SuppressWarnings("unchecked")
               final IPointsContainer<IVector2> container = (IPointsContainer<IVector2>) geometry;
               drawVertices(g2d, container, bounds, scale, imageSize);
            }
         }
      }

      g2d.dispose();

      return image;
   }


   private static Color getRectangleColor() {
      return new Color(255, 255, 0, 64);
   }


   public static void render(final Collection<? extends IGeometry2D> geometries,
                             final boolean drawVertices,
                             final GAxisAlignedRectangle bounds,
                             final IVectorI2 imageSize,
                             final GFileName fileName) throws IOException {
      final BufferedImage image = render(geometries, drawVertices, bounds, imageSize);

      ImageIO.write(image, "png", fileName.asFile());
   }


   private static Color getPolygonalChainColor() {
      return new Color(255, 255, 255, 255);
   }


   private static Color getPolygonColor() {
      return new Color(255, 0, 255, 64);
   }


   private static Color getPointColor() {
      return new Color(255, 0, 0, 255);
   }


   public static void main(final String[] args) throws IOException {
      System.out.println("GGeometryRenderer 0.1");
      System.out.println("---------------------");


      final List<GVector2D> points = Arrays.asList(new GVector2D(100, 100), new GVector2D(200, 200), new GVector2D(100, 200),
               new GVector2D(200, 100));

      //      final GAxisAlignedRectangle bounds = GAxisAlignedRectangle.minimumBoundingRectangle(points);
      final GAxisAlignedRectangle bounds = new GAxisAlignedRectangle(new GVector2D(0, 0), new GVector2D(300, 300));

      final BufferedImage image = render(points, true, bounds, new GVector2I(640, 480));

      ImageIO.write(image, "png", new File("/home/dgd/Escritorio/GGeometryRenderer.png"));
   }


}
