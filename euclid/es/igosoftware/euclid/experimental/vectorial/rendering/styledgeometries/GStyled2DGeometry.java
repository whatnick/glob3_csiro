

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


   public abstract void draw(final IVectorial2DDrawer drawer);


}
