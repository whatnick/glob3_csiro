/**
 * 
 */
package es.igosoftware.io;

import java.io.Writer;

public class StringBufferWriter
         extends
            Writer {
   final private StringBuffer _buffer;


   public StringBufferWriter(final StringBuffer buffer) {
      _buffer = buffer;
   }


   @Override
   public void write(final char cbuf[],
                     final int offset,
                     final int len) {
      _buffer.append(cbuf, offset, len);
   }


   @Override
   public void flush() {}


   @Override
   public void close() {}


   @Override
   public void write(final int i) {
      _buffer.append((char) i);
   }


   @Override
   public void write(final String s) {
      _buffer.append(s);
   }
}
