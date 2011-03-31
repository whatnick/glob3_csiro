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


package es.igosoftware.panoramic;

import es.igosoftware.euclid.features.IGlobeFeatureCollection;
import es.igosoftware.euclid.projection.GProjection;
import es.igosoftware.globe.GGlobeApplication;
import es.igosoftware.globe.IGlobeApplication;
import es.igosoftware.globe.IGlobeVectorLayer;
import es.igosoftware.globe.actions.ILayerAction;
import es.igosoftware.globe.attributes.ILayerAttribute;
import es.igosoftware.globe.layers.GVector2RenderingTheme;
import es.igosoftware.globe.layers.hud.GHUDLayer;
import es.igosoftware.globe.view.customView.GCustomView;
import gov.nasa.worldwind.View;
import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.awt.WorldWindowGLCanvas;
import gov.nasa.worldwind.geom.Angle;
import gov.nasa.worldwind.geom.Line;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.geom.Sector;
import gov.nasa.worldwind.geom.Sphere;
import gov.nasa.worldwind.globes.Globe;
import gov.nasa.worldwind.layers.AbstractLayer;
import gov.nasa.worldwind.layers.Layer;
import gov.nasa.worldwind.render.DrawContext;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.Icon;


public class GPanoramicLayer
         extends
            AbstractLayer
         implements
            IGlobeVectorLayer {


   private final String           _name;
   private final List<GPanoramic> _panoramics    = new ArrayList<GPanoramic>();

   private List<PickListener>     _pickListeners;

   private final Set<Layer>       _hiddenLayers  = new HashSet<Layer>();
   private boolean                _hasHiddenLayers;
   private boolean                _isInitialized = false;


   public GPanoramicLayer(final String name) {
      _name = name;
   }


   @Override
   public String getName() {
      return _name;
   }


   public void addPanoramic(final GPanoramic panoramica) {
      if (isDuplicateName(panoramica.getName())) {
         throw new RuntimeException("A Panoramic with the name " + panoramica.getName() + " already exists!!");
      }
      _panoramics.add(panoramica);
      panoramica.addActivationListener(new GPanoramic.ActivationListener() {

         @Override
         public void deactivated(final GPanoramic panoramic) {
            unhideHiddenPanoramics();
            unhideHiddenLayers();

         }


         @Override
         public void activated(final GPanoramic panoramic) {
            hideOtherLayers(GPanoramicLayer.this, panoramic.getHUDLayer());
            hideOtherPanoramics(panoramic);

         }
      });

   }


   private boolean isDuplicateName(final String name) {
      for (final GPanoramic panoramic : _panoramics) {
         if (name.equals(panoramic.getName())) {
            return true;
         }
      }
      return false;
   }


   @Override
   public Icon getIcon(final IGlobeApplication application) {
      return application.getIcon("panoramic.png");
   }


   @Override
   public Sector getExtent() {
      if (isEnabled()) {
         final List<Sphere> panoramicsBounds = new ArrayList<Sphere>();
         for (final GPanoramic panoramic : _panoramics) {
            panoramicsBounds.add(panoramic.getGlobalBounds());
         }
         final Globe globe = GGlobeApplication.instance().getGlobe();
         final Sphere totalbounds = Sphere.createBoundingSphere(panoramicsBounds);
         final Sector totalSector = Sector.boundingSector(globe, globe.computePositionFromPoint(totalbounds.getCenter()),
                  totalbounds.getRadius());
         return totalSector;
      }
      return null;
   }


   @Override
   public GProjection getProjection() {
      return GProjection.EPSG_4326;
   }


   @Override
   public void redraw() {
      firePropertyChange(AVKey.LAYER, null, this);
   }


   @Override
   public List<ILayerAttribute<?>> getLayerAttributes(final IGlobeApplication application) {
      return null;
   }


   @Override
   public void doDefaultAction(final IGlobeApplication application) {
      if (isEnabled()) {
         final Sector sector = getExtent();

         final double altitude = application.calculateAltitudeForZooming(sector);
         application.goTo(new Position(sector.getCentroid(), 0), Angle.ZERO, Angle.fromDegrees(75), altitude);
      }
   }


   @Override
   public List<? extends ILayerAction> getLayerActions(final IGlobeApplication application) {
      return null;
   }


   @Override
   public GVector2RenderingTheme getRenderingTheme() {
      return null;
   }


   @Override
   public IGlobeFeatureCollection getFeaturesCollection() {
      return null;
   }


   @Override
   protected void doRender(final DrawContext dc) {
      if (dc.isPickingMode()) {
         return;
      }

      if (!_isInitialized) {
         final View view = GGlobeApplication.instance().getView();
         if (view instanceof GCustomView) {
            _isInitialized = true;
         }
         else {
            throw new RuntimeException("Panoramics only work with a GCustomView. The current View is of type " + view.getClass());
         }

      }

      initializeEvents();

      for (final GPanoramic panoramic : _panoramics) {
         if (!panoramic.isHidden()) {
            panoramic.doRender(dc);
         }
      }
   }

   public static interface PickListener {
      public void picked(final GPanoramic pickedPanoramic);
   }


   public void addPickListener(final GPanoramicLayer.PickListener pickListener) {
      if (_pickListeners == null) {
         _pickListeners = new ArrayList<GPanoramicLayer.PickListener>();
      }
      _pickListeners.add(pickListener);
   }


   private void initializeEvents() {
      final WorldWindowGLCanvas wwGLCanvas = GGlobeApplication.instance().getWorldWindowGLCanvas();

      wwGLCanvas.getInputHandler().addMouseListener(new MouseAdapter() {
         @Override
         public void mouseClicked(final MouseEvent e) {

            final View view = wwGLCanvas.getView();

            final Line ray = view.computeRayFromScreenPoint(e.getX(), e.getY());

            if (pick(ray) != null) {
               e.consume();
            }
         }
      });

   }


   private GPanoramic pick(final Line ray) {
      if ((_pickListeners == null) || _pickListeners.isEmpty()) {
         return null;
      }
      double closestDistance = Double.MAX_VALUE;
      GPanoramic pickedPanoramic = null;
      for (final GPanoramic panoramic : _panoramics) {
         if (panoramic.getGlobalBounds().intersects(ray)) {
            final double currentDistance = panoramic.getCurrentDistanceFromEye();
            if (currentDistance < closestDistance) {
               closestDistance = currentDistance;
               pickedPanoramic = panoramic;
            }
         }
      }

      for (final PickListener listener : _pickListeners) {
         listener.picked(pickedPanoramic);
      }
      return pickedPanoramic;


   }


   private void hideOtherPanoramics(final GPanoramic visiblePanoramic) {
      if (_panoramics.size() <= 1) {
         return;
      }
      for (final GPanoramic panoramic : _panoramics) {
         if (panoramic == visiblePanoramic) {
            continue;
         }
         panoramic.setHidden(true);
      }
   }


   private void unhideHiddenPanoramics() {
      if (_panoramics.isEmpty()) {
         return;
      }
      for (final GPanoramic panoramic : _panoramics) {
         panoramic.setHidden(false);
      }
   }


   private void hideOtherLayers(final Layer visibleLayer,
                                final GHUDLayer hudLayer) {
      _hasHiddenLayers = true;

      final GGlobeApplication application = GGlobeApplication.instance();
      for (final Layer layer : application.getLayerList()) {
         if ((layer == visibleLayer) || (layer == hudLayer)) {
            continue;
         }

         if (layer.isEnabled()) {
            _hiddenLayers.add(layer);
            layer.setEnabled(false);
         }
      }
      redraw();
   }


   private void unhideHiddenLayers() {
      if (!_hasHiddenLayers) {
         return;
      }
      for (final Layer layer : _hiddenLayers) {
         layer.setEnabled(true);
      }
   }


}
