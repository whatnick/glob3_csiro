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

public final class StringUtils {
   private static final String SPACES;
   private static final String DASHES;
   private static final String SHARPS;

   private static final String NULL_STRING = "<null>";

   static {
      String sp = "                                                                               ";
      sp = sp + sp;
      sp = sp + sp;
      sp = sp + sp;
      SPACES = sp;

      String d = "-------------------------------------------------------------------------------";
      d = d + d;
      d = d + d;
      d = d + d;
      DASHES = d;

      String s = "###############################################################################";
      s = s + s;
      s = s + s;
      s = s + s;
      SHARPS = s;
   }


   private StringUtils() {}


   public static String toString(final Object obj) {
      if (obj == null) {
         return StringUtils.NULL_STRING;
      }
      return obj.toString();
   }


   public static String toString(final Object[] collection) {
      if (collection == null) {
         return StringUtils.NULL_STRING;
      }

      final StringBuilder buffer = new StringBuilder();
      boolean first = true;
      for (final Object o : collection) {
         if (!first) {
            first = false;
            buffer.append(",");
         }
         buffer.append(o);
      }

      return buffer.toString();
   }


   public static String spaces(final int count) {
      return StringUtils.SPACES.substring(0, count);
   }


   public static String dashes(final int count) {
      return StringUtils.DASHES.substring(0, count);
   }


   public static String sharps(final int count) {
      return StringUtils.SHARPS.substring(0, count);
   }

}
