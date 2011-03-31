

package es.igosoftware.utils;

import java.util.ArrayList;
import java.util.List;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryCollection;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.MultiPoint;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;

import es.igosoftware.euclid.GMultiGeometry2D;
import es.igosoftware.euclid.IBoundedGeometry;
import es.igosoftware.euclid.bounding.IFiniteBounds;
import es.igosoftware.euclid.shape.GComplexPolygon2D;
import es.igosoftware.euclid.shape.GLinesStrip2D;
import es.igosoftware.euclid.shape.GQuad2D;
import es.igosoftware.euclid.shape.GSegment2D;
import es.igosoftware.euclid.shape.GShape;
import es.igosoftware.euclid.shape.GSimplePolygon2D;
import es.igosoftware.euclid.shape.GTriangle2D;
import es.igosoftware.euclid.shape.ILineal2D;
import es.igosoftware.euclid.shape.IPolygon2D;
import es.igosoftware.euclid.vector.GVector2D;
import es.igosoftware.euclid.vector.IPointsContainer;
import es.igosoftware.euclid.vector.IVector2;


/**
 * 
 * Utility methods to convert JTS geometries from/to Euclid geometries
 * 
 * @author dgd
 * 
 */
public class GJTSUtils {

   private static final GeometryFactory JTS_GEOMETRY_FACTORY = new GeometryFactory();


   private GJTSUtils() {
      // do not instantiate, just static methods
   }


   public static IBoundedGeometry<IVector2, ? extends IFiniteBounds<IVector2, ?>> toEuclid(final Geometry jtsGeometry) {
      if (jtsGeometry == null) {
         return null;
      }
      else if (jtsGeometry instanceof Point) {
         return toEuclid((Point) jtsGeometry);
      }
      else if (jtsGeometry instanceof Polygon) {
         return toEuclid((Polygon) jtsGeometry);
      }
      else if (jtsGeometry instanceof LineString) {
         return toEuclid((LineString) jtsGeometry);
      }
      else if (jtsGeometry instanceof MultiPoint) {
         return toEuclid((MultiPoint) jtsGeometry);
      }
      else if (jtsGeometry instanceof MultiLineString) {
         return toEuclid((MultiLineString) jtsGeometry);
      }
      else if (jtsGeometry instanceof MultiPolygon) {
         return toEuclid((MultiPolygon) jtsGeometry);
      }
      else {
         throw new RuntimeException("JTS Geometry not supported " + jtsGeometry.getGeometryType());
      }
   }


   public static IVector2 toEuclid(final Point jtsPoint) {
      if (jtsPoint == null) {
         return null;
      }

      return new GVector2D(jtsPoint.getX(), jtsPoint.getY());
   }


   public static IPolygon2D toEuclid(final Polygon jtsPolygon) {
      if (jtsPolygon == null) {
         return null;
      }

      final IPolygon2D outerPolygon = createEuclidPolygon(jtsPolygon.getCoordinates());

      final int holesCount = jtsPolygon.getNumInteriorRing();
      if (holesCount == 0) {
         return outerPolygon;
      }


      final List<IPolygon2D> jtsHoles = new ArrayList<IPolygon2D>(holesCount);
      for (int j = 0; j < holesCount; j++) {
         final LineString jtsHole = jtsPolygon.getInteriorRingN(j);

         jtsHoles.add(createEuclidPolygon(jtsHole.getCoordinates()));
      }

      return new GComplexPolygon2D(outerPolygon, jtsHoles);
   }


   public static IPolygon2D createEuclidPolygon(final Coordinate... coordinates) {
      return GShape.createPolygon2(false, toEuclid(coordinates));
   }


   public static ILineal2D createEuclidLine(final Coordinate... coordinates) {
      return GShape.createLine2(false, toEuclid(coordinates));
   }


   public static IVector2[] toEuclid(final Coordinate... coordinates) {
      final IVector2[] points = new IVector2[coordinates.length];
      for (int i = 0; i < coordinates.length; i++) {
         final Coordinate coordinate = coordinates[i];
         points[i] = new GVector2D(coordinate.x, coordinate.y);
      }
      return points;
   }


   public static ILineal2D toEuclid(final LineString jtsLine) {
      if (jtsLine == null) {
         return null;
      }

      return createEuclidLine(jtsLine.getCoordinates());
   }


   public static GMultiGeometry2D<IVector2> toEuclid(final MultiPoint jtsPoints) {
      if (jtsPoints == null) {
         return null;
      }

      final int count = jtsPoints.getNumGeometries();
      final List<IVector2> points = new ArrayList<IVector2>(count);
      for (int i = 0; i < count; i++) {
         points.add(toEuclid((Point) jtsPoints.getGeometryN(i)));
      }

      return new GMultiGeometry2D<IVector2>(points);
   }


