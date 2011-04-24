

package es.igosoftware.euclid.experimental.vectorial.rendering.context;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Image;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import es.igosoftware.euclid.bounding.GAxisAlignedOrthotope;
import es.igosoftware.euclid.experimental.vectorial.rendering.utils.GAWTPoints;
import es.igosoftware.euclid.vector.IVector2;


public interface IVectorial2DDrawer {


   /* -------------------------------------------------------------------------------------- */
   /* Shape drawing */
   public void draw(final Shape shape,
                    final Color color,
                    final Stroke stroke);


   public void fill(final Shape shape,
                    final Color color);


   /* -------------------------------------------------------------------------------------- */
   /* Oval drawing */
   public void drawOval(final double x,
                        final double y,
                        final double width,
                        final double height,
                        final Color color,
                        final Stroke stroke);


   public void drawOval(final IVector2 position,
                        final IVector2 extent,
                        final Color color,
                        final Stroke stroke);


   public void fillOval(final double x,
                        final double y,
                        final double width,
                        final double height,
                        final Color color);


   public void fillOval(final IVector2 position,
                        final IVector2 extent,
                        final Color color);


   /* -------------------------------------------------------------------------------------- */
   /* PolyLine drawing */
   public void drawPolyline(final int[] xPoints,
                            final int[] yPoints,
                            final int length,
                            final Color color,
                            final Stroke stroke);


   public void drawPolyline(final GAWTPoints points,
                            final Color color,
                            final Stroke stroke);


   /* -------------------------------------------------------------------------------------- */
   /* Rect drawing */
   public void drawRect(final double x,
                        final double y,
                        final double width,
                        final double height,
                        final Color color,
                        final Stroke stroke);


   public void drawRect(final IVector2 position,
                        final IVector2 extent,
                        final Color color,
                        final BasicStroke stroke);


   public void drawRect(final GAxisAlignedOrthotope<IVector2, ?> rectangle,
                        final Color color,
                        final BasicStroke stroke);


   public void drawRect(final Rectangle2D rectangle,
                        final Color color,
                        final BasicStroke stroke);


   public void fillRect(final double x,
                        final double y,
                        final double width,
                        final double height,
                        final Color color);


   public void fillRect(final IVector2 position,
                        final IVector2 extent,
                        final Color color);


   public void fillRect(final GAxisAlignedOrthotope<IVector2, ?> rectangle,
                        final Color color);


   public void fillRect(final Rectangle2D rectangle,
                        final Color color);


   /* -------------------------------------------------------------------------------------- */
   /* Image drawing */
   public void drawImage(final Image image,
                         final double x,
                         final double y);


   public void drawImage(final Image image,
                         final IVector2 position);


   public void drawImage(final BufferedImage image,
                         final double x,
                         final double y,
                         final float opacity);


   public void drawImage(final BufferedImage image,
                         final IVector2 position,
                         final float opacity);


}
