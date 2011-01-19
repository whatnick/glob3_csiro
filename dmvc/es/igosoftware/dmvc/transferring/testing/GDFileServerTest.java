

package es.igosoftware.dmvc.transferring.testing;

import es.igosoftware.dmvc.server.GDServer;
import es.igosoftware.dmvc.transferring.GDFileServer;


public class GDFileServerTest {


   @SuppressWarnings("unused")
   public static void main(final String[] args) {
      System.out.println("GDFileServerTest 0.1");
      System.out.println("--------------------\n");

      if (args.length > 1) {
         System.err.println("Usage: " + GDFileServerTest.class.getSimpleName() + " [<port>]");
         return;
      }

      final int port = (args.length >= 1) ? Integer.parseInt(args[0]) : 8080;

      final GDFileServer rootObject = new GDFileServer("Testing_ServerRootDirectory");
      new GDServer(port, rootObject, true);
   }

}
