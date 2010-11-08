package es.igosoftware.globe;

import java.awt.Component;
import java.util.List;

import es.igosoftware.globe.actions.IGenericAction;
import es.igosoftware.globe.actions.ILayerAction;
import es.igosoftware.globe.attributes.ILayerAttribute;
import es.igosoftware.util.GPair;

public interface IGlobeModule
         extends
            IGlobeComponent {


   public String getName();


   public String getVersion();


   public String getDescription();


   public List<IGenericAction> getGenericActions(final IGlobeApplication application);


   public List<ILayerAction> getLayerActions(final IGlobeApplication application,
                                             final IGlobeLayer layer);


   public List<ILayerAttribute<?>> getLayerAttributes(final IGlobeApplication application,
                                                      final IGlobeLayer layer);


   public List<GPair<String, Component>> getPanels(final IGlobeApplication application);


   public void initializeTranslations(final IGlobeApplication application);


}
