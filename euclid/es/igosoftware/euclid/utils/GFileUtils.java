package es.igosoftware.euclid.utils;

public final class GFileUtils {

   private GFileUtils() {}


   public static boolean hasExtension(final String fileName,
                                      final String extension) {
      return fileName.toLowerCase().endsWith("." + extension.toLowerCase());
   }

}
