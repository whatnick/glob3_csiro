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


package es.igosoftware.utils;

import es.igosoftware.euclid.bounding.GAxisAlignedRectangle;
import es.igosoftware.euclid.projection.GProjection;
import es.igosoftware.euclid.vector.IVector2;
import es.igosoftware.euclid.vector.IVector3;
import gov.nasa.worldwind.geom.Angle;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.geom.Sector;


public final class GConverter {
   private GConverter() {
   }


   public static Sector createSector(final GProjection sourceProjection,
                                     final GAxisAlignedRectangle rectangle) {
      //      final UTMCoord utmLower = GConverter.toUTM(sourceProjection, rectangle.lower);
      //      final UTMCoord utmUpper = GConverter.toUTM(sourceProjection, rectangle.upper);
      //      return new Sector(utmLower.getLatitude(), utmUpper.getLatitude(), utmLower.getLongitude(), utmUpper.getLongitude());

      final IVector2<?> geodesicLower = rectangle._lower.reproject(sourceProjection, GProjection.EPSG_4326);
      final IVector2<?> geodesicUpper = rectangle._upper.reproject(sourceProjection, GProjection.EPSG_4326);

      final Angle minLatitude = Angle.fromRadiansLatitude(geodesicLower.y());
      final Angle maxLatitude = Angle.fromRadiansLatitude(geodesicUpper.y());
      final Angle minLongitude = Angle.fromRadiansLongitude(geodesicLower.x());
      final Angle maxLongitude = Angle.fromRadiansLongitude(geodesicUpper.x());

      return new Sector(minLatitude, maxLatitude, minLongitude, maxLongitude);
   }


   public static Position toPosition(final GProjection sourceProjection,
                                     final IVector3<?> point) {
      final IVector3<?> geodesicPoint = point.reproject(sourceProjection, GProjection.EPSG_4326);
      return Position.fromRadians(geodesicPoint.y(), geodesicPoint.x(), geodesicPoint.z());
   }


}
