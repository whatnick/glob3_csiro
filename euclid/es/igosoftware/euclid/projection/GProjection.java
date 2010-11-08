package es.igosoftware.euclid.projection;

import java.util.ArrayList;

import com.sun.jna.Pointer;

import es.igosoftware.euclid.vector.GVector2D;
import es.igosoftware.euclid.vector.GVector3D;
import es.igosoftware.euclid.vector.IVector2;
import es.igosoftware.euclid.vector.IVector3;


public enum GProjection {

   EUCLID(null),
   EPSG_23029("+proj=utm +zone=29 +ellps=intl +units=m +no_defs", 23029),
   EPSG_23030("+proj=utm +zone=30 +ellps=intl +units=m +no_defs", 23030),
   EPSG_4326("+proj=longlat +ellps=WGS84 +datum=WGS84 +no_defs", 4326);

   private static final int  NO_EPSG_CODE = -1;

   private final String      _proj4Definition;


   private transient Pointer _proj4Pointer;
   private int               _epsgCode;


   private GProjection(final String proj4Definition,
                       final int epsgCode) {
      _proj4Definition = proj4Definition;
      _epsgCode = epsgCode;
   }


   private GProjection(final String proj4Definition) {
      _proj4Definition = proj4Definition;
      _epsgCode = NO_EPSG_CODE;
   }


   public static GProjection get(final int epsgCode) {
      for (final GProjection proj : GProjection.values()) {
         if (proj._epsgCode == epsgCode) {
            return proj;
         }
      }

      return null;
   }


   public static GProjection[] getEPSGProjections() {

      final GProjection[] projs = values();
      final ArrayList<GProjection> list = new ArrayList<GProjection>();
      for (final GProjection proj : projs) {
         if (proj.isEPSG()) {
            list.add(proj);
         }
      }

      return list.toArray(new GProjection[0]);

   }


   private boolean isEPSG() {

      return _epsgCode != NO_EPSG_CODE;

   }


   public synchronized Pointer getProj4Pointer() {
      if (_proj4Definition == null) {
         return null;
      }

      if (_proj4Pointer == null) {
         _proj4Pointer = GProj4Library.pj_init_plus(_proj4Definition);
      }

      return _proj4Pointer;
   }


   public IVector3<?> transformPoint(final GProjection targetProjection,
                                     final IVector3<?> point) {
      if (this == targetProjection) {
         return point;
      }


      if (_proj4Definition == null) {
         throw new IllegalArgumentException("The receiver doesn't contains a Proj4 definition");
      }

      if (targetProjection._proj4Definition == null) {
         throw new IllegalArgumentException("Destination doesn't contains a Proj4 definition");
      }


      final double[] xB = { point.x() };
      final double[] yB = { point.y() };
      final double[] zB = { point.z() };

      //      synchronized (GProj4Library.MUTEX) {
      final Pointer src = getProj4Pointer();
      final Pointer dst = targetProjection.getProj4Pointer();
      final int errorCode = GProj4Library.pj_transform(src, dst, 1, 1, xB, yB, zB);
      if (errorCode != 0) {
         throw new RuntimeException("GProj4Library.pj_transform() errorCode=" + errorCode + " \""
                                    + GProj4Library.pj_strerrno(errorCode) + "\", point=" + point + ", source=" + this
                                    + ", destination=" + targetProjection);
      }
      //      }

      return new GVector3D(xB[0], yB[0], zB[0]);
   }


   public IVector2<?> transformPoint(final GProjection targetProjection,
                                     final IVector2<?> point) {
      if (this == targetProjection) {
         return point;
      }

      //      return transformTo(targetProjection, new GVector3D(point, 0)).asVector2();

      if (_proj4Definition == null) {
         throw new IllegalArgumentException("The receiver doesn't contains a Proj4 definition");
      }

      if (targetProjection._proj4Definition == null) {
         throw new IllegalArgumentException("Destination doesn't contains a Proj4 definition");
      }


      final double[] xB = { point.x() };
      final double[] yB = { point.y() };

      //      synchronized (GProj4Library.MUTEX) {
      final Pointer src = getProj4Pointer();
      final Pointer dst = targetProjection.getProj4Pointer();
      final int errorCode = GProj4Library.pj_transform(src, dst, 1, 1, xB, yB, null);
      if (errorCode != 0) {
         throw new RuntimeException("GProj4Library.pj_transform() errorCode=" + errorCode + " \""
                                    + GProj4Library.pj_strerrno(errorCode) + "\", point=" + point + ", source=" + this
                                    + ", destination=" + targetProjection);
      }
      //      }

      return new GVector2D(xB[0], yB[0]);
   }


   public boolean isGeodesic() {
      if (_proj4Definition == null) {
         throw new IllegalArgumentException("The receiver doesn't contains a Proj4 definition");
      }

      return GProj4Library.pj_is_geocent(getProj4Pointer());
   }


   public boolean isLatLong() {
      if (_proj4Definition == null) {
         throw new IllegalArgumentException("The receiver doesn't contains a Proj4 definition");
      }

      return GProj4Library.pj_is_latlong(getProj4Pointer());
   }


   public static void main(final String[] args) {
      final GVector3D point = new GVector3D(689523.09, 4278770.23, 0);

      System.out.println("         point=" + point);

      final GProjection sourceProjection = EPSG_23029;
      final GProjection targetProjection = EPSG_4326;
      //final GProjection targetProjection = EPSG_23030;

      final IVector3<?> reprojected = point.reproject(sourceProjection, targetProjection);
      System.out.println("   reprojected=" + reprojected);
      System.out.println("           lat: " + Math.toDegrees(reprojected.y()) + " lon:" + Math.toDegrees(reprojected.x()));

      final IVector3<?> rereprojected = reprojected.reproject(targetProjection, sourceProjection);
      System.out.println("re-reprojected=" + rereprojected);
      System.out.println("delta= " + rereprojected.sub(point));
   }


}
