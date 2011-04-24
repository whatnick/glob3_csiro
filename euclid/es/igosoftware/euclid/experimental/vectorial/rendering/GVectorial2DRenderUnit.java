

package es.igosoftware.euclid.experimental.vectorial.rendering;

import java.awt.Color;
import java.awt.image.BufferedImage;

import es.igosoftware.euclid.IBoundedGeometry2D;
import es.igosoftware.euclid.ICurve2D;
import es.igosoftware.euclid.ISurface2D;
import es.igosoftware.euclid.bounding.GAxisAlignedOrthotope;
import es.igosoftware.euclid.bounding.GAxisAlignedRectangle;
import es.igosoftware.euclid.bounding.IFiniteBounds;
import es.igosoftware.euclid.experimental.measurement.GArea;
import es.igosoftware.euclid.experimental.measurement.IMeasure;
import es.igosoftware.euclid.experimental.vectorial.rendering.context.GVectorial2DRenderingScaler;
import es.igosoftware.euclid.experimental.vectorial.rendering.context.IProjectionTool;
import es.igosoftware.euclid.experimental.vectorial.rendering.context.IVectorial2DDrawer;
import es.igosoftware.euclid.experimental.vectorial.rendering.context.IVectorial2DRenderingScaler;
import es.igosoftware.euclid.experimental.vectorial.rendering.features.ICurveRenderingShape;
import es.igosoftware.euclid.experimental.vectorial.rendering.features.INodeRenderingShape;
import es.igosoftware.euclid.experimental.vectorial.rendering.features.IRenderingSymbol;
import es.igosoftware.euclid.experimental.vectorial.rendering.features.ISurfaceRenderingShape;
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
                      final GRenderingQuadtree<IGlobeFeature<IVector2, ? extends IBoundedGeometry2D<? extends IFiniteBounds<IVector2, ?>>>> quadtree,
                      final GProjection projection,
                      final IProjectionTool projectionTool,
                      final GAxisAlignedRectangle viewport,
                      final IRenderingStyle renderingStyle,
                      final IVectorial2DDrawer drawer) {

      final IVectorial2DRenderingScaler scaler = new GVectorial2DRenderingScaler(viewport, projection, projectionTool,
               renderedImage.getWidth(), renderedImage.getHeight());

      final GAxisAlignedRectangle extendedRegion = calculateExtendedRegion(viewport, scaler, renderingStyle);

      processNode(quadtree.getRoot(), extendedRegion, renderingStyle, scaler, drawer);
   }


   private static GAxisAlignedRectangle calculateExtendedRegion(final GAxisAlignedRectangle viewport,
                                                                final IVectorial2DRenderingScaler scaler,
                                                                final IRenderingStyle renderingStyle) {
      final IMeasure<GArea> maximumSize = renderingStyle.getMaximumSize();

      final double areaInSquaredMeters = maximumSize.getValueInReferenceUnits();
      final double extent = GMath.sqrt(areaInSquaredMeters);
      final IVector2 lower = scaler.increment(viewport._lower, -extent, -extent);
      final IVector2 upper = scaler.increment(viewport._upper, extent, extent);

      return new GAxisAlignedRectangle(lower, upper);
   }


   private void processNode(final GGTNode<IVector2, IGlobeFeature<IVector2, ? extends IBoundedGeometry2D<? extends IFiniteBounds<IVector2, ?>>>, IBoundedGeometry2D<? extends IFiniteBounds<IVector2, ?>>> node,
                            final GAxisAlignedRectangle extendedRegion,
                            final IRenderingStyle renderingStyle,
                            final IVectorial2DRenderingScaler scaler,
                            final IVectorial2DDrawer drawer) {

      final GAxisAlignedRectangle nodeBounds = node.getMinimumBounds().asRectangle();

      if (!nodeBounds.touches(extendedRegion)) {
         return;
      }


      final IVector2 scaledNodeExtent = scaler.scaleExtent(nodeBounds.getExtent());
      final double scaledNodeSize = scaledNodeExtent.x() * scaledNodeExtent.y();
      if (scaledNodeSize < renderingStyle.getLODMinSize()) {
         if (renderingStyle.isDebugRendering()) {
            final GAxisAlignedOrthotope<IVector2, ?> scaledNodeBounds = scaler.scaleAndTranslate(nodeBounds);
            final Color color = renderingStyle.getLODColor().asAWTColor();
            drawer.fillRect(scaledNodeBounds, color);
         }

         return;
      }


      if (node instanceof GGTInnerNode) {
         final GGTInnerNode<IVector2, IGlobeFeature<IVector2, ? extends IBoundedGeometry2D<? extends IFiniteBounds<IVector2, ?>>>, IBoundedGeometry2D<? extends IFiniteBounds<IVector2, ?>>> inner;
         inner = (GGTInnerNode<IVector2, IGlobeFeature<IVector2, ? extends IBoundedGeometry2D<? extends IFiniteBounds<IVector2, ?>>>, IBoundedGeometry2D<? extends IFiniteBounds<IVector2, ?>>>) node;

         for (final GGTNode<IVector2, IGlobeFeature<IVector2, ? extends IBoundedGeometry2D<? extends IFiniteBounds<IVector2, ?>>>, IBoundedGeometry2D<? extends IFiniteBounds<IVector2, ?>>> child : inner.getChildren()) {
            processNode(child, extendedRegion, renderingStyle, scaler, drawer);
         }
      }

      drawNode(node, extendedRegion, renderingStyle, scaler, drawer);
   }


   private void drawNode(final GGTNode<IVector2, IGlobeFeature<IVector2, ? extends IBoundedGeometry2D<? extends IFiniteBounds<IVector2, ?>>>, IBoundedGeometry2D<? extends IFiniteBounds<IVector2, ?>>> node,
                         final GAxisAlignedRectangle extendedRegion,
                         final IRenderingStyle renderingStyle,
                         final IVectorial2DRenderingScaler scaler,
                         final IVectorial2DDrawer drawer) {

      final INodeRenderingShape nodeShape = renderingStyle.getNodeShape(node, scaler);
      if (nodeShape != null) {
         nodeShape.draw(node, renderingStyle, scaler, drawer);
      }


      for (final GElementGeometryPair<IVector2, IGlobeFeature<IVector2, ? extends IBoundedGeometry2D<? extends IFiniteBounds<IVector2, ?>>>, IBoundedGeometry2D<? extends IFiniteBounds<IVector2, ?>>> pair : node.getElements()) {
         final IBoundedGeometry2D<? extends IFiniteBounds<IVector2, ?>> geometry = pair.getGeometry();
         drawGeometry(geometry, pair.getElement(), extendedRegion, renderingStyle, scaler, drawer);
      }

   }


   private void drawGeometry(final IBoundedGeometry2D<? extends IFiniteBounds<IVector2, ?>> geometry,
                             final IGlobeFeature<IVector2, ? extends IBoundedGeometry2D<? extends IFiniteBounds<IVector2, ?>>> feature,
                             final GAxisAlignedRectangle extendedRegion,
                             final IRenderingStyle renderingStyle,
                             final IVectorial2DRenderingScaler scaler,
                             final IVectorial2DDrawer drawer) {

      if (!geometry.getBounds().asAxisAlignedOrthotope().touches(extendedRegion)) {
         return;
      }

      if (geometry instanceof GMultiGeometry2D) {
         @SuppressWarnings("unchecked")
         final GMultiGeometry2D<IBoundedGeometry2D<? extends IFiniteBounds<IVector2, ?>>> multigeometry = (GMultiGeometry2D<IBoundedGeometry2D<? extends IFiniteBounds<IVector2, ?>>>) geometry;
         for (final IBoundedGeometry2D<? extends IFiniteBounds<IVector2, ?>> child : multigeometry) {
            drawGeometry(child, feature, extendedRegion, renderingStyle, scaler, drawer);
         }
      }
      else if (geometry instanceof IVector2) {
         final IVector2 point = (IVector2) geometry;
         final IRenderingSymbol symbol = renderingStyle.getPointSymbol(point, feature, scaler);
         if (symbol != null) {
            symbol.draw(point, feature, renderingStyle, scaler, drawer);
         }
      }
      else if (geometry instanceof ICurve2D<?>) {
         final ICurve2D<? extends IFiniteBounds<IVector2, ?>> curve = (ICurve2D<? extends IFiniteBounds<IVector2, ?>>) geometry;
         final ICurveRenderingShape<ICurve2D<? extends IFiniteBounds<IVector2, ?>>> shape = renderingStyle.getCurveShape(curve,
                  feature, scaler);
         if (shape != null) {
            shape.draw(curve, feature, renderingStyle, scaler, drawer);
         }
      }
      else if (geometry instanceof ISurface2D<?>) {
         final ISurface2D<? extends IFiniteBounds<IVector2, ?>> surface = (ISurface2D<? extends IFiniteBounds<IVector2, ?>>) geometry;
         final ISurfaceRenderingShape<ISurface2D<? extends IFiniteBounds<IVector2, ?>>> shape = renderingStyle.getSurfaceShape(
                  surface, feature, scaler);
         if (shape != null) {
            shape.draw(surface, feature, renderingStyle, scaler, drawer);
         }
      }
      else {
         System.out.println("Warning: geometry type " + geometry.getClass() + " not supported");
      }

   }


}
