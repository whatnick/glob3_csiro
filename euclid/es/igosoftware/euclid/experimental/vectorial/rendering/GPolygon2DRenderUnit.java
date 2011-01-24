

package es.igosoftware.euclid.experimental.vectorial.rendering;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

import es.igosoftware.euclid.bounding.GAxisAlignedRectangle;
import es.igosoftware.euclid.ntree.GGTInnerNode;
import es.igosoftware.euclid.ntree.GGTNode;
import es.igosoftware.euclid.ntree.quadtree.GGeometryQuadtree;
import es.igosoftware.euclid.shape.GComplexPolytope;
import es.igosoftware.euclid.shape.IPolygon2D;
import es.igosoftware.euclid.vector.GVector2D;
import es.igosoftware.euclid.vector.IVector2;


class GPolygon2DRenderUnit
         implements
            IPolygon2DRenderUnit {


   @Override
   public BufferedImage render(final GGeometryQuadtree<IPolygon2D<?>> quadtree,
                               final GAxisAlignedRectangle region,
                               final GRenderingAttributes attributes) {

      final IVector2<?> extent = region.getExtent();

      final int width = attributes._textureWidth;
      final int height = attributes._textureHeight;


      final IVector2<?> scale = new GVector2D(width, height).div(extent);


      final BufferedImage renderedImage = new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR);
      renderedImage.setAccelerationPriority(1);

      final Graphics2D g2d = renderedImage.createGraphics();
      g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
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

      //      final AffineTransform transform = new AffineTransform();
      //      transform.concatenate(transformFlipY);
      //      g2d.setTransform(transform);
      g2d.setTransform(transformFlipY);


      processNode(quadtree.getRoot(), quadtree, region, attributes, scale, g2d, renderedImage);


      return renderedImage;
   }


   private static void processNode(final GGTNode<IVector2<?>, GAxisAlignedRectangle, IPolygon2D<?>> node,
                                   final GGeometryQuadtree<IPolygon2D<?>> quadtree,
                                   final GAxisAlignedRectangle region,
                                   final GRenderingAttributes attributes,
                                   final IVector2<?> scale,
                                   final Graphics2D g2d,
                                   final BufferedImage renderedImage) {

      final GAxisAlignedRectangle nodeBounds = node.getBounds();

      if (!nodeBounds.touches(region)) {
         return;
      }


      final IVector2<?> scaledNodeExtent = nodeBounds.getExtent().scale(scale);
      final double projectedSize = scaledNodeExtent.x() * scaledNodeExtent.y();
      if (projectedSize <= attributes._lodMinSize) {
         if (attributes._renderLODIgnores || attributes._debugLODRendering) {
            final Color color = attributes._debugLODRendering ? Color.RED : attributes._lodColor;

            final IVector2<?> projectedCenter = nodeBounds.getCenter().sub(region._lower).scale(scale);
            setPixel(renderedImage, projectedCenter, color);
         }

         return;
      }


      if (node instanceof GGTInnerNode) {
         final GGTInnerNode<IVector2<?>, GAxisAlignedRectangle, IPolygon2D<?>> inner = (GGTInnerNode<IVector2<?>, GAxisAlignedRectangle, IPolygon2D<?>>) node;

         for (final GGTNode<IVector2<?>, GAxisAlignedRectangle, IPolygon2D<?>> child : inner.getChildren()) {
            processNode(child, quadtree, region, attributes, scale, g2d, renderedImage);
         }
      }

      // renderNodeGeometries(node, scale, g2d, renderedImage);
      renderNodeGeometries(node, region, attributes, scale, g2d, renderedImage);
   }


   private static void renderNodeGeometries(final GGTNode<IVector2<?>, GAxisAlignedRectangle, IPolygon2D<?>> node,
                                            final GAxisAlignedRectangle region,
                                            final GRenderingAttributes attributes,
                                            final IVector2<?> scale,
                                            final Graphics2D g2d,
                                            final BufferedImage renderedImage) {


      if (attributes._renderBounds) {
         final GAxisAlignedRectangle nodeBounds = node.getBounds();

         final IVector2<?> nodeLower = nodeBounds._lower.sub(region._lower).scale(scale);
         final IVector2<?> nodeUpper = nodeBounds._upper.sub(region._lower).scale(scale);

         g2d.setStroke(new BasicStroke(0.25f));
         g2d.setColor(Color.GREEN);
         final int x = Math.round((float) nodeLower.x());
         final int y = Math.round((float) nodeLower.y());
         final int width = Math.round((float) (nodeUpper.x() - nodeLower.x()));
         final int height = Math.round((float) (nodeUpper.y() - nodeLower.y()));
         g2d.drawRect(x, y, width, height);
      }


      for (final IPolygon2D<?> geometry : node.getGeometries()) {
         if (geometry.getBounds().touches(region)) {
            renderGeometry(geometry, scale, renderedImage, g2d, region, attributes);
         }
      }

   }


   //   private Color scaleColor(final Color color,
   //                            final double alpha) {
   //      final float alphaF = GMath.clamp((float) alpha, 0.5f, 1);
   //
   //      //      final int r = Math.round(color.getRed() * alphaF);
   //      //      final int g = Math.round(color.getGreen() * alphaF);
   //      //      final int b = Math.round(color.getBlue() * alphaF);
   //
   //      final int r = color.getRed();
   //      final int g = color.getGreen();
   //      final int b = color.getBlue();
   //
   //      final int a = Math.round(color.getAlpha() * alphaF);
   //      //      final int a = color.getAlpha();
   //
   //      return new Color(r, g, b, a);
   //   }


   private static void setPixel(final BufferedImage renderedImage,
                                final IVector2<?> point,
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


   private static void renderGeometry(final IPolygon2D<?> geometry,
                                      final IVector2<?> scale,
                                      final BufferedImage renderedImage,
                                      final Graphics2D g2d,
                                      final GAxisAlignedRectangle region,
                                      final GRenderingAttributes attributes) {

      final IPolygon2D<?> geometryToDraw;
      if (geometry instanceof GComplexPolytope) {
         geometryToDraw = (IPolygon2D<?>) geometry.getHull();
      }
      else {
         geometryToDraw = geometry;
      }


      final GAxisAlignedRectangle geometryBounds = geometryToDraw.getBounds();
      final IVector2<?> scaledGeometryExtent = geometryBounds.getExtent().scale(scale);
      final double projectedSize = scaledGeometryExtent.x() * scaledGeometryExtent.y();
      if (projectedSize <= attributes._lodMinSize) {
         if (attributes._renderLODIgnores || attributes._debugLODRendering) {
            final Color color = attributes._debugLODRendering ? Color.MAGENTA : attributes._lodColor;

            final IVector2<?> projectedCenter = geometryBounds.getCenter().sub(region._lower).scale(scale);
            setPixel(renderedImage, projectedCenter, color);
         }

         return;
      }


      final int nPoints = geometryToDraw.getPointsCount();
      final int[] xPoints = new int[nPoints];
      final int[] yPoints = new int[nPoints];

      for (int i = 0; i < nPoints; i++) {
         final IVector2<?> point = geometryToDraw.getPoint(i).sub(region._lower).scale(scale);

         xPoints[i] = Math.round((float) point.x());
         yPoints[i] = Math.round((float) point.y());
      }

      switch (geometryToDraw.getRenderType()) {
         case POLYGON:
            drawPolygon(g2d, attributes, nPoints, xPoints, yPoints);
            break;

         case POLYLINE:
            renderPolyline(g2d, attributes, nPoints, xPoints, yPoints);
            break;
      }


   }


   private static void renderPolyline(final Graphics2D g2d,
                                      final GRenderingAttributes attributes,
                                      final int nPoints,
                                      final int[] xPoints,
                                      final int[] yPoints) {
      // render border
      if (attributes._borderWidth > 0) {
         //final float borderWidth = (float) (attributes._borderWidth / ((scale.x() + scale.y()) / 2));
         final float borderWidth = attributes._borderWidth;
         if (borderWidth > 0) {
            final BasicStroke borderStroke = new BasicStroke(borderWidth, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);

            g2d.setStroke(borderStroke);
            g2d.setColor(attributes._borderColor);
            g2d.drawPolyline(xPoints, yPoints, nPoints);
         }
      }
   }


   private static void drawPolygon(final Graphics2D g2d,
                                   final GRenderingAttributes attributes,
                                   final int nPoints,
                                   final int[] xPoints,
                                   final int[] yPoints) {
      // fill polygon
      g2d.setColor(attributes._fillColor);
      g2d.fillPolygon(xPoints, yPoints, nPoints);


      // render border
      if (attributes._borderWidth > 0) {
         //final float borderWidth = (float) (attributes._borderWidth / ((scale.x() + scale.y()) / 2));
         final float borderWidth = attributes._borderWidth;
         if (borderWidth > 0) {
            final BasicStroke borderStroke = new BasicStroke(borderWidth, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);

            g2d.setStroke(borderStroke);
            g2d.setColor(attributes._borderColor);
            g2d.drawPolygon(xPoints, yPoints, nPoints);
         }
      }
   }
}
