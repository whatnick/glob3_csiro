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
import es.igosoftware.globe.GField;
import es.igosoftware.globe.GVectorLayerType;
import es.igosoftware.globe.IGlobeApplication;
import es.igosoftware.globe.IGlobeFeatureCollection;
import es.igosoftware.globe.IGlobeVectorLayer;
import es.igosoftware.globe.actions.ILayerAction;
import es.igosoftware.globe.attributes.ILayerAttribute;
import es.igosoftware.util.GAssert;
import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.geom.Angle;
import gov.nasa.worldwind.geom.Sector;
import gov.nasa.worldwind.globes.Globe;
import gov.nasa.worldwind.layers.RenderableLayer;
import gov.nasa.worldwind.render.DrawContext;
import gov.nasa.worldwind.render.Renderable;

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


public class GGlobeVectorLayer
         extends
            RenderableLayer
         implements
            IGlobeVectorLayer {

   private final GVectorRenderingTheme   _renderer;
   private final IGlobeFeatureCollection _features;
   private final GField[]                _fields;

   private boolean                       _isInitialized = false;


   private static GVectorRenderingTheme getDefaultRenderer(final IGlobeFeatureCollection features) {

      final Geometry geom = features.get(0).getGeometry();
      if (geom instanceof com.vividsolutions.jts.geom.Point) {
         return new GPointsRenderingTheme();
      }
      else if ((geom instanceof LineString) || (geom instanceof MultiLineString)) {
         return new GLinesRenderingTheme();
      }
      else if ((geom instanceof com.vividsolutions.jts.geom.Polygon)
               || (geom instanceof com.vividsolutions.jts.geom.MultiPolygon)) {
         return new GPolygonsRenderingTheme();
      }
      else {
         return null;
      }

   }


   public GGlobeVectorLayer(final String sName,
                            final IGlobeFeatureCollection features,
                            final GField[] fields) {
      this(sName, features, fields, getDefaultRenderer(features));
   }


   public GGlobeVectorLayer(final String sName,
                            final IGlobeFeatureCollection features,
                            final GField[] fields,
                            final GVectorRenderingTheme vrenderer) {
      GAssert.notNull(features, "features");

      setName(sName);
      setMaxActiveAltitude(1000000000);
      setMinActiveAltitude(0);
      _features = features;
      _renderer = vrenderer;
      _fields = fields;

      //addRenderableObjects();
   }


   private void addRenderableObjects(final Globe globe) {
      removeAllRenderables();

      _renderer.calculateExtremeValues(_features);

      final GProjection projection = _features.getProjection();

      for (final IGlobeFeature element2 : _features) {
         final Renderable[] ren = _renderer.getRenderables(element2, projection, globe);
         for (final Renderable element : ren) {
            addRenderable(element);
         }
      }
   }


   @Override
   public IGlobeFeatureCollection getFeaturesCollection() {
      return _features;
   }


   @Override
   public Sector getExtent() {
      final ArrayList<Geometry> geoms = new ArrayList<Geometry>();

      for (final IGlobeFeature element : _features) {
         final Geometry geom = element.getGeometry();
         geoms.add(geom);
      }

      final GeometryCollection extentGeom = new GeometryFactory().createGeometryCollection(geoms.toArray(new Geometry[0]));

      final Envelope extent = extentGeom.getEnvelopeInternal();

      final GProjection projection = _features.getProjection();
      final IVector2<?> min = projection.transformPoint(GProjection.EPSG_4326, new GVector2D(extent.getMinX(), extent.getMinY()));
      final IVector2<?> max = projection.transformPoint(GProjection.EPSG_4326, new GVector2D(extent.getMaxX(), extent.getMaxY()));

      final Sector sector = new Sector(Angle.fromRadians(min.y()), Angle.fromRadians(max.y()), Angle.fromRadians(min.x()),
               Angle.fromRadians(max.x()));

      return sector;

   }


   @Override
   public GVectorRenderingTheme getRenderingTheme() {
      return _renderer;
   }


   @Override
   public void redraw() {
      // fire event to force a redraw
      firePropertyChange(AVKey.LAYER, null, this);
   }


   @Override
   public Icon getIcon(final IGlobeApplication application) {
      return application.getIcon("vectorial.png");
   }


   @Override
   public GProjection getProjection() {
      return _features.getProjection();
   }


   @Override
   public GField[] getFields() {
      return _fields;

   }


   @Override
   public GVectorLayerType getShapeType() {
      if ((_features == null) || (_features.isEmpty())) {
         return GVectorLayerType.POLYGON;
      }

      return getShapeType(_features.get(0).getGeometry());
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
   protected void doRender(final DrawContext dc) {
      checkInitialization(dc);

      super.doRender(dc);
   }


   @Override
   protected void doRender(final DrawContext dc,
                           final Iterable<? extends Renderable> renderables) {
      checkInitialization(dc);

      super.doRender(dc, renderables);
   }


   private void checkInitialization(final DrawContext dc) {
      if (!_isInitialized) {
         _isInitialized = true;
         addRenderableObjects(dc.getGlobe());
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
