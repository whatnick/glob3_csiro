package es.igosoftware.io;

import java.io.File;

import es.igosoftware.util.GAssert;

public class GFileLoader
         extends
            GAbstractLoader {

   protected final File _rootDirectory;


   public GFileLoader(final String rootDirectoryName) {
      GAssert.notNull(rootDirectoryName, "rootDirectoryName");

      _rootDirectory = new File(rootDirectoryName);
      validateRootDirectory();
   }


   private void validateRootDirectory() {
      if (!_rootDirectory.exists()) {
         throw new IllegalArgumentException("Root directory not found (" + _rootDirectory.getAbsolutePath() + ")");
      }

      if (!_rootDirectory.isDirectory()) {
         throw new IllegalArgumentException(_rootDirectory.getAbsolutePath() + " is not a directory");
      }

   }


   public GFileLoader(final File rootDirectory) {
      GAssert.notNull(rootDirectory, "rootDirectory");

      _rootDirectory = rootDirectory;
      validateRootDirectory();
   }


   @Override
   public void load(final String fileName,
                    final int bytesToLoad,
                    final int priority,
                    final ILoader.IHandler handler) {

      //      submitTask(new GAbstractLoader.Task(fileName, priority) {
      //         private boolean _stoped = false;
      //
      //
      //         @Override
      //         public void run() {
      //            if (_stoped) {
      //               return;
      //            }

      final File file = new File(_rootDirectory, fileName);

      if (!file.exists()) {
         handler.loadError(file, ILoader.ErrorType.NOT_FOUND);
      }
      else if (!file.canRead()) {
         handler.loadError(file, ILoader.ErrorType.CANT_READ);
      }
      else {
         try {
            //                  final boolean simulateSlowLoad = false;
            //
            //                  if (simulateSlowLoad) {
            //                     final int stepWidth = 1024;
            //
            //                     int available = stepWidth;
            //                     while (available <= bytesToLoad) {
            //                        handler.loaded(file, available);
            //                        Utils.delay(5);
            //                        available += stepWidth;
            //                     }
            //                     if (available != bytesToLoad + stepWidth) {
            //                        handler.loaded(file, available);
            //                     }
            //                  }
            //                  else {
            // in files, all the needed bytes are available in a shot 
            final int bytesLoaded = bytesToLoad;
            handler.loaded(file, bytesLoaded);
            //                  }
         }
         catch (final ILoader.AbortLoading e) {
            // do nothing
         }
      }

      //         }
      //
      //
      //         @Override
      //         protected void stop() {
      //            _stoped = true;
      //            handler.stop();
      //         }
      //      });


   }


}
