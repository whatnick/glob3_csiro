package es.igosoftware.globe.view;

import gov.nasa.worldwind.geom.Angle;
import gov.nasa.worldwind.geom.Sector;
import gov.nasa.worldwind.view.orbit.BasicOrbitViewLimits;

public class GBasicOrbitViewLimits
         extends
            BasicOrbitViewLimits {

   //   private Sector _centerLocationLimits;
   //   private double _minCenterElevation;
   //   private double _maxCenterElevation;
   //   private double _minZoom;
   //   private double _maxZoom;


   public GBasicOrbitViewLimits() {
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
      maxPitch = Angle.fromDegrees(89);

      minZoom = 5;
      maxZoom = Double.MAX_VALUE;
   }


}
