package es.igosoftware.globe;

import gov.nasa.worldwind.Model;
import gov.nasa.worldwind.WWObjectImpl;
import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.geom.Extent;
import gov.nasa.worldwind.globes.Globe;
import gov.nasa.worldwind.layers.Layer;
import gov.nasa.worldwind.layers.LayerList;

public class GModel
         extends
            WWObjectImpl
         implements
            Model {

   private Globe     _globe;
   private LayerList _layers;

   private boolean   _showTessellationBoundingVolumes;
   private boolean   _showWireframeExterior;
   private boolean   _showWireframeInterior;


   GModel(final Globe globe) {
      setGlobe(globe);

      setLayers(new LayerList());
   }


   /**
    * Returns the bounding sphere in Cartesian world coordinates of the model.
    * 
    * @return the model's bounding sphere in Cartesian coordinates, or null if the extent cannot be determined.
    */
   @Override
   public Extent getExtent() {
      // See if the layers have it.
      final LayerList layers = getLayers();
      if (layers != null) {
         for (final Layer layer : layers) {
            final Extent e = (Extent) layer.getValue(AVKey.EXTENT);
            if (e != null) {
               return e;
            }
         }
      }

      // See if the Globe has it.
      final Globe globe = getGlobe();
      if (globe != null) {
         final Extent e = globe.getExtent();
         if (e != null) {
            return e;
         }
      }

      return null;
   }


   @Override
   public Globe getGlobe() {
      return _globe;
   }


   @Override
   public LayerList getLayers() {
      return _layers;
   }


   @Override
   public boolean isShowTessellationBoundingVolumes() {
      return _showTessellationBoundingVolumes;
   }


   @Override
   public boolean isShowWireframeExterior() {
      return _showWireframeExterior;
   }


   @Override
   public boolean isShowWireframeInterior() {
      return _showWireframeInterior;
   }


   @Override
   public void setGlobe(final Globe globe) {
      if (_globe != null) {
         _globe.removePropertyChangeListener(this);
      }

      // if the new globe is not null, add "this" as a property change listener.
      if (globe != null) {
         globe.addPropertyChangeListener(this);
      }

      final Globe old = _globe;
      _globe = globe;
      this.firePropertyChange(AVKey.GLOBE, old, _globe);
   }


   @Override
   public void setLayers(final LayerList layers) {
      if (_layers != null) {
         _layers.removePropertyChangeListener(this);
      }

      if (layers != null) {
         layers.addPropertyChangeListener(this);
      }

      final LayerList old = _layers;
      _layers = layers;
      this.firePropertyChange(AVKey.LAYERS, old, _layers);
   }


   @Override
   public void setShowTessellationBoundingVolumes(final boolean showTessellationBoundingVolumes) {
      _showTessellationBoundingVolumes = showTessellationBoundingVolumes;
   }


   @Override
   public void setShowWireframeExterior(final boolean showWireframeExterior) {
      _showWireframeExterior = showWireframeExterior;
   }


   @Override
   public void setShowWireframeInterior(final boolean showWireframeInterior) {
      _showWireframeInterior = showWireframeInterior;
   }

}
