

package es.igosoftware.experimental.wms;

import java.awt.Component;
import java.util.Arrays;
import java.util.List;

import es.igosoftware.globe.GAbstractGlobeModule;
import es.igosoftware.globe.IGlobeApplication;
import es.igosoftware.globe.IGlobeLayer;
import es.igosoftware.globe.actions.GButtonGenericAction;
import es.igosoftware.globe.actions.IGenericAction;
import es.igosoftware.globe.actions.ILayerAction;
import es.igosoftware.globe.attributes.ILayerAttribute;
import es.igosoftware.io.GFileName;
import es.igosoftware.util.GPair;


public class GWMSModule
         extends
            GAbstractGlobeModule {

   @Override
   public String getName() {
      return "WMS Module";
   }


   @Override
   public String getVersion() {
      return "0.1";
   }


   @Override
   public String getDescription() {
      return "Web Map Services Module for Glob3";
   }


   @Override
   public List<? extends IGenericAction> getGenericActions(final IGlobeApplication application) {

      final IGenericAction addWMSLayer = new GButtonGenericAction("Add WMS layer", 'W',
               application.getSmallIcon(GFileName.relative("earth-add.png")), IGenericAction.MenuArea.FILE, true) {

         @Override
         public boolean isVisible() {
            return true;
         }


         @Override
         public void execute() {
            addNewLayer(application);
         }
      };

      return Arrays.asList(addWMSLayer);
   }


   public IGlobeLayer addNewLayer(final IGlobeApplication application) {

      final GWMSDialog dialog = new GWMSDialog(application);
      dialog.showWMSDialog();

      //      if (newLayer != null) {
      //         final LayerList layers = application.getLayerList();
      //         newLayer.setOpacity(0.7);
      //         layers.add(newLayer);
      //         System.out.println("Añadida WMS layer !");
      //      }
      //      else {
      //         System.out.println("POS NO PUEDO AÑADIR LAYER !");
      //      }

      return null;
   }


   //-------------------------------------------------------------------------------------------------------


   @Override
   public List<? extends ILayerAction> getLayerActions(final IGlobeApplication application,
                                                       final IGlobeLayer layer) {
      return null;
   }


   @Override
   public List<ILayerAttribute<?>> getLayerAttributes(final IGlobeApplication application,
                                                      final IGlobeLayer layer) {
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
