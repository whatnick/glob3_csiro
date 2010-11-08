package es.igosoftware.euclid.io;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

import es.igosoftware.util.GAssert;


public final class GBufferedFileInputStream
         extends
            InputStream {

   private static final int      DEFAULT_BUFFER_SIZE = 1024 * 32 /* 32Kb */;


   private final FileInputStream _fis;
   private final FileChannel     _fileChannel;
   private final ByteBuffer      _buffer;

   private int                   _count;
   private int                   _position;


   public GBufferedFileInputStream(final FileInputStream fis) {
      this(fis, GBufferedFileInputStream.DEFAULT_BUFFER_SIZE);
   }


   public GBufferedFileInputStream(final FileInputStream fis,
                                   final int bufferSize) {
      GAssert.notNull(fis, "fis");

      _fis = fis;
      _fileChannel = _fis.getChannel();
      _buffer = ByteBuffer.allocate(bufferSize);
   }


   public GBufferedFileInputStream(final String fileName,
                                   final int bufferSize) throws IOException {
      this(new FileInputStream(fileName), bufferSize);
   }


   public GBufferedFileInputStream(final String fileName) throws IOException {
      this(new FileInputStream(fileName));
   }


   private void fill() throws IOException {
      _count = _fileChannel.read(_buffer);
      _buffer.rewind();
      _position = 0;
   }


   @Override
   public synchronized int read() throws IOException {
      if (_position >= _count) {
         fill();
         if (_position >= _count) {
            return -1;
         }
      }

      return _buffer.get(_position++) & 0xff;
   }


   @Override
   public synchronized int available() throws IOException {
      return _fis.available() + (_count - _position);
   }


   @Override
   public void close() throws IOException {
      _fis.close();
      _fileChannel.close();
   }


}
