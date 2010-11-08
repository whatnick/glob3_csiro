package es.igosoftware.utils;

import es.igosoftware.euclid.bounding.GAxisAlignedBox;
import es.igosoftware.euclid.bounding.GAxisAlignedOrthotope;
import es.igosoftware.euclid.projection.GProjection;
import es.igosoftware.euclid.vector.GVector3D;
import es.igosoftware.euclid.vector.IVector3;
import es.igosoftware.util.GAssert;
import es.igosoftware.util.GMath;
import gov.nasa.worldwind.geom.Angle;
import gov.nasa.worldwind.geom.Cylinder;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.geom.Sector;
import gov.nasa.worldwind.globes.Globe;
import gov.nasa.worldwind.render.DrawContext;

import javax.media.opengl.GL;


public final class GPositionBox {
   public static final GPositionBox                         EMPTY = new GPositionBox(Position.ZERO, Position.ZERO);

   private static final GGlobeCache<GPositionBox, Cylinder> extentsCache;

   static {
      extentsCache = new GGlobeCache<GPositionBox, Cylinder>(new GGlobeCache.Factory<GPositionBox, Cylinder>() {
         @Override
         public Cylinder create(final GPositionBox box,
                                final Globe globe,
                                final double verticalExaggeration) {
            //            return globe.computeBoundingCylinder(verticalExaggeration, box._sector, box._lower.elevation,
            //                     GMath.nextUp(box._upper.elevation));
            return Cylinder.computeVerticalBoundingCylinder(globe, verticalExaggeration, box._sector, box._lower.elevation,
                     GMath.nextUp(box._upper.elevation));
         }
      });
   }


   public static GPositionBox merge(final Iterable<GPositionBox> boxes) {
      double minLatitude = Double.POSITIVE_INFINITY;
      double minLongitude = Double.POSITIVE_INFINITY;
      double minElevation = Double.POSITIVE_INFINITY;

      double maxLatitude = Double.NEGATIVE_INFINITY;
      double maxLongitude = Double.NEGATIVE_INFINITY;
      double maxElevation = Double.NEGATIVE_INFINITY;

      for (final GPositionBox box : boxes) {
         final Position currentLower = box._lower;
         final Position currentUpper = box._upper;

         minLatitude = Math.min(minLatitude, currentLower.latitude.radians);
         minLongitude = Math.min(minLongitude, currentLower.longitude.radians);
         minElevation = Math.min(minElevation, currentLower.elevation);

         maxLatitude = Math.max(maxLatitude, currentUpper.latitude.radians);
         maxLongitude = Math.max(maxLongitude, currentUpper.longitude.radians);
         maxElevation = Math.max(maxElevation, currentUpper.elevation);
      }


      if (minLatitude == Double.POSITIVE_INFINITY) {
         return GPositionBox.EMPTY;
      }

      final Position lower = Position.fromRadians(minLatitude, minLongitude, minElevation);
      final Position upper = Position.fromRadians(maxLatitude, maxLongitude, maxElevation);
      return new GPositionBox(lower, upper);
   }


   public final Position    _lower;
   public final Position    _upper;

   private final Position[] _vertices;
   public final Position    _center;
   public final Sector      _sector;
   //   private final Position   center;
   //   private final Position[] _bottomQuadVertices;
   //   private final Position[] _topQuadVertices;
   //   private final Position[] _middleQuadVertices;

   private final Position[] _bottomQuadVertices;


   public GPositionBox(final GAxisAlignedOrthotope<IVector3<?>, ?> box,
                       final GProjection projection) {
      this(GConverter.toPosition(projection, box._lower), GConverter.toPosition(projection, box._upper));
   }


   public GPositionBox(final Sector sector,
                       final double minElevation,
                       final double maxElevation) {
      _sector = sector;

      final Position lower = new Position(sector.getMinLatitude(), sector.getMinLongitude(), minElevation);
      final Position upper = new Position(sector.getMaxLatitude(), sector.getMaxLongitude(), maxElevation);
      _lower = GWWUtils.min(lower, upper);
      _upper = GWWUtils.max(lower, upper);

      _center = initializeCenter();
      _vertices = initializeVertices();
      _bottomQuadVertices = initializeBottomQuadVertices();
   }


   public GPositionBox(final Position lower,
                       final Position upper) {
      GAssert.notNull(lower, "lower");
      GAssert.notNull(upper, "upper");

      _lower = GWWUtils.min(lower, upper);
      _upper = GWWUtils.max(lower, upper);

      _sector = new Sector(_lower.latitude, _upper.latitude, _lower.longitude, _upper.longitude);

      _center = initializeCenter();
      _vertices = initializeVertices();
      _bottomQuadVertices = initializeBottomQuadVertices();

      //      _topQuadVertices = new Position[] { new Position(_lower.latitude, _lower.longitude, _upper.elevation), //
      //               new Position(_lower.latitude, _upper.longitude, _upper.elevation), //
      //               new Position(_upper.latitude, _upper.longitude, _upper.elevation), // 
      //               new Position(_upper.latitude, _lower.longitude, _upper.elevation) };

      //      final double middleElevation = (_lower.elevation + _upper.elevation) / 2;
      //      _middleQuadVertices = new Position[] { new Position(_lower.latitude, _lower.longitude, middleElevation), //
      //               new Position(_lower.latitude, _upper.longitude, middleElevation), //
      //               new Position(_upper.latitude, _upper.longitude, middleElevation), // 
      //               new Position(_upper.latitude, _lower.longitude, middleElevation) };
   }


