

package es.igosoftware.experimental.vectorial;

import java.io.File;
import java.io.IOException;

import javax.swing.JOptionPane;

import es.igosoftware.euclid.IBoundedGeometry;
import es.igosoftware.euclid.bounding.IFiniteBounds;
import es.igosoftware.euclid.features.IGlobeFeatureCollection;
import es.igosoftware.euclid.projection.GProjection;
import es.igosoftware.euclid.vector.IVector2;
import es.igosoftware.globe.GDragAndDropModule;
import es.igosoftware.globe.IGlobeApplication;
import es.igosoftware.util.GAssert;


public class GShapeLoaderDropHandler
         implements
            GDragAndDropModule.IDropFileHandler {


   private final IGlobeApplication _application;
   private final boolean           _confirmOpen;


   public GShapeLoaderDropHandler(final IGlobeApplication application,
                                  final boolean confirmOpen) {
      GAssert.notNull(application, "application");

      _application = application;
      _confirmOpen = confirmOpen;
   }


   @Override
   public String getDescription() {
      return "Shape file loading";
   }


   @Override
   public boolean acceptDirectories() {
      return false;
   }


   @Override
   public boolean acceptFile(final File droppedFile) {
      return droppedFile.getName().toLowerCase().endsWith(".shp");
   }


   private boolean confirmOpenFile(final File file) {
      final String[] options = { _application.getTranslation("Yes"), _application.getTranslation("No") };
      final String title = _application.getTranslation("Are you sure to open the file?");
      final String message = file.toString();

      final int answer = JOptionPane.showOptionDialog(_application.getFrame(), message, title, JOptionPane.YES_NO_OPTION,
               JOptionPane.QUESTION_MESSAGE, null, options, options[1]);

      return (answer == 0);
   }


   @Override
   public boolean processFile(final File droppedFile) {

      if (_confirmOpen && !confirmOpenFile(droppedFile)) {
         return false;
      }


      final Thread worker = new Thread() {
         @Override
         public void run() {
            final int TODO_read_projection_or_ask_user;
            final GProjection projection = GProjection.EPSG_4326;

            try {
               final IGlobeFeatureCollection<IVector2, ? extends IBoundedGeometry<IVector2, ? extends IFiniteBounds<IVector2, ?>>> featuresCollection = GShapeLoader.readFeatures(
                        droppedFile, projection);

               final GVectorial2DLayer layer = new GVectorial2DLayer(droppedFile.getName(), featuresCollection);
               _application.addLayer(layer);
               layer.doDefaultAction(_application);
            }
            catch (final IOException e) {
               _application.logSevere("Error trying to load: " + droppedFile, e);
            }
         }
      };
      worker.setPriority(Thread.MIN_PRIORITY);
      worker.setDaemon(true);
      worker.start();

      return true;
   }

}
