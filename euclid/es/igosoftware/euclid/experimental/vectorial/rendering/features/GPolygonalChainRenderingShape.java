

package es.igosoftware.euclid.experimental.vectorial.rendering.features;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Rectangle;

import es.igosoftware.euclid.IBoundedGeometry;
import es.igosoftware.euclid.ICurve2D;
import es.igosoftware.euclid.bounding.IFiniteBounds;
import es.igosoftware.euclid.colors.IColor;
import es.igosoftware.euclid.experimental.measurement.GLength;
import es.igosoftware.euclid.experimental.measurement.IMeasure;
import es.igosoftware.euclid.experimental.vectorial.rendering.context.IVectorial2DDrawer;
import es.igosoftware.euclid.experimental.vectorial.rendering.context.IVectorial2DRenderingScaler;
import es.igosoftware.euclid.experimental.vectorial.rendering.styling.IRenderingStyle;
import es.igosoftware.euclid.experimental.vectorial.rendering.utils.GAWTPoints;
import es.igosoftware.euclid.features.IGlobeFeature;
import es.igosoftware.euclid.shape.IPolygonalChain2D;
import es.igosoftware.euclid.vector.IVector2;


public class GPolygonalChainRenderingShape
         extends
            GRenderingFeatureAbstract<ICurve2D<? extends IFiniteBounds<IVector2, ?>>>
         implements
            ICurveRenderingShape<ICurve2D<? extends IFiniteBounds<IVector2, ?>>> {


   private final float      _borderWidth;
   private final Rectangle  _bounds;
   private final GAWTPoints _points;


   public GPolygonalChainRenderingShape(final IPolygonalChain2D polygonalChain,
                                        final GAWTPoints points,
                                        final IMeasure<GLength> curveBorderSize,
                                        final IVectorial2DRenderingScaler scaler) {
      final IVector2 point = polygonalChain.getCentroid();

      final double borderLenghtInMeters = curveBorderSize.getValueInReferenceUnits();
      final IVector2 pointPlusBorderSize = scaler.increment(point, borderLenghtInMeters, 0);
      _borderWidth = (float) scaler.scaleExtent(pointPlusBorderSize.sub(point)).x();

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


   private Color getLODIgnoreColor(final ICurve2D<? extends IFiniteBounds<IVector2, ?>> polygonalChain,
                                   final IGlobeFeature<IVector2, ? extends IBoundedGeometry<IVector2, ? extends IFiniteBounds<IVector2, ?>>> feature,
                                   final IRenderingStyle renderingStyle,
                                   final IVectorial2DRenderingScaler scaler) {
      if (renderingStyle.isDebugRendering()) {
         return renderingStyle.getLODColor().asAWTColor();
      }

      final IColor pointColor = renderingStyle.getCurveColor(polygonalChain, feature, scaler);
      final float pointOpacity = renderingStyle.getCurveOpacity(polygonalChain, feature, scaler);

      return pointColor.asAWTColor(pointOpacity);
   }


   @Override
   protected final void renderLODIgnore(final ICurve2D<? extends IFiniteBounds<IVector2, ?>> polygonalChain,
                                        final IGlobeFeature<IVector2, ? extends IBoundedGeometry<IVector2, ? extends IFiniteBounds<IVector2, ?>>> feature,
                                        final IRenderingStyle renderingStyle,
                                        final IVectorial2DRenderingScaler scaler,
                                        final IVectorial2DDrawer drawer) {
      final Color color = getLODIgnoreColor(polygonalChain, feature, renderingStyle, scaler);

      drawer.fillRect(_bounds, color);
   }


   @Override
   protected void rawDraw(final ICurve2D<? extends IFiniteBounds<IVector2, ?>> polygonalChain,
                          final IGlobeFeature<IVector2, ? extends IBoundedGeometry<IVector2, ? extends IFiniteBounds<IVector2, ?>>> feature,
                          final IRenderingStyle renderingStyle,
                          final IVectorial2DRenderingScaler scaler,
                          final IVectorial2DDrawer drawer) {

      if (_borderWidth <= 0) {
         return;
      }


      final IColor curveColor = renderingStyle.getCurveColor(polygonalChain, feature, scaler);
      final float curveOpacity = renderingStyle.getCurveOpacity(polygonalChain, feature, scaler);

      final BasicStroke borderStroke = new BasicStroke(_borderWidth, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);

      drawer.drawPolyline(_points, curveColor.asAWTColor(curveOpacity), borderStroke);
   }


}
