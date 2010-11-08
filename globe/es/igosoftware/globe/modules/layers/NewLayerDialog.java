package es.igosoftware.globe.modules.layers;

import javax.swing.JDialog;

import es.igosoftware.globe.IGlobeApplication;

public class NewLayerDialog
         extends
            JDialog {

   private static final long       serialVersionUID = 1L;

   @SuppressWarnings("unused")
   private final IGlobeApplication _app;


   public NewLayerDialog(final IGlobeApplication application) {

      super();
      _app = application;
   }

}
