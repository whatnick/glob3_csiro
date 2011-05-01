

package es.igosoftware.euclid.experimental.vectorial.rendering.styledgeometries;


import java.awt.Paint;
import java.awt.Stroke;
import java.util.Collection;

import es.igosoftware.euclid.IBoundedGeometry2D;
import es.igosoftware.euclid.bounding.GAxisAlignedRectangle;
import es.igosoftware.euclid.bounding.IFinite2DBounds;
import es.igosoftware.euclid.experimental.vectorial.rendering.context.IVectorial2DDrawer;
import es.igosoftware.euclid.shape.IPolygonalChain2D;


public class GStyledPolygonalChain2D
         extends
            GStyledCurve2D<IPolygonalChain2D> {


   public GStyledPolygonalChain2D(final IPolygonalChain2D polygonalChain,
                                  final ICurve2DStyle curveStyle,
                                  final int priority) {
      super(polygonalChain, curveStyle, priority);
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


   @Override
   public boolean isGroupableWith(final GStyled2DGeometry<? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>> that) {
      return false;
   }


   @Override
   public String toString() {
      return "GStyledPolygonalChain2D [geometry=" + _geometry + ", curveStyle=" + _curveStyle + "]";
   }


   @Override
   public GAxisAlignedRectangle getBounds() {
      return _geometry.getBounds();
   }


   @Override
   public boolean isGroupable() {
      return false;
   }


   @Override
   protected GStyled2DGeometry<? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>> getAverageSymbol(final Collection<? extends GStyled2DGeometry<? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>>> group) {
      return null;
   }


}
