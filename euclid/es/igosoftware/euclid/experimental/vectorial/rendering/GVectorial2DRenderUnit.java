

package es.igosoftware.euclid.experimental.vectorial.rendering;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.image.BufferedImage;
import java.util.Arrays;

import es.igosoftware.euclid.IBoundedGeometry;
import es.igosoftware.euclid.bounding.GAxisAlignedOrthotope;
import es.igosoftware.euclid.bounding.GAxisAlignedRectangle;
import es.igosoftware.euclid.bounding.IFiniteBounds;
import es.igosoftware.euclid.features.IGlobeFeature;
import es.igosoftware.euclid.multigeometry.GMultiGeometry2D;
import es.igosoftware.euclid.ntree.GElementGeometryPair;
import es.igosoftware.euclid.ntree.GGTInnerNode;
import es.igosoftware.euclid.ntree.GGTNode;
import es.igosoftware.euclid.shape.IComplexPolygon2D;
import es.igosoftware.euclid.shape.IPolygon2D;
import es.igosoftware.euclid.shape.IPolygonalChain2D;
import es.igosoftware.euclid.shape.ISimplePolygon2D;
import es.igosoftware.euclid.vector.GVector2D;
import es.igosoftware.euclid.vector.IPointsContainer;
import es.igosoftware.euclid.vector.IVector2;


