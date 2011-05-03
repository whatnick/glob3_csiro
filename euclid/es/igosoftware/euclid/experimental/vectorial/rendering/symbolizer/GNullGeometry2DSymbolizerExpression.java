

package es.igosoftware.euclid.experimental.vectorial.rendering.symbolizer;

import java.awt.image.BufferedImage;
import java.util.Collection;

import es.igosoftware.euclid.IGeometry2D;
import es.igosoftware.euclid.experimental.vectorial.rendering.context.IVectorial2DRenderingScaler;
import es.igosoftware.euclid.features.IGlobeFeature;
import es.igosoftware.euclid.features.IGlobeFeatureCollection;


class GNullGeometry2DSymbolizerExpression
         implements
            IGeometry2DSymbolizerExpression {

   public static final GNullGeometry2DSymbolizerExpression INSTANCE = new GNullGeometry2DSymbolizerExpression();


   private GNullGeometry2DSymbolizerExpression() {
   }


   @Override
   public double getMaximumSizeInMeters(final IVectorial2DRenderingScaler scaler) {
      return 0;
   }


   @Override
   public void preprocessFeatures(final IGlobeFeatureCollection features) {
   }


   @Override
   public void preRenderImage(final BufferedImage image) {
   }


   @Override
   public Collection evaluate(final IGeometry2D geometry,
                              final IGlobeFeature feature,
                              final IVectorial2DRenderingScaler scaler) {
      return null;
   }


   @Override
   public void postRenderImage(final BufferedImage image) {
   }


}
