

package es.igosoftware.euclid.experimental.vectorial.rendering.styledgeometries;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import es.igosoftware.euclid.IBoundedGeometry2D;
import es.igosoftware.euclid.bounding.GAxisAlignedRectangle;
import es.igosoftware.euclid.bounding.IFinite2DBounds;
import es.igosoftware.euclid.colors.GColorF;
import es.igosoftware.euclid.experimental.vectorial.rendering.context.IVectorial2DDrawer;
import es.igosoftware.euclid.vector.IVector2;
import es.igosoftware.util.GAssert;


public abstract class GStyled2DGeometry<

GeometryT extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>

> {


   protected final GeometryT _geometry;
   private final String      _label;
   private final int         _priority;
   private int               _position = -1;


   protected GStyled2DGeometry(final GeometryT geometry,
                               final String label,
                               final int priority) {
      GAssert.notNull(geometry, "geometry");

      _geometry = geometry;
      _label = label;
      _priority = priority;
   }


   /* used from renderer */
   public void setPosition(final int position) {
      _position = position;
   }


   public int getPriority() {
      return _priority;
   }


   public int getPosition() {
      return _position;
   }


   public final void draw(final IVectorial2DDrawer drawer,
                          final double lodMinSize,
                          final boolean debugRendering,
                          final boolean renderLODIgnores) {
      GAssert.isTrue(_position != -1, "_position not initialized (" + this + ")");

      if (isBigger(lodMinSize)) {
         draw(drawer, debugRendering);
         if (_label != null) {
            drawLabel(drawer);
         }
      }
      else {
         if (renderLODIgnores) {
            drawLODIgnore(drawer, debugRendering);
         }
      }
   }


   private void drawLabel(final IVectorial2DDrawer drawer) {
      final IVector2 position = getBounds().getCentroid();

      drawer.drawShadowedStringCentered(_label, position, Color.BLACK, 1, new Color(255, 255, 255, 127));
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


   public final Collection<? extends GStyled2DGeometry<? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>>> createGroupSymbols(final Collection<? extends GStyled2DGeometry<? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>>> group) {

      boolean allSameClass = true;
      final Class<? extends GStyled2DGeometry> klass = getClass();
      for (final GStyled2DGeometry<? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>> each : group) {
         if (each.getClass() != klass) {
            allSameClass = false;
            break;
         }
      }

      final String label = Integer.toString(group.size());

      if (allSameClass) {
         final GStyled2DGeometry<? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>> averageSymbol = getAverageSymbol(
                  group, label);
         averageSymbol.setPosition(_position);
         return Collections.singleton(averageSymbol);
      }

      final Collection<GStyled2DGeometry<? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>>> result = new ArrayList<GStyled2DGeometry<? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>>>();

      GAxisAlignedRectangle mergedBounds = null;
      for (final GStyled2DGeometry<? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>> each : group) {

         final GAxisAlignedRectangle bounds = each.getBounds();
         mergedBounds = (mergedBounds == null) ? bounds : mergedBounds.mergedWith(bounds);

         result.add(each);
      }

      final ISurface2DStyle surfaceStyle = new GSurface2DStyle(GColorF.RED, 0.5f);
      final ICurve2DStyle curveStyle = GNullCurve2DStyle.INSTANCE;
      final GStyledRectangle2D rectangle = new GStyledRectangle2D(mergedBounds, label, surfaceStyle, curveStyle, 10000000);
      rectangle.setPosition(_position);
      result.add(rectangle);

      return result;
   }


   protected abstract GStyled2DGeometry<? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>> getAverageSymbol(final Collection<? extends GStyled2DGeometry<? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>>> group,
                                                                                                                     final String label);


}
