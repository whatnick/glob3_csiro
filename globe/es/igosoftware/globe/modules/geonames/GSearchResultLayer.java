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


package es.igosoftware.globe.modules.geonames;

import es.igosoftware.euclid.projection.GProjection;
import es.igosoftware.globe.GField;
import es.igosoftware.globe.GVectorLayerType;
import es.igosoftware.globe.IGlobeApplication;
import es.igosoftware.globe.IGlobeVectorLayer;
import es.igosoftware.globe.actions.ILayerAction;
import es.igosoftware.globe.attributes.ILayerAttribute;
import es.igosoftware.globe.layers.GFeature;
import es.igosoftware.globe.layers.GVectorRenderingTheme;
import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.geom.Sector;
import gov.nasa.worldwind.layers.MarkerLayer;
import gov.nasa.worldwind.render.markers.Marker;

import java.util.ArrayList;
import java.util.List;

import javax.swing.Icon;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;


public class GSearchResultLayer
         extends
            MarkerLayer
         implements
            IGlobeVectorLayer {

   private final GFeature[] m_Features;


   public GSearchResultLayer(final ArrayList<Marker> list) {

      super(list);

      m_Features = new GFeature[list.size()];

      final GeometryFactory gf = new GeometryFactory();

      for (int i = 0; i < m_Features.length; i++) {
         final GSearchResultMarker marker = (GSearchResultMarker) list.get(i);
         final Point geom = gf.createPoint(new Coordinate(marker.getPosition().longitude.degrees,
                  marker.getPosition().latitude.degrees));
         try {
            final Object[] attribs = new Object[] { marker.getToponym().getName(), marker.getToponym().getPopulation() };
            m_Features[i] = new GFeature(geom, attribs);
         }
         catch (final Exception e) {
            m_Features[i] = new GFeature(geom, new Object[] { "", Long.valueOf(0) });
         }

      }

   }


   @Override
   public Sector getExtent() {
      return Sector.FULL_SPHERE;
   }


   @Override
   public Icon getIcon(final IGlobeApplication application) {
      return null;
   }


   @Override
   public GProjection getProjection() {
      return GProjection.EPSG_4326;
   }


   @Override
   public GField[] getFields() {
      return new GField[] { new GField("Name", String.class), new GField("Population", Integer.class) };
   }


   @Override
   public GVectorLayerType getShapeType() {
      return GVectorLayerType.POINT;
   }


   @Override
   public final void redraw() {
      // fire event to force a redraw
      firePropertyChange(AVKey.LAYER, null, this);
   }


   @Override
   public GFeature[] getFeatures() {

      return m_Features;

   }


   @Override
   public GVectorRenderingTheme getRenderingTheme() {
      return null;
   }


   @Override
   public List<ILayerAttribute<?>> getLayerAttributes(final IGlobeApplication application) {
      return null;
   }


   @Override
   public void doDefaultAction(final IGlobeApplication application) {
      application.zoomToSector(getExtent());
   }


   @Override
   public List<ILayerAction> getLayerActions(final IGlobeApplication application) {
      return null;
   }

}
