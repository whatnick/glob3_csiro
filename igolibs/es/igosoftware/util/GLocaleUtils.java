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

import java.util.Locale;
import java.util.ResourceBundle;

public final class GLocaleUtils {
   private static Locale currentLocale;


   static {
      setCurrentLocale(Locale.getDefault());
   }


   private GLocaleUtils() {}


   public static void setCurrentLocale(final Locale newLocale) {
      currentLocale = newLocale;
      if (GUtils.isDevelopment()) {
         System.out.println("Current Language: " + getCurrentLanguage());
      }
      Locale.setDefault(newLocale);
   }


   public static Locale getCurrentLocale() {
      return currentLocale;
   }


   public static String getCurrentLanguage() {
      return getCurrentLocale().getLanguage();
   }


   public static ResourceBundle getResourceBundle() {
      return ResourceBundle.getBundle("locale.ResourceBundle", getCurrentLocale());
   }


   public static String getString(final String key) {
      return getResourceBundle().getString(key);
   }


   public static String getString(final String key,
                                  final String language) {
      return ResourceBundle.getBundle("locale.ResourceBundle", new Locale(language)).getString(key);
   }

}
