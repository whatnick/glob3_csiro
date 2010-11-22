

package es.igosoftware.euclid.experimental.vectorial.rendering;

import java.awt.BasicStroke;
import java.awt.Color;


public class GRenderingAttributes {

   //
   //   private static final double  DEFAULT_LOD_MIN_SIZE      = 4;
   //   private static final boolean DEFAULT_DEBUG_RENDERING   = true;
   //   private static final int     DEFAULT_TEXTURE_DIMENSION = 512;
   //   private static final boolean DEFAULT_RENDER_BOUNDS     = true;


   final boolean     _renderLeafs;
   final int         _maxDepth;
   final boolean     _renderLODIgnores;
   final float       _borderWidth;
   final BasicStroke _stroke;
   final Color       _fillColor;
   final Color       _borderColor;

   final double      _lodMinSize;
   final boolean     _debugLODRendering;
   final int         _textureDimension;
   final boolean     _renderBounds;


   //   public GRenderingAttributes(final boolean renderLeafs,
   //                               final int maxDepth,
   //                               final boolean renderLODIgnores,
   //                               final float borderWidth,
   //                               final Color fillColor,
   //                               final Color borderColor) {
   //      this(renderLeafs, maxDepth, renderLODIgnores, borderWidth, fillColor, borderColor, DEFAULT_LOD_MIN_SIZE,
   //           DEFAULT_DEBUG_RENDERING, DEFAULT_TEXTURE_DIMENSION, DEFAULT_RENDER_BOUNDS);
   //   }


   public GRenderingAttributes(final boolean renderLeafs,
                               final int maxDepth,
                               final boolean renderLODIgnores,
                               final float borderWidth,
                               final Color fillColor,
                               final Color borderColor,
                               final double lodMinSize,
                               final boolean debugLODRendering,
                               final int textureDimension,
                               final boolean renderBounds) {
      _renderLeafs = renderLeafs;
      _maxDepth = maxDepth;
      _renderLODIgnores = renderLODIgnores;
      _borderWidth = borderWidth;
      _fillColor = fillColor;
      _borderColor = borderColor;
      _lodMinSize = lodMinSize;
      _debugLODRendering = debugLODRendering;
      _textureDimension = textureDimension;
      _renderBounds = renderBounds;

      _stroke = (_borderWidth > 0) ? new BasicStroke(borderWidth) : null;
   }

}
