package es.igosoftware.globe.view.customView;

import gov.nasa.worldwind.animation.MoveToDoubleAnimator;
import gov.nasa.worldwind.util.PropertyAccessor;

public class GCustomViewMoveToZoomAnimator
         extends
            MoveToDoubleAnimator {

   GCustomView _customView;
   boolean     _endCenterOnSurface;


   GCustomViewMoveToZoomAnimator(final GCustomView customView,
                                 final Double endValue,
                                 final double smoothingAmount,
                                 final PropertyAccessor.DoubleAccessor propertyAcc,
                                 final boolean endCenterOnSurface) {
      super(endValue, smoothingAmount, propertyAcc);
      this._customView = customView;
      this._endCenterOnSurface = endCenterOnSurface;
   }


   @Override
   protected void setImpl(final double interpolant) {
      final Double newValue = this.nextDouble(interpolant);
      if (newValue == null) {
         return;
      }

      this.propertyAccessor.setDouble(newValue);
   }


   @Override
   public Double nextDouble(final double interpolant) {
      final double newValue = (1 - interpolant) * propertyAccessor.getDouble() + interpolant * this.end;
      if (Math.abs(newValue - propertyAccessor.getDouble()) < minEpsilon) {
         this.stop();
         if (this._endCenterOnSurface) {
            _customView.setViewOutOfFocus(true);
         }
         return (null);
      }
      return newValue;
   }

}
