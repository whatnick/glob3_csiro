

package es.igosoftware.euclid.experimental.vectorial.rendering.styledgeometries;

import es.igosoftware.euclid.IGeometry2D;
import es.igosoftware.euclid.experimental.vectorial.rendering.context.IVectorial2DDrawer;
import es.igosoftware.util.GAssert;


public abstract class GStyled2DGeometry<GeometryT extends IGeometry2D> {


   protected final GeometryT _geometry;


   protected GStyled2DGeometry(final GeometryT geometry) {
      GAssert.notNull(geometry, "geometry");

      _geometry = geometry;
   }


   public final void draw(final IVectorial2DDrawer drawer,
                          final double lodMinSize,
                          final boolean debugRendering,
                          final boolean renderLODIgnores) {
      if (isBigger(lodMinSize)) {
         draw(drawer, debugRendering);
      }
      else {
         if (renderLODIgnores) {
            drawLODIgnore(drawer, debugRendering);
         }
      }
   }


   protected abstract boolean isBigger(final double lodMinSize);


   protected abstract void draw(final IVectorial2DDrawer drawer,
                                final boolean debugRendering);


   protected abstract void drawLODIgnore(final IVectorial2DDrawer drawer,
                                         final boolean debugRendering);


}
