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
      _endCenterOnSurface = endCenterOnSurface;
      _customView = customView;
   }


   @Override
   public Position nextPosition(double interpolant) {
      Position nextPosition = end;
      final Position curCenter = propertyAccessor.getPosition();

      final double latlonDifference = LatLon.greatCircleDistance(nextPosition, curCenter).degrees;
      final double elevDifference = Math.abs(nextPosition.getElevation() - curCenter.getElevation());
      final boolean stopMoving = Math.max(latlonDifference, elevDifference) < positionMinEpsilon;
      if (!stopMoving) {
         interpolant = 1 - smoothing;
         nextPosition = new Position(Angle.mix(interpolant, curCenter.getLatitude(), end.getLatitude()), Angle.mix(interpolant,
                  curCenter.getLongitude(), end.getLongitude()), (1 - interpolant) * curCenter.getElevation() + interpolant
                                                                 * end.getElevation());
      }
      // If target is close, cancel future value changes.
      if (stopMoving) {
         stop();
         propertyAccessor.setPosition(nextPosition);
         if (_endCenterOnSurface) {
            _customView.setViewOutOfFocus(true);
         }
         return (null);
      }
      return nextPosition;
   }


   @Override
   protected void setImpl(final double interpolant) {
      final Position newValue = nextPosition(interpolant);
      if (newValue == null) {
         return;
      }

      propertyAccessor.setPosition(newValue);
      _customView.setViewOutOfFocus(true);
   }


   @Override
   public void stop() {
      super.stop();
   }

}
