

package es.igosoftware.euclid.experimental.vectorial.rendering;

import java.awt.image.BufferedImage;
import java.util.Collection;

import es.igosoftware.euclid.IBoundedGeometry;
import es.igosoftware.euclid.bounding.GAxisAlignedOrthotope;
import es.igosoftware.euclid.bounding.GAxisAlignedRectangle;
import es.igosoftware.euclid.bounding.IFiniteBounds;
import es.igosoftware.euclid.features.IGlobeFeature;
import es.igosoftware.euclid.features.IGlobeFeatureCollection;
import es.igosoftware.euclid.ntree.GGeometryNTreeParameters;
import es.igosoftware.euclid.vector.IVector2;


public class GVectorial2DRenderer {

   private final IGlobeFeatureCollection<IVector2, ? extends IBoundedGeometry<IVector2, ? extends IFiniteBounds<IVector2, ?>>>           _features;
   private final GRenderingQuadtree<IGlobeFeature<IVector2, ? extends IBoundedGeometry<IVector2, ? extends IFiniteBounds<IVector2, ?>>>> _quadtree;


   public GVectorial2DRenderer(final IGlobeFeatureCollection<IVector2, ? extends IBoundedGeometry<IVector2, ? extends IFiniteBounds<IVector2, ?>>> features) {
      _features = features;

      _quadtree = createQuadtree();
   }


   private GRenderingQuadtree<IGlobeFeature<IVector2, ? extends IBoundedGeometry<IVector2, ? extends IFiniteBounds<IVector2, ?>>>> createQuadtree() {
      final GGeometryNTreeParameters.AcceptLeafNodeCreationPolicy acceptLeafNodeCreationPolicy;
      acceptLeafNodeCreationPolicy = new GGeometryNTreeParameters.Accept2DLeafNodeCreationPolicy<IGlobeFeature<IVector2, ? extends IBoundedGeometry<IVector2, ? extends IFiniteBounds<IVector2, ?>>>>() {
         @Override
         public boolean accept(final int depth,
                               final GAxisAlignedOrthotope<IVector2, ?> bounds,
                               final Collection<IGlobeFeature<IVector2, ? extends IBoundedGeometry<IVector2, ? extends IFiniteBounds<IVector2, ?>>>> elements) {
            return (depth >= 10) || (elements.size() <= 2);
         }
      };


      final GGeometryNTreeParameters parameters = new GGeometryNTreeParameters(true, acceptLeafNodeCreationPolicy,
               GGeometryNTreeParameters.BoundsPolicy.MINIMUM, true);

      // TODO: evaluate_if_multigeometries_must_go_into_the_quadtree;
      return new GRenderingQuadtree<IGlobeFeature<IVector2, ? extends IBoundedGeometry<IVector2, ? extends IFiniteBounds<IVector2, ?>>>>(
               "Rendering", _features, parameters);

   }


   public BufferedImage render(final GAxisAlignedRectangle region,
                               final GRenderingAttributes attributes) {
      final IVectorial2DRenderUnit renderUnit = new GVectorial2DRenderUnit();
      return renderUnit.render(_quadtree, region, attributes);
   }


}
