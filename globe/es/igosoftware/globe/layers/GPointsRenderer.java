package es.igosoftware.globe.layers;

import es.igosoftware.euclid.projection.GProjection;
import es.igosoftware.euclid.vector.GVector2D;
import es.igosoftware.euclid.vector.IVector2;
import gov.nasa.worldwind.geom.Angle;
import gov.nasa.worldwind.geom.LatLon;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.render.Material;
import gov.nasa.worldwind.render.Renderable;
import gov.nasa.worldwind.render.markers.BasicMarkerAttributes;
import gov.nasa.worldwind.render.markers.BasicMarkerShape;
import gov.nasa.worldwind.render.markers.MarkerAttributes;

import java.awt.Color;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Point;

public class GPointsRenderer
         extends
            GVectorRenderer {

   public static final int ALTITUDE_METHOD_CLAMPED_TO_GROUND  = 0;
   public static final int ALTITUDE_METHOD_RELATIVE_TO_GROUND = 1;
   public static final int ALTITUDE_METHOD_ABSOLUTE           = 2;
   public static final int TAKE_ALTITUDE_FROM_FIXED           = 0;
   public static final int TAKE_ALTITUDE_FROM_FIELD           = 1;

   private double          _size                              = 10;
   private String          _shapeType                         = BasicMarkerShape.SPHERE;
   private double          _fixedAltitude                     = 0;
   private int             _altitudeField                     = 0;
   private int             _altitudeMethod                    = ALTITUDE_METHOD_CLAMPED_TO_GROUND;
   private int             _altitudeOrigin                    = TAKE_ALTITUDE_FROM_FIXED;


   public GPointsRenderer(final Feature[] features) {

      super(features);

   }


   @Override
   public Renderable[] getRenderables(final Feature feature,
                                      final GProjection crs) {

      final Point geom = (Point) feature._geometry;
      final Coordinate coord = geom.getCoordinate();

      LatLon latlon;
      if (crs.equals(GProjection.EPSG_4326)) {
         //if WGS84, we do not transform, but assume that the coordinates are in degrees
         //(as opossed to the coordinates given by proj4, which are radians)
         latlon = new LatLon(Angle.fromDegrees(coord.y), Angle.fromDegrees(coord.x));
      }
      else {
         final IVector2<?> transformedPt = crs.transformPoint(GProjection.EPSG_4326, new GVector2D(coord.x, coord.y));
         latlon = new LatLon(Angle.fromRadians(transformedPt.y()), Angle.fromRadians(transformedPt.x()));
      }

      double dAltitude = _globe.getElevation(latlon.latitude, latlon.longitude) + 1;

      double dValue = 0d;

      try {
         dValue = ((Number) feature._attributes[_fieldIndex]).doubleValue();
      }
      catch (final Exception e) {}

      final Color color;

      if (_coloringMethod == COLORING_METHOD_COLOR_RAMP) {
         color = new Color(getColorFromColorRamp(dValue));
      }
      else {
         color = _color;
      }


      if (_altitudeMethod == ALTITUDE_METHOD_CLAMPED_TO_GROUND) {

      }
      else {
         if (_altitudeOrigin == TAKE_ALTITUDE_FROM_FIELD) {
            try {
               dAltitude = ((Number) feature._attributes[_altitudeField]).doubleValue();
            }
            catch (final Exception e) {
               dAltitude = _globe.getElevation(latlon.latitude, latlon.longitude) + 1;
            }

         }
         else if (_altitudeOrigin == TAKE_ALTITUDE_FROM_FIXED) {
            dAltitude = _fixedAltitude;
         }
      }

      if (_altitudeOrigin == ALTITUDE_METHOD_RELATIVE_TO_GROUND) {
         dAltitude += _globe.getElevation(latlon.latitude, latlon.longitude);
      }

      final MarkerAttributes markerAttributes = new BasicMarkerAttributes(new Material(color), _shapeType, 1, _size, _size);

      return new RenderableMarker[] { new RenderableMarker(new Position(latlon, dAltitude), markerAttributes) };

   }


   public double getSize() {
      return _size;
   }


   public void setSize(final double size) {
      _size = size;
   }


   public String getShapeType() {
      return _shapeType;
   }


   public void setShapeType(final String shapeType) {
      _shapeType = shapeType;
   }


   public double getFixedAltitude() {
      return _fixedAltitude;
   }


   public void setFixedAltitude(final double fixedAltitude) {
      _fixedAltitude = fixedAltitude;
   }


   public int getAltitudeField() {
      return _altitudeField;
   }


   public void setAltitudeField(final int altitudeField) {
      _altitudeField = altitudeField;
   }


   public int getAltitudeMethod() {
      return _altitudeMethod;
   }


   public void setAltitudeMethod(final int altitudeMethod) {
      _altitudeMethod = altitudeMethod;
   }


   public int getAltitudeOrigin() {
      return _altitudeOrigin;
   }


   public void setAltitudeOrigin(final int altitudeOrigin) {
      _altitudeOrigin = altitudeOrigin;
   }

}
