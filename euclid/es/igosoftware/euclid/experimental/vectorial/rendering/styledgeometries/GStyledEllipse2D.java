

package es.igosoftware.euclid.experimental.vectorial.rendering.styledgeometries;

import java.awt.Paint;
import java.awt.Stroke;

import es.igosoftware.euclid.bounding.GAxisAlignedRectangle;
import es.igosoftware.euclid.experimental.vectorial.rendering.context.IVectorial2DDrawer;
import es.igosoftware.euclid.shape.GAxisAlignedEllipse2D;


public class GStyledEllipse2D
         extends
            GStyledSurface2D<GAxisAlignedEllipse2D> {


   public GStyledEllipse2D(final GAxisAlignedEllipse2D ellipse,
                           final ISurface2DStyle surfaceStyle,
                           final ICurve2DStyle curveStyle) {
      super(ellipse, surfaceStyle, curveStyle);
   }


   @Override
   protected void draw(final IVectorial2DDrawer drawer,
                       final boolean debugRendering) {

      final GAxisAlignedRectangle bounds = _geometry.getBounds();

      // render surface
      final Paint fillPaint = _surfaceStyle.getSurfacePaint();
      if (fillPaint != null) {
         drawer.fillOval(bounds._lower, bounds._extent, fillPaint);
      }


      // render border
      final Stroke borderStroke = _curveStyle.getBorderStroke();
      if (borderStroke != null) {
         final Paint borderPaint = _curveStyle.getBorderPaint();
         drawer.drawOval(bounds._lower, bounds._extent, borderPaint, borderStroke);
      }
   }


}
