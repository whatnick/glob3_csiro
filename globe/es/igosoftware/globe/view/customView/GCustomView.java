/*

 IGO Software SL  -  info@igosoftware.es

 http://www.glob3.org

-------------------------------------------------------------------------------
 Copyright (c) 2010, IGO Software SL
 All rights reserved.

 Redistribution and use in source and binary forms, with or without
 modification, are permitted provided that the following conditions are met:
     * Redistributions of source code must retain the above copyright
       notice, this list of conditions and the following disclaimer.
     * Redistributions in binary form must reproduce the above copyright
       notice, this list of conditions and the following disclaimer in the
       documentation and/or other materials provided with the distribution.
     * Neither the name of the IGO Software SL nor the
       names of its contributors may be used to endorse or promote products
       derived from this software without specific prior written permission.

 THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 DISCLAIMED. IN NO EVENT SHALL IGO Software SL BE LIABLE FOR ANY
 DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
-------------------------------------------------------------------------------

*/


package es.igosoftware.globe.view.customView;

import es.igosoftware.globe.GCameraState;
import es.igosoftware.globe.view.GInputState;
import es.igosoftware.panoramic.GPanoramic;
import es.igosoftware.util.GMath;
import gov.nasa.worldwind.Configuration;
import gov.nasa.worldwind.View;
import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.geom.Angle;
import gov.nasa.worldwind.geom.Frustum;
import gov.nasa.worldwind.geom.Intersection;
import gov.nasa.worldwind.geom.Line;
import gov.nasa.worldwind.geom.Matrix;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.geom.Vec4;
import gov.nasa.worldwind.render.DrawContext;
import gov.nasa.worldwind.util.Logging;
import gov.nasa.worldwind.util.RestorableSupport;
import gov.nasa.worldwind.view.BasicView;
import gov.nasa.worldwind.view.BasicViewPropertyLimits;
import gov.nasa.worldwind.view.ViewUtil;
import gov.nasa.worldwind.view.orbit.OrbitView;
import gov.nasa.worldwind.view.orbit.OrbitViewLimits;

import javax.media.opengl.GL;


