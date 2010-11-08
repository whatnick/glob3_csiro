package es.igosoftware.globe.view;

import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.util.Logging;
import gov.nasa.worldwind.view.ViewUtil;
import gov.nasa.worldwind.view.orbit.BasicOrbitView;
import gov.nasa.worldwind.view.orbit.BasicOrbitViewLimits;


public class GBasicOrbitView
         extends
            BasicOrbitView {


   @Override
   protected double computeNearDistance(final Position eyePosition1) {
      double near = 0;

      if ((eyePosition1 != null) && (dc != null)) {
         final double elevation = ViewUtil.computeElevationAboveSurface(dc, eyePosition1);
         final double tanHalfFov = fieldOfView.tanHalfAngle();
         near = elevation / (2 * Math.sqrt(2 * tanHalfFov * tanHalfFov + 1));
      }

      return (near < 0.001) ? 0.001 : near / 4;
   }


   @Override
   public void setCenterPosition(final Position center1) {
      if (center1 == null) {
         final String message = Logging.getMessage("nullValue.PositionIsNull");
         Logging.logger().severe(message);
         throw new IllegalArgumentException(message);
      }
      if ((center1.getLatitude().degrees < -90) || (center1.getLatitude().degrees > 90)) {
         final String message = Logging.getMessage("generic.LatitudeOutOfRange", center1.getLatitude());
         Logging.logger().severe(message);
         throw new IllegalArgumentException(message);
      }

      this.center = normalizedCenterPosition(center1);
      this.center = BasicOrbitViewLimits.limitCenterPosition(this.center, this.getOrbitViewLimits());

   }


}
