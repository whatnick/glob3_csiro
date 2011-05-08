

package es.igosoftware.euclid.experimental.vectorial.rendering.symbolizer;

import java.util.Collection;

import es.igosoftware.euclid.IBoundedGeometry2D;
import es.igosoftware.euclid.ICurve2D;
import es.igosoftware.euclid.IGeometry2D;
import es.igosoftware.euclid.ISurface2D;
import es.igosoftware.euclid.bounding.GAxisAlignedRectangle;
import es.igosoftware.euclid.bounding.IFinite2DBounds;
import es.igosoftware.euclid.experimental.vectorial.rendering.context.IProjectionTool;
import es.igosoftware.euclid.experimental.vectorial.rendering.context.IVectorial2DDrawer;
import es.igosoftware.euclid.experimental.vectorial.rendering.context.IVectorial2DRenderingScaler;
import es.igosoftware.euclid.experimental.vectorial.rendering.symbolizer.expressions.GNullExpression;
import es.igosoftware.euclid.experimental.vectorial.rendering.symbolizer.expressions.IExpression;
import es.igosoftware.euclid.experimental.vectorial.rendering.symbols.GSymbol2D;
import es.igosoftware.euclid.features.IGlobeFeature;
import es.igosoftware.euclid.features.IGlobeFeatureCollection;
import es.igosoftware.euclid.ntree.GGTNode;
import es.igosoftware.euclid.vector.IVector2;
import es.igosoftware.euclid.vector.IVectorI2;
import es.igosoftware.util.GMath;


