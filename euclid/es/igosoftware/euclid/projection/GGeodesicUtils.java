package es.igosoftware.euclid.projection;

import es.igosoftware.euclid.vector.IVector2;

public class GGeodesicUtils {
   private GGeodesicUtils() {}


   // Earth's quadratic mean radius for WGS-84
   private static final double EARTH_QUADRATIC_RADIUS_IN_METERS = 6372797.560856;


   /**
    * Computes the arc, in radians, between two WGS-84 positions.
    */
   public static double arcInRadians(final IVector2<?> from,
                                     final IVector2<?> to) {
      final double fromLatitude = from.y();
      final double fromLongitude = from.x();
      final double toLatitude = to.y();
      final double toLongidute = to.x();

      final double latitudeArc = fromLatitude - toLatitude;
      final double longitudeArc = fromLongitude - toLongidute;

      double latitudeH = Math.sin(latitudeArc * 0.5);
      latitudeH *= latitudeH;

      double lontitudeH = Math.sin(longitudeArc * 0.5);
      lontitudeH *= lontitudeH;

      final double tmp = Math.cos(fromLatitude) * Math.cos(toLatitude);
      return 2.0 * Math.asin(Math.sqrt(latitudeH + tmp * lontitudeH));
   }


   /**
    * Computes the distance, in meters, between two WGS-84 positions.
    */
   public static double distanceInMeters(final IVector2<?> from,
                                         final IVector2<?> to) {
      return EARTH_QUADRATIC_RADIUS_IN_METERS * arcInRadians(from, to);
   }


}
