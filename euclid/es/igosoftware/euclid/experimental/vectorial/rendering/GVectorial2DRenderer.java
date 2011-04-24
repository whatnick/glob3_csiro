

package es.igosoftware.euclid.experimental.vectorial.rendering;

import java.awt.image.BufferedImage;
import java.util.Collection;

import es.igosoftware.euclid.IBoundedGeometry2D;
import es.igosoftware.euclid.bounding.GAxisAlignedOrthotope;
import es.igosoftware.euclid.bounding.GAxisAlignedRectangle;
import es.igosoftware.euclid.bounding.IFinite2DBounds;
import es.igosoftware.euclid.experimental.vectorial.rendering.context.GJava2DVectorial2DDrawer;
import es.igosoftware.euclid.experimental.vectorial.rendering.context.IProjectionTool;
import es.igosoftware.euclid.experimental.vectorial.rendering.context.IVectorial2DDrawer;
import es.igosoftware.euclid.experimental.vectorial.rendering.styling.IRenderingStyle2D;
import es.igosoftware.euclid.experimental.vectorial.rendering.utils.GRenderingQuadtree;
import es.igosoftware.euclid.features.IGlobeFeature;
import es.igosoftware.euclid.features.IGlobeFeatureCollection;
import es.igosoftware.euclid.ntree.GElementGeometryPair;
import es.igosoftware.euclid.ntree.GGeometryNTreeParameters;
import es.igosoftware.euclid.vector.IVector2;
import es.igosoftware.util.GAssert;


public class GVectorial2DRenderer {

   private final IGlobeFeatureCollection<IVector2, ? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>>           _features;
   private final GRenderingQuadtree<IGlobeFeature<IVector2, ? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>>> _quadtree;


   public GVectorial2DRenderer(final IGlobeFeatureCollection<IVector2, ? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>> features,
                               final boolean verbose) {
      _features = features;

      _quadtree = createQuadtree(verbose);
   }


   private GRenderingQuadtree<IGlobeFeature<IVector2, ? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>>> createQuadtree(final boolean verbose) {
      final GGeometryNTreeParameters.AcceptLeafNodeCreationPolicy acceptLeafNodeCreationPolicy;
      acceptLeafNodeCreationPolicy = new GGeometryNTreeParameters.Accept2DLeafNodeCreationPolicy<

      IGlobeFeature<IVector2, ? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>>,

      IBoundedGeometry2D<? extends IFinite2DBounds<?>>

      >() {

         @Override
         public boolean acceptLeafNodeCreation(final int depth,
                                               final GAxisAlignedOrthotope<IVector2, ?> bounds,
                                               final Collection<GElementGeometryPair<IVector2, IGlobeFeature<IVector2, ? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>>, IBoundedGeometry2D<? extends IFinite2DBounds<?>>>> elements) {
            if (depth >= 12) {
               return true;
            }

            return (elements.size() <= 50);
         }
      };


      final GGeometryNTreeParameters parameters = new GGeometryNTreeParameters(verbose, acceptLeafNodeCreationPolicy,
               GGeometryNTreeParameters.BoundsPolicy.GIVEN, true);

      final GAxisAlignedRectangle bounds = _features.getBounds().asRectangle().expandedByPercent(0.05);
      return new GRenderingQuadtree<IGlobeFeature<IVector2, ? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>>>(
               "Rendering", _features, parameters, bounds);
   }


   public GRenderingQuadtree<IGlobeFeature<IVector2, ? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>>> getQuadtree() {
      return _quadtree;
   }


   public void render(final GAxisAlignedRectangle viewport,
                      final BufferedImage image,
                      final IProjectionTool projectionTool,
                      final IRenderingStyle2D renderingStyle,
                      final IVectorial2DDrawer drawer) {
      GAssert.notNull(viewport, "viewport");
      GAssert.notNull(image, "image");
      GAssert.notNull(renderingStyle, "renderingStyle");

      renderingStyle.preprocessFeatures(_features);

      renderingStyle.preRenderImage(image);

      final IVectorial2DRenderUnit renderUnit = new GVectorial2DRenderUnit();
      renderUnit.render(image, _quadtree, _features.getProjection(), projectionTool, viewport, renderingStyle, drawer);

      renderingStyle.postRenderImage(image);
   }


   public BufferedImage getRenderedImage(final GAxisAlignedRectangle viewport,
                                         final int imageWidth,
                                         final int imageHeight,
                                         final IProjectionTool projectionTool,
                                         final IRenderingStyle2D renderingStyle) {
      GAssert.notNull(viewport, "viewport");
      GAssert.isPositive(imageWidth, "imageWidth");
      GAssert.isPositive(imageHeight, "imageHeight");
      GAssert.notNull(renderingStyle, "renderingStyle");

      final BufferedImage image = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_4BYTE_ABGR);
      image.setAccelerationPriority(1);

      final IVectorial2DDrawer drawer = new GJava2DVectorial2DDrawer(image);

      render(viewport, image, projectionTool, renderingStyle, drawer);

      return image;
   }


}
