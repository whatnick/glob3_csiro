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

import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.util.PropertyAccessor;
import gov.nasa.worldwind.view.ViewPropertyAccessor;
import gov.nasa.worldwind.view.orbit.OrbitView;


public class GCustomViewPropertyAccessor
         extends
            ViewPropertyAccessor {

   private GCustomViewPropertyAccessor() {
   }


   public static PropertyAccessor.PositionAccessor createCenterPositionAccessor(final OrbitView view) {
      return new CenterPositionAccessor(view);
   }


   public static PropertyAccessor.DoubleAccessor createZoomAccessor(final OrbitView view) {
      return new ZoomAccessor(view);
   }

   private static class CenterPositionAccessor
            implements
               PropertyAccessor.PositionAccessor {
      private final OrbitView _customView;


      public CenterPositionAccessor(final OrbitView view) {
         this._customView = view;
      }


      @Override
      public Position getPosition() {
         if (this._customView == null) {
            return null;
         }

         return _customView.getCenterPosition();

      }


      @Override
      public boolean setPosition(final Position value) {
         //noinspection SimplifiableIfStatement
         if ((this._customView == null) || (value == null)) {
            return false;
         }


         try {

            this._customView.setCenterPosition(value);
            return true;
         }
         catch (final Exception e) {
            return false;
         }
      }
   }

   private static class ZoomAccessor
            implements
               PropertyAccessor.DoubleAccessor {
      OrbitView _customView;


      public ZoomAccessor(final OrbitView customView) {
         this._customView = customView;
      }


      @Override
      public final Double getDouble() {
         if (this._customView == null) {
            return null;
         }

         return this._customView.getZoom();

      }


      @Override
      public final boolean setDouble(final Double value) {
         //noinspection SimplifiableIfStatement
         if ((this._customView == null) || (value == null)) {
            return false;
         }

         try {
            this._customView.setZoom(value);
            return true;

         }
         catch (final Exception e) {
            return false;
         }
      }
   }

}
