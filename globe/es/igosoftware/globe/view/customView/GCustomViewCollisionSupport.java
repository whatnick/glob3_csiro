

package es.igosoftware.globe.view.customView;

import gov.nasa.worldwind.geom.Angle;
import gov.nasa.worldwind.geom.Matrix;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.geom.Vec4;
import gov.nasa.worldwind.globes.Globe;
import gov.nasa.worldwind.render.DrawContext;
import gov.nasa.worldwind.util.Logging;
import gov.nasa.worldwind.view.orbit.OrbitView;


public class GCustomViewCollisionSupport {

   private double _collisionThreshold;
   private int    _numIterations;


   public GCustomViewCollisionSupport() {
      setNumIterations(1);
   }


   public double getCollisionThreshold() {
      return _collisionThreshold;
   }


   public void setCollisionThreshold(final double collisionThreshold) {
      if (collisionThreshold < 0) {
         final String message = Logging.getMessage("generic.ArgumentOutOfRange", collisionThreshold);
         Logging.logger().severe(message);
         throw new IllegalArgumentException(message);
      }

      _collisionThreshold = collisionThreshold;
   }


   public int getNumIterations() {
      return _numIterations;
   }


   public void setNumIterations(final int numIterations) {
      if (numIterations < 1) {
         final String message = Logging.getMessage("generic.ArgumentOutOfRange", numIterations);
         Logging.logger().severe(message);
         throw new IllegalArgumentException(message);
      }

      _numIterations = numIterations;
   }


   public boolean isColliding(final OrbitView orbitView,
                              final double nearDistance,
                              final DrawContext dc) {
      if (orbitView == null) {
         final String message = Logging.getMessage("nullValue.OrbitViewIsNull");
         Logging.logger().severe(message);
         throw new IllegalArgumentException(message);
      }
      if (nearDistance < 0) {
         final String message = Logging.getMessage("generic.ArgumentOutOfRange", nearDistance);
         Logging.logger().severe(message);
         throw new IllegalArgumentException(message);
      }
      if (dc == null) {
         final String message = Logging.getMessage("nullValue.DrawContextIsNull");
         Logging.logger().severe(message);
         throw new IllegalArgumentException(message);
      }
      final Globe globe = dc.getGlobe();
      if (globe == null) {
         final String message = Logging.getMessage("nullValue.DrawingContextGlobeIsNull");
         Logging.logger().severe(message);
         throw new IllegalArgumentException(message);
      }

      final Matrix modelviewInv = getModelviewInverse(globe, orbitView.getCenterPosition(), orbitView.getHeading(),
               orbitView.getPitch(), orbitView.getZoom());
      if (modelviewInv != null) {
         // OrbitView is colliding when its eye point is below the collision threshold.
         final double heightAboveSurface = computeViewHeightAboveSurface(dc, modelviewInv, orbitView.getFieldOfView(),
                  orbitView.getViewport(), nearDistance);
         return heightAboveSurface < _collisionThreshold;
      }

      return false;
   }


   public Position computeCenterPositionToResolveCollision(final GCustomView customView,
                                                           final double nearDistance,
                                                           final DrawContext dc) {
      if (customView == null) {
         final String message = Logging.getMessage("nullValue.OrbitViewIsNull");
         Logging.logger().severe(message);
         throw new IllegalArgumentException(message);
      }
      if (nearDistance < 0) {
         final String message = Logging.getMessage("generic.ArgumentOutOfRange", nearDistance);
         Logging.logger().severe(message);
         throw new IllegalArgumentException(message);
      }
      if (dc == null) {
         final String message = Logging.getMessage("nullValue.DrawContextIsNull");
         Logging.logger().severe(message);
         throw new IllegalArgumentException(message);
      }
      final Globe globe = dc.getGlobe();
      if (globe == null) {
         final String message = Logging.getMessage("nullValue.DrawingContextGlobeIsNull");
         Logging.logger().severe(message);
         throw new IllegalArgumentException(message);
      }

      Position newCenter = null;

      for (int i = 0; i < _numIterations; i++) {
         final Matrix modelviewInv = getModelviewInverse(globe, newCenter != null ? newCenter : customView.getCenterPosition(),
                  customView.getHeading(), customView.getPitch(), customView.getZoom());
         if (modelviewInv != null) {
            final double heightAboveSurface = computeViewHeightAboveSurface(dc, modelviewInv, customView.getFieldOfView(),
                     customView.getViewport(), nearDistance);
            final double adjustedHeight = heightAboveSurface - _collisionThreshold;
            if (adjustedHeight < 0) {
               newCenter = new Position(newCenter != null ? newCenter : customView.getCenterPosition(),
                        (newCenter != null ? newCenter.getElevation() : customView.getCenterPosition().getElevation())
                                 - adjustedHeight);
            }
         }
      }

      return newCenter;
   }


