

package es.igosoftware.euclid.experimental.vectorial.rendering.features;

import es.igosoftware.euclid.IBoundedGeometry;
import es.igosoftware.euclid.bounding.IFiniteBounds;
import es.igosoftware.euclid.experimental.vectorial.rendering.context.IVectorial2DDrawer;
import es.igosoftware.euclid.experimental.vectorial.rendering.context.IVectorial2DRenderingScaler;
import es.igosoftware.euclid.experimental.vectorial.rendering.styling.IRenderingStyle;
import es.igosoftware.euclid.features.IGlobeFeature;
import es.igosoftware.euclid.vector.IVector2;


public interface IRenderingFeature<

GeometryT extends IBoundedGeometry<IVector2, ? extends IFiniteBounds<IVector2, ?>>

> {


   public boolean isBiggerThan(final double lodMinSize);


   public void draw(final GeometryT geometry,
                    final IGlobeFeature<IVector2, ? extends IBoundedGeometry<IVector2, ? extends IFiniteBounds<IVector2, ?>>> feature,
                    final IRenderingStyle renderingStyle,
                    final IVectorial2DRenderingScaler scaler,
                    final IVectorial2DDrawer drawer);


}
