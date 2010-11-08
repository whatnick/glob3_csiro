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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.StringTokenizer;

public final class Logger {
   private static final DateFormat format   = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS: ");
   private static final Logger     instance = new Logger();


   synchronized public static Logger instance() {
      return Logger.instance;
   }


   private int _identationLevel = 0;


   private Logger() {}


   private String level() {
      return StringUtils.spaces(_identationLevel * 2);
   }


   synchronized public void increaseIdentationLevel() {
      _identationLevel++;
   }


   synchronized public void decreaseIdentationLevel() {
      if (_identationLevel > 0) {
         _identationLevel--;
      }
   }


   private String timestamp() {
      synchronized (format) {
         return format.format(Calendar.getInstance().getTime());
      }
   }


   synchronized public void info(final String msg) {
      final String notUsedString = "XIXIXIXIXIXIXIXIXIXI";
      final StringTokenizer lines = new StringTokenizer(msg.replaceAll("\n", notUsedString + "\n"), "\n", false);

      final String prefix = timestamp() + level();
      //final String secondPrefix = Utils.spaces(prefix.length());
      while (lines.hasMoreTokens()) {
         final String line = lines.nextToken().replaceAll(notUsedString, "");
         System.out.println(prefix + line);

         //prefix = secondPrefix;
      }
   }


   synchronized public void severe(final String msg) {
      System.err.println(timestamp() + level() + "SEVERE: " + msg);
   }


   synchronized public void severe(final Throwable e) {
      System.err.println(timestamp() + level() + "SEVERE: " + e);
      e.printStackTrace(System.err);
   }


   public void severe(final String msg,
                      final Throwable e) {
      System.err.println(timestamp() + level() + "SEVERE: " + msg + " " + e);
      e.printStackTrace(System.err);
   }


   synchronized public void warning(final String msg) {
      System.err.println(timestamp() + level() + "WARNING: " + msg);
   }


}
