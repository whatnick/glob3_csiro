

package es.igosoftware.euclid.experimental.vectorial.rendering;

import es.igosoftware.euclid.IBoundedGeometry;
import es.igosoftware.euclid.bounding.IFiniteBounds;
import es.igosoftware.euclid.features.IGlobeFeature;
import es.igosoftware.euclid.vector.IVector2;


public abstract class GRenderingFeature<

GeometryT extends IBoundedGeometry<IVector2, ? extends IFiniteBounds<IVector2, ?>>

> {


   public abstract boolean isBiggerThan(final double lodMinSize);


   public final void draw(final GeometryT geometry,
                          final IGlobeFeature<IVector2, ? extends IBoundedGeometry<IVector2, ? extends IFiniteBounds<IVector2, ?>>> feature,
                          final IRenderingStyle renderingStyle,
                          final IVectorial2DRenderingContext rc) {
      if (isBiggerThan(renderingStyle.getLODMinSize())) {
         rawDraw(geometry, feature, renderingStyle, rc);
      }
      else {
         if (renderingStyle.isRenderLODIgnores() || renderingStyle.isDebugRendering()) {
            renderLODIgnore(geometry, feature, renderingStyle, rc);
         }
      }
   }


   protected abstract void renderLODIgnore(final GeometryT geometry,
                                           final IGlobeFeature<IVector2, ? extends IBoundedGeometry<IVector2, ? extends IFiniteBounds<IVector2, ?>>> feature,
                                           final IRenderingStyle renderingStyle,
                                           final IVectorial2DRenderingContext rc);


   protected abstract void rawDraw(final GeometryT geometry,
                                   final IGlobeFeature<IVector2, ? extends IBoundedGeometry<IVector2, ? extends IFiniteBounds<IVector2, ?>>> feature,
                                   final IRenderingStyle renderingStyle,
                                   final IVectorial2DRenderingContext rc);


}
