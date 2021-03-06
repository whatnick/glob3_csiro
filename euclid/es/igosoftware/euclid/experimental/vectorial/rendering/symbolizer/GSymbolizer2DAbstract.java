

package es.igosoftware.euclid.experimental.vectorial.rendering.symbolizer;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Paint;
import java.awt.Stroke;

import es.igosoftware.euclid.IBoundedGeometry2D;
import es.igosoftware.euclid.ICurve2D;
import es.igosoftware.euclid.ISurface2D;
import es.igosoftware.euclid.bounding.GAxisAlignedRectangle;
import es.igosoftware.euclid.bounding.IFinite2DBounds;
import es.igosoftware.euclid.colors.IColor;
import es.igosoftware.euclid.experimental.measurement.GArea;
import es.igosoftware.euclid.experimental.measurement.GLength;
import es.igosoftware.euclid.experimental.measurement.IMeasure;
import es.igosoftware.euclid.experimental.vectorial.rendering.context.IVectorial2DRenderingScaler;
import es.igosoftware.euclid.experimental.vectorial.rendering.styling.GCurve2DStyle;
import es.igosoftware.euclid.experimental.vectorial.rendering.styling.GNullSurface2DStyle;
import es.igosoftware.euclid.experimental.vectorial.rendering.styling.GSurface2DStyle;
import es.igosoftware.euclid.experimental.vectorial.rendering.styling.ICurve2DStyle;
import es.igosoftware.euclid.experimental.vectorial.rendering.styling.ISurface2DStyle;
import es.igosoftware.euclid.experimental.vectorial.rendering.symbols.GLabel2DSymbol;
import es.igosoftware.euclid.experimental.vectorial.rendering.symbols.GOval2DSymbol;
import es.igosoftware.euclid.experimental.vectorial.rendering.symbols.GPolygon2DSymbol;
import es.igosoftware.euclid.experimental.vectorial.rendering.symbols.GPolygonalChain2DSymbol;
import es.igosoftware.euclid.experimental.vectorial.rendering.symbols.GRectangle2DSymbol;
import es.igosoftware.euclid.experimental.vectorial.rendering.symbols.GSymbol2DList;
import es.igosoftware.euclid.features.IGlobeFeature;
import es.igosoftware.euclid.ntree.GGTInnerNode;
import es.igosoftware.euclid.ntree.GGTNode;
import es.igosoftware.euclid.shape.GAxisAlignedOval2D;
import es.igosoftware.euclid.shape.IPolygon2D;
import es.igosoftware.euclid.shape.IPolygonalChain2D;
import es.igosoftware.euclid.vector.IVector2;
import es.igosoftware.util.GMath;