class GVectorial2DRenderUnit
         implements
            IVectorial2DRenderUnit {


   @Override
   public BufferedImage render(final GRenderingQuadtree<IGlobeFeature<IVector2, ? extends IBoundedGeometry<IVector2, ? extends IFiniteBounds<IVector2, ?>>>> quadtree,
                               final GAxisAlignedRectangle region,
                               final GVectorialRenderingAttributes attributes) {

      final IVector2 extent = region.getExtent();

      final int width = attributes._imageWidth;
      final int height = attributes._imageHeight;

      final IVector2 scale = new GVector2D(width, height).div(extent);

      final BufferedImage renderedImage = new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR);
      renderedImage.setAccelerationPriority(1);

      final Graphics2D g2d = renderedImage.createGraphics();
      g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
      g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
      //      g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);

      if (attributes._renderBounds) {
         g2d.setColor(Color.YELLOW);
         g2d.setStroke(new BasicStroke(2));

         g2d.drawRect(0, 0, width, height);

         //         g2d.drawString(region._lower.toString(), 10, 10);
         //         g2d.drawString(" " + region._upper, 10, 20);
      }

      final AffineTransform transformFlipY = AffineTransform.getScaleInstance(1, -1);
      transformFlipY.concatenate(AffineTransform.getTranslateInstance(0, -height));

      g2d.setTransform(transformFlipY);

      processNode(quadtree.getRoot(), quadtree, region, attributes, scale, g2d, renderedImage);

      return renderedImage;
   }


   private void processNode(final GGTNode<IVector2, IGlobeFeature<IVector2, ? extends IBoundedGeometry<IVector2, ? extends IFiniteBounds<IVector2, ?>>>> node,
                            final GRenderingQuadtree<IGlobeFeature<IVector2, ? extends IBoundedGeometry<IVector2, ? extends IFiniteBounds<IVector2, ?>>>> quadtree,
                            final GAxisAlignedRectangle region,
                            final GVectorialRenderingAttributes attributes,
                            final IVector2 scale,
                            final Graphics2D g2d,
                            final BufferedImage renderedImage) {

      final GAxisAlignedRectangle nodeBounds = node.getBounds().asRectangle();

      if (!nodeBounds.touches(region)) {
         return;
      }


      final IVector2 scaledNodeExtent = nodeBounds.getExtent().scale(scale);
      final double projectedSize = scaledNodeExtent.x() * scaledNodeExtent.y();
      if (projectedSize <= attributes._lodMinSize) {
         if (attributes._renderLODIgnores || attributes._debugLODRendering) {
            final Color color = attributes._debugLODRendering ? Color.RED : attributes._lodColor;

            final IVector2 projectedCenter = nodeBounds.getCenter().sub(region._lower).scale(scale);
            setPixel(renderedImage, projectedCenter, color);
         }

         return;
      }


      if (node instanceof GGTInnerNode) {
         final GGTInnerNode<IVector2, IGlobeFeature<IVector2, ? extends IBoundedGeometry<IVector2, ? extends IFiniteBounds<IVector2, ?>>>> inner;
         inner = (GGTInnerNode<IVector2, IGlobeFeature<IVector2, ? extends IBoundedGeometry<IVector2, ? extends IFiniteBounds<IVector2, ?>>>>) node;

         for (final GGTNode<IVector2, IGlobeFeature<IVector2, ? extends IBoundedGeometry<IVector2, ? extends IFiniteBounds<IVector2, ?>>>> child : inner.getChildren()) {
            processNode(child, quadtree, region, attributes, scale, g2d, renderedImage);
         }
      }

      renderNodeGeometries(node, region, attributes, scale, g2d, renderedImage);
   }


   private void renderNodeGeometries(final GGTNode<IVector2, IGlobeFeature<IVector2, ? extends IBoundedGeometry<IVector2, ? extends IFiniteBounds<IVector2, ?>>>> node,
                                     final GAxisAlignedRectangle region,
                                     final GVectorialRenderingAttributes attributes,
                                     final IVector2 scale,
                                     final Graphics2D g2d,
                                     final BufferedImage renderedImage) {


      if (attributes._renderBounds) {
         final GAxisAlignedOrthotope<IVector2, ?> nodeBounds = node.getBounds();

         final IVector2 nodeLower = nodeBounds._lower.sub(region._lower).scale(scale);
         final IVector2 nodeUpper = nodeBounds._upper.sub(region._lower).scale(scale);

         g2d.setStroke(new BasicStroke(0.25f));
         g2d.setColor(Color.GREEN);
         final int x = Math.round((float) nodeLower.x());
         final int y = Math.round((float) nodeLower.y());
         final int width = Math.round((float) (nodeUpper.x() - nodeLower.x()));
         final int height = Math.round((float) (nodeUpper.y() - nodeLower.y()));
         g2d.drawRect(x, y, width, height);
      }


      for (final GElementGeometryPair<IVector2, IGlobeFeature<IVector2, ? extends IBoundedGeometry<IVector2, ? extends IFiniteBounds<IVector2, ?>>>> feature : node.getElements()) {
         final IBoundedGeometry<IVector2, ? extends IFiniteBounds<IVector2, ?>> geometry = feature.getGeometry();
         if (geometry.getBounds().asAxisAlignedOrthotope().touches(region)) {
            renderGeometry(geometry, scale, renderedImage, g2d, region, attributes);
         }
      }

   }


   private static void setPixel(final BufferedImage renderedImage,
                                final IVector2 point,
                                final Color color) {

      final int imageX = Math.round((float) point.x());
      final int imageY = Math.round((float) point.y());

      if ((imageX >= 0) && (imageY >= 0)) {
         final int imageWidth = renderedImage.getWidth();
         final int imageHeight = renderedImage.getHeight();

         if ((imageX < imageWidth) && (imageY < imageHeight)) {
            final int rotatedImageY = imageHeight - 1 - imageY;

            final int oldRGB = renderedImage.getRGB(imageX, rotatedImageY);
            if (oldRGB == 0) {
               renderedImage.setRGB(imageX, rotatedImageY, color.getRGB());
            }
            else {
               final Color oldColor = new Color(oldRGB);
               final Color mixed = mix(oldColor, color);
               renderedImage.setRGB(imageX, rotatedImageY, mixed.getRGB());
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


   private void renderGeometry(final IBoundedGeometry<IVector2, ? extends IFiniteBounds<IVector2, ?>> geometry,
                               final IVector2 scale,
                               final BufferedImage renderedImage,
                               final Graphics2D g2d,
                               final GAxisAlignedRectangle region,
                               final GVectorialRenderingAttributes attributes) {

      if (geometry instanceof GMultiGeometry2D) {
         @SuppressWarnings("unchecked")
         final GMultiGeometry2D<IBoundedGeometry<IVector2, ? extends IFiniteBounds<IVector2, ?>>> multigeometry = (GMultiGeometry2D<IBoundedGeometry<IVector2, ? extends IFiniteBounds<IVector2, ?>>>) geometry;
         for (final IBoundedGeometry<IVector2, ? extends IFiniteBounds<IVector2, ?>> child : multigeometry) {
            if (child.getBounds().asAxisAlignedOrthotope().touches(region)) {
               renderGeometry(child, scale, renderedImage, g2d, region, attributes);
            }
         }
      }
      else if (geometry instanceof IVector2) {
         renderPoint((IVector2) geometry, scale, g2d, region, attributes);
      }
      else {
         // size validation only for non-points
         final GAxisAlignedOrthotope<IVector2, ?> geometryBounds = geometry.getBounds().asAxisAlignedOrthotope();
         final IVector2 scaledGeometryExtent = geometryBounds.getExtent().scale(scale);
         final double projectedSize = scaledGeometryExtent.x() * scaledGeometryExtent.y();
         if (projectedSize <= attributes._lodMinSize) {
            if (attributes._renderLODIgnores || attributes._debugLODRendering) {
               final Color color = attributes._debugLODRendering ? Color.MAGENTA : attributes._lodColor;

               final IVector2 projectedCenter = geometryBounds.getCenter().sub(region._lower).scale(scale);
               setPixel(renderedImage, projectedCenter, color);
            }

            return;
         }


         if (geometry instanceof IPolygonalChain2D) {
            drawPolyline(g2d, attributes, getPoints((IPolygonalChain2D) geometry, scale, region));
         }
         else if (geometry instanceof IPolygon2D) {
            final IPolygon2D polygon = (IPolygon2D) geometry;
            if (polygon instanceof IComplexPolygon2D) {
               final IComplexPolygon2D complexPolygon = (IComplexPolygon2D) polygon;

               final Area complexShape = getPoints(complexPolygon.getHull(), scale, region).asArea();

               for (final ISimplePolygon2D hole : complexPolygon.getHoles()) {
                  // complexShape.exclusiveOr(getPoints(hole, scale, region).asArea());
                  complexShape.subtract(getPoints(hole, scale, region).asArea());
               }

               drawShape(g2d, attributes, complexShape);
            }
            else {
               drawShape(g2d, attributes, getPoints(polygon, scale, region).asShape());
            }
         }
         else {
            System.out.println("Warning: geometry type " + geometry.getClass() + " not supported");
         }

      }
   }


   private static void renderPoint(final IVector2 point,
                                   final IVector2 scale,
                                   final Graphics2D g2d,
                                   final GAxisAlignedRectangle region,
                                   final GVectorialRenderingAttributes attributes) {
      final IVector2 projectedPoint = point.sub(region._lower).scale(scale);

      final int x = Math.round((float) projectedPoint.x());
      final int y = Math.round((float) projectedPoint.y());

      drawPoint(g2d, attributes, x, y);
   }


   private static class Points {
      private final int[] _xPoints;
      private final int[] _yPoints;


      private Points(final int[] xPoints,
                     final int[] yPoints) {
         _xPoints = xPoints;
         _yPoints = yPoints;
      }


      @Override
      public String toString() {
         return "Points [_xPoints=" + Arrays.toString(_xPoints) + ", _yPoints=" + Arrays.toString(_yPoints) + "]";
      }


      private Shape asShape() {
         return new Polygon(_xPoints, _yPoints, _xPoints.length);
      }


      private Area asArea() {
         return new Area(asShape());
      }
   }


   private static Points getPoints(final IPointsContainer<IVector2> polygon,
                                   final IVector2 scale,
                                   final GAxisAlignedRectangle region) {
      final int nPoints = polygon.getPointsCount();
      final int[] xPoints = new int[nPoints];
      final int[] yPoints = new int[nPoints];

      for (int i = 0; i < nPoints; i++) {
         final IVector2 point = polygon.getPoint(i).sub(region._lower).scale(scale);

         xPoints[i] = Math.round((float) point.x());
         yPoints[i] = Math.round((float) point.y());
      }

      return new Points(xPoints, yPoints);
   }


   private static void drawPoint(final Graphics2D g2d,
                                 final GVectorialRenderingAttributes attributes,
                                 final int x,
                                 final int y) {
      final int width = Math.max(1, Math.round(attributes._borderWidth) * 2);
      final int height = width;

      // fill point
      g2d.setColor(attributes._fillColor);
      g2d.fillOval(x, y, width, height);

      // render border
      if (attributes._borderWidth > 0) {
         g2d.setColor(attributes._borderColor);
         g2d.drawOval(x, y, width, height);
      }
   }


   private static void drawPolyline(final Graphics2D g2d,
                                    final GVectorialRenderingAttributes attributes,
                                    final Points points) {
      // render border
      if (attributes._borderWidth > 0) {
         //final float borderWidth = (float) (attributes._borderWidth / ((scale.x() + scale.y()) / 2));
         final float borderWidth = attributes._borderWidth;
         if (borderWidth > 0) {
            final BasicStroke borderStroke = new BasicStroke(borderWidth, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);

            g2d.setStroke(borderStroke);
            g2d.setColor(attributes._borderColor);
            g2d.drawPolyline(points._xPoints, points._yPoints, points._xPoints.length);
         }
      }
   }


   private static void drawShape(final Graphics2D g2d,
                                 final GVectorialRenderingAttributes attributes,
                                 final Shape shape) {
      // fill polygon
      g2d.setColor(attributes._fillColor);
      g2d.fill(shape);


      // render border
      if (attributes._borderWidth > 0) {
         //final float borderWidth = (float) (attributes._borderWidth / ((scale.x() + scale.y()) / 2));
         final float borderWidth = attributes._borderWidth;
         if (borderWidth > 0) {
            final BasicStroke borderStroke = new BasicStroke(borderWidth, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);

            g2d.setStroke(borderStroke);
            g2d.setColor(attributes._borderColor);
            g2d.draw(shape);
         }
      }
   }


}
