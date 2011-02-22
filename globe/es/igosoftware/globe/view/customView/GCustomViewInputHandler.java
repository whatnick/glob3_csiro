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

import es.igosoftware.globe.GGlobeApplication;
import es.igosoftware.globe.layers.I3DContentCollectionLayer;
import es.igosoftware.globe.view.GInputState;
import es.igosoftware.util.GMath;
import gov.nasa.worldwind.View;
import gov.nasa.worldwind.animation.AngleAnimator;
import gov.nasa.worldwind.animation.AnimationController;
import gov.nasa.worldwind.animation.AnimationSupport;
import gov.nasa.worldwind.animation.Animator;
import gov.nasa.worldwind.animation.CompoundAnimator;
import gov.nasa.worldwind.animation.DoubleAnimator;
import gov.nasa.worldwind.animation.Interpolator;
import gov.nasa.worldwind.animation.PositionAnimator;
import gov.nasa.worldwind.animation.RotateToAngleAnimator;
import gov.nasa.worldwind.animation.ScheduledInterpolator;
import gov.nasa.worldwind.animation.SmoothInterpolator;
import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.awt.BasicViewInputHandler;
import gov.nasa.worldwind.awt.ViewInputAttributes;
import gov.nasa.worldwind.awt.ViewInputAttributes.ActionAttributes;
import gov.nasa.worldwind.awt.ViewInputAttributes.DeviceAttributes;
import gov.nasa.worldwind.geom.Angle;
import gov.nasa.worldwind.geom.LatLon;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.geom.Vec4;
import gov.nasa.worldwind.util.Logging;
import gov.nasa.worldwind.util.PropertyAccessor;
import gov.nasa.worldwind.view.BasicViewPropertyLimits;
import gov.nasa.worldwind.view.ViewPropertyAccessor;
import gov.nasa.worldwind.view.ViewUtil;
import gov.nasa.worldwind.view.orbit.OrbitView;
import gov.nasa.worldwind.view.orbit.OrbitViewLimits;
import gov.nasa.worldwind.view.orbit.OrbitViewPropertyAccessor;

import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.MouseWheelEvent;
import java.util.Date;


