

package es.igosoftware.euclid.utils;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.Collection;

import es.igosoftware.euclid.IGeometry2D;
import es.igosoftware.euclid.bounding.GAxisAlignedRectangle;
import es.igosoftware.euclid.bounding.GDisk;
import es.igosoftware.euclid.shape.IPolygon2D;
import es.igosoftware.euclid.shape.IPolygonalChain2D;
import es.igosoftware.euclid.vector.GVector2D;
import es.igosoftware.euclid.vector.IPointsContainer;
import es.igosoftware.euclid.vector.IVector2;
import es.igosoftware.euclid.vector.IVectorI2;
import es.igosoftware.util.GMath;


public class GGeometry2DRenderer {


   private final GAxisAlignedRectangle _bounds;
   private final IVectorI2             _imageSize;
   private final GVector2D             _scale;
   private final BufferedImage         _image;
   private final Graphics2D            _g2d;


   public GGeometry2DRenderer(final GAxisAlignedRectangle bounds,
                              final IVectorI2 imageSize) {
      _bounds = bounds;
      _imageSize = imageSize;

      _scale = new GVector2D(imageSize.x(), imageSize.y()).div(bounds.getExtent());

      _image = new BufferedImage(imageSize.x(), imageSize.y(), BufferedImage.TYPE_4BYTE_ABGR_PRE);

      _g2d = _image.createGraphics();
      _g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
      _g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
      _g2d.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
      _g2d.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
      _g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
      _g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

      _g2d.setBackground(Color.BLACK);
      _g2d.clearRect(0, 0, imageSize.x(), imageSize.y());

      _g2d.setColor(Color.WHITE);
      _g2d.drawString("bounds: " + bounds.asParseableString(), 0, imageSize.y());
   }


