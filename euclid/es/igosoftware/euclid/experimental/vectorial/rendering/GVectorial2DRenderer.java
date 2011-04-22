

package es.igosoftware.euclid.experimental.vectorial.rendering;

import java.awt.image.BufferedImage;
import java.util.Collection;

import es.igosoftware.euclid.IBoundedGeometry;
import es.igosoftware.euclid.bounding.GAxisAlignedOrthotope;
import es.igosoftware.euclid.bounding.GAxisAlignedRectangle;
import es.igosoftware.euclid.bounding.IFiniteBounds;
import es.igosoftware.euclid.experimental.vectorial.rendering.styling.IRenderingStyle;
import es.igosoftware.euclid.experimental.vectorial.rendering.utils.GRenderingQuadtree;
import es.igosoftware.euclid.features.IGlobeFeature;
import es.igosoftware.euclid.features.IGlobeFeatureCollection;
import es.igosoftware.euclid.ntree.GElementGeometryPair;
import es.igosoftware.euclid.ntree.GGeometryNTreeParameters;
import es.igosoftware.euclid.vector.IVector2;
import es.igosoftware.util.GAssert;


public class GVectorial2DRenderer {

   private final IGlobeFeatureCollection<IVector2, ? extends IBoundedGeometry<IVector2, ? extends IFiniteBounds<IVector2, ?>>>           _features;
   private final GRenderingQuadtree<IGlobeFeature<IVector2, ? extends IBoundedGeometry<IVector2, ? extends IFiniteBounds<IVector2, ?>>>> _quadtree;


   public GVectorial2DRenderer(final IGlobeFeatureCollection<IVector2, ? extends IBoundedGeometry<IVector2, ? extends IFiniteBounds<IVector2, ?>>> features,
                               final boolean verbose) {
      _features = features;

      _quadtree = createQuadtree(verbose);
   }


   private GRenderingQuadtree<IGlobeFeature<IVector2, ? extends IBoundedGeometry<IVector2, ? extends IFiniteBounds<IVector2, ?>>>> createQuadtree(final boolean verbose) {
      final GGeometryNTreeParameters.AcceptLeafNodeCreationPolicy acceptLeafNodeCreationPolicy;
      acceptLeafNodeCreationPolicy = new GGeometryNTreeParameters.Accept2DLeafNodeCreationPolicy<IGlobeFeature<IVector2, ? extends IBoundedGeometry<IVector2, ? extends IFiniteBounds<IVector2, ?>>>>() {

         @Override
         public boolean acceptLeafNodeCreation(final int depth,
                                               final GAxisAlignedOrthotope<IVector2, ?> bounds,
                                               final Collection<GElementGeometryPair<IVector2, IGlobeFeature<IVector2, ? extends IBoundedGeometry<IVector2, ? extends IFiniteBounds<IVector2, ?>>>>> elements) {
            if (depth >= 12) {
               return true;
            }

            //            int verticesCounter = 0;
            //            for (final GElementGeometryPair<IVector2, IGlobeFeature<IVector2, ? extends IBoundedGeometry<IVector2, ? extends IFiniteBounds<IVector2, ?>>>> pair : elements) {
            //               final IBoundedGeometry<IVector2, ? extends IFiniteBounds<IVector2, ?>> geometry = pair.getGeometry();
            //
            //               if (geometry instanceof IVector2) {
            //                  verticesCounter++;
            //               }
            //               else if (geometry instanceof IPointsContainer) {
            //                  verticesCounter += ((IPointsContainer) geometry).getPointsCount();
            //               }
            //               else if (geometry instanceof ICurve) {
            //                  verticesCounter += ((ICurve) geometry).getVerticesCount();
            //               }
            //               else if (geometry instanceof ISurface) {
            //                  verticesCounter += ((ISurface) geometry).getVerticesCount();
            //               }
            //               else {
            //                  verticesCounter += 5; // estimation
            //               }
            //            }
            //
            //            return (verticesCounter <= 400);
            return (elements.size() <= 50);
         }
      };


      final GGeometryNTreeParameters parameters = new GGeometryNTreeParameters(verbose, acceptLeafNodeCreationPolicy,
               GGeometryNTreeParameters.BoundsPolicy.GIVEN, true);

      final GAxisAlignedRectangle bounds = _features.getBounds().asRectangle().expandedByPercent(0.05);
      return new GRenderingQuadtree<IGlobeFeature<IVector2, ? extends IBoundedGeometry<IVector2, ? extends IFiniteBounds<IVector2, ?>>>>(
               "Rendering", _features, parameters, bounds);
   }


   public GRenderingQuadtree<IGlobeFeature<IVector2, ? extends IBoundedGeometry<IVector2, ? extends IFiniteBounds<IVector2, ?>>>> getQuadtree() {
      return _quadtree;
   }


   public void render(final GAxisAlignedRectangle region,
                      final BufferedImage image,
                      final IRenderingStyle renderingStyle) {
      GAssert.notNull(region, "region");
      GAssert.notNull(image, "image");
      GAssert.notNull(renderingStyle, "renderingStyle");

      final IVectorial2DRenderUnit renderUnit = new GVectorial2DRenderUnit();

      renderingStyle.preprocessFeatures(_features);

      renderingStyle.preRenderImage(image);

      renderUnit.render(image, _quadtree, _features.getProjection(), region, renderingStyle);

      renderingStyle.postRenderImage(image);
   }


   public BufferedImage render(final GAxisAlignedRectangle region,
                               final int imageWidth,
                               final int imageHeight,
                               final IRenderingStyle renderingStyle) {
      GAssert.isPositive(imageWidth, "imageWidth");
      GAssert.isPositive(imageHeight, "imageHeight");

      final BufferedImage image = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_4BYTE_ABGR);
      image.setAccelerationPriority(1);

      render(region, image, renderingStyle);

      return image;
   }


}
