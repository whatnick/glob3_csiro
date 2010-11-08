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
