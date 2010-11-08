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

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;


public class GIOUtils {

   private GIOUtils() {
   }


   public static void copyFile(final File fromFile,
                               final File toFile) throws IOException {

      File destinationFile = toFile;

      if (!fromFile.exists()) {
         throw new IOException("FileCopy: " + "no such source file: " + fromFile.getAbsolutePath());
      }
      if (!fromFile.isFile()) {
         throw new IOException("FileCopy: " + "can't copy directory: " + fromFile.getAbsolutePath());
      }
      if (!fromFile.canRead()) {
         throw new IOException("FileCopy: " + "source file is unreadable: " + fromFile.getAbsolutePath());
      }

      if (destinationFile.isDirectory()) {
         destinationFile = new File(destinationFile, fromFile.getName());
      }

      if (destinationFile.exists()) {
         if (!destinationFile.canWrite()) {
            throw new IOException("FileCopy: " + "destination file is unwriteable: " + destinationFile.getAbsolutePath());
         }
      }
      else {
         String parent = destinationFile.getParent();
         if (parent == null) {
            parent = System.getProperty("user.dir");
         }
         final File dir = new File(parent);
         if (!dir.exists()) {
            throw new IOException("FileCopy: " + "destination directory doesn't exist: " + parent);
         }
         if (dir.isFile()) {
            throw new IOException("FileCopy: " + "destination is not a directory: " + parent);
         }
         if (!dir.canWrite()) {
            throw new IOException("FileCopy: " + "destination directory is unwriteable: " + parent);
         }
      }

      FileInputStream from = null;
      FileOutputStream to = null;
      try {
         from = new FileInputStream(fromFile);
         to = new FileOutputStream(destinationFile);
         final byte[] buffer = new byte[4096];
         int bytesRead;

         while ((bytesRead = from.read(buffer)) != -1) {
            to.write(buffer, 0, bytesRead); // write
         }
      }
      finally {
         if (from != null) {
            try {
               from.close();
            }
            catch (final IOException e) {
            }
         }
         if (to != null) {
            try {
               to.close();
            }
            catch (final IOException e) {
            }
         }
      }
   }


   public static void copyFile(final String fromFileName,
                               final String toFileName) throws IOException {
      final File fromFile = new File(fromFileName);
      File toFile = new File(toFileName);

      if (!fromFile.exists()) {
         throw new IOException("FileCopy: " + "no such source file: " + fromFileName);
      }
      if (!fromFile.isFile()) {
         throw new IOException("FileCopy: " + "can't copy directory: " + fromFileName);
      }
      if (!fromFile.canRead()) {
         throw new IOException("FileCopy: " + "source file is unreadable: " + fromFileName);
      }

      if (toFile.isDirectory()) {
         toFile = new File(toFile, fromFile.getName());
      }

      if (toFile.exists()) {
         if (!toFile.canWrite()) {
            throw new IOException("FileCopy: " + "destination file is unwriteable: " + toFileName);
         }
      }
      else {
         String parent = toFile.getParent();
         if (parent == null) {
            parent = System.getProperty("user.dir");
         }
         final File dir = new File(parent);
         if (!dir.exists()) {
            throw new IOException("FileCopy: " + "destination directory doesn't exist: " + parent);
         }
         if (dir.isFile()) {
            throw new IOException("FileCopy: " + "destination is not a directory: " + parent);
         }
         if (!dir.canWrite()) {
            throw new IOException("FileCopy: " + "destination directory is unwriteable: " + parent);
         }
      }

      FileInputStream from = null;
      FileOutputStream to = null;
      try {
         from = new FileInputStream(fromFile);
         to = new FileOutputStream(toFile);
         final byte[] buffer = new byte[4096];
         int bytesRead;

         while ((bytesRead = from.read(buffer)) != -1) {
            to.write(buffer, 0, bytesRead); // write
         }
      }
      finally {
         if (from != null) {
            try {
               from.close();
            }
            catch (final IOException e) {
            }
         }
         if (to != null) {
            try {
               to.close();
            }
            catch (final IOException e) {
            }
         }
      }
   }


   public static void copy(final InputStream in,
                           final OutputStream out) throws IOException {
      final byte[] buf = new byte[1024];
      int len;
      while ((len = in.read(buf)) > 0) {
         out.write(buf, 0, len);
      }
      in.close();
   }


   public static void gentlyClose(final Socket socket) {
      if (socket == null) {
         return;
      }

      try {
         socket.close();
      }
      catch (final IOException e) {
      }
   }


   public static void gentlyClose(final ServerSocket socket) {
      if (socket == null) {
         return;
      }

      try {
         socket.close();
      }
      catch (final IOException e) {
      }
   }


   public static void gentlyClose(final Closeable closeable) {
      if (closeable == null) {
         return;
      }

      try {
         closeable.close();
      }
      catch (final IOException e) {
      }

   }


   public static void assureEmptyDirectory(final String directoryName,
                                           final boolean verbose) throws IOException {
      final File directory = new File(directoryName);

      if (!directory.exists()) {
         System.out.println("- Creating directory \"" + directoryName + "\"");
         if (!directory.mkdirs()) {
            throw new IOException("Can't create directory \"" + directoryName + "\"");
         }
         return;
      }

      // the directory already exists, clean the contents
      cleanDirectory(directoryName, verbose);
   }


   public static void cleanDirectory(final File directory,
                                     final boolean verbose) throws IOException {
      if (!directory.exists()) {
         throw new IOException("Directory \"" + directory.getAbsolutePath() + "\" doesn't exist");
      }

      if (!directory.isDirectory()) {
         throw new IOException("The path \"" + directory.getAbsolutePath() + "\" is not a directory");
      }

      final File[] children = directory.listFiles();
      for (final File child : children) {
         if (child.isDirectory()) {
            cleanDirectory(child, verbose);
         }

         if (child.delete()) {
            System.out.println("- Deleted \"" + child.getPath() + "\"");
         }
         else {
            throw new IOException("Can't delete \"" + child.getAbsolutePath() + "\"");
         }
      }
   }


   public static void cleanDirectory(final String directoryName,
                                     final boolean verbose) throws IOException {
      final File directory = new File(directoryName);
      cleanDirectory(directory, verbose);
   }


   /*
     * Get the extension of a file.
     */
   public static String getExtension(final File file) {
      String ext = null;
      final String s = file.getName();
      final int i = s.lastIndexOf('.');

      if ((i > 0) && (i < s.length() - 1)) {
         ext = s.substring(i + 1).toLowerCase();
      }

      return ext;
   }


}
