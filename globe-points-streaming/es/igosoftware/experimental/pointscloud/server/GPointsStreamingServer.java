

package es.igosoftware.experimental.pointscloud.server;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.zip.GZIPInputStream;

import es.igosoftware.dmvc.model.GDModel;
import es.igosoftware.dmvc.model.IDProperty;
import es.igosoftware.dmvc.server.GDServer;
import es.igosoftware.euclid.pointscloud.octree.GPCPointsCloud;
import es.igosoftware.experimental.pointscloud.model.IPointsStreamingServer;
import es.igosoftware.io.GIOUtils;
import es.igosoftware.util.GAssert;


public class GPointsStreamingServer
         extends
            GDModel
         implements
            IPointsStreamingServer {


   private final File _pointsCloudsDirectory;


   public GPointsStreamingServer(final String pointsCloudsDirectoryName) {
      GAssert.notNull(pointsCloudsDirectoryName, "pointsCloudsDirectoryName");

      _pointsCloudsDirectory = initializePointsCloudsDirectory(pointsCloudsDirectoryName);
   }


   private File initializePointsCloudsDirectory(final String pointsCloudsDirectoryName) {
      final File pointsCloudsDirectory = new File(pointsCloudsDirectoryName);

      if (!pointsCloudsDirectory.exists() || !pointsCloudsDirectory.canRead()) {
         throw new RuntimeException("Invalid pointsCloudsDirectoryName (" + pointsCloudsDirectory.getAbsolutePath() + ")");
      }

      return pointsCloudsDirectory;
   }


   @Override
   protected List<IDProperty> defaultProperties() {
      return Collections.emptyList();
   }


   @Override
   public List<String> getPointsCloudsNames() {
      final String[] directoriesNames = _pointsCloudsDirectory.list(new FilenameFilter() {
         @Override
         public boolean accept(final File dir,
                               final String name) {
            final File file = new File(dir, name);
            if (!file.isDirectory()) {
               return false;
            }

            final File treeFile = new File(file, "tree.object.gz");
            return treeFile.exists();
         }
      });

      return Arrays.asList(directoriesNames);
   }


   @Override
   public GPCPointsCloud getPointsCloud(final String pointsCloudName) throws IOException {
      ObjectInputStream input = null;
      try {
         final File treeObjectGZFile = new File(new File(_pointsCloudsDirectory, pointsCloudName), "/tree.object.gz");

         //         final byte[] bytes = GIOUtils.getBytesFromFile(treeObjectGZFile);

         input = new ObjectInputStream(new GZIPInputStream(new FileInputStream(treeObjectGZFile), 2048));

         final GPCPointsCloud pointsCloud = (GPCPointsCloud) input.readObject();

         return pointsCloud;
      }
      catch (final ClassNotFoundException ex) {
         throw new IOException(ex);
      }
      finally {
         GIOUtils.gentlyClose(input);
      }
   }


   @SuppressWarnings("unused")
   public static void main(final String[] args) {
      System.out.println("GPointsStreamingServer 0.1");
      System.out.println("--------------------------\n");


      if ((args.length < 1) || (args.length > 2)) {
         System.err.println("Usage: " + GPointsStreamingServer.class.getSimpleName() + " pointsCloudDirectoryName [<port>]");
         return;
      }

      final String pointsCloudDirectoryName = args[0];
      final int port = (args.length >= 2) ? Integer.parseInt(args[1]) : 8000;

      final GPointsStreamingServer model = new GPointsStreamingServer(pointsCloudDirectoryName);

      new GDServer(port, model, true);
   }


}
