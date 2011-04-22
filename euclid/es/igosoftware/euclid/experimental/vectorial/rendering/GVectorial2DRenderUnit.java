

package es.igosoftware.euclid.experimental.vectorial.rendering;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

import es.igosoftware.euclid.IBoundedGeometry;
import es.igosoftware.euclid.ICurve2D;
import es.igosoftware.euclid.ISurface2D;
import es.igosoftware.euclid.bounding.GAxisAlignedOrthotope;
import es.igosoftware.euclid.bounding.GAxisAlignedRectangle;
import es.igosoftware.euclid.bounding.IFiniteBounds;
import es.igosoftware.euclid.experimental.measurement.GArea;
import es.igosoftware.euclid.experimental.measurement.IMeasure;
import es.igosoftware.euclid.experimental.vectorial.rendering.context.GJava2DVectorial2DRenderingContext;
import es.igosoftware.euclid.experimental.vectorial.rendering.context.IVectorial2DRenderingContext;
import es.igosoftware.euclid.experimental.vectorial.rendering.styling.IRenderingStyle;
import es.igosoftware.euclid.experimental.vectorial.rendering.utils.GRenderingQuadtree;
import es.igosoftware.euclid.features.IGlobeFeature;
import es.igosoftware.euclid.multigeometry.GMultiGeometry2D;
import es.igosoftware.euclid.ntree.GElementGeometryPair;
import es.igosoftware.euclid.ntree.GGTInnerNode;
import es.igosoftware.euclid.ntree.GGTNode;
import es.igosoftware.euclid.projection.GProjection;
import es.igosoftware.euclid.vector.GVector2D;
import es.igosoftware.euclid.vector.IVector2;
import es.igosoftware.util.GMath;


