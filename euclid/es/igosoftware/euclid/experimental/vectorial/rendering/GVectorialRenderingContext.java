

package es.igosoftware.euclid.experimental.vectorial.rendering;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.image.BufferedImage;

import es.igosoftware.euclid.bounding.GAxisAlignedRectangle;
import es.igosoftware.euclid.projection.GProjection;
import es.igosoftware.euclid.vector.IPointsContainer;
import es.igosoftware.euclid.vector.IVector2;


public class GVectorialRenderingContext {
   private final IVector2              _scale;
   private final GAxisAlignedRectangle _region;
   final GAxisAlignedRectangle         _extendedRegion;
   final GVectorialRenderingAttributes _attributes;
   final IRenderingStyle               _renderingStyle;
   final GProjection                   _projection;
   private final Graphics2D            _g2d;
   private final BufferedImage         _renderedImage;


   GVectorialRenderingContext(final IVector2 scale,
                              final GAxisAlignedRectangle region,
                              final GAxisAlignedRectangle extendedRegion,
                              final GVectorialRenderingAttributes attributes,
                              final IRenderingStyle renderingStyle,
                              final GProjection projection,
                              final Graphics2D g2d,
                              final BufferedImage renderedImage) {
      _scale = scale;
      _region = region;
      _extendedRegion = extendedRegion;
      _attributes = attributes;
      _renderingStyle = renderingStyle;
      _projection = projection;
      _g2d = g2d;
      _renderedImage = renderedImage;
   }


   IVector2 scaleExtent(final IVector2 extent) {
      return extent.scale(_scale);
   }


   IVector2 scaleAndTranslatePoint(final IVector2 point) {
      return point.sub(_region._lower).scale(_scale);
   }


   GAWTPoints getPoints(final IPointsContainer<IVector2> polygon) {
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


   public void setPixel(final IVector2 scaledPoint,
                        final Color color) {

      final int imageX = Math.round((float) scaledPoint.x());
      final int imageY = Math.round((float) scaledPoint.y());

      if ((imageX >= 0) && (imageY >= 0)) {
         final int imageWidth = _renderedImage.getWidth();
         final int imageHeight = _renderedImage.getHeight();

         if ((imageX < imageWidth) && (imageY < imageHeight)) {
            final int rotatedImageY = imageHeight - 1 - imageY;

            final int oldRGB = _renderedImage.getRGB(imageX, rotatedImageY);
            if (oldRGB == 0) {
               _renderedImage.setRGB(imageX, rotatedImageY, color.getRGB());
            }
            else {
               final Color oldColor = new Color(oldRGB);
               final Color mixed = mix(oldColor, color);
               _renderedImage.setRGB(imageX, rotatedImageY, mixed.getRGB());
            }
         }
      }
   }


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


   private static int toInt(final double value) {
      return (int) Math.round(value);
   }


   void setColor(final Color fillColor) {
      _g2d.setColor(fillColor);
   }


   void setStroke(final Stroke stroke) {
      _g2d.setStroke(stroke);
   }


   void draw(final Shape shape) {
      _g2d.draw(shape);
   }


   void fill(final Shape shape) {
      _g2d.fill(shape);
   }


   void drawOval(final double x,
                 final double y,
                 final double width,
                 final double height) {
      _g2d.drawOval(toInt(x), toInt(y), toInt(width), toInt(height));
   }


   void fillOval(final double x,
                 final double y,
                 final double width,
                 final double height) {
      _g2d.fillOval(toInt(x), toInt(y), toInt(width), toInt(height));
   }


   void drawPolyline(final int[] xPoints,
                     final int[] yPoints,
                     final int length) {
      _g2d.drawPolyline(xPoints, yPoints, length);
   }


   void drawPolyline(final GAWTPoints points) {
      drawPolyline(points._xPoints, points._yPoints, points._xPoints.length);
   }


   void drawRect(final double x,
                 final double y,
                 final double width,
                 final double height) {
      _g2d.drawRect(toInt(x), toInt(y), toInt(width), toInt(height));
   }


   void fillRect(final double x,
                 final double y,
                 final double width,
                 final double height) {
      _g2d.fillRect(toInt(x), toInt(y), toInt(width), toInt(height));
   }


   //   void drawImage(final Image image,
   //                  final double x,
   //                  final double y,
   //                  final double width,
   //                  final double height) {
   //      _g2d.drawImage(image, toInt(x), toInt(y), toInt(width), toInt(height), null);
   //   }


   void drawFlippedImage(final Image image,
                         final double x,
                         final double y,
                         final double width,
                         final double height) {

      _g2d.drawImage(//
               image, // 
               toInt(x), toInt(y + height), toInt(x + width), toInt(y), //
               0, 0, toInt(width), toInt(height), //
               null);
   }


   void drawFlippedImage(final Image image,
                         final double x,
                         final double y,
                         final double width,
                         final double height,
                         final Color bgColor) {
      _g2d.drawImage(//
               image, // 
               toInt(x), toInt(y + height), toInt(x + width), toInt(y), //
               0, 0, toInt(width), toInt(height), //
               bgColor, null);
   }


}
