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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.util.List;

import es.igosoftware.dmvc.client.GDClient;
import es.igosoftware.euclid.pointscloud.octree.GPCInnerNode;
import es.igosoftware.euclid.pointscloud.octree.GPCLeafNode;
import es.igosoftware.euclid.pointscloud.octree.GPCNode;
import es.igosoftware.euclid.pointscloud.octree.GPCPointsCloud;
import es.igosoftware.util.GUtils;


public class GPointsStreamingClient {


   public static void main(final String[] args) throws IOException {
      System.out.println("GPointsStreamingClient 0.1");
      System.out.println("---------------------\n");


      // Print usage if no argument is specified.
      if (args.length > 2) {
         System.err.println("Usage: " + GDClient.class.getSimpleName() + " [<host> [<port>]]");
         return;
      }

      // Parse options. 
      final String host = (args.length >= 1) ? args[0] : "127.0.0.1";
      final int port = (args.length >= 2) ? Integer.parseInt(args[1]) : 8000;

      final GDClient client = new GDClient(host, port, true);

      final int sessionID = client.getSessionID();
      //      System.out.println("Session ID=" + sessionID);

      final IPointsStreamingServer server = (IPointsStreamingServer) client.getRootObject();
      System.out.println("Root Model=" + server);


      server.addPropertyChangeListener("points_" + sessionID, new PropertyChangeListener() {
         @Override
         public void propertyChange(final PropertyChangeEvent evt) {
            final GPointsData result = (GPointsData) evt.getNewValue();
            System.out.println("Received " + result);
         }
      });


      final List<String> pointsCloudsNames = server.getPointsCloudsNames();
      System.out.println("PointsCloudsNames=" + pointsCloudsNames);

      final String pointsCloudName = pointsCloudsNames.get(0);
      final GPCPointsCloud pointsCloud = server.getPointsCloud(pointsCloudName);
      System.out.println("PointCloud=" + pointsCloud);


      final GPCLeafNode leaf = findAnyLeaf(pointsCloud.getRoot());
      System.out.println("leaf=" + leaf);

      final int wantedPoints = 22100 / 2;
      final int priority = 100;

      final int taskID = server.loadPoints(sessionID, pointsCloudName, leaf.getId(), wantedPoints, priority);

      //      int loadedPoints = 0;
      //      while (loadedPoints < wantedPoints) {
      //         final GPointsData points = server.poll(taskID);
      //         //         System.out.println("Received " + points.pointsCount() + " points");
      //         loadedPoints += points.pointsCount();
      //      }
      //
      //
      final int cancelableTaskID = server.loadPoints(sessionID, pointsCloudName, leaf.getId(), wantedPoints, priority + 1);

      GUtils.delay(300);
      server.cancel(cancelableTaskID);
      //      if (server.poll(cancelableTaskID) != null) {
      //         throw new RuntimeException("The poll must return null");
      //      }


   }


   private static GPCLeafNode findAnyLeaf(final GPCInnerNode inner) {
      final GPCNode[] children = inner.getChildren();
      for (final GPCNode child : children) {
         if (child instanceof GPCLeafNode) {
            return (GPCLeafNode) child;
         }
         else if (child instanceof GPCInnerNode) {
            return findAnyLeaf((GPCInnerNode) child);
         }
      }
      return null;
   }

}
