

package es.unex.s3xtante.modules.sextante;

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
import es.unex.s3xtante.modules.sextante.bindings.WWGUIFactory;
import es.unex.s3xtante.modules.sextante.bindings.WWInputFactory;
import es.unex.s3xtante.modules.sextante.bindings.WWOutputFactory;
import es.unex.s3xtante.modules.sextante.bindings.WWPostProcessTaskFactory;
import es.unex.sextante.gui.additionalResults.AdditionalResults;
import es.unex.sextante.gui.core.SextanteGUI;
import es.unex.sextante.gui.history.History;
import es.unex.sextante.wps.SextanteWPSConfig;
import gov.nasa.worldwind.Model;


public class Sextante
         extends
            GAbstractGlobeModule {

   private String getJarsFolder() {

      final String sPath = System.getProperty("user.dir") + "/lib/sextante";

      return sPath;

   }


   private String getHelpPath() {

      final String sPath = System.getProperty("user.dir") + "/sextante_help";
      return sPath;

   }


   @Override
   public String getDescription() {

      return "SEXTANTE Toolbox";

   }


   @Override
   public List<IGenericAction> getGenericActions(final IGlobeApplication application) {

      final GGenericAction toolbox = new GButtonGenericAction("SEXTANTE Toolbox", 'T', new ImageIcon("images/sextante.gif"),
               IGenericAction.MenuArea.ANALYSIS, false) {

         @Override
         public void execute() {

            SextanteGUI.getGUIFactory().showToolBoxDialog();
         }

      };

      final GGenericAction modeler = new GButtonGenericAction("SEXTANTE Modeler", 'M', new ImageIcon("images/model.png"),
               IGenericAction.MenuArea.ANALYSIS, false) {

         @Override
         public void execute() {

            SextanteGUI.getGUIFactory().showModelerDialog();
         }

      };

      final GGenericAction commandline = new GButtonGenericAction("SEXTANTE Command Line", 'C', new ImageIcon(
               "images/terminal.png"), IGenericAction.MenuArea.ANALYSIS, false) {

         @Override
         public void execute() {

            SextanteGUI.getGUIFactory().showCommandLineDialog();

         }

      };

      final GGenericAction history = new GButtonGenericAction("SEXTANTE History", 'H', new ImageIcon("images/history.gif"),
               IGenericAction.MenuArea.ANALYSIS, false) {

         @Override
         public void execute() {

            SextanteGUI.getGUIFactory().showHistoryDialog();

         }

      };

      final GGenericAction results = new GButtonGenericAction("SEXTANTE Results", 'R', new ImageIcon("images/chart.gif"),
               IGenericAction.MenuArea.ANALYSIS, false) {

         @Override
         public void execute() {

            SextanteGUI.getGUIFactory().showAdditionalResultsDialog(AdditionalResults.getComponents());
         }

      };

      final GGenericAction explorer = new GButtonGenericAction("SEXTANTE Data Explorer", 'E', new ImageIcon(
               "images/documenter.png"), IGenericAction.MenuArea.ANALYSIS, false) {

         @Override
         public void execute() {

            SextanteGUI.getGUIFactory().showDataExplorer();
         }

      };

      return GCollections.createList(toolbox, modeler, commandline, history, results, explorer);
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

      final Model model = application.getModel();
      es.unex.sextante.core.Sextante.initialize(getJarsFolder());
      SextanteGUI.initialize();
      SextanteGUI.setGUIFactory(new WWGUIFactory(model));
      SextanteGUI.setMainFrame(application.getFrame());
      SextanteGUI.setHelpPath(getHelpPath());
      SextanteGUI.setInputFactory(new WWInputFactory(model));
      SextanteGUI.setOutputFactory(new WWOutputFactory());
      SextanteWPSConfig.setOutputFactory(new WWOutputFactory());
      SextanteWPSConfig.setInputFactory(new WWInputFactory(model));
      SextanteGUI.setPostProcessTaskFactory(new WWPostProcessTaskFactory());
      History.startSession();

   }


   @Override
   public String getVersion() {
      return null;
   }


   @Override
   public String getName() {
      return "SEXTANTE Toolbox";
   }


   @Override
   public void initializeTranslations(final IGlobeApplication application) {
      // TODO Auto-generated method stub

   }

}
