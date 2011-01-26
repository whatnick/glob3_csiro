

package es.igosoftware.globe.server.experimental.points;


import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBufferOutputStream;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelPipelineCoverage;
import org.jboss.netty.handler.codec.oneone.OneToOneEncoder;

import es.igosoftware.io.GIOUtils;


@ChannelPipelineCoverage("all")
public class GPointsProtocolEncoder
         extends
            OneToOneEncoder {

   private static final byte[] LENGTH_PLACEHOLDER = new byte[4];

   private final int           _estimatedLength;


   //   public GPointsProtocolEncoder() {
   //      this(512);
   //   }


   public GPointsProtocolEncoder(final int estimatedLength) {
      if (estimatedLength < 0) {
         throw new IllegalArgumentException("_estimatedLength: " + estimatedLength);
      }
      _estimatedLength = estimatedLength;
   }


   @Override
   protected Object encode(final ChannelHandlerContext ctx,
                           final Channel channel,
                           final Object msg) throws Exception {

      final String object = (String) msg;

      ChannelBufferOutputStream bout = null;
      try {
         bout = new ChannelBufferOutputStream(ChannelBuffers.dynamicBuffer(_estimatedLength,
                  ctx.getChannel().getConfig().getBufferFactory()));
         bout.write(LENGTH_PLACEHOLDER);

         final byte[] rawBytes = object.getBytes("UTF-8");
         final byte[] compressedBytes = GIOUtils.compress(rawBytes);

         //      final int TODO_Remove_Print;
         //      System.out.println("-----> sending message, raw " + rawBytes.length + ", compressed " + compressedBytes.length);

         final int compressedSign; // the message lenght is in negative to flag the contents is compressed 
         if (compressedBytes.length < rawBytes.length) {
            compressedSign = -1;
            bout.write(compressedBytes);
         }
         else {
            compressedSign = 1;
            bout.write(rawBytes);
         }

         final ChannelBuffer encoded = bout.buffer();
         encoded.setInt(0, (encoded.writerIndex() - 4) * compressedSign);
         return encoded;
      }
      finally {
         GIOUtils.gentlyClose(bout);
      }
   }


}
