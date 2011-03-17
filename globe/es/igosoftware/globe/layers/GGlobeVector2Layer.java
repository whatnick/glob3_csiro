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
import es.igosoftware.euclid.IGeometry;
import es.igosoftware.euclid.bounding.GAxisAlignedRectangle;
import es.igosoftware.euclid.projection.GProjection;
import es.igosoftware.euclid.shape.GLinesStrip;
import es.igosoftware.euclid.shape.GSegment;
import es.igosoftware.euclid.shape.IPolygon;
import es.igosoftware.euclid.vector.IVector;
import es.igosoftware.euclid.vector.IVector2;
import es.igosoftware.globe.GField;
import es.igosoftware.globe.GVectorLayerType;
import es.igosoftware.globe.IGlobeApplication;
import es.igosoftware.globe.IGlobeFeatureCollection;
import es.igosoftware.globe.IGlobeVectorLayer;
import es.igosoftware.globe.actions.ILayerAction;
import es.igosoftware.globe.attributes.ILayerAttribute;
import es.igosoftware.util.GAssert;
import es.igosoftware.utils.GWWUtils;
import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.geom.Sector;
import gov.nasa.worldwind.globes.Globe;
import gov.nasa.worldwind.layers.RenderableLayer;
import gov.nasa.worldwind.render.DrawContext;
import gov.nasa.worldwind.render.Renderable;

import java.util.List;

import javax.swing.Icon;


public class GGlobeVector2Layer
         extends
            RenderableLayer
         implements
            IGlobeVectorLayer<IVector2<?>, GAxisAlignedRectangle> {

   private final GVector2RenderingTheme                                      _renderingTheme;
   private final IGlobeFeatureCollection<IVector2<?>, GAxisAlignedRectangle> _features;
   private final GField[]                                                    _fields;

   private boolean                                                           _isInitialized = false;
   private Sector                                                            _extent;


   private static GVector2RenderingTheme getDefaultRenderer(final IGlobeFeatureCollection<IVector2<?>, GAxisAlignedRectangle> features) {

      if (features.isEmpty()) {
         return null;
      }

      final IBoundedGeometry<IVector2<?>, ?, GAxisAlignedRectangle> geometry = features.get(0).getGeometry();

      if (geometry instanceof IVector) {
         return new GPoints2RenderingTheme();
      }
      else if ((geometry instanceof GSegment) || (geometry instanceof GLinesStrip)) {
         return new GLines2RenderingTheme();
      }
      else if (geometry instanceof IPolygon) {
         return new GPolygons2RenderingTheme();
      }
      else {
         return null;
      }
   }


   public GGlobeVector2Layer(final String name,
                             final IGlobeFeatureCollection<IVector2<?>, GAxisAlignedRectangle> features,
                             final GField[] fields) {
      this(name, features, fields, getDefaultRenderer(features));
   }


   public GGlobeVector2Layer(final String name,
                             final IGlobeFeatureCollection<IVector2<?>, GAxisAlignedRectangle> features,
                             final GField[] fields,
                             final GVector2RenderingTheme rendereringTheme) {
      GAssert.notNull(features, "features");

      setName(name);
      setMaxActiveAltitude(1000000000);
      setMinActiveAltitude(0);
      _features = features;
      _renderingTheme = rendereringTheme;
      _fields = fields;

      //addRenderableObjects();
   }


   private void addRenderableObjects(final Globe globe) {
      removeAllRenderables();

      _renderingTheme.calculateExtremeValues(_features);

      final GProjection projection = _features.getProjection();

      for (final IGlobeFeature<IVector2<?>, GAxisAlignedRectangle> feature : _features) {
         for (final Renderable element : _renderingTheme.getRenderables(feature, projection, globe)) {
            addRenderable(element);
         }
      }
   }


   @Override
   public IGlobeFeatureCollection<IVector2<?>, GAxisAlignedRectangle> getFeaturesCollection() {
      return _features;
   }


   @Override
   public Sector getExtent() {
      if (_extent == null) {
         _extent = calculateExtent();
      }

      return _extent;
   }


   private Sector calculateExtent() {
      GAxisAlignedRectangle mergedExtent = null;

      for (final IGlobeFeature<IVector2<?>, GAxisAlignedRectangle> feature : _features) {
         final IBoundedGeometry<IVector2<?>, ?, GAxisAlignedRectangle> geom = feature.getGeometry();
         final GAxisAlignedRectangle bounds = geom.getBounds();

         if (mergedExtent == null) {
            mergedExtent = bounds;
         }
         else {
            mergedExtent = mergedExtent.mergedWith(bounds);
         }
      }

      if (mergedExtent == null) {
         return null;
      }

      return GWWUtils.toSector(mergedExtent, _features.getProjection());
   }


   @Override
   public GVector2RenderingTheme getRenderingTheme() {
      return _renderingTheme;
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


   private static <VectorT extends IVector<VectorT, ?, ?>> GVectorLayerType getShapeType(final IGeometry<VectorT, ?> geometry) {
      if (geometry instanceof IVector) {
         return GVectorLayerType.POINT;
      }
      else if ((geometry instanceof GSegment) || (geometry instanceof GLinesStrip)) {
         return GVectorLayerType.LINE;
      }
      else if (geometry instanceof IPolygon) {
         return GVectorLayerType.POLYGON;
      }
      else {
         return null;
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
