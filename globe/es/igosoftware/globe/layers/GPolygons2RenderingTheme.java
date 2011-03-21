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
import es.igosoftware.euclid.vector.IPointsContainer;
import es.igosoftware.euclid.vector.IVector2;
import gov.nasa.worldwind.geom.Angle;
import gov.nasa.worldwind.geom.LatLon;
import gov.nasa.worldwind.globes.Globe;
import gov.nasa.worldwind.render.Material;
import gov.nasa.worldwind.render.Renderable;
import gov.nasa.worldwind.render.airspaces.AirspaceAttributes;
import gov.nasa.worldwind.render.airspaces.Polygon;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;


public class GPolygons2RenderingTheme
         extends
            GVector2RenderingTheme {

   private final Color _borderColor     = Color.black;
   private float       _borderThickness = 1;


   public GPolygons2RenderingTheme() {
      super();
   }


   @Override
   protected Renderable[] getRenderables(final IGlobeFeature<IVector2<?>, GAxisAlignedRectangle> feature,
                                         final GProjection projection,
                                         final Globe globe) {

      final IBoundedGeometry<IVector2<?>, ?, GAxisAlignedRectangle> geom = feature.getDefaultGeometry();

      @SuppressWarnings("unchecked")
      final IPointsContainer<IVector2<?>, ?> points = (IPointsContainer<IVector2<?>, ?>) geom;

      final List<LatLon> list = new ArrayList<LatLon>(points.getPointsCount());
      for (final IVector2<?> point : points) {
         final IVector2<?> transformedPt = projection.transformPoint(GProjection.EPSG_4326, point);
         final LatLon latlon = new LatLon(Angle.fromRadians(transformedPt.y()), Angle.fromRadians(transformedPt.x()));
         list.add(latlon);
      }


      final Polygon poly = new Polygon(list);
      final AirspaceAttributes attrs = poly.getAttributes();
      // TODO:Fix this
      poly.setAltitude(200d);


      final Color color;
      if (_coloringMethod == GVector2RenderingTheme.ColoringMethod.COLOR_RAMP) {
         final double value = ((Number) feature.getAttribute(_fieldIndex)).doubleValue();
         color = new Color(getColorFromColorRamp(value));
      }
      else {
         color = _color;
      }

      attrs.setMaterial(new Material(color));
      attrs.setOutlineMaterial(new Material(_borderColor));
      attrs.setOutlineWidth(_borderThickness);
      attrs.setDrawOutline(true);
      attrs.setDrawInterior(true);

      return new Renderable[] { poly };
   }


   public float getBorderThickness() {
      return _borderThickness;
   }


   public void setBorderThickness(final float borderThickness) {
      _borderThickness = borderThickness;
   }

}
