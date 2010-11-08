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


package es.igosoftware.globe;

import gov.nasa.worldwind.geom.Angle;
import gov.nasa.worldwind.geom.Position;


public class GCameraState {

   private final Position _eyePosition;
   private final Angle    _fov;


   private final Position _centerPosition;
   private final double   _zoom;


   //   private final double   _elevation;
   private final Angle    _pitch;
   private final Angle    _heading;


   //   public GCameraState(final Position eyePosition,
   //                       final double elevation,
   //                       final Angle fov,
   //                       final Angle pitch,
   //                       final Angle heading) {
   //      _eyePosition = eyePosition;
   //      _elevation = elevation;
   //      _fov = fov;
   //      _pitch = pitch;
   //      _heading = heading;
   //   }


   public GCameraState(final Position eyePosition,
                       final Position centerPosition,
                       final double zoom,
                       final Angle fov,
                       final Angle heading,
                       final Angle pitch) {

      _eyePosition = eyePosition;
      _centerPosition = centerPosition;
      _zoom = zoom;
      _fov = fov;
      _heading = heading;
      _pitch = pitch;

   }


   public Position getEyePosition() {
      return _eyePosition;
   }


   public Angle getFov() {
      return _fov;
   }


   public Position getCenterPosition() {
      return _centerPosition;
   }


   public double getZoom() {
      return _zoom;
   }


   //   public double getElevation() {
   //      return _elevation;
   //   }
   //
   //
   public Angle getPitch() {
      return _pitch;
   }


   //
   //
   public Angle getHeading() {
      return _heading;
   }

}
