

package es.igosoftware.euclid.experimental.vectorial.rendering;

import java.awt.image.BufferedImage;
import java.util.Collection;
import java.util.List;

import es.igosoftware.euclid.bounding.GAxisAlignedOrthotope;
import es.igosoftware.euclid.bounding.GAxisAlignedRectangle;
import es.igosoftware.euclid.ntree.GGeometryNTreeParameters;
import es.igosoftware.euclid.ntree.quadtree.GGeometryQuadtree;
import es.igosoftware.euclid.shape.IPolygon2D;
import es.igosoftware.euclid.vector.IVector2;


public class GPolygon2DRenderer {


   private final List<IPolygon2D<?>>              _polygons;
   private final GGeometryQuadtree<IPolygon2D<?>> _quadtree;


   public GPolygon2DRenderer(final List<IPolygon2D<?>> polygons) {
      _polygons = polygons;

      _quadtree = createQuadtree();
   }


   private GGeometryQuadtree<IPolygon2D<?>> createQuadtree() {
      final GGeometryNTreeParameters.AcceptLeafNodeCreationPolicy acceptLeafNodeCreationPolicy;
      acceptLeafNodeCreationPolicy = new GGeometryNTreeParameters.Accept2DLeafNodeCreationPolicy<IPolygon2D<?>>() {
         @Override
         public boolean accept(final int depth,
                               final GAxisAlignedOrthotope<IVector2<?>, ?> bounds,
                               final Collection<IPolygon2D<?>> geometries) {
            if (depth >= 10) {
               return true;
            }

            if (geometries.size() <= 1) {
               return true;
            }

            int totalPoints = 0;
            for (final IPolygon2D<?> geometry : geometries) {
               totalPoints += geometry.getPointsCount();
            }

            return (totalPoints <= 2048 * 2);
         }
      };

      final GGeometryNTreeParameters parameters = new GGeometryNTreeParameters(true, acceptLeafNodeCreationPolicy,
               GGeometryNTreeParameters.BoundsPolicy.DIMENSIONS_MULTIPLE_OF_SMALLEST_AND_CENTERED);

      return new GGeometryQuadtree<IPolygon2D<?>>("Rendering", null, _polygons, parameters);
   }


   public BufferedImage render(final GAxisAlignedRectangle region,
                               final GRenderingAttributes attributes) {
      return new GPolygon2DRenderUnit(_quadtree, region, attributes).render();
   }


}