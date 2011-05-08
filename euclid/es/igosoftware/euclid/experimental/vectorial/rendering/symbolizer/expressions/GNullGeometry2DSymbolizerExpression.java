

package es.igosoftware.euclid.experimental.vectorial.rendering.symbolizer.expressions;

import java.util.Collection;

import es.igosoftware.euclid.IGeometry2D;
import es.igosoftware.euclid.bounding.GAxisAlignedRectangle;
import es.igosoftware.euclid.experimental.vectorial.rendering.context.IProjectionTool;
import es.igosoftware.euclid.experimental.vectorial.rendering.context.IVectorial2DDrawer;
import es.igosoftware.euclid.experimental.vectorial.rendering.context.IVectorial2DRenderingScaler;
import es.igosoftware.euclid.experimental.vectorial.rendering.symbolizer.ISymbolizer2D;
import es.igosoftware.euclid.features.IGlobeFeature;
import es.igosoftware.euclid.features.IGlobeFeatureCollection;
import es.igosoftware.euclid.vector.IVectorI2;


public class GNullGeometry2DSymbolizerExpression
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
   public void preRender(final IVectorI2 renderExtent,
                         final IProjectionTool projectionTool,
                         final GAxisAlignedRectangle viewport,
                         final ISymbolizer2D renderingStyle,
                         final IVectorial2DDrawer drawer) {
   }


   @Override
   public Collection evaluate(final IGeometry2D geometry,
                              final IGlobeFeature feature,
                              final IVectorial2DRenderingScaler scaler) {
      return null;
   }


   @Override
   public void postRender(final IVectorI2 renderExtent,
                          final IProjectionTool projectionTool,
                          final GAxisAlignedRectangle viewport,
                          final ISymbolizer2D renderingStyle,
                          final IVectorial2DDrawer drawer) {
   }


}
