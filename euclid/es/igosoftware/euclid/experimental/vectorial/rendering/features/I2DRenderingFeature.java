

package es.igosoftware.euclid.experimental.vectorial.rendering.features;

import es.igosoftware.euclid.IBoundedGeometry2D;
import es.igosoftware.euclid.bounding.IFinite2DBounds;
import es.igosoftware.euclid.experimental.vectorial.rendering.context.IVectorial2DDrawer;
import es.igosoftware.euclid.experimental.vectorial.rendering.context.IVectorial2DRenderingScaler;
import es.igosoftware.euclid.experimental.vectorial.rendering.styling.IRenderingStyle2D;
import es.igosoftware.euclid.features.IGlobeFeature;
import es.igosoftware.euclid.vector.IVector2;


public interface I2DRenderingFeature<

GeometryT extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>

> {


   public boolean isBiggerThan(final double lodMinSize);


   public void draw(final GeometryT geometry,
                    final IGlobeFeature<IVector2, ? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>> feature,
                    final IRenderingStyle2D renderingStyle,
                    final IVectorial2DRenderingScaler scaler,
                    final IVectorial2DDrawer drawer);


}
