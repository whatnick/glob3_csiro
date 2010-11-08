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

import java.util.ArrayList;
import java.util.StringTokenizer;

public final class XStringTokenizer
         extends
            StringTokenizer {

   public static double[] nextDoubleTokens(final String str,
                                           final int count) {
      final XStringTokenizer instance = new XStringTokenizer(str);
      final double[] result = instance.nextDoubleTokens(count);
      if (instance.hasMoreTokens()) {
         throw new NumberFormatException("The given string has more than " + count + " tokens");
      }
      return result;
   }


   public static String[] getAllTokens(final String str) {
      return new XStringTokenizer(str).getAllTokens();
   }


   public static String[] getAllTokens(final String str,
                                       final String delim) {
      return new XStringTokenizer(str, delim).getAllTokens();
   }


   public static String[] getAllTokens(final String str,
                                       final String delim,
                                       final boolean returnDelims) {
      return new XStringTokenizer(str, delim, returnDelims).getAllTokens();
   }


   public String[] getAllTokens() {
      final ArrayList<String> result = new ArrayList<String>();
      while (hasMoreTokens()) {
         result.add(nextToken());
      }
      return result.toArray(new String[0]);
   }


   public XStringTokenizer(final String str) {
      super(str);
   }


   public XStringTokenizer(final String str,
                           final String delim) {
      super(str, delim);
   }


   public XStringTokenizer(final String str,
                           final String delim,
                           final boolean returnDelims) {
      super(str, delim, returnDelims);
   }


   public double nextDoubleToken() {
      return Double.parseDouble(nextToken());
   }


   public double[] nextDoubleTokens(final int count) {
      final double[] result = new double[count];
      for (int i = 0; i < count; i++) {
         result[i] = nextDoubleToken();
      }
      return result;
   }


   public float nextFloatToken() {
      return Float.parseFloat(nextToken());
   }


   public int nextIntToken() {
      return Integer.parseInt(nextToken());
   }

}
