

package es.igosoftware.io;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.Thread.UncaughtExceptionHandler;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Comparator;
import java.util.Iterator;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.TimeUnit;

import es.igosoftware.util.GAssert;
import es.igosoftware.util.GHolder;
import es.igosoftware.util.GLogger;
import es.igosoftware.util.GStringUtils;
import es.igosoftware.util.GUtils;


public class GHttpLoader
         implements
            ILoader {

   private static final GLogger LOGGER                         = GLogger.instance();

   private static final String  RENDERING_CACHE_DIRECTORY_NAME = "http-cache";
   private static final File    RENDERING_CACHE_DIRECTORY      = new File(RENDERING_CACHE_DIRECTORY_NAME);

   static {
      if (!RENDERING_CACHE_DIRECTORY.exists()) {
         RENDERING_CACHE_DIRECTORY.mkdirs();
      }
   }


   private class Task {
      private final String           _fileName;
      private final int              _priority;
      private final ILoader.IHandler _handler;

      private boolean                _isCanceled;


      private Task(final String fileName,
                   final int priority,
                   final ILoader.IHandler handler) {
         _fileName = fileName;
         _priority = priority;
         _handler = handler;
      }


      private void execute() {
         _loadCounter++;

         final File file = new File(_rootCacheDirectory, _fileName);

         if (file.exists()) {
            _loadCacheHits++;
            try {
               _handler.loaded(file, file.length(), true);
            }
            catch (final ILoader.AbortLoading e) {
               // do nothing, the file is already on the cache and there are no download to cancel
            }

            tryToShowStatistics();
            return;
         }

         final File tempFile = new File(_rootCacheDirectory, _fileName + ".part");
         if (tempFile.exists()) {
            LOGGER.severe("tempFile is present: " + tempFile);
         }

         InputStream is = null;
         OutputStream out = null;
         try {
            final URL url = new URL(_rootURL, _fileName);

            is = new BufferedInputStream(url.openStream());

            out = new BufferedOutputStream(new FileOutputStream(tempFile));

            GIOUtils.copy(is, out);
            is = null; // GIOUtils.copy() closes the in stream

            out.flush();
            out.close();
            out = null;

            if (!tempFile.renameTo(file)) {
               LOGGER.severe("can't rename " + tempFile + " to " + file);
            }

            final long bytesLoaded = file.length();
            _bytesDownloaded += bytesLoaded;

            if (!_isCanceled) {
               try {
                  _handler.loaded(file, bytesLoaded, true);
               }
               catch (final ILoader.AbortLoading e) {
                  // do nothing, the file is already downloaded
               }
            }
         }
         catch (final MalformedURLException e) {
            _handler.loadError(ILoader.ErrorType.NOT_FOUND, e);
         }
         catch (final IOException e) {
            _handler.loadError(ILoader.ErrorType.CANT_READ, e);
         }
         finally {
            GIOUtils.gentlyClose(is);
            GIOUtils.gentlyClose(out);
         }

         tryToShowStatistics();
      }


      private void cancel() {
         _isCanceled = true;
      }
   }


   private class Worker
            extends
               Thread
            implements
               UncaughtExceptionHandler {


      private Worker(final int id) {
         super("GHttpLoader " + _rootURL + ", worker #" + id);
         setDaemon(true);
         setPriority(MIN_PRIORITY);
         setUncaughtExceptionHandler(this);
      }


      @Override
      public void run() {
         try {
            while (true) {
               final Task task = _tasks.poll(100, TimeUnit.MILLISECONDS);
               if (task != null) {
                  if (task._isCanceled) {
                     continue;
                  }

                  task.execute();
               }
            }
         }
         catch (final InterruptedException e) {
            // do nothing, just exit from run()
         }
      }


      @Override
      public void uncaughtException(final Thread thread,
                                    final Throwable e) {
         LOGGER.severe("EncaughtException in thread " + thread, e);
      }
   }


   private final URL                         _rootURL;
   private final File                        _rootCacheDirectory;
   private final PriorityBlockingQueue<Task> _tasks;
   private final boolean                     _verbose;


   //   private int                               _loadCounter     = 0;
   //   private int                               _loadCacheHits   = 0;
   //   private long                              _bytesDownloaded = 0;


   public GHttpLoader(final URL root,
                      final int workersCount,
                      final boolean verbose) {
      GAssert.notNull(root, "root");

      if (!root.getProtocol().equals("http")) {
         throw new RuntimeException("Only http urls are supported");
      }

      _rootURL = root;
      _verbose = verbose;

      _rootCacheDirectory = new File(RENDERING_CACHE_DIRECTORY, getDirectoryName(_rootURL));
      if (!_rootCacheDirectory.exists()) {
         if (!_rootCacheDirectory.mkdirs()) {
            throw new RuntimeException("Can't create cache directory: " + _rootCacheDirectory.getAbsolutePath());
         }
      }

      _tasks = new PriorityBlockingQueue<Task>(25, new Comparator<Task>() {
         @Override
         public int compare(final Task task1,
                            final Task task2) {
            final int d1 = task1._priority;
            final int d2 = task2._priority;

            if (d1 < d2) {
               return -1;
            }
            else if (d1 > d2) {
               return 1;
            }
            else {
               return 0;
            }
         }
      });

      initializeWorkers(workersCount);
   }


   private static String getDirectoryName(final URL url) {
      String result = url.toString().replace("http://", "");

      if (result.endsWith("/")) {
         result = result.substring(0, result.length() - 1);
      }

      result = result.replace("/", "_");
      return result;
   }


   private void initializeWorkers(final int workersCount) {
      for (int i = 0; i < workersCount; i++) {
         new Worker(i).start();
      }
   }


   @Override
   public void load(final String fileName,
                    final long bytesToLoad,
                    final int priority,
                    final ILoader.IHandler handler) {
      GAssert.notNull(fileName, "fileName");
      GAssert.notNull(handler, "handler");

      if (bytesToLoad >= 0) {
         throw new RuntimeException("fragment downloading is not supported");
      }


      synchronized (_tasks) {
         for (final Task task : _tasks) {
            if (task._fileName.equals(fileName)) {
               throw new RuntimeException("Can't download the very same file at the same time (" + fileName + ")");
            }
         }

         _tasks.add(new Task(fileName, priority, handler));
      }


      //      tryToShowStatistics();

   }


   private void tryToShowStatistics() {
      if ((_loadCounter != 0) && ((_loadCounter % 25) == 0)) {
         //      if (_loadCounter != 0) {
         showStatistics();
      }
   }


   private void showStatistics() {
      if (!_verbose) {
         return;
      }

      final double hitsPercent = (double) _loadCacheHits / _loadCounter;
      LOGGER.info("HttpLoader \"" + _rootURL + "\": " + //
                  "loads=" + _loadCounter + ", " + //
                  "cache hits=" + _loadCacheHits + " (" + GStringUtils.formatPercent(hitsPercent) + "), " + //
                  "bytesDownloaded=" + _bytesDownloaded);
   }


   @Override
   public void cancelLoading(final String fileName) {
      synchronized (_tasks) {
         final Iterator<Task> iterator = _tasks.iterator();
         while (iterator.hasNext()) {
            final Task task = iterator.next();
            if (task._fileName.equals(fileName)) {
               task.cancel();
               iterator.remove();
            }
         }
      }
   }


   public static void main(final String[] args) throws MalformedURLException {
      final URL url = new URL("http://localhost/PANOS/cantabria1.jpg/");

      final GHttpLoader loader = new GHttpLoader(url, 2, true);

      final GHolder<Boolean> downloaded = new GHolder<Boolean>(false);

      final ILoader.IHandler handler = new ILoader.IHandler() {
         @Override
         public void loaded(final File file,
                            final long bytesLoaded,
                            final boolean completeLoaded) {
            //            if (!completeLoaded) {
            //               return;
            //            }

            System.out.println("loaded " + file + ", bytesLoaded=" + bytesLoaded + ", completeLoaded=" + completeLoaded);
            downloaded.set(true);
         }


         @Override
         public void loadError(final ILoader.ErrorType error,
                               final Throwable e) {
            System.out.println("Error=" + error + ", exception=" + e);
         }
      };
      loader.load("info.txt", -1, 1, handler);

      while (!downloaded.get()) {
         GUtils.delay(10);
      }
      loader.load("info.txt", -1, 1, handler);
      loader.load("info.txt", -1, 1, handler);
      loader.load("info.txt", -1, 1, handler);

   }


}