class GVectorial2DRenderUnit
         implements
            IVectorial2DRenderUnit {


   @Override
   public void render(final BufferedImage renderedImage,
                      final GRenderingQuadtree<IGlobeFeature<IVector2, ? extends IBoundedGeometry<IVector2, ? extends IFiniteBounds<IVector2, ?>>>> quadtree,
                      final GProjection projection,
                      final GAxisAlignedRectangle region,
                      final IRenderingStyle renderingStyle) {

      final int imageWidth = renderedImage.getWidth();
      final int imageHeight = renderedImage.getHeight();

      final IVector2 scale = new GVector2D(imageWidth, imageHeight).div(region.getExtent());

      final Graphics2D g2d = renderedImage.createGraphics();
      g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
      g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
      //      g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);

      if (renderingStyle.isDebugRendering()) {
         g2d.setColor(Color.YELLOW);
         g2d.setStroke(new BasicStroke(1));

         g2d.drawRect(0, 0, imageWidth, imageHeight);

         //         g2d.drawString(region._lower.toString(), 10, 10);
         //         g2d.drawString(" " + region._upper, 10, 20);
      }

      final AffineTransform transformFlipY = AffineTransform.getScaleInstance(1, -1);
      transformFlipY.concatenate(AffineTransform.getTranslateInstance(0, -imageHeight));

      g2d.setTransform(transformFlipY);


      final GAxisAlignedRectangle extendedRegion = calculateExtendedRegion(region, projection, renderingStyle);
      final IVectorial2DRenderingContext rc = new GJava2DVectorial2DRenderingContext(scale, region, projection, g2d,
               renderedImage);

      processNode(quadtree.getRoot(), extendedRegion, renderingStyle, rc);

   }


   private static GAxisAlignedRectangle calculateExtendedRegion(final GAxisAlignedRectangle region,
                                                                final GProjection projection,
                                                                final IRenderingStyle renderingStyle) {
      final IMeasure<GArea> maximumSize = renderingStyle.getMaximumSize();

      final double areaInSquaredMeters = maximumSize.getValueInReferenceUnits();
      final double extent = GMath.sqrt(areaInSquaredMeters);
      final IVector2 lower = renderingStyle.increment(region._lower, projection, -extent, -extent);
      final IVector2 upper = renderingStyle.increment(region._upper, projection, extent, extent);

      return new GAxisAlignedRectangle(lower, upper);
   }


   private void processNode(final GGTNode<IVector2, IGlobeFeature<IVector2, ? extends IBoundedGeometry<IVector2, ? extends IFiniteBounds<IVector2, ?>>>> node,
                            final GAxisAlignedRectangle extendedRegion,
                            final IRenderingStyle renderingStyle,
                            final IVectorial2DRenderingContext rc) {

      final GAxisAlignedRectangle nodeBounds = node.getMinimumBounds().asRectangle();

      if (!nodeBounds.touches(extendedRegion)) {
         return;
      }


      final IVector2 scaledNodeExtent = rc.scaleExtent(nodeBounds.getExtent());
      final double projectedSize = scaledNodeExtent.x() * scaledNodeExtent.y();
      if (projectedSize < renderingStyle.getLODMinSize()) {
         if (renderingStyle.isRenderLODIgnores() || renderingStyle.isDebugRendering()) {
            final Color color = renderingStyle.isDebugRendering() ? Color.RED : renderingStyle.getLODColor().asAWTColor();

            final IVector2 projectedCenter = rc.scaleAndTranslatePoint(nodeBounds.getCenter());
            //            rc.setPixel(projectedCenter, color);
            rc.setColor(color);
            rc.fillRect(projectedCenter.x(), projectedCenter.y(), 1, 1);
         }

         return;
      }


      if (node instanceof GGTInnerNode) {
         final GGTInnerNode<IVector2, IGlobeFeature<IVector2, ? extends IBoundedGeometry<IVector2, ? extends IFiniteBounds<IVector2, ?>>>> inner;
         inner = (GGTInnerNode<IVector2, IGlobeFeature<IVector2, ? extends IBoundedGeometry<IVector2, ? extends IFiniteBounds<IVector2, ?>>>>) node;

         for (final GGTNode<IVector2, IGlobeFeature<IVector2, ? extends IBoundedGeometry<IVector2, ? extends IFiniteBounds<IVector2, ?>>>> child : inner.getChildren()) {
            processNode(child, extendedRegion, renderingStyle, rc);
         }
      }

      renderNodeGeometries(node, extendedRegion, renderingStyle, rc);
   }


   private void renderNodeGeometries(final GGTNode<IVector2, IGlobeFeature<IVector2, ? extends IBoundedGeometry<IVector2, ? extends IFiniteBounds<IVector2, ?>>>> node,
                                     final GAxisAlignedRectangle extendedRegion,
                                     final IRenderingStyle renderingStyle,
                                     final IVectorial2DRenderingContext rc) {


      if (renderingStyle.isDebugRendering()) {
         final GAxisAlignedOrthotope<IVector2, ?> nodeBounds = node.getBounds();

         final IVector2 nodeLower = rc.scaleAndTranslatePoint(nodeBounds._lower);
         final IVector2 nodeUpper = rc.scaleAndTranslatePoint(nodeBounds._upper);

         //         g2d.setStroke(new BasicStroke(0.25f));
         final boolean isInner = (node instanceof GGTInnerNode);
         rc.setStroke(new BasicStroke(1));
         rc.setColor(isInner ? Color.GREEN.darker().darker().darker().darker().darker() : Color.GREEN);

         final int x = Math.round((float) nodeLower.x());
         final int y = Math.round((float) nodeLower.y());
         //         final int width = Math.round((float) (nodeUpper.x() - nodeLower.x()));
         //         final int height = Math.round((float) (nodeUpper.y() - nodeLower.y()));
         final int width = (int) (nodeUpper.x() - nodeLower.x());
         final int height = (int) (nodeUpper.y() - nodeLower.y());
         rc.drawRect(x, y, width, height);
      }


      for (final GElementGeometryPair<IVector2, IGlobeFeature<IVector2, ? extends IBoundedGeometry<IVector2, ? extends IFiniteBounds<IVector2, ?>>>> pair : node.getElements()) {
         final IBoundedGeometry<IVector2, ? extends IFiniteBounds<IVector2, ?>> geometry = pair.getGeometry();
         renderGeometry(geometry, pair.getElement(), extendedRegion, renderingStyle, rc);
      }

   }


   private void renderGeometry(final IBoundedGeometry<IVector2, ? extends IFiniteBounds<IVector2, ?>> geometry,
                               final IGlobeFeature<IVector2, ? extends IBoundedGeometry<IVector2, ? extends IFiniteBounds<IVector2, ?>>> feature,
                               final GAxisAlignedRectangle extendedRegion,
                               final IRenderingStyle renderingStyle,
                               final IVectorial2DRenderingContext rc) {


      if (!geometry.getBounds().asAxisAlignedOrthotope().touches(extendedRegion)) {
         return;
      }

      if (geometry instanceof GMultiGeometry2D) {
         @SuppressWarnings("unchecked")
         final GMultiGeometry2D<IBoundedGeometry<IVector2, ? extends IFiniteBounds<IVector2, ?>>> multigeometry = (GMultiGeometry2D<IBoundedGeometry<IVector2, ? extends IFiniteBounds<IVector2, ?>>>) geometry;
         for (final IBoundedGeometry<IVector2, ? extends IFiniteBounds<IVector2, ?>> child : multigeometry) {
            renderGeometry(child, feature, extendedRegion, renderingStyle, rc);
         }
      }
      else if (geometry instanceof IVector2) {
         final IVector2 point = (IVector2) geometry;
         renderingStyle.drawPoint(point, feature, rc);
      }
      else if (geometry instanceof ICurve2D<?>) {
         final ICurve2D<?> curve = (ICurve2D<?>) geometry;
         renderingStyle.drawCurve(curve, feature, rc);
      }
      else if (geometry instanceof ISurface2D<?>) {
         final ISurface2D<?> surface = (ISurface2D<?>) geometry;
         renderingStyle.drawSurface(surface, feature, rc);
      }
      else {
         System.out.println("Warning: geometry type " + geometry.getClass() + " not supported");
      }

   }


}
