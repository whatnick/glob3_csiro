

package es.igosoftware.euclid.experimental.vectorial.rendering;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Rectangle;

import es.igosoftware.euclid.IBoundedGeometry;
import es.igosoftware.euclid.bounding.IFiniteBounds;
import es.igosoftware.euclid.colors.IColor;
import es.igosoftware.euclid.experimental.measurement.GLength;
import es.igosoftware.euclid.experimental.measurement.IMeasure;
import es.igosoftware.euclid.features.IGlobeFeature;
import es.igosoftware.euclid.shape.IPolygonalChain2D;
import es.igosoftware.euclid.vector.IVector2;


public class GPolygonalChainRenderingShape
         extends
            GRenderingShape<IPolygonalChain2D> {


   private final float      _borderWidth;
   private final Rectangle  _bounds;
   private final GAWTPoints _points;


   public GPolygonalChainRenderingShape(final IPolygonalChain2D polygonalChain,
                                        final GAWTPoints points,
                                        final IMeasure<GLength> curveBorderSize,
                                        final IRenderingStyle renderingStyle,
                                        final IVectorial2DRenderingContext rc) {
      final IVector2 point = polygonalChain.getCentroid();

      final double borderLenghtInMeters = curveBorderSize.getValueInReferenceUnits();
      final IVector2 pointPlusBorderSize = renderingStyle.increment(point, rc.getProjection(), borderLenghtInMeters, 0);
      _borderWidth = (float) rc.scaleExtent(pointPlusBorderSize.sub(point)).x();

      _bounds = calculateBounds(points);

      _points = points;
   }


   private Rectangle calculateBounds(final GAWTPoints points) {
      int minX = Integer.MAX_VALUE;
      int minY = Integer.MAX_VALUE;
      int maxX = Integer.MIN_VALUE;
      int maxY = Integer.MIN_VALUE;

      for (int i = 0; i < points._xPoints.length; i++) {
         final int x = points._xPoints[i];
         final int y = points._yPoints[i];

         maxX = Math.max(maxX, x);
         maxY = Math.max(maxY, y);

         minX = Math.min(minX, x);
         minY = Math.min(minY, y);
      }

      return new Rectangle(minX, minY, maxX - minX, maxY - minY);
   }


   @Override
   public boolean isBiggerThan(final double lodMinSize) {
      return (_bounds.getWidth() * _bounds.getHeight()) >= lodMinSize;
   }


   private Color getLODIgnoreColor(final IPolygonalChain2D polygonalChain,
                                   final IGlobeFeature<IVector2, ? extends IBoundedGeometry<IVector2, ? extends IFiniteBounds<IVector2, ?>>> feature,
                                   final IRenderingStyle renderingStyle,
                                   final IVectorial2DRenderingContext rc) {
      if (renderingStyle.isDebugRendering()) {
         return renderingStyle.getLODColor().asAWTColor();
      }

      final IColor pointColor = renderingStyle.getCurveColor(polygonalChain, feature, rc);
      final float pointOpacity = renderingStyle.getCurveOpacity(polygonalChain, feature, rc);

      return pointColor.asAWTColor(pointOpacity);
   }


   @Override
   protected final void renderLODIgnore(final IPolygonalChain2D polygonalChain,
                                        final IGlobeFeature<IVector2, ? extends IBoundedGeometry<IVector2, ? extends IFiniteBounds<IVector2, ?>>> feature,
                                        final IRenderingStyle renderingStyle,
                                        final IVectorial2DRenderingContext rc) {
      final Color color = getLODIgnoreColor(polygonalChain, feature, renderingStyle, rc);

      final IVector2 scaledPoint = rc.scaleAndTranslatePoint(polygonalChain.getCentroid());
      //            rc.setPixel(scaledPoint, color);
      rc.setColor(color);
      rc.fillRect(scaledPoint.x(), scaledPoint.y(), 1, 1);
   }


   @Override
   protected void rawDraw(final IPolygonalChain2D polygonalChain,
                          final IGlobeFeature<IVector2, ? extends IBoundedGeometry<IVector2, ? extends IFiniteBounds<IVector2, ?>>> feature,
                          final IRenderingStyle renderingStyle,
                          final IVectorial2DRenderingContext rc) {

      if (_borderWidth > 0) {
         final IColor polygonColor = renderingStyle.getCurveColor(polygonalChain, feature, rc);
         final float polygonOpacity = renderingStyle.getCurveOpacity(polygonalChain, feature, rc);

         final Color fillColor = polygonColor.asAWTColor(polygonOpacity);

         //         final BasicStroke borderStroke = new BasicStroke(_borderWidth, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
         final int cap = BasicStroke.CAP_ROUND;
         final int join = BasicStroke.JOIN_ROUND;
         final float miterlimit = 10;
         final float[] dash = { 20, 5 };
         final float dash_phase = 0;
         final BasicStroke borderStroke = new BasicStroke(_borderWidth, cap, join, miterlimit, dash, dash_phase);

         rc.setStroke(borderStroke);
         rc.setColor(fillColor);
         rc.drawPolyline(_points);
      }
   }


}
