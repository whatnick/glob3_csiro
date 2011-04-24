

package es.igosoftware.euclid.experimental.vectorial.rendering.styling;

import java.awt.geom.Area;

import es.igosoftware.euclid.IBoundedGeometry2D;
import es.igosoftware.euclid.ICurve2D;
import es.igosoftware.euclid.ISurface2D;
import es.igosoftware.euclid.bounding.IFinite2DBounds;
import es.igosoftware.euclid.experimental.measurement.GArea;
import es.igosoftware.euclid.experimental.measurement.GLength;
import es.igosoftware.euclid.experimental.measurement.IMeasure;
import es.igosoftware.euclid.experimental.vectorial.rendering.context.IVectorial2DRenderingScaler;
import es.igosoftware.euclid.experimental.vectorial.rendering.features.GDebugNodeRenderingShape;
import es.igosoftware.euclid.experimental.vectorial.rendering.features.GEllipseRenderingSymbol;
import es.igosoftware.euclid.experimental.vectorial.rendering.features.GPolygonRenderingShape;
import es.igosoftware.euclid.experimental.vectorial.rendering.features.GPolygonalChainRenderingShape;
import es.igosoftware.euclid.experimental.vectorial.rendering.features.ICurveRenderingShape;
import es.igosoftware.euclid.experimental.vectorial.rendering.features.INodeRenderingShape;
import es.igosoftware.euclid.experimental.vectorial.rendering.features.IRenderingSymbol;
import es.igosoftware.euclid.experimental.vectorial.rendering.features.ISurfaceRenderingShape;
import es.igosoftware.euclid.features.IGlobeFeature;
import es.igosoftware.euclid.ntree.GGTNode;
import es.igosoftware.euclid.shape.IComplexPolygon2D;
import es.igosoftware.euclid.shape.IPolygon2D;
import es.igosoftware.euclid.shape.IPolygonalChain2D;
import es.igosoftware.euclid.shape.ISimplePolygon2D;
import es.igosoftware.euclid.vector.IVector2;


public abstract class GRenderingStyleAbstract
         implements
            IRenderingStyle {

   /* -------------------------------------------------------------------------------------- */
   /* nodes */
   @Override
   public INodeRenderingShape getNodeShape(final GGTNode<IVector2, IGlobeFeature<IVector2, ? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>>, IBoundedGeometry2D<? extends IFinite2DBounds<?>>> node,
                                           final IVectorial2DRenderingScaler scaler) {
      if (!isDebugRendering()) {
         return null;
      }

      return new GDebugNodeRenderingShape(node, scaler);
   }


   /* -------------------------------------------------------------------------------------- */
   /* points */
   @Override
   public IRenderingSymbol getPointSymbol(final IVector2 point,
                                          final IGlobeFeature<IVector2, ? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>> feature,
                                          final IVectorial2DRenderingScaler scaler) {
      final IMeasure<GArea> pointSize = getPointSize(point, feature, scaler);
      final IMeasure<GLength> pointBorderSize = getPointBorderSize(point, feature, scaler);
      return new GEllipseRenderingSymbol(point, pointSize, pointBorderSize, scaler);
   }


   /* -------------------------------------------------------------------------------------- */
   /* surfaces */
   @Override
   public ISurfaceRenderingShape<ISurface2D<? extends IFinite2DBounds<?>>> getSurfaceShape(final ISurface2D<? extends IFinite2DBounds<?>> surface,
                                                                                           final IGlobeFeature<IVector2, ? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>> feature,
                                                                                           final IVectorial2DRenderingScaler scaler) {

      final IMeasure<GLength> surfaceBorderSize = getSurfaceBorderSize(surface, feature, scaler);

      if (surface instanceof IPolygon2D) {
         final IPolygon2D polygon = (IPolygon2D) surface;

         if (polygon instanceof IComplexPolygon2D) {
            final IComplexPolygon2D complexPolygon = (IComplexPolygon2D) polygon;

            final Area complexShape = scaler.toScaledAndTranslatedPoints(complexPolygon.getHull()).asPolygonArea();

            for (final ISimplePolygon2D hole : complexPolygon.getHoles()) {
               // complexShape.exclusiveOr(scaler.toScaledAndTranslatedPoints(hole).asPolygonArea());
               complexShape.subtract(scaler.toScaledAndTranslatedPoints(hole).asPolygonArea());
            }

            return new GPolygonRenderingShape(polygon, complexShape, surfaceBorderSize, scaler);
         }


         return new GPolygonRenderingShape(polygon, scaler.toScaledAndTranslatedPoints(polygon).asPolygonShape(),
                  surfaceBorderSize, scaler);
      }

      throw new RuntimeException("Surface type (" + surface.getClass() + ") not supported");
   }


   /* -------------------------------------------------------------------------------------- */
   /* curves */
   @Override
   public ICurveRenderingShape<ICurve2D<? extends IFinite2DBounds<?>>> getCurveShape(final ICurve2D<? extends IFinite2DBounds<?>> curve,
                                                                                     final IGlobeFeature<IVector2, ? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>> feature,
                                                                                     final IVectorial2DRenderingScaler scaler) {
      if (curve instanceof IPolygonalChain2D) {
         final IPolygonalChain2D polygonalChain = (IPolygonalChain2D) curve;

         final IMeasure<GLength> curveBorderSize = getCurveBorderSize(polygonalChain, feature, scaler);
         return new GPolygonalChainRenderingShape(polygonalChain, scaler.toScaledAndTranslatedPoints(polygonalChain),
                  curveBorderSize, scaler);
      }

      throw new RuntimeException("Curve type (" + curve.getClass() + ") not supported");
   }


}
