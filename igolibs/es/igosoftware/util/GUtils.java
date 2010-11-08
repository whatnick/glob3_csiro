/*
 * Cáceres 3D
 * 
 * Copyright (c) 2008 Junta de Extremadura.
 * 
 * Permission is hereby granted, free of charge, to any
 * person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the
 * Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the
 * Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice
 * shall be included in all copies or substantial portions of
 * the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY
 * KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS
 * OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR
 * OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR
 * OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 * SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 * 
 * 
 * Funded by European Union. FEDER program.
 * Developed by: IGO SOFTWARE, S.L.
 * 
 * For more information, contact: 
 * 
 *    Junta de Extremadura
 *    Consejería de Cultura y Turismo
 *    C/ Almendralejo 14 Mérida
 *    06800 Badajoz
 *    SPAIN
 * 
 *    Tel: +34 924007009
 *    http://www.culturaextremadura.com
 * 
 *   or
 * 
 *    IGO SOFTWARE, S.L.
 *    Calle Santiago Caldera Nro 4
 *    Cáceres
 *    Spain
 *    Tel: +34 927 629 436
 *    e-mail: support@igosoftware.es
 *    http://www.igosoftware.es
 */

package es.igosoftware.util;

import static java.util.Locale.ENGLISH;

import java.awt.Image;
import java.awt.Toolkit;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

public final class GUtils {


   public static final double   SMALL_NUM = 0.000001;

   private final static boolean isDevelopment;


   private GUtils() {}

   private static final boolean isLinux;
   private static final boolean isWindows;
   private static final boolean isMac;

   static {
      isDevelopment = System.getProperty("development", "off").equalsIgnoreCase("on");

      final String osName = System.getProperty("os.name", "").toLowerCase();

      isLinux = osName.contains("linux");
      isWindows = osName.contains("windows");
      isMac = osName.contains("mac") || osName.contains("darwin");

      //      if (isDevelopment()) {
      //         System.out.println("Development mode.");
      //         if (isLinux) {
      //            System.out.println("** Detected: Linux **");
      //         }
      //         if (isWindows) {
      //            System.out.println("** Detected: Windows **");
      //         }
      //         if (isMac) {
      //            System.out.println("** Detected: Mac **");
      //         }
      //      }
   }


   public static boolean isDevelopment() {
      return GUtils.isDevelopment;
   }


   public static boolean isLinux() {
      return GUtils.isLinux;
   }


   public static boolean isWindows() {
      return GUtils.isWindows;
   }


   public static boolean isMac() {
      return GUtils.isMac;
   }


   public static boolean is64Bits() {
      return "64".equals(System.getProperty("sun.arch.data.model"));
   }


   public static void delay(final long millis) {
      delay(millis, 0);
   }


   public static void delay(final long millis,
                            final int nanos) {
      if (millis < 0) {
         return;
      }

      try {
         Thread.sleep(millis, nanos);
      }
      catch (final InterruptedException e) {}
   }


   public static Image getImage(final String imageName,
                                final ClassLoader classLoader) {
      final URL url = classLoader.getResource("bitmaps/" + imageName);
      if (url == null) {
         return null;
      }

      try {
         return ImageIO.read(url);
      }
      catch (final IOException e) {
         e.printStackTrace();
      }

      return null;
   }


   public static Image getImage(final String imageName) {
      return Toolkit.getDefaultToolkit().getImage("bitmaps/" + imageName);
   }


   public static ImageIcon getImageIcon(final String iconName) {
      return new ImageIcon(getImage(iconName));
   }


   public static void showMemoryInfo() {
      final Runtime runtime = Runtime.getRuntime();

      final long maxMemory = runtime.maxMemory();
      final long allocatedMemory = runtime.totalMemory();
      final long freeMemory = runtime.freeMemory();

      System.out.println("------------------------------------------------------");
      System.out.println("free memory: " + freeMemory / 1024);
      System.out.println("allocated memory: " + allocatedMemory / 1024);
      System.out.println("max memory: " + maxMemory / 1024);
      System.out.println("total free memory: " + (freeMemory + (maxMemory - allocatedMemory)) / 1024);
      System.out.println("------------------------------------------------------");
   }


   public static boolean equals(final Object obj1,
                                final Object obj2) {
      if (obj1 == null) {
         return obj2 == null;
      }

      if (obj2 == null) {
         return false;
      }

      return obj1.equals(obj2);
   }


   public static void renameOldFile(final String fileName) {
      final File file = new File(fileName);
      if (file.exists()) {
         final File oldFile = new File(fileName + ".old");
         if (oldFile.exists()) {
            oldFile.delete();
         }

         file.renameTo(oldFile);
      }
   }


   public static <T extends Exception> void checkExceptions(final List<T> exceptions) throws T {
      if (exceptions.isEmpty()) {
         return;
      }

      for (final T exception : exceptions) {
         exception.printStackTrace(System.err);
      }

      throw exceptions.get(0);
   }


   public static String getTimeMessage(final long ms) {
      if (ms < 1000) {
         return ms + "ms";
      }

      if (ms < 60000) {
         final double seconds = ms / 1000d;
         return Math.round(seconds) + "s";
      }

      final long minutes = ms / 60000;
      final long seconds = (ms - (minutes * 60000)) / 1000;
      if (seconds <= 0) {
         return minutes + "m";
      }
      return minutes + "m " + seconds + "s";
   }


   public static String toString(final Object object) {
      if (object == null) {
         return "null";
      }

      if (object instanceof CharSequence) {
         return "\"" + object + "\"";
      }

      if (object instanceof Object[]) {
         return Arrays.toString((Object[]) object);
      }

      return String.valueOf(object);
   }


   /**
    * Returns a String which capitalizes the first letter of the string.
    */
   public static String capitalize(final String name) {
      if ((name == null) || (name.length() == 0)) {
         return name;
      }
      return name.substring(0, 1).toUpperCase(ENGLISH) + name.substring(1);
   }
}
