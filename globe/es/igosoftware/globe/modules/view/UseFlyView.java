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
import gov.nasa.worldwind.View;
import gov.nasa.worldwind.view.firstperson.BasicFlyView;
import gov.nasa.worldwind.view.orbit.BasicOrbitView;

public class UseFlyView
         extends
            GAbstractGlobeModule {

   private boolean _isActive = false;


   @Override
   public String getDescription() {
      return "Use fly view";
   }


   @Override
   public List<IGenericAction> getGenericActions(final IGlobeApplication application) {

      final IGenericAction action = new GCheckBoxGenericAction("Use fly view", ' ', null, IGenericAction.MenuArea.VIEW, false,
               false) {

         @Override
         public void execute() {

            _isActive = !_isActive;

            if (_isActive) {
               final BasicFlyView view = new BasicFlyView();
               final View currentView = application.getWorldWindowGLCanvas().getView();
               view.copyViewState(currentView);
               application.getWorldWindowGLCanvas().setView(view);

            }
            else {
               final BasicOrbitView view = new BasicOrbitView();
               final View currentView = application.getWorldWindowGLCanvas().getView();
               view.copyViewState(currentView);
               application.getWorldWindowGLCanvas().setView(view);
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
      return "Use fly view";
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
