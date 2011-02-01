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


package es.igosoftware.experimental.pointscloud.loading;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;

import org.jboss.netty.channel.Channel;

import es.igosoftware.dmvc.model.GDModel;
import es.igosoftware.dmvc.model.IDProperty;
import es.igosoftware.dmvc.server.GDServer;
import es.igosoftware.euclid.pointscloud.octree.GPCPointsCloud;
import es.igosoftware.euclid.vector.GVector3F;
import es.igosoftware.euclid.vector.IVector3;
import es.igosoftware.io.GIOUtils;
import es.igosoftware.util.GAssert;
import es.igosoftware.util.LRUCache;


public class GPointsStreamingServer
         extends
            GDModel
         implements
            IPointsStreamingServer {

   private static final int                                    POINTS_GROUP_SIZE  = 1024;
   private static final int                                    BYTES_PER_VECTOR3F = 3 * 4;                                        // x, y, z * float 

   private final File                                          _pointsCloudsDirectory;
   private final LinkedList<Task>                              _tasks             = new LinkedList<GPointsStreamingServer.Task>();

   private int                                                 TASK_ID_COUNTER    = 0;
   private final Map<Integer, Long>                            _lastSends         = new HashMap<Integer, Long>();

   private final LRUCache<String, GPCPointsCloud, IOException> _pointsCloudCache;


   public GPointsStreamingServer(final String pointsCloudsDirectoryName) {
      GAssert.notNull(pointsCloudsDirectoryName, "pointsCloudsDirectoryName");

      _pointsCloudsDirectory = initializePointsCloudsDirectory(pointsCloudsDirectoryName);

      _pointsCloudCache = new LRUCache<String, GPCPointsCloud, IOException>(25,
               new LRUCache.ValueFactory<String, GPCPointsCloud, IOException>() {
                  @Override
                  public GPCPointsCloud create(final String pointsCloudName) throws IOException {
                     ObjectInputStream input = null;
                     try {
                        final File treeObjectGZFile = new File(new File(_pointsCloudsDirectory, pointsCloudName),
                                 "/tree.object.gz");

                        input = new ObjectInputStream(new GZIPInputStream(new FileInputStream(treeObjectGZFile), 2048));

                        final GPCPointsCloud pointsCloud = (GPCPointsCloud) input.readObject();

                        return pointsCloud;
                     }
                     catch (final ClassNotFoundException ex) {
                        return null;
                     }
                     finally {
                        GIOUtils.gentlyClose(input);
                     }
                  }
               });


      initializeWorker();
   }


   private Thread initializeWorker() {

      final Thread worker = new Thread() {
         @Override
         public void run() {
            try {
               while (true) {
                  final Task task = selectTask();

                  if (task == null) {
                     Thread.sleep(150);
                  }
                  else {
                     synchronized (_lastSends) {
                        _lastSends.put(task._sessionID, System.currentTimeMillis());
                     }
                     final GPointsData result = task.execute();
                     if (result != null) {
                        firePropertyChange("points_" + task._sessionID, null, result);
                     }
                  }
               }
            }
            catch (final InterruptedException e) {
               // do nothing, just exit from run()
            }
         }
      };

      worker.setDaemon(true);
      worker.setPriority(Thread.MAX_PRIORITY);
      worker.start();

      return worker;
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
   public GPCPointsCloud getPointsCloud(final String pointsCloudName) {
      try {
         return _pointsCloudCache.get(pointsCloudName);
      }
      catch (final IOException e) {
         return null;
      }
   }


   private class Task {

      private final String _pointsCloudName;
      private final String _tileID;
      private final int    _from;
      private final int    _to;
      private int          _priority;
      private final int    _taskID;
      private final int    _sessionID;


      private Task(final String pointsCloudName,
                   final String tileID,
                   final int from,
                   final int to,
                   final int priority,
                   final int taskID,
                   final int sessionID) {
         _pointsCloudName = pointsCloudName;
         _tileID = tileID;
         _from = from;
         _to = to;
         _priority = priority;
         _taskID = taskID;
         _sessionID = sessionID;
      }


      private GPointsData execute() {
         final GPCPointsCloud pointsCloud = getPointsCloud(_pointsCloudName);
         if (pointsCloud == null) {
            return null;
         }

         final boolean hasColors = pointsCloud.hasColors();
         final boolean hasIntensities = pointsCloud.hasIntensities();
         final boolean hasNormals = pointsCloud.hasNormals();

         final int bytesPerPoint = BYTES_PER_VECTOR3F + //
                                   (hasColors ? 4 : 0) + //
                                   (hasIntensities ? 4 : 0) + // 
                                   (hasNormals ? BYTES_PER_VECTOR3F : 0);

         final String tileFileName = _pointsCloudName + "/tile-" + _tileID + ".points";
         final File tileFile = new File(_pointsCloudsDirectory, tileFileName);

         DataInputStream input = null;
         try {
            input = new DataInputStream(new BufferedInputStream(new FileInputStream(tileFile.getAbsolutePath()), 16 * 1024));

            input.skipBytes(_from * bytesPerPoint);

            final List<IVector3<?>> points = new ArrayList<IVector3<?>>(_to - _from + 1);
            final List<Float> intensities = hasIntensities ? new ArrayList<Float>(_to - _from + 1) : null;
            final List<IVector3<?>> normals = hasNormals ? new ArrayList<IVector3<?>>(_to - _from + 1) : null;
            final List<Integer> colors = hasColors ? new ArrayList<Integer>(_to - _from + 1) : null;

            try {
               for (int i = _from; i <= _to; i++) {
                  final GVector3F point = readVector3F(input);
                  points.add(point);

                  if (hasIntensities) {
                     final float intensity = input.readFloat();
                     intensities.add(intensity);
                  }

                  if (hasNormals) {
                     final GVector3F normal = readVector3F(input);
                     normals.add(normal);
                  }

                  if (hasColors) {
                     final int color = input.readInt();
                     colors.add(color);
                  }
               }
            }
            catch (final EOFException eof) {

            }


            return new GPointsData(points, intensities, normals, colors);
         }
         catch (final IOException e) {
            e.printStackTrace();
            return null;
         }
         finally {
            GIOUtils.gentlyClose(input);
         }
      }
   }


   private static GVector3F readVector3F(final DataInputStream input) throws IOException {
      final float x = input.readFloat();
      final float y = input.readFloat();
      final float z = input.readFloat();
      return new GVector3F(x, y, z);
   }


   @Override
   public int loadPoints(final int sessionID,
                         final String pointsCloudName,
                         final String tileID,
                         final int wantedPoints,
                         final int priority) {
      int from = 0;

      synchronized (_tasks) {
         final int taskID = calculateTaskID();

         while (from < wantedPoints) {
            final int to = Math.min(from + POINTS_GROUP_SIZE, wantedPoints) - 1;

            _tasks.add(new Task(pointsCloudName, tileID, from, to, priority, taskID, sessionID));

            from += POINTS_GROUP_SIZE;
         }

         return taskID;
      }
   }


   private int calculateTaskID() {
      return TASK_ID_COUNTER++;
   }


   //   private Task selectTask(final int taskID) {
   //      Task selectedTask = null;
   //
   //      synchronized (_tasks) {
   //         for (final Task task : _tasks) {
   //            if (task._taskID == taskID) {
   //               if ((selectedTask == null) || (selectedTask._priority > task._priority)) {
   //                  selectedTask = task;
   //               }
   //            }
   //         }
   //
   //         if (selectedTask != null) {
   //            _tasks.remove(selectedTask);
   //         }
   //      }
   //
   //      return selectedTask;
   //   }


   private Task selectTask() {
      Task selectedTask = null;

      final long threshold = System.currentTimeMillis() - 150;

      synchronized (_tasks) {
         for (final Task task : _tasks) {
            if ((selectedTask == null) || (selectedTask._priority > task._priority)) {
               if (lastSend(task._sessionID) < threshold) {
                  selectedTask = task;
               }
            }
         }

         if (selectedTask != null) {
            _tasks.remove(selectedTask);
         }
      }

      return selectedTask;
   }


   private long lastSend(final int sessionID) {
      synchronized (_lastSends) {
         final Long lastSend = _lastSends.get(sessionID);
         if (lastSend == null) {
            return Long.MIN_VALUE;
         }
         return lastSend;
      }
   }


   @Override
   public void cancel(final int taskID) {
      synchronized (_tasks) {
         final Iterator<Task> iterator = _tasks.iterator();
         while (iterator.hasNext()) {
            final Task task = iterator.next();
            if (task._taskID == taskID) {
               iterator.remove();
            }
         }
      }
   }


   //   @Override
   //   public GPointsData poll(final int taskID) {
   //      final Task selectedTask = selectTask(taskID);
   //
   //      if (selectedTask == null) {
   //         return null;
   //      }
   //
   //      return selectedTask.execute();
   //   }


   @Override
   public void setPriority(final int taskID,
                           final int priority) {
      synchronized (_tasks) {
         for (final Task task : _tasks) {
            if (task._taskID == taskID) {
               task._priority = priority;
            }
         }
      }
   }


   private void sessionClosed(final int sessionID) {
      synchronized (_lastSends) {
         _lastSends.remove(Integer.valueOf(sessionID));
      }
   }


   @SuppressWarnings("unused")
   public static void main(final String[] args) {
      System.out.println("GPointsStreamingServer 0.1");
      System.out.println("--------------------------\n");


      if ((args.length < 1) || (args.length > 2)) {
         System.err.println("Usage: " + GPointsStreamingServer.class + " pointsCloudDirectoryName [<port>]");
         return;
      }

      final String pointsCloudDirectoryName = args[0];
      final int port = (args.length >= 2) ? Integer.parseInt(args[1]) : 8000;

      final GPointsStreamingServer model = new GPointsStreamingServer(pointsCloudDirectoryName);

      new GDServer(port, model, true) {
         @Override
         public void channelClosed(final Channel channel,
                                   final int sessionID) {
            super.channelClosed(channel, sessionID);

            model.sessionClosed(sessionID);
         }
      };
   }


}