public class GExpressionsSymbolizer2D
         implements
            ISymbolizer2D {


   final private boolean                                                                                                                                            _debugRendering;
   final private double                                                                                                                                             _lodMinSize;
   final private boolean                                                                                                                                            _renderLODIgnores;
   final private boolean                                                                                                                                            _clusterSymbols;

   final private IExpression<IVector2, Collection<? extends GSymbol2D<? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>>>>                                 _pointExpression;
   final private IExpression<ICurve2D<? extends IFinite2DBounds<?>>, Collection<? extends GSymbol2D<? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>>>>   _curveExpression;
   final private IExpression<ISurface2D<? extends IFinite2DBounds<?>>, Collection<? extends GSymbol2D<? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>>>> _surfaceExpression;


   public GExpressionsSymbolizer2D(final double lodMinSize,
                                   final boolean renderLODIgnores,
                                   final boolean clusterSymbols,
                                   final IExpression<IVector2, Collection<? extends GSymbol2D<? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>>>> pointExpression,
                                   final IExpression<? extends ICurve2D<? extends IFinite2DBounds<?>>, Collection<? extends GSymbol2D<? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>>>> curveExpression,
                                   final IExpression<? extends ISurface2D<? extends IFinite2DBounds<?>>, Collection<? extends GSymbol2D<? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>>>> surfaceExpression) {
      this(false, lodMinSize, renderLODIgnores, clusterSymbols, pointExpression, curveExpression, surfaceExpression);
   }


   public GExpressionsSymbolizer2D(final boolean debugRendering,
                                   final double lodMinSize,
                                   final boolean renderLODIgnores,
                                   final boolean clusterSymbols,
                                   final IExpression<IVector2, Collection<? extends GSymbol2D<? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>>>> pointExpression,
                                   final IExpression<? extends ICurve2D<? extends IFinite2DBounds<?>>, Collection<? extends GSymbol2D<? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>>>> curveExpression,
                                   final IExpression<? extends ISurface2D<? extends IFinite2DBounds<?>>, Collection<? extends GSymbol2D<? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>>>> surfaceExpression) {

      _debugRendering = debugRendering;
      _lodMinSize = lodMinSize;
      _renderLODIgnores = renderLODIgnores;
      _clusterSymbols = clusterSymbols;

      _pointExpression = expressionOrNull(pointExpression);
      _curveExpression = expressionOrNull(curveExpression);
      _surfaceExpression = expressionOrNull(surfaceExpression);
   }


   @SuppressWarnings("unchecked")
   private static <GeometryT extends IGeometry2D> IExpression<GeometryT, Collection<? extends GSymbol2D<? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>>>> expressionOrNull(final IExpression<? extends GeometryT, Collection<? extends GSymbol2D<? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>>>> expression) {
      return (expression == null) ? GNullExpression.INSTANCE : expression;
   }


   @Override
   public boolean isDebugRendering() {
      return _debugRendering;
   }


   @Override
   public double getLODMinSize() {
      return _lodMinSize;
   }


   @Override
   public boolean isRenderLODIgnores() {
      return _renderLODIgnores;
   }


   @Override
   public String uniqueName() {
      return null;
   }


   @Override
   public void preprocessFeatures(final IGlobeFeatureCollection<IVector2, ? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>> features) {
      _pointExpression.preprocessFeatures(features);
      _curveExpression.preprocessFeatures(features);
      _surfaceExpression.preprocessFeatures(features);
   }


   @Override
   public void preRender(final IVectorI2 renderExtent,
                         final IProjectionTool projectionTool,
                         final GAxisAlignedRectangle viewport,
                         final ISymbolizer2D renderingStyle,
                         final IVectorial2DDrawer drawer) {
      _pointExpression.preRender(renderExtent, projectionTool, viewport, renderingStyle, drawer);
      _curveExpression.preRender(renderExtent, projectionTool, viewport, renderingStyle, drawer);
      _surfaceExpression.preRender(renderExtent, projectionTool, viewport, renderingStyle, drawer);
   }


   @Override
   public void postRender(final IVectorI2 renderExtent,
                          final IProjectionTool projectionTool,
                          final GAxisAlignedRectangle viewport,
                          final ISymbolizer2D renderingStyle,
                          final IVectorial2DDrawer drawer) {
      _pointExpression.postRender(renderExtent, projectionTool, viewport, renderingStyle, drawer);
      _curveExpression.postRender(renderExtent, projectionTool, viewport, renderingStyle, drawer);
      _surfaceExpression.postRender(renderExtent, projectionTool, viewport, renderingStyle, drawer);
   }


   @Override
   public double getMaximumSizeInMeters(final IVectorial2DRenderingScaler scaler) {
      return GMath.maxD(//
               _pointExpression.getMaximumSizeInMeters(scaler), //
               _curveExpression.getMaximumSizeInMeters(scaler), //
               _surfaceExpression.getMaximumSizeInMeters(scaler));
   }


   @Override
   public boolean isClusterSymbols() {
      return _clusterSymbols;
   }


   @Override
   public Collection<? extends GSymbol2D<? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>>> getNodeSymbols(final GGTNode<IVector2, IGlobeFeature<IVector2, ? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>>, IBoundedGeometry2D<? extends IFinite2DBounds<?>>> node,
                                                                                                                     final IVectorial2DRenderingScaler scaler) {
      if (isDebugRendering()) {

      }
      return null;
   }


   @Override
   public Collection<? extends GSymbol2D<? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>>> getPointSymbols(final IVector2 point,
                                                                                                                      final IGlobeFeature<IVector2, ? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>> feature,
                                                                                                                      final IVectorial2DRenderingScaler scaler) {
      return _pointExpression.evaluate(point, feature, scaler);
   }


   @Override
   public Collection<? extends GSymbol2D<? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>>> getCurveSymbols(final ICurve2D<? extends IFinite2DBounds<?>> curve,
                                                                                                                      final IGlobeFeature<IVector2, ? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>> feature,
                                                                                                                      final IVectorial2DRenderingScaler scaler) {
      return _curveExpression.evaluate(curve, feature, scaler);
   }


   @Override
   public Collection<? extends GSymbol2D<? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>>> getSurfaceSymbols(final ISurface2D<? extends IFinite2DBounds<?>> surface,
                                                                                                                        final IGlobeFeature<IVector2, ? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>> feature,
                                                                                                                        final IVectorial2DRenderingScaler scaler) {
      return _surfaceExpression.evaluate(surface, feature, scaler);
   }


}
