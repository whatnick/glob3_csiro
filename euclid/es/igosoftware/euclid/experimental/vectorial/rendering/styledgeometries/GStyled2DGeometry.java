

package es.igosoftware.euclid.experimental.vectorial.rendering.styledgeometries;

import java.awt.Color;
import java.util.Collection;

import es.igosoftware.euclid.IBoundedGeometry2D;
import es.igosoftware.euclid.bounding.GAxisAlignedRectangle;
import es.igosoftware.euclid.bounding.IFinite2DBounds;
import es.igosoftware.euclid.experimental.vectorial.rendering.context.IVectorial2DDrawer;
import es.igosoftware.euclid.vector.IVector2;
import es.igosoftware.util.GAssert;


public abstract class GStyled2DGeometry<

GeometryT extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>

> {


   protected final GeometryT _geometry;


   protected GStyled2DGeometry(final GeometryT geometry) {
      GAssert.notNull(geometry, "geometry");

      _geometry = geometry;
   }


   public final void draw(final IVectorial2DDrawer drawer,
                          final double lodMinSize,
                          final boolean debugRendering,
                          final boolean renderLODIgnores) {
      draw(drawer, lodMinSize, debugRendering, renderLODIgnores, null);
   }


   public final void draw(final IVectorial2DDrawer drawer,
                          final double lodMinSize,
                          final boolean debugRendering,
                          final boolean renderLODIgnores,
                          final String label) {
      if (isBigger(lodMinSize)) {
         draw(drawer, debugRendering);
         if (label != null) {
            drawLabel(drawer, label);
         }
      }
      else {
         if (renderLODIgnores) {
            drawLODIgnore(drawer, debugRendering);
         }
      }
   }


   private void drawLabel(final IVectorial2DDrawer drawer,
                          final String label) {

      final IVector2 position = getBounds().getCentroid();

      drawer.drawShadowedStringCentered(label, position, Color.BLACK, 1, new Color(255, 255, 255, 127));
   }


   protected abstract boolean isBigger(final double lodMinSize);


   protected abstract void draw(final IVectorial2DDrawer drawer,
                                final boolean debugRendering);


   protected abstract void drawLODIgnore(final IVectorial2DDrawer drawer,
                                         final boolean debugRendering);


   public GeometryT getGeometry() {
      return _geometry;
   }


   public abstract boolean isGroupable();


   public abstract boolean isGroupableWith(final GStyled2DGeometry<? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>> that);


   public abstract GAxisAlignedRectangle getBounds();


   public void drawGroup(final Collection<? extends GStyled2DGeometry<? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>>> cluster,
                         final IVectorial2DDrawer drawer,
                         final double lodMinSize,
                         final boolean debugRendering,
                         final boolean renderLODIgnores) {
      final String label = Integer.toString(cluster.size());
      draw(drawer, lodMinSize, debugRendering, renderLODIgnores, label);
   }


}