public class GCustomView
         extends
            BasicView
         implements
            OrbitView {

   protected Position                          _center           = Position.ZERO;
   protected double                            _zoom;
   private boolean                             _viewOutOfFocus;
   // Stateless helper classes.
   protected final GCustomViewCollisionSupport _collisionSupport = new GCustomViewCollisionSupport();

   private GInputState                         _inputState;
   protected GCameraState                      _savedCameraState = null;

   private GPanoramic                          _panoramica       = null;


   /**
    * Custom View Class. So far it is mostly a copy of NASA's BasicOrbitView Class with some custom tweaks added
    */
   public GCustomView() {

      viewInputHandler = new GCustomViewInputHandler();
      viewLimits = new GCustomViewLimits();

      _inputState = GInputState.ORBIT;

      _collisionSupport.setCollisionThreshold(COLLISION_THRESHOLD);
      _collisionSupport.setNumIterations(COLLISION_NUM_ITERATIONS);
      getViewInputHandler().setStopOnFocusLost(false);
      loadConfigurationValues();
   }


   public void enterPanoramic(final GPanoramic panoramic) {
      _panoramica = panoramic;
      setInputState(GInputState.PANORAMICS);
   }


   public void exitPanoramic(final GPanoramic panoramic) {

      if (panoramic != _panoramica) {
         System.out.println("This is not the same panoramic that you entered...");
      }
      setInputState(GInputState.ORBIT);
      _panoramica = null;
   }


   public GPanoramic getPanoramic() {
      return _panoramica;
   }


   public void setInputState(final GInputState state) {
      _inputState = state;
   }


   public GInputState getInputState() {
      return _inputState;
   }


   /**
    * The initial values to be loaded at startup (loaded from config-file)
    */
   private void loadConfigurationValues() {
      final Double initLat = Configuration.getDoubleValue(AVKey.INITIAL_LATITUDE);
      final Double initLon = Configuration.getDoubleValue(AVKey.INITIAL_LONGITUDE);
      final double initElev = _center.getElevation();
      // Set center latitude and longitude. Do not change center elevation.
      if ((initLat != null) && (initLon != null)) {
         setCenterPosition(Position.fromDegrees(initLat, initLon, initElev));
      }
      else if (initLat != null) {
         setCenterPosition(Position.fromDegrees(initLat, _center.getLongitude().degrees, initElev));
      }
      else if (initLon != null) {
         setCenterPosition(Position.fromDegrees(_center.getLatitude().degrees, initLon, initElev));
      }

      final Double initHeading = Configuration.getDoubleValue(AVKey.INITIAL_HEADING);
      if (initHeading != null) {
         setHeading(Angle.fromDegrees(initHeading));
      }

      final Double initPitch = Configuration.getDoubleValue(AVKey.INITIAL_PITCH);
      if (initPitch != null) {
         setPitch(Angle.fromDegrees(initPitch));
      }

      final Double initAltitude = Configuration.getDoubleValue(AVKey.INITIAL_ALTITUDE);
      if (initAltitude != null) {
         setZoom(initAltitude);
      }

      final Double initFov = Configuration.getDoubleValue(AVKey.FOV);
      if (initFov != null) {
         setFieldOfView(Angle.fromDegrees(initFov));
      }

      setViewOutOfFocus(true);
   }


   protected void flagHadCollisions() {
      if (_inputState.isDetectCollisions()) {
         hadCollisions = false;
      }

      hadCollisions = true;
   }


   /**
    * Alerts the GCustomView that the view requires the point of rotation for heading and pitch changes to be' recalculate.
    * 
    * @param b
    *           true if the point of rotation needs recalculation, false if it does not.
    */
   public void setViewOutOfFocus(final boolean b) {
      _viewOutOfFocus = b;
   }


   /**
    * Copies the Orientation from a given <code>View</code> and applies it to this GCustomView
    */
   @Override
   public void copyViewState(final View view) {
      globe = view.getGlobe();
      final Vec4 center = view.getCenterPoint();
      if (center == null) {
         final String message = Logging.getMessage("nullValue.PositionIsNull");
         Logging.logger().severe(message);
         throw new IllegalArgumentException(message);
      }
      setOrientation(view.getEyePosition(), globe.computePositionFromPoint(center));
   }


   /**
    * return the centerPoint (as a <code>Position</code>)
    */
   @Override
   public Position getCenterPosition() {
      return _center;
   }


   /**
    * return the centerPoint (as a <code>Vec4</code>)
    */
   @Override
   public Vec4 getCenterPoint() {
      return (globe.computePointFromPosition(_center));
   }


   /**
    * return the <code>Position</code> of the camera (lat/lon/alt)
    */
   @Override
   public Position getEyePosition() {
      if (lastEyePosition == null) {
         lastEyePosition = computeEyePositionFromModelview();
      }
      return lastEyePosition;
   }


   @Override
   public Position getCurrentEyePosition() {
      if (globe != null) {
         final Matrix _modelview = GCustomViewInputSupport.computeTransformMatrix(globe, _center, heading, pitch, _zoom);
         if (_modelview != null) {
            final Matrix _modelviewInv = _modelview.getInverse();
            if (_modelviewInv != null) {
               final Vec4 eyePoint = Vec4.UNIT_W.transformBy4(_modelviewInv);
               return globe.computePositionFromPoint(eyePoint);
            }
         }
      }

      return Position.ZERO;
   }


   @Override
   public Vec4 getCurrentEyePoint() {
      if (globe != null) {
         final Matrix _modelview = GCustomViewInputSupport.computeTransformMatrix(globe, _center, heading, pitch, _zoom);
         if (_modelview != null) {
            final Matrix _modelviewInv = _modelview.getInverse();
            if (_modelviewInv != null) {
               return Vec4.UNIT_W.transformBy4(_modelviewInv);
            }
         }
      }

      return Vec4.ZERO;
   }


   @Override
   public void setEyePosition(final Position eyePos) {
      if (eyePos == null) {
         final String message = Logging.getMessage("nullValue.PositionIsNull");
         Logging.logger().severe(message);
         throw new IllegalArgumentException(message);
      }

      final double elevation = eyePos.getElevation();

      // Set the center lat/lon to the eye lat/lon. Set the center elevation to zero if the eye elevation is >= 0.
      // Set the center elevation to the eye elevation if the eye elevation is < 0.
      _center = new Position(eyePos, elevation >= 0 ? 0 : elevation);
      heading = Angle.ZERO;
      pitch = Angle.ZERO;
      // If the eye elevation is >= 0, zoom gets the eye elevation. If the eye elevation < 0, zoom gets 0.
      _zoom = elevation >= 0 ? elevation : 0;

      //resolveCollisionsWithCenterPosition();
   }


   @Override
   public void setOrientation(final Position eyePos,
                              final Position centerPosition) {
      if ((eyePos == null) || (centerPosition == null)) {
         final String message = Logging.getMessage("nullValue.PositionIsNull");
         Logging.logger().severe(message);
         throw new IllegalArgumentException(message);
      }
      if (globe == null) {

         final String message = Logging.getMessage("nullValue.DrawingContextGlobeIsNull");
         Logging.logger().severe(message);
         throw new IllegalStateException(message);
      }

      final Vec4 newEyePoint = globe.computePointFromPosition(eyePos);
      final Vec4 newCenterPoint = globe.computePointFromPosition(centerPosition);
      if ((newEyePoint == null) || (newCenterPoint == null)) {
         final String message = Logging.getMessage("View.ErrorSettingOrientation", eyePos, centerPosition);
         Logging.logger().severe(message);
         throw new IllegalArgumentException(message);
      }

      // If eye lat/lon != center lat/lon, then the surface normal at the center point will be a good value
      // for the up direction.
      Vec4 up = globe.computeSurfaceNormalAtPoint(newCenterPoint);
      // Otherwise, estimate the up direction by using the *current* heading with the new center position.
      final Vec4 forward = newCenterPoint.subtract3(newEyePoint).normalize3();
      if (forward.cross3(up).getLength3() < 0.001) {
         final Matrix modelviewMat = GCustomViewInputSupport.computeTransformMatrix(globe, centerPosition, heading, Angle.ZERO, 1);
         if (modelviewMat != null) {
            final Matrix modelviewInvMat = modelviewMat.getInverse();
            if (modelviewInvMat != null) {
               up = Vec4.UNIT_Y.transformBy4(modelviewInvMat);
            }
         }
      }

      if (up == null) {
         final String message = Logging.getMessage("View.ErrorSettingOrientation", eyePos, centerPosition);
         Logging.logger().severe(message);
         throw new IllegalArgumentException(message);
      }

      final GCustomViewInputSupport.CustomViewState modelCoords = GCustomViewInputSupport.computeCustomViewState(globe,
               newEyePoint, newCenterPoint, up);
      if (!validateModelCoordinates(modelCoords)) {
         final String message = Logging.getMessage("View.ErrorSettingOrientation", eyePos, centerPosition);
         Logging.logger().severe(message);
         throw new IllegalArgumentException(message);
      }

      setModelCoordinates(modelCoords);
   }


   /**
    * Determines if the BasicOrbitView can be focused on the viewport center {@link #focusOnViewportCenter}. Focusing on the
    * viewport center requires a non-null {@link gov.nasa.worldwind.globes.Globe}, a non-null {@link DrawContext}, and the
    * viewport center is on the terrain.
    * 
    * @return true if the BasicOrbitView can focus on the viewport center.
    **/
   @Override
   public boolean canFocusOnViewportCenter() {
      return (dc != null) && (dc.getViewportCenterPosition() != null) && (globe != null);
   }


   /**
    * sets the point of rotation for heading and pitch changes to the surface position at the viewport center.
    **/
   @Override
   public void focusOnViewportCenter() {
      if (isAnimating()) {
         return;
      }
      if (dc == null) {
         final String message = Logging.getMessage("nullValue.DrawContextIsNull");
         Logging.logger().severe(message);
         throw new IllegalStateException(message);
      }
      if (globe == null) {
         final String message = Logging.getMessage("nullValue.DrawingContextGlobeIsNull");
         Logging.logger().severe(message);
         throw new IllegalStateException(message);
      }

      final Position viewportCenterPos = dc.getViewportCenterPosition();
      if (viewportCenterPos == null) {
         final String message = Logging.getMessage("nullValue.DrawingContextViewportCenterIsNull");
         Logging.logger().severe(message);
         throw new IllegalStateException(message);
      }

      // We want the actual "geometric point" here, which must be adjusted for vertical exaggeration.
      final Vec4 viewportCenterPoint = globe.computePointFromPosition(
               viewportCenterPos.getLatitude(),
               viewportCenterPos.getLongitude(),
               globe.getElevation(viewportCenterPos.getLatitude(), viewportCenterPos.getLongitude())
                        * dc.getVerticalExaggeration());

      if (viewportCenterPoint != null) {
         final Matrix modelviewMat = GCustomViewInputSupport.computeTransformMatrix(globe, _center, heading, pitch, _zoom);
         if (modelviewMat != null) {
            final Matrix modelviewInvMat = modelviewMat.getInverse();
            if (modelviewInvMat != null) {
               // The change in focus must happen seamlessly; we can't move the eye or the forward vector
               // (only the center position and zoom should change). Therefore we pick a point along the
               // forward vector, and *near* the viewportCenterPoint, but not necessarily at the
               // viewportCenterPoint itself.
               final Vec4 eyePoint = Vec4.UNIT_W.transformBy4(modelviewInvMat);
               final Vec4 forward = Vec4.UNIT_NEGATIVE_Z.transformBy4(modelviewInvMat);
               final double distance = eyePoint.distanceTo3(viewportCenterPoint);
               final Vec4 newCenterPoint = Vec4.fromLine3(eyePoint, distance, forward);

               final GCustomViewInputSupport.CustomViewState modelCoords = GCustomViewInputSupport.computeCustomViewState(globe,
                        modelviewMat, newCenterPoint);
               if (validateModelCoordinates(modelCoords)) {
                  setModelCoordinates(modelCoords);
               }
            }
         }
      }

   }


   public boolean canFocusOnTerrainCenter() {
      return (dc != null) && (dc.getSurfaceGeometry() != null) && (globe != null);
   }


   public void focusOnTerrainCenter() {
      if (dc == null) {
         final String message = Logging.getMessage("nullValue.DrawContextIsNull");
         Logging.logger().severe(message);
         throw new IllegalStateException(message);
      }
      if (globe == null) {
         final String message = Logging.getMessage("nullValue.DrawingContextGlobeIsNull");
         Logging.logger().severe(message);
         throw new IllegalStateException(message);
      }

      if (dc.getSurfaceGeometry() == null) {
         return;
      }
      if (isAnimating()) {
         return;
      }

      final Matrix modelviewMat = GCustomViewInputSupport.computeTransformMatrix(globe, _center, heading, pitch, _zoom);
      if (modelviewMat != null) {
         final Matrix modelviewInvMat = modelviewMat.getInverse();
         if (modelviewInvMat != null) {
            // The change in focus must happen seamlessly; we can't move the eye or the forward vector
            // (only the center position and zoom should change). 

            final Vec4 eyePoint = Vec4.UNIT_W.transformBy4(modelviewInvMat);
            final Vec4 forward = Vec4.UNIT_NEGATIVE_Z.transformBy4(modelviewInvMat);
            final Intersection[] intersections = dc.getSurfaceGeometry().intersect(new Line(eyePoint, forward));
            if ((intersections != null) && (intersections.length > 0)) {
               final Vec4 viewportCenterPoint = intersections[0].getIntersectionPoint();
               final GCustomViewInputSupport.CustomViewState modelCoords = GCustomViewInputSupport.computeCustomViewState(globe,
                        modelviewMat, viewportCenterPoint);
               if (validateModelCoordinates(modelCoords)) {
                  setModelCoordinates(modelCoords);
               }
            }
         }
      }
   }


   @Override
   public double getZoom() {
      return _zoom;
   }


   @Override
   public void setCenterPosition(final Position center) {
      if (center == null) {
         final String message = Logging.getMessage("nullValue.PositionIsNull");
         Logging.logger().severe(message);
         throw new IllegalArgumentException(message);
      }
      if ((center.getLatitude().degrees < -90) || (center.getLatitude().degrees > 90)) {
         final String message = Logging.getMessage("generic.LatitudeOutOfRange", center.getLatitude());
         Logging.logger().severe(message);
         throw new IllegalArgumentException(message);
      }

      _center = normalizedCenterPosition(center);
      _center = GCustomViewLimits.limitCenterPosition(_center, getOrbitViewLimits());

      if (_inputState.isDetectCollisions()) {
         resolveCollisionsWithCenterPosition();
      }

   }


   @Override
   public void setHeading(final Angle newHeading) {
      if (newHeading == null) {
         final String message = Logging.getMessage("nullValue.AngleIsNull");
         Logging.logger().severe(message);
         throw new IllegalArgumentException(message);
      }

      heading = normalizedHeading(newHeading);
      heading = BasicViewPropertyLimits.limitHeading(heading, getOrbitViewLimits());
      if (_inputState.isDetectCollisions()) {
         resolveCollisionsWithPitch();
      }
   }


   @Override
   public void setPitch(final Angle newPitch) {
      if (newPitch == null) {
         final String message = Logging.getMessage("nullValue.AngleIsNull");
         Logging.logger().severe(message);
         throw new IllegalArgumentException(message);
      }

      pitch = normalizedPitch(newPitch);
      pitch = BasicViewPropertyLimits.limitPitch(pitch, getOrbitViewLimits());
      if (_inputState.isDetectCollisions()) {
         resolveCollisionsWithPitch();
      }
   }


   @Override
   public void setZoom(final double newZoom) {
      if (newZoom < 0) {
         final String message = Logging.getMessage("generic.ArgumentOutOfRange", newZoom);
         Logging.logger().severe(message);
         throw new IllegalArgumentException(message);
      }

      _zoom = newZoom;
      _zoom = GCustomViewLimits.limitZoom(_zoom, getOrbitViewLimits());
      if (_inputState.isDetectCollisions()) {
         resolveCollisionsWithCenterPosition();
      }

   }


   @Override
   public OrbitViewLimits getOrbitViewLimits() {
      return (OrbitViewLimits) viewLimits;
   }


   /**
    * Sets the <code>ICustomViewLimits</code> that will apply to this <code>GCustomView</code>. Incoming parameters to the methods
    * setCenterPosition, setHeading, setPitch, or setZoom will be limited by the parameters defined in <code>viewLimits</code>.
    * 
    * @param newViewLimits
    *           the <code>OrbitViewLimits</code> that will apply to this <code>GCustomView</code>.
    * @throws IllegalArgumentException
    *            if <code>viewLimits</code> is null.
    */
   @Override
   public void setOrbitViewLimits(final OrbitViewLimits newViewLimits) {
      if (newViewLimits == null) {
         final String message = Logging.getMessage("nullValue.ViewLimitsIsNull");
         Logging.logger().severe(message);
         throw new IllegalArgumentException(message);
      }

      viewLimits = newViewLimits;

   }


   @Override
   public void stopMovementOnCenter() {
      firePropertyChange(CENTER_STOPPED, null, null);

   }


   public static Position normalizedCenterPosition(final Position unnormalizedPosition) {
      if (unnormalizedPosition == null) {
         final String message = Logging.getMessage("nullValue.PositionIsNull");
         Logging.logger().severe(message);
         throw new IllegalArgumentException(message);
      }

      return new Position(Angle.normalizedLatitude(unnormalizedPosition.getLatitude()),
               Angle.normalizedLongitude(unnormalizedPosition.getLongitude()), unnormalizedPosition.getElevation());
   }


   public static Angle normalizedHeading(final Angle unnormalizedHeading) {
      if (unnormalizedHeading == null) {
         final String message = Logging.getMessage("nullValue.AngleIsNull");
         Logging.logger().severe(message);
         throw new IllegalArgumentException(message);
      }

      final double degrees = unnormalizedHeading.degrees;
      final double heading = degrees % 360;
      return Angle.fromDegrees(heading > 180 ? heading - 360 : (heading < -180 ? 360 + heading : heading));
   }


   public static Angle normalizedPitch(final Angle unnormalizedPitch) {
      if (unnormalizedPitch == null) {
         final String message = Logging.getMessage("nullValue.AngleIsNull");
         Logging.logger().severe(message);
         throw new IllegalArgumentException(message);
      }

      // Normalize pitch to the range [-180, 180].
      final double degrees = unnormalizedPitch.degrees;
      final double pitch = degrees % 360;
      return Angle.fromDegrees(pitch > 180 ? pitch - 360 : (pitch < -180 ? 360 + pitch : pitch));
   }


   //   private void resolveCollisionsWithCenterPosition()
   //   {
   //       if (this.dc == null)
   //           return;
   //
   //       if (!isDetectCollisions())
   //           return;
   //
   //
   //       // If there is no collision, 'newCenterPosition' will be null. Otherwise it will contain a value
   //       // that will resolve the collision.
   //       double nearDistance = this.computeNearDistance(this.getCurrentEyePosition());
   //       Position newCenter = this.collisionSupport.computeCenterPositionToResolveCollision(this, nearDistance, this.dc);
   //       if (newCenter != null && newCenter.getLatitude().degrees >= -90 && newCenter.getLongitude().degrees <= 90)
   //       {
   //           this.center = newCenter;
   //           flagHadCollisions();
   //       }
   //   }

   //   protected void resolveCollisionsWithPitch()
   //   {
   //       if (this.dc == null)
   //           return;
   //
   //       if (!isDetectCollisions())
   //           return;
   //
   //       // Compute the near distance corresponding to the current set of values.
   //       // If there is no collision, 'newPitch' will be null. Otherwise it will contain a value
   //       // that will resolve the collision.
   //       double nearDistance = this.computeNearDistance(this.getCurrentEyePosition());
   //       Angle newPitch = this.collisionSupport.computePitchToResolveCollision(this, nearDistance, this.dc);
   //       if (newPitch != null && newPitch.degrees <= 90 && newPitch.degrees >= 0)
   //       {
   //           this.pitch = newPitch;
   //           flagHadCollisions();
   //       }
   //   }

   /**
    * computes and sets the center of rotation for heading and pitch changes, if it is needed.
    */
   public void computeAndSetViewCenterIfNeeded() {
      if (_viewOutOfFocus) {
         computeAndSetViewCenter();
      }
   }


   /**
    * computes and sets the center of rotation for heading and pitch changes.
    */
   public void computeAndSetViewCenter() {
      try {
         // Update the View's focus.
         if (canFocusOnViewportCenter()) {
            focusOnViewportCenter();
            setViewOutOfFocus(false);
         }
      }
      catch (final Exception e) {
         final String message = Logging.getMessage("generic.ExceptionWhileChangingView");
         Logging.logger().log(java.util.logging.Level.SEVERE, message, e);
         // If updating the View's focus failed, raise the flag again.
         setViewOutOfFocus(true);
      }
   }


   @Override
   protected void doApply(final DrawContext drawContext) {
      if (drawContext == null) {
         final String message = Logging.getMessage("nullValue.DrawContextIsNull");
         Logging.logger().severe(message);
         throw new IllegalArgumentException(message);
      }
      if (drawContext.getGL() == null) {
         final String message = Logging.getMessage("nullValue.DrawingContextGLIsNull");
         Logging.logger().severe(message);
         throw new IllegalArgumentException(message);
      }
      if (drawContext.getGlobe() == null) {
         final String message = Logging.getMessage("nullValue.DrawingContextGlobeIsNull");
         Logging.logger().severe(message);
         throw new IllegalArgumentException(message);
      }

      // Update DrawContext and Globe references.
      dc = drawContext;
      globe = dc.getGlobe();
      //========== modelview matrix state ==========//
      // Compute the current modelview matrix.
      modelview = GCustomViewInputSupport.computeTransformMatrix(globe, _center, heading, pitch, _zoom);
      if (modelview == null) {
         modelview = Matrix.IDENTITY;
      }
      // Compute the current inverse-modelview matrix.
      modelviewInv = modelview.getInverse();
      if (modelviewInv == null) {
         modelviewInv = Matrix.IDENTITY;
      }

      //========== projection matrix state ==========//
      // Get the current OpenGL viewport state.
      final int[] viewportArray = new int[4];
      dc.getGL().glGetIntegerv(GL.GL_VIEWPORT, viewportArray, 0);
      viewport = new java.awt.Rectangle(viewportArray[0], viewportArray[1], viewportArray[2], viewportArray[3]);
      // Compute the current clip plane distances.

      nearClipDistance = computeNearClipDistance();
      farClipDistance = computeFarClipDistance();
      // Compute the current viewport dimensions.
      final double viewportWidth = viewport.getWidth() <= 0.0 ? 1.0 : viewport.getWidth();
      final double viewportHeight = viewport.getHeight() <= 0.0 ? 1.0 : viewport.getHeight();
      // Compute the current projection matrix.
      projection = Matrix.fromPerspective(fieldOfView, viewportWidth, viewportHeight, nearClipDistance, farClipDistance);
      // Compute the current frustum.
      frustum = Frustum.fromPerspective(fieldOfView, (int) viewportWidth, (int) viewportHeight, nearClipDistance, farClipDistance);

      //========== load GL matrix state ==========//
      loadGLViewState(drawContext, modelview, projection);

      //========== after apply (GL matrix state) ==========//
      afterDoApply();
   }


   protected void afterDoApply() {
      // Clear cached computations.
      lastEyePosition = null;
      lastEyePoint = null;
      lastUpVector = null;
      lastForwardVector = null;
      lastFrustumInModelCoords = null;
   }


   @Override
   public Vec4 project(final Vec4 modelPoint) {
      if (modelPoint == null) {
         final String message = Logging.getMessage("nullValue.Vec4IsNull");
         Logging.logger().severe(message);
         throw new IllegalArgumentException(message);
      }

      return project(modelPoint, modelview, projection, viewport);
   }


   @Override
   public Vec4 unProject(final Vec4 windowPoint) {
      if (windowPoint == null) {
         final String message = Logging.getMessage("nullValue.Vec4IsNull");
         Logging.logger().severe(message);
         throw new IllegalArgumentException(message);
      }

      return unProject(windowPoint, modelview, projection, viewport);
   }


   @Override
   public Line computeRayFromScreenPoint(final double x,
                                         final double y) {
      return ViewUtil.computeRayFromScreenPoint(this, x, y, modelview, projection, viewport);
   }


   @Override
   public Position computePositionFromScreenPoint(final double x,
                                                  final double y) {
      if (globe != null) {
         final Line ray = computeRayFromScreenPoint(x, y);
         if (ray != null) {
            return globe.getIntersectionPosition(ray);
         }
      }

      return null;
   }


   @Override
   public double computeHorizonDistance() {
      double horizon = 0;
      final Position eyePos = computeEyePositionFromModelview();
      if (eyePos != null) {
         horizon = computeHorizonDistance(eyePos);
      }

      return horizon;
   }


   @Override
   protected double computeHorizonDistance(final Position eyePos) {
      if ((globe != null) && (eyePos != null)) {
         final double elevation = eyePos.getElevation();
         final double elevationAboveSurface = ViewUtil.computeElevationAboveSurface(dc, eyePos);
         return ViewUtil.computeHorizonDistance(globe, Math.max(elevation, elevationAboveSurface));
      }

      return 0;
   }


   @Override
   protected Position computeEyePositionFromModelview() {
      if (globe != null) {
         final Vec4 eyePoint = Vec4.UNIT_W.transformBy4(modelviewInv);
         return globe.computePositionFromPoint(eyePoint);
      }

      return Position.ZERO;
   }


   @Override
   public double computePixelSizeAtDistance(final double distance) {
      return ViewUtil.computePixelSizeAtDistance(distance, fieldOfView, viewport);
   }


   @Override
   protected double computeNearDistance(final Position eyePosition1) {
      double near = 0;

      if ((eyePosition1 != null) && (dc != null)) {
         final double elevation = ViewUtil.computeElevationAboveSurface(dc, eyePosition1);
         final double tanHalfFov = fieldOfView.tanHalfAngle();
         near = elevation / (2 * GMath.sqrt(2 * tanHalfFov * tanHalfFov + 1));
      }

      return (near < 0.001) ? 0.001 : near / 4;
   }


   protected void setModelCoordinates(final GCustomViewInputSupport.CustomViewState modelCoords) {
      if (modelCoords != null) {
         if (modelCoords.getCenterPosition() != null) {
            _center = normalizedCenterPosition(modelCoords.getCenterPosition());
            _center = GCustomViewLimits.limitCenterPosition(_center, getOrbitViewLimits());
         }
         if (modelCoords.getHeading() != null) {
            heading = normalizedHeading(modelCoords.getHeading());
            heading = BasicViewPropertyLimits.limitHeading(heading, getOrbitViewLimits());
         }
         if (modelCoords.getPitch() != null) {
            pitch = normalizedPitch(modelCoords.getPitch());
            pitch = BasicViewPropertyLimits.limitPitch(pitch, getOrbitViewLimits());
         }

         _zoom = modelCoords.getZoom();
         _zoom = GCustomViewLimits.limitZoom(_zoom, getOrbitViewLimits());
      }
   }


   protected boolean validateModelCoordinates(final GCustomViewInputSupport.CustomViewState modelCoords) {
      return ((modelCoords != null) && (modelCoords.getCenterPosition() != null)
              && (modelCoords.getCenterPosition().getLatitude().degrees >= -90)
              && (modelCoords.getCenterPosition().getLatitude().degrees <= 90) && (modelCoords.getHeading() != null)
              && (modelCoords.getPitch() != null) && (modelCoords.getPitch().degrees >= 0)
              && (modelCoords.getPitch().degrees <= 90) && (modelCoords.getZoom() >= 0));
   }


   //**************************************************************//
   //******************** Restorable State  ***********************//
   //**************************************************************//

   @Override
   protected void doGetRestorableState(final RestorableSupport rs,
                                       final RestorableSupport.StateObject context) {
      super.doGetRestorableState(rs, context);

      if (getCenterPosition() != null) {
         final RestorableSupport.StateObject so = rs.addStateObject(context, "center");
         if (so != null) {
            rs.addStateValueAsDouble(so, "latitude", getCenterPosition().getLatitude().degrees);
            rs.addStateValueAsDouble(so, "longitude", getCenterPosition().getLongitude().degrees);
            rs.addStateValueAsDouble(so, "elevation", getCenterPosition().getElevation());
         }
      }

      rs.addStateValueAsDouble(context, "zoom", getZoom());
   }


   @Override
   protected void doRestoreState(final RestorableSupport rs,
                                 final RestorableSupport.StateObject context) {
      // Invoke the legacy restore functionality. This will enable the shape to recognize state XML elements
      // from previous versions of BasicOrbitView.
      legacyRestoreState(rs, context);

      super.doRestoreState(rs, context);

      // Restore the center property only if all parts are available.
      // We will not restore a partial center (for example, just latitude).
      final RestorableSupport.StateObject so = rs.getStateObject(context, "center");
      if (so != null) {
         final Double lat = rs.getStateValueAsDouble(so, "latitude");
         final Double lon = rs.getStateValueAsDouble(so, "longitude");
         final Double ele = rs.getStateValueAsDouble(so, "elevation");
         if ((lat != null) && (lon != null)) {
            setCenterPosition(Position.fromDegrees(lat, lon, (ele != null ? ele : 0)));
         }
      }

      final Double d = rs.getStateValueAsDouble(context, "zoom");
      if (d != null) {
         setZoom(d);
      }
   }


   /**
    * Restores state values from previous versions of the BasicObitView state XML. These values are stored or named differently
    * than the current implementation. Those values which have not changed are ignored here, and are restored in
    * {@link #doRestoreState(gov.nasa.worldwind.util.RestorableSupport, gov.nasa.worldwind.util.RestorableSupport.StateObject)}.
    * 
    * @param rs
    *           RestorableSupport object which contains the state value properties.
    * @param context
    *           active context in the RestorableSupport to read state from.
    */
   protected void legacyRestoreState(final RestorableSupport rs,
                                     final RestorableSupport.StateObject context) {
      final RestorableSupport.StateObject so = rs.getStateObject(context, "orbitViewLimits");
      if (so != null) {
         getOrbitViewLimits().restoreState(rs, so);
      }
   }


   public boolean hasCameraState() {
      return (_savedCameraState != null);
   }


   public void saveCameraState() {

      //_savedCameraState = new GCameraState(getCurrentEyePosition(), getZoom(), getFieldOfView(), getPitch(), getHeading());

      _savedCameraState = new GCameraState(getEyePosition(), getCenterPosition(), getZoom(), getFieldOfView(), getHeading(),
               getPitch());

   }


   public void restoreCameraState() {
      if (_savedCameraState == null) {
         System.out.println("No camera State!!");
         return;
      }
      stopAnimations();
      //      stopMovement();
      //      stopMovementOnCenter();


      setFieldOfView(_savedCameraState.getFov());
      setCenterPosition(_savedCameraState.getCenterPosition());
      setZoom(_savedCameraState.getZoom());
      setHeading(_savedCameraState.getHeading());
      setPitch(_savedCameraState.getPitch());

      _savedCameraState = null;


   }


   //**************************************************************//
   //******************** Animator Convenience Methods ************//
   //**************************************************************//

   @Override
   public void goTo(final Position position,
                    final double distance) {
      viewInputHandler.goTo(position, distance);
   }


   /**
    * 
    * @param position
    * @param distance
    * 
    *           Does the same as <code>goTo(Position position, double distance)</code> but without an animation
    */
   public void jumpTo(final Position position,
                      final double distance) {
      ((GCustomViewInputHandler) viewInputHandler).jumpTo(position, distance);
   }


   public void addPanToAnimator(final Position beginCenterPos,
                                final Position endCenterPos,
                                final Angle beginHeading,
                                final Angle endHeading,
                                final Angle beginPitch,
                                final Angle endPitch,
                                final double beginZoom,
                                final double endZoom,
                                final long timeToMove,
                                final boolean endCenterOnSurface) {
      ((GCustomViewInputHandler) viewInputHandler).addPanToAnimator(beginCenterPos, endCenterPos, beginHeading, endHeading,
               beginPitch, endPitch, beginZoom, endZoom, timeToMove, endCenterOnSurface);
   }


   public void addPanToAnimator(final Position beginCenterPos,
                                final Position endCenterPos,
                                final Angle beginHeading,
                                final Angle endHeading,
                                final Angle beginPitch,
                                final Angle endPitch,
                                final double beginZoom,
                                final double endZoom,
                                final boolean endCenterOnSurface) {

      ((GCustomViewInputHandler) viewInputHandler).addPanToAnimator(beginCenterPos, endCenterPos, beginHeading, endHeading,
               beginPitch, endPitch, beginZoom, endZoom, endCenterOnSurface);
   }


   public void addPanToAnimator(final Position centerPos,
                                final Angle newHeading,
                                final Angle newPitch,
                                final double newZoom,
                                final long timeToMove,
                                final boolean endCenterOnSurface) {
      ((GCustomViewInputHandler) viewInputHandler).addPanToAnimator(centerPos, newHeading, newPitch, newZoom, timeToMove,
               endCenterOnSurface);
   }


   public void addPanToAnimator(final Position centerPos,
                                final Angle newHeading,
                                final Angle newPitch,
                                final double newZoom,
                                final boolean endCenterOnSurface) {
      ((GCustomViewInputHandler) viewInputHandler).addPanToAnimator(centerPos, newHeading, newPitch, newZoom, endCenterOnSurface);
   }


   public void addPanToAnimator(final Position centerPos,
                                final Angle newHeading,
                                final Angle newPitch,
                                final double zoom) {
      ((GCustomViewInputHandler) viewInputHandler).addPanToAnimator(centerPos, newHeading, newPitch, zoom);
   }


   public void addEyePositionAnimator(final long timeToIterate,
                                      final Position beginPosition,
                                      final Position endPosition) {
      ((GCustomViewInputHandler) viewInputHandler).addEyePositionAnimator(timeToIterate, beginPosition, endPosition);
   }


   public void addHeadingAnimator(final Angle begin,
                                  final Angle end) {
      ((GCustomViewInputHandler) viewInputHandler).addHeadingAnimator(begin, end);
   }


   public void addPitchAnimator(final Angle begin,
                                final Angle end) {
      ((GCustomViewInputHandler) viewInputHandler).addPitchAnimator(begin, end);
   }


   public void addHeadingPitchAnimator(final Angle beginHeading,
                                       final Angle endHeading,
                                       final Angle beginPitch,
                                       final Angle endPitch) {
      ((GCustomViewInputHandler) viewInputHandler).addHeadingPitchAnimator(beginHeading, endHeading, beginPitch, endPitch);
   }


   public void addZoomAnimator(final double zoomStart,
                               final double zoomEnd) {
      ((GCustomViewInputHandler) viewInputHandler).addZoomAnimator(zoomStart, zoomEnd);
   }


   public void addFlyToZoomAnimator(final Angle newHeading,
                                    final Angle newPitch,
                                    final double zoomAmount) {
      ((GCustomViewInputHandler) viewInputHandler).addFlyToZoomAnimator(newHeading, newPitch, zoomAmount);
   }


   public void addCenterAnimator(final Position begin,
                                 final Position end,
                                 final boolean smoothed) {
      ((GCustomViewInputHandler) viewInputHandler).addCenterAnimator(begin, end, smoothed);
   }


   public void addCenterAnimator(final Position begin,
                                 final Position end,
                                 final long lengthMillis,
                                 final boolean smoothed) {
      ((GCustomViewInputHandler) viewInputHandler).addCenterAnimator(begin, end, lengthMillis, smoothed);
   }


   private void resolveCollisionsWithCenterPosition() {
      if (dc == null) {
         return;
      }

      if (!isDetectCollisions()) {
         return;
      }


      // If there is no collision, 'newCenterPosition' will be null. Otherwise it will contain a value
      // that will resolve the collision.
      final double nearDistance = computeNearDistance(getCurrentEyePosition());
      final Position newCenter = _collisionSupport.computeCenterPositionToResolveCollision(this, nearDistance, dc);
      if ((newCenter != null) && (newCenter.getLatitude().degrees >= -90) && (newCenter.getLongitude().degrees <= 90)) {
         _center = newCenter;
         flagHadCollisions();
      }
   }


   protected void resolveCollisionsWithPitch() {
      if (dc == null) {
         return;
      }

      if (!isDetectCollisions()) {
         return;
      }

      // Compute the near distance corresponding to the current set of values.
      // If there is no collision, 'newPitch' will be null. Otherwise it will contain a value
      // that will resolve the collision.
      final double nearDistance = computeNearDistance(getCurrentEyePosition());
      final Angle newPitch = _collisionSupport.computePitchToResolveCollision(this, nearDistance, dc);
      if ((newPitch != null) && (newPitch.degrees <= 90) && (newPitch.degrees >= 0)) {
         pitch = newPitch;
         flagHadCollisions();
      }
   }


   @Override
   public void setNearClipDistance(final double clipDistance) {
      nearClipDistance = clipDistance;
   }

}
