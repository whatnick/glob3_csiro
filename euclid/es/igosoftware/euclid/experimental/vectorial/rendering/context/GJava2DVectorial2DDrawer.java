

package es.igosoftware.euclid.experimental.vectorial.rendering.context;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Paint;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.font.FontRenderContext;
import java.awt.font.LineMetrics;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.RescaleOp;

import es.igosoftware.euclid.bounding.GAxisAlignedOrthotope;
import es.igosoftware.euclid.experimental.vectorial.rendering.utils.GAWTPoints;
import es.igosoftware.euclid.vector.IPointsContainer;
import es.igosoftware.euclid.vector.IVector2;
import es.igosoftware.util.GAssert;
import es.igosoftware.util.GMath;


public class GJava2DVectorial2DDrawer
         implements
            IVectorial2DDrawer {


   private static final AffineTransform IDENTITY_TRANSFORM = new AffineTransform();


   private final Graphics2D             _g2d;
   private final BufferedImage          _image;
   private final boolean                _flippedY;


   public GJava2DVectorial2DDrawer(final BufferedImage image,
                                   final boolean flipY) {
      GAssert.notNull(image, "image");

      _image = image;

      _flippedY = flipY;

      _g2d = initializeG2D(image, flipY);
   }


   private static Graphics2D initializeG2D(final BufferedImage image,
                                           final boolean flipY) {
      final Graphics2D g2d = image.createGraphics();

      g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
      g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
      g2d.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
      g2d.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
      g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
      g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

      if (flipY) {
         final AffineTransform transformFlipY = AffineTransform.getScaleInstance(1, -1);
         transformFlipY.concatenate(AffineTransform.getTranslateInstance(0, -image.getHeight()));
         g2d.setTransform(transformFlipY);
      }

      return g2d;
   }


   @Override
   public void finalize() {
      if (_g2d != null) {
         _g2d.dispose();
      }
   }


   @Override
   public final void draw(final Shape shape,
                          final Paint paint,
                          final Stroke stroke) {
      _g2d.setPaint(paint);
      _g2d.setStroke(stroke);
      _g2d.draw(shape);
   }


   @Override
   public final void fill(final Shape shape,
                          final Paint paint) {
      _g2d.setPaint(paint);
      _g2d.fill(shape);
   }


   @Override
   public final void drawOval(final double x,
                              final double y,
                              final double width,
                              final double height,
                              final Paint paint,
                              final Stroke stroke) {
      draw(new Ellipse2D.Double(x, y, width, height), paint, stroke);
   }


   @Override
   public void drawOval(final IVector2 position,
                        final IVector2 extent,
                        final Paint paint,
                        final Stroke stroke) {
      drawOval(position.x(), position.y(), extent.x(), extent.y(), paint, stroke);
   }


   @Override
   public final void fillOval(final double x,
                              final double y,
                              final double width,
                              final double height,
                              final Paint paint) {
      fill(new Ellipse2D.Double(x, y, width, height), paint);
   }


   @Override
   public void fillOval(final IVector2 position,
                        final IVector2 extent,
                        final Paint paint) {
      fillOval(position.x(), position.y(), extent.x(), extent.y(), paint);
   }


   @Override
   public final void drawPolyline(final int[] xPoints,
                                  final int[] yPoints,
                                  final int length,
                                  final Paint paint,
                                  final Stroke stroke) {
      _g2d.setPaint(paint);
      _g2d.setStroke(stroke);
      _g2d.drawPolyline(xPoints, yPoints, length);
   }


   @Override
   public final void drawPolyline(final GAWTPoints points,
                                  final Paint paint,
                                  final Stroke stroke) {
      drawPolyline(points._xPoints, points._yPoints, points._xPoints.length, paint, stroke);
   }


   @Override
   public void drawPolyline(final IPointsContainer<IVector2> pointsContainer,
                            final Paint paint,
                            final Stroke stroke) {
      final int pointsCount = pointsContainer.getPointsCount();
      final int[] xPoints = new int[pointsCount];
      final int[] yPoints = new int[pointsCount];
      for (int i = 0; i < pointsCount; i++) {
         final IVector2 point = pointsContainer.getPoint(i);
         xPoints[i] = GMath.toRoundedInt(point.x());
         yPoints[i] = GMath.toRoundedInt(point.y());
      }


      drawPolyline(xPoints, yPoints, pointsCount, paint, stroke);
   }


   @Override
   public final void drawRect(final double x,
                              final double y,
                              final double width,
                              final double height,
                              final Paint paint,
                              final Stroke stroke) {
      draw(new Rectangle2D.Double(x, y, width, height), paint, stroke);
   }


   @Override
   public void drawRect(final IVector2 position,
                        final IVector2 extent,
                        final Paint paint,
                        final Stroke stroke) {
      drawRect(position.x(), position.y(), extent.x(), extent.y(), paint, stroke);
   }


   @Override
   public void drawRect(final GAxisAlignedOrthotope<IVector2, ?> rectangle,
                        final Paint paint,
                        final Stroke stroke) {
      drawRect(rectangle._lower, rectangle._extent, paint, stroke);
   }


   @Override
   public void drawRect(final Rectangle2D rectangle,
                        final Paint paint,
                        final Stroke stroke) {
      drawRect(rectangle.getX(), rectangle.getY(), rectangle.getWidth(), rectangle.getHeight(), paint, stroke);
   }


   @Override
   public final void fillRect(final double x,
                              final double y,
                              final double width,
                              final double height,
                              final Paint paint) {
      fill(new Rectangle2D.Double(x, y, width, height), paint);
   }


   @Override
   public void fillRect(final IVector2 position,
                        final IVector2 extent,
                        final Paint paint) {
      fillRect(position.x(), position.y(), extent.x(), extent.y(), paint);
   }


   @Override
   public void fillRect(final GAxisAlignedOrthotope<IVector2, ?> rectangle,
                        final Paint paint) {
      fillRect(rectangle._lower, rectangle._extent, paint);
   }


   @Override
   public void fillRect(final Rectangle2D rectangle,
                        final Paint paint) {
      fillRect(rectangle.getX(), rectangle.getY(), rectangle.getWidth(), rectangle.getHeight(), paint);
   }


   @Override
   public final void drawImage(final Image image,
                               final double x,
                               final double y) {
      if (_flippedY) {
         final AffineTransform currentTransform = _g2d.getTransform();
         _g2d.setTransform(IDENTITY_TRANSFORM);

         final int imageHeight = _image.getHeight();
         _g2d.drawImage(image, GMath.toRoundedInt(x), imageHeight - GMath.toRoundedInt(y) - image.getHeight(null), null);

         _g2d.setTransform(currentTransform);
      }
      else {
         _g2d.drawImage(image, GMath.toRoundedInt(x), GMath.toRoundedInt(y), null);
      }
   }


   @Override
   public final void drawImage(final BufferedImage image,
                               final double x,
                               final double y,
                               final float opacity) {

      if (opacity <= 0) {
         return;
      }

      if (opacity >= 1) {
         drawImage(image, x, y);
      }

      final float[] scales = { 1f, 1f, 1f, opacity };
      final float[] offsets = new float[4];
      final BufferedImageOp rop = new RescaleOp(scales, offsets, null);

      if (_flippedY) {
         final AffineTransform currentTransform = _g2d.getTransform();
         _g2d.setTransform(IDENTITY_TRANSFORM);

         final int imageHeight = _image.getHeight();

         _g2d.drawImage(image, rop, GMath.toRoundedInt(x), imageHeight - GMath.toRoundedInt(y) - image.getHeight());

         _g2d.setTransform(currentTransform);
      }
      else {
         _g2d.drawImage(image, rop, GMath.toRoundedInt(x), GMath.toRoundedInt(y));
      }

   }


   @Override
   public void drawImage(final Image image,
                         final IVector2 position) {
      drawImage(image, position.x(), position.y());
   }


   @Override
   public void drawImage(final BufferedImage image,
                         final IVector2 position,
                         final float opacity) {
      drawImage(image, position.x(), position.y(), opacity);
   }


   @Override
   public void drawString(final String str,
                          final double x,
                          final double y,
                          final Paint paint) {
      _g2d.setPaint(paint);

      if (_flippedY) {
         final AffineTransform currentTransform = _g2d.getTransform();
         _g2d.setTransform(IDENTITY_TRANSFORM);

         final float imageHeight = _image.getHeight();

         final Font f = _g2d.getFont();
         final FontRenderContext frc = _g2d.getFontRenderContext();

         final LineMetrics metrics = f.getLineMetrics(str, frc);

         final float lineheight = metrics.getHeight(); // Total line height
         final double flipedY = imageHeight - (y - lineheight / 2);

         _g2d.drawString(str, (float) x, (float) flipedY);

         _g2d.setTransform(currentTransform);
      }
      else {
         _g2d.drawString(str, (float) x, (float) y);
      }
   }


   @Override
   public void drawString(final String str,
                          final IVector2 position,
                          final Paint paint) {
      drawString(str, position.x(), position.y(), paint);
   }


   @Override
   public void drawShadowedString(final String str,
                                  final IVector2 position,
                                  final Paint paint,
                                  final double shadowOffset,
                                  final Paint shadowPaint) {
      final double x = position.x();
      final double y = position.y();
      if (shadowOffset > 0) {
         drawString(str, x + 1, y - 1, shadowPaint);
         drawString(str, x + 1, y + 1, shadowPaint);
         drawString(str, x - 1, y - 1, shadowPaint);
         drawString(str, x - 1, y + 1, shadowPaint);
      }
      drawString(str, x, y, paint);
   }


   @Override
   public void drawShadowedStringCentered(final String str,
                                          final IVector2 position,
                                          final Paint paint,
                                          final double shadowOffset,
                                          final Paint shadowPaint) {
      drawShadowedStringCentered(str, position, _g2d.getFont(), paint, shadowOffset, shadowPaint);

      //      final Font f = _g2d.getFont();
      //      final FontRenderContext frc = _g2d.getFontRenderContext();
      //      final Rectangle2D bounds = f.getStringBounds(str, frc);
      //      final LineMetrics metrics = f.getLineMetrics(str, frc);
      //      final double width = bounds.getWidth(); // The width of our text
      //      final float lineheight = metrics.getHeight(); // Total line height
      //      final float ascent = metrics.getAscent(); // Top of text to baseline
      //
      //      final double x = (position.x() + (0 - width) / 2);
      //      final double y = (position.y() + (0 - lineheight) / 2 + ascent);
      //
      //      if (shadowOffset > 0) {
      //         drawString(str, x + shadowOffset, y - shadowOffset, shadowPaint);
      //         drawString(str, x + shadowOffset, y + shadowOffset, shadowPaint);
      //         drawString(str, x - shadowOffset, y - shadowOffset, shadowPaint);
      //         drawString(str, x - shadowOffset, y + shadowOffset, shadowPaint);
      //      }
      //      drawString(str, x, y, paint);
   }


   @Override
   public void drawShadowedStringCentered(final String str,
                                          final IVector2 position,
                                          final Font font,
                                          final Paint paint,
                                          final double shadowOffset,
                                          final Paint shadowPaint) {

      final Font currentFont = _g2d.getFont();
      _g2d.setFont(font);

      final FontRenderContext frc = _g2d.getFontRenderContext();
      final Rectangle2D bounds = font.getStringBounds(str, frc);
      final LineMetrics metrics = font.getLineMetrics(str, frc);
      final double width = bounds.getWidth(); // The width of our text
      final float lineheight = metrics.getHeight(); // Total line height
      final float ascent = metrics.getAscent(); // Top of text to baseline

      final double x = (position.x() + (0 - width) / 2);
      final double y = (position.y() + (0 - lineheight) / 2 + ascent);

      if (shadowOffset > 0) {
         drawString(str, x + shadowOffset, y - shadowOffset, shadowPaint);
         drawString(str, x + shadowOffset, y + shadowOffset, shadowPaint);
         drawString(str, x - shadowOffset, y - shadowOffset, shadowPaint);
         drawString(str, x - shadowOffset, y + shadowOffset, shadowPaint);
      }
      drawString(str, x, y, paint);

      _g2d.setFont(currentFont);

   }


   //   public static void main(final String[] args) throws IOException {
   //
   //      final int width = 320;
   //      final int height = 240;
   //      final BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
   //      final GJava2DVectorial2DDrawer drawer = new GJava2DVectorial2DDrawer(image, false);
   //
   //      final double centerX = (width / 3d * 2);
   //      final double centerY = (height / 3d * 2);
   //
   //      final double rectWidth = 20;
   //      drawer.fillRect(centerX - rectWidth / 2, centerY - rectWidth / 2, rectWidth, rectWidth, Color.BLUE);
   //
   //
   //      drawer.drawShadowedStringCentered("Wooo!", new GVector2D(centerX, centerY), Color.WHITE, 1, Color.LIGHT_GRAY);
   //
   //      final double ovalWidth = 2;
   //      drawer.fillOval(centerX - ovalWidth / 2, centerY - ovalWidth / 2, ovalWidth, ovalWidth, Color.RED);
   //
   //
   //      ImageIO.write(image, "png", new File("/home/dgd/Desktop/centeredLabel.png"));
   //   }

}
