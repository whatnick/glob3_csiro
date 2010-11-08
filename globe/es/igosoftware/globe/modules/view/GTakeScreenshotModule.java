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
import gov.nasa.worldwind.examples.util.ScreenShotAction;

public class GTakeScreenshotModule
         extends
            GAbstractGlobeModule {

   @Override
   public String getDescription() {

      return "Take screenshot";

   }


   @Override
   public List<IGenericAction> getGenericActions(final IGlobeApplication application) {

      final ScreenShotAction ssaction = new ScreenShotAction(application.getWorldWindowGLCanvas());
      final IGenericAction action = new GButtonGenericAction("Take screenshot...", ' ', null, IGenericAction.MenuArea.VIEW, false) {

         @Override
         public void execute() {
            ssaction.actionPerformed(null);
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
      return "Take screenshot";
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
