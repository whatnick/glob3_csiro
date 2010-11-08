package es.igosoftware.util;

import java.io.IOException;

public class GInternetBrowser {

   private static final Runtime  RUNTIME        = Runtime.getRuntime();

   //   private static final String[] LINUX_BROWSERS = { "epiphany", "firefox", "mozilla", "konqueror", "netscape", "opera", "links", "lynx"                             };
   private static final String[] LINUX_BROWSERS = { "firefox", "mozilla", "epiphany", "konqueror", "netscape", "opera", "links",
            "lynx"                             };


   private GInternetBrowser() {}


   public static boolean browse(final String url) {
      try {
         if (GUtils.isWindows()) {
            // this doesn't support showing urls in the form of "page.html#nameLink" 
            RUNTIME.exec("rundll32 url.dll,FileProtocolHandler " + url);
         }
         else if (GUtils.isMac()) {
            RUNTIME.exec("open " + url);
         }
         else if (GUtils.isLinux()) {
            // Build a command string which looks like "browser1 "url" || browser2 "url" ||..."
            final StringBuffer cmd = new StringBuffer();
            for (int i = 0; i < LINUX_BROWSERS.length; i++) {
               cmd.append((i == 0 ? "" : " || ") + LINUX_BROWSERS[i] + " \"" + url + "\" ");
            }

            RUNTIME.exec(new String[] { "sh", "-c", cmd.toString() });
         }
         else {
            Logger.instance().severe("Unsupported platform (" + System.getProperty("os.name", "") + ")");
            return false;
         }
      }
      catch (final IOException e) {
         Logger.instance().severe(e);
         return false;
      }

      return true;
   }
}
