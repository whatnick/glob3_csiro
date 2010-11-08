package es.igosoftware.loading;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

public class GResourceRetriever {


   public static URL getResourceAsUrl(final String filename) throws IOException {
      URL result;

      try {
         result = new URL(filename);
      }
      catch (final MalformedURLException e) {
         // When the string was not a valid URL, try to load it as a resource using
         // an anonymous class in the tree.
         final Object object = new Object() {};
         result = object.getClass().getClassLoader().getResource(filename);

         if (result == null) {
            result = new URL("file", "localhost", filename);
         }
      }

      return result;
   }


   public static InputStream getResourceAsInputStream(final String filename) throws IOException {
      URL result;

      try {
         result = getResourceAsUrl(filename);
      }
      catch (final IOException e) {
         return new FileInputStream(filename);
      }

      if (result == null) {
         return new FileInputStream(filename);
      }
      return result.openStream();
   }

}