public class GCustomViewInputHandler
         extends
            BasicViewInputHandler {

   private static final String       VIEW_ANIM_HEADING       = "ViewAnimHeading";
   private static final String       VIEW_ANIM_PITCH         = "ViewAnimPitch";
   private static final String       VIEW_ANIM_HEADING_PITCH = "ViewAnimHeadingPitch";
   private static final String       VIEW_ANIM_POSITION      = "ViewAnimPosition";
   private static final String       VIEW_ANIM_CENTER        = "ViewAnimCenter";
   private static final String       VIEW_ANIM_ZOOM          = "ViewAnimZoom";
   private static final String       VIEW_ANIM_PAN           = "ViewAnimPan";
   private static final String       VIEW_ANIM_APP           = "ViewAnimApp";


   private final AnimationController _gotoAnimControl        = new AnimationController();
   private final AnimationController _uiAnimControl          = new AnimationController();


   public GCustomViewInputHandler() {

   }


   //**************************************************************//
   //********************  View Change Events  ********************//
   //**************************************************************//
   @Override
   protected void onMoveTo(final Position focalPosition,
                           final ViewInputAttributes.ActionAttributes actionAttribs) {

   }


   @Override
   protected void onMoveTo(final Position focalPosition,
                           final ViewInputAttributes.DeviceAttributes deviceAttributes,
                           final ViewInputAttributes.ActionAttributes actionAttribs) {

      final View view = getView();
      if (view == null) { // include this test to ensure any derived implementation performs it
         return;
      }

      GInputState inputState = null;
      if (view instanceof GCustomView) {
         inputState = ((GCustomView) view).getInputState();
      }
      if ((inputState != null) && (!inputState.isMoving())) {
         return;
      }

      //System.out.println("onMoveTo");
      stopAllAnimators();
      if (view instanceof OrbitView) {
         // We're treating a speed parameter as smoothing here. A greater speed results in greater smoothing and
         // slower response. Therefore the min speed used at lower altitudes ought to be *greater* than the max
         // speed used at higher altitudes.
         //double[] values = actionAttribs.getValues();
         double smoothing = getScaleValueZoom(actionAttribs);
         if (!actionAttribs.isEnableSmoothing()) {
            smoothing = 0.0;
         }

         final GCustomViewCenterAnimator centerAnimator = new GCustomViewCenterAnimator((GCustomView) getView(),
                  view.getEyePosition(), focalPosition, smoothing,
                  GCustomViewPropertyAccessor.createCenterPositionAccessor((OrbitView) view), true);
         _gotoAnimControl.put(VIEW_ANIM_CENTER, centerAnimator);
         view.firePropertyChange(AVKey.VIEW, null, view);
      }
   }


   @Override
   protected void onHorizontalTranslateAbs(final Angle latitudeChange,
                                           final Angle longitudeChange,
                                           final ViewInputAttributes.ActionAttributes actionAttribs) {

      stopGoToAnimators();
      stopUserInputAnimators(VIEW_ANIM_HEADING, VIEW_ANIM_PITCH, VIEW_ANIM_ZOOM);

      final View view = getView();
      if (view == null) { // include this test to ensure any derived implementation performs it
         return;
      }

      GInputState inputState = null;
      if (view instanceof GCustomView) {
         inputState = ((GCustomView) view).getInputState();
      }
      if ((inputState != null) && (!inputState.isMoving())) {
         return;
      }

      if (latitudeChange.equals(Angle.ZERO) && longitudeChange.equals(Angle.ZERO)) {
         return;
      }

      //System.out.println("onHorizontalTranslateAbs");
      if (view instanceof OrbitView) {
         final Position newPosition = ((OrbitView) view).getCenterPosition().add(
                  new Position(latitudeChange, longitudeChange, 0.0));

         setCenterPosition((GCustomView) view, _uiAnimControl, newPosition, actionAttribs);
      }
   }


   @Override
   protected void onHorizontalTranslateRel(double forwardInput,
                                           double sideInput,
                                           final double totalForwardInput,
                                           final double totalSideInput,
                                           final ViewInputAttributes.DeviceAttributes deviceAttributes,
                                           final ViewInputAttributes.ActionAttributes actionAttributes) {
      final View view = getView();
      GInputState inputState = null;
      if (view instanceof GCustomView) {
         inputState = ((GCustomView) view).getInputState();
      }
      if ((inputState != null) && (!inputState.isMoving())) {
         onRotateView(Angle.fromDegrees(-sideInput * 0.5), Angle.fromDegrees(forwardInput * 0.5), actionAttributes);
         return;
      }


      //System.out.println("onHorizontalTranslateRel(6 args)");
      stopGoToAnimators();
      stopUserInputAnimators(VIEW_ANIM_HEADING, VIEW_ANIM_PITCH, VIEW_ANIM_ZOOM);

      if (actionAttributes.getMouseActions() != null) {
         // Normalize the forward and right magnitudes.
         final double length = Math.sqrt(forwardInput * forwardInput + sideInput * sideInput);
         if (length > 0.0) {
            forwardInput /= length;
            sideInput /= length;
         }

         final Point point = constrainToSourceBounds(getMousePoint(), getWorldWindow());
         final Point lastPoint = constrainToSourceBounds(getLastMousePoint(), getWorldWindow());
         if (getSelectedPosition() == null) {
            // Compute the current selected position if none exists. This happens if the user starts dragging when
            // the cursor is off the globe, then drags the cursor onto the globe.
            setSelectedPosition(computeSelectedPosition());
         }
         else if (computeSelectedPosition() == null) {
            // User dragged the cursor off the globe. Clear the selected position to ensure a new one will be
            // computed if the user drags the cursor back to the globe.
            setSelectedPosition(null);
         }
         else if ((computeSelectedPointAt(point) == null) || (computeSelectedPointAt(lastPoint) == null)) {
            // User selected a position that is won't work for dragging. Probably the selected elevation is above the
            // eye elevation, in which case dragging becomes unpredictable. Clear the selected position to ensure
            // a new one will be computed if the user drags the cursor to a valid position.
            setSelectedPosition(null);
         }

         final Vec4 vec = computeSelectedPointAt(point);
         final Vec4 lastVec = computeSelectedPointAt(lastPoint);

         // Cursor is on the globe, pan between the two positions.
         if ((vec != null) && (lastVec != null)) {
            // Compute the change in view location given two screen points and corresponding world vectors.
            final LatLon latlon = getChangeInLocation(lastPoint, point, lastVec, vec);
            onHorizontalTranslateAbs(latlon.getLatitude(), latlon.getLongitude(), actionAttributes);
            return;
         }

         final Point movement = ViewUtil.subtract(point, lastPoint);
         forwardInput = movement.y;
         sideInput = -movement.x;
      }

      // Cursor is off the globe, we potentially want to simulate globe dragging.
      // or this is a keyboard event.
      final Angle forwardChange = Angle.fromDegrees(forwardInput * getScaleValueHorizTransRel(deviceAttributes, actionAttributes));
      final Angle sideChange = Angle.fromDegrees(sideInput * getScaleValueHorizTransRel(deviceAttributes, actionAttributes));
      onHorizontalTranslateRel(forwardChange, sideChange, actionAttributes);
   }


   @Override
   protected void onHorizontalTranslateRel(final Angle forwardChange,
                                           final Angle sideChange,
                                           final ActionAttributes actionAttribs) {
      //System.out.println("onHorizontalTranslateRel(3args)");
      final View view = getView();
      if (view == null) { // include this test to ensure any derived implementation performs it
         return;
      }

      if (forwardChange.equals(Angle.ZERO) && sideChange.equals(Angle.ZERO)) {
         return;
      }

      if (view instanceof OrbitView) {
         final double sinHeading = view.getHeading().sin();
         final double cosHeading = view.getHeading().cos();
         final double latChange = cosHeading * forwardChange.getDegrees() - sinHeading * sideChange.getDegrees();
         final double lonChange = sinHeading * forwardChange.getDegrees() + cosHeading * sideChange.getDegrees();
         final Position newPosition = ((OrbitView) view).getCenterPosition().add(Position.fromDegrees(latChange, lonChange, 0.0));

         setCenterPosition((GCustomView) view, _uiAnimControl, newPosition, actionAttribs);
      }
   }


   @Override
   protected void onResetHeading(final ViewInputAttributes.ActionAttributes actionAttribs) {
      //System.out.println("onResetHeading");
      stopAllAnimators();

      final View view = getView();
      if (view == null) { // include this test to ensure any derived implementation performs it
         return;
      }
      addHeadingAnimator(view.getHeading(), Angle.ZERO);
   }


   @Override
   protected void onResetHeadingAndPitch(final ViewInputAttributes.ActionAttributes actionAttribs) {
      //System.out.println("onResetHeadingAndPitch");
      stopAllAnimators();

      final View view = getView();
      if (view == null) { // include this test to ensure any derived implementation performs it
         return;
      }

      addHeadingPitchAnimator(view.getHeading(), Angle.ZERO, view.getPitch(), Angle.ZERO);
   }


   @Override
   protected void onRotateView(final Angle headingChange,
                               final Angle pitchChange,
                               final ActionAttributes actionAttribs) {
      //System.out.println("onRotateView (3args)");
      final View view = getView();
      if (view == null) { // include this test to ensure any derived implementation performs it
         return;
      }

      if (view instanceof GCustomView) {
         if (!headingChange.equals(Angle.ZERO)) {
            final GCustomView customView = (GCustomView) view;
            final GInputState inputState = customView.getInputState();
            if (inputState == GInputState.PANORAMICS) {
               changeHeading((GCustomView) view, _uiAnimControl, headingChange.multiply(-1.0), actionAttribs);
            }
            else {
               changeHeading((GCustomView) view, _uiAnimControl, headingChange, actionAttribs);
            }
         }

         if (!pitchChange.equals(Angle.ZERO)) {
            changePitch((GCustomView) view, _uiAnimControl, pitchChange, actionAttribs);
         }
      }
   }


   @Override
   protected void onRotateView(double headingInput,
                               double pitchInput,
                               final double totalHeadingInput,
                               final double totalPitchInput,
                               final DeviceAttributes deviceAttributes,
                               final ActionAttributes actionAttributes) {
      //System.out.println("onRotateView (6 args)");
      stopGoToAnimators();
      stopUserInputAnimators(VIEW_ANIM_CENTER, VIEW_ANIM_ZOOM);

      if (actionAttributes.getMouseActions() != null) {
         // Switch the direction of heading change depending on whether the cursor is above or below
         // the center of the screen.
         //         if (getWorldWindow() instanceof Component) {
         //            if (getMousePoint().y < ((Component) getWorldWindow()).getHeight() / 2) {
         //               headingInput = -headingInput;
         //            }
         //         }
      }
      else {
         final double length = Math.sqrt(headingInput * headingInput + pitchInput * pitchInput);
         if (length > 0.0) {
            headingInput /= length;
            pitchInput /= length;
         }
      }

      final Angle headingChange = Angle.fromDegrees(headingInput * getScaleValueRotate(actionAttributes));
      final Angle pitchChange = Angle.fromDegrees(pitchInput * getScaleValueRotate(actionAttributes));

      onRotateView(headingChange, pitchChange, actionAttributes);
   }


   @Override
   protected void onVerticalTranslate(final double translateChange,
                                      final double totalTranslateChange,
                                      final DeviceAttributes deviceAttributes,
                                      final ActionAttributes actionAttributes) {
      stopGoToAnimators();
      stopUserInputAnimators(VIEW_ANIM_CENTER, VIEW_ANIM_HEADING, VIEW_ANIM_PITCH);

      final double zoomChange = translateChange * getScaleValueRotate(actionAttributes);
      onVerticalTranslate(zoomChange, actionAttributes);
   }


   @Override
   protected void onVerticalTranslate(final double translateChange,
                                      final ViewInputAttributes.ActionAttributes actionAttribs) {
      final View view = getView();
      if (view == null) { // include this test to ensure any derived implementation performs it
         return;
      }

      if (translateChange == 0) {
         return;
      }

      if (view instanceof GCustomView) {
         changeZoom((GCustomView) view, _uiAnimControl, translateChange, actionAttribs);
      }
   }


   //**************************************************************//
   //********************  Property Change Events  ****************//
   //**************************************************************//

   @Override
   protected void handlePropertyChange(final java.beans.PropertyChangeEvent e) {
      super.handlePropertyChange(e);

      if (e.getPropertyName() == OrbitView.CENTER_STOPPED) {
         handleCustomViewCenterStopped();
      }
   }


   protected void stopAllAnimators() {
      // Explicitly stop all animators, then clear the data structure which holds them. If we remove an animator
      // from this data structure without invoking stop(), the animator has no way of knowing it was forcibly stopped.
      // An animator's owner - potentially an object other than this ViewInputHandler - may need to know if an
      // animator has been forcibly stopped in order to react correctly to that event.
      _uiAnimControl.stopAnimations();
      _gotoAnimControl.stopAnimations();
      _uiAnimControl.clear();
      _gotoAnimControl.clear();

      final View view = getView();
      if (view == null) {
         return;
      }

      if (view instanceof GCustomView) {
         ((GCustomView) view).setViewOutOfFocus(true);
      }
   }


   protected void stopGoToAnimators() {
      // Explicitly stop all 'go to' animators, then clear the data structure which holds them. If we remove an
      // animator from this data structure without invoking stop(), the animator has no way of knowing it was forcibly
      // stopped. An animator's owner - likely an application object other - may need to know if an animator has been
      // forcibly stopped in order to react correctly to that event.
      _gotoAnimControl.stopAnimations();
      _gotoAnimControl.clear();
   }


   protected void stopUserInputAnimators(final Object... names) {
      for (final Object o : names) {
         if (_uiAnimControl.get(o) != null) {
            // Explicitly stop the 'ui' animator, then clear it from the data structure which holds it. If we remove
            // an animator from this data structure without invoking stop(), the animator has no way of knowing it
            // was forcibly stopped. Though applications cannot access the 'ui' animator data structure, stopping
            // the animators here is the correct action.
            _uiAnimControl.get(o).stop();
            _uiAnimControl.remove(o);
         }
      }
   }


   @Override
   protected void handleViewStopped() {
      stopAllAnimators();
   }


   protected void handleCustomViewCenterStopped() {
      // The "center stopped" message instructs components to stop modifying the OrbitView's center position.
      // Therefore we stop any center position animations started by this view controller.
      stopUserInputAnimators(VIEW_ANIM_CENTER);
   }


   //**************************************************************//
   //********************  View State Change Utilities  ***********//
   //**************************************************************//
   protected void setCenterPosition(final GCustomView view,
                                    final AnimationController animControl,
                                    final Position position,
                                    final ViewInputAttributes.ActionAttributes attrib) {
      double smoothing = attrib.getSmoothingValue();
      if (!(attrib.isEnableSmoothing() && isEnableSmoothing())) {
         smoothing = 0.0;
      }

      if (smoothing == 0) {
         if (animControl.get(VIEW_ANIM_CENTER) != null) {
            animControl.remove(VIEW_ANIM_CENTER);
         }
         final Position newPosition = GCustomViewLimits.limitCenterPosition(position, view.getOrbitViewLimits());
         view.setCenterPosition(newPosition);
         view.setViewOutOfFocus(true);
      }
      else {
         GCustomViewCenterAnimator centerAnimator = (GCustomViewCenterAnimator) animControl.get(VIEW_ANIM_CENTER);
         final Position cur = view.getCenterPosition();

         if ((centerAnimator == null) || !centerAnimator.hasNext()) {
            final Position newPosition = computeNewPosition(position, view.getOrbitViewLimits());
            centerAnimator = new GCustomViewCenterAnimator((GCustomView) getView(), cur, newPosition, smoothing,
                     GCustomViewPropertyAccessor.createCenterPositionAccessor(view), true);
            animControl.put(VIEW_ANIM_CENTER, centerAnimator);
         }
         else {
            Position newPosition = new Position(centerAnimator.getEnd().getLatitude().add(position.getLatitude()).subtract(
                     cur.getLatitude()), centerAnimator.getEnd().getLongitude().add(position.getLongitude()).subtract(
                     cur.getLongitude()), centerAnimator.getEnd().getElevation() + position.getElevation() - cur.getElevation());
            newPosition = computeNewPosition(newPosition, view.getOrbitViewLimits());
            centerAnimator.setEnd(newPosition);
         }

         centerAnimator.start();
      }

      view.firePropertyChange(AVKey.VIEW, null, view);
   }


   protected void changeHeading(final GCustomView view,
                                final AnimationController animControl,
                                final Angle change,
                                final ViewInputAttributes.ActionAttributes attrib) {
      view.computeAndSetViewCenterIfNeeded();

      double smoothing = attrib.getSmoothingValue();
      if (!(attrib.isEnableSmoothing() && isEnableSmoothing())) {
         smoothing = 0.0;
      }

      if (smoothing == 0) {
         if (animControl.get(VIEW_ANIM_HEADING) != null) {
            animControl.remove(VIEW_ANIM_HEADING);
         }
         final Angle newHeading = computeNewHeading(view.getHeading().add(change), view.getOrbitViewLimits());
         view.setHeading(newHeading);
      }
      else {
         RotateToAngleAnimator angleAnimator = (RotateToAngleAnimator) animControl.get(VIEW_ANIM_HEADING);

         if ((angleAnimator == null) || !angleAnimator.hasNext()) {
            final Angle newHeading = computeNewHeading(view.getHeading().add(change), view.getOrbitViewLimits());
            angleAnimator = new RotateToAngleAnimator(view.getHeading(), newHeading, smoothing,
                     ViewPropertyAccessor.createHeadingAccessor(view));
            animControl.put(VIEW_ANIM_HEADING, angleAnimator);
         }
         else {
            final Angle newHeading = computeNewHeading(angleAnimator.getEnd().add(change), view.getOrbitViewLimits());
            angleAnimator.setEnd(newHeading);
         }

         angleAnimator.start();
      }

      view.firePropertyChange(AVKey.VIEW, null, view);
   }


   protected void changePitch(final GCustomView view,
                              final AnimationController animControl,
                              final Angle change,
                              final ViewInputAttributes.ActionAttributes attrib) {
      view.computeAndSetViewCenterIfNeeded();

      double smoothing = attrib.getSmoothingValue();
      if (!(attrib.isEnableSmoothing() && isEnableSmoothing())) {
         smoothing = 0.0;
      }

      if (smoothing == 0.0) {
         if (animControl.get(VIEW_ANIM_PITCH) != null) {
            animControl.remove(VIEW_ANIM_PITCH);
         }
         final Angle newPitch = computeNewPitch(view.getPitch().add(change), view.getOrbitViewLimits());
         view.setPitch(newPitch);
      }
      else {
         RotateToAngleAnimator angleAnimator = (RotateToAngleAnimator) animControl.get(VIEW_ANIM_PITCH);

         if ((angleAnimator == null) || !angleAnimator.hasNext()) {
            // Create an angle animator which tilts the view to the specified new pitch. If this changes causes the
            // view to collide with the surface, this animator is set to stop. We enable this behavior by using a
            // {@link #CollisionAwarePitchAccessor} angle acessor and setting the animator's stopOnInvalidState
            // property to 'true'.
            final Angle newPitch = computeNewPitch(view.getPitch().add(change), view.getOrbitViewLimits());
            angleAnimator = new RotateToAngleAnimator(view.getPitch(), newPitch, smoothing, new CollisionAwarePitchAccessor(view));
            angleAnimator.setStopOnInvalidState(true);
            animControl.put(VIEW_ANIM_PITCH, angleAnimator);
         }
         else {
            final Angle newPitch = computeNewPitch(angleAnimator.getEnd().add(change), view.getOrbitViewLimits());
            angleAnimator.setEnd(newPitch);
         }

         angleAnimator.start();
      }

      view.firePropertyChange(AVKey.VIEW, null, view);
   }


   protected void changeZoom(final GCustomView view,
                             final AnimationController animControl,
                             final double change,
                             final ViewInputAttributes.ActionAttributes attrib) {
      view.computeAndSetViewCenterIfNeeded();

      double smoothing = attrib.getSmoothingValue();
      if (!(attrib.isEnableSmoothing() && isEnableSmoothing())) {
         smoothing = 0.0;
      }

      if (smoothing == 0.0) {
         if (animControl.get(VIEW_ANIM_ZOOM) != null) {
            animControl.remove(VIEW_ANIM_ZOOM);
         }
         view.setZoom(computeNewZoom(view.getZoom(), change, view.getOrbitViewLimits()));
      }
      else {
         double newZoom;
         GCustomViewMoveToZoomAnimator zoomAnimator = (GCustomViewMoveToZoomAnimator) animControl.get(VIEW_ANIM_ZOOM);

         if ((zoomAnimator == null) || !zoomAnimator.hasNext()) {
            newZoom = computeNewZoom(view.getZoom(), change, view.getOrbitViewLimits());
            zoomAnimator = new GCustomViewMoveToZoomAnimator(view, newZoom, smoothing,
                     GCustomViewPropertyAccessor.createZoomAccessor(view), false);
            animControl.put(VIEW_ANIM_ZOOM, zoomAnimator);
         }
         else {
            newZoom = computeNewZoom(zoomAnimator.getEnd(), change, view.getOrbitViewLimits());
            zoomAnimator.setEnd(newZoom);
         }

         zoomAnimator.start();
      }
      view.firePropertyChange(AVKey.VIEW, null, view);
   }


   protected static Position computeNewPosition(final Position position,
                                                final OrbitViewLimits limits) {
      final Position newPosition = new Position(Angle.normalizedLatitude(position.getLatitude()),
               Angle.normalizedLongitude(position.getLongitude()), position.getElevation());
      return GCustomViewLimits.limitCenterPosition(newPosition, limits);
   }


   protected static Angle computeNewHeading(final Angle heading,
                                            final OrbitViewLimits limits) {
      final Angle newHeading = GCustomView.normalizedHeading(heading);
      return BasicViewPropertyLimits.limitHeading(newHeading, limits);
   }


   protected static Angle computeNewPitch(final Angle pitch,
                                          final OrbitViewLimits limits) {
      final Angle newHeading = GCustomView.normalizedPitch(pitch);
      return BasicViewPropertyLimits.limitPitch(newHeading, limits);
   }


   protected static double computeNewZoom(final double curZoom,
                                          final double change,
                                          final OrbitViewLimits limits) {
      final double logCurZoom = curZoom != 0 ? Math.log(curZoom) : 0;
      final double newZoom = Math.exp(logCurZoom + change);
      return GCustomViewLimits.limitZoom(newZoom, limits);
   }


   // ************************************************ //
   // ******  Input Handler Property Accessors  ****** //
   // ************************************************ //
   /**
    * CollisionAwarePitchAccessor implements an {@link gov.nasa.worldwind.util.PropertyAccessor.AngleAccessor} interface onto the
    * pitch property of an {@link gov.nasa.worldwind.view.orbit.OrbitView}. In addition to accessing the pitch property, this
    * implementation is aware of view-surface collisions caused by setting the pitch property. If a call to
    * {@link #setAngle(gov.nasa.worldwind.geom.Angle)} causes the view to collide with the surface, then the call returns false
    * indicating to the caller that the set operation was not entirely successful.
    */
   protected static class CollisionAwarePitchAccessor
            implements
               PropertyAccessor.AngleAccessor {
      protected OrbitView _customView;


      /**
       * Creates a new CollisionAwarePitchAccessor with the specified OrbitView, but otherwise does nothing.
       * 
       * @param orbitView
       *           the OrbitView who's pitch will be accessed.
       * 
       * @throws IllegalArgumentException
       *            if the orbitView is null.
       */
      public CollisionAwarePitchAccessor(final OrbitView customView) {
         if (customView == null) {
            final String message = Logging.getMessage("nullValue.CustomViewIsNull");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
         }

         _customView = customView;
      }


      /**
       * Returns the pitch property value from this accessor's view.
       * 
       * @return the pitch from this accessor's view.
       */
      @Override
      public Angle getAngle() {
         return _customView.getPitch();
      }


      /**
       * Sets the pitch property of this accessor's view to the specified value. If the value is null, setting the view's pitch
       * causes a surface collision, or setting the view's pitch causes an exception, this returns false. Otherwise this returns
       * true.
       * 
       * @param value
       *           the value to set as this view's pitch property.
       * 
       * @return true if the pitch property was successfully set, and false otherwise.
       */
      @Override
      public boolean setAngle(final Angle value) {
         if (value == null) {
            return false;
         }

         // If the view supports surface collision detection, then clear the view's collision flag prior to
         // making any property changes.
         if (_customView.isDetectCollisions()) {
            _customView.hadCollisions();
         }

         try {
            _customView.setPitch(value);
         }
         catch (final Exception e) {
            final String message = Logging.getMessage("generic.ExceptionWhileChangingView");
            Logging.logger().log(java.util.logging.Level.SEVERE, message, e);
            return false;
         }

         // If the view supports surface collision detection, then return false if the collision flag is set,
         // otherwise return true.
         return !(_customView.isDetectCollisions() && _customView.hadCollisions());
      }
   }


   //**************************************************************//
   //********************  Scaling Utilities  *********************//
   //**************************************************************//
   protected double getScaleValueHorizTransRel(final ViewInputAttributes.DeviceAttributes deviceAttributes,
                                               final ViewInputAttributes.ActionAttributes actionAttributes) {

      final View view = getView();
      if (view == null) {
         return 0.0;
      }

      if (view instanceof OrbitView) {
         final double[] range = actionAttributes.getValues();
         // If this is a CustomView, we use the zoom value to set the scale
         final double radius = getWorldWindow().getModel().getGlobe().getRadius();
         final double t = getScaleValue(range[0], range[1], ((OrbitView) view).getZoom(), 3.0 * radius, true);
         return t;
      }

      // Any other view, use the base class scaling method
      return super.getScaleValueElevation(deviceAttributes, actionAttributes);

   }


   protected double getScaleValueRotate(final ViewInputAttributes.ActionAttributes actionAttributes) {

      final View view = getView();
      if (view == null) {
         return 0.0;
      }

      if (view instanceof OrbitView) {
         final double[] range = actionAttributes.getValues();
         // If this is a CustomView, we use the zoom value to set the scale
         final double radius = getWorldWindow().getModel().getGlobe().getRadius();
         final double t = getScaleValue(range[0], range[1], ((OrbitView) view).getZoom(), 3.0 * radius, false);
         return t;
      }

      return 1.0;
   }


   protected double getScaleValueZoom(final ViewInputAttributes.ActionAttributes actionAttributes) {
      final View view = getView();
      if (view == null) {
         return 0.0;
      }

      if (view instanceof OrbitView) {
         final double[] range = actionAttributes.getValues();
         // If this is a CustomView, we use the zoom value to set the scale
         final double radius = getWorldWindow().getModel().getGlobe().getRadius();
         double t = ((OrbitView) view).getZoom() / (3.0 * radius);
         t = (t < 0 ? 0 : (t > 1 ? 1 : t));
         return range[0] * (1.0 - t) + range[1] * t;
      }

      return 1.0;
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
      final OrbitView customView = (OrbitView) getView();
      final GFlyToCustomViewAnimator panAnimator = GFlyToCustomViewAnimator.createFlyToCustomViewAnimator(customView,
               beginCenterPos, endCenterPos, beginHeading, endHeading, beginPitch, endPitch, beginZoom, endZoom, timeToMove,
               endCenterOnSurface);

      _gotoAnimControl.put(VIEW_ANIM_PAN, panAnimator);
      getView().firePropertyChange(AVKey.VIEW, null, getView());
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

      // TODO: scale on mid-altitude?
      final long MIN_LENGTH_MILLIS = 4000;
      final long MAX_LENGTH_MILLIS = 16000;
      final long timeToMove = AnimationSupport.getScaledTimeMillisecs(beginCenterPos, endCenterPos, MIN_LENGTH_MILLIS,
               MAX_LENGTH_MILLIS);
      final OrbitView customView = (OrbitView) getView();
      final GFlyToCustomViewAnimator panAnimator = GFlyToCustomViewAnimator.createFlyToCustomViewAnimator(customView,
               beginCenterPos, endCenterPos, beginHeading, endHeading, beginPitch, endPitch, beginZoom, endZoom, timeToMove,
               endCenterOnSurface);

      _gotoAnimControl.put(VIEW_ANIM_PAN, panAnimator);
      getView().firePropertyChange(AVKey.VIEW, null, getView());
   }


   public void addPanToAnimator(final Position centerPos,
                                final Angle heading,
                                final Angle pitch,
                                final double zoom,
                                final long timeToMove,
                                final boolean endCenterOnSurface) {
      final OrbitView view = (OrbitView) getView();
      addPanToAnimator(view.getCenterPosition(), centerPos, view.getHeading(), heading, view.getPitch(), pitch, view.getZoom(),
               zoom, timeToMove, endCenterOnSurface);
      getView().firePropertyChange(AVKey.VIEW, null, getView());
   }


   public void addPanToAnimator(final Position centerPos,
                                final Angle heading,
                                final Angle pitch,
                                final double zoom,
                                final boolean endCenterOnSurface) {
      final OrbitView view = (OrbitView) getView();
      addPanToAnimator(view.getCenterPosition(), centerPos, view.getHeading(), heading, view.getPitch(), pitch, view.getZoom(),
               zoom, endCenterOnSurface);
      getView().firePropertyChange(AVKey.VIEW, null, getView());
   }


   public void addPanToAnimator(final Position centerPos,
                                final Angle heading,
                                final Angle pitch,
                                final double zoom) {
      final OrbitView view = (OrbitView) getView();
      addPanToAnimator(view.getCenterPosition(), centerPos, view.getHeading(), heading, view.getPitch(), pitch, view.getZoom(),
               zoom, false);
      getView().firePropertyChange(AVKey.VIEW, null, getView());
   }


   public void addEyePositionAnimator(final long timeToIterate,
                                      final Position beginPosition,
                                      final Position endPosition) {
      final PositionAnimator eyePosAnimator = ViewUtil.createEyePositionAnimator(getView(), timeToIterate, beginPosition,
               endPosition);
      _gotoAnimControl.put(VIEW_ANIM_POSITION, eyePosAnimator);
      getView().firePropertyChange(AVKey.VIEW, null, getView());
   }


   public void addHeadingAnimator(final Angle begin,
                                  final Angle end) {
      _gotoAnimControl.remove(VIEW_ANIM_HEADING_PITCH);
      final AngleAnimator headingAnimator = ViewUtil.createHeadingAnimator(getView(), begin, end);
      _gotoAnimControl.put(VIEW_ANIM_HEADING, headingAnimator);
      getView().firePropertyChange(AVKey.VIEW, null, getView());
   }


   public void addPitchAnimator(final Angle begin,
                                final Angle end) {
      _gotoAnimControl.remove(VIEW_ANIM_HEADING_PITCH);
      final AngleAnimator pitchAnimator = ViewUtil.createPitchAnimator(getView(), begin, end);
      _gotoAnimControl.put(VIEW_ANIM_PITCH, pitchAnimator);
      getView().firePropertyChange(AVKey.VIEW, null, getView());
   }


   public void addHeadingPitchAnimator(final Angle beginHeading,
                                       final Angle endHeading,
                                       final Angle beginPitch,
                                       final Angle endPitch) {
      _gotoAnimControl.remove(VIEW_ANIM_PITCH);
      _gotoAnimControl.remove(VIEW_ANIM_HEADING);
      final CompoundAnimator headingPitchAnimator = ViewUtil.createHeadingPitchAnimator(getView(), beginHeading, endHeading,
               beginPitch, endPitch);
      _gotoAnimControl.put(VIEW_ANIM_HEADING_PITCH, headingPitchAnimator);
      getView().firePropertyChange(AVKey.VIEW, null, getView());
   }


   public void addZoomAnimator(final double zoomStart,
                               final double zoomEnd) {
      final long DEFAULT_LENGTH_MILLIS = 4000;
      final DoubleAnimator zoomAnimator = new DoubleAnimator(new ScheduledInterpolator(DEFAULT_LENGTH_MILLIS), zoomStart,
               zoomEnd, OrbitViewPropertyAccessor.createZoomAccessor(((OrbitView) getView())));
      _gotoAnimControl.put(VIEW_ANIM_ZOOM, zoomAnimator);
      getView().firePropertyChange(AVKey.VIEW, null, getView());
   }


   public void addFlyToZoomAnimator(final Angle heading,
                                    final Angle pitch,
                                    final double zoom) {
      if ((heading == null) || (pitch == null)) {
         final String message = Logging.getMessage("nullValue.AngleIsNull");
         Logging.logger().severe(message);
         throw new IllegalArgumentException(message);
      }

      final View view = getView();
      if (view instanceof OrbitView) {
         final OrbitView customView = (OrbitView) view;
         final Angle beginHeading = customView.getHeading();
         final Angle beginPitch = customView.getPitch();
         final double beginZoom = customView.getZoom();
         final long MIN_LENGTH_MILLIS = 1000;
         final long MAX_LENGTH_MILLIS = 8000;
         final long lengthMillis = AnimationSupport.getScaledTimeMillisecs(beginZoom, zoom, MIN_LENGTH_MILLIS, MAX_LENGTH_MILLIS);
         final DoubleAnimator zoomAnimator = new DoubleAnimator(new ScheduledInterpolator(lengthMillis), beginZoom, zoom,
                  GCustomViewPropertyAccessor.createZoomAccessor(customView));
         final AngleAnimator headingAnimator = new AngleAnimator(new ScheduledInterpolator(lengthMillis), beginHeading, heading,
                  ViewPropertyAccessor.createHeadingAccessor(customView));
         final AngleAnimator pitchAnimator = new AngleAnimator(new ScheduledInterpolator(lengthMillis), beginPitch, pitch,
                  ViewPropertyAccessor.createPitchAccessor(customView));

         _gotoAnimControl.put(VIEW_ANIM_ZOOM, zoomAnimator);
         _gotoAnimControl.put(VIEW_ANIM_HEADING, headingAnimator);
         _gotoAnimControl.put(VIEW_ANIM_PITCH, pitchAnimator);
         customView.firePropertyChange(AVKey.VIEW, null, customView);
      }
   }


   public void addCenterAnimator(final Position begin,
                                 final Position end,
                                 final boolean smoothed) {
      if ((begin == null) || (end == null)) {
         final String message = Logging.getMessage("nullValue.PositionIsNull");
         Logging.logger().fine(message);
         throw new IllegalArgumentException(message);
      }

      final View view = getView();
      if (view instanceof OrbitView) {
         // TODO: length-scaling factory function
         final long DEFAULT_LENGTH_MILLIS = 4000;
         addCenterAnimator(begin, end, DEFAULT_LENGTH_MILLIS, smoothed);
      }
   }


   public void addCenterAnimator(final Position begin,
                                 final Position end,
                                 final long lengthMillis,
                                 final boolean smoothed) {
      if ((begin == null) || (end == null)) {
         final String message = Logging.getMessage("nullValue.PositionIsNull");
         Logging.logger().fine(message);
         throw new IllegalArgumentException(message);
      }

      final View view = getView();
      if (view instanceof OrbitView) {
         final OrbitView customView = (OrbitView) view;
         Interpolator interpolator;
         if (smoothed) {
            interpolator = new SmoothInterpolator(lengthMillis);
         }
         else {
            interpolator = new ScheduledInterpolator(lengthMillis);
         }
         final Animator centerAnimator = new PositionAnimator(interpolator, begin, end,
                  GCustomViewPropertyAccessor.createCenterPositionAccessor(customView));
         _gotoAnimControl.put(VIEW_ANIM_CENTER, centerAnimator);
         customView.firePropertyChange(AVKey.VIEW, null, customView);
      }
   }


   @Override
   public void goTo(final Position lookAtPos,
                    final double distance) {
      final OrbitView view = (OrbitView) getView();
      stopAnimators();
      addPanToAnimator(lookAtPos, view.getHeading(), view.getPitch(), distance, true);
      getView().firePropertyChange(AVKey.VIEW, null, getView());
   }


   public void jumpTo(final Position lookAtPos,
                      final double distance) {
      final OrbitView view = (OrbitView) getView();
      stopAnimators();
      view.setCenterPosition(new Position(lookAtPos.latitude, lookAtPos.longitude, view.getGlobe().getElevation(
               lookAtPos.latitude, lookAtPos.longitude)));
      view.setZoom(distance);
      getView().firePropertyChange(AVKey.VIEW, null, getView());
   }


   ////////////////////////////////////////////////////////


   @Override
   public void addAnimator(final Animator animator) {
      final long date = new Date().getTime();
      _gotoAnimControl.put(VIEW_ANIM_APP + date, animator);
   }


   @Override
   public boolean isAnimating() {
      return (_uiAnimControl.hasActiveAnimation() || _gotoAnimControl.hasActiveAnimation());
   }


   @Override
   public void stopAnimators() {
      _uiAnimControl.stopAnimations();
      _gotoAnimControl.stopAnimations();
   }


   /**
    * Apply the changes prior to rendering a frame. The method will step animators, applying the results of those steps to the
    * View, then if a focus on terrain is required, it will do that as well.
    * 
    **/
   @Override
   public void apply() {
      super.apply();

      final View view = getView();
      if (view == null) {
         return;
      }

      if (_gotoAnimControl.stepAnimators()) {
         view.firePropertyChange(AVKey.VIEW, null, view);
      }
      else {
         _gotoAnimControl.clear();
      }

      if (_uiAnimControl.stepAnimators()) {
         view.firePropertyChange(AVKey.VIEW, null, view);
      }
      else {
         _uiAnimControl.clear();
      }
   }


   /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
   // Custom input Event handling
   //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
   @Override
   protected void handleMouseWheelMoved(final MouseWheelEvent e) {
      boolean eventHandled = false;

      final View view = getView();
      if (view instanceof GCustomView) {
         final GInputState inputState = ((GCustomView) view).getInputState();
         if ((inputState != null) && (inputState.isPanoramicZoom())) {
            eventHandled = onChangeFieldOfView(e, view);
         }
      }

      if (!eventHandled) {
         super.handleMouseWheelMoved(e);
      }
   }


   @Override
   protected void handleKeyPressed(final KeyEvent e) {
      boolean eventHandled = false;

      if (e.getKeyCode() == 27) { // Escape
         final View view = getView();
         if (view instanceof GCustomView) {
            final GInputState inputState = ((GCustomView) view).getInputState();
            if (inputState == GInputState.PANORAMICS) {
               eventHandled = onExitPanoramic(view);
            }
         }
      }

      if (!eventHandled) {
         super.handleKeyPressed(e);
      }
   }


   private boolean onChangeFieldOfView(final MouseWheelEvent e,
                                       final View view) {


      final double oldFov = view.getFieldOfView().degrees;
      final double newFov = oldFov + (e.getWheelRotation() * 1.25);

      view.setFieldOfView(Angle.fromDegrees(GMath.clamp(newFov, 10, 110)));
      e.consume();

      return true;
   }


   private boolean onExitPanoramic(final View view) {
      final GCustomView customView = (GCustomView) view;
      final GGlobeApplication application = GGlobeApplication.instance();
      final I3DContentCollectionLayer panoramicLayer = application.getContentCollectionLayer();
      if (panoramicLayer != null) {
         panoramicLayer.exitContent(customView);
      }
      application.redraw();

      //System.out.println(customView.getRestorableState());
      //customView.goTo(new Position(Angle.fromDegrees(39.4737), Angle.fromDegrees(-6.3710), 270000), 270000);

      return true;
   }


}
