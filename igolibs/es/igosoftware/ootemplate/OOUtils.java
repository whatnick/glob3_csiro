package es.igosoftware.ootemplate;

import java.util.HashMap;
import java.util.Map;

public class OOUtils {
   private OOUtils() {}

   private static final Map<String, String> ENTITIES = new HashMap<String, String>();


   static {
      OOUtils.ENTITIES.put(">", "&gt;");
      OOUtils.ENTITIES.put("<", "&lt;");
      OOUtils.ENTITIES.put("&", "&amp;");
      OOUtils.ENTITIES.put("\"", "&quot;");
      OOUtils.ENTITIES.put("'", "&apos;");
      OOUtils.ENTITIES.put("\\", "&#092;");
      OOUtils.ENTITIES.put("\u00a9", "&copy;");
      OOUtils.ENTITIES.put("\u00ae", "&reg;");
   }


   public static final String escape(final String s) {
      final StringBuffer buffer = new StringBuffer(s.length() * 2);

      for (int i = 0; i < s.length(); i++) {
         final char ch = s.charAt(i);
         if (((ch >= 63) && (ch <= 90)) || ((ch >= 97) && (ch <= 122)) || (ch == ' ')) {
            buffer.append(ch);
         }
         else {
            final String encoded = OOUtils.ENTITIES.get(String.valueOf(ch));
            if (encoded == null) {
               buffer.append(ch);
            }
            else {
               buffer.append(encoded);
            }
         }
      }

      return buffer.toString();
   }
}
