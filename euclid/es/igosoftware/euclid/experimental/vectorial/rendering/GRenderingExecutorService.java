

package es.igosoftware.euclid.experimental.vectorial.rendering;

import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.RunnableFuture;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;


public class GRenderingExecutorService
         extends
            ThreadPoolExecutor {


   private static class MyThreadFactory
            implements
               ThreadFactory {
      private static final AtomicInteger poolNumber    = new AtomicInteger(1);

      private final ThreadGroup          _group;
      private final AtomicInteger        _threadNumber = new AtomicInteger(1);
      private final String               _namePrefix;


      private MyThreadFactory() {
         final SecurityManager s = System.getSecurityManager();
         _group = (s != null) ? s.getThreadGroup() : Thread.currentThread().getThreadGroup();
         _namePrefix = "pool-" + poolNumber.getAndIncrement() + "-thread-";
      }


      @Override
      public Thread newThread(final Runnable runnable) {
         final Thread thread = new Thread(_group, runnable, _namePrefix + _threadNumber.getAndIncrement(), 0);
         thread.setDaemon(true);
         thread.setPriority(Thread.MIN_PRIORITY);
         return thread;
      }
   }


   public GRenderingExecutorService(final int numberOfThreads) {
      super(numberOfThreads, numberOfThreads, 0L, TimeUnit.MILLISECONDS, new PriorityBlockingQueue<Runnable>(),
            new MyThreadFactory(), new ThreadPoolExecutor.DiscardPolicy());
   }


   //   @Override
   //   protected void beforeExecute(final Thread thread,
   //                                final Runnable runnable) {
   //      super.beforeExecute(thread, runnable);
   //   }


   //   @Override
   //   protected void afterExecute(final Runnable runnable,
   //                               final Throwable throwable) {
   //      super.afterExecute(runnable, throwable);
   //   }


   private static class ComparableFutureTask<T>
            extends
               FutureTask<T>
            implements
               Comparable<ComparableFutureTask<T>> {

      private final Comparable<T> _comparable;


      @SuppressWarnings("unchecked")
      private ComparableFutureTask(final Callable<T> callable) {
         super(callable);

         _comparable = (Comparable<T>) callable;
      }


      @SuppressWarnings("unchecked")
      private ComparableFutureTask(final Runnable runnable,
                                   final T result) {
         super(runnable, result);

         _comparable = (Comparable<T>) runnable;
      }


      @SuppressWarnings("unchecked")
      @Override
      public int compareTo(final ComparableFutureTask<T> that) {
         return _comparable.compareTo((T) that._comparable);
      }

   }


   @Override
   protected <T> RunnableFuture<T> newTaskFor(final Runnable runnable,
                                              final T value) {
      return new ComparableFutureTask<T>(runnable, value);
   }


   @Override
   protected <T> RunnableFuture<T> newTaskFor(final Callable<T> callable) {
      return new ComparableFutureTask<T>(callable);
   }

}