public abstract class GSymbolizer2DAbstract
         implements
            ISymbolizer2D {


   private static final ICurve2DStyle INNER_NODE_STYLE = new ICurve2DStyle() {
                                                          @Override
                                                          public Stroke getBorderStroke() {
                                                             return new BasicStroke(1);
                                                          }


                                                          @Override
                                                          public Paint getBorderPaint() {
                                                             return new Color(0f, 1f, 0f, 0.5f).darker().darker();
                                                          }


                                                          @Override
                                                          public boolean isGroupableWith(final ICurve2DStyle that) {
                                                             return false;
                                                          }
                                                       };

   private static final ICurve2DStyle LEAF_NODE_STYLE  = new ICurve2DStyle() {
                                                          @Override
                                                          public Stroke getBorderStroke() {
                                                             return new BasicStroke(1, BasicStroke.CAP_ROUND,
                                                                      BasicStroke.JOIN_ROUND, 10, new float[] { 2, 2 }, 0);
                                                          }


                                                          @Override
                                                          public Paint getBorderPaint() {
                                                             return new Color(0f, 1f, 0f, 0.5f);
                                                          }


                                                          @Override
                                                          public boolean isGroupableWith(final ICurve2DStyle that) {
                                                             return false;
                                                          }
                                                       };


   /* -------------------------------------------------------------------------------------- */
   /* nodes */
   @Override
   public GSymbol2DList getNodeSymbols(final GGTNode<IVector2, IGlobeFeature<IVector2, ? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>>, IBoundedGeometry2D<? extends IFinite2DBounds<?>>> node,
                                       final IVectorial2DRenderingScaler scaler) {
      if (!isDebugRendering()) {
         return null;
      }

      if (node.getElementsCount() == 0) {
         return null;
      }

      final boolean isInner = (node instanceof GGTInnerNode);

      final ISurface2DStyle surfaceStyle = GNullSurface2DStyle.INSTANCE;
      final ICurve2DStyle curveStyle = isInner ? INNER_NODE_STYLE : LEAF_NODE_STYLE;

      final GAxisAlignedRectangle scaledBounds = (GAxisAlignedRectangle) scaler.scaleAndTranslate(node.getBounds());
      final GRectangle2DSymbol boundsRectangle = new GRectangle2DSymbol(scaledBounds, null, surfaceStyle, curveStyle,
               Integer.MAX_VALUE, false);

      final IVector2 position = scaledBounds._center;
      final String msg = "" + node.getAllElementsCount();
      final Font font = new Font("Dialog", Font.PLAIN, 8);

      final GLabel2DSymbol label = new GLabel2DSymbol(position, msg, font);
      //      return Collections.singleton(boundsRectangle);
      @SuppressWarnings("unchecked")
      final GSymbol2DList symbols = new GSymbol2DList(boundsRectangle, label);
      return symbols;
   }


   /* -------------------------------------------------------------------------------------- */
   /* points */
   protected abstract IMeasure<GArea> getPointSize(final IVector2 point,
                                                   final IGlobeFeature<IVector2, ? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>> feature,
                                                   final IVectorial2DRenderingScaler scaler);


   protected abstract IMeasure<GLength> getPointBorderSize(final IVector2 point,
                                                           final IGlobeFeature<IVector2, ? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>> feature,
                                                           final IVectorial2DRenderingScaler scaler);


   protected abstract IColor getPointColor(final IVector2 point,
                                           final IGlobeFeature<IVector2, ? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>> feature,
                                           final IVectorial2DRenderingScaler scaler);


   protected abstract IColor getPointBorderColor(final IVector2 point,
                                                 final IGlobeFeature<IVector2, ? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>> feature,
                                                 final IVectorial2DRenderingScaler scaler);


   protected abstract float getPointOpacity(final IVector2 point,
                                            final IGlobeFeature<IVector2, ? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>> feature,
                                            final IVectorial2DRenderingScaler scaler);


   @Override
   public GSymbol2DList getPointSymbols(final IVector2 point,
                                        final IGlobeFeature<IVector2, ? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>> feature,
                                        final IVectorial2DRenderingScaler scaler) {

      final IVector2 extent = calculateOvalExtent(point, feature, scaler);

      final IVector2 position = calculatePosition(point, feature, scaler, extent);
      final GAxisAlignedOval2D oval = new GAxisAlignedOval2D(position, extent);

      final ISurface2DStyle surfaceStyle = getPointSurfaceStyle(point, feature, scaler);
      final ICurve2DStyle curveStyle = getPointCurveStyle(point, feature, scaler);

      return new GSymbol2DList(new GOval2DSymbol(oval, null, surfaceStyle, curveStyle, 1000, true));
   }


   protected ICurve2DStyle getPointCurveStyle(final IVector2 point,
                                              final IGlobeFeature<IVector2, ? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>> feature,
                                              final IVectorial2DRenderingScaler scaler) {
      final IMeasure<GLength> borderSize = getPointBorderSize(point, feature, scaler);

      final double borderLenghtInMeters = borderSize.getValueInReferenceUnits();
      final IVector2 pointPlusBorderSize = scaler.increment(point, borderLenghtInMeters, 0);
      final float borderWidth = (float) scaler.scaleExtent(pointPlusBorderSize.sub(point)).x();

      final IColor color = getPointBorderColor(point, feature, scaler);
      final float opacity = getPointOpacity(point, feature, scaler);

      return new GCurve2DStyle(borderWidth, color, opacity);
   }


   protected ISurface2DStyle getPointSurfaceStyle(final IVector2 point,
                                                  final IGlobeFeature<IVector2, ? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>> feature,
                                                  final IVectorial2DRenderingScaler scaler) {
      final IColor color = getPointColor(point, feature, scaler);
      final float opacity = getPointOpacity(point, feature, scaler);

      return new GSurface2DStyle(color, opacity);
   }


   protected IVector2 calculateOvalExtent(final IVector2 point,
                                          final IGlobeFeature<IVector2, ? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>> feature,
                                          final IVectorial2DRenderingScaler scaler) {
      final IMeasure<GArea> pointSize = getPointSize(point, feature, scaler);

      final double areaInSquaredMeters = pointSize.getValueInReferenceUnits();

      final double radius = GMath.sqrt(areaInSquaredMeters / Math.PI);
      final IVector2 pointPlusRadius = scaler.increment(point, radius, radius);
      return scaler.scaleExtent(pointPlusRadius.sub(point)).scale(2); // radius times 2 (for extent)
   }


   protected IVector2 calculateRectangleExtent(final IVector2 point,
                                               final IGlobeFeature<IVector2, ? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>> feature,
                                               final IVectorial2DRenderingScaler scaler) {
      final IMeasure<GArea> pointSize = getPointSize(point, feature, scaler);

      final double areaInSquaredMeters = pointSize.getValueInReferenceUnits();

      final double radius = GMath.sqrt(areaInSquaredMeters);
      final IVector2 pointPlusRadius = scaler.increment(point, radius, radius);
      return scaler.scaleExtent(pointPlusRadius.sub(point)).scale(2); // radius times 2 (for extent)
   }


   protected IVector2 calculatePosition(final IVector2 point,
                                        @SuppressWarnings("unused") final IGlobeFeature<IVector2, ? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>> feature,
                                        final IVectorial2DRenderingScaler scaler,
                                        final IVector2 extent) {
      final IVector2 scaledPoint = scaler.scaleAndTranslate(point);
      return scaledPoint.sub(extent.div(2));
   }


   /* -------------------------------------------------------------------------------------- */
   /* surfaces */
   protected abstract IMeasure<GLength> getSurfaceBorderSize(final ISurface2D<?> surface,
                                                             final IGlobeFeature<IVector2, ? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>> feature,
                                                             final IVectorial2DRenderingScaler scaler);


   protected abstract IColor getSurfaceColor(final ISurface2D<?> surface,
                                             final IGlobeFeature<IVector2, ? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>> feature,
                                             final IVectorial2DRenderingScaler scaler);


   protected abstract IColor getSurfaceBorderColor(final ISurface2D<?> surface,
                                                   final IGlobeFeature<IVector2, ? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>> feature,
                                                   final IVectorial2DRenderingScaler scaler);


   protected abstract float getSurfaceOpacity(final ISurface2D<?> surface,
                                              final IGlobeFeature<IVector2, ? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>> feature,
                                              final IVectorial2DRenderingScaler scaler);


   @Override
   public GSymbol2DList getSurfaceSymbols(final ISurface2D<? extends IFinite2DBounds<?>> surface,
                                          final IGlobeFeature<IVector2, ? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>> feature,
                                          final IVectorial2DRenderingScaler scaler) {

      if (surface instanceof IPolygon2D) {
         final IPolygon2D polygon = (IPolygon2D) surface;
         final IPolygon2D scaledPolygon = polygon.transform(scaler);

         final ISurface2DStyle surfaceStyle = getSurfaceStyle(surface, feature, scaler);
         final ICurve2DStyle curveStyle = getSurfaceCurveStyle(surface, feature, scaler);

         return new GSymbol2DList(new GPolygon2DSymbol(scaledPolygon, null, surfaceStyle, curveStyle, 0));
      }

      throw new RuntimeException("Surface type (" + surface.getClass() + ") not supported");
   }


   protected ICurve2DStyle getSurfaceCurveStyle(final ISurface2D<? extends IFinite2DBounds<?>> surface,
                                                final IGlobeFeature<IVector2, ? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>> feature,
                                                final IVectorial2DRenderingScaler scaler) {
      final IMeasure<GLength> borderSize = getSurfaceBorderSize(surface, feature, scaler);
      final IVector2 point = surface.getCentroid();

      final double borderLenghtInMeters = borderSize.getValueInReferenceUnits();
      final IVector2 pointPlusBorderSize = scaler.increment(point, borderLenghtInMeters, 0);
      final float borderWidth = (float) scaler.scaleExtent(pointPlusBorderSize.sub(point)).x();


      final IColor color = getSurfaceBorderColor(surface, feature, scaler);
      final float opacity = getSurfaceOpacity(surface, feature, scaler);

      return new GCurve2DStyle(borderWidth, color, opacity);
   }


   protected ISurface2DStyle getSurfaceStyle(final ISurface2D<? extends IFinite2DBounds<?>> surface,
                                             final IGlobeFeature<IVector2, ? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>> feature,
                                             final IVectorial2DRenderingScaler scaler) {
      final IColor color = getSurfaceColor(surface, feature, scaler);
      final float opacity = getSurfaceOpacity(surface, feature, scaler);

      return new GSurface2DStyle(color, opacity);
   }


   /* -------------------------------------------------------------------------------------- */
   /* curves */
   protected abstract IMeasure<GLength> getCurveBorderSize(final ICurve2D<?> curve,
                                                           final IGlobeFeature<IVector2, ? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>> feature,
                                                           final IVectorial2DRenderingScaler scaler);


   protected abstract IColor getCurveColor(final ICurve2D<?> curve,
                                           final IGlobeFeature<IVector2, ? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>> feature,
                                           final IVectorial2DRenderingScaler scaler);


   protected abstract float getCurveOpacity(final ICurve2D<?> curve,
                                            final IGlobeFeature<IVector2, ? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>> feature,
                                            final IVectorial2DRenderingScaler scaler);


   @Override
   public GSymbol2DList getCurveSymbols(final ICurve2D<? extends IFinite2DBounds<?>> curve,
                                        final IGlobeFeature<IVector2, ? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>> feature,
                                        final IVectorial2DRenderingScaler scaler) {

      if (curve instanceof IPolygonalChain2D) {
         final IPolygonalChain2D polygonalChain = (IPolygonalChain2D) curve;
         final IPolygonalChain2D scaledPolygonalChain = polygonalChain.transform(scaler);

         final ICurve2DStyle curveStyle = getCurveStyle(curve, feature, scaler);

         return new GSymbol2DList(new GPolygonalChain2DSymbol(scaledPolygonalChain, null, curveStyle, 10));
      }

      throw new RuntimeException("Curve type (" + curve.getClass() + ") not supported");
   }


   protected ICurve2DStyle getCurveStyle(final ICurve2D<? extends IFinite2DBounds<?>> curve,
                                         final IGlobeFeature<IVector2, ? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>> feature,
                                         final IVectorial2DRenderingScaler scaler) {
      final IMeasure<GLength> borderSize = getCurveBorderSize(curve, feature, scaler);
      final IVector2 point = curve.getCentroid();

      final double borderLenghtInMeters = borderSize.getValueInReferenceUnits();
      final IVector2 pointPlusBorderSize = scaler.increment(point, borderLenghtInMeters, 0);
      final float borderWidth = (float) scaler.scaleExtent(pointPlusBorderSize.sub(point)).x();


      final IColor color = getCurveColor(curve, feature, scaler);
      final float opacity = getCurveOpacity(curve, feature, scaler);


      return new GCurve2DStyle(borderWidth, color, opacity);
   }


}
