

package es.igosoftware.experimental.vectorial;

import java.awt.Component;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.swing.Icon;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import es.igosoftware.euclid.projection.GProjection;
import es.igosoftware.euclid.shape.IPolygon2D;
import es.igosoftware.globe.GAbstractGlobeModule;
import es.igosoftware.globe.GLayerInfo;
import es.igosoftware.globe.IGlobeApplication;
import es.igosoftware.globe.IGlobeLayer;
import es.igosoftware.globe.ILayerFactoryModule;
import es.igosoftware.globe.ILayerInfo;
import es.igosoftware.globe.actions.IGenericAction;
import es.igosoftware.globe.actions.ILayerAction;
import es.igosoftware.globe.attributes.ILayerAttribute;
import es.igosoftware.io.GGenericFileFilter;
import es.igosoftware.io.GIOUtils;
import es.igosoftware.util.GPair;


public class GPolygon2DModule
         extends
            GAbstractGlobeModule
         implements
            ILayerFactoryModule {

   @Override
   public String getName() {
      return "Polygons 2D Module";
   }


   @Override
   public String getVersion() {
      return "experimental";
   }


   @Override
   public String getDescription() {
      return "Module for handling of vectorial layers";
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
   public List<GPair<String, Component>> getPanels(final IGlobeApplication application) {
      return null;
   }


   @Override
   public void initializeTranslations(final IGlobeApplication application) {

   }


   @Override
   public List<ILayerInfo> getAvailableLayers(final IGlobeApplication application) {
      return GLayerInfo.createFromNames("Vectorial file");
   }


   @Override
   public GPolygon2DLayer addNewLayer(final IGlobeApplication application,
                                      final ILayerInfo layerInfo) {

      final JFileChooser fileChooser = createSqueakProjectSaveFileChooser(application);


      final int returnVal = fileChooser.showOpenDialog(application.getFrame());
      if (returnVal == JFileChooser.APPROVE_OPTION) {
         final File selectedFile = fileChooser.getSelectedFile();
         if (selectedFile != null) {
            openFile(selectedFile, application);
         }
      }

      return null;
   }


   private void openFile(final File file,
                         final IGlobeApplication application) {
      final Thread worker = new Thread("Vectorial layer loader") {
         @Override
         public void run() {
            final int TODO_read_projection_or_ask_user;
            //            final boolean convertToRadians = true;
            final GProjection projection = GProjection.EPSG_4326;

            try {
               final GPair<String, List<IPolygon2D<?>>> polygons = GShapeLoader.readPolygons(file.getAbsolutePath(), projection);

               final GPolygon2DLayer layer = new GPolygon2DLayer(file.getName(), polygons._first, polygons._second);
               //               layer.setShowExtents(true);
               application.getLayerList().add(layer);

               layer.doDefaultAction(application);
            }
            catch (final IOException e) {
               SwingUtilities.invokeLater(new Runnable() {
                  @Override
                  public void run() {
                     JOptionPane.showMessageDialog(application.getFrame(), "Error opening " + file.getAbsolutePath() + "\n\n "
                                                                           + e.getLocalizedMessage(), "Error",
                              JOptionPane.ERROR_MESSAGE);
                  }
               });
            }
         }
      };

      worker.setDaemon(true);
      worker.setPriority(Thread.MIN_PRIORITY);
      worker.start();
   }


   private JFileChooser createSqueakProjectSaveFileChooser(final IGlobeApplication application) {
      final JFileChooser fileChooser = new JFileChooser(System.getProperty("user.home")) {
         private static final long serialVersionUID = 1L;


         @Override
         public String getTypeDescription(final File f) {
            final String extension = GIOUtils.getExtension(f);

            if ((extension != null) && extension.toLowerCase().equals("shp")) {
               return "SHP File";
            }

            return super.getDescription(f);
         }


         @Override
         public Icon getIcon(final File f) {
            if (f.isDirectory()) {
               return super.getIcon(f);
            }

            final String extension = GIOUtils.getExtension(f);

            if ((extension != null) && extension.toLowerCase().equals("shp")) {
               return application.getIcon("vectorial.png");
            }

            return super.getIcon(f);
         }
      };

      fileChooser.setMultiSelectionEnabled(false);
      fileChooser.setAcceptAllFileFilterUsed(false);
      fileChooser.setFileFilter(new GGenericFileFilter("shp", "SHP files (*.shp)"));
      //      fileChooser.setSelectedFile(new File(new File(fileName).getName()));

      return fileChooser;
   }


}
