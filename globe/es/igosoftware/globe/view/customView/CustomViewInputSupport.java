package es.igosoftware.globe.view.customView;

import gov.nasa.worldwind.geom.Angle;
import gov.nasa.worldwind.geom.Intersection;
import gov.nasa.worldwind.geom.Line;
import gov.nasa.worldwind.geom.Matrix;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.geom.Vec4;
import gov.nasa.worldwind.globes.Globe;
import gov.nasa.worldwind.terrain.SectorGeometryList;
import gov.nasa.worldwind.util.Logging;
import gov.nasa.worldwind.view.ViewUtil;


public class CustomViewInputSupport {

   protected static class CustomViewState {
      private final Position _center;
      private final Angle    _heading;
      private final Angle    _pitch;
      private final double   _zoom;


      public CustomViewState(final Position center,
                             final Angle heading,
                             final Angle pitch,
                             final double zoom) {
         if (center == null) {
            final String message = Logging.getMessage("nullValue.CenterIsNull");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
         }
         if (heading == null) {
            final String message = Logging.getMessage("nullValue.HeadingIsNull");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
         }
         if (pitch == null) {
            final String message = Logging.getMessage("nullValue.PitchIsNull");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
         }

         this._center = center;
         this._heading = heading;
         this._pitch = pitch;
         this._zoom = zoom;
      }


      public Position getCenterPosition() {
         return this._center;
      }


      public Angle getHeading() {
         return this._heading;
      }


      public Angle getPitch() {
         return this._pitch;
      }


      public double getZoom() {
         return this._zoom;
      }
   }


   public CustomViewInputSupport() {}


   public static Matrix computeTransformMatrix(final Globe globe,
                                               final Position center,
                                               final Angle heading,
                                               final Angle pitch,
                                               final double zoom) {
      if (globe == null) {
         final String message = Logging.getMessage("nullValue.GlobeIsNull");
         Logging.logger().severe(message);
         throw new IllegalArgumentException(message);
      }
      if (center == null) {
         final String message = Logging.getMessage("nullValue.CenterIsNull");
         Logging.logger().severe(message);
         throw new IllegalArgumentException(message);
      }
      if (heading == null) {
         final String message = Logging.getMessage("nullValue.HeadingIsNull");
         Logging.logger().severe(message);
         throw new IllegalArgumentException(message);
      }
      if (pitch == null) {
         final String message = Logging.getMessage("nullValue.PitchIsNull");
         Logging.logger().severe(message);
         throw new IllegalArgumentException(message);
      }

      // Construct the model-view transform matrix for the specified coordinates.
      // Because this is a model-view transform, matrices are applied in reverse order.
      Matrix transform;
      // Zoom, heading, pitch.
      transform = CustomViewInputSupport.computeHeadingPitchZoomTransform(heading, pitch, zoom);
      // Center position.
      transform = transform.multiply(CustomViewInputSupport.computeCenterTransform(globe, center));

      return transform;
   }


   public static CustomViewState computeCustomViewState(final Globe globe,
                                                        final Vec4 eyePoint,
                                                        final Vec4 centerPoint,
                                                        final Vec4 up) {
      if (globe == null) {
         final String message = Logging.getMessage("nullValue.GlobeIsNull");
         Logging.logger().severe(message);
         throw new IllegalArgumentException(message);
      }
      if (eyePoint == null) {
         final String message = "nullValue.EyePointIsNull";
         Logging.logger().severe(message);
         throw new IllegalArgumentException(message);
      }
      if (centerPoint == null) {
         final String message = "nullValue.CenterPointIsNull";
         Logging.logger().severe(message);
         throw new IllegalArgumentException(message);
      }
      if (up == null) {
         final String message = "nullValue.UpIsNull";
         Logging.logger().severe(message);
         throw new IllegalArgumentException(message);
      }

      final Matrix modelview = Matrix.fromViewLookAt(eyePoint, centerPoint, up);
      return CustomViewInputSupport.computeCustomViewState(globe, modelview, centerPoint);
   }


   public static CustomViewState computeCustomViewState(final Globe globe,
                                                        final Matrix modelTransform,
                                                        final Vec4 centerPoint) {
      if (globe == null) {
         final String message = Logging.getMessage("nullValue.GlobeIsNull");
         Logging.logger().severe(message);
         throw new IllegalArgumentException(message);
      }
      if (modelTransform == null) {
         final String message = "nullValue.ModelTransformIsNull";
         Logging.logger().severe(message);
         throw new IllegalArgumentException(message);
      }
      if (centerPoint == null) {
         final String message = "nullValue.CenterPointIsNull";
         Logging.logger().severe(message);
         throw new IllegalArgumentException(message);
      }

      // Compute the center position.
      final Position centerPos = globe.computePositionFromPoint(centerPoint);
      // Compute the center position transform.
      final Matrix centerTransform = CustomViewInputSupport.computeCenterTransform(globe, centerPos);
      final Matrix centerTransformInv = centerTransform.getInverse();
      if (centerTransformInv == null) {
         final String message = Logging.getMessage("generic.NoninvertibleMatrix");
         Logging.logger().severe(message);
         throw new IllegalStateException(message);
      }

      // Compute the heading-pitch-zoom transform.
      final Matrix hpzTransform = modelTransform.multiply(centerTransformInv);
      // Extract the heading, pitch, and zoom values from the transform.
      final Angle heading = ViewUtil.computeHeading(hpzTransform);
      final Angle pitch = ViewUtil.computePitch(hpzTransform);
      final double zoom = CustomViewInputSupport.computeZoom(hpzTransform);
      if ((heading == null) || (pitch == null)) {
         return null;
      }

      return new CustomViewState(centerPos, heading, pitch, zoom);
   }