   public Angle computePitchToResolveCollision(final GCustomView customView,
                                               final double nearDistance,
                                               final DrawContext dc) {
      if (customView == null) {
         final String message = Logging.getMessage("nullValue.OrbitViewIsNull");
         Logging.logger().severe(message);
         throw new IllegalArgumentException(message);
      }
      if (nearDistance < 0) {
         final String message = Logging.getMessage("generic.ArgumentOutOfRange", nearDistance);
         Logging.logger().severe(message);
         throw new IllegalArgumentException(message);
      }
      if (dc == null) {
         final String message = Logging.getMessage("nullValue.DrawContextIsNull");
         Logging.logger().severe(message);
         throw new IllegalArgumentException(message);
      }
      final Globe globe = dc.getGlobe();
      if (globe == null) {
         final String message = Logging.getMessage("nullValue.DrawingContextGlobeIsNull");
         Logging.logger().severe(message);
         throw new IllegalArgumentException(message);
      }

      Angle newPitch = null;

      for (int i = 0; i < _numIterations; i++) {
         final Matrix modelviewInv = getModelviewInverse(globe, customView.getCenterPosition(), customView.getHeading(),
                  newPitch != null ? newPitch : customView.getPitch(), customView.getZoom());
         if (modelviewInv != null) {
            final double heightAboveSurface = computeViewHeightAboveSurface(dc, modelviewInv, customView.getFieldOfView(),
                     customView.getViewport(), nearDistance);
            final double adjustedHeight = heightAboveSurface - _collisionThreshold;
            if (adjustedHeight < 0) {
               final Vec4 eyePoint = getEyePoint(modelviewInv);
               final Vec4 centerPoint = globe.computePointFromPosition(customView.getCenterPosition());
               if ((eyePoint != null) && (centerPoint != null)) {
                  final Position eyePos = globe.computePositionFromPoint(eyePoint);
                  // Compute the eye point required to resolve the collision.
                  final Vec4 newEyePoint = globe.computePointFromPosition(eyePos.getLatitude(), eyePos.getLongitude(),
                           eyePos.getElevation() - adjustedHeight);
                  // Compute the pitch that corresponds with the elevation of the eye point
                  // (but not necessarily the latitude and longitude).
                  final Vec4 normalAtCenter = globe.computeSurfaceNormalAtPoint(centerPoint);
                  final Vec4 newEye_sub_center = newEyePoint.subtract3(centerPoint).normalize3();
                  final double dot = normalAtCenter.dot3(newEye_sub_center);
                  if ((dot >= -1) || (dot <= 1)) {
                     final double angle = Math.acos(dot);
                     newPitch = Angle.fromRadians(angle);
                  }
               }
            }
         }
      }

      return newPitch;
   }


   private double computeViewHeightAboveSurface(final DrawContext dc,
                                                final Matrix modelviewInv,
                                                final Angle fieldOfView,
                                                final java.awt.Rectangle viewport,
                                                final double nearDistance) {
      double height = Double.POSITIVE_INFINITY;
      if ((dc != null) && (modelviewInv != null) && (fieldOfView != null) && (viewport != null) && (nearDistance >= 0)) {
         final Vec4 eyePoint = getEyePoint(modelviewInv);
         if (eyePoint != null) {
            final double eyeHeight = computePointHeightAboveSurface(dc, eyePoint);
            if (eyeHeight < height) {
               height = eyeHeight;
            }
         }

         final Vec4 nearPoint = getPointOnNearPlane(modelviewInv, fieldOfView, viewport, nearDistance);
         if (nearPoint != null) {
            final double nearHeight = computePointHeightAboveSurface(dc, nearPoint);
            if (nearHeight < height) {
               height = nearHeight;
            }
         }
      }
      return height;
   }


   private double computePointHeightAboveSurface(final DrawContext dc,
                                                 final Vec4 point) {
      double height = Double.POSITIVE_INFINITY;
      if ((dc != null) && (dc.getGlobe() != null) && (point != null)) {
         final Globe globe = dc.getGlobe();
         final Position position = globe.computePositionFromPoint(point);
         Position surfacePosition = null;
         // Look for the surface geometry point at 'position'.
         final Vec4 pointOnGlobe = dc.getPointOnTerrain(position.getLatitude(), position.getLongitude());
         if (pointOnGlobe != null) {
            surfacePosition = globe.computePositionFromPoint(pointOnGlobe);
         }
         // Fallback to using globe elevation values.
         if (surfacePosition == null) {
            surfacePosition = new Position(position, globe.getElevation(position.getLatitude(), position.getLongitude())
                                                     * dc.getVerticalExaggeration());
         }
         height = position.getElevation() - surfacePosition.getElevation();
      }
      return height;
   }


   private Matrix getModelviewInverse(final Globe globe,
                                      final Position centerPosition,
                                      final Angle heading,
                                      final Angle pitch,
                                      final double zoom) {
      if ((globe != null) && (centerPosition != null) && (heading != null) && (pitch != null)) {
         // Use the OrbitViewModel to compute the current modelview matrix.
         final Matrix modelview = GCustomViewInputSupport.computeTransformMatrix(globe, centerPosition, heading, pitch, zoom);
         if (modelview != null) {
            return modelview.getInverse();
         }
      }

      return null;
   }


   private Vec4 getEyePoint(final Matrix modelviewInv) {
      return modelviewInv != null ? Vec4.UNIT_W.transformBy4(modelviewInv) : null;
   }


   private Vec4 getPointOnNearPlane(final Matrix modelviewInv,
                                    final Angle fieldOfView,
                                    final java.awt.Rectangle viewport,
                                    final double nearDistance) {
      if ((modelviewInv != null) && (fieldOfView != null) && (viewport != null) && (nearDistance >= 0)) {
         // If either either the viewport width or height is zero, then fall back to an aspect ratio of 1. 
         // Otherwise, compute the standard aspect ratio.
         final double aspect = ((viewport.getWidth() <= 0) || (viewport.getHeight() <= 0))
                                                                                          ? 1d
                                                                                          : (viewport.getHeight() / viewport.getWidth());
         final double nearClipHeight = 2 * aspect * nearDistance * fieldOfView.tanHalfAngle();
         // Computes the point on the bottom center of the near clip plane.
         final Vec4 nearClipVec = new Vec4(0, -nearClipHeight / 2.0, -nearDistance, 1);
         return nearClipVec.transformBy4(modelviewInv);
      }

      return null;
   }


}
