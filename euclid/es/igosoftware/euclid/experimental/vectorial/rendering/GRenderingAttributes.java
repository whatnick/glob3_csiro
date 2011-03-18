

package es.igosoftware.euclid.experimental.vectorial.rendering;

import java.awt.Color;

import es.igosoftware.euclid.utils.GColorUtil;
import es.igosoftware.util.GAssert;


public class GRenderingAttributes {


   public final boolean _renderLODIgnores;
   public final float   _borderWidth;
   public final Color   _fillColor;
   public final Color   _borderColor;

   public final double  _lodMinSize;
   public final boolean _debugLODRendering;
   public final boolean _renderBounds;
   public final int     _textureWidth;
   public final int     _textureHeight;
   public final Color   _lodColor;


   public GRenderingAttributes(final boolean renderLODIgnores,
                               final float borderWidth,
                               final Color fillColor,
                               final Color borderColor,
                               final double lodMinSize,
                               final boolean debugLODRendering,
                               final int textureWidth,
                               final int textureHeight,
                               final boolean renderBounds) {
      GAssert.isPositiveOrZero(borderWidth, "borderWidth");
      GAssert.isPositive(textureWidth, "textureWidth");
      GAssert.isPositive(textureHeight, "textureHeight");

      _renderLODIgnores = renderLODIgnores;
      _borderWidth = borderWidth;
      _fillColor = fillColor;
      _borderColor = borderColor;
      _lodMinSize = lodMinSize;
      _debugLODRendering = debugLODRendering;
      _textureWidth = textureWidth;
      _textureHeight = textureHeight;
      _renderBounds = renderBounds;

      _lodColor = GColorUtil.mix(_borderColor, _fillColor, 0.75f);

      //      _borderStroke = (_borderWidth > 0) ? new BasicStroke(borderWidth, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND) : null;
   }


   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((_borderColor == null) ? 0 : _borderColor.hashCode());
      result = prime * result + Float.floatToIntBits(_borderWidth);
      result = prime * result + (_debugLODRendering ? 1231 : 1237);
      result = prime * result + ((_fillColor == null) ? 0 : _fillColor.hashCode());
      long temp;
      temp = Double.doubleToLongBits(_lodMinSize);
      result = prime * result + (int) (temp ^ (temp >>> 32));
      result = prime * result + (_renderBounds ? 1231 : 1237);
      result = prime * result + (_renderLODIgnores ? 1231 : 1237);
      result = prime * result + _textureHeight;
      result = prime * result + _textureWidth;
      return result;
   }


   @Override
   public boolean equals(final Object obj) {
      if (this == obj) {
         return true;
      }
      if (obj == null) {
         return false;
      }
      if (getClass() != obj.getClass()) {
         return false;
      }
      final GRenderingAttributes other = (GRenderingAttributes) obj;
      if (_borderColor == null) {
         if (other._borderColor != null) {
            return false;
         }
      }
      else if (!_borderColor.equals(other._borderColor)) {
         return false;
      }
      if (Float.floatToIntBits(_borderWidth) != Float.floatToIntBits(other._borderWidth)) {
         return false;
      }
      if (_debugLODRendering != other._debugLODRendering) {
         return false;
      }
      if (_fillColor == null) {
         if (other._fillColor != null) {
            return false;
         }
      }
      else if (!_fillColor.equals(other._fillColor)) {
         return false;
      }
      if (Double.doubleToLongBits(_lodMinSize) != Double.doubleToLongBits(other._lodMinSize)) {
         return false;
      }
      if (_renderBounds != other._renderBounds) {
         return false;
      }
      if (_renderLODIgnores != other._renderLODIgnores) {
         return false;
      }
      if (_textureHeight != other._textureHeight) {
         return false;
      }
      if (_textureWidth != other._textureWidth) {
         return false;
      }
      return true;
   }


   public String uniqueName() {
      return (_renderLODIgnores ? "t" : "f") + _borderWidth + Integer.toHexString(_fillColor.getRGB())
             + Integer.toHexString(_borderColor.getRGB()) + _lodMinSize + (_debugLODRendering ? "t" : "f")
             + (_renderBounds ? "t" : "f") + Integer.toHexString(_textureWidth) + Integer.toHexString(_textureHeight)
             + Integer.toHexString(_lodColor.getRGB());
   }


   @Override
   public String toString() {
      return "GRenderingAttributes [_renderLODIgnores=" + _renderLODIgnores + ", _borderWidth=" + _borderWidth + ", _fillColor="
             + _fillColor + ", _borderColor=" + _borderColor + ", _lodMinSize=" + _lodMinSize + ", _debugLODRendering="
             + _debugLODRendering + ", _renderBounds=" + _renderBounds + ", _textureWidth=" + _textureWidth + ", _textureHeight="
             + _textureHeight + ", _lodColor=" + _lodColor + "]";
   }

}
