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
import gov.nasa.worldwind.globes.Earth;
import gov.nasa.worldwind.globes.EarthFlat;
import gov.nasa.worldwind.layers.LayerList;
import gov.nasa.worldwind.layers.SkyColorLayer;
import gov.nasa.worldwind.layers.SkyGradientLayer;
import gov.nasa.worldwind.view.orbit.BasicOrbitView;
import gov.nasa.worldwind.view.orbit.FlatOrbitView;

public class GFlatWorldModule
         extends
            GAbstractGlobeModule {

   private boolean _isActive = false;


   @Override
   public String getDescription() {

      return "View flat world";

   }


   @Override
   public List<IGenericAction> getGenericActions(final IGlobeApplication application) {

      final IGenericAction action = new GCheckBoxGenericAction("View flat world", ' ', null, IGenericAction.MenuArea.VIEW, false,
               false) {


         @Override
         public void execute() {

            _isActive = !_isActive;

            if (_isActive) {
               application.getModel().setGlobe(new EarthFlat());
               final BasicOrbitView orbitView = (BasicOrbitView) application.getWorldWindowGLCanvas().getView();
               final FlatOrbitView flatOrbitView = new FlatOrbitView();
               flatOrbitView.setCenterPosition(orbitView.getCenterPosition());
               flatOrbitView.setZoom(orbitView.getZoom());
               flatOrbitView.setHeading(orbitView.getHeading());
               flatOrbitView.setPitch(orbitView.getPitch());
               application.getWorldWindowGLCanvas().setView(flatOrbitView);
               final LayerList layers = application.getModel().getLayers();
               for (int i = 0; i < layers.size(); i++) {
                  if (layers.get(i) instanceof SkyGradientLayer) {
                     layers.set(i, new SkyColorLayer());
                  }
               }
            }
            else {
               application.getModel().setGlobe(new Earth());
               final FlatOrbitView flatOrbitView = (FlatOrbitView) application.getWorldWindowGLCanvas().getView();
               final BasicOrbitView orbitView = new BasicOrbitView();
               orbitView.setCenterPosition(flatOrbitView.getCenterPosition());
               orbitView.setZoom(flatOrbitView.getZoom());
               orbitView.setHeading(flatOrbitView.getHeading());
               orbitView.setPitch(flatOrbitView.getPitch());
               application.getWorldWindowGLCanvas().setView(orbitView);
               final LayerList layers = application.getModel().getLayers();
               for (int i = 0; i < layers.size(); i++) {
                  if (layers.get(i) instanceof SkyColorLayer) {
                     layers.set(i, new SkyGradientLayer());
                  }
               }
            }

         }

      };

      //      return new IGenericAction[] { action };

      return Collections.singletonList(action);

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
      return "View flat world";
   }


   @Override
   public String getVersion() {
      return null;
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
