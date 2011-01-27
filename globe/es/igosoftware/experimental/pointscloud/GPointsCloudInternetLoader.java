

package es.igosoftware.experimental.pointscloud;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Arrays;

import com.google.gson.Gson;

import es.igosoftware.io.GIOUtils;
import es.igosoftware.io.ILoader;
import es.igosoftware.io.pointscloud.IPointsCloudLoader;


public class GPointsCloudInternetLoader
         implements
            IPointsCloudLoader {

   private final String _host;
   private final int    _port;
   private Session      _session;


   public GPointsCloudInternetLoader(final String serverName,
                                     final int port) {
      _host = serverName;
      _port = port;
   }


   private static class Session {
      private final Socket           _socket;
      private final DataOutputStream _os;
      private final DataInputStream  _is;


      private Session(final String host,
                      final int port) throws IOException {
         _socket = new Socket(host, port);

         _os = new DataOutputStream(_socket.getOutputStream());
         _is = new DataInputStream(_socket.getInputStream());
      }


      private void sendCommand(final String command) throws IOException {
         final byte[] bytes = command.getBytes("UTF-8");

         final byte[] compressed = GIOUtils.compress(bytes);

         if (compressed.length < bytes.length) {
            _os.writeInt(compressed.length * -1);
            _os.write(compressed);
         }
         else {
            _os.writeInt(bytes.length);
            _os.write(bytes);
         }
         _os.flush();
      }


      private String readLine() throws IOException {

         final int sizeAndCompressedFlag = _is.readInt();

         final int size;
         final boolean compressed;
         if (sizeAndCompressedFlag < 0) {
            size = sizeAndCompressedFlag * -1;
            compressed = true;
         }
         else {
            size = sizeAndCompressedFlag;
            compressed = false;
         }

         final byte[] rawBytes = new byte[size];
         _is.read(rawBytes);

         if (!compressed) {
            return new String(rawBytes, "UTF-8");
         }

         final byte[] bytes = GIOUtils.uncompress(rawBytes);
         return new String(bytes, "UTF-8");
      }


      private void close() {
         try {
            _socket.close();
         }
         catch (final IOException e) {
            // no nothing
         }
      }
   }


   private synchronized Session getSession() throws IOException {
      if (_session == null) {
         _session = new Session(_host, _port);
      }
      return _session;
   }


   private synchronized void takeSession(final Session session) {
      if (session != _session) {
         throw new RuntimeException("Invalid session");
      }
      _session.close();
      _session = null;
   }


   @Override
   public String[] getPointsCloudsNames() throws IOException {
      final Session session = getSession();
      try {
         session.sendCommand(GPointsClouseServerCommands.DIR_COMMAND);

         final String answer = session.readLine();
         final Gson gson = new Gson();
         return gson.fromJson(answer, String[].class);
      }
      finally {
         takeSession(session);
      }
   }


   @Override
   public void load(final String fileName,
                    final int bytesToLoad,
                    final int priority,
                    final ILoader.IHandler handler) {
      final int __Diego_at_work;

   }


   @Override
   public void cancelLoading(final String fileName) {
      final int __Diego_at_work;

   }


   public static void main(final String[] args) throws IOException {
      System.out.println("GPointsCloudHttpLoader 0.1");
      System.out.println("--------------------------\n");


      final GPointsCloudInternetLoader loader = new GPointsCloudInternetLoader("localhost", 8000);

      final String[] pointsCloudsNames = loader.getPointsCloudsNames();
      System.out.println(Arrays.toString(pointsCloudsNames));
   }


}
