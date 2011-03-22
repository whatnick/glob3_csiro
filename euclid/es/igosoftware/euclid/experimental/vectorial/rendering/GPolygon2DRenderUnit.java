

package es.igosoftware.euclid.experimental.vectorial.rendering;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

import es.igosoftware.euclid.IBoundedGeometry;
import es.igosoftware.euclid.bounding.GAxisAlignedRectangle;
import es.igosoftware.euclid.features.IGlobeFeature;
import es.igosoftware.euclid.ntree.GGTInnerNode;
import es.igosoftware.euclid.ntree.GGTNode;
import es.igosoftware.euclid.shape.GComplexPolytope;
import es.igosoftware.euclid.shape.GRenderType;
import es.igosoftware.euclid.shape.IPolygon2D;
import es.igosoftware.euclid.vector.GVector2D;
import es.igosoftware.euclid.vector.IVector;
import es.igosoftware.euclid.vector.IVector2;


class GPolygon2DRenderUnit
         implements
            IPolygon2DRenderUnit {


   @Override
   public BufferedImage render(final GRenderingQuadtree<IGlobeFeature<IVector2<?>, IBoundedGeometry<IVector2<?>, ?, GAxisAlignedRectangle>, GAxisAlignedRectangle>, IBoundedGeometry<IVector2<?>, ?, GAxisAlignedRectangle>, GAxisAlignedRectangle> quadtree,
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


   private void processNode(final GGTNode<IVector2<?>, GAxisAlignedRectangle, IGlobeFeature<IVector2<?>, IBoundedGeometry<IVector2<?>, ?, GAxisAlignedRectangle>, GAxisAlignedRectangle>, IBoundedGeometry<IVector2<?>, ?, GAxisAlignedRectangle>> node,
                            final GRenderingQuadtree<IGlobeFeature<IVector2<?>, IBoundedGeometry<IVector2<?>, ?, GAxisAlignedRectangle>, GAxisAlignedRectangle>, IBoundedGeometry<IVector2<?>, ?, GAxisAlignedRectangle>, GAxisAlignedRectangle> quadtree,
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
         final GGTInnerNode<IVector2<?>, GAxisAlignedRectangle, IGlobeFeature<IVector2<?>, IBoundedGeometry<IVector2<?>, ?, GAxisAlignedRectangle>, GAxisAlignedRectangle>, IBoundedGeometry<IVector2<?>, ?, GAxisAlignedRectangle>> inner;
         inner = (GGTInnerNode<IVector2<?>, GAxisAlignedRectangle, IGlobeFeature<IVector2<?>, IBoundedGeometry<IVector2<?>, ?, GAxisAlignedRectangle>, GAxisAlignedRectangle>, IBoundedGeometry<IVector2<?>, ?, GAxisAlignedRectangle>>) node;

         for (final GGTNode<IVector2<?>, GAxisAlignedRectangle, IGlobeFeature<IVector2<?>, IBoundedGeometry<IVector2<?>, ?, GAxisAlignedRectangle>, GAxisAlignedRectangle>, IBoundedGeometry<IVector2<?>, ?, GAxisAlignedRectangle>> child : inner.getChildren()) {
            processNode(child, quadtree, region, attributes, scale, g2d, renderedImage);
         }
      }

      // renderNodeGeometries(node, scale, g2d, renderedImage);
      renderNodeGeometries(node, region, attributes, scale, g2d, renderedImage);
   }


   private void renderNodeGeometries(final GGTNode<IVector2<?>, GAxisAlignedRectangle, IGlobeFeature<IVector2<?>, IBoundedGeometry<IVector2<?>, ?, GAxisAlignedRectangle>, GAxisAlignedRectangle>, IBoundedGeometry<IVector2<?>, ?, GAxisAlignedRectangle>> node,
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


      for (final IGlobeFeature<IVector2<?>, IBoundedGeometry<IVector2<?>, ?, GAxisAlignedRectangle>, GAxisAlignedRectangle> feature : node.getElements()) {
         final IBoundedGeometry<IVector2<?>, ?, GAxisAlignedRectangle> geometry = feature.getDefaultGeometry();
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


   @SuppressWarnings("unchecked")
   private void renderGeometry(final IBoundedGeometry<IVector2<?>, ?, GAxisAlignedRectangle> geometry,
                               final IVector2<?> scale,
                               final BufferedImage renderedImage,
                               final Graphics2D g2d,
                               final GAxisAlignedRectangle region,
                               final GRenderingAttributes attributes) {

      final GRenderType renderType = geometry.getRenderType();

      if (renderType == GRenderType.DO_NOT_RENDER) {
         System.out.println("Ignoring rendering of " + geometry);
         return;
      }


      final IBoundedGeometry<IVector2<?>, ?, GAxisAlignedRectangle> geometryToDraw;
      if (geometry instanceof GComplexPolytope) {
         geometryToDraw = ((GComplexPolytope) geometry).getHull();
      }
      else if (geometry instanceof IPolygon2D) {
         geometryToDraw = geometry;
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


      if (geometryToDraw instanceof IVector) {
         final IVector2<?> point2 = (IVector2<?>) geometryToDraw;
         final IVector2<?> point = point2.sub(region._lower).scale(scale);

         final int x = Math.round((float) point.x());
         final int y = Math.round((float) point.y());

         drawPoint(g2d, attributes, x, y);
      }
      else if (geometryToDraw instanceof IPolygon2D) {
         final IPolygon2D<?> polygon = (IPolygon2D<?>) geometryToDraw;
         final int nPoints = polygon.getPointsCount();
         final int[] xPoints = new int[nPoints];
         final int[] yPoints = new int[nPoints];

         for (int i = 0; i < nPoints; i++) {
            final IVector2<?> point = polygon.getPoint(i).sub(region._lower).scale(scale);

            xPoints[i] = Math.round((float) point.x());
            yPoints[i] = Math.round((float) point.y());
         }

         switch (renderType) {
            case POINT:
               // do nothing, POINTS ARE RENDERED BEFORE was ignored before
               break;

            case POLYLINE:
               drawPolyline(g2d, attributes, nPoints, xPoints, yPoints);
               break;

            case POLYGON:
               drawPolygon(g2d, attributes, nPoints, xPoints, yPoints);
               break;

            case DO_NOT_RENDER:
               // do nothing, DO_NOT_RENDER was ignored before
               break;
         }
      }

   }


   private static void drawPoint(final Graphics2D g2d,
                                 final GRenderingAttributes attributes,
                                 final int x,
                                 final int y) {
      final int width = 2;
      final int height = 2;

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
