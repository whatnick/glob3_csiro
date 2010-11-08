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
import gov.nasa.worldwind.AnaglyphSceneController;
import gov.nasa.worldwind.Configuration;
import gov.nasa.worldwind.SceneController;
import gov.nasa.worldwind.avlist.AVKey;

public class GAnaglyphViewerModule
         extends
            GAbstractGlobeModule {

   private static final String DEFAULT_LABEL = "View anaglyph";

   private final String        _label;
   private boolean             _isActive     = true;


   public GAnaglyphViewerModule(final boolean isActive) {
      this(DEFAULT_LABEL, isActive);
   }


   public GAnaglyphViewerModule(final String label,
                                final boolean isActive) {
      _label = label;
      _isActive = isActive;
   }


   static {
      Configuration.setValue(AVKey.SCENE_CONTROLLER_CLASS_NAME, AnaglyphSceneController.class.getName());
   }


   @Override
   public String getDescription() {
      return DEFAULT_LABEL;
   }


   @Override
   public void initialize(final IGlobeApplication application) {
      super.initialize(application);

      doIt(application);
   }


   @Override
   public List<IGenericAction> getGenericActions(final IGlobeApplication application) {

      final IGenericAction action = new GCheckBoxGenericAction(_label, ' ', application.getIcon("anaglyph.png"),
               IGenericAction.MenuArea.VIEW, true, _isActive) {

         @Override
         public void execute() {

            _isActive = !_isActive;

            doIt(application);
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
      return DEFAULT_LABEL;
   }


   @Override
   public String getVersion() {
      return null;
   }


   @Override
   public List<GPair<String, Component>> getPanels(final IGlobeApplication application) {
      return null;
   }


   private void doIt(final IGlobeApplication application) {
      if (_isActive) {
         final SceneController controller = application.getWorldWindowGLCanvas().getSceneController();
         if (controller instanceof AnaglyphSceneController) {
            ((AnaglyphSceneController) controller).setDisplayMode(AnaglyphSceneController.DISPLAY_MODE_STEREO);
         }
      }
      else {
         final SceneController controller = application.getWorldWindowGLCanvas().getSceneController();
         if (controller instanceof AnaglyphSceneController) {
            ((AnaglyphSceneController) controller).setDisplayMode(AnaglyphSceneController.DISPLAY_MODE_MONO);
         }
      }


      application.redraw();
   }


   @Override
   public void initializeTranslations(final IGlobeApplication application) {
      application.addTranslation("es", DEFAULT_LABEL, "Ver Anaglifo");
      application.addTranslation("de", DEFAULT_LABEL, "3D-Anaglyph-Sicht");

   }


}
