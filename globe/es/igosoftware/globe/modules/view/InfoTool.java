package es.igosoftware.globe.modules.view;

import java.awt.Component;
import java.util.Collections;
import java.util.List;

import javax.swing.ImageIcon;

import es.igosoftware.globe.GAbstractGlobeModule;
import es.igosoftware.globe.IGlobeApplication;
import es.igosoftware.globe.IGlobeLayer;
import es.igosoftware.globe.actions.GCheckBoxGenericAction;
import es.igosoftware.globe.actions.IGenericAction;
import es.igosoftware.globe.actions.ILayerAction;
import es.igosoftware.globe.attributes.ILayerAttribute;
import es.igosoftware.util.GPair;

public class InfoTool
         extends
            GAbstractGlobeModule {

   private boolean          _isActive = false;
   private InfoToolListener _listener;


   @Override
   public String getDescription() {
      return "Info tool";
   }


   @Override
   public List<IGenericAction> getGenericActions(final IGlobeApplication application) {

      final IGenericAction action = new GCheckBoxGenericAction("Info tool", ' ', new ImageIcon("images/icon-16-_info.png"),
               IGenericAction.MenuArea.VIEW, true, false) {

         @Override
         public void execute() {
            _isActive = !_isActive;
            if (!_isActive) {
               application.getWorldWindowGLCanvas().removeMouseListener(_listener);
            }
            else {
               application.getWorldWindowGLCanvas().addMouseListener(_listener);
            }
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
      return "Info tool";
   }


   @Override
   public String getVersion() {
      return null;
   }


   @Override
   public void initialize(final IGlobeApplication application) {
      _listener = new InfoToolListener(application);
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
