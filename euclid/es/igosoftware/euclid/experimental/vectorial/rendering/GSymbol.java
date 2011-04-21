

package es.igosoftware.euclid.experimental.vectorial.rendering;

import es.igosoftware.euclid.IBoundedGeometry;
import es.igosoftware.euclid.bounding.IFiniteBounds;
import es.igosoftware.euclid.features.IGlobeFeature;
import es.igosoftware.euclid.vector.IVector2;


public abstract class GSymbol {


   public abstract boolean isBiggerThan(final double lodMinSize);


   public final void draw(final IVector2 point,
                          final IGlobeFeature<IVector2, ? extends IBoundedGeometry<IVector2, ? extends IFiniteBounds<IVector2, ?>>> feature,
                          final GVectorialRenderingContext rc) {
      if (isBiggerThan(rc._renderingStyle.getLODMinSize())) {
         rawDraw(point, feature, rc);
      }
      else {
         if (rc._renderingStyle.isRenderLODIgnores() || rc._renderingStyle.isDebugRendering()) {
            renderLODIgnore(point, feature, rc);
         }
      }
   }


   protected abstract void renderLODIgnore(final IVector2 point,
                                           final IGlobeFeature<IVector2, ? extends IBoundedGeometry<IVector2, ? extends IFiniteBounds<IVector2, ?>>> feature,
                                           final GVectorialRenderingContext rc);


   protected abstract void rawDraw(final IVector2 point,
                                   final IGlobeFeature<IVector2, ? extends IBoundedGeometry<IVector2, ? extends IFiniteBounds<IVector2, ?>>> feature,
                                   final GVectorialRenderingContext rc);


}
