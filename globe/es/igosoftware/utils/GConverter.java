package es.igosoftware.utils;

import es.igosoftware.euclid.bounding.GAxisAlignedRectangle;
import es.igosoftware.euclid.projection.GProjection;
import es.igosoftware.euclid.vector.IVector2;
import es.igosoftware.euclid.vector.IVector3;
import gov.nasa.worldwind.geom.Angle;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.geom.Sector;

public final class GConverter {
   private GConverter() {}


   public static Sector createSector(final GProjection sourceProjection,
                                     final GAxisAlignedRectangle rectangle) {
      //      final UTMCoord utmLower = GConverter.toUTM(sourceProjection, rectangle.lower);
      //      final UTMCoord utmUpper = GConverter.toUTM(sourceProjection, rectangle.upper);
      //      return new Sector(utmLower.getLatitude(), utmUpper.getLatitude(), utmLower.getLongitude(), utmUpper.getLongitude());

      final IVector2<?> geodesicLower = rectangle._lower.reproject(sourceProjection, GProjection.EPSG_4326);
      final IVector2<?> geodesicUpper = rectangle._upper.reproject(sourceProjection, GProjection.EPSG_4326);

      final Angle minLatitude = Angle.fromRadiansLatitude(geodesicLower.y());
      final Angle maxLatitude = Angle.fromRadiansLatitude(geodesicUpper.y());
      final Angle minLongitude = Angle.fromRadiansLongitude(geodesicLower.x());
      final Angle maxLongitude = Angle.fromRadiansLongitude(geodesicUpper.x());

      return new Sector(minLatitude, maxLatitude, minLongitude, maxLongitude);
   }


   public static Position toPosition(final GProjection sourceProjection,
                                     final IVector3<?> point) {
      final IVector3<?> geodesicPoint = point.reproject(sourceProjection, GProjection.EPSG_4326);
      return Position.fromRadians(geodesicPoint.y(), geodesicPoint.x(), geodesicPoint.z());
   }


}
