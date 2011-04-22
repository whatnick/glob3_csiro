

package es.igosoftware.euclid.experimental.vectorial.rendering;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.RescaleOp;

import es.igosoftware.euclid.bounding.GAxisAlignedRectangle;
import es.igosoftware.euclid.projection.GProjection;
import es.igosoftware.euclid.vector.IPointsContainer;
import es.igosoftware.euclid.vector.IVector2;
import es.igosoftware.util.GMath;


public class GJava2DVectorial2DRenderingContext
         implements
            IVectorial2DRenderingContext {


   private static final AffineTransform IDENTITY_TRANSFORM = new AffineTransform();


   private final IVector2               _scale;
   private final GAxisAlignedRectangle  _region;
   private final GProjection            _projection;
   private final Graphics2D             _g2d;
   private final BufferedImage          _renderedImage;


   GJava2DVectorial2DRenderingContext(final IVector2 scale,
                                      final GAxisAlignedRectangle region,
                                      final GProjection projection,
                                      final Graphics2D g2d,
                                      final BufferedImage renderedImage) {
      _scale = scale;
      _region = region;
      _projection = projection;
      _g2d = g2d;
      _renderedImage = renderedImage;
   }


   @Override
   public GProjection getProjection() {
      return _projection;
   }


   @Override
   public IVector2 scaleExtent(final IVector2 extent) {
      return extent.scale(_scale);
   }


   @Override
   public IVector2 scaleAndTranslatePoint(final IVector2 point) {
      return point.sub(_region._lower).scale(_scale);
   }


   @Override
   public GAWTPoints scaleAndTranslatePoints(final IPointsContainer<IVector2> polygon) {
      final int nPoints = polygon.getPointsCount();
      final int[] xPoints = new int[nPoints];
      final int[] yPoints = new int[nPoints];

      for (int i = 0; i < nPoints; i++) {
         final IVector2 point = scaleAndTranslatePoint(polygon.getPoint(i));

         xPoints[i] = Math.round((float) point.x());
         yPoints[i] = Math.round((float) point.y());
      }

      return new GAWTPoints(xPoints, yPoints);
   }


   //   public void setPixel(final IVector2 scaledPoint,
   //                        final Color color) {
   //
   //      final int imageX = Math.round((float) scaledPoint.x());
   //      final int imageY = Math.round((float) scaledPoint.y());
   //
   //      if ((imageX >= 0) && (imageY >= 0)) {
   //         final int imageWidth = _renderedImage.getWidth();
   //         final int imageHeight = _renderedImage.getHeight();
   //
   //         if ((imageX < imageWidth) && (imageY < imageHeight)) {
   //            final int rotatedImageY = imageHeight - 1 - imageY;
   //
   //            final int oldRGB = _renderedImage.getRGB(imageX, rotatedImageY);
   //            if (oldRGB == 0) {
   //               _renderedImage.setRGB(imageX, rotatedImageY, color.getRGB());
   //            }
   //            else {
   //               final Color oldColor = colorFromRGB(oldRGB);
   //               final Color mixed = mix(oldColor, color);
   //               _renderedImage.setRGB(imageX, rotatedImageY, mixed.getRGB());
   //            }
   //         }
   //      }
   //   }
   //
   //
   //   private Color colorFromRGB(final int rgb) {
   //      final int a = (rgb >>> 24) & 0xFF;
   //      final int r = (rgb >>> 16) & 0xFF;
   //      final int g = (rgb >>> 8) & 0xFF;
   //      final int b = (rgb >>> 0) & 0xFF;
   //      return new Color(r, g, b, a);
   //   }
   //
   //
   //   private static Color mix(final Color colorA,
   //                            final Color colorB) {
   //
   //      final int r = average(colorA.getRed(), colorB.getRed());
   //      final int g = average(colorA.getGreen(), colorB.getGreen());
   //      final int b = average(colorA.getBlue(), colorB.getBlue());
   //      //      final int a = average(colorA.getAlpha(), colorB.getAlpha());
   //      final int a = Math.max(colorA.getAlpha(), colorB.getAlpha());
   //      return new Color(r, g, b, a);
   //   }
   //
   //
   //   private static int average(final int a,
   //                              final int b) {
   //      return (a + b) / 2;
   //   }


   @Override
   public void setColor(final Color color) {
      _g2d.setColor(color);
   }


   @Override
   public void setStroke(final Stroke stroke) {
      _g2d.setStroke(stroke);
   }


   @Override
   public void draw(final Shape shape) {
      _g2d.draw(shape);
   }


   @Override
   public void fill(final Shape shape) {
      _g2d.fill(shape);
   }


   @Override
   public void drawOval(final double x,
                        final double y,
                        final double width,
                        final double height) {
      _g2d.drawOval(GMath.toRoundedInt(x), GMath.toRoundedInt(y), GMath.toRoundedInt(width), GMath.toRoundedInt(height));
   }


   @Override
   public void fillOval(final double x,
                        final double y,
                        final double width,
                        final double height) {
      _g2d.fillOval(GMath.toRoundedInt(x), GMath.toRoundedInt(y), GMath.toRoundedInt(width), GMath.toRoundedInt(height));
   }


   @Override
   public void drawPolyline(final int[] xPoints,
                            final int[] yPoints,
                            final int length) {
      _g2d.drawPolyline(xPoints, yPoints, length);
   }


   @Override
   public void drawPolyline(final GAWTPoints points) {
      drawPolyline(points._xPoints, points._yPoints, points._xPoints.length);
   }


   @Override
   public void drawRect(final double x,
                        final double y,
                        final double width,
                        final double height) {
      _g2d.drawRect(GMath.toRoundedInt(x), GMath.toRoundedInt(y), GMath.toRoundedInt(width), GMath.toRoundedInt(height));
   }


   @Override
   public void fillRect(final double x,
                        final double y,
                        final double width,
                        final double height) {
      _g2d.fillRect(GMath.toRoundedInt(x), GMath.toRoundedInt(y), GMath.toRoundedInt(width), GMath.toRoundedInt(height));
   }


   //   void drawFlippedImage(final Image image,
   //                         final double x,
   //                         final double y,
   //                         final double width,
   //                         final double height) {
   //      _g2d.drawImage(image, toInt(x), toInt(y), toInt(width), toInt(height), null);
   //   }


   @Override
   public void drawImage(final Image image,
                         final double x,
                         final double y,
                         final double width,
                         final double height) {
      final AffineTransform currentTransform = _g2d.getTransform();
      _g2d.setTransform(IDENTITY_TRANSFORM);

      final int imageHeight = _renderedImage.getHeight();
      _g2d.drawImage(image, GMath.toRoundedInt(x), imageHeight - GMath.toRoundedInt(y), GMath.toRoundedInt(width),
               GMath.toRoundedInt(height), null);

      _g2d.setTransform(currentTransform);


      //      _g2d.drawImage(//
      //               image, // 
      //               toInt(x), toInt(y + height), toInt(x + width), toInt(y), //
      //               0, 0, toInt(width), toInt(height), //
      //               null);
   }


   @Override
   public void drawImage(final Image image,
                         final double x,
                         final double y) {
      final AffineTransform currentTransform = _g2d.getTransform();
      _g2d.setTransform(IDENTITY_TRANSFORM);

      final int imageHeight = _renderedImage.getHeight();
      _g2d.drawImage(image, GMath.toRoundedInt(x), imageHeight - GMath.toRoundedInt(y), null);

      _g2d.setTransform(currentTransform);


      //      _g2d.drawImage(//
      //               image, // 
      //               toInt(x), toInt(y + height), toInt(x + width), toInt(y), //
      //               0, 0, toInt(width), toInt(height), //
      //               null);
   }


   @Override
   public void drawImage(final BufferedImage image,
                         final double x,
                         final double y,
                         final float opacity) {

      if (opacity >= 1) {
         drawImage(image, x, y);
      }

      final float[] scales = { 1f, 1f, 1f, opacity };
      final float[] offsets = new float[4];
      final BufferedImageOp rop = new RescaleOp(scales, offsets, null);

      final AffineTransform currentTransform = _g2d.getTransform();
      _g2d.setTransform(IDENTITY_TRANSFORM);

      final int imageHeight = _renderedImage.getHeight();

      _g2d.drawImage(image, rop, GMath.toRoundedInt(x), imageHeight - GMath.toRoundedInt(y));

      _g2d.setTransform(currentTransform);
   }


   @Override
   public void drawFlippedImage(final Image image,
                                final double x,
                                final double y,
                                final double width,
                                final double height,
                                final Color bgColor) {
      _g2d.drawImage(//
               image, // 
               GMath.toRoundedInt(x), GMath.toRoundedInt((y + height)), GMath.toRoundedInt((x + width)), GMath.toRoundedInt(y), //
               0, 0, GMath.toRoundedInt(width), GMath.toRoundedInt(height), //
               bgColor, null);
   }


}
