

package es.igosoftware.euclid.experimental.vectorial.rendering.styledgeometries;


import java.awt.Paint;
import java.awt.Stroke;

import es.igosoftware.euclid.experimental.vectorial.rendering.context.IVectorial2DDrawer;
import es.igosoftware.euclid.shape.IPolygonalChain2D;


public class GStyledPolygonalChain2D
         extends
            GStyledCurve2D<IPolygonalChain2D> {


   public GStyledPolygonalChain2D(final IPolygonalChain2D polygonalChain,
                                  final ICurve2DStyle curveStyle) {
      super(polygonalChain, curveStyle);
   }


   @Override
   protected void draw(final IVectorial2DDrawer drawer,
                       final boolean debugRendering) {
      // render border
      final Stroke borderStroke = _curveStyle.getBorderStroke();
      if (borderStroke != null) {
         final Paint borderPaint = _curveStyle.getBorderPaint();
         drawer.drawPolyline(_geometry, borderPaint, borderStroke);
      }
   }


}
