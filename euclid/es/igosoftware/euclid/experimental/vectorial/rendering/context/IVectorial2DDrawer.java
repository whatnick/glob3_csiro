

package es.igosoftware.euclid.experimental.vectorial.rendering.context;

import java.awt.Color;
import java.awt.Image;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.image.BufferedImage;

import es.igosoftware.euclid.experimental.vectorial.rendering.utils.GAWTPoints;


public interface IVectorial2DDrawer {


   public void draw(final Shape shape,
                    final Color color,
                    final Stroke stroke);


   public void fill(final Shape shape,
                    final Color color);


   public void drawOval(final double x,
                        final double y,
                        final double width,
                        final double height,
                        final Color color,
                        final Stroke stroke);


   public void fillOval(final double x,
                        final double y,
                        final double width,
                        final double height,
                        final Color color);


   public void drawPolyline(final int[] xPoints,
                            final int[] yPoints,
                            final int length,
                            final Color color,
                            final Stroke stroke);


   public void drawPolyline(final GAWTPoints points,
                            final Color color,
                            final Stroke stroke);


   public void drawRect(final double x,
                        final double y,
                        final double width,
                        final double height,
                        final Color color,
                        final Stroke stroke);


   public void fillRect(final double x,
                        final double y,
                        final double width,
                        final double height,
                        final Color color);


   public void drawImage(final Image image,
                         final double x,
                         final double y,
                         final double width,
                         final double height);


   public void drawImage(final Image image,
                         final double x,
                         final double y);


   public void drawImage(final BufferedImage image,
                         final double x,
                         final double y,
                         final float opacity);


   public void drawFlippedImage(final Image image,
                                final double x,
                                final double y,
                                final double width,
                                final double height,
                                final Color bgColor);


}
