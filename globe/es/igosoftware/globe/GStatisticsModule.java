package es.igosoftware.globe;

import java.awt.Component;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;

import es.igosoftware.globe.actions.IGenericAction;
import es.igosoftware.globe.actions.ILayerAction;
import es.igosoftware.globe.attributes.ILayerAttribute;
import es.igosoftware.util.GPair;
import gov.nasa.worldwind.util.StatisticsPanel;

public class GStatisticsModule
         extends
            GAbstractGlobeModule {

   @Override
   public String getName() {
      return "Running Statistics";
   }


   @Override
   public String getDescription() {
      return "Statistics";
   }


   @Override
   public String getVersion() {
      return "0.1";
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
   public List<GPair<String, Component>> getPanels(final IGlobeApplication application) {
      final ArrayList<GPair<String, Component>> panels = new ArrayList<GPair<String, Component>>();

      panels.add(new GPair<String, Component>("Statistics", new StatisticsPanel(application.getWorldWindowGLCanvas(),
               new Dimension(250, 500))));

      return panels;
   }


   @Override
   public List<ILayerAttribute<?>> getLayerAttributes(final IGlobeApplication application,
                                                      final IGlobeLayer layer) {
      return null;
   }


   @Override
   public void initializeTranslations(final IGlobeApplication application) {
      application.addTranslation("es", "Statistics", "Estadísticas");
      application.addTranslation("de", "Statistics", "Statistik");
   }

}
