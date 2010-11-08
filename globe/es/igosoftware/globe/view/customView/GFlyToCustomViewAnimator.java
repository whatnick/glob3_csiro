package es.igosoftware.globe.view.customView;

import gov.nasa.worldwind.animation.AngleAnimator;
import gov.nasa.worldwind.animation.AnimationSupport;
import gov.nasa.worldwind.animation.CompoundAnimator;
import gov.nasa.worldwind.animation.DoubleAnimator;
import gov.nasa.worldwind.animation.Interpolator;
import gov.nasa.worldwind.animation.PositionAnimator;
import gov.nasa.worldwind.animation.ScheduledInterpolator;
import gov.nasa.worldwind.geom.Angle;
import gov.nasa.worldwind.geom.LatLon;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.globes.Globe;
import gov.nasa.worldwind.util.PropertyAccessor;
import gov.nasa.worldwind.view.ViewElevationAnimator;
import gov.nasa.worldwind.view.ViewPropertyAccessor;
import gov.nasa.worldwind.view.orbit.OrbitView;

public class GFlyToCustomViewAnimator
         extends
            CompoundAnimator {

   boolean                   _endCenterOnSurface;
   OnSurfacePositionAnimator _centerAnimator;
   ViewElevationAnimator     _zoomAnimator;
   AngleAnimator             _headingAnimator;
   AngleAnimator             _pitchAnimator;
   GCustomView               _customView;


   public GFlyToCustomViewAnimator(final OrbitView orbitView,
                                   final Interpolator interpol,
                                   final boolean endCenterOnSurface,
                                   final PositionAnimator centerAnimator,
                                   final DoubleAnimator zoomAnimator,
                                   final AngleAnimator headingAnimator,
                                   final AngleAnimator pitchAnimator) {
      super(interpol, centerAnimator, zoomAnimator, headingAnimator, pitchAnimator);
      this._customView = (GCustomView) orbitView;
      this._centerAnimator = (OnSurfacePositionAnimator) centerAnimator;
      this._zoomAnimator = (ViewElevationAnimator) zoomAnimator;
      this._headingAnimator = headingAnimator;
      this._pitchAnimator = pitchAnimator;
      if (interpol == null) {
         this.interpolator = new ScheduledInterpolator(10000);
      }
      this._endCenterOnSurface = endCenterOnSurface;
   }


   public static GFlyToCustomViewAnimator createFlyToCustomViewAnimator(final OrbitView customView,
                                                                        final Position beginCenterPos,
                                                                        final Position endCenterPos,
                                                                        final Angle beginHeading,
                                                                        final Angle endHeading,
                                                                        final Angle beginPitch,
                                                                        final Angle endPitch,
                                                                        final double beginZoom,
                                                                        final double endZoom,
                                                                        final long timeToMove,
                                                                        final boolean endCenterOnSurface) {

      final OnSurfacePositionAnimator centerAnimator = new OnSurfacePositionAnimator(customView.getGlobe(),
               new ScheduledInterpolator(timeToMove), beginCenterPos, endCenterPos,
               GCustomViewPropertyAccessor.createCenterPositionAccessor(customView), endCenterOnSurface);

      final ViewElevationAnimator zoomAnimator = new ViewElevationAnimator(customView.getGlobe(), beginZoom, endZoom,
               beginCenterPos, endCenterPos, GCustomViewPropertyAccessor.createZoomAccessor(customView));

      centerAnimator.useMidZoom = zoomAnimator.getUseMidZoom();

      final AngleAnimator headingAnimator = new AngleAnimator(new ScheduledInterpolator(timeToMove), beginHeading, endHeading,
               ViewPropertyAccessor.createHeadingAccessor(customView));

      final AngleAnimator pitchAnimator = new AngleAnimator(new ScheduledInterpolator(timeToMove), beginPitch, endPitch,
               ViewPropertyAccessor.createPitchAccessor(customView));

      final GFlyToCustomViewAnimator panAnimator = new GFlyToCustomViewAnimator(customView,
               new ScheduledInterpolator(timeToMove), endCenterOnSurface, centerAnimator, zoomAnimator, headingAnimator,
               pitchAnimator);

      return (panAnimator);
   }

   private static class OnSurfacePositionAnimator
            extends
               PositionAnimator {
      Globe   globe;
      boolean endCenterOnSurface;
      boolean useMidZoom = true;


      public OnSurfacePositionAnimator(final Globe globus,
                                       final Interpolator interpol,
                                       final Position beginPos,
                                       final Position endPos,
                                       final PropertyAccessor.PositionAccessor propertyAcc,
                                       final boolean isEndCenterOnSurface) {
         super(interpol, beginPos, endPos, propertyAcc);
         this.globe = globus;
         this.endCenterOnSurface = isEndCenterOnSurface;
      }


      @Override
      protected Position nextPosition(final double interpolant) {

         final int MAX_SMOOTHING = 1;

         final double CENTER_START = this.useMidZoom ? 0.2 : 0.0;
         final double CENTER_STOP = this.useMidZoom ? 0.8 : 0.8;
         final double latLonInterpolant = AnimationSupport.basicInterpolant(interpolant, CENTER_START, CENTER_STOP, MAX_SMOOTHING);

         // Invoke the standard next position functionality.
         Position pos = super.nextPosition(latLonInterpolant);

         // If the caller has flagged endCenterOnSurface, then we override endPosition's elevation with
         // the surface elevation.
         if (endCenterOnSurface) {
            // Use interpolated lat/lon.
            final LatLon ll = pos;
            // Override end position elevation with surface elevation at end lat/lon.
            final double e1 = getBegin().getElevation();
            final double e2 = globe.getElevation(getEnd().getLatitude(), getEnd().getLongitude());
            pos = new Position(ll, (1 - latLonInterpolant) * e1 + latLonInterpolant * e2);
         }

         return pos;
      }
   }


   @Override
   public void stop() {
      if (_endCenterOnSurface) {
         _customView.setViewOutOfFocus(true);
      }
      super.stop();

   }

}
