package es.igosoftware.io;

import java.io.File;
import java.util.ArrayList;

public final class GPointsCloudFileLoader
         extends
            GFileLoader
         implements
            IPointsCloudLoader {

   public GPointsCloudFileLoader(final File rootDirectory) {
      super(rootDirectory);
   }


   public GPointsCloudFileLoader(final String rootDirectoryName) {
      super(rootDirectoryName);
   }


   @Override
   public String[] getPointsCloudsNames() {
      final ArrayList<String> result = new ArrayList<String>();

      for (final File child : _rootDirectory.listFiles()) {
         if (child.isDirectory() && child.canRead()) {
            final File pointsCloudFile = new File(child, "tree.object.gz");
            if (pointsCloudFile.exists() && pointsCloudFile.canRead()) {
               result.add(child.getName());
            }
         }
      }

      return result.toArray(new String[] {});
   }

}
