package es.igosoftware.globe.modules.view;

import java.awt.Component;
import java.util.Collections;
import java.util.List;

import es.igosoftware.globe.GAbstractGlobeModule;
import es.igosoftware.globe.IGlobeApplication;
import es.igosoftware.globe.IGlobeLayer;
import es.igosoftware.globe.actions.GButtonGenericAction;
import es.igosoftware.globe.actions.IGenericAction;
import es.igosoftware.globe.actions.ILayerAction;
import es.igosoftware.globe.attributes.ILayerAttribute;
import es.igosoftware.util.GPair;

public class ShowMeasureTool
         extends
            GAbstractGlobeModule {

   @Override
   public String getDescription() {

      return "Measure tool";

   }


   @Override
   public List<IGenericAction> getGenericActions(final IGlobeApplication application) {

      final IGenericAction action = new GButtonGenericAction("Measure tool", ' ', null, IGenericAction.MenuArea.VIEW, false) {

         @Override
         public void execute() {
            final MeasureToolDialog dialog = new MeasureToolDialog(application.getFrame(), application.getWorldWindowGLCanvas());
            dialog.setVisible(true);
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
      return "Measure tool";
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
