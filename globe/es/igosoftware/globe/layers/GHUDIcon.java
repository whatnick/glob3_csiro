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


package es.igosoftware.globe.layers;

import java.awt.Point;
import java.awt.Rectangle;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.media.opengl.GL;

import com.sun.opengl.util.texture.Texture;
import com.sun.opengl.util.texture.TextureCoords;
import com.sun.opengl.util.texture.TextureIO;

import es.igosoftware.util.Logger;
import gov.nasa.worldwind.exception.WWRuntimeException;
import gov.nasa.worldwind.geom.Vec4;
import gov.nasa.worldwind.render.DrawContext;
import gov.nasa.worldwind.util.Logging;


public class GHUDIcon
         implements
            IHUDElement {

   private static final Logger logger = Logger.instance();


   public static enum Position {
      NORTHWEST,
      SOUTHWEST,
      NORTHEAST,
      SOUTHEAST;
   }


   public static enum ResizeBehavior {
      RESIZE_STRETCH,
      RESIZE_SHRINK_ONLY,
      RESIZE_KEEP_FIXED_SIZE;
   }


   private String               _iconFileName;
   private int                  _iconWidth;
   private int                  _iconHeight;
   private final double         _iconScale       = 1.0;
   private final double         _toViewportScale = 1.0;
   private final ResizeBehavior _resizeBehavior;
   private final Position       _position;
   private int                  _borderWidth     = 20;
   private int                  _borderHeight    = 20;
   private double               _opacity         = 1;

   private boolean              _isEnable        = true;
   private double               _distanceFromEye = 0;


   public GHUDIcon(final String iconFileName) {
      this(iconFileName, Position.SOUTHWEST, ResizeBehavior.RESIZE_SHRINK_ONLY);
   }


   public GHUDIcon(final String iconFileName,
                   final Position position) {
      this(iconFileName, position, ResizeBehavior.RESIZE_SHRINK_ONLY);
   }


   public GHUDIcon(final String iconFileName,
                   final Position position,
                   final ResizeBehavior resizeBehavior) {
      _iconFileName = iconFileName;
      _position = position;
      _resizeBehavior = resizeBehavior;
   }


   @Override
   public double getDistanceFromEye() {
      return _distanceFromEye;
   }


   @Override
   public void pick(final DrawContext dc,
                    final Point pickPoint) {
      drawIcon(dc);
   }


   @Override
   public void render(final DrawContext dc) {
      drawIcon(dc);
   }


   private void drawIcon(final DrawContext dc) {
      if (_iconFileName == null) {
         return;
      }

      final GL gl = dc.getGL();

      boolean attribsPushed = false;
      boolean modelviewPushed = false;
      boolean projectionPushed = false;

      try {
         gl.glPushAttrib(GL.GL_DEPTH_BUFFER_BIT | GL.GL_COLOR_BUFFER_BIT | GL.GL_ENABLE_BIT | GL.GL_TEXTURE_BIT
                         | GL.GL_TRANSFORM_BIT | GL.GL_VIEWPORT_BIT | GL.GL_CURRENT_BIT);
         attribsPushed = true;

         // Initialize texture if not done yet 
         Texture iconTexture = dc.getTextureCache().get(_iconFileName);
         if (iconTexture == null) {
            initializeTexture(dc);
            iconTexture = dc.getTextureCache().get(_iconFileName);
            if (iconTexture == null) {
               logger.warning("Can't load icon \"" + _iconFileName + "\"");
               return;
            }
         }

         gl.glEnable(GL.GL_BLEND);
         gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE);
         gl.glDisable(GL.GL_DEPTH_TEST);

         final double width = getScaledIconWidth();
         final double height = getScaledIconHeight();

         // Load a parallel projection with xy dimensions (viewportWidth, viewportHeight)
         // into the GL projection matrix.
         final Rectangle viewport = dc.getView().getViewport();
         gl.glMatrixMode(GL.GL_PROJECTION);
         gl.glPushMatrix();
         projectionPushed = true;
         gl.glLoadIdentity();
         final double maxwh = width > height ? width : height;
         gl.glOrtho(0d, viewport.width, 0d, viewport.height, -0.6 * maxwh, 0.6 * maxwh);

         gl.glMatrixMode(GL.GL_MODELVIEW);
         gl.glPushMatrix();
         modelviewPushed = true;
         gl.glLoadIdentity();

         // Translate and scale
         final double scale = computeScale(viewport);
         final Vec4 locationSW = computeLocation(viewport, scale);
         gl.glTranslated(locationSW.x(), locationSW.y(), locationSW.z());
         // Scale to 0..1 space
         gl.glScaled(scale, scale, 1);
         gl.glScaled(width, height, 1d);

         if (!dc.isPickingMode()) {
            // Draw world map icon
            gl.glColor4d(1d, 1d, 1d, getOpacity());
            gl.glEnable(GL.GL_TEXTURE_2D);
            iconTexture.bind();

            final TextureCoords texCoords = iconTexture.getImageTexCoords();
            dc.drawUnitQuad(texCoords);
         }
      }
      finally {
         if (projectionPushed) {
            gl.glMatrixMode(GL.GL_PROJECTION);
            gl.glPopMatrix();
         }
         if (modelviewPushed) {
            gl.glMatrixMode(GL.GL_MODELVIEW);
            gl.glPopMatrix();
         }
         if (attribsPushed) {
            gl.glPopAttrib();
         }
      }
   }


   private void initializeTexture(final DrawContext dc) {
      Texture iconTexture = dc.getTextureCache().get(_iconFileName);
      if (iconTexture != null) {
         return;
      }

      try {
         InputStream iconStream = getClass().getResourceAsStream(_iconFileName);
         if (iconStream == null) {
            final File iconFile = new File(_iconFileName);
            if (iconFile.exists()) {
               iconStream = new FileInputStream(iconFile);
            }
         }

         iconTexture = TextureIO.newTexture(iconStream, true, null);
         iconTexture.bind();
         _iconWidth = iconTexture.getWidth();
         _iconHeight = iconTexture.getHeight();
         dc.getTextureCache().put(_iconFileName, iconTexture);
      }
      catch (final IOException e) {
         final String msg = Logging.getMessage("layers.IOExceptionDuringInitialization");
         Logging.logger().severe(msg);
         throw new WWRuntimeException(msg, e);
      }

      final GL gl = dc.getGL();
      gl.glTexEnvf(GL.GL_TEXTURE_ENV, GL.GL_TEXTURE_ENV_MODE, GL.GL_MODULATE);
      gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, GL.GL_LINEAR_MIPMAP_LINEAR);
      gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, GL.GL_LINEAR);
      gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_S, GL.GL_CLAMP_TO_EDGE);
      gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_T, GL.GL_CLAMP_TO_EDGE);

      // Enable texture anisotropy, improves "tilted" world map quality.
      /*int[] maxAnisotropy = new int[1];
      gl
      		.glGetIntegerv(GL.GL_MAX_TEXTURE_MAX_ANISOTROPY_EXT,
      				maxAnisotropy, 0);
      gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAX_ANISOTROPY_EXT,
      		maxAnisotropy[0]);*/
   }


   private double getScaledIconWidth() {
      return _iconWidth * _iconScale;
   }


   private double getScaledIconHeight() {
      return _iconHeight * _iconScale;
   }


   private double computeScale(final Rectangle viewport) {

      switch (_resizeBehavior) {
         case RESIZE_SHRINK_ONLY:
            return Math.min(1d, (_toViewportScale) * viewport.width / getScaledIconWidth());
         case RESIZE_STRETCH:
            return (_toViewportScale) * viewport.width / getScaledIconWidth();
         case RESIZE_KEEP_FIXED_SIZE:
            return 1d;
         default:
            return 1d;
      }

      //      if (_resizeBehavior.equals(RESIZE_SHRINK_ONLY)) {
      //         return Math.min(1d, (_toViewportScale) * viewport.width / getScaledIconWidth());
      //      }
      //      else if (_resizeBehavior.equals(RESIZE_STRETCH)) {
      //         return (_toViewportScale) * viewport.width / getScaledIconWidth();
      //      }
      //      else if (_resizeBehavior.equals(RESIZE_KEEP_FIXED_SIZE)) {
      //         return 1d;
      //      }
      //      else {
      //         return 1d;
      //      }

   }


   private Vec4 computeLocation(final Rectangle viewport,
                                final double scale) {
      final double width = getScaledIconWidth();
      final double height = getScaledIconHeight();

      final double scaledWidth = scale * width;
      final double scaledHeight = scale * height;

      double x = 0;
      double y = 0;

      switch (_position) {
         case NORTHEAST:
            x = viewport.getWidth() - scaledWidth - _borderWidth;
            y = viewport.getHeight() - scaledHeight - _borderHeight;
            break;
         case SOUTHEAST:
            x = viewport.getWidth() - scaledWidth - _borderWidth;
            y = 0d + _borderHeight;
            break;
         case NORTHWEST:
            x = 0d + _borderWidth;
            y = viewport.getHeight() - scaledHeight - _borderHeight;
            break;
         case SOUTHWEST:
            x = 0d + _borderWidth;
            y = 0d + _borderHeight;
            break;
      }

      return new Vec4(x, y, 0);
   }


   public double getOpacity() {
      return _opacity;
   }


   public void setOpacity(final double opacity) {
      _opacity = opacity;
   }


   public int getBorderWidth() {
      return _borderWidth;
   }


   public void setBorderWidth(final int borderWidth) {
      _borderWidth = borderWidth;
   }


   public int getBorderHeight() {
      return _borderHeight;
   }


   public void setBorderHeight(final int borderHeight) {
      _borderHeight = borderHeight;
   }


   @Override
   public boolean isEnable() {
      return _isEnable;
   }


   public void setEnable(final boolean isEnable) {
      _isEnable = isEnable;
   }


   public String getIconFileName() {
      return _iconFileName;
   }


   public void setIconFileName(final String iconFileName) {
      _iconFileName = iconFileName;
   }


   public void setDistanceFromEye(final double distanceFromEye) {
      _distanceFromEye = distanceFromEye;
   }

}
