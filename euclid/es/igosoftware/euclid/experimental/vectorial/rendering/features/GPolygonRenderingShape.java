

package es.igosoftware.euclid.experimental.vectorial.rendering.features;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;

import es.igosoftware.euclid.IBoundedGeometry;
import es.igosoftware.euclid.bounding.IFiniteBounds;
import es.igosoftware.euclid.colors.IColor;
import es.igosoftware.euclid.experimental.measurement.GLength;
import es.igosoftware.euclid.experimental.measurement.IMeasure;
import es.igosoftware.euclid.experimental.vectorial.rendering.context.IVectorial2DDrawer;
import es.igosoftware.euclid.experimental.vectorial.rendering.context.IVectorial2DRenderingScaleContext;
import es.igosoftware.euclid.experimental.vectorial.rendering.styling.IRenderingStyle;
import es.igosoftware.euclid.features.IGlobeFeature;
import es.igosoftware.euclid.shape.IPolygon2D;
import es.igosoftware.euclid.vector.IVector2;
import es.igosoftware.util.GAWTUtils;


public class GPolygonRenderingShape
         extends
            GRenderingFeatureAbstract<IPolygon2D>
         implements
            IRenderingShape<IPolygon2D> {


   private final float       _borderWidth;
   private final Rectangle2D _bounds;
   private final Shape       _awtShape;


   public GPolygonRenderingShape(final IPolygon2D polygon,
                                 final Shape awtShape,
                                 final IMeasure<GLength> surfaceBorderSize,
                                 final IRenderingStyle renderingStyle,
                                 final IVectorial2DRenderingScaleContext scaler) {
      final IVector2 point = polygon.getCentroid();

      final double borderLenghtInMeters = surfaceBorderSize.getValueInReferenceUnits();
      final IVector2 pointPlusBorderSize = renderingStyle.increment(point, scaler.getProjection(), borderLenghtInMeters, 0);
      _borderWidth = (float) scaler.scaleExtent(pointPlusBorderSize.sub(point)).x();

      _bounds = awtShape.getBounds2D();

      _awtShape = awtShape;
   }


   @Override
   public boolean isBiggerThan(final double lodMinSize) {
      return (_bounds.getWidth() * _bounds.getHeight()) >= lodMinSize;
   }


   private Color getLODIgnoreColor(final IPolygon2D polygon,
                                   final IGlobeFeature<IVector2, ? extends IBoundedGeometry<IVector2, ? extends IFiniteBounds<IVector2, ?>>> feature,
                                   final IRenderingStyle renderingStyle,
                                   final IVectorial2DRenderingScaleContext scaler,
                                   final IVectorial2DDrawer drawer) {
      if (renderingStyle.isDebugRendering()) {
         return renderingStyle.getLODColor().asAWTColor();
      }

      final IColor surfaceColor = renderingStyle.getSurfaceColor(polygon, feature, scaler, drawer);
      final float surfaceOpacity = renderingStyle.getSurfaceOpacity(polygon, feature, scaler, drawer);

      if (_borderWidth <= 0) {
         return surfaceColor.asAWTColor(surfaceOpacity);
      }

      final IColor surfaceBorderColor = renderingStyle.getSurfaceBorderColor(polygon, feature, scaler, drawer);
      return GAWTUtils.mix(surfaceColor.asAWTColor(surfaceOpacity), surfaceBorderColor.asAWTColor(surfaceOpacity));
   }


   @Override
   public final void renderLODIgnore(final IPolygon2D polygon,
                                     final IGlobeFeature<IVector2, ? extends IBoundedGeometry<IVector2, ? extends IFiniteBounds<IVector2, ?>>> feature,
                                     final IRenderingStyle renderingStyle,
                                     final IVectorial2DRenderingScaleContext scaler,
                                     final IVectorial2DDrawer drawer) {
      final Color color = getLODIgnoreColor(polygon, feature, renderingStyle, scaler, drawer);

      drawer.fillRect(_bounds.getX(), _bounds.getY(), _bounds.getWidth(), _bounds.getHeight(), color);
   }


   @Override
   public final void rawDraw(final IPolygon2D polygon,
                             final IGlobeFeature<IVector2, ? extends IBoundedGeometry<IVector2, ? extends IFiniteBounds<IVector2, ?>>> feature,
                             final IRenderingStyle renderingStyle,
                             final IVectorial2DRenderingScaleContext scaler,
                             final IVectorial2DDrawer drawer) {


      final IColor surfaceColor = renderingStyle.getSurfaceColor(polygon, feature, scaler, drawer);
      final float surfaceOpacity = renderingStyle.getSurfaceOpacity(polygon, feature, scaler, drawer);

      // fill polygon
      drawer.fill(_awtShape, surfaceColor.asAWTColor(surfaceOpacity));


      // render border 
      if (_borderWidth > 0) {
         final IColor surfaceBorderColor = renderingStyle.getSurfaceBorderColor(polygon, feature, scaler, drawer);
         final BasicStroke borderStroke = new BasicStroke(_borderWidth, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
         drawer.draw(_awtShape, surfaceBorderColor.asAWTColor(surfaceOpacity), borderStroke);
      }
   }


}
