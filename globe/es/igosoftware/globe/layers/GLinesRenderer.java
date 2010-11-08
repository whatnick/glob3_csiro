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

import es.igosoftware.euclid.projection.GProjection;
import es.igosoftware.euclid.vector.GVector2D;
import es.igosoftware.euclid.vector.IVector2;
import gov.nasa.worldwind.geom.Angle;
import gov.nasa.worldwind.geom.LatLon;
import gov.nasa.worldwind.render.Polyline;
import gov.nasa.worldwind.render.Renderable;

import java.awt.Color;
import java.util.ArrayList;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;


public class GLinesRenderer
         extends
            GVectorRenderer {

   private int _lineThickness = 1;


   public GLinesRenderer(final Feature[] features) {

      super(features);
      // TODO Auto-generated constructor stub
   }


   @Override
   public Renderable[] getRenderables(final Feature feature,
                                      final GProjection crs) {

      try {
         final ArrayList<Polyline> polys = new ArrayList<Polyline>();
         final Geometry geom = feature._geometry;
         for (int i = 0; i < geom.getNumGeometries(); i++) {
            final ArrayList<LatLon> list = new ArrayList<LatLon>();
            for (int j = 0; j < geom.getNumPoints(); j++) {
               final Coordinate coord = geom.getCoordinates()[j];
               final IVector2<?> transformedPt = crs.transformPoint(GProjection.EPSG_4326, new GVector2D(coord.x, coord.y));
               final LatLon latlon = new LatLon(Angle.fromRadians(transformedPt.y()), Angle.fromRadians(transformedPt.x()));
               list.add(latlon);
            }
            final Polyline poly = new Polyline(list, 0);

            double dValue = 0d;
            try {
               dValue = ((Number) feature._attributes[_fieldIndex]).doubleValue();
            }
            catch (final Exception e) {
            }

            final Color color;
            if (_coloringMethod == COLORING_METHOD_COLOR_RAMP) {
               color = new Color(getColorFromColorRamp(dValue));
            }
            else {
               color = _color;
            }

            poly.setColor(color);

            poly.setLineWidth(_lineThickness);
            poly.setFollowTerrain(true);
            polys.add(poly);
         }
         return polys.toArray(new Polyline[0]);
      }
      catch (final Exception e) {
         return new Renderable[0];
      }

   }


   public int getLineThickness() {
      return _lineThickness;
   }


   public void setLineThickness(final int lineThickness) {
      _lineThickness = lineThickness;
   }

}
