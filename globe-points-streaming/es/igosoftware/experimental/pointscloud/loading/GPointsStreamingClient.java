

package es.igosoftware.experimental.pointscloud.loading;

import java.io.IOException;
import java.util.List;

import es.igosoftware.dmvc.client.GDClient;


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
      System.out.println("Session ID=" + sessionID);

      final IPointsStreamingServer server = (IPointsStreamingServer) client.getRootObject();
      System.out.println("Root Model=" + server);

      final List<String> pointsCloudsNames = server.getPointsCloudsNames();
      System.out.println("PointsCloudsNames=" + pointsCloudsNames);

      System.out.println("PointCloud=" + server.getPointsCloud(pointsCloudsNames.get(0)));
   }

}
