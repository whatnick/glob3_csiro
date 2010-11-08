package es.igosoftware.globe;

import gov.nasa.worldwind.geom.Angle;
import gov.nasa.worldwind.geom.Position;

public class GCameraState {

   private final Position _eyePosition;
   private final Angle    _fov;


   private final Position _centerPosition;
   private final double   _zoom;


   //   private final double   _elevation;
   private final Angle    _pitch;
   private final Angle    _heading;


   //   public GCameraState(final Position eyePosition,
   //                       final double elevation,
   //                       final Angle fov,
   //                       final Angle pitch,
   //                       final Angle heading) {
   //      _eyePosition = eyePosition;
   //      _elevation = elevation;
   //      _fov = fov;
   //      _pitch = pitch;
   //      _heading = heading;
   //   }


   public GCameraState(final Position eyePosition,
                       final Position centerPosition,
                       final double zoom,
                       final Angle fov,
                       final Angle heading,
                       final Angle pitch) {

      _eyePosition = eyePosition;
      _centerPosition = centerPosition;
      _zoom = zoom;
      _fov = fov;
      _heading = heading;
      _pitch = pitch;

   }


   public Position getEyePosition() {
      return _eyePosition;
   }


   public Angle getFov() {
      return _fov;
   }


   public Position getCenterPosition() {
      return _centerPosition;
   }


   public double getZoom() {
      return _zoom;
   }


   //   public double getElevation() {
   //      return _elevation;
   //   }
   //
   //
   public Angle getPitch() {
      return _pitch;
   }


   //
   //
   public Angle getHeading() {
      return _heading;
   }

}