   private Position[] initializeBottomQuadVertices() {
      return new Position[] { new Position(_lower.latitude, _lower.longitude, _lower.elevation), //
               new Position(_lower.latitude, _upper.longitude, _lower.elevation), //
               new Position(_upper.latitude, _upper.longitude, _lower.elevation), // 
               new Position(_upper.latitude, _lower.longitude, _lower.elevation) };
   }


   private Position[] initializeVertices() {
      return new Position[] { //
               new Position(_lower.latitude, _lower.longitude, _lower.elevation), //
               new Position(_lower.latitude, _lower.longitude, _upper.elevation), //
               new Position(_lower.latitude, _upper.longitude, _upper.elevation), //
               new Position(_lower.latitude, _upper.longitude, _lower.elevation), //

               new Position(_upper.latitude, _lower.longitude, _lower.elevation), //
               new Position(_upper.latitude, _lower.longitude, _upper.elevation), //
               new Position(_upper.latitude, _upper.longitude, _upper.elevation),
               new Position(_upper.latitude, _upper.longitude, _lower.elevation) };
   }


   private Position initializeCenter() {
      final Angle centerLatitude = Angle.average(_lower.latitude, _upper.latitude);
      final Angle centerLongitude = Angle.average(_lower.longitude, _upper.longitude);
      final double centerElevation = (_lower.elevation + _upper.elevation) / 2;
      return new Position(centerLatitude, centerLongitude, centerElevation);
   }


   //   private GPositionBox split(final int key) {
   //      final int xKey = key & 1;
   //      final int yKey = key & 2;
   //      final int zKey = key & 4;
   //
   //      final double lowerLatitude = (xKey == 0) ? _lower.latitude.radians : _center.latitude.radians;
   //      final double lowerLongitude = (yKey == 0) ? _lower.longitude.radians : _center.longitude.radians;
   //      final double lowerElevation = (zKey == 0) ? _lower.elevation : _center.elevation;
   //
   //      double upperLatitude = (xKey == 0) ? _center.latitude.radians : _upper.latitude.radians;
   //      double upperLongitude = (yKey == 0) ? _center.longitude.radians : _upper.longitude.radians;
   //      double upperElevation = (zKey == 0) ? _center.elevation : _upper.elevation;
   //
   //      if (upperLatitude < _upper.latitude.radians) {
   //         upperLatitude = GMath.previousDown(upperLatitude);
   //      }
   //      if (upperLongitude < _upper.longitude.radians) {
   //         upperLongitude = GMath.previousDown(upperLongitude);
   //      }
   //      if (upperElevation < _upper.elevation) {
   //         upperElevation = GMath.previousDown(upperElevation);
   //      }
   //
   //      return new GPositionBox(Position.fromRadians(lowerLatitude, lowerLongitude, lowerElevation), Position.fromRadians(
   //               upperLatitude, upperLongitude, upperElevation));
   //   }
   //
   //
   //   public GPositionBox[] subdivide() {
   //      final GPositionBox[] result = new GPositionBox[8];
   //
   //      for (int i = 0; i < 8; i++) {
   //         result[i] = split(i);
   //      }
   //
   //      return result;
   //   }


   //      public GPositionBox[] subdivideOverLargestDimension(final Globe globe) {
   //         final GPositionBox[] result = new GPositionBox[2];
   //   
   //         final double equatorialRadius = globe.getEquatorialRadius();
   //         final double polarRadius = globe.getPolarRadius();
   //   
   //         final Angle midLat = Angle.average(_lower.latitude, _upper.latitude);
   //         final Angle midLon = Angle.average(_lower.longitude, _upper.longitude);
   //   
   //         final double latitudeDistance = LatLon.ellipsoidalDistance(//
   //                  new LatLon(_lower.latitude, midLon), //
   //                  new LatLon(_upper.latitude, midLon), //
   //                  equatorialRadius, //
   //                  polarRadius);
   //   
   //         final double longitudeDistance = LatLon.ellipsoidalDistance(//
   //                  new LatLon(midLat, _lower.longitude), //
   //                  new LatLon(midLat, _upper.longitude), //
   //                  equatorialRadius, //
   //                  polarRadius);
   //   
   //         final double elevationDistance = _upper.elevation - _lower.elevation;
   //   
   //         if ((latitudeDistance > longitudeDistance) && (latitudeDistance > elevationDistance)) {
   //            // split by latitude
   //            result[0] = new GPositionBox(_lower, new Position(Angle.fromRadians(GMath.previousDown(midLat.radians)),
   //                     _upper.latitude, _upper.elevation));
   //            result[1] = new GPositionBox(new Position(midLat, _lower.latitude, _lower.elevation), _upper);
   //         }
   //         else if ((longitudeDistance > latitudeDistance) && (longitudeDistance > elevationDistance)) {
   //            // split by longitude
   //            result[0] = new GPositionBox(_lower, new Position(_lower.latitude,
   //                     Angle.fromRadians(GMath.previousDown(midLon.radians)), _upper.elevation));
   //            result[1] = new GPositionBox(new Position(_lower.latitude, midLon, _lower.elevation), _upper);
   //         }
   //         else {
   //            // split by _elevation
   //            final double midEletation = (_lower.elevation + _upper.elevation) / 2;
   //            result[0] = new GPositionBox(_lower, new Position(midLat, midLon, GMath.previousDown(midEletation)));
   //            result[1] = new GPositionBox(new Position(midLat, midLon, midEletation), _upper);
   //         }
   //   
   //         return result;
   //      }


