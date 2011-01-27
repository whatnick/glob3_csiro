package es.unex.meigas.globe;

import java.awt.Image;

import javax.swing.SwingUtilities;

import es.igosoftware.experimental.vectorial.GPolygon2DModule;
import es.igosoftware.globe.GGlobeApplication;
import es.igosoftware.globe.GLayersManagerModule;
import es.igosoftware.globe.GStatisticsModule;
import es.igosoftware.globe.IGlobeModule;
import es.igosoftware.globe.modules.view.ViewControls;
import es.unex.meigas.core.MeigasExtension;


public class MeigasGlobe
         extends
            GGlobeApplication {

   private static final long   serialVersionUID = 1L;

   private static final String VERSION          = "0.1";


   public MeigasGlobe() {
      super();
   }


   @Override
   protected String getApplicationName() {
      return "MEIGAS";
   }


   @Override
   public IGlobeModule[] getModules() {

      return new IGlobeModule[] { new GLayersManagerModule(), new GPolygon2DModule(), new GStatisticsModule(),
               new ViewControls(), new MeigasExtension() };

   }


   @Override
   protected Image getImageIcon() {
      return null;
   }


   @Override
   protected String getApplicationVersion() {
      return VERSION;
   }


   public static void main(final String[] args) {

      SwingUtilities.invokeLater(new Runnable() {

         @Override
         public void run() {

            new MeigasGlobe().openInFrame();
         }
      });
   }


}
