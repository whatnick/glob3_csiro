package es.igosoftware.globe.modules.locationManager;

import java.awt.Component;
import java.util.Collections;
import java.util.List;

import javax.swing.JDialog;

import es.igosoftware.globe.GAbstractGlobeModule;
import es.igosoftware.globe.IGlobeApplication;
import es.igosoftware.globe.IGlobeLayer;
import es.igosoftware.globe.actions.GButtonGenericAction;
import es.igosoftware.globe.actions.IGenericAction;
import es.igosoftware.globe.actions.ILayerAction;
import es.igosoftware.globe.attributes.ILayerAttribute;
import es.igosoftware.util.GPair;
import gov.nasa.worldwind.geom.Position;

public class LocationManager
         extends
            GAbstractGlobeModule {

   public LocationManager() {

      Locations.setDefaultLocation(new NamedLocation("DEFAULT", Position.ZERO, 50000));


   }


   public LocationManager(final Position position,
                          final double elevation) {

      Locations.setDefaultLocation(new NamedLocation("DEFAULT", position, elevation));

   }


   @Override
   public String getDescription() {

      return "Location manager";

   }


   @Override
   public List<IGenericAction> getGenericActions(final IGlobeApplication application) {

      final IGenericAction action = new GButtonGenericAction("Location manager", ' ', null, IGenericAction.MenuArea.NAVIGATION,
               false) {

         @Override
         public void execute() {

            final JDialog locationManager = new LocationManagerDialog(application);
            locationManager.setVisible(true);

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
      return "Location manager";
   }


   @Override
   public String getVersion() {
      return null;
   }


   @Override
   public List<GPair<String, Component>> getPanels(final IGlobeApplication application) {
      return null;
   }


   public void postInitialize() {


   }


   @Override
   public void initializeTranslations(final IGlobeApplication application) {
      // TODO Auto-generated method stub

   }


}