   public static GMultiGeometry2D<ILineal2D> toEuclid(final MultiLineString jtsLines) {
      if (jtsLines == null) {
         return null;
      }

      final int count = jtsLines.getNumGeometries();
      final List<ILineal2D> lines = new ArrayList<ILineal2D>(count);
      for (int i = 0; i < count; i++) {
         lines.add(toEuclid((LineString) jtsLines.getGeometryN(i)));
      }

      return new GMultiGeometry2D<ILineal2D>(lines);
   }


   public static GMultiGeometry2D<IPolygon2D> toEuclid(final MultiPolygon jtsPolygons) {
      if (jtsPolygons == null) {
         return null;
      }

      final int count = jtsPolygons.getNumGeometries();
      final List<IPolygon2D> polygons = new ArrayList<IPolygon2D>(count);
      for (int i = 0; i < count; i++) {
         polygons.add(toEuclid((Polygon) jtsPolygons.getGeometryN(i)));
      }

      return new GMultiGeometry2D<IPolygon2D>(polygons);
   }


   public static Geometry toJTS(final IBoundedGeometry<IVector2, ? extends IFiniteBounds<IVector2, ?>> geometry) {
      if (geometry == null) {
         return null;
      }

      if (geometry instanceof IVector2) {
         return toJTS(((IVector2) geometry));
      }
      else if (geometry instanceof ILineal2D) {
         return toJTS((ILineal2D) geometry);
      }
      else if (geometry instanceof IPolygon2D) {
         return toJTS((IPolygon2D) geometry);
      }
      else if (geometry instanceof GMultiGeometry2D) {
         @SuppressWarnings("unchecked")
         final GMultiGeometry2D<IBoundedGeometry<IVector2, ? extends IFiniteBounds<IVector2, ?>>> multigeometry = (GMultiGeometry2D<IBoundedGeometry<IVector2, ? extends IFiniteBounds<IVector2, ?>>>) geometry;
         return toJTS(multigeometry);
      }
      else {
         throw new RuntimeException("Euclid geometry not supported (" + geometry.getClass() + ")");
      }
   }


   private static Coordinate[] getJTSCoordinates(final IPointsContainer<IVector2> pointsContainer) {
      if (pointsContainer == null) {
         return null;
      }

      final Coordinate[] result = new Coordinate[pointsContainer.getPointsCount()];

      for (int i = 0; i < result.length; i++) {
         final IVector2 point = pointsContainer.getPoint(i);
         result[i] = new Coordinate(point.x(), point.y());
      }

      return result;
   }


   public static LineString toJTS(final ILineal2D lineal) {
      if (lineal == null) {
         return null;
      }

      if (lineal instanceof GLinesStrip2D) {
         return toJTS((GLinesStrip2D) lineal);
      }
      else if (lineal instanceof GSegment2D) {
         return toJTS((GSegment2D) lineal);
      }
      else {
         throw new RuntimeException("Lineal type not supported (" + lineal.getClass() + ")");
      }
   }


   public static Polygon toJTS(final IPolygon2D polygon) {
      if (polygon == null) {
         return null;
      }

      if (polygon instanceof GTriangle2D) {
         return toJTS((GTriangle2D) polygon);
      }
      else if (polygon instanceof GQuad2D) {
         return toJTS((GQuad2D) polygon);
      }
      else if (polygon instanceof GSimplePolygon2D) {
         return toJTS((GSimplePolygon2D) polygon);
      }
      else if (polygon instanceof GComplexPolygon2D) {
         return toJTS((GComplexPolygon2D) polygon);
      }
      else {
         throw new RuntimeException("Polygon type not supported (" + polygon.getClass() + ")");
      }
   }


   public static LineString toJTS(final GLinesStrip2D linesStrip) {
      if (linesStrip == null) {
         return null;
      }

      return JTS_GEOMETRY_FACTORY.createLineString(getJTSCoordinates(linesStrip));
   }


   public static LineString toJTS(final GSegment2D segment) {
      if (segment == null) {
         return null;
      }

      return JTS_GEOMETRY_FACTORY.createLineString(getJTSCoordinates(segment));
   }


   public static Polygon toJTS(final GQuad2D quad) {
      if (quad == null) {
         return null;
      }

      final LinearRing jtsShell = JTS_GEOMETRY_FACTORY.createLinearRing(getJTSCoordinates(quad));
      final LinearRing[] jtsHoles = null;
      return JTS_GEOMETRY_FACTORY.createPolygon(jtsShell, jtsHoles);
   }


