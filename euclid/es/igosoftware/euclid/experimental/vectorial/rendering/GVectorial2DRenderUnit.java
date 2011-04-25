

package es.igosoftware.euclid.experimental.vectorial.rendering;

import java.awt.image.BufferedImage;

import es.igosoftware.euclid.IBoundedGeometry2D;
import es.igosoftware.euclid.ICurve2D;
import es.igosoftware.euclid.IGeometry2D;
import es.igosoftware.euclid.ISurface2D;
import es.igosoftware.euclid.bounding.GAxisAlignedRectangle;
import es.igosoftware.euclid.bounding.IFinite2DBounds;
import es.igosoftware.euclid.experimental.measurement.GArea;
import es.igosoftware.euclid.experimental.measurement.IMeasure;
import es.igosoftware.euclid.experimental.vectorial.rendering.context.GVectorial2DRenderingScaler;
import es.igosoftware.euclid.experimental.vectorial.rendering.context.IProjectionTool;
import es.igosoftware.euclid.experimental.vectorial.rendering.context.IVectorial2DDrawer;
import es.igosoftware.euclid.experimental.vectorial.rendering.context.IVectorial2DRenderingScaler;
import es.igosoftware.euclid.experimental.vectorial.rendering.styledgeometries.GStyled2DGeometry;
import es.igosoftware.euclid.experimental.vectorial.rendering.styling.IRenderingStyle2D;
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
                      final GRenderingQuadtree<IGlobeFeature<IVector2, ? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>>> quadtree,
                      final GProjection projection,
                      final IProjectionTool projectionTool,
                      final GAxisAlignedRectangle viewport,
                      final IRenderingStyle2D renderingStyle,
                      final IVectorial2DDrawer drawer) {

      final IVectorial2DRenderingScaler scaler = new GVectorial2DRenderingScaler(viewport, projection, projectionTool,
               renderedImage.getWidth(), renderedImage.getHeight());

      final GAxisAlignedRectangle extendedViewport = calculateExtendedViewport(viewport, scaler, renderingStyle);

      processNode(quadtree.getRoot(), extendedViewport, renderingStyle, scaler, drawer);
   }


   private static GAxisAlignedRectangle calculateExtendedViewport(final GAxisAlignedRectangle viewport,
                                                                  final IVectorial2DRenderingScaler scaler,
                                                                  final IRenderingStyle2D renderingStyle) {
      final IMeasure<GArea> maximumSize = renderingStyle.getMaximumSize();

      final double areaInSquaredMeters = maximumSize.getValueInReferenceUnits();
      final double extent = GMath.sqrt(areaInSquaredMeters);
      final IVector2 lower = scaler.increment(viewport._lower, -extent, -extent);
      final IVector2 upper = scaler.increment(viewport._upper, extent, extent);

      return new GAxisAlignedRectangle(lower, upper);
   }


   private void processNode(final GGTNode<IVector2, IGlobeFeature<IVector2, ? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>>, IBoundedGeometry2D<? extends IFinite2DBounds<?>>> node,
                            final GAxisAlignedRectangle extendedRegion,
                            final IRenderingStyle2D renderingStyle,
                            final IVectorial2DRenderingScaler scaler,
                            final IVectorial2DDrawer drawer) {

      final GAxisAlignedRectangle nodeBounds = node.getMinimumBounds().asRectangle();

      if (!nodeBounds.touches(extendedRegion)) {
         return;
      }

      if (!renderingStyle.processNode(node, scaler, drawer)) {
         return;
      }

      final GStyled2DGeometry<? extends IGeometry2D> nodeSymbol = renderingStyle.getNodeSymbol(node, scaler);
      if (nodeSymbol != null) {
         nodeSymbol.draw(drawer, Double.POSITIVE_INFINITY, renderingStyle.isDebugRendering(), renderingStyle.isRenderLODIgnores());
      }

      if (node instanceof GGTInnerNode) {
         final GGTInnerNode<IVector2, IGlobeFeature<IVector2, ? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>>, IBoundedGeometry2D<? extends IFinite2DBounds<?>>> inner;
         inner = (GGTInnerNode<IVector2, IGlobeFeature<IVector2, ? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>>, IBoundedGeometry2D<? extends IFinite2DBounds<?>>>) node;

         for (final GGTNode<IVector2, IGlobeFeature<IVector2, ? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>>, IBoundedGeometry2D<? extends IFinite2DBounds<?>>> child : inner.getChildren()) {
            processNode(child, extendedRegion, renderingStyle, scaler, drawer);
         }
      }

      drawNode(node, extendedRegion, renderingStyle, scaler, drawer);
   }


   private void drawNode(final GGTNode<IVector2, IGlobeFeature<IVector2, ? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>>, IBoundedGeometry2D<? extends IFinite2DBounds<?>>> node,
                         final GAxisAlignedRectangle extendedRegion,
                         final IRenderingStyle2D renderingStyle,
                         final IVectorial2DRenderingScaler scaler,
                         final IVectorial2DDrawer drawer) {


      for (final GElementGeometryPair<IVector2, IGlobeFeature<IVector2, ? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>>, IBoundedGeometry2D<? extends IFinite2DBounds<?>>> pair : node.getElements()) {
         final IBoundedGeometry2D<? extends IFinite2DBounds<?>> geometry = pair.getGeometry();
         drawGeometry(geometry, pair.getElement(), extendedRegion, renderingStyle, scaler, drawer);
      }

   }


   private void drawGeometry(final IBoundedGeometry2D<? extends IFinite2DBounds<?>> geometry,
                             final IGlobeFeature<IVector2, ? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>> feature,
                             final GAxisAlignedRectangle extendedRegion,
                             final IRenderingStyle2D renderingStyle,
                             final IVectorial2DRenderingScaler scaler,
                             final IVectorial2DDrawer drawer) {

      if (!geometry.getBounds().asAxisAlignedOrthotope().touches(extendedRegion)) {
         return;
      }

      if (geometry instanceof GMultiGeometry2D) {
         @SuppressWarnings("unchecked")
         final GMultiGeometry2D<IBoundedGeometry2D<? extends IFinite2DBounds<?>>> multigeometry = (GMultiGeometry2D<IBoundedGeometry2D<? extends IFinite2DBounds<?>>>) geometry;
         for (final IBoundedGeometry2D<? extends IFinite2DBounds<?>> child : multigeometry) {
            drawGeometry(child, feature, extendedRegion, renderingStyle, scaler, drawer);
         }
      }
      else if (geometry instanceof IVector2) {
         final IVector2 point = (IVector2) geometry;

         final GStyled2DGeometry<? extends IGeometry2D> symbol = renderingStyle.getPointStyledSurface(point, feature, scaler);
         drawSymbol(symbol, renderingStyle, drawer);
      }
      else if (geometry instanceof ICurve2D<?>) {
         final ICurve2D<? extends IFinite2DBounds<?>> curve = (ICurve2D<? extends IFinite2DBounds<?>>) geometry;

         final GStyled2DGeometry<? extends IGeometry2D> symbol = renderingStyle.getStyledCurve(curve, feature, scaler);
         drawSymbol(symbol, renderingStyle, drawer);
      }
      else if (geometry instanceof ISurface2D<?>) {
         final ISurface2D<? extends IFinite2DBounds<?>> surface = (ISurface2D<? extends IFinite2DBounds<?>>) geometry;

         final GStyled2DGeometry<? extends IGeometry2D> symbol = renderingStyle.getStyledSurface(surface, feature, scaler);
         drawSymbol(symbol, renderingStyle, drawer);
      }
      else {
         System.out.println("Warning: geometry type " + geometry.getClass() + " not supported");
      }

   }


   private void drawSymbol(final GStyled2DGeometry<? extends IGeometry2D> symbol,
                           final IRenderingStyle2D renderingStyle,
                           final IVectorial2DDrawer drawer) {
      if (symbol != null) {
         symbol.draw(drawer, renderingStyle.getLODMinSize(), renderingStyle.isDebugRendering(),
                  renderingStyle.isRenderLODIgnores());
      }
   }


}
