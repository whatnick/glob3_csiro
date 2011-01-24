

package es.igosoftware.io.pointscloud;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Arrays;

import com.google.gson.Gson;

import es.igosoftware.io.GIOUtils;
import es.igosoftware.io.ILoader;


public class GPointsCloudInternetLoader
         implements
            IPointsCloudLoader {

   private final String _host;
   private final int    _port;


   public GPointsCloudInternetLoader(final String serverName,
                                     final int port) {
      _host = serverName;
      _port = port;
   }


   @Override
   public String[] getPointsCloudsNames() throws IOException {
      Socket socket = null;
      try {
         socket = new Socket(_host, _port);


         final OutputStreamWriter os = new OutputStreamWriter(socket.getOutputStream());

         os.write(GPointsClouseServerCommands.DIR_COMMAND);
         os.write("\n");
         os.flush();


         final BufferedReader is = new BufferedReader(new InputStreamReader(socket.getInputStream()));
         final String answer = is.readLine();

         final Gson gson = new Gson();
         return gson.fromJson(answer, String[].class);
      }
      finally {
         GIOUtils.gentlyClose(socket);
      }
   }


   @Override
   public void load(final String fileName,
                    final int bytesToLoad,
                    final int priority,
                    final ILoader.IHandler handler) {
      //      final int TOTO;

   }


   @Override
   public void cancelLoading(final String fileName) {
      //      final int TOTO;

   }


   public static void main(final String[] args) throws IOException {
      System.out.println("GPointsCloudHttpLoader 0.1");
      System.out.println("--------------------------\n");


      //      final URL url = new URL("http://localhost/");
      //
      //      final HttpURLConnection connection = (HttpURLConnection) url.openConnection();
      //
      //
      //      //      connnection.setRequestProperty("NAME", "VALUE");
      //      connection.setDoInput(true);
      //      connection.setDoOutput(true);
      //      connection.setAllowUserInteraction(false);
      //
      //      //      connection.setRequestProperty("connection", "Keep-Alive");
      //      connection.setRequestProperty("If-None-Match", "\"542ca7-b1-47ec3e7e3bd78\"");
      //      connection.setUseCaches(true);
      //      //      connection.setRequestProperty("If-Modified-Since", "Thu, 04 Feb 2010 10:36:21 GMT");
      //      //      connection.setRequestProperty("Range", "bytes=0-99");
      //      connection.setRequestMethod("GET");
      //
      //
      //      if (acceptRanges(connection)) {
      //         System.out.println("\n*** Accept Range ***\n");
      //      }
      //
      //      System.out.println("Status Code: " + connection.getResponseCode() + " - " + connection.getResponseMessage());
      //
      //      final BufferedReader is = new BufferedReader(new InputStreamReader(connection.getInputStream()));
      //
      //      String line = null;
      //      //      int lineCounter = 0;
      //      while ((line = is.readLine()) != null) {
      //         //         lineCounter++;
      //         //         System.out.println("#" + lineCounter + ": " + line);
      //         System.out.println(line);
      //      }
      //
      //
      //      connection.disconnect();

      //      final Gson gson = new Gson();
      //
      //      System.out.println(gson.toJson(new int[] { 1, 2, 3, 4 }));


      final GPointsCloudInternetLoader loader = new GPointsCloudInternetLoader("localhost", 8000);

      final String[] pointsCloudsNames = loader.getPointsCloudsNames();
      System.out.println(Arrays.toString(pointsCloudsNames));
   }


   //   private static boolean acceptRanges(final HttpURLConnection connection) {
   //      final Map<String, List<String>> fields = connection.getHeaderFields();
   //
   //      System.out.println(fields);
   //
   //      final List<String> acceptRanges = fields.get("Accept-Ranges");
   //      if ((acceptRanges == null) || acceptRanges.isEmpty()) {
   //         return false;
   //      }
   //
   //      if (!acceptRanges.contains("bytes")) {
   //         return false;
   //      }
   //
   //
   //      final List<String> contentLenght = fields.get("Content-Length");
   //      if ((contentLenght == null) || contentLenght.isEmpty()) {
   //         return false;
   //      }
   //
   //      System.out.println(" Content-Length: " + contentLenght.get(0));
   //
   //
   //      final List<String> eTag = fields.get("ETag");
   //      if ((eTag == null) || eTag.isEmpty()) {
   //         return false;
   //      }
   //
   //      System.out.println(" ETag: " + eTag.get(0));
   //
   //
   //      return true;
   //   }


}
