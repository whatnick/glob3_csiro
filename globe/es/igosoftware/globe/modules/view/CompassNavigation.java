package es.igosoftware.globe.modules.view;

import java.awt.Component;
import java.util.List;

import es.igosoftware.globe.GAbstractGlobeModule;
import es.igosoftware.globe.IGlobeApplication;
import es.igosoftware.globe.IGlobeLayer;
import es.igosoftware.globe.actions.IGenericAction;
import es.igosoftware.globe.actions.ILayerAction;
import es.igosoftware.globe.attributes.ILayerAttribute;
import es.igosoftware.util.GPair;
import gov.nasa.worldwind.View;
import gov.nasa.worldwind.event.SelectEvent;
import gov.nasa.worldwind.event.SelectListener;
import gov.nasa.worldwind.geom.Angle;
import gov.nasa.worldwind.layers.CompassLayer;
import gov.nasa.worldwind.layers.Layer;

public class CompassNavigation
         extends
            GAbstractGlobeModule {

   @Override
   public String getDescription() {

      return "Compass navigation";

   }


   @Override
   public List<IGenericAction> getGenericActions(final IGlobeApplication application) {
      return null;

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
      return "Compass navigation";
   }


   @Override
   public String getVersion() {
      return null;
   }


   @Override
   public void initialize(final IGlobeApplication application) {

      // Find Compass _layer and enable picking
      for (final Layer layer : application.getModel().getLayers()) {
         if (layer instanceof CompassLayer) {
            layer.setPickEnabled(true);
         }
      }

      // Add select listener to handle drag events on the compass
      application.getWorldWindowGLCanvas().addSelectListener(new SelectListener() {

         Angle dragStartHeading = null;
         Angle viewStartHeading = null;
         View  view             = application.getWorldWindowGLCanvas().getView();


         @Override
         public void selected(final SelectEvent event) {

            if (event.getTopObject() instanceof CompassLayer) {
               final Angle heading = (Angle) event.getTopPickedObject().getValue("Heading");
               if (event.getEventAction().equals(SelectEvent.DRAG) && (dragStartHeading == null)) {
                  dragStartHeading = heading;
                  viewStartHeading = view.getHeading();
               }
               else if (event.getEventAction().equals(SelectEvent.ROLLOVER) && (dragStartHeading != null)) {
                  final double move = heading.degrees - dragStartHeading.degrees;
                  double newHeading = viewStartHeading.degrees - move;
                  newHeading = newHeading >= 0 ? newHeading : newHeading + 360;
                  view.stopAnimations();
                  view.setHeading(Angle.fromDegrees(newHeading));
               }
               else if (event.getEventAction().equals(SelectEvent.DRAG_END)) {
                  dragStartHeading = null;
               }
            }

         }

      });

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
