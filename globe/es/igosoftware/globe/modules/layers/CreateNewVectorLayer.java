package es.igosoftware.globe.modules.layers;

import java.awt.Component;
import java.util.Collections;
import java.util.List;

import javax.swing.JDialog;

import es.igosoftware.globe.GAbstractGlobeModule;
import es.igosoftware.globe.IGlobeApplication;
import es.igosoftware.globe.IGlobeLayer;
import es.igosoftware.globe.actions.GButtonGenericAction;
import es.igosoftware.globe.actions.IGenericAction;
import es.igosoftware.globe.actions.ILayerAction;
import es.igosoftware.globe.attributes.ILayerAttribute;
import es.igosoftware.util.GPair;

public class CreateNewVectorLayer
         extends
            GAbstractGlobeModule {

   @Override
   public String getDescription() {
      return "Create new vector layer";
   }


   @Override
   public List<IGenericAction> getGenericActions(final IGlobeApplication application) {

      final IGenericAction action = new GButtonGenericAction("Create new vector layer", ' ', null, IGenericAction.MenuArea.FILE,
               false) {

         @Override
         public void execute() {
            final JDialog newLayerDialog = new NewLayerDialog(application);
            newLayerDialog.setVisible(true);
         }
      };

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
      return "Create new vector layer";
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
