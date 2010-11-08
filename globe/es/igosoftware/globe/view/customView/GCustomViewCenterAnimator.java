package es.igosoftware.globe.view.customView;

import gov.nasa.worldwind.animation.MoveToPositionAnimator;
import gov.nasa.worldwind.geom.Angle;
import gov.nasa.worldwind.geom.LatLon;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.util.PropertyAccessor;

public class GCustomViewCenterAnimator
         extends
            MoveToPositionAnimator {

   private final GCustomView _customView;
   boolean                   _endCenterOnSurface;


   public GCustomViewCenterAnimator(final GCustomView customView,
                                   final Position startPosition,
                                   final Position endPosition,
                                   final double smoothingAmount,
                                   final PropertyAccessor.PositionAccessor propertyAcc,
                                   final boolean endCenterOnSurface) {
      super(startPosition, endPosition, smoothingAmount, propertyAcc);
      this._endCenterOnSurface = endCenterOnSurface;
      this._customView = customView;
   }


   @Override
   public Position nextPosition(double interpolant) {
      Position nextPosition = this.end;
      final Position curCenter = this.propertyAccessor.getPosition();

      final double latlonDifference = LatLon.greatCircleDistance(nextPosition, curCenter).degrees;
      final double elevDifference = Math.abs(nextPosition.getElevation() - curCenter.getElevation());
      final boolean stopMoving = Math.max(latlonDifference, elevDifference) < this.positionMinEpsilon;
      if (!stopMoving) {
         interpolant = 1 - this.smoothing;
         nextPosition = new Position(Angle.mix(interpolant, curCenter.getLatitude(), this.end.getLatitude()), Angle.mix(
                  interpolant, curCenter.getLongitude(), this.end.getLongitude()), (1 - interpolant) * curCenter.getElevation()
                                                                                   + interpolant * this.end.getElevation());
      }
      // If target is close, cancel future value changes.
      if (stopMoving) {
         this.stop();
         this.propertyAccessor.setPosition(nextPosition);
         if (_endCenterOnSurface) {
            this._customView.setViewOutOfFocus(true);
         }
         return (null);
      }
      return nextPosition;
   }


   @Override
   protected void setImpl(final double interpolant) {
      final Position newValue = this.nextPosition(interpolant);
      if (newValue == null) {
         return;
      }

      this.propertyAccessor.setPosition(newValue);
      this._customView.setViewOutOfFocus(true);
   }


   @Override
   public void stop() {
      super.stop();
   }

}
