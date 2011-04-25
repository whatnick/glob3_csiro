

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
                          final double lodMinSize) {
      if (getSize() > lodMinSize) {
         rawDraw(drawer);
      }
      else {
         drawLODIgnore(drawer);
      }
   }


   protected abstract void rawDraw(final IVectorial2DDrawer drawer);


   protected abstract double getSize();


   protected abstract void drawLODIgnore(final IVectorial2DDrawer drawer);


}
