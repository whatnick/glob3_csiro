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

import java.util.ArrayList;
import java.util.List;

import javax.swing.Icon;

import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryCollection;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Polygon;

import es.igosoftware.euclid.projection.GProjection;
import es.igosoftware.euclid.vector.GVector2D;
import es.igosoftware.euclid.vector.IVector2;
import es.igosoftware.globe.GField;
import es.igosoftware.globe.GVectorLayerType;
import es.igosoftware.globe.IGlobeApplication;
import es.igosoftware.globe.IGlobeVectorLayer;
import es.igosoftware.globe.actions.ILayerAction;
import es.igosoftware.globe.attributes.ILayerAttribute;
import gov.nasa.worldwind.geom.Angle;
import gov.nasa.worldwind.geom.Sector;
import gov.nasa.worldwind.layers.RenderableLayer;
import gov.nasa.worldwind.render.Renderable;


public class GGlobeVectorLayer
         extends
            RenderableLayer
         implements
            IGlobeVectorLayer {

   private final GVectorRenderer m_Renderer;

   private final Feature[]       m_Features;

   private GProjection           m_CRS = GProjection.EPSG_4326;


   private final GField[]        _fields;


   public GGlobeVectorLayer(final String sName,
                            final Feature[] features,
                            final GField[] fields,
                            final GProjection crs) {

      this(sName, features, fields, crs, getDefaultRenderer(features));

   }


   private static GVectorRenderer getDefaultRenderer(final Feature[] features) {

      final Object geom = features[0]._geometry;
      if (geom instanceof com.vividsolutions.jts.geom.Point) {
         return new GPointsRenderer();
      }
      else if ((geom instanceof LineString) || (geom instanceof MultiLineString)) {
         return new GLinesRenderer();
      }
      else if ((geom instanceof com.vividsolutions.jts.geom.Polygon)
               || (geom instanceof com.vividsolutions.jts.geom.MultiPolygon)) {
         return new GPolygonsRenderer();
      }
      else {
         return null;
      }

   }


   public GGlobeVectorLayer(final String sName,
                            final Feature[] features,
                            final GField[] fields,
                            final GProjection crs,
                            final GVectorRenderer vrenderer) {

      super();

      setName(sName);
      setMaxActiveAltitude(1000000000);
      setMinActiveAltitude(0);
      m_Features = features;
      m_Renderer = vrenderer;
      _fields = fields;
      m_CRS = crs;

      //addRenderableObjects();

   }


   private void addRenderableObjects() {

      this.removeAllRenderables();

      m_Renderer.calculateExtremeValues(m_Features);

      try {
         for (final Feature element2 : m_Features) {
            final Renderable[] ren = m_Renderer.getRenderables(element2, m_CRS);
            for (final Renderable element : ren) {
               this.addRenderable(element);
            }
         }
      }
      catch (final Exception e) {
         e.printStackTrace();
      }

   }


   @Override
   public Feature[] getFeatures() {

      return m_Features;

   }


   @Override
   public Sector getExtent() {

      final ArrayList<Geometry> geoms = new ArrayList<Geometry>();

      try {
         for (final Feature element : m_Features) {
            final Geometry geom = element._geometry;
            geoms.add(geom);
         }

         final GeometryCollection extentGeom = new GeometryFactory().createGeometryCollection(geoms.toArray(new Geometry[0]));

         final Envelope extent = extentGeom.getEnvelopeInternal();

         final IVector2<?> min = m_CRS.transformPoint(GProjection.EPSG_4326, new GVector2D(extent.getMinX(), extent.getMinY()));
         final IVector2<?> max = m_CRS.transformPoint(GProjection.EPSG_4326, new GVector2D(extent.getMaxX(), extent.getMaxY()));

         final Sector sector = new Sector(Angle.fromRadians(min.y()), Angle.fromRadians(max.y()), Angle.fromRadians(min.x()),
                  Angle.fromRadians(max.x()));

         return sector;

      }
      catch (final Exception e) {
         return null;
      }

   }


   @Override
   public GVectorRenderer getRenderer() {

      return m_Renderer;

   }


   @Override
   public void redraw() {

      addRenderableObjects();

   }


   @Override
   public Icon getIcon(final IGlobeApplication application) {

      return null;

   }


   @Override
   public GProjection getProjection() {

      return m_CRS;

   }


   @Override
   public GField[] getFields() {
      return _fields;

   }


   @Override
   public void setProjection(final GProjection proj) {

      m_CRS = proj;

   }


   @Override
   public GVectorLayerType getShapeType() {

      if ((m_Features == null) || (m_Features.length == 0)) {
         return GVectorLayerType.POLYGON;
      }
      return getShapeType(m_Features[0]._geometry);

   }


   private GVectorLayerType getShapeType(final Geometry geom) {

      if ((geom instanceof Polygon) || (geom instanceof MultiPolygon)) {
         return GVectorLayerType.POLYGON;
      }
      else if ((geom instanceof LineString) || (geom instanceof MultiLineString)) {
         return GVectorLayerType.LINE;
      }
      else {
         return GVectorLayerType.POINT;
      }

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
