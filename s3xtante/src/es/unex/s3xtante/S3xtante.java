

package es.unex.s3xtante;

import java.awt.Image;

import javax.swing.SwingUtilities;

import es.igosoftware.experimental.vectorial.GPolygon2DModule;
import es.igosoftware.globe.GGlobeApplication;
import es.igosoftware.globe.GLayersManagerModule;
import es.igosoftware.globe.GStatisticsModule;
import es.igosoftware.globe.IGlobeModule;
import es.igosoftware.globe.modules.layers.GAddRasterLayerModule;
import es.igosoftware.globe.modules.layers.GAddVectorLayerModule;
import es.igosoftware.globe.modules.view.GViewControlsModule;
import es.unex.s3xtante.modules.sextante.GSextanteModule;
import es.unex.s3xtante.modules.tables.GAddTableModule;


public class S3xtante
         extends
            GGlobeApplication {

   private static final long   serialVersionUID = 1L;

   private static final String VERSION          = "0.1";


   public S3xtante() {
      super();
   }


   @Override
   protected String getApplicationName() {
      return "S3XTANTE";
   }


   @Override
   protected IGlobeModule[] getInitialModules() {
      //final GPointsCloudFileLoader loader = new GPointsCloudFileLoader("/home/dgd/Desktop/LOD/");
      //final GPointsCloudModule pointsCloudModule = new GPointsCloudModule(loader);


      return new IGlobeModule[] { new GLayersManagerModule(), new GPolygon2DModule(), new GStatisticsModule(), new GSextanteModule(),
               new GAddTableModule(), new GAddRasterLayerModule(), new GAddVectorLayerModule(), new GViewControlsModule() };

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

      System.out.println("S3XTANTE 0.1");
      System.out.println("-------------\n");

      SwingUtilities.invokeLater(new Runnable() {

         @Override
         public void run() {

            new S3xtante().openInFrame();
         }
      });
   }

}
