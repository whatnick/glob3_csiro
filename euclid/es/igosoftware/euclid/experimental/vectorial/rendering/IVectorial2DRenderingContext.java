

package es.igosoftware.euclid.experimental.vectorial.rendering;

import java.awt.Color;
import java.awt.Image;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.image.BufferedImage;

import es.igosoftware.euclid.projection.GProjection;
import es.igosoftware.euclid.vector.IPointsContainer;
import es.igosoftware.euclid.vector.IVector2;


public interface IVectorial2DRenderingContext {

   /* coordinate transformation methods*/
   public GAWTPoints scaleAndTranslatePoints(final IPointsContainer<IVector2> pointsContainer);


   public IVector2 scaleExtent(final IVector2 extent);


   public IVector2 scaleAndTranslatePoint(final IVector2 point);


   public GProjection getProjection();


   /* drawing methods */
   public void setColor(final Color fillColor);


   public void setStroke(final Stroke stroke);


   public void draw(final Shape shape);


   public void fill(final Shape shape);


   public void drawOval(final double x,
                        final double y,
                        final double width,
                        final double height);


   public void fillOval(final double x,
                        final double y,
                        final double width,
                        final double height);


   public void drawPolyline(final int[] xPoints,
                            final int[] yPoints,
                            final int length);


   public void drawPolyline(final GAWTPoints points);


   public void drawRect(final double x,
                        final double y,
                        final double width,
                        final double height);


   public void fillRect(final double x,
                        final double y,
                        final double width,
                        final double height);


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
