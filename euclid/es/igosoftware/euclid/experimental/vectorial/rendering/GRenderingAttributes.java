

package es.igosoftware.euclid.experimental.vectorial.rendering;

import java.awt.BasicStroke;
import java.awt.Color;


public class GRenderingAttributes {


   final boolean     _renderLODIgnores;
   final float       _borderWidth;
   final BasicStroke _borderStroke;
   final Color       _fillColor;
   final Color       _borderColor;

   final double      _lodMinSize;
   final boolean     _debugLODRendering;
   final boolean     _renderBounds;
   final int         _textureWidth;
   final int         _textureHeight;


   public GRenderingAttributes(final boolean renderLODIgnores,
                               final float borderWidth,
                               final Color fillColor,
                               final Color borderColor,
                               final double lodMinSize,
                               final boolean debugLODRendering,
                               final int textureWidth,
                               final int textureHeight,
                               final boolean renderBounds) {
      _renderLODIgnores = renderLODIgnores;
      _borderWidth = borderWidth;
      _fillColor = fillColor;
      _borderColor = borderColor;
      _lodMinSize = lodMinSize;
      _debugLODRendering = debugLODRendering;
      _textureWidth = textureWidth;
      _textureHeight = textureHeight;
      _renderBounds = renderBounds;

      _borderStroke = (_borderWidth > 0) ? new BasicStroke(borderWidth, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND) : null;
   }

}