   public GPositionBox[] subdivideOverLatitudeAndLongitude() {
      final Angle midLat = Angle.average(_lower.latitude, _upper.latitude);
      final Angle midLon = Angle.average(_lower.longitude, _upper.longitude);

      final double minEle = _lower.elevation;
      final double maxEle = _upper.elevation;

      //      if (upperLatitude < _upper.latitude.radians) {
      //         upperLatitude = GMath.previousDown(upperLatitude);
      //      }
      //      if (upperLongitude < _upper.longitude.radians) {
      //         upperLongitude = GMath.previousDown(upperLongitude);
      //      }
      //      if (upperElevation < _upper.elevation) {
      //         upperElevation = GMath.previousDown(upperElevation);
      //      }

      return new GPositionBox[] {
               new GPositionBox(new Position(_lower.latitude, midLon, minEle), new Position(midLat, _lower.longitude, maxEle)), //
               new GPositionBox(new Position(_lower.latitude, _upper.longitude, minEle), new Position(midLat, midLon, maxEle)), //
               new GPositionBox(new Position(midLat, midLon, minEle), new Position(_upper.latitude, _lower.longitude, maxEle)), //
               new GPositionBox(new Position(midLat, _upper.longitude, minEle), new Position(_upper.latitude, midLon, maxEle)) };
   }


   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + _lower.hashCode();
      result = prime * result + _upper.hashCode();
      return result;
   }


   @Override
   public boolean equals(final Object obj) {
      if (this == obj) {
         return true;
      }

      if (obj == null) {
         return false;
      }

      if (getClass() != obj.getClass()) {
         return false;
      }

      final GPositionBox other = (GPositionBox) obj;

      return _lower.equals(other._lower);
   }


   @Override
   public String toString() {
      //return "GPositionBox [" + _lower + " -> " + _upper + "]";
      return "[" + _lower + " -> " + _upper + "]";
   }


   public Position[] getVertices() {
      return _vertices;
   }


   public Cylinder getExtent(final Globe globe,
                             final double verticalExaggeration) {
      //return globe.computeBoundingCylinder(verticalExaggeration, _sector, _lower.elevation, _upper.elevation);
      return extentsCache.get(this, globe, verticalExaggeration);
   }


   public void render(final DrawContext dc) {

      final Globe globe = dc.getGlobe();
      final double verticalExaggeration = dc.getVerticalExaggeration();

      final Cylinder extent = getExtent(globe, verticalExaggeration);
      extent.render(dc);

      final GL gl = dc.getGL();
      GWWUtils.pushOffset(gl);
      GWWUtils.renderQuad(dc, _bottomQuadVertices, 1, 1, 0);
      GWWUtils.popOffset(gl);
      // GWWUtils.renderQuad(dc, _topQuadVertices, 1, 1, 0);
      // GWWUtils.renderQuad(dc, _middleQuadVertices, 1, 1, 0);
   }


   public Position getCenter() {
      return _center;
   }


   public boolean contains(final Position position) {
      return GMath.between(position.latitude.degrees, _lower.latitude.degrees, _upper.latitude.degrees)
             && GMath.between(position.longitude.degrees, _lower.longitude.degrees, _upper.longitude.degrees)
             && GMath.between(position.elevation, _lower.elevation, _upper.elevation);
   }


   public GAxisAlignedBox asAxisAlignedBox() {
      final GVector3D lower = new GVector3D(_lower.longitude.radians, _lower.latitude.radians, _lower.elevation);
      final GVector3D upper = new GVector3D(_upper.longitude.radians, _upper.latitude.radians, _upper.elevation);

      return new GAxisAlignedBox(lower, upper);
   }


   public Sector asSector() {
      return new Sector(_lower.latitude, _upper.latitude, _lower.longitude, _upper.longitude);
   }


   //   public static void main(final String[] args) {
   //      final GPositionBox box = new GPositionBox(Position.ZERO, Position.fromDegrees(45, 90, 100));
   //
   //      System.out.println(box);
   //      //for (final GPositionBox subdivision : box.subdivideOverLargestDimension(new Earth())) {
   //      for (final GPositionBox subdivision : box.subdivideOverLatitudeAndLongitude()) {
   //         System.out.println(" " + subdivision);
   //      }
   //   }

}
