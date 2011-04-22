

package es.igosoftware.euclid.experimental.vectorial.rendering.context;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.RescaleOp;

import es.igosoftware.euclid.experimental.vectorial.rendering.utils.GAWTPoints;
import es.igosoftware.util.GAssert;
import es.igosoftware.util.GMath;


public class GJava2DVectorial2DDrawer
         implements
            IVectorial2DDrawer {


   private static final AffineTransform IDENTITY_TRANSFORM = new AffineTransform();


   private final Graphics2D             _g2d;
   private final BufferedImage          _image;


   public GJava2DVectorial2DDrawer(final BufferedImage image) {
      GAssert.notNull(image, "image");

      _image = image;

      _g2d = initializeG2D(image);
   }


   private Graphics2D initializeG2D(final BufferedImage image) {
      final Graphics2D g2d = image.createGraphics();

      g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
      g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
      g2d.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
      g2d.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
      g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
      g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

      final AffineTransform transformFlipY = AffineTransform.getScaleInstance(1, -1);
      transformFlipY.concatenate(AffineTransform.getTranslateInstance(0, -image.getHeight()));
      g2d.setTransform(transformFlipY);

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
                          final Color color,
                          final Stroke stroke) {
      _g2d.setColor(color);
      _g2d.setStroke(stroke);
      _g2d.draw(shape);
   }


   @Override
   public final void fill(final Shape shape,
                          final Color color) {
      _g2d.setColor(color);
      _g2d.fill(shape);
   }


   @Override
   public final void drawOval(final double x,
                              final double y,
                              final double width,
                              final double height,
                              final Color color,
                              final Stroke stroke) {
      draw(new Ellipse2D.Double(x, y, width, height), color, stroke);
   }


   @Override
   public final void fillOval(final double x,
                              final double y,
                              final double width,
                              final double height,
                              final Color color) {
      fill(new Ellipse2D.Double(x, y, width, height), color);
   }


   @Override
   public final void drawPolyline(final int[] xPoints,
                                  final int[] yPoints,
                                  final int length,
                                  final Color color,
                                  final Stroke stroke) {
      _g2d.setColor(color);
      _g2d.setStroke(stroke);
      _g2d.drawPolyline(xPoints, yPoints, length);
   }


   @Override
   public final void drawPolyline(final GAWTPoints points,
                                  final Color color,
                                  final Stroke stroke) {
      drawPolyline(points._xPoints, points._yPoints, points._xPoints.length, color, stroke);
   }


   @Override
   public final void drawRect(final double x,
                              final double y,
                              final double width,
                              final double height,
                              final Color color,
                              final Stroke stroke) {
      draw(new Rectangle2D.Double(x, y, width, height), color, stroke);
   }


   @Override
   public final void fillRect(final double x,
                              final double y,
                              final double width,
                              final double height,
                              final Color color) {
      fill(new Rectangle2D.Double(x, y, width, height), color);
   }


   @Override
   public final void drawImage(final Image image,
                               final double x,
                               final double y) {
      final AffineTransform currentTransform = _g2d.getTransform();
      _g2d.setTransform(IDENTITY_TRANSFORM);

      final int imageHeight = _image.getHeight();
      _g2d.drawImage(image, GMath.toRoundedInt(x), imageHeight - GMath.toRoundedInt(y), null);

      _g2d.setTransform(currentTransform);
   }


   @Override
   public final void drawImage(final BufferedImage image,
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

      final int imageHeight = _image.getHeight();

      _g2d.drawImage(image, rop, GMath.toRoundedInt(x), imageHeight - GMath.toRoundedInt(y));

      _g2d.setTransform(currentTransform);
   }


}
