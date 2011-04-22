

package es.igosoftware.euclid.experimental.vectorial.rendering;

import java.awt.geom.Area;

import es.igosoftware.euclid.IBoundedGeometry;
import es.igosoftware.euclid.ICurve2D;
import es.igosoftware.euclid.ISurface2D;
import es.igosoftware.euclid.bounding.IFiniteBounds;
import es.igosoftware.euclid.experimental.measurement.GArea;
import es.igosoftware.euclid.experimental.measurement.GLength;
import es.igosoftware.euclid.experimental.measurement.IMeasure;
import es.igosoftware.euclid.features.IGlobeFeature;
import es.igosoftware.euclid.shape.IComplexPolygon2D;
import es.igosoftware.euclid.shape.IPolygon2D;
import es.igosoftware.euclid.shape.IPolygonalChain2D;
import es.igosoftware.euclid.shape.ISimplePolygon2D;
import es.igosoftware.euclid.vector.IVector2;


public abstract class GRenderingStyleAbstract
         implements
            IRenderingStyle {


   /* points */
   @Override
   public GRenderingSymbol getPointSymbol(final IVector2 point,
                                          final IGlobeFeature<IVector2, ? extends IBoundedGeometry<IVector2, ? extends IFiniteBounds<IVector2, ?>>> feature,
                                          final IVectorialRenderingContext rc) {
      final IMeasure<GArea> pointSize = getPointSize(point, feature, rc);
      final IMeasure<GLength> pointBorderSize = getPointBorderSize(point, feature, rc);
      return new GEllipseRenderingSymbol(point, pointSize, pointBorderSize, this, rc);
   }


   @Override
   public void drawPoint(final IVector2 point,
                         final IGlobeFeature<IVector2, ? extends IBoundedGeometry<IVector2, ? extends IFiniteBounds<IVector2, ?>>> feature,
                         final IVectorialRenderingContext rc) {

      final GRenderingSymbol symbol = getPointSymbol(point, feature, rc);
      if (symbol != null) {
         symbol.draw(point, feature, this, rc);
      }
   }


   /* surfaces */
   @Override
   public GPolygonRenderingShape getSurfaceShape(final ISurface2D<?> surface,
                                                 final IGlobeFeature<IVector2, ? extends IBoundedGeometry<IVector2, ? extends IFiniteBounds<IVector2, ?>>> feature,
                                                 final IVectorialRenderingContext rc) {

      final IMeasure<GLength> surfaceBorderSize = getSurfaceBorderSize(surface, feature, rc);

      if (surface instanceof IPolygon2D) {
         final IPolygon2D polygon = (IPolygon2D) surface;
         if (polygon instanceof IComplexPolygon2D) {
            final IComplexPolygon2D complexPolygon = (IComplexPolygon2D) polygon;

            final Area complexShape = rc.getPoints(complexPolygon.getHull()).asArea();

            for (final ISimplePolygon2D hole : complexPolygon.getHoles()) {
               // complexShape.exclusiveOr(getPoints(hole, scale, region).asArea());
               complexShape.subtract(rc.getPoints(hole).asArea());
            }

            return new GPolygonRenderingShape(polygon, complexShape, surfaceBorderSize, this, rc);
         }


         return new GPolygonRenderingShape(polygon, rc.getPoints(polygon).asShape(), surfaceBorderSize, this, rc);
      }

      throw new RuntimeException("Surface type (" + surface.getClass() + ") not supported");
   }


   @Override
   public void drawSurface(final ISurface2D<?> surface,
                           final IGlobeFeature<IVector2, ? extends IBoundedGeometry<IVector2, ? extends IFiniteBounds<IVector2, ?>>> feature,
                           final IVectorialRenderingContext rc) {
      final GPolygonRenderingShape shape = getSurfaceShape(surface, feature, rc);
      if (shape != null) {
         shape.draw((IPolygon2D) surface, feature, this, rc);
      }
   }


   /* curves */

   @Override
   public GPolygonalChainRenderingShape getCurveShape(final ICurve2D<?> curve,
                                                      final IGlobeFeature<IVector2, ? extends IBoundedGeometry<IVector2, ? extends IFiniteBounds<IVector2, ?>>> feature,
                                                      final IVectorialRenderingContext rc) {
      if (curve instanceof IPolygonalChain2D) {
         final IPolygonalChain2D polygonalChain = (IPolygonalChain2D) curve;

         final IMeasure<GLength> curveBorderSize = getCurveBorderSize(polygonalChain, feature, rc);
         return new GPolygonalChainRenderingShape(polygonalChain, rc.getPoints(polygonalChain), curveBorderSize, this, rc);
      }

      throw new RuntimeException("Curve type (" + curve.getClass() + ") not supported");
   }


   @Override
   public void drawCurve(final ICurve2D<?> curve,
                         final IGlobeFeature<IVector2, ? extends IBoundedGeometry<IVector2, ? extends IFiniteBounds<IVector2, ?>>> feature,
                         final IVectorialRenderingContext rc) {
      final GPolygonalChainRenderingShape shape = getCurveShape(curve, feature, rc);
      if (shape != null) {
         shape.draw((IPolygonalChain2D) curve, feature, this, rc);
      }
   }


}