   public static Polygon toJTS(final GSimplePolygon2D polygon) {
      if (polygon == null) {
         return null;
      }

      final LinearRing jtsShell = JTS_GEOMETRY_FACTORY.createLinearRing(getJTSCoordinates(polygon));
      final LinearRing[] jtsHoles = null;
      return JTS_GEOMETRY_FACTORY.createPolygon(jtsShell, jtsHoles);
   }


   public static Polygon toJTS(final GComplexPolygon2D polygon) {
      if (polygon == null) {
         return null;
      }

      final LinearRing jtsShell = JTS_GEOMETRY_FACTORY.createLinearRing(getJTSCoordinates(polygon));

      final List<IPolygon2D> holes = polygon.getHoles();
      final LinearRing[] jtsHoles = new LinearRing[holes.size()];

      for (int i = 0; i < jtsHoles.length; i++) {
         jtsHoles[i] = JTS_GEOMETRY_FACTORY.createLinearRing(getJTSCoordinates(holes.get(i)));
      }

      return JTS_GEOMETRY_FACTORY.createPolygon(jtsShell, jtsHoles);
   }


   public static Polygon toJTS(final GTriangle2D triangle) {
      if (triangle == null) {
         return null;
      }

      final LinearRing jtsShell = JTS_GEOMETRY_FACTORY.createLinearRing(getJTSCoordinates(triangle));
      final LinearRing[] jtsHoles = null;
      return JTS_GEOMETRY_FACTORY.createPolygon(jtsShell, jtsHoles);
   }


   public static GeometryCollection toJTS(final GMultiGeometry2D<? extends IBoundedGeometry<IVector2, ? extends IFiniteBounds<IVector2, ?>>> multigeometry) {
      final IBoundedGeometry<IVector2, ? extends IFiniteBounds<IVector2, ?>> exemplar = multigeometry.getExemplar();

      if (exemplar instanceof ILineal2D) {
         return createMultiLineString(multigeometry);
      }
      else if (exemplar instanceof IPolygon2D) {
         return createMultiPolygon(multigeometry);
      }
      else if (exemplar instanceof IVector2) {
         return createMultiPoint(multigeometry);
      }
      else {
         throw new RuntimeException("Multigeometry children's type not supported (" + exemplar.getClass() + ")");
      }
   }


   private static MultiLineString createMultiLineString(final GMultiGeometry2D<? extends IBoundedGeometry<IVector2, ? extends IFiniteBounds<IVector2, ?>>> multigeometry) {
      final LineString[] children = new LineString[multigeometry.getChildrenCount()];
      int index = 0;

      for (final IBoundedGeometry<IVector2, ? extends IFiniteBounds<IVector2, ?>> child : multigeometry) {
         children[index++] = toJTS((ILineal2D) child);
      }

      return JTS_GEOMETRY_FACTORY.createMultiLineString(children);
   }


   private static MultiPolygon createMultiPolygon(final GMultiGeometry2D<? extends IBoundedGeometry<IVector2, ? extends IFiniteBounds<IVector2, ?>>> multigeometry) {
      final Polygon[] children = new Polygon[multigeometry.getChildrenCount()];
      int index = 0;

      for (final IBoundedGeometry<IVector2, ? extends IFiniteBounds<IVector2, ?>> child : multigeometry) {
         children[index++] = toJTS((IPolygon2D) child);
      }

      return JTS_GEOMETRY_FACTORY.createMultiPolygon(children);
   }


   private static MultiPoint createMultiPoint(final GMultiGeometry2D<? extends IBoundedGeometry<IVector2, ? extends IFiniteBounds<IVector2, ?>>> multigeometry) {
      final Point[] children = new Point[multigeometry.getChildrenCount()];
      int index = 0;

      for (final IBoundedGeometry<IVector2, ? extends IFiniteBounds<IVector2, ?>> child : multigeometry) {
         children[index++] = toJTS((IVector2) child);
      }

      return JTS_GEOMETRY_FACTORY.createMultiPoint(children);
   }


   public static Point[] toJTS(final List<IVector2> points) {
      if (points == null) {
         return null;
      }

      final Point[] result = new Point[points.size()];
      for (int i = 0; i < result.length; i++) {
         result[i] = toJTS(points.get(i));
      }

      return result;
   }


   public static Point[] toJTS(final IVector2... points) {
      final Point[] result = new Point[points.length];

      for (int i = 0; i < result.length; i++) {
         result[i] = toJTS(points[i]);
      }

      return result;
   }


   public static Point toJTS(final IVector2 vector) {
      if (vector == null) {
         return null;
      }

      return JTS_GEOMETRY_FACTORY.createPoint(new Coordinate(vector.x(), vector.y()));
   }


}
