

package es.igosoftware.globe.server.experimental.points;

import java.io.StreamCorruptedException;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.frame.FrameDecoder;

import es.igosoftware.io.GIOUtils;


public class GPointsProtocolDecoder
         extends
            FrameDecoder {

   private final int _maxObjectSize;


   public GPointsProtocolDecoder(final int maxObjectSize) {
      if (maxObjectSize <= 0) {
         throw new IllegalArgumentException("_maxObjectSize: " + maxObjectSize);
      }

      _maxObjectSize = maxObjectSize;
   }


   @Override
   protected Object decode(final ChannelHandlerContext ctx,
                           final Channel channel,
                           final ChannelBuffer buffer) throws Exception {
      if (buffer.readableBytes() < 4) {
         return null;
      }

      final int readerIndex = buffer.readerIndex();
      final int dataLenAndCompressedFlag = buffer.getInt(readerIndex);

      final boolean compressedFlag;
      final int dataLen;
      if (dataLenAndCompressedFlag < 0) {
         compressedFlag = true;
         dataLen = dataLenAndCompressedFlag * -1;
      }
      else {
         compressedFlag = false;
         dataLen = dataLenAndCompressedFlag;
      }

      if (dataLen > _maxObjectSize) {
         throw new StreamCorruptedException("data length too big: " + dataLen + " (max: " + _maxObjectSize + ')');
      }

      if (buffer.readableBytes() < dataLen + 4) {
         return null;
      }

      final byte[] rawBytes = new byte[dataLen];
      buffer.getBytes(4 + readerIndex, rawBytes);
      buffer.skipBytes(4 + dataLen);

      if (compressedFlag) {
         final byte[] bytes = GIOUtils.uncompress(rawBytes);
         return new String(bytes, "UTF-8");
      }

      return new String(rawBytes, "UTF-8");
   }

}
