package es.igosoftware.globe.view;

import es.igosoftware.globe.view.customView.GCustomViewLimits;
import gov.nasa.worldwind.geom.Angle;
import gov.nasa.worldwind.geom.Sector;

public class GPanoramicViewLimits
         extends
            GCustomViewLimits {


   public GPanoramicViewLimits() {
      centerLocationLimits = Sector.FULL_SPHERE;
      minCenterElevation = -Double.MAX_VALUE;
      maxCenterElevation = Double.MAX_VALUE;
      minHeading = Angle.NEG180;
      maxHeading = Angle.POS180;

      //      //      minPitch = Angle.NEG90;
      //      minPitch = Angle.fromDegrees(-85);
      //      //      maxPitch = Angle.POS90;
      //      maxPitch = Angle.fromDegrees(85);

      minPitch = Angle.ZERO;
      //      maxPitch = Angle.POS90;
      maxPitch = Angle.fromDegrees(170);

      minZoom = 0;
      maxZoom = 1;
   }
}
