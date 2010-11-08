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


package es.igosoftware.utils;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import com.sun.opengl.util.ImageUtil;
import com.sun.opengl.util.texture.Texture;
import com.sun.opengl.util.texture.TextureData;
import com.sun.opengl.util.texture.TextureIO;

import es.igosoftware.util.GPair;
import es.igosoftware.util.Logger;
import gov.nasa.worldwind.formats.dds.DDSCompressor;
import gov.nasa.worldwind.formats.dds.DXTCompressionAttributes;
import gov.nasa.worldwind.util.WWIO;
import gov.nasa.worldwind.util.WWMath;


public class GTexturesCache {
   private static final Logger                               logger         = Logger.instance();

   private static final Map<GPair<String, Boolean>, Texture> _texturesCache = new HashMap<GPair<String, Boolean>, Texture>();


   private GTexturesCache() {

   }


   public static Texture getTexture(final URL url,
                                    final boolean mipmap) {
      if (url == null) {
         return null;
      }

      final GPair<String, Boolean> textureKey = new GPair<String, Boolean>(url.toString(), mipmap);
      synchronized (_texturesCache) {
         //         if (_texturesCache.containsKey(textureKey)) {
         //            return _texturesCache.get(textureKey);
         //         }
         //
         //         final Texture texture = loadTexture(url, mipmap);
         //         _texturesCache.put(textureKey, texture);
         //         return texture;

         final Texture texture = _texturesCache.get(textureKey);
         if (texture != null) {
            return texture;
         }

         final Texture newTexture = loadTexture(url, mipmap);
         _texturesCache.put(textureKey, newTexture);
         return newTexture;
      }
   }


   private static Texture loadTexture(final URL url,
                                      final boolean mipmap) {
      if (url == null) {
         return null;
      }

      //      logger.info("Loading texture " + url + " (mipmap=" + mipmap + ")...");

      try {
         TextureData newTextureData;

         final boolean compress = true;
         if (compress && !url.toString().toLowerCase().endsWith("dds")) {
            // Configure a DDS compressor to generate mipmaps based according to the 'useMipMaps' parameter, and
            // convert the image URL to a compressed DDS format.
            final DXTCompressionAttributes attributes = DDSCompressor.getDefaultCompressionAttributes();
            attributes.setBuildMipmaps(mipmap);
            final ByteBuffer buffer = DDSCompressor.compressImageURL(url, attributes);

            newTextureData = TextureIO.newTextureData(WWIO.getInputStreamFromByteBuffer(buffer), mipmap, null);
         }
         else {
            // If the caller has disabled texture compression, or if the texture data is already a DDS file, then read
            // the texture data without converting it.
            newTextureData = TextureIO.newTextureData(url, mipmap, null);
         }


         //         TextureData newTextureData = TextureIO.newTextureData(url, mipmap, null);
         if (newTextureData.getMustFlipVertically()) {
            final BufferedImage image = ImageIO.read(url);
            ImageUtil.flipImageVertically(image);

            newTextureData = TextureIO.newTextureData(image, mipmap);
            newTextureData.setMustFlipVertically(false);
         }

         //         final Texture newTexture = TextureIO.newTexture(url, mipmap, null);
         final Texture newTexture = TextureIO.newTexture(newTextureData);

         if (!WWMath.isPowerOfTwo(newTexture.getImageHeight())) {
            System.out.println("\n\nWARNING: Texture " + url + ", height is not power of 2 (" + newTexture.getImageHeight() + ")");
         }
         if (!WWMath.isPowerOfTwo(newTexture.getImageWidth())) {
            System.out.println("\n\nWARNING: Texture " + url + ", width is not power of 2 (" + newTexture.getImageWidth() + ")");
         }

         //         logger.info("  Loaded texture " + url + " (mipmap=" + mipmap + "), result=" + newTexture);

         return newTexture;
      }
      catch (final IOException e) {
         logger.severe("Error loading texture " + url, e);
      }

      return null;
   }
}
