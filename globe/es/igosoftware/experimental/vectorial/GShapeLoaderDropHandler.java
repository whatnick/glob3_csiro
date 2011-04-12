

package es.igosoftware.experimental.vectorial;

import java.io.File;
import java.io.IOException;

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


   public GShapeLoaderDropHandler(final IGlobeApplication application) {
      GAssert.notNull(application, "application");

      _application = application;
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


   @Override
   public boolean processFile(final File droppedFile) {
      final Thread worker = new Thread() {
         @Override
         public void run() {
            System.out.println("Processing file: " + droppedFile);

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
