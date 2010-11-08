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
import gov.nasa.worldwind.view.orbit.BasicOrbitView;
import gov.nasa.worldwind.view.orbit.BasicOrbitViewLimits;
import gov.nasa.worldwind.view.orbit.OrbitView;
import gov.nasa.worldwind.view.orbit.OrbitViewLimits;

public class ViewLimits
         extends
            GAbstractGlobeModule {

   private final OrbitViewLimits _limits;


   public ViewLimits(final OrbitViewLimits limits) {
      _limits = limits;
   }


   @Override
   public String getDescription() {
      return "View limits";
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
      return "View limits";
   }


   @Override
   public String getVersion() {
      return null;
   }


   @Override
   public void initialize(final IGlobeApplication application) {
      final View view = application.getView();
      if (view instanceof BasicOrbitView) {
         BasicOrbitViewLimits.applyLimits((OrbitView) view, _limits);
      }

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
