

package es.igosoftware.euclid.experimental.vectorial.rendering.features;

import es.igosoftware.euclid.IBoundedGeometry2D;
import es.igosoftware.euclid.bounding.IFinite2DBounds;
import es.igosoftware.euclid.experimental.vectorial.rendering.context.IVectorial2DDrawer;
import es.igosoftware.euclid.experimental.vectorial.rendering.context.IVectorial2DRenderingScaler;
import es.igosoftware.euclid.experimental.vectorial.rendering.styling.IRenderingStyle2D;
import es.igosoftware.euclid.features.IGlobeFeature;
import es.igosoftware.euclid.vector.IVector2;


public abstract class G2DRenderingFeatureAbstract<

GeometryT extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>

>
         implements
            I2DRenderingFeature<GeometryT> {


   @Override
   public final void draw(final GeometryT geometry,
                          final IGlobeFeature<IVector2, ? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>> feature,
                          final IRenderingStyle2D renderingStyle,
                          final IVectorial2DRenderingScaler scaler,
                          final IVectorial2DDrawer drawer) {
      if (isBiggerThan(renderingStyle.getLODMinSize())) {
         rawDraw(geometry, feature, renderingStyle, scaler, drawer);
      }
      else {
         if (renderingStyle.isRenderLODIgnores() || renderingStyle.isDebugRendering()) {
            renderLODIgnore(geometry, feature, renderingStyle, scaler, drawer);
         }
      }
   }


   protected abstract void rawDraw(final GeometryT geometry,
                                   final IGlobeFeature<IVector2, ? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>> feature,
                                   final IRenderingStyle2D renderingStyle,
                                   final IVectorial2DRenderingScaler scaler,
                                   final IVectorial2DDrawer drawer);


   protected abstract void renderLODIgnore(final GeometryT geometry,
                                           final IGlobeFeature<IVector2, ? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>> feature,
                                           final IRenderingStyle2D renderingStyle,
                                           final IVectorial2DRenderingScaler scaler,
                                           final IVectorial2DDrawer drawer);


}
