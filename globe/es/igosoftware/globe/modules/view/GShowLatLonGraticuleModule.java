package es.igosoftware.globe.modules.view;

import java.awt.Component;
import java.util.Collections;
import java.util.List;

import es.igosoftware.globe.GAbstractGlobeModule;
import es.igosoftware.globe.IGlobeApplication;
import es.igosoftware.globe.IGlobeLayer;
import es.igosoftware.globe.actions.GCheckBoxGenericAction;
import es.igosoftware.globe.actions.IGenericAction;
import es.igosoftware.globe.actions.ILayerAction;
import es.igosoftware.globe.attributes.ILayerAttribute;
import es.igosoftware.util.GPair;
import gov.nasa.worldwind.layers.LatLonGraticuleLayer;
import gov.nasa.worldwind.layers.Layer;

public class GShowLatLonGraticuleModule
         extends
            GAbstractGlobeModule {

   private boolean _isActive = false;


   @Override
   public String getDescription() {
      return "Show Lat-Lon Graticule";
   }


   @Override
   public List<IGenericAction> getGenericActions(final IGlobeApplication application) {

      final IGenericAction graticule = new GCheckBoxGenericAction("Show Lat-Lon Graticule", ' ', null,
               IGenericAction.MenuArea.VIEW, false, false) {

         @Override
         public void execute() {
            _isActive = !_isActive;
            final List<Layer> layers = application.getModel().getLayers().getLayersByClass(LatLonGraticuleLayer.class);
            if (layers.size() != 0) {
               layers.get(0).setEnabled(_isActive);
            }
         }

      };

      //      return new IGenericAction[] { graticule };
      return Collections.singletonList(graticule);
   }


   @Override
   public String getVersion() {
      return null;
   }


   @Override
   public List<ILayerAction> getLayerActions(final IGlobeApplication application,
                                             final IGlobeLayer layer) {
      return null;
   }


   @Override
   public List<ILayerAttribute<?>> getLayerAttributes(final IGlobeApplication application,
                                                      final IGlobeLayer layer) {
      return null;
   }


   @Override
   public String getName() {

      return "Show Lat-Lon Graticule";
   }


   @Override
   public void initialize(final IGlobeApplication application) {
      final LatLonGraticuleLayer graticuleLayer = new LatLonGraticuleLayer();
      graticuleLayer.setEnabled(false);
      application.getModel().getLayers().add(graticuleLayer);
   }


   @Override
   public List<GPair<String, Component>> getPanels(final IGlobeApplication application) {
      return null;
   }


   @Override
   public void initializeTranslations(final IGlobeApplication application) {
      // TODO Auto-generated method stub

   }


}
