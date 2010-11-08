package es.igosoftware.globe.modules;

import java.awt.Component;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.util.Collections;
import java.util.List;

import javax.swing.JFrame;

import es.igosoftware.globe.GAbstractGlobeModule;
import es.igosoftware.globe.IGlobeApplication;
import es.igosoftware.globe.IGlobeLayer;
import es.igosoftware.globe.actions.GButtonGenericAction;
import es.igosoftware.globe.actions.IGenericAction;
import es.igosoftware.globe.actions.ILayerAction;
import es.igosoftware.globe.attributes.ILayerAttribute;
import es.igosoftware.util.GPair;

public class GFullScreenModule
         extends
            GAbstractGlobeModule {


   private static final String DEFAULT_LABEL = "Full Screen";

   private final String        _label;


   public GFullScreenModule() {
      this(DEFAULT_LABEL);
   }


   public GFullScreenModule(final String label) {
      _label = label;
   }


   @Override
   public String getName() {
      return "Full Screen";
   }


   @Override
   public String getVersion() {
      return "0.1";
   }


   @Override
   public String getDescription() {
      return "Full Screen behavior module";
   }


   @Override
   public List<IGenericAction> getGenericActions(final IGlobeApplication application) {
      //      if (_frame == null) {
      //         return null;
      //      }

      if (!isFullScreenSupported()) {
         System.out.println("FULLSCREEN not supported");
         return null;
      }


      final IGenericAction switchFullScreen = new GButtonGenericAction(_label, 'F', application.getIcon("fullscreen.png"),
               IGenericAction.MenuArea.VIEW, true) {

         private boolean _isInFullScreen = false;


         @Override
         public void execute() {
            final JFrame frame = application.getFrame();
            if (frame == null) {
               return;
            }

            final GraphicsDevice gs = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
            if (gs.isFullScreenSupported()) {
               if (_isInFullScreen) {
                  //                  frame.removeNotify();
                  //                  frame.setUndecorated(false);
                  //                  frame.setVisible(true);
                  gs.setFullScreenWindow(null);
               }
               else {
                  //                  frame.removeNotify();
                  //                  frame.setUndecorated(true);
                  //                  frame.setVisible(true);
                  gs.setFullScreenWindow(frame);
                  frame.validate();
               }
               _isInFullScreen = !_isInFullScreen;
            }
            else {
               // Full-screen mode will be simulated 
            }
         }
      };

      return Collections.singletonList(switchFullScreen);
   }


   private boolean isFullScreenSupported() {
      final GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
      return ge.getDefaultScreenDevice().isFullScreenSupported();
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
   public List<GPair<String, Component>> getPanels(final IGlobeApplication application) {
      return null;
   }


   @Override
   public void initializeTranslations(final IGlobeApplication application) {
      application.addTranslation("es", DEFAULT_LABEL, "Pantalla Completa");
      application.addTranslation("de", DEFAULT_LABEL, "Vollbild");
   }


}
