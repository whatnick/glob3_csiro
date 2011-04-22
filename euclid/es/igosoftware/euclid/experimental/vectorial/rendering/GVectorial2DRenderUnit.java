

package es.igosoftware.euclid.experimental.vectorial.rendering;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.image.BufferedImage;

import es.igosoftware.euclid.IBoundedGeometry;
import es.igosoftware.euclid.ICurve2D;
import es.igosoftware.euclid.ISurface2D;
import es.igosoftware.euclid.bounding.GAxisAlignedOrthotope;
import es.igosoftware.euclid.bounding.GAxisAlignedRectangle;
import es.igosoftware.euclid.bounding.IFiniteBounds;
import es.igosoftware.euclid.experimental.measurement.GArea;
import es.igosoftware.euclid.experimental.measurement.IMeasure;
import es.igosoftware.euclid.experimental.vectorial.rendering.context.GJava2DVectorial2DDrawer;
import es.igosoftware.euclid.experimental.vectorial.rendering.context.GJava2DVectorial2DRenderingContext;
import es.igosoftware.euclid.experimental.vectorial.rendering.context.GVectorial2DRenderingScaleContext;
import es.igosoftware.euclid.experimental.vectorial.rendering.context.IVectorial2DDrawer;
import es.igosoftware.euclid.experimental.vectorial.rendering.context.IVectorial2DRenderingContext;
import es.igosoftware.euclid.experimental.vectorial.rendering.context.IVectorial2DRenderingScaleContext;
import es.igosoftware.euclid.experimental.vectorial.rendering.styling.IRenderingStyle;
import es.igosoftware.euclid.experimental.vectorial.rendering.utils.GRenderingQuadtree;
import es.igosoftware.euclid.features.IGlobeFeature;
import es.igosoftware.euclid.multigeometry.GMultiGeometry2D;
import es.igosoftware.euclid.ntree.GElementGeometryPair;
import es.igosoftware.euclid.ntree.GGTInnerNode;
import es.igosoftware.euclid.ntree.GGTNode;
import es.igosoftware.euclid.projection.GProjection;
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


      //      final IVectorial2DRenderingContext rc = new GJava2DVectorial2DRenderingContext(region, projection, renderedImage);
      final IVectorial2DDrawer drawer = new GJava2DVectorial2DDrawer(renderedImage);
      final IVectorial2DRenderingScaleContext scaler = new GVectorial2DRenderingScaleContext(region, projection,
               renderedImage.getWidth(), renderedImage.getHeight());
      final IVectorial2DRenderingContext rc = new GJava2DVectorial2DRenderingContext(scaler, drawer);

      final GAxisAlignedRectangle extendedRegion = calculateExtendedRegion(region, projection, renderingStyle);
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


      final IVector2 scaledNodeExtent = rc.getScaler().scaleExtent(nodeBounds.getExtent());
      final double projectedSize = scaledNodeExtent.x() * scaledNodeExtent.y();
      if (projectedSize < renderingStyle.getLODMinSize()) {
         //                  if (renderingStyle.isRenderLODIgnores() || renderingStyle.isDebugRendering()) {
         //                     final Color color = renderingStyle.isDebugRendering() ? Color.RED : renderingStyle.getLODColor().asAWTColor();
         //         
         //                     final IVector2 projectedPosition = rc.scaleAndTranslatePoint(nodeBounds.getCenter()).sub(scaledNodeExtent.div(2));
         //                     rc.setColor(color);
         //                     rc.fillRect(projectedPosition.x(), projectedPosition.y(), scaledNodeExtent.x(), scaledNodeExtent.y());
         //                  }
         if (renderingStyle.isDebugRendering()) {
            final Color color = renderingStyle.getLODColor().asAWTColor();
            final IVector2 projectedPosition = rc.getScaler().scaleAndTranslatePoint(nodeBounds.getCenter()).sub(
                     scaledNodeExtent.div(2));
            rc.getDrawer().fillRect(projectedPosition.x(), projectedPosition.y(), scaledNodeExtent.x(), scaledNodeExtent.y(),
                     color);
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

         final IVector2 nodeLower = rc.getScaler().scaleAndTranslatePoint(nodeBounds._lower);
         final IVector2 nodeUpper = rc.getScaler().scaleAndTranslatePoint(nodeBounds._upper);

         final boolean isInner = (node instanceof GGTInnerNode);

         rc.getDrawer().drawRect(nodeLower.x(), nodeLower.y(),//
                  (nodeUpper.x() - nodeLower.x()), (nodeUpper.y() - nodeLower.y()), //
                  isInner ? Color.GREEN.darker().darker().darker().darker().darker() : Color.GREEN, //
                  new BasicStroke(1));
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
