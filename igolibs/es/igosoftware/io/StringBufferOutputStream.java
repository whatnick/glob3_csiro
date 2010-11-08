/**
 * 
 */
package es.igosoftware.io;

import java.io.OutputStream;

public class StringBufferOutputStream
         extends
            OutputStream {
   final private StringBuffer _buffer;


   public StringBufferOutputStream(final StringBuffer buffer) {
      _buffer = buffer;
   }


   @Override
   public void write(final int i) {
      _buffer.append((char) i);
   }


}
