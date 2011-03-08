

package es.igosoftware.io;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import es.igosoftware.util.GAssert;
import es.igosoftware.util.GCollections;


public class GFileName {
   private final String[] _parts;


   public GFileName(final String... parts) {
      GAssert.notEmpty(parts, "parts");
      //stillHasSlashes(parts);

      //      _parts = Arrays.copyOf(parts, parts.length);
      final String[] correctedArray = stillHasSlashes(parts);
      _parts = Arrays.copyOf(correctedArray, correctedArray.length);


   }


   public GFileName(final GFileName parent,
                    final String... parts) {
      GAssert.notNull(parent, "parent");
      GAssert.notEmpty(parts, "parts");

      //stillHasSlashes(parts);

      final String[] correctedArray = stillHasSlashes(parts);
      //      _parts = GCollections.concatenate(parent._parts, parts);
      _parts = GCollections.concatenate(parent._parts, correctedArray);

   }


   private static String[] stillHasSlashes(final String... parts) {
      final List<String> partsList = new ArrayList<String>();
      for (final String part : parts) {
         if (part.contains("/") || part.contains("\\")) {
            final String[] dividedPart = dissectComplexPart(part);
            for (final String element : dividedPart) {
               partsList.add(element);
            }

         }
         else {
            partsList.add(part);
         }
      }
      final String[] stringArray = new String[partsList.size()];
      return partsList.toArray(stringArray);
   }


   //in case a part still has a "/" or "\"
   private static String[] dissectComplexPart(final String string) {
      final String[] result = string.split("[/\\\\]");
      return result;
   }


   public String buildPath() {
      return GIOUtils.buildPath(_parts);
   }


   public String buildPath(final char separator) {
      return GIOUtils.buildPath(separator, _parts);
   }


   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + Arrays.hashCode(_parts);
      return result;
   }


   @Override
   public boolean equals(final Object obj) {
      if (this == obj) {
         return true;
      }
      if (obj == null) {
         return false;
      }
      if (getClass() != obj.getClass()) {
         return false;
      }
      final GFileName other = (GFileName) obj;
      if (!Arrays.equals(_parts, other._parts)) {
         return false;
      }
      return true;
   }


   @Override
   public String toString() {
      return "GFileName [" + buildPath() + "]";
   }


   //   public static void main(final String[] args) {
   //      System.out.println("GFileName 0.1");
   //      System.out.println("-------------\n");
   //
   //      final GFileName fileName1 = new GFileName("dir1", "dir2", "dir3");
   //      System.out.println(fileName1);
   //
   //      final GFileName fileName2 = new GFileName(fileName1, "fileName.ext");
   //      System.out.println(fileName2);
   //
   //      final String parts[] = "dir1/dir2\\filename.ext".split("[/\\\\]");
   //      System.out.println(Arrays.toString(parts));
   //
   //
   //   }

}
