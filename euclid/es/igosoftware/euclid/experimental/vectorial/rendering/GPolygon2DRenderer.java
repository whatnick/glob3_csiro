

package es.igosoftware.euclid.experimental.vectorial.rendering;

import java.awt.image.BufferedImage;
import java.util.Collection;

import es.igosoftware.euclid.IBoundedGeometry;
import es.igosoftware.euclid.bounding.GAxisAlignedOrthotope;
import es.igosoftware.euclid.bounding.GAxisAlignedRectangle;
import es.igosoftware.euclid.features.IGlobeFeature;
import es.igosoftware.euclid.features.IGlobeFeatureCollection;
import es.igosoftware.euclid.ntree.GGeometryNTreeParameters;
import es.igosoftware.euclid.vector.IVector2;
import es.igosoftware.util.ITransformer;


public class GPolygon2DRenderer {


   private final IGlobeFeatureCollection<IVector2<?>, GAxisAlignedRectangle, ?> _features;
   private final GRenderingQuadtree                                             _quadtree;


   public GPolygon2DRenderer(final IGlobeFeatureCollection<IVector2<?>, GAxisAlignedRectangle, ?> features) {
      _features = features;

      _quadtree = createQuadtree();
   }


   private GRenderingQuadtree createQuadtree() {
      final GGeometryNTreeParameters.AcceptLeafNodeCreationPolicy acceptLeafNodeCreationPolicy;
      acceptLeafNodeCreationPolicy = new GGeometryNTreeParameters.Accept2DLeafNodeCreationPolicy<IGlobeFeature<IVector2<?>, GAxisAlignedRectangle>, IBoundedGeometry<IVector2<?>, ?, GAxisAlignedRectangle>>() {

         @Override
         public boolean accept(final int depth,
                               final GAxisAlignedOrthotope<IVector2<?>, ?> bounds,
                               final Collection<IGlobeFeature<IVector2<?>, GAxisAlignedRectangle>> elements) {
            if (depth >= 10) {
               return true;
            }

            return elements.size() <= 2;
         }
      };

      final GGeometryNTreeParameters parameters = new GGeometryNTreeParameters(true, acceptLeafNodeCreationPolicy,
               GGeometryNTreeParameters.BoundsPolicy.MINIMUM, true);

      final ITransformer<IGlobeFeature<IVector2<?>, GAxisAlignedRectangle>, IBoundedGeometry<IVector2<?>, ?, GAxisAlignedRectangle>> transformer = new ITransformer<IGlobeFeature<IVector2<?>, GAxisAlignedRectangle>, IBoundedGeometry<IVector2<?>, ?, GAxisAlignedRectangle>>() {

         @Override
         public IBoundedGeometry<IVector2<?>, ?, GAxisAlignedRectangle> transform(final IGlobeFeature<IVector2<?>, GAxisAlignedRectangle> element) {
            return element.getDefaultGeometry();
         }
      };

      return new GRenderingQuadtree("Rendering", null, _features, transformer, parameters);
   }


   public BufferedImage render(final GAxisAlignedRectangle region,
                               final GRenderingAttributes attributes) {
      final IPolygon2DRenderUnit renderUnit = new GPolygon2DRenderUnit();
      return renderUnit.render(_quadtree, region, attributes);
   }


}
