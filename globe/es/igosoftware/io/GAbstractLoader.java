/*

 IGO Software SL  -  info@igosoftware.es

 http://www.glob3.org

-------------------------------------------------------------------------------
 Copyright (c) 2010, IGO Software SL
 All rights reserved.

 Redistribution and use in source and binary forms, with or without
 modification, are permitted provided that the following conditions are met:
     * Redistributions of source code must retain the above copyright
       notice, this list of conditions and the following disclaimer.
     * Redistributions in binary form must reproduce the above copyright
       notice, this list of conditions and the following disclaimer in the
       documentation and/or other materials provided with the distribution.
     * Neither the name of the IGO Software SL nor the
       names of its contributors may be used to endorse or promote products
       derived from this software without specific prior written permission.

 THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 DISCLAIMED. IN NO EVENT SHALL IGO Software SL BE LIABLE FOR ANY
 DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
-------------------------------------------------------------------------------

*/


package es.igosoftware.io;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.PriorityBlockingQueue;

import es.igosoftware.util.GUtils;
import es.igosoftware.util.LoggerObject;


abstract class GAbstractLoader
         extends
            LoggerObject
         implements
            ILoader,
            Thread.UncaughtExceptionHandler {


   //   private static class TaskId {
   //      private final String _name;
   //
   //
   //      //      private final int    _bytesToLoad;
   //
   //
   //      private TaskId(final String _name/*, final int bytesToLoad*/) {
   //         _name = _name;
   //         //         _bytesToLoad = bytesToLoad;
   //      }
   //
   //
   //      @Override
   //      public int hashCode() {
   //         final int prime = 31;
   //         int result = 1;
   //         //         result = prime * result + _bytesToLoad;
   //         result = prime * result + ((_name == null) ? 0 : _name.hashCode());
   //         return result;
   //      }
   //
   //
   //      @Override
   //      public boolean equals(final Object obj) {
   //         if (this == obj) {
   //            return true;
   //         }
   //         if (obj == null) {
   //            return false;
   //         }
   //         if (getClass() != obj.getClass()) {
   //            return false;
   //         }
   //         final TaskId other = (TaskId) obj;
   //         //         if (_bytesToLoad != other._bytesToLoad) {
   //         //            return false;
   //         //         }
   //         if (_name == null) {
   //            if (other._name != null) {
   //               return false;
   //            }
   //         }
   //         else if (!_name.equals(other._name)) {
   //            return false;
   //         }
   //         return true;
   //      }
   //
   //
   //      @Override
   //      public String toString() {
   //         //return "TaskId [_name=" + _name + ", bytesToLoad=" + _bytesToLoad + "]";
   //         return "TaskId [_name=" + _name + "]";
   //      }
   //
   //   }


   protected abstract static class Task
            implements
               Runnable {

      private final String _name;
      private final int    _priority;


      protected Task(final String name,
                     final int priority) {
         _name = name;
         _priority = priority;
      }


      protected abstract void stop();
   }


   private final PriorityBlockingQueue<Task> _tasksQueue  = new PriorityBlockingQueue<Task>(1000, new Comparator<Task>() {
                                                             @Override
                                                             public int compare(final Task task1,
                                                                                final Task task2) {
                                                                final int priority1 = task1._priority;
                                                                final int priority2 = task2._priority;
                                                                if (priority1 == priority2) {
                                                                   return 0;
                                                                }
                                                                else if (priority1 > priority2) {
                                                                   return -1;
                                                                }
                                                                else {
                                                                   return 1;
                                                                }
                                                             }
                                                          });
   private final Map<String, Task>           _activeTasks = Collections.synchronizedMap(new HashMap<String, Task>());


   protected GAbstractLoader() {
      initializeWorkers();
   }


   private void initializeWorkers() {
      final int workersCount = Math.max(Runtime.getRuntime().availableProcessors() / 2, 2);
      //      final int workersCount = 1;

      final ThreadGroup group = new ThreadGroup("Loader Workers Group");
      group.setDaemon(true);
      group.setMaxPriority(Thread.MIN_PRIORITY);
      //group.setMaxPriority(Thread.MAX_PRIORITY);

      for (int i = 0; i < workersCount; i++) {
         createWorker(group, i);
      }
   }


   private void createWorker(final ThreadGroup group,
                             final int i) {
      final Thread thread = new Thread(group, "Loader Worker #" + i) {
         @Override
         public void run() {
            //            try {
            while (true) {
               final Task task = _tasksQueue.poll();
               if (task == null) {
                  GUtils.delay(50);
                  continue;
               }

               synchronized (_activeTasks) {
                  final Task currentSimilarTask = _activeTasks.get(task._name);
                  if (currentSimilarTask != null) {
                     currentSimilarTask.stop();
                  }
                  _activeTasks.put(task._name, task);
               }

               try {
                  task.run();
               }
               catch (final Throwable e) {
                  uncaughtException(this, e);
               }
               finally {
                  _activeTasks.remove(task);
               }
            }
            //            }
            //            catch (final InterruptedException e) {
            //               e.printStackTrace(System.err);
            //            }
         }
      };

      thread.setDaemon(true);
      thread.setPriority(Thread.MIN_PRIORITY);
      //thread.setPriority(Thread.MAX_PRIORITY);

      thread.start();
   }


   protected void submitTask(final Task task) {
      //GAssert.notNull(task, "task");


      //      task.run();
      //      synchronized (_tasksQueue) {
      final Iterator<Task> iterator = _tasksQueue.iterator();
      while (iterator.hasNext()) {
         final Task oldTask = iterator.next();
         if (oldTask._name.equals(task._name)) {
            iterator.remove();
         }
      }

      _tasksQueue.add(task);
      //      }
   }


   @Override
   public boolean logVerbose() {
      return true;
   }


   @Override
   public void uncaughtException(final Thread t,
                                 final Throwable e) {
      logSevere("Thread " + t, e);
   }


   @Override
   public final void cancelLoading(final String taskName) {
      //      synchronized (_tasksQueue) {
      final Iterator<Task> iterator = _tasksQueue.iterator();
      while (iterator.hasNext()) {
         final Task oldTask = iterator.next();
         if (oldTask._name.equals(taskName)) {
            iterator.remove();
         }
      }
      //      }
   }


}
