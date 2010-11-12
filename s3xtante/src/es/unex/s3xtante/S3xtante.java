

package es.unex.s3xtante;

import java.awt.Image;

import javax.swing.SwingUtilities;

import es.igosoftware.globe.GGlobeApplication;
import es.igosoftware.globe.GLayersManagerModule;
import es.igosoftware.globe.GStatisticsModule;
import es.igosoftware.globe.IGlobeModule;
import es.igosoftware.globe.modules.GFullScreenModule;
import es.igosoftware.globe.modules.gazetteer.Gazetteer;
import es.igosoftware.globe.modules.geonames.SearchGeonames;
import es.igosoftware.globe.modules.layers.AddRasterLayer;
import es.igosoftware.globe.modules.layers.AddVectorLayer;
import es.igosoftware.globe.modules.layers.CreateNewVectorLayer;
import es.igosoftware.globe.modules.locationManager.LocationManager;
import es.igosoftware.globe.modules.view.CompassNavigation;
import es.igosoftware.globe.modules.view.GAnaglyphViewerModule;
import es.igosoftware.globe.modules.view.InfoTool;
import es.igosoftware.globe.modules.view.ShowMeasureTool;
import es.igosoftware.globe.modules.view.UseFlyView;
import es.igosoftware.globe.modules.view.ViewControls;
import es.unex.s3xtante.modules.sextante.Sextante;


public class S3xtante
         extends
            GGlobeApplication {

   private static final long   serialVersionUID = 1L;

   //   private static final double HOME_ELEVATION   = 643;
   private static final String VERSION          = "0.1";


   public S3xtante() {
      super();
   }


   //   @Override
   //   protected Position getHomePosition() {
   //      return new Position(Angle.fromDegrees(39.46146901156602), Angle.fromDegrees(-6.376652835965634), HOME_ELEVATION);
   //   }


   @Override
   protected String getApplicationName() {
      return "S3XTANTE";
   }


   @Override
   public IGlobeModule[] getModules() {

      //final GPointsCloudFileLoader loader = new GPointsCloudFileLoader("/home/dgd/Desktop/LOD/");
      //final GPointsCloudModule pointsCloudModule = new GPointsCloudModule(loader);


      return new IGlobeModule[] { new GLayersManagerModule(),
      /*new GPointsCloudModule(new GPointsCloudFileLoader("e:\\geodata\\puntos_diego")),*/new GFullScreenModule(),
               new GStatisticsModule(), new Sextante(), /*new ShowLatLonGraticule(), new ShowUTMGraticule()*/
               new AddRasterLayer(),
               /*new AddTable()*/new AddVectorLayer(), new CreateNewVectorLayer(), new SearchGeonames(), new Gazetteer(),
               /*new FlatWorld()*/new UseFlyView(), new GAnaglyphViewerModule(false), new LocationManager(),
               new ShowMeasureTool(),
               /*new TakeScreenshot()*/new ViewControls(), new CompassNavigation(), new InfoTool() };

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
