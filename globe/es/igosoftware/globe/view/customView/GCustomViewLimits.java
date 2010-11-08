package es.igosoftware.globe.view.customView;

import gov.nasa.worldwind.geom.Angle;
import gov.nasa.worldwind.geom.LatLon;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.geom.Sector;
import gov.nasa.worldwind.util.Logging;
import gov.nasa.worldwind.util.RestorableSupport;
import gov.nasa.worldwind.view.BasicViewPropertyLimits;
import gov.nasa.worldwind.view.orbit.OrbitView;
import gov.nasa.worldwind.view.orbit.OrbitViewLimits;

public class GCustomViewLimits
         extends
            BasicViewPropertyLimits
         implements
            OrbitViewLimits {

   protected Sector centerLocationLimits;
   protected double minCenterElevation;
   protected double maxCenterElevation;
   protected double minZoom;
   protected double maxZoom;


   public GCustomViewLimits() {
      this.centerLocationLimits = Sector.FULL_SPHERE;
      this.minCenterElevation = -Double.MAX_VALUE;
      this.maxCenterElevation = Double.MAX_VALUE;
      this.minHeading = Angle.NEG180;
      this.maxHeading = Angle.POS180;
      this.minPitch = Angle.ZERO;
      this.maxPitch = Angle.POS90;
      this.minZoom = 0;
      this.maxZoom = Double.MAX_VALUE;
   }


   @Override
   public double[] getCenterElevationLimits() {
      return new double[] { this.minCenterElevation, this.maxCenterElevation };
   }


   @Override
   public Sector getCenterLocationLimits() {
      return this.centerLocationLimits;
   }


   @Override
   public double[] getZoomLimits() {
      return new double[] { this.minZoom, this.maxZoom };
   }


   @Override
   public void setCenterElevationLimits(final double minValue,
                                        final double maxValue) {
      this.minCenterElevation = minValue;
      this.maxCenterElevation = maxValue;

   }


   @Override
   public void setCenterLocationLimits(final Sector sector) {
      if (sector == null) {
         final String message = Logging.getMessage("nullValue.SectorIsNull");
         Logging.logger().severe(message);
         throw new IllegalArgumentException(message);
      }

      this.centerLocationLimits = sector;

   }


   @Override
   public void setZoomLimits(final double minValue,
                             final double maxValue) {
      this.minZoom = minValue;
      this.maxZoom = maxValue;

   }


   public static void applyLimits(final OrbitView view,
                                  final OrbitViewLimits viewLimits) {
      if (view == null) {
         final String message = Logging.getMessage("nullValue.ViewIsNull");
         Logging.logger().severe(message);
         throw new IllegalArgumentException(message);
      }
      if (viewLimits == null) {
         final String message = Logging.getMessage("nullValue.ViewLimitsIsNull");
         Logging.logger().severe(message);
         throw new IllegalArgumentException(message);
      }

      view.setCenterPosition(limitCenterPosition(view.getCenterPosition(), viewLimits));
      view.setHeading(limitHeading(view.getHeading(), viewLimits));
      view.setPitch(limitPitch(view.getPitch(), viewLimits));
      view.setZoom(limitZoom(view.getZoom(), viewLimits));
   }


   public static Position limitCenterPosition(final Position position,
                                              final OrbitViewLimits viewLimits) {
      if (position == null) {
         final String message = Logging.getMessage("nullValue.PositionIsNull");
         Logging.logger().severe(message);
         throw new IllegalArgumentException(message);
      }
      if (viewLimits == null) {
         final String message = Logging.getMessage("nullValue.ViewLimitsIsNull");
         Logging.logger().severe(message);
         throw new IllegalArgumentException(message);
      }

      return new Position(limitCenterLocation(position.getLatitude(), position.getLongitude(), viewLimits), limitCenterElevation(
               position.getElevation(), viewLimits));

   }


   public static LatLon limitCenterLocation(final Angle latitude,
                                            final Angle longitude,
                                            final OrbitViewLimits viewLimits) {
      if ((latitude == null) || (longitude == null)) {
         final String message = Logging.getMessage("nullValue.LatitudeOrLongitudeIsNull");
         Logging.logger().severe(message);
         throw new IllegalArgumentException(message);
      }
      if (viewLimits == null) {
         final String message = Logging.getMessage("nullValue.ViewLimitsIsNull");
         Logging.logger().severe(message);
         throw new IllegalArgumentException(message);
      }

      final Sector limits = viewLimits.getCenterLocationLimits();
      Angle newLatitude = latitude;
      Angle newLongitude = longitude;

      if (latitude.compareTo(limits.getMinLatitude()) < 0) {
         newLatitude = limits.getMinLatitude();
      }
      else if (latitude.compareTo(limits.getMaxLatitude()) > 0) {
         newLatitude = limits.getMaxLatitude();
      }

      if (longitude.compareTo(limits.getMinLongitude()) < 0) {
         newLongitude = limits.getMinLongitude();
      }
      else if (longitude.compareTo(limits.getMaxLongitude()) > 0) {
         newLongitude = limits.getMaxLongitude();
      }

      return new LatLon(newLatitude, newLongitude);
   }


   public static double limitCenterElevation(final double value,
                                             final OrbitViewLimits viewLimits) {
      if (viewLimits == null) {
         final String message = Logging.getMessage("nullValue.ViewLimitsIsNull");
         Logging.logger().severe(message);
         throw new IllegalArgumentException(message);
      }

      final double[] limits = viewLimits.getCenterElevationLimits();
      double newValue = value;

      if (value < limits[0]) {
         newValue = limits[0];
      }
      else if (value > limits[1]) {
         newValue = limits[1];
      }

      return newValue;
   }


   public static double limitZoom(final double value,
                                  final OrbitViewLimits viewLimits) {
      if (viewLimits == null) {
         final String message = Logging.getMessage("nullValue.ViewLimitsIsNull");
         Logging.logger().severe(message);
         throw new IllegalArgumentException(message);
      }

      final double[] limits = viewLimits.getZoomLimits();
      double newValue = value;

      if (value < limits[0]) {
         newValue = limits[0];
      }
      else if (value > limits[1]) {
         newValue = limits[1];
      }

      return newValue;
   }


   //**************************************************************//
   //******************** Restorable State  ***********************//
   //**************************************************************//

   @Override
   public void getRestorableState(final RestorableSupport rs,
                                  final RestorableSupport.StateObject context) {
      super.getRestorableState(rs, context);

      rs.addStateValueAsSector(context, "centerLocationLimits", this.centerLocationLimits);
      rs.addStateValueAsDouble(context, "minCenterElevation", this.minCenterElevation);
      rs.addStateValueAsDouble(context, "maxCenterElevation", this.maxCenterElevation);
      rs.addStateValueAsDouble(context, "minZoom", this.minZoom);
      rs.addStateValueAsDouble(context, "maxZoom", this.maxZoom);
   }


   @Override
   public void restoreState(final RestorableSupport rs,
                            final RestorableSupport.StateObject context) {
      super.restoreState(rs, context);

      final Sector sector = rs.getStateValueAsSector(context, "centerLocationLimits");
      if (sector != null) {
         this.setCenterLocationLimits(sector);
      }

      // Min and max center elevation.
      double[] minAndMaxValue = this.getCenterElevationLimits();
      Double min = rs.getStateValueAsDouble(context, "minCenterElevation");
      if (min != null) {
         minAndMaxValue[0] = min;
      }

      Double max = rs.getStateValueAsDouble(context, "maxCenterElevation");
      if (max != null) {
         minAndMaxValue[1] = max;
      }

      if ((min != null) || (max != null)) {
         this.setCenterElevationLimits(minAndMaxValue[0], minAndMaxValue[1]);
      }

      // Min and max zoom value.        
      minAndMaxValue = this.getZoomLimits();
      min = rs.getStateValueAsDouble(context, "minZoom");
      if (min != null) {
         minAndMaxValue[0] = min;
      }

      max = rs.getStateValueAsDouble(context, "maxZoom");
      if (max != null) {
         minAndMaxValue[1] = max;
      }

      if ((min != null) || (max != null)) {
         this.setZoomLimits(minAndMaxValue[0], minAndMaxValue[1]);
      }
   }

}