   private final IVector2 scaleAndTranslate(final IVector2 point) {
      //      return point.sub(bounds._lower).scale(scale);

      final IVector2 scaled = point.sub(_bounds._lower).scale(_scale);
      return new GVector2D(scaled.x(), _imageSize.y() - scaled.y());

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


   private AWTPoints getTranslatedAWTPoints(final IPointsContainer<IVector2> container) {
      final int[] xPoints = new int[container.getPointsCount()];
      final int[] yPoints = new int[container.getPointsCount()];

      for (int i = 0; i < container.getPointsCount(); i++) {
         final IVector2 scaledPoint = scaleAndTranslate(container.getPoint(i));
         xPoints[i] = GMath.toRoundedInt(scaledPoint.x());
         yPoints[i] = GMath.toRoundedInt(scaledPoint.y());
      }

      return new AWTPoints(xPoints, yPoints);
   }


   private void drawVertices(final IPointsContainer<IVector2> container) {
      _g2d.setColor(Color.WHITE);

      for (final IVector2 vector : container) {
         final IVector2 scaledVector = scaleAndTranslate(vector);
         _g2d.fillOval(GMath.toRoundedInt(scaledVector.x() - 2), GMath.toRoundedInt(scaledVector.y() - 2), 4, 4);
      }
   }


   public void drawGeometries(final Collection<? extends IGeometry2D> geometries,
                              final boolean drawVertices) {
      for (final IGeometry2D geometry : geometries) {
         drawGeometry(geometry, null, drawVertices);
      }
   }


   public void drawGeometry(final IGeometry2D geometry,
                            final Color color,
                            final boolean drawVertices) {
      if (geometry instanceof IVector2) {
         final IVector2 vector = (IVector2) geometry;

         final IVector2 scaledVector = scaleAndTranslate(vector);
         _g2d.setColor((color != null) ? color : getPointColor());
         _g2d.fillOval(GMath.toRoundedInt(scaledVector.x() - 1), GMath.toRoundedInt(scaledVector.y() - 1), 2, 2);
      }
      else if (geometry instanceof IPolygonalChain2D) {
         final IPolygonalChain2D polygonalChain = (IPolygonalChain2D) geometry;

         _g2d.setColor((color != null) ? color : getPolygonalChainColor());

         final AWTPoints awtPoints = getTranslatedAWTPoints(polygonalChain);
         _g2d.drawPolyline(awtPoints._xPoints, awtPoints._yPoints, awtPoints._xPoints.length);
      }
      else if (geometry instanceof IPolygon2D) {
         final IPolygon2D polygon = (IPolygon2D) geometry;
         _g2d.setColor((color != null) ? color : getPolygonColor());

         final AWTPoints awtPoints = getTranslatedAWTPoints(polygon);
         _g2d.fillPolygon(awtPoints._xPoints, awtPoints._yPoints, awtPoints._xPoints.length);

         _g2d.setColor(_g2d.getColor().darker().darker().darker());
         _g2d.drawPolygon(awtPoints._xPoints, awtPoints._yPoints, awtPoints._xPoints.length);
      }
      else if (geometry instanceof GAxisAlignedRectangle) {
         final GAxisAlignedRectangle rectangle = (GAxisAlignedRectangle) geometry;

         _g2d.setColor((color != null) ? color : getRectangleColor());

         final IVector2 scaledLower = scaleAndTranslate(rectangle._lower);
         final IVector2 scaledUpper = scaleAndTranslate(rectangle._upper);
         _g2d.fillRect(//
                  GMath.toRoundedInt(scaledLower.x()), GMath.toRoundedInt(scaledLower.y()), //
                  GMath.toRoundedInt(scaledUpper.x() - scaledLower.x()), GMath.toRoundedInt(scaledUpper.y() - scaledLower.y()) //
         );


         _g2d.setColor(_g2d.getColor().darker().darker().darker());
         _g2d.drawRect(//
                  GMath.toRoundedInt(scaledLower.x()), GMath.toRoundedInt(scaledLower.y()), //
                  GMath.toRoundedInt(scaledUpper.x() - scaledLower.x()), GMath.toRoundedInt(scaledUpper.y() - scaledLower.y()) //
         );
      }
      else if (geometry instanceof GDisk) {
         final GDisk disk = (GDisk) geometry;

         final IVector2 centerPlusRadius = disk._center.add(disk._radius);
         final IVector2 scaledRadius = centerPlusRadius.sub(disk._center).scale(_scale); // radius times 2 (for extent)

         final IVector2 scaledPosition = scaleAndTranslate(disk._center).sub(scaledRadius);

         final IVector2 scaledExtent = scaledRadius.scale(2);

         _g2d.setColor((color != null) ? color : getDiskColor());
         _g2d.fillOval(GMath.toRoundedInt(scaledPosition.x()), GMath.toRoundedInt(scaledPosition.y()), //
                  GMath.toRoundedInt(scaledExtent.x()), GMath.toRoundedInt(scaledExtent.y()));

         _g2d.setColor(_g2d.getColor().darker().darker().darker());
         _g2d.drawOval(GMath.toRoundedInt(scaledPosition.x()), GMath.toRoundedInt(scaledPosition.y()), //
                  GMath.toRoundedInt(scaledExtent.x()), GMath.toRoundedInt(scaledExtent.y()));

         if (drawVertices) {
            final IVector2 scaledCenter = scaleAndTranslate(disk._center);
            _g2d.fillOval(GMath.toRoundedInt(scaledCenter.x() - 1), GMath.toRoundedInt(scaledCenter.y() - 1), 2, 2);
         }

      }
      else {
         throw new RuntimeException("Geometry type not yet supported (" + geometry.getClass() + ")");
      }


      if (geometry instanceof IPointsContainer) {
         if (drawVertices) {
            @SuppressWarnings("unchecked")
            final IPointsContainer<IVector2> container = (IPointsContainer<IVector2>) geometry;
            drawVertices(container);
         }
      }
   }


   public void drawString(final String string,
                          final IVector2 position,
                          final Color color) {
      final IVector2 scaledPosition = scaleAndTranslate(position);
      _g2d.setColor((color != null) ? color : Color.WHITE);
      _g2d.drawString(string, GMath.toRoundedInt(scaledPosition.x()), GMath.toRoundedInt(scaledPosition.y()));
   }


   private static Color getDiskColor() {
      return new Color(0, 0, 255, 64);
   }


   private static Color getRectangleColor() {
      return new Color(255, 255, 0, 64);
   }


   //   public static void render(final Collection<? extends IGeometry2D> geometries,
   //                             final boolean drawVertices,
   //                             final GAxisAlignedRectangle bounds,
   //                             final IVectorI2 imageSize,
   //                             final GFileName fileName) throws IOException {
   //      final BufferedImage image = render(geometries, drawVertices, bounds, imageSize);
   //
   //      ImageIO.write(image, "png", fileName.asFile());
   //   }


   private static Color getPolygonalChainColor() {
      return new Color(255, 255, 255, 255);
   }


   private static Color getPolygonColor() {
      return new Color(255, 0, 255, 64);
   }


   private static Color getPointColor() {
      return new Color(255, 0, 0, 255);
   }


   public BufferedImage getImage() {
      return _image;
   }


}
