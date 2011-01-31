package es.unex.meigas.core;

import java.awt.Component;
import java.util.List;

import javax.swing.ImageIcon;

import es.igosoftware.globe.GAbstractGlobeModule;
import es.igosoftware.globe.IGlobeApplication;
import es.igosoftware.globe.IGlobeLayer;
import es.igosoftware.globe.actions.GButtonGenericAction;
import es.igosoftware.globe.actions.GGenericAction;
import es.igosoftware.globe.actions.IGenericAction;
import es.igosoftware.globe.actions.ILayerAction;
import es.igosoftware.globe.attributes.ILayerAttribute;
import es.igosoftware.util.GCollections;
import es.igosoftware.util.GPair;
import es.unex.meigas.globe.GlobeGISConnection;
import es.unex.meigas.gui.MeigasGUI;

public class MeigasExtension
         extends
            GAbstractGlobeModule {


   private GlobeGISConnection _gisConnection;


   @Override
   public String getDescription() {

      return "Meigas";

   }


   @Override
   public List<IGenericAction> getGenericActions(final IGlobeApplication application) {

      final GGenericAction meigas = new GButtonGenericAction("Meigas", 'M', new ImageIcon("images/meigas.gif"),
               IGenericAction.MenuArea.ANALYSIS, false) {

         @Override
         public void execute() {

            MeigasGUI.showMeigasWindow();

         }

      };

      return GCollections.createList(meigas);
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
   public void initialize(final IGlobeApplication application) {

      //TODO: pasar application a meigas para que se comunique con el globe

      Meigas.initialize();

      _gisConnection = new GlobeGISConnection(application);

   }


   @Override
   public String getVersion() {
      return null;
   }


   @Override
   public String getName() {
      return "Meigas";
   }


   @Override
   public void initializeTranslations(final IGlobeApplication application) {


   }

}
