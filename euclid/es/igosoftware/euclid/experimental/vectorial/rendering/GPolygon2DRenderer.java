

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
import es.igosoftware.util.ITransformer;


public class GPolygon2DRenderer {


   //   private final IGlobeFeatureCollection<IVector2<?>, GAxisAlignedRectangle, ?> _features; 
   private final IGlobeFeatureCollection<IVector2<?>, IBoundedGeometry<IVector2<?>, ?, ? extends IFiniteBounds<IVector2<?>, ?>>, ?>                                                                                   _features;
   private final GRenderingQuadtree<IGlobeFeature<IVector2<?>, IBoundedGeometry<IVector2<?>, ?, ? extends IFiniteBounds<IVector2<?>, ?>>>, IBoundedGeometry<IVector2<?>, ?, ? extends IFiniteBounds<IVector2<?>, ?>>> _quadtree;


   public GPolygon2DRenderer(final IGlobeFeatureCollection<IVector2<?>, IBoundedGeometry<IVector2<?>, ?, ? extends IFiniteBounds<IVector2<?>, ?>>, ?> features) {
      _features = features;

      _quadtree = createQuadtree();
   }


   private GRenderingQuadtree<IGlobeFeature<IVector2<?>, IBoundedGeometry<IVector2<?>, ?, ? extends IFiniteBounds<IVector2<?>, ?>>>, IBoundedGeometry<IVector2<?>, ?, ? extends IFiniteBounds<IVector2<?>, ?>>> createQuadtree() {
      final GGeometryNTreeParameters.AcceptLeafNodeCreationPolicy acceptLeafNodeCreationPolicy;
      acceptLeafNodeCreationPolicy = new GGeometryNTreeParameters.Accept2DLeafNodeCreationPolicy<IGlobeFeature<IVector2<?>, IBoundedGeometry<IVector2<?>, ?, GAxisAlignedRectangle>>, IBoundedGeometry<IVector2<?>, ?, GAxisAlignedRectangle>>() {
         @Override
         public boolean accept(final int depth,
                               final GAxisAlignedOrthotope<IVector2<?>, ?> bounds,
                               final Collection<IGlobeFeature<IVector2<?>, IBoundedGeometry<IVector2<?>, ?, GAxisAlignedRectangle>>> elements) {
            if (depth >= 10) {
               return true;
            }

            return elements.size() <= 2;
         }
      };


      final GGeometryNTreeParameters parameters = new GGeometryNTreeParameters(true, acceptLeafNodeCreationPolicy,
               GGeometryNTreeParameters.BoundsPolicy.MINIMUM, true);

      final ITransformer<IGlobeFeature<IVector2<?>, IBoundedGeometry<IVector2<?>, ?, ? extends IFiniteBounds<IVector2<?>, ?>>>, IBoundedGeometry<IVector2<?>, ?, ? extends IFiniteBounds<IVector2<?>, ?>>> transformer;
      transformer = new ITransformer<IGlobeFeature<IVector2<?>, IBoundedGeometry<IVector2<?>, ?, ? extends IFiniteBounds<IVector2<?>, ?>>>, IBoundedGeometry<IVector2<?>, ?, ? extends IFiniteBounds<IVector2<?>, ?>>>() {
         @Override
         public IBoundedGeometry<IVector2<?>, ?, ? extends IFiniteBounds<IVector2<?>, ?>> transform(final IGlobeFeature<IVector2<?>, IBoundedGeometry<IVector2<?>, ?, ? extends IFiniteBounds<IVector2<?>, ?>>> element) {
            return element.getDefaultGeometry();
         }
      };


      //      return new GRenderingQuadtree<FeatureT, GeometryT>("Rendering", null, _features, transformer, parameters);
      return new GRenderingQuadtree<IGlobeFeature<IVector2<?>, IBoundedGeometry<IVector2<?>, ?, ? extends IFiniteBounds<IVector2<?>, ?>>>, IBoundedGeometry<IVector2<?>, ?, ? extends IFiniteBounds<IVector2<?>, ?>>>(
               "Rendering", null, _features, transformer, parameters);
   }


   public BufferedImage render(final GAxisAlignedRectangle region,
                               final GRenderingAttributes attributes) {
      final IPolygon2DRenderUnit renderUnit = new GPolygon2DRenderUnit();
      return renderUnit.render(_quadtree, region, attributes);
   }


}