   protected static Matrix computeCenterTransform(final Globe globe,
                                                  final Position center) {
      if (globe == null) {
         final String message = Logging.getMessage("nullValue.GlobeIsNull");
         Logging.logger().severe(message);
         throw new IllegalArgumentException(message);
      }
      if (center == null) {
         final String message = Logging.getMessage("nullValue.CenterIsNull");
         Logging.logger().severe(message);
         throw new IllegalArgumentException(message);
      }

      // The view eye position will be the same as the center position.
      // This is only the case without any zoom, heading, and pitch.
      final Vec4 eyePoint = globe.computePointFromPosition(center);
      // The view forward direction will be colinear with the
      // geoid surface normal at the center position.
      final Vec4 normal = globe.computeSurfaceNormalAtLocation(center.getLatitude(), center.getLongitude());
      final Vec4 lookAtPoint = eyePoint.subtract3(normal);
      // The up direction will be pointing towards the north pole.
      final Vec4 north = globe.computeNorthPointingTangentAtLocation(center.getLatitude(), center.getLongitude());
      // Creates a viewing matrix looking from eyePoint towards lookAtPoint,
      // with the given up direction. The forward, right, and up vectors
      // contained in the matrix are guaranteed to be orthogonal. This means
      // that the Matrix's up may not be equivalent to the specified up vector
      // here (though it will point in the same general direction).
      // In this case, the forward direction would not be affected.
      return Matrix.fromViewLookAt(eyePoint, lookAtPoint, north);
   }


   protected static Matrix computeHeadingPitchZoomTransform(final Angle heading,
                                                            final Angle pitch,
                                                            final double zoom) {
      if (heading == null) {
         final String message = Logging.getMessage("nullValue.HeadingIsNull");
         Logging.logger().severe(message);
         throw new IllegalArgumentException(message);
      }
      if (pitch == null) {
         final String message = Logging.getMessage("nullValue.PitchIsNull");
         Logging.logger().severe(message);
         throw new IllegalArgumentException(message);
      }

      Matrix transform;
      // Zoom.
      transform = Matrix.fromTranslation(0, 0, -zoom);
      // Pitch is treated clockwise as rotation about the X-axis. We flip the pitch value so that a positive
      // rotation produces a clockwise rotation (when facing the axis).
      transform = transform.multiply(Matrix.fromRotationX(pitch.multiply(-1.0)));
      // Heading.
      transform = transform.multiply(Matrix.fromRotationZ(heading));
      return transform;
   }


   protected static double computeZoom(final Matrix headingPitchZoomTransform) {
      if (headingPitchZoomTransform == null) {
         final String message = "nullValue.HeadingPitchZoomTransformTransformIsNull";
         Logging.logger().severe(message);
         throw new IllegalArgumentException(message);
      }

      final Vec4 v = headingPitchZoomTransform.getTranslation();
      return v != null ? v.getLength3() : 0.0;
   }


   public static CustomViewState getSurfaceIntersection(final Globe globe,
                                                        final SectorGeometryList terrain,
                                                        final Position centerPosition,
                                                        final Angle heading,
                                                        final Angle pitch,
                                                        final double zoom) {
      if (globe != null) {
         final Matrix modelview = CustomViewInputSupport.computeTransformMatrix(globe, centerPosition, heading, pitch, zoom);
         if (modelview != null) {
            final Matrix modelviewInv = modelview.getInverse();
            if (modelviewInv != null) {
               final Vec4 eyePoint = Vec4.UNIT_W.transformBy4(modelviewInv);
               final Vec4 centerPoint = globe.computePointFromPosition(centerPosition);
               final Vec4 eyeToCenter = eyePoint.subtract3(centerPoint);
               final Intersection[] intersections = terrain.intersect(new Line(eyePoint, eyeToCenter.normalize3().multiply3(-1)));
               if ((intersections != null) && (intersections.length >= 0)) {
                  final Position newCenter = globe.computePositionFromPoint(intersections[0].getIntersectionPoint());
                  return (new CustomViewState(newCenter, heading, pitch, zoom));
               }
            }
         }
      }
      return null;
   }

}
