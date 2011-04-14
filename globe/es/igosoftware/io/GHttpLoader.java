

package es.igosoftware.io;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.Thread.UncaughtExceptionHandler;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import es.igosoftware.util.GAssert;
import es.igosoftware.util.GLogger;
import es.igosoftware.util.GStringUtils;
import es.igosoftware.util.GUtils;


public class GHttpLoader
         implements
            ILoader {


   private static final GLogger LOGGER                       = GLogger.instance();

   private static final String  DEFAULT_CACHE_DIRECTORY_NAME = GUtils.isWindows() ? "http-cache" : ".http-cache";
   private static final File    DEFAULT_CACHE_DIRECTORY      = new File(DEFAULT_CACHE_DIRECTORY_NAME);


   private static class HandlerData {
      private final ILoader.LoadID   _loadID;
      private final ILoader.IHandler _handler;
      private final boolean          _reportIncompleteLoads;


      private HandlerData(final ILoader.LoadID loadID,
                          final ILoader.IHandler handler,
                          final boolean reportIncompleteLoads) {
         _loadID = loadID;
         _handler = handler;
         _reportIncompleteLoads = reportIncompleteLoads;
      }
   }


   private class Task {
      private final String            _fileName;
      private int                     _priority;
      private final List<HandlerData> _handlersData  = new LinkedList<HandlerData>();

      private boolean                 _isCanceled    = false;
      private boolean                 _isDownloading = false;


      private Task(final String fileName,
                   final int priority,
                   final HandlerData handlerData) {
         _fileName = fileName;
         _priority = priority;

         _handlersData.add(handlerData);
      }


      private void execute() {
         final File parentDirectory = new File(_rootCacheDirectory, _fileName).getParentFile();
         if (!parentDirectory.exists()) {
            synchronized (_rootCacheDirectory) {
               if (!parentDirectory.exists()) {
                  if (!parentDirectory.mkdirs()) {
                     notifyInternalError("can't create directory " + parentDirectory);
                  }
               }
            }
         }


         final long start = System.currentTimeMillis();

         File partFile = null;
         try {
            partFile = File.createTempFile(_fileName, ".part", _rootCacheDirectory);
         }
         catch (final IOException e) {
            notifyErrorToHandlers(e);
            return;
         }

         partFile.deleteOnExit(); // just in case...

         InputStream is = null;
         OutputStream out = null;
         try {
            final URL url = new URL(_rootURL, convertToURL(_fileName));

            is = new BufferedInputStream(url.openStream());

            out = new BufferedOutputStream(new FileOutputStream(partFile));

            copyDataToPartFile(is, out, partFile);
            is.close();
            is = null;

            out.flush();
            out.close();
            out = null;


            final File cacheFile = new File(_rootCacheDirectory, _fileName);

            if (!partFile.renameTo(cacheFile)) {
               notifyInternalError("can't rename " + partFile + " to " + cacheFile);
               return;
            }

            final long bytesLoaded = cacheFile.length();
            final long ellapsed = System.currentTimeMillis() - start;
            cacheMiss(bytesLoaded, ellapsed);

            if (!_isCanceled) {
               notifySuccessfullyLoadToHandlers(cacheFile, bytesLoaded, true);
            }
         }
         catch (final IOException e) {
            notifyErrorToHandlers(e);
         }
         finally {
            GIOUtils.gentlyClose(is);
            GIOUtils.gentlyClose(out);
         }
      }


      private void notifyInternalError(final String msg) {
         LOGGER.severe(msg);
         notifyErrorToHandlers(new IOException(msg));
      }


      private String convertToURL(final String url) {
         return url.replace(" ", "%20");
      }


      private void copyDataToPartFile(final InputStream in,
                                      final OutputStream out,
                                      final File partFile) throws IOException {
         final byte[] buf = new byte[4096];
         int len;
         int read = 0;
         while ((len = in.read(buf)) > 0) {
            out.write(buf, 0, len);
            out.flush();
            read += len;

            if (_simulateSlowConnection) {
               GUtils.delay(250);
            }

            notifySuccessfullyLoadToHandlers(partFile, read, false);
         }
      }


      private void notifySuccessfullyLoadToHandlers(final File cacheFile,
                                                    final long bytesLoaded,
                                                    final boolean completeLoaded) {

         synchronized (_tasks) {
            _tasks.remove(_fileName);
         }

         synchronized (_handlersData) {
            for (final HandlerData handlerData : _handlersData) {
               final boolean reportIncompleteLoads = handlerData._reportIncompleteLoads;
               if (completeLoaded || reportIncompleteLoads) {
                  try {
                     handlerData._handler.loaded(cacheFile, bytesLoaded, completeLoaded);
                  }
                  catch (final ILoader.AbortLoading e) {
                     // do nothing, the file is already downloaded
                  }
                  catch (final Exception e) {
                     LOGGER.severe("Error while notifying to " + handlerData._handler, e);
                  }
               }
            }

         }
      }


      private void notifyErrorToHandlers(final IOException e) {

         synchronized (_tasks) {
            _tasks.remove(_fileName);
         }

         synchronized (_handlersData) {
            for (final HandlerData handlerData : _handlersData) {
               try {
                  handlerData._handler.loadError(e);
               }
               catch (final Exception e2) {
                  LOGGER.severe("Error while notifying loadError to " + handlerData._handler, e2);
               }
            }
         }
      }


      private void cancel() {
         _isCanceled = true;
      }


      private void addHandler(final int priority,
                              final HandlerData handlerData) {
         synchronized (_handlersData) {
            _priority = Math.max(priority, _priority);
            _handlersData.add(handlerData);
            _isCanceled = false;
         }
      }
   }


   private void cacheHit() {
      synchronized (_statisticsMutex) {
         _loadCounter++;
         _loadCacheHits++;

         tryToShowStatistics();
      }
   }


   private void cacheMiss(final long bytesLoaded,
                          final long ellapsedTimeInMS) {
      synchronized (_statisticsMutex) {
         _loadCounter++;
         _bytesDownloaded += bytesLoaded;
         _downloadEllapsedTime += ellapsedTimeInMS;

         tryToShowStatistics();
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
         setPriority(MAX_PRIORITY);
         setUncaughtExceptionHandler(this);
      }


      @Override
      public void run() {
         try {
            while (true) {
               //               final Task task = _tasks.poll(1, TimeUnit.DAYS);
               final Task task = selectTask();
               if (task != null) {
                  if (task._isCanceled) {
                     continue; // ignored the canceled task
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
         LOGGER.severe("Uncaught exception in thread " + thread, e);
      }


      private Task selectTask() throws InterruptedException {
         Task selected = null;

         synchronized (_tasks) {
            final Set<Entry<GFileName, Task>> entries = _tasks.entrySet();
            for (final Entry<GFileName, Task> entry : entries) {
               final Task currentTask = entry.getValue();
               if (!currentTask._isDownloading && !currentTask._isCanceled) {
                  if ((selected == null)
                      || (currentTask._priority > selected._priority)
                      || ((currentTask._priority == selected._priority) && (currentTask._handlersData.size() > selected._handlersData.size()))) {
                     selected = currentTask;
                  }
               }
            }

            if (selected != null) {
               selected._isDownloading = true;
            }
         }

         if (selected == null) {
            Thread.sleep(10);
         }

         return selected;
      }
   }


   private final URL                  _rootURL;
   private final File                 _rootCacheDirectory;
   private final Map<GFileName, Task> _tasks                = new HashMap<GFileName, Task>();
   private final boolean              _verbose;
   private final boolean              _debug;
   private final boolean              _simulateSlowConnection;

   private final Object               _statisticsMutex      = new Object();
   private long                       _loadCounter          = 0;
   private long                       _loadCacheHits        = 0;
   private long                       _bytesDownloaded      = 0;
   private long                       _downloadEllapsedTime = 0;

   private int                        _loadID               = Integer.MIN_VALUE;


   public GHttpLoader(final URL root,
                      final int workersCount,
                      final boolean verbose) {
      this(root, workersCount, verbose, false, false);
   }


   public GHttpLoader(final URL root,
                      final int workersCount,
                      final boolean verbose,
                      final boolean debug) {
      this(root, workersCount, verbose, debug, false);
   }


   public GHttpLoader(final URL root,
                      final int workersCount,
                      final boolean verbose,
                      final boolean debug,
                      final boolean simulateSlowConnection) {
      GAssert.notNull(root, "root");
      GAssert.isPositive(workersCount, "workersCount");

      if (!root.getProtocol().equals("http")) {
         throw new RuntimeException("Only http URLs are supported");
      }

      _rootURL = root;
      _verbose = verbose;
      _debug = debug;
      _simulateSlowConnection = simulateSlowConnection;

      _rootCacheDirectory = new File(DEFAULT_CACHE_DIRECTORY, getDirectoryName(_rootURL));

      if (!_rootCacheDirectory.exists()) {
         if (!_rootCacheDirectory.mkdirs()) {
            throw new RuntimeException("Can't create cache directory \"" + _rootCacheDirectory.getAbsolutePath() + "\"");
         }
      }

      initializeWorkers(workersCount);
   }


   private static String getDirectoryName(final URL url) {
      String result = url.toString();

      if (result.endsWith("/")) {
         result = result.substring(0, result.length() - 1);
      }

      result = result.replace("http://", "");

      return GIOUtils.replaceIllegalFileNameCharacters(result);
   }


   private void initializeWorkers(final int workersCount) {
      for (int i = 0; i < workersCount; i++) {
         new Worker(i).start();
      }
   }


   @Override
   public ILoader.LoadID load(final GFileName fileName,
                              final long bytesToLoad,
                              final boolean reportIncompleteLoads,
                              final int priority,
                              final ILoader.IHandler handler) {
      GAssert.notNull(fileName, "fileName");
      GAssert.notNull(handler, "handler");


      if (fileName.isAbsolute()) {
         throw new RuntimeException("Absolutes fileNames are not supported");
      }

      if (_debug) {
         LOGGER.info("  -> DEBUG: load(" + fileName + ", " + bytesToLoad + ", " + priority + ")");
      }

      if (bytesToLoad >= 0) {
         throw new RuntimeException("fragment downloading is not supported");
      }

      final File cacheFile = new File(_rootCacheDirectory, fileName.buildPath());

      if (cacheFile.exists()) {
         cacheHit();

         try {
            handler.loaded(cacheFile, cacheFile.length(), true);
         }
         catch (final ILoader.AbortLoading e) {
            // do nothing, the file is already on the cache and there are no download to cancel
         }

         if (_debug) {
            LOGGER.info("  -> DEBUG: load(" + fileName + ", " + bytesToLoad + ", " + priority + ") done from cache!");
         }

         return null;
      }


      synchronized (_tasks) {
         final ILoader.LoadID loadID = new ILoader.LoadID(_loadID++);
         final HandlerData handlerData = new HandlerData(loadID, handler, reportIncompleteLoads);

         final Task existingTask = _tasks.get(fileName);
         if (existingTask == null) {
            _tasks.put(fileName, new Task(fileName.buildPath('/'), priority, handlerData));
         }
         else {
            existingTask.addHandler(priority, handlerData);
         }

         return loadID;
      }
   }


   private void tryToShowStatistics() {
      if (_verbose && (_loadCounter != 0) && ((_loadCounter % 50) == 0)) {
         showStatistics();
      }
      else if (_debug) {
         showStatistics();
      }
   }


   private void showStatistics() {
      final double hitsPercent = (double) _loadCacheHits / _loadCounter;

      final String msg = "HttpLoader \"" + _rootURL + "\": " + //
                         "loads=" + _loadCounter + ", " + //
                         "cache hits=" + _loadCacheHits + " (" + GStringUtils.formatPercent(hitsPercent) + ")";

      if (_bytesDownloaded != 0) {
         final double throughput = (double) _bytesDownloaded / _downloadEllapsedTime * 1000;
         LOGGER.info(msg + ", downloaded=" + GStringUtils.getSpaceMessage(_bytesDownloaded) + ", throughput="
                     + GStringUtils.getSpaceMessage(throughput) + "/s");
      }
      else {
         LOGGER.info(msg);
      }
   }


   @Override
   public void cancelLoad(final ILoader.LoadID id) {
      synchronized (_tasks) {

         final Iterator<Entry<GFileName, Task>> tasksIterator = _tasks.entrySet().iterator();

         while (tasksIterator.hasNext()) {
            final Entry<GFileName, Task> entry = tasksIterator.next();
            final Task task = entry.getValue();

            synchronized (task._handlersData) {
               final Iterator<HandlerData> handlersDataIterator = task._handlersData.iterator();
               while (handlersDataIterator.hasNext()) {
                  final HandlerData handlerData = handlersDataIterator.next();
                  if (id.equals(handlerData._loadID)) {
                     handlersDataIterator.remove();
                  }
               }

               if (!task._isDownloading) {
                  if (task._handlersData.isEmpty()) {
                     tasksIterator.remove();
                     task.cancel();
                  }
               }
            }
         }
      }
   }


   @Override
   public void cancelAllLoads(final GFileName fileName) {
      synchronized (_tasks) {
         final Task task = _tasks.remove(fileName);
         if (task != null) {
            task.cancel();
         }
      }
   }


}
