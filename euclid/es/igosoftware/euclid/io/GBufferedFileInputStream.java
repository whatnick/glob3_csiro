/*

 IGO Software SL  -  info@igosoftware.es

 http://www.glob3.org

-------------------------------------------------------------------------------
 Copyright (c) 2010, IGO Software SL
 All rights reserved.

 Redistribution and use in source and binary forms, with or without
 modification, are permitted provided that the following conditions are met:
     * Redistributions of source code must retain the above copyright
       notice, this list of conditions and the following disclaimer.
     * Redistributions in binary form must reproduce the above copyright
       notice, this list of conditions and the following disclaimer in the
       documentation and/or other materials provided with the distribution.
     * Neither the name of the IGO Software SL nor the
       names of its contributors may be used to endorse or promote products
       derived from this software without specific prior written permission.

 THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 DISCLAIMED. IN NO EVENT SHALL IGO Software SL BE LIABLE FOR ANY
 DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
-------------------------------------------------------------------------------

*/

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
