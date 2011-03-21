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


package es.igosoftware.globe.layers;

import es.igosoftware.euclid.IBoundedGeometry;
import es.igosoftware.euclid.bounding.GAxisAlignedRectangle;
import es.igosoftware.euclid.features.IGlobeFeature;
import es.igosoftware.euclid.projection.GProjection;
import es.igosoftware.euclid.vector.GVector2D;
import es.igosoftware.euclid.vector.IVector2;
import gov.nasa.worldwind.geom.Angle;
import gov.nasa.worldwind.geom.LatLon;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.globes.Globe;
import gov.nasa.worldwind.render.Material;
import gov.nasa.worldwind.render.Renderable;
import gov.nasa.worldwind.render.markers.BasicMarkerAttributes;
import gov.nasa.worldwind.render.markers.BasicMarkerShape;
import gov.nasa.worldwind.render.markers.MarkerAttributes;

import java.awt.Color;


public class GPoints2RenderingTheme
         extends
            GVector2RenderingTheme {


   public static enum AltitudeMethod {
      CLAMPED_TO_GROUND,
      RELATIVE_TO_GROUND,
      ABSOLUTE;
   }


   public static enum TakeAltitude {
      FROM_FIXED,
      FROM_FIELD;
   }


   private double                                _size           = 10;
   private String                                _shapeType      = BasicMarkerShape.SPHERE;
   private double                                _fixedAltitude  = 0;
   private int                                   _altitudeField  = 0;
   private GPoints2RenderingTheme.AltitudeMethod _altitudeMethod = GPoints2RenderingTheme.AltitudeMethod.CLAMPED_TO_GROUND;
   private GPoints2RenderingTheme.TakeAltitude   _altitudeOrigin = GPoints2RenderingTheme.TakeAltitude.FROM_FIXED;


   public GPoints2RenderingTheme() {
      super();
   }


   @Override
   protected Renderable[] getRenderables(final IGlobeFeature<IVector2<?>, GAxisAlignedRectangle> feature,
                                         final GProjection projection,
                                         final Globe globe) {

      final IBoundedGeometry<IVector2<?>, ?, GAxisAlignedRectangle> geom = feature.getDefaultGeometry();
      final IVector2<?> coord = geom.getBounds()._center;

      LatLon latlon;
      if (projection.equals(GProjection.EPSG_4326)) {
         //if WGS84, we do not transform, but assume that the coordinates are in degrees
         //(as opposed to the coordinates given by proj4, which are radians)
         latlon = new LatLon(Angle.fromDegrees(coord.y()), Angle.fromDegrees(coord.x()));
      }
      else {
         final IVector2<?> transformedPt = projection.transformPoint(GProjection.EPSG_4326, new GVector2D(coord.x(), coord.y()));
         latlon = new LatLon(Angle.fromRadians(transformedPt.y()), Angle.fromRadians(transformedPt.x()));
      }


      double altitude = globe.getElevation(latlon.latitude, latlon.longitude) + 1;


      final Color color;
      if (_coloringMethod == GVector2RenderingTheme.ColoringMethod.COLOR_RAMP) {
         final double value = ((Number) feature.getAttribute(_fieldIndex)).doubleValue();
         color = new Color(getColorFromColorRamp(value));
      }
      else {
         color = _color;
      }


      if (_altitudeMethod == AltitudeMethod.CLAMPED_TO_GROUND) {

      }
      else {
         if (_altitudeOrigin == TakeAltitude.FROM_FIELD) {
            try {
               altitude = ((Number) feature.getAttribute(_altitudeField)).doubleValue();
            }
            catch (final Exception e) {
               altitude = globe.getElevation(latlon.latitude, latlon.longitude) + 1;
            }
         }
         else if (_altitudeOrigin == TakeAltitude.FROM_FIXED) {
            altitude = _fixedAltitude;
         }
      }

      if (_altitudeOrigin == TakeAltitude.FROM_FIELD) {
         altitude += globe.getElevation(latlon.latitude, latlon.longitude);
      }

      final MarkerAttributes markerAttributes = new BasicMarkerAttributes(new Material(color), _shapeType, 1, _size, _size);

      return new Renderable[] { new RenderableMarker(new Position(latlon, altitude), markerAttributes) };

   }


   public double getSize() {
      return _size;
   }


   public void setSize(final double size) {
      _size = size;
   }


   public String getShapeType() {
      return _shapeType;
   }


   public void setShapeType(final String shapeType) {
      _shapeType = shapeType;
   }


   public double getFixedAltitude() {
      return _fixedAltitude;
   }


   public void setFixedAltitude(final double fixedAltitude) {
      _fixedAltitude = fixedAltitude;
   }


   public int getAltitudeField() {
      return _altitudeField;
   }


   public void setAltitudeField(final int altitudeField) {
      _altitudeField = altitudeField;
   }


   public AltitudeMethod getAltitudeMethod() {
      return _altitudeMethod;
   }


   public void setAltitudeMethod(final AltitudeMethod altitudeMethod) {
      _altitudeMethod = altitudeMethod;
   }


   public TakeAltitude getAltitudeOrigin() {
      return _altitudeOrigin;
   }


   public void setAltitudeOrigin(final TakeAltitude altitudeOrigin) {
      _altitudeOrigin = altitudeOrigin;
   }

}
