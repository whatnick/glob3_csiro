

package es.igosoftware.euclid.experimental.vectorial.rendering.styledgeometries;


import java.awt.Paint;
import java.awt.Polygon;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Area;

import es.igosoftware.euclid.experimental.vectorial.rendering.context.IVectorial2DDrawer;
import es.igosoftware.euclid.shape.IComplexPolygon2D;
import es.igosoftware.euclid.shape.IPolygon2D;
import es.igosoftware.euclid.shape.ISimplePolygon2D;
import es.igosoftware.euclid.vector.IVector2;
import es.igosoftware.util.GMath;


public class GStyledPolygon2D
         extends
            GStyledSurface2D<IPolygon2D> {


   private final Shape _polygonShape;


   public GStyledPolygon2D(final IPolygon2D polygon,
                           final ISurface2DStyle surfaceStyle,
                           final ICurve2DStyle curveStyle) {
      super(polygon, surfaceStyle, curveStyle);

      _polygonShape = initializeShape();
   }


   private Shape initializeShape() {

      final IPolygon2D polygon = _geometry;

      if (polygon instanceof IComplexPolygon2D) {
         final IComplexPolygon2D complexPolygon = (IComplexPolygon2D) polygon;

         final Area complexShape = asPolygonArea(complexPolygon.getHull());

         for (final ISimplePolygon2D hole : complexPolygon.getHoles()) {
            // complexShape.exclusiveOr(scaler.toScaledAndTranslatedPoints(hole).asPolygonArea());
            complexShape.subtract(asPolygonArea(hole));
         }

         return complexShape;
      }


      return asPolygonShape(polygon);
   }


   private static final Area asPolygonArea(final ISimplePolygon2D polygon) {
      return new Area(asPolygonShape(polygon));
   }


   private static Shape asPolygonShape(final IPolygon2D polygon) {
      final int pointsCount = polygon.getPointsCount();
      final int[] xPoints = new int[pointsCount];
      final int[] yPoints = new int[pointsCount];

      for (int i = 0; i < pointsCount; i++) {
         final IVector2 point = polygon.getPoint(i);
         xPoints[i] = GMath.toRoundedInt(point.x());
         yPoints[i] = GMath.toRoundedInt(point.y());
      }

      return new Polygon(xPoints, yPoints, pointsCount);
   }


   @Override
   protected void draw(final IVectorial2DDrawer drawer,
                       final boolean debugRendering) {
      // render surface
      final Paint fillPaint = _surfaceStyle.getSurfacePaint();
      if (fillPaint != null) {
         drawer.fill(_polygonShape, fillPaint);
      }


      // render border
      final Stroke borderStroke = _curveStyle.getBorderStroke();
      if (borderStroke != null) {
         final Paint borderPaint = _curveStyle.getBorderPaint();
         drawer.draw(_polygonShape, borderPaint, borderStroke);
      }
   }


}
