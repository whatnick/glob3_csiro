

package es.igosoftware.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.MultiPoint;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;

import es.igosoftware.euclid.IBoundedGeometry;
import es.igosoftware.euclid.bounding.GAxisAlignedRectangle;
import es.igosoftware.euclid.bounding.IFiniteBounds;
import es.igosoftware.euclid.shape.GComplexPolygon2D;
import es.igosoftware.euclid.shape.GLinesStrip2D;
import es.igosoftware.euclid.shape.GQuad2D;
import es.igosoftware.euclid.shape.GSegment2D;
import es.igosoftware.euclid.shape.GShape;
import es.igosoftware.euclid.shape.GSimplePolygon2D;
import es.igosoftware.euclid.shape.GTriangle2D;
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


   public static List<? extends IBoundedGeometry<IVector2<?>, ?, GAxisAlignedRectangle>> toEuclid(final Geometry jtsGeometry) {
      if (jtsGeometry == null) {
         return null;
      }
      else if (jtsGeometry instanceof Point) {
         return Collections.singletonList(toEuclid((Point) jtsGeometry));
      }
      else if (jtsGeometry instanceof Polygon) {
         return Collections.singletonList(toEuclid((Polygon) jtsGeometry));
      }
      else if (jtsGeometry instanceof LineString) {
         return Collections.singletonList(toEuclid((LineString) jtsGeometry));
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


   public static IVector2<?> toEuclid(final Point jtsPoint) {
      if (jtsPoint == null) {
         return null;
      }

      return new GVector2D(jtsPoint.getX(), jtsPoint.getY());
   }


   public static IPolygon2D<?> toEuclid(final Polygon jtsPolygon) {
      if (jtsPolygon == null) {
         return null;
      }

      final IPolygon2D<?> outerPolygon = createEuclidPolygon(jtsPolygon.getCoordinates());

      final int holesCount = jtsPolygon.getNumInteriorRing();
      if (holesCount == 0) {
         return outerPolygon;
      }


      final List<IPolygon2D<?>> jtsHoles = new ArrayList<IPolygon2D<?>>(holesCount);
      for (int j = 0; j < holesCount; j++) {
         final LineString jtsHole = jtsPolygon.getInteriorRingN(j);

         jtsHoles.add(createEuclidPolygon(jtsHole.getCoordinates()));
      }

      return new GComplexPolygon2D(outerPolygon, jtsHoles);
   }


   public static IPolygon2D<?> createEuclidPolygon(final Coordinate... coordinates) {
      return GShape.createPolygon2(false, toEuclid(coordinates));
   }


   public static IPolygon2D<?> createEuclidLine(final Coordinate... coordinates) {
      return GShape.createLine2(false, toEuclid(coordinates));
   }


   public static IVector2<?>[] toEuclid(final Coordinate... coordinates) {
      final IVector2<?>[] points = new IVector2<?>[coordinates.length];
      for (int i = 0; i < coordinates.length; i++) {
         final Coordinate coordinate = coordinates[i];
         points[i] = new GVector2D(coordinate.x, coordinate.y);
      }
      return points;
   }


   public static IPolygon2D<?> toEuclid(final LineString jtsLine) {
      if (jtsLine == null) {
         return null;
      }

      return createEuclidLine(jtsLine.getCoordinates());
   }


   public static List<IVector2<?>> toEuclid(final MultiPoint jtsPoints) {
      if (jtsPoints == null) {
         return null;
      }

      final int count = jtsPoints.getNumGeometries();
      final List<IVector2<?>> result = new ArrayList<IVector2<?>>(count);
      for (int i = 0; i < count; i++) {
         result.add(toEuclid((Point) jtsPoints.getGeometryN(i)));
      }

      return result;
   }


   public static List<IPolygon2D<?>> toEuclid(final MultiLineString jtsLines) {
      if (jtsLines == null) {
         return null;
      }

      final int count = jtsLines.getNumGeometries();
      final List<IPolygon2D<?>> result = new ArrayList<IPolygon2D<?>>(count);
      for (int i = 0; i < count; i++) {
         result.add(toEuclid((LineString) jtsLines.getGeometryN(i)));
      }

      return result;
   }


   public static List<IPolygon2D<?>> toEuclid(final MultiPolygon jtsPolygons) {
      if (jtsPolygons == null) {
         return null;
      }

      final int count = jtsPolygons.getNumGeometries();
      final List<IPolygon2D<?>> result = new ArrayList<IPolygon2D<?>>(count);
      for (int i = 0; i < count; i++) {
         result.add(toEuclid((Polygon) jtsPolygons.getGeometryN(i)));
      }

      return result;
   }


   public static Geometry toJTS(final IBoundedGeometry<IVector2<?>, ?, ? extends IFiniteBounds<IVector2<?>, ?>> geometry) {


      if (geometry == null) {
         return null;
      }


      if (geometry instanceof IVector2) {
         return toJTS(((IVector2<?>) geometry));
      }
      else if (geometry instanceof GLinesStrip2D) {
         return toJTS((GLinesStrip2D) geometry);
      }
      else if (geometry instanceof GSegment2D) {
         return toJTS((GSegment2D) geometry);
      }
      else if (geometry instanceof GComplexPolygon2D) {
         return toJTS((GComplexPolygon2D) geometry);
      }
      else if (geometry instanceof GSimplePolygon2D) {
         return toJTS((GSimplePolygon2D) geometry);
      }
      else if (geometry instanceof GQuad2D) {
         return toJTS((GQuad2D) geometry);
      }
      else if (geometry instanceof GTriangle2D) {
         return toJTS((GTriangle2D) geometry);
      }
      else {
         throw new RuntimeException("Euclid geometry not supported (" + geometry.getClass() + ")");
      }
   }


   private static Coordinate[] getJTSCoordinates(final IPointsContainer<IVector2<?>, ?> pointsContainer) {
      if (pointsContainer == null) {
         return null;
      }

      final Coordinate[] result = new Coordinate[pointsContainer.getPointsCount()];

      for (int i = 0; i < result.length; i++) {
         final IVector2<?> point = pointsContainer.getPoint(i);
         result[i] = new Coordinate(point.x(), point.y());
      }

      return result;
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

      final List<IPolygon2D<?>> holes = polygon.getHoles();
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


   public static Coordinate[] toJTS(final List<IVector2<?>> points) {
      if (points == null) {
         return null;
      }

      final Coordinate[] result = new Coordinate[points.size()];
      for (int i = 0; i < result.length; i++) {
         final IVector2<?> point = points.get(i);
         result[i] = new Coordinate(point.x(), point.y());
      }

      return result;
   }


   public static Coordinate[] toJTS(final IVector2<?>... points) {
      final Coordinate[] result = new Coordinate[points.length];
      for (int i = 0; i < result.length; i++) {
         final IVector2<?> point = points[i];
         result[i] = new Coordinate(point.x(), point.y());
      }

      return result;
   }


   public static Point toJTS(final IVector2<?> vector) {
      if (vector == null) {
         return null;
      }

      return JTS_GEOMETRY_FACTORY.createPoint(new Coordinate(vector.x(), vector.y()));
   }


}
