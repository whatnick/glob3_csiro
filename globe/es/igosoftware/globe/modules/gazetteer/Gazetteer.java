package es.igosoftware.globe.modules.gazetteer;

import java.awt.Component;
import java.util.ArrayList;
import java.util.List;

import es.igosoftware.globe.GAbstractGlobeModule;
import es.igosoftware.globe.IGlobeApplication;
import es.igosoftware.globe.IGlobeLayer;
import es.igosoftware.globe.actions.IGenericAction;
import es.igosoftware.globe.actions.ILayerAction;
import es.igosoftware.globe.attributes.ILayerAttribute;
import es.igosoftware.util.GPair;

public class Gazetteer
         extends
            GAbstractGlobeModule {

   @Override
   public String getName() {
      return "Gazetteer";
   }


   @Override
   public String getDescription() {
      return "Gazetteer";
   }


   @Override
   public String getVersion() {
      return null;
   }


   @Override
   public List<IGenericAction> getGenericActions(final IGlobeApplication application) {
      return null;
   }


   @Override
   public List<ILayerAction> getLayerActions(final IGlobeApplication application,
                                             final IGlobeLayer layer) {
      return null;
   }


   @Override
   public List<GPair<String, Component>> getPanels(final IGlobeApplication application) {

      final ArrayList<GPair<String, Component>> panels = new ArrayList<GPair<String, Component>>();

      try {
         panels.add(new GPair<String, Component>("Go to", new GazetteerPanel(application.getWorldWindowGLCanvas(), null)));
      }
      catch (final Exception e) {}

      return panels;


   }


   @Override
   public List<ILayerAttribute<?>> getLayerAttributes(final IGlobeApplication application,
                                                      final IGlobeLayer layer) {
      return null;
   }


   @Override
   public void initializeTranslations(final IGlobeApplication application) {
      // TODO Auto-generated method stub

   }


}
