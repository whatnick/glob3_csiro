

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


class GPolygon2DRenderUnit {


   private final GGeometryQuadtree<IPolygon2D<?>> _quadtree;
   private final GAxisAlignedRectangle            _region;
   private final GRenderingAttributes             _attributes;


   GPolygon2DRenderUnit(final GGeometryQuadtree<IPolygon2D<?>> quadtree,
                        final GAxisAlignedRectangle region,
                        final GRenderingAttributes attributes) {
      _quadtree = quadtree;
      _region = region;
      _attributes = attributes;
   }


   BufferedImage render() {

      final IVector2<?> extent = _region.getExtent();

      final int width;
      final int height;

      if (extent.x() > extent.y()) {
         height = _attributes._textureDimension;
         width = (int) Math.round(extent.x() / extent.y() * _attributes._textureDimension);
      }
      else {
         width = _attributes._textureDimension;
         height = (int) Math.round(extent.y() / extent.x() * _attributes._textureDimension);
      }

      final IVector2<?> scale = new GVector2D(width, height).div(extent);


      final BufferedImage renderedImage = new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR);
      renderedImage.setAccelerationPriority(1);

      final Graphics2D g2d = renderedImage.createGraphics();
      g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
      g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);


      final AffineTransform transformFlipY = AffineTransform.getScaleInstance(1, -1);
      transformFlipY.concatenate(AffineTransform.getTranslateInstance(0, -height));

      final AffineTransform translation = AffineTransform.getTranslateInstance(-_region._lower.x(), -_region._lower.y());
      final AffineTransform scaling = AffineTransform.getScaleInstance(scale.x(), scale.y());

      final AffineTransform transform = new AffineTransform();
      transform.concatenate(transformFlipY);
      transform.concatenate(scaling);
      transform.concatenate(translation);

      g2d.setTransform(transform);

      processNode(_quadtree.getRoot(), scale, g2d, renderedImage);

      return renderedImage;
   }


   private void processNode(final GGTNode<IVector2<?>, GAxisAlignedRectangle, IPolygon2D<?>> node,
                            final IVector2<?> scale,
                            final Graphics2D g2d,
                            final BufferedImage renderedImage) {

      final GAxisAlignedRectangle nodeBounds = node.getBounds();

      if (!nodeBounds.touches(_region)) {
         return;
      }


      final IVector2<?> scaledNodeExtent = nodeBounds.getExtent().scale(scale);
      final double projectedSize = scaledNodeExtent.x() * scaledNodeExtent.y();
      if (projectedSize <= _attributes._lodMinSize) {
         if (_attributes._renderLODIgnores || _attributes._debugLODRendering) {
            final Color color = _attributes._debugLODRendering ? Color.RED : _attributes._borderColor;

            final IVector2<?> projectedCenter = nodeBounds._center.sub(_region._lower).scale(scale);
            setPixel(renderedImage, projectedCenter, color);
         }

         return;
      }


      if (node instanceof GGTInnerNode) {
         final GGTInnerNode<IVector2<?>, GAxisAlignedRectangle, IPolygon2D<?>> inner = (GGTInnerNode<IVector2<?>, GAxisAlignedRectangle, IPolygon2D<?>>) node;

         for (final GGTNode<IVector2<?>, GAxisAlignedRectangle, IPolygon2D<?>> child : inner.getChildren()) {
            processNode(child, scale, g2d, renderedImage);
         }
      }

      renderNodeGeometries(node, scale, g2d, renderedImage);
   }


   private void renderNodeGeometries(final GGTNode<IVector2<?>, GAxisAlignedRectangle, IPolygon2D<?>> node,
                                     final IVector2<?> scale,
                                     final Graphics2D g2d,
                                     final BufferedImage renderedImage) {


      if (_attributes._renderBounds) {
         final GAxisAlignedRectangle nodeBounds = node.getBounds();

         final IVector2<?> nodeLower = nodeBounds._lower;
         final IVector2<?> nodeUpper = nodeBounds._upper;

         g2d.setStroke(new BasicStroke(2));
         g2d.setColor(Color.GREEN);
         final int x = Math.round((float) nodeLower.x());
         final int y = Math.round((float) nodeLower.y());
         final int width = Math.round((float) (nodeUpper.x() - nodeLower.x()));
         final int height = Math.round((float) (nodeUpper.y() - nodeLower.y()));
         g2d.drawRect(x, y, width, height);
      }


      for (final IPolygon2D<?> geometry : node.getGeometries()) {
         if (geometry.getBounds().touches(_region)) {
            renderGeometry(geometry, scale, renderedImage, g2d);
         }
      }

   }


   private void renderGeometry(final IPolygon2D<?> geometry,
                               final IVector2<?> scale,
                               final Graphics2D g2d,
                               final BufferedImage renderedImage) {
      final IVector2<?> scaledGeometryExtent = geometry.getBounds().getExtent().scale(scale);
      final double projectedSize = scaledGeometryExtent.x() * scaledGeometryExtent.y();
      if (projectedSize <= _attributes._lodMinSize) {
         if (_attributes._renderLODIgnores || _attributes._debugLODRendering) {
            final Color color = _attributes._debugLODRendering ? Color.MAGENTA : _attributes._borderColor;

            final IVector2<?> projectedCenter = geometry.getBounds()._center.sub(_region._lower).scale(scale);
            setPixel(renderedImage, projectedCenter, color);
         }

         return;
      }


      final int nPoints = geometry.getPointsCount();
      final int[] xPoints = new int[nPoints];
      final int[] yPoints = new int[nPoints];

      int i = 0;
      for (final IVector2<?> point : geometry.getPoints()) {
         xPoints[i] = Math.round((float) point.x());
         yPoints[i] = Math.round((float) point.y());

         i++;
      }


      if (_attributes._stroke != null) {
         g2d.setStroke(_attributes._stroke);
         g2d.setColor(_attributes._fillColor);
         g2d.fillPolygon(xPoints, yPoints, nPoints);
      }

      g2d.setColor(_attributes._borderColor);
      g2d.drawPolygon(xPoints, yPoints, nPoints);
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


   private void setPixel(final BufferedImage renderedImage,
                         final IVector2<?> point,
                         final Color color) {

      final int imageX = Math.round((float) point.x());
      final int imageY = Math.round((float) point.y());

      if ((imageX >= 0) && (imageY >= 0)) {
         final int imageWidth = renderedImage.getWidth();
         final int imageHeight = renderedImage.getHeight();

         if ((imageX < imageWidth) && (imageY < imageHeight)) {
            final int oldRGB = renderedImage.getRGB(imageX, imageY);
            if (oldRGB == 0) {
               renderedImage.setRGB(imageX, imageHeight - 1 - imageY, color.getRGB());
            }
            else {
               final Color oldColor = new Color(oldRGB);
               final Color mixed = mix(oldColor, color);
               renderedImage.setRGB(imageX, imageHeight - 1 - imageY, mixed.getRGB());
            }
         }
      }

   }


   private Color mix(final Color colorA,
                     final Color colorB) {

      final int r = average(colorA.getRed(), colorB.getRed());
      final int g = average(colorA.getGreen(), colorB.getGreen());
      final int b = average(colorA.getBlue(), colorB.getBlue());
      //final int a = average(colorA.getAlpha(), colorB.getAlpha());
      final int a = Math.max(colorA.getAlpha(), colorB.getAlpha());
      return new Color(r, g, b, a);
   }


   private int average(final int a,
                       final int b) {
      return (a + b) / 2;
   }


   private void renderGeometry(final IPolygon2D<?> geometry,
                               final IVector2<?> scale,
                               final BufferedImage renderedImage,
                               final Graphics2D g2d) {
      if (geometry instanceof GComplexPolytope) {
         renderGeometry((IPolygon2D<?>) geometry.getHull(), scale, g2d, renderedImage);
      }
      else {
         renderGeometry(geometry, scale, g2d, renderedImage);
      }
   }

}