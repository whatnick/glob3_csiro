

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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import es.igosoftware.util.GAssert;
import es.igosoftware.util.GLogger;
import es.igosoftware.util.GPair;
import es.igosoftware.util.GStringUtils;


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
      private final String                        _fileName;
      private int                                 _priority;
      //      private final ILoader.IHandler _handler;
      private final List<GPair<LoadID, IHandler>> _handlers;

      private boolean                             _isCanceled    = false;
      private boolean                             _isDownloading = false;


      private Task(final String fileName,
                   final int priority,
                   final GPair<LoadID, IHandler> idAndHandler) {
         _fileName = fileName;
         _priority = priority;
         _handlers = new ArrayList<GPair<LoadID, IHandler>>(2);
         _handlers.add(idAndHandler);
      }


      private void execute() {
         //         final File partFile = new File(_rootCacheDirectory, _fileName + ".part");

         final File directory = new File(_rootCacheDirectory, _fileName).getParentFile();
         synchronized (_rootCacheDirectory) {
            if (!directory.exists()) {
               if (!directory.mkdirs()) {
                  LOGGER.severe("can't create directory " + directory);
               }
            }
         }

         File partFile = null;
         try {
            partFile = File.createTempFile(_fileName, ".part", _rootCacheDirectory);
         }
         catch (final IOException e) {
            notifyErrorToHandlers(ILoader.ErrorType.NOT_FOUND, e);
            return;
         }

         partFile.deleteOnExit(); // just in case...

         final long start = System.currentTimeMillis();

         InputStream is = null;
         OutputStream out = null;
         try {
            final URL url = new URL(_rootURL, _fileName);

            is = new BufferedInputStream(url.openStream());

            out = new BufferedOutputStream(new FileOutputStream(partFile));

            GIOUtils.copy(is, out);
            is = null; // GIOUtils.copy() closes the in stream

            out.flush();
            out.close();
            out = null;

            final long ellapsed = System.currentTimeMillis() - start;

            synchronized (_rootCacheDirectory) {
               final File cacheFile = new File(_rootCacheDirectory, _fileName);

               if (!partFile.renameTo(cacheFile)) {
                  LOGGER.severe("can't rename " + partFile + " to " + cacheFile);
                  notifyErrorToHandlers(ILoader.ErrorType.CANT_READ, null);
                  return;
               }

               final long bytesLoaded = cacheFile.length();
               cacheMiss(bytesLoaded, ellapsed);

               if (!_isCanceled) {
                  notifyLoadToHandlers(cacheFile, bytesLoaded);
               }
            }
         }
         catch (final MalformedURLException e) {
            notifyErrorToHandlers(ILoader.ErrorType.NOT_FOUND, e);
         }
         catch (final IOException e) {
            notifyErrorToHandlers(ILoader.ErrorType.CANT_READ, e);
         }
         finally {
            GIOUtils.gentlyClose(is);
            GIOUtils.gentlyClose(out);
         }
      }


      private synchronized void notifyLoadToHandlers(final File cacheFile,
                                                     final long bytesLoaded) {

         synchronized (_tasks) {
            _tasks.remove(_fileName);
         }

         try {
            for (final GPair<LoadID, IHandler> idAndHandler : _handlers) {
               final ILoader.IHandler handler = idAndHandler._second;
               handler.loaded(cacheFile, bytesLoaded, true);
            }
         }
         catch (final ILoader.AbortLoading e) {
            // do nothing, the file is already downloaded
         }
      }


      private synchronized void notifyErrorToHandlers(final ILoader.ErrorType error,
                                                      final Throwable exception) {
         synchronized (_tasks) {
            _tasks.remove(_fileName);
         }

         for (final GPair<LoadID, IHandler> idAndHandler : _handlers) {
            final ILoader.IHandler handler = idAndHandler._second;
            handler.loadError(error, exception);
         }
      }


      private void cancel() {
         _isCanceled = true;
      }


      private synchronized void addHandler(final int priority,
                                           final GPair<ILoader.LoadID, ILoader.IHandler> idAndHandler) {
         _priority = Math.max(priority, _priority);
         _handlers.add(idAndHandler);
         _isCanceled = false;
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
            final Set<Entry<String, Task>> entries = _tasks.entrySet();
            for (final Entry<String, Task> entry : entries) {
               final Task current = entry.getValue();
               if (!current._isDownloading && !current._isCanceled) {
                  if ((selected == null) || (current._priority > selected._priority)) {
                     selected = current;
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


   private final URL               _rootURL;
   private final File              _rootCacheDirectory;
   private final Map<String, Task> _tasks                = new HashMap<String, Task>();
   private final boolean           _verbose;
   private final boolean           _debug;

   private final Object            _statisticsMutex      = new Object();
   private int                     _loadCounter          = 0;
   private int                     _loadCacheHits        = 0;
   private long                    _bytesDownloaded      = 0;
   private long                    _downloadEllapsedTime = 0;

   private int                     _loadID               = Integer.MIN_VALUE;


   public GHttpLoader(final URL root,
                      final int workersCount,
                      final boolean verbose) {
      this(root, null, workersCount, verbose, false);
   }


   public GHttpLoader(final URL root,
                      final File cacheRootDirectory,
                      final int workersCount,
                      final boolean verbose) {
      this(root, cacheRootDirectory, workersCount, verbose, false);
   }


   public GHttpLoader(final URL root,
                      final int workersCount,
                      final boolean verbose,
                      final boolean debug) {
      this(root, RENDERING_CACHE_DIRECTORY, workersCount, verbose, debug);
   }


   public GHttpLoader(final URL root,
                      final File cacheRootDirectory,
                      final int workersCount,
                      final boolean verbose,
                      final boolean debug) {
      GAssert.notNull(root, "root");

      if (!root.getProtocol().equals("http")) {
         throw new RuntimeException("Only http URLs are supported");
      }


      _rootURL = root;
      _verbose = verbose;
      _debug = debug;

      if (cacheRootDirectory == null) {
         _rootCacheDirectory = new File(RENDERING_CACHE_DIRECTORY, getDirectoryName(_rootURL));
      }
      else {
         _rootCacheDirectory = new File(cacheRootDirectory, getDirectoryName(_rootURL));
      }

      //      System.out.println("root cache dir : " + _rootCacheDirectory);
      if (!_rootCacheDirectory.exists()) {
         if (!_rootCacheDirectory.mkdirs()) {
            throw new RuntimeException("Can't create cache directory: " + _rootCacheDirectory.getAbsolutePath());
         }
      }

      initializeWorkers(workersCount);
   }


   private static String getDirectoryName(final URL url) {
      String result = url.toString().replace("http://", "");

      if (result.endsWith("/")) {
         result = result.substring(0, result.length() - 1);
      }

      result = result.replace("/", "_");
      result = result.replace(":", "_");

      return result;
   }


   private void initializeWorkers(final int workersCount) {
      for (int i = 0; i < workersCount; i++) {
         new Worker(i).start();
      }
   }


   @Override
   public ILoader.LoadID load(final String fileName,
                              final long bytesToLoad,
                              final int priority,
                              final ILoader.IHandler handler) {
      GAssert.notNull(fileName, "fileName");
      GAssert.notNull(handler, "handler");

      if (_debug) {
         LOGGER.info("load(" + fileName + ", " + bytesToLoad + ", " + priority + ", " + handler);
      }

      if (bytesToLoad >= 0) {
         throw new RuntimeException("fragment downloading is not supported");
      }

      final File file = new File(_rootCacheDirectory, fileName);

      if (file.exists()) {
         cacheHit();

         try {
            handler.loaded(file, file.length(), true);
         }
         catch (final ILoader.AbortLoading e) {
            // do nothing, the file is already on the cache and there are no download to cancel
         }

         return null;
      }


      synchronized (_tasks) {
         final ILoader.LoadID loadID = new ILoader.LoadID(_loadID++);
         final GPair<ILoader.LoadID, ILoader.IHandler> idAndHandler = new GPair<ILoader.LoadID, ILoader.IHandler>(loadID, handler);

         final Task existingTask = _tasks.get(fileName);
         if (existingTask == null) {
            _tasks.put(fileName, new Task(fileName, priority, idAndHandler));
         }
         else {
            existingTask.addHandler(priority, idAndHandler);
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

         final Iterator<Entry<String, Task>> tasksIterator = _tasks.entrySet().iterator();

         while (tasksIterator.hasNext()) {
            final Entry<String, Task> entry = tasksIterator.next();
            final Task task = entry.getValue();

            final Iterator<GPair<ILoader.LoadID, ILoader.IHandler>> taskHandlersIterator = task._handlers.iterator();
            while (taskHandlersIterator.hasNext()) {
               final GPair<ILoader.LoadID, ILoader.IHandler> idAndHandler = taskHandlersIterator.next();
               if (id.equals(idAndHandler._first)) {
                  taskHandlersIterator.remove();
               }
            }

            if (!task._isDownloading) {
               if (task._handlers.isEmpty()) {
                  tasksIterator.remove();
                  task.cancel();
               }
            }
         }
      }
   }


   @Override
   public void cancelAllLoads(final String fileName) {
      synchronized (_tasks) {
         final Task task = _tasks.remove(fileName);
         if (task != null) {
            task.cancel();
         }
      }
   }

}
