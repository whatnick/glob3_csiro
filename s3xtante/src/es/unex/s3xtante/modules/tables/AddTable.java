package es.unex.s3xtante.modules.tables;

import java.awt.Component;
import java.io.File;
import java.util.Collections;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.table.DefaultTableModel;

import es.igosoftware.globe.GAbstractGlobeModule;
import es.igosoftware.globe.IGlobeApplication;
import es.igosoftware.globe.IGlobeLayer;
import es.igosoftware.globe.actions.GButtonGenericAction;
import es.igosoftware.globe.actions.IGenericAction;
import es.igosoftware.globe.actions.ILayerAction;
import es.igosoftware.globe.attributes.ILayerAttribute;
import es.igosoftware.util.GPair;
import es.unex.s3xtante.modules.sextante.bindings.WWTable;
import es.unex.s3xtante.tables.CSVFileTools;
import es.unex.s3xtante.tables.Tables;
import es.unex.sextante.gui.algorithm.GenericFileFilter;

public class AddTable
         extends
            GAbstractGlobeModule {

   @Override
   public String getDescription() {

      return "Add Table";

   }


   @Override
   public List<IGenericAction> getGenericActions(final IGlobeApplication application) {

      final IGenericAction graticule = new GButtonGenericAction("Add table", ' ', null, IGenericAction.MenuArea.FILE, false) {

         @Override
         public void execute() {

            final JFileChooser fc = new JFileChooser();
            fc.setFileFilter(new GenericFileFilter(new String[] { "csv" }, "Comma-separated values (*.csv)"));
            final int returnVal = fc.showOpenDialog(application.getFrame());

            if (returnVal == JFileChooser.APPROVE_OPTION) {
               final String sFilename = fc.getSelectedFile().getAbsolutePath();
               try {
                  final DefaultTableModel model = CSVFileTools.read(new File(sFilename));
                  if (model != null) {
                     final WWTable table = new WWTable();
                     table.create(model, sFilename);
                     Tables.addTable(table);
                  }
               }
               catch (final Exception e) {
                  //TODO:
               }
            }
         }

      };

      //      return new IGenericAction[] { graticule };
      return Collections.singletonList(graticule);
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
      return "Add table";
   }


   @Override
   public String getVersion() {
      return null;
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
