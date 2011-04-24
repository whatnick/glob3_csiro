

package es.igosoftware.euclid.experimental.vectorial.rendering.features;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;

import es.igosoftware.euclid.IBoundedGeometry2D;
import es.igosoftware.euclid.ISurface2D;
import es.igosoftware.euclid.bounding.IFiniteBounds;
import es.igosoftware.euclid.colors.IColor;
import es.igosoftware.euclid.experimental.measurement.GLength;
import es.igosoftware.euclid.experimental.measurement.IMeasure;
import es.igosoftware.euclid.experimental.vectorial.rendering.context.IVectorial2DDrawer;
import es.igosoftware.euclid.experimental.vectorial.rendering.context.IVectorial2DRenderingScaler;
import es.igosoftware.euclid.experimental.vectorial.rendering.styling.IRenderingStyle;
import es.igosoftware.euclid.features.IGlobeFeature;
import es.igosoftware.euclid.shape.IPolygon2D;
import es.igosoftware.euclid.vector.IVector2;
import es.igosoftware.util.GAWTUtils;


public class GPolygonRenderingShape
         extends
            GRenderingFeatureAbstract<ISurface2D<? extends IFiniteBounds<IVector2, ?>>>
         implements
            ISurfaceRenderingShape<ISurface2D<? extends IFiniteBounds<IVector2, ?>>> {


   private final float       _borderWidth;
   private final Rectangle2D _bounds;
   private final Shape       _awtShape;


   public GPolygonRenderingShape(final IPolygon2D polygon,
                                 final Shape awtShape,
                                 final IMeasure<GLength> surfaceBorderSize,
                                 final IVectorial2DRenderingScaler scaler) {
      final IVector2 point = polygon.getCentroid();

      final double borderLenghtInMeters = surfaceBorderSize.getValueInReferenceUnits();
      final IVector2 pointPlusBorderSize = scaler.increment(point, borderLenghtInMeters, 0);
      _borderWidth = (float) scaler.scaleExtent(pointPlusBorderSize.sub(point)).x();

      _bounds = awtShape.getBounds2D();

      _awtShape = awtShape;
   }


   @Override
   public boolean isBiggerThan(final double lodMinSize) {
      return (_bounds.getWidth() * _bounds.getHeight()) >= lodMinSize;
   }


   private Color getLODIgnoreColor(final ISurface2D<? extends IFiniteBounds<IVector2, ?>> polygon,
                                   final IGlobeFeature<IVector2, ? extends IBoundedGeometry2D<? extends IFiniteBounds<IVector2, ?>>> feature,
                                   final IRenderingStyle renderingStyle,
                                   final IVectorial2DRenderingScaler scaler) {
      if (renderingStyle.isDebugRendering()) {
         return renderingStyle.getLODColor().asAWTColor();
      }

      final IColor surfaceColor = renderingStyle.getSurfaceColor(polygon, feature, scaler);
      final float surfaceOpacity = renderingStyle.getSurfaceOpacity(polygon, feature, scaler);

      if (_borderWidth <= 0) {
         return surfaceColor.asAWTColor(surfaceOpacity);
      }

      final IColor surfaceBorderColor = renderingStyle.getSurfaceBorderColor(polygon, feature, scaler);
      return GAWTUtils.mix(surfaceColor.asAWTColor(surfaceOpacity), surfaceBorderColor.asAWTColor(surfaceOpacity));
   }


   @Override
   protected final void renderLODIgnore(final ISurface2D<? extends IFiniteBounds<IVector2, ?>> polygon,
                                        final IGlobeFeature<IVector2, ? extends IBoundedGeometry2D<? extends IFiniteBounds<IVector2, ?>>> feature,
                                        final IRenderingStyle renderingStyle,
                                        final IVectorial2DRenderingScaler scaler,
                                        final IVectorial2DDrawer drawer) {
      final Color color = getLODIgnoreColor(polygon, feature, renderingStyle, scaler);

      drawer.fillRect(_bounds, color);
   }


   @Override
   protected final void rawDraw(final ISurface2D<? extends IFiniteBounds<IVector2, ?>> polygon,
                                final IGlobeFeature<IVector2, ? extends IBoundedGeometry2D<? extends IFiniteBounds<IVector2, ?>>> feature,
                                final IRenderingStyle renderingStyle,
                                final IVectorial2DRenderingScaler scaler,
                                final IVectorial2DDrawer drawer) {


      final IColor surfaceColor = renderingStyle.getSurfaceColor(polygon, feature, scaler);
      final float surfaceOpacity = renderingStyle.getSurfaceOpacity(polygon, feature, scaler);

      // fill polygon
      drawer.fill(_awtShape, surfaceColor.asAWTColor(surfaceOpacity));


      // render border 
      if (_borderWidth > 0) {
         final IColor surfaceBorderColor = renderingStyle.getSurfaceBorderColor(polygon, feature, scaler);
         final BasicStroke borderStroke = new BasicStroke(_borderWidth, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
         drawer.draw(_awtShape, surfaceBorderColor.asAWTColor(surfaceOpacity), borderStroke);
      }
   }


}
