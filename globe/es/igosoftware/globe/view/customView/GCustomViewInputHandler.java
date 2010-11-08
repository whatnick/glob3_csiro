package es.igosoftware.globe.view.customView;

import es.igosoftware.globe.GGlobeApplication;
import es.igosoftware.globe.GPanoramicLayer;
import es.igosoftware.globe.view.GInputState;
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

   protected AnimationController gotoAnimControl         = new AnimationController();
   protected AnimationController uiAnimControl           = new AnimationController();
   protected static final String VIEW_ANIM_HEADING       = "ViewAnimHeading";
   protected static final String VIEW_ANIM_PITCH         = "ViewAnimPitch";
   protected static final String VIEW_ANIM_HEADING_PITCH = "ViewAnimHeadingPitch";
   protected static final String VIEW_ANIM_POSITION      = "ViewAnimPosition";
   protected static final String VIEW_ANIM_CENTER        = "ViewAnimCenter";
   protected static final String VIEW_ANIM_ZOOM          = "ViewAnimZoom";
   protected static final String VIEW_ANIM_PAN           = "ViewAnimPan";
   protected static final String VIEW_ANIM_APP           = "ViewAnimApp";


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

      final View view = this.getView();
      if (view == null) // include this test to ensure any derived implementation performs it
      {
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
      this.stopAllAnimators();
      if (view instanceof OrbitView) {

         // We're treating a speed parameter as smoothing here. A greater speed results in greater smoothing and
         // slower response. Therefore the min speed used at lower altitudes ought to be *greater* than the max
         // speed used at higher altitudes.
         //double[] values = actionAttribs.getValues();
         double smoothing = this.getScaleValueZoom(actionAttribs);
         if (!actionAttribs.isEnableSmoothing()) {
            smoothing = 0.0;
         }

         final GCustomViewCenterAnimator centerAnimator = new GCustomViewCenterAnimator((GCustomView) this.getView(),
                  view.getEyePosition(), focalPosition, smoothing,
                  GCustomViewPropertyAccessor.createCenterPositionAccessor((OrbitView) view), true);
         this.gotoAnimControl.put(VIEW_ANIM_CENTER, centerAnimator);
         view.firePropertyChange(AVKey.VIEW, null, view);
      }
   }


   @Override
   protected void onHorizontalTranslateAbs(final Angle latitudeChange,
                                           final Angle longitudeChange,
                                           final ViewInputAttributes.ActionAttributes actionAttribs) {

      this.stopGoToAnimators();
      this.stopUserInputAnimators(VIEW_ANIM_HEADING, VIEW_ANIM_PITCH, VIEW_ANIM_ZOOM);

      final View view = this.getView();
      if (view == null) // include this test to ensure any derived implementation performs it
      {
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

         this.setCenterPosition((GCustomView) view, uiAnimControl, newPosition, actionAttribs);
      }
   }


   @Override
   protected void onHorizontalTranslateRel(double forwardInput,
                                           double sideInput,
                                           final double totalForwardInput,
                                           final double totalSideInput,
                                           final ViewInputAttributes.DeviceAttributes deviceAttributes,
                                           final ViewInputAttributes.ActionAttributes actionAttributes) {
      final View view = this.getView();
      GInputState inputState = null;
      if (view instanceof GCustomView) {
         inputState = ((GCustomView) view).getInputState();
      }
      if ((inputState != null) && (!inputState.isMoving())) {
         System.out.println("No Movement allowed!!");
         return;
      }


      //System.out.println("onHorizontalTranslateRel(6 args)");
      this.stopGoToAnimators();
      this.stopUserInputAnimators(VIEW_ANIM_HEADING, VIEW_ANIM_PITCH, VIEW_ANIM_ZOOM);

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
      final View view = this.getView();
      if (view == null) // include this test to ensure any derived implementation performs it
      {
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

         this.setCenterPosition((GCustomView) view, this.uiAnimControl, newPosition, actionAttribs);
      }

   }


   @Override
   protected void onResetHeading(final ViewInputAttributes.ActionAttributes actionAttribs) {
      //System.out.println("onResetHeading");
      this.stopAllAnimators();

      final View view = this.getView();
      if (view == null) // include this test to ensure any derived implementation performs it
      {
         return;
      }
      this.addHeadingAnimator(view.getHeading(), Angle.ZERO);
   }


   @Override
   protected void onResetHeadingAndPitch(final ViewInputAttributes.ActionAttributes actionAttribs) {
      //System.out.println("onResetHeadingAndPitch");
      this.stopAllAnimators();

      final View view = this.getView();
      if (view == null) // include this test to ensure any derived implementation performs it
      {
         return;
      }

      this.addHeadingPitchAnimator(view.getHeading(), Angle.ZERO, view.getPitch(), Angle.ZERO);
   }


   @Override
   protected void onRotateView(final Angle headingChange,
                               final Angle pitchChange,
                               final ActionAttributes actionAttribs) {
      //System.out.println("onRotateView (3args)");
      final View view = this.getView();
      if (view == null) // include this test to ensure any derived implementation performs it
      {
         return;
      }

      if (view instanceof GCustomView) {
         if (!headingChange.equals(Angle.ZERO)) {
            this.changeHeading((GCustomView) view, uiAnimControl, headingChange, actionAttribs);
         }

         if (!pitchChange.equals(Angle.ZERO)) {
            this.changePitch((GCustomView) view, uiAnimControl, pitchChange, actionAttribs);
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
      this.stopGoToAnimators();
      this.stopUserInputAnimators(VIEW_ANIM_CENTER, VIEW_ANIM_ZOOM);

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
      //System.out.println("onVerticalTranslate (4 args)");
      this.stopGoToAnimators();
      this.stopUserInputAnimators(VIEW_ANIM_CENTER, VIEW_ANIM_HEADING, VIEW_ANIM_PITCH);

      final double zoomChange = translateChange * getScaleValueRotate(actionAttributes);
      onVerticalTranslate(zoomChange, actionAttributes);

   }


   @Override
   protected void onVerticalTranslate(final double translateChange,
                                      final ViewInputAttributes.ActionAttributes actionAttribs) {
      //System.out.println("onVerticalTranslate (2 args)");
      final View view = this.getView();
      if (view == null) // include this test to ensure any derived implementation performs it
      {
         return;
      }

      if (translateChange == 0) {
         return;
      }
      if (view instanceof GCustomView) {
         this.changeZoom((GCustomView) view, uiAnimControl, translateChange, actionAttribs);
      }
   }


   //**************************************************************//
   //********************  Property Change Events  ****************//
   //**************************************************************//

   @Override
   protected void handlePropertyChange(final java.beans.PropertyChangeEvent e) {
      super.handlePropertyChange(e);

      //noinspection StringEquality
      if (e.getPropertyName() == OrbitView.CENTER_STOPPED) {
         this.handleCustomViewCenterStopped();
      }
   }


   protected void stopAllAnimators() {
      // Explicitly stop all animators, then clear the data structure which holds them. If we remove an animator
      // from this data structure without invoking stop(), the animator has no way of knowing it was forcibly stopped.
      // An animator's owner - potentially an object other than this ViewInputHandler - may need to know if an
      // animator has been forcibly stopped in order to react correctly to that event.
      this.uiAnimControl.stopAnimations();
      this.gotoAnimControl.stopAnimations();
      this.uiAnimControl.clear();
      this.gotoAnimControl.clear();

      final View view = this.getView();
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
      this.gotoAnimControl.stopAnimations();
      this.gotoAnimControl.clear();
   }


   protected void stopUserInputAnimators(final Object... names) {
      for (final Object o : names) {
         if (this.uiAnimControl.get(o) != null) {
            // Explicitly stop the 'ui' animator, then clear it from the data structure which holds it. If we remove
            // an animator from this data structure without invoking stop(), the animator has no way of knowing it
            // was forcibly stopped. Though applications cannot access the 'ui' animator data structure, stopping
            // the animators here is the correct action.
            this.uiAnimControl.get(o).stop();
            this.uiAnimControl.remove(o);
         }
      }
   }


   @Override
   protected void handleViewStopped() {
      this.stopAllAnimators();
   }


   protected void handleCustomViewCenterStopped() {
      // The "center stopped" message instructs components to stop modifying the OrbitView's center position.
      // Therefore we stop any center position animations started by this view controller.
      this.stopUserInputAnimators(VIEW_ANIM_CENTER);
   }


   //**************************************************************//
   //********************  View State Change Utilities  ***********//
   //**************************************************************//
   protected void setCenterPosition(final GCustomView view,
                                    final AnimationController animControl,
                                    final Position position,
                                    final ViewInputAttributes.ActionAttributes attrib) {
      double smoothing = attrib.getSmoothingValue();
      if (!(attrib.isEnableSmoothing() && this.isEnableSmoothing())) {
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
            centerAnimator = new GCustomViewCenterAnimator((GCustomView) this.getView(), cur, newPosition, smoothing,
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
      if (!(attrib.isEnableSmoothing() && this.isEnableSmoothing())) {
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
      if (!(attrib.isEnableSmoothing() && this.isEnableSmoothing())) {
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
      if (!(attrib.isEnableSmoothing() && this.isEnableSmoothing())) {
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
      return GCustomViewLimits.limitHeading(newHeading, limits);
   }


   protected static Angle computeNewPitch(final Angle pitch,
                                          final OrbitViewLimits limits) {
      final Angle newHeading = GCustomView.normalizedPitch(pitch);
      return GCustomViewLimits.limitPitch(newHeading, limits);
   }


   protected static double computeNewZoom(final double curZoom,
                                          final double change,
                                          final OrbitViewLimits limits) {
      final double logCurZoom = curZoom != 0 ? Math.log(curZoom) : 0;
      final double newZoom = Math.exp(logCurZoom + change);
      return GCustomViewLimits.limitZoom(newZoom, limits);
   }

   //**************************************************************//
   //********************  Input Handler Property Accessors  ******//
   //**************************************************************//

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

         this._customView = customView;
      }


      /**
       * Returns the pitch property value from this accessor's view.
       * 
       * @return the pitch from this accessor's view.
       */
      @Override
      public Angle getAngle() {
         return this._customView.getPitch();
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
         if (this._customView.isDetectCollisions()) {
            this._customView.hadCollisions();
         }

         try {
            this._customView.setPitch(value);
         }
         catch (final Exception e) {
            final String message = Logging.getMessage("generic.ExceptionWhileChangingView");
            Logging.logger().log(java.util.logging.Level.SEVERE, message, e);
            return false;
         }

         // If the view supports surface collision detection, then return false if the collision flag is set,
         // otherwise return true.
         return !(this._customView.isDetectCollisions() && this._customView.hadCollisions());
      }
   }


   //**************************************************************//
   //********************  Scaling Utilities  *********************//
   //**************************************************************//
   protected double getScaleValueHorizTransRel(final ViewInputAttributes.DeviceAttributes deviceAttributes,
                                               final ViewInputAttributes.ActionAttributes actionAttributes) {

      final View view = this.getView();
      if (view == null) {
         return 0.0;
      }
      if (view instanceof OrbitView) {
         final double[] range = actionAttributes.getValues();
         // If this is a CustomView, we use the zoom value to set the scale
         final double radius = this.getWorldWindow().getModel().getGlobe().getRadius();
         final double t = getScaleValue(range[0], range[1], ((OrbitView) view).getZoom(), 3.0 * radius, true);
         return (t);
      }

      // Any other view, use the base class scaling method
      return (super.getScaleValueElevation(deviceAttributes, actionAttributes));

   }


   protected double getScaleValueRotate(final ViewInputAttributes.ActionAttributes actionAttributes) {

      final View view = this.getView();
      if (view == null) {
         return 0.0;
      }
      if (view instanceof OrbitView) {
         final double[] range = actionAttributes.getValues();
         // If this is a CustomView, we use the zoom value to set the scale
         final double radius = this.getWorldWindow().getModel().getGlobe().getRadius();
         final double t = getScaleValue(range[0], range[1], ((OrbitView) view).getZoom(), 3.0 * radius, false);
         return (t);
      }
      return (1.0);
   }


   protected double getScaleValueZoom(final ViewInputAttributes.ActionAttributes actionAttributes) {
      final View view = this.getView();
      if (view == null) {
         return 0.0;
      }
      if (view instanceof OrbitView) {
         final double[] range = actionAttributes.getValues();
         // If this is a CustomView, we use the zoom value to set the scale
         final double radius = this.getWorldWindow().getModel().getGlobe().getRadius();
         double t = ((OrbitView) view).getZoom() / (3.0 * radius);
         t = (t < 0 ? 0 : (t > 1 ? 1 : t));
         return range[0] * (1.0 - t) + range[1] * t;
      }
      return (1.0);
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
      final OrbitView customView = (OrbitView) this.getView();
      final GFlyToCustomViewAnimator panAnimator = GFlyToCustomViewAnimator.createFlyToCustomViewAnimator(customView,
               beginCenterPos, endCenterPos, beginHeading, endHeading, beginPitch, endPitch, beginZoom, endZoom, timeToMove,
               endCenterOnSurface);


      this.gotoAnimControl.put(VIEW_ANIM_PAN, panAnimator);
      this.getView().firePropertyChange(AVKey.VIEW, null, this.getView());
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
      final OrbitView customView = (OrbitView) this.getView();
      final GFlyToCustomViewAnimator panAnimator = GFlyToCustomViewAnimator.createFlyToCustomViewAnimator(customView,
               beginCenterPos, endCenterPos, beginHeading, endHeading, beginPitch, endPitch, beginZoom, endZoom, timeToMove,
               endCenterOnSurface);


      this.gotoAnimControl.put(VIEW_ANIM_PAN, panAnimator);
      this.getView().firePropertyChange(AVKey.VIEW, null, this.getView());
   }


   public void addPanToAnimator(final Position centerPos,
                                final Angle heading,
                                final Angle pitch,
                                final double zoom,
                                final long timeToMove,
                                final boolean endCenterOnSurface) {
      final OrbitView view = (OrbitView) this.getView();
      addPanToAnimator(view.getCenterPosition(), centerPos, view.getHeading(), heading, view.getPitch(), pitch, view.getZoom(),
               zoom, timeToMove, endCenterOnSurface);
      this.getView().firePropertyChange(AVKey.VIEW, null, this.getView());
   }


   public void addPanToAnimator(final Position centerPos,
                                final Angle heading,
                                final Angle pitch,
                                final double zoom,
                                final boolean endCenterOnSurface) {
      final OrbitView view = (OrbitView) this.getView();
      addPanToAnimator(view.getCenterPosition(), centerPos, view.getHeading(), heading, view.getPitch(), pitch, view.getZoom(),
               zoom, endCenterOnSurface);
      this.getView().firePropertyChange(AVKey.VIEW, null, this.getView());
   }


   public void addPanToAnimator(final Position centerPos,
                                final Angle heading,
                                final Angle pitch,
                                final double zoom) {
      final OrbitView view = (OrbitView) this.getView();
      addPanToAnimator(view.getCenterPosition(), centerPos, view.getHeading(), heading, view.getPitch(), pitch, view.getZoom(),
               zoom, false);
      this.getView().firePropertyChange(AVKey.VIEW, null, this.getView());
   }


   public void addEyePositionAnimator(final long timeToIterate,
                                      final Position beginPosition,
                                      final Position endPosition) {
      final PositionAnimator eyePosAnimator = ViewUtil.createEyePositionAnimator(this.getView(), timeToIterate, beginPosition,
               endPosition);
      this.gotoAnimControl.put(VIEW_ANIM_POSITION, eyePosAnimator);
      this.getView().firePropertyChange(AVKey.VIEW, null, this.getView());
   }


   public void addHeadingAnimator(final Angle begin,
                                  final Angle end) {
      this.gotoAnimControl.remove(VIEW_ANIM_HEADING_PITCH);
      final AngleAnimator headingAnimator = ViewUtil.createHeadingAnimator(this.getView(), begin, end);
      this.gotoAnimControl.put(VIEW_ANIM_HEADING, headingAnimator);
      this.getView().firePropertyChange(AVKey.VIEW, null, this.getView());
   }


   public void addPitchAnimator(final Angle begin,
                                final Angle end) {
      this.gotoAnimControl.remove(VIEW_ANIM_HEADING_PITCH);
      final AngleAnimator pitchAnimator = ViewUtil.createPitchAnimator(this.getView(), begin, end);
      this.gotoAnimControl.put(VIEW_ANIM_PITCH, pitchAnimator);
      this.getView().firePropertyChange(AVKey.VIEW, null, this.getView());
   }


   public void addHeadingPitchAnimator(final Angle beginHeading,
                                       final Angle endHeading,
                                       final Angle beginPitch,
                                       final Angle endPitch) {
      this.gotoAnimControl.remove(VIEW_ANIM_PITCH);
      this.gotoAnimControl.remove(VIEW_ANIM_HEADING);
      final CompoundAnimator headingPitchAnimator = ViewUtil.createHeadingPitchAnimator(this.getView(), beginHeading, endHeading,
               beginPitch, endPitch);
      this.gotoAnimControl.put(VIEW_ANIM_HEADING_PITCH, headingPitchAnimator);
      this.getView().firePropertyChange(AVKey.VIEW, null, this.getView());
   }


   public void addZoomAnimator(final double zoomStart,
                               final double zoomEnd) {
      final long DEFAULT_LENGTH_MILLIS = 4000;
      final DoubleAnimator zoomAnimator = new DoubleAnimator(new ScheduledInterpolator(DEFAULT_LENGTH_MILLIS), zoomStart,
               zoomEnd, OrbitViewPropertyAccessor.createZoomAccessor(((OrbitView) this.getView())));
      this.gotoAnimControl.put(VIEW_ANIM_ZOOM, zoomAnimator);
      this.getView().firePropertyChange(AVKey.VIEW, null, this.getView());
   }


   public void addFlyToZoomAnimator(final Angle heading,
                                    final Angle pitch,
                                    final double zoom) {
      if ((heading == null) || (pitch == null)) {
         final String message = Logging.getMessage("nullValue.AngleIsNull");
         Logging.logger().severe(message);
         throw new IllegalArgumentException(message);
      }
      final View view = this.getView();
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

         this.gotoAnimControl.put(VIEW_ANIM_ZOOM, zoomAnimator);
         this.gotoAnimControl.put(VIEW_ANIM_HEADING, headingAnimator);
         this.gotoAnimControl.put(VIEW_ANIM_PITCH, pitchAnimator);
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

      final View view = this.getView();
      if (view instanceof OrbitView) {
         // TODO: length-scaling factory function
         final long DEFAULT_LENGTH_MILLIS = 4000;
         this.addCenterAnimator(begin, end, DEFAULT_LENGTH_MILLIS, smoothed);
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

      final View view = this.getView();
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
         this.gotoAnimControl.put(VIEW_ANIM_CENTER, centerAnimator);
         customView.firePropertyChange(AVKey.VIEW, null, customView);
      }
   }


   @Override
   public void goTo(final Position lookAtPos,
                    final double distance) {
      final OrbitView view = (OrbitView) this.getView();
      stopAnimators();
      addPanToAnimator(lookAtPos, view.getHeading(), view.getPitch(), distance, true);
      this.getView().firePropertyChange(AVKey.VIEW, null, this.getView());
   }


   public void jumpTo(final Position lookAtPos,
                      final double distance) {
      final OrbitView view = (OrbitView) this.getView();
      stopAnimators();
      view.setCenterPosition(new Position(lookAtPos.latitude, lookAtPos.longitude, view.getGlobe().getElevation(
               lookAtPos.latitude, lookAtPos.longitude)));
      view.setZoom(distance);
      this.getView().firePropertyChange(AVKey.VIEW, null, this.getView());
   }


   ////////////////////////////////////////////////////////


   @Override
   public void addAnimator(final Animator animator) {
      final long date = new Date().getTime();
      this.gotoAnimControl.put(VIEW_ANIM_APP + date, animator);

   }


   @Override
   public boolean isAnimating() {
      return (this.uiAnimControl.hasActiveAnimation() || this.gotoAnimControl.hasActiveAnimation());
   }


   @Override
   public void stopAnimators() {
      this.uiAnimControl.stopAnimations();
      this.gotoAnimControl.stopAnimations();

   }


   /**
    * Apply the changes prior to rendering a frame. The method will step animators, applying the results of those steps to the
    * View, then if a focus on terrain is required, it will do that as well.
    * 
    **/
   @Override
   public void apply() {
      super.apply();

      final View view = this.getView();
      if (view == null) {
         return;
      }

      if (this.gotoAnimControl.stepAnimators()) {
         view.firePropertyChange(AVKey.VIEW, null, view);
      }
      else {
         this.gotoAnimControl.clear();
      }

      if (this.uiAnimControl.stepAnimators()) {
         view.firePropertyChange(AVKey.VIEW, null, view);
      }
      else {
         this.uiAnimControl.clear();
      }
   }


   /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
   // Custom input Event handling
   //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
   @Override
   protected void handleMouseWheelMoved(final MouseWheelEvent e) {
      boolean eventHandled = false;


      final View view = this.getView();
      GInputState inputState = null;
      if (view instanceof GCustomView) {
         inputState = ((GCustomView) view).getInputState();
      }
      if ((inputState != null) && (inputState.isPanoramicZoom())) {
         eventHandled = onChangeFieldOfView(e, view);
      }


      if (!eventHandled) {
         super.handleMouseWheelMoved(e);
      }
   }


   @Override
   protected void handleKeyPressed(final KeyEvent e) {

      boolean eventHandled = false;

      //Escape
      if (e.getKeyCode() == 27) {
         final View view = this.getView();
         GInputState inputState = null;
         if (view instanceof GCustomView) {
            inputState = ((GCustomView) view).getInputState();
         }
         if (inputState == GInputState.PANORAMICS) {
            eventHandled = onExitPanoramic(view);
         }
      }

      if (!eventHandled) {
         super.handleKeyPressed(e);
      }


   }


   private boolean onChangeFieldOfView(final MouseWheelEvent e,
                                       final View view) {


      final double oldFov = view.getFieldOfView().degrees;
      double newFov = oldFov + e.getWheelRotation();
      if (newFov <= 20.0) {
         newFov = 20.0;
      }
      if (newFov >= 100.0) {
         newFov = 100.0;
      }
      view.setFieldOfView(Angle.fromDegrees(newFov));
      e.consume();


      return true;
   }


   private boolean onExitPanoramic(final View view) {


      final GCustomView customView = (GCustomView) view;
      final GGlobeApplication application = GGlobeApplication.instance();
      final GPanoramicLayer panoramicLayer = application.getPanoramicLayer();
      if (panoramicLayer != null) {
         panoramicLayer.exitPanoramic(customView);
      }
      application.redraw();


      //System.out.println(customView.getRestorableState());
      //customView.goTo(new Position(Angle.fromDegrees(39.4737), Angle.fromDegrees(-6.3710), 270000), 270000);


      return true;
   }

}
