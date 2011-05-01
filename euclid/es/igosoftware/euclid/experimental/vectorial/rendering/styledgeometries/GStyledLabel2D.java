

package es.igosoftware.euclid.experimental.vectorial.rendering.styledgeometries;

import java.awt.Color;
import java.awt.Font;
import java.util.Collection;

import es.igosoftware.euclid.IBoundedGeometry2D;
import es.igosoftware.euclid.bounding.GAxisAlignedRectangle;
import es.igosoftware.euclid.bounding.IFinite2DBounds;
import es.igosoftware.euclid.experimental.vectorial.rendering.context.IVectorial2DDrawer;
import es.igosoftware.euclid.vector.IVector2;


public class GStyledLabel2D
         extends
            GStyled2DGeometry<IVector2> {

   private final String _label;
   private final Font   _font;


   public GStyledLabel2D(final IVector2 position,
                         final String label,
                         final Font font) {
      super(position, null, 1000000);

      _label = label;
      _font = font;
   }


   @Override
   protected boolean isBigger(final double lodMinSize) {
      return true;
   }


   @Override
   protected void draw(final IVectorial2DDrawer drawer,
                       final boolean debugRendering) {
      drawer.drawShadowedStringCentered(_label, _geometry, _font, Color.BLACK, 1, Color.LIGHT_GRAY.brighter());
   }


   @Override
   protected void drawLODIgnore(final IVectorial2DDrawer drawer,
                                final boolean debugRendering) {

   }


   @Override
   public boolean isGroupable() {
      return false;
   }


   @Override
   public boolean isGroupableWith(final GStyled2DGeometry<? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>> that) {
      return false;
   }


   @Override
   public GAxisAlignedRectangle getBounds() {
      final int TODO;
      return null;
   }


   @Override
   protected GStyled2DGeometry<? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>> getAverageSymbol(final Collection<? extends GStyled2DGeometry<? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>>> group,
                                                                                                            final String label) {
      return null;
   }

}
