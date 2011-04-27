

package es.igosoftware.euclid.experimental.vectorial.rendering.styledgeometries;

import java.awt.Color;
import java.util.Collection;

import es.igosoftware.euclid.IBoundedGeometry2D;
import es.igosoftware.euclid.bounding.GAxisAlignedRectangle;
import es.igosoftware.euclid.bounding.IFinite2DBounds;
import es.igosoftware.euclid.experimental.vectorial.rendering.context.IVectorial2DDrawer;
import es.igosoftware.euclid.vector.GVector2D;
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


   public void drawGroup(final Collection<? extends GStyled2DGeometry<? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>>> group,
                         final IVectorial2DDrawer drawer,
                         final double lodMinSize,
                         final boolean debugRendering,
                         final boolean renderLODIgnores) {
      final String label = Integer.toString(group.size());

      boolean allSameClass = true;
      final Class<? extends GStyled2DGeometry> klass = getClass();
      for (final GStyled2DGeometry<? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>> each : group) {
         if (each.getClass() != klass) {
            allSameClass = false;
            break;
         }
      }

      if (allSameClass) {
         //         for (final GStyled2DGeometry<? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>> each : group) {
         //            final GAxisAlignedRectangle bounds = each.getBounds();
         //
         //            each.draw(drawer, debugRendering);
         //            drawer.fillRect(bounds, new Color(200, 200, 200, 127));
         //         }

         getAverageSymbol(group).draw(drawer, lodMinSize, debugRendering, renderLODIgnores, label);
      }
      else {
         IVector2 lower = GVector2D.POSITIVE_INFINITY;
         IVector2 upper = GVector2D.NEGATIVE_INFINITY;
         for (final GStyled2DGeometry<? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>> each : group) {
            final GAxisAlignedRectangle bounds = each.getBounds();
            lower = lower.min(bounds._lower);
            upper = upper.max(bounds._upper);

            drawer.fillRect(bounds, new Color(255, 0, 0, 127));
            each.draw(drawer, debugRendering);
         }
         final GAxisAlignedRectangle groupBounds = new GAxisAlignedRectangle(lower, upper);
         drawer.fillRect(groupBounds, new Color(255, 0, 0, 64));
         drawer.drawShadowedStringCentered(label, groupBounds._center, Color.BLACK, 1, new Color(255, 255, 255, 127));
      }
   }


   protected abstract GStyled2DGeometry<? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>> getAverageSymbol(final Collection<? extends GStyled2DGeometry<? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>>> group);


}
