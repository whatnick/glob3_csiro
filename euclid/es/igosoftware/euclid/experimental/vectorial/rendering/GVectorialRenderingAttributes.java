

package es.igosoftware.euclid.experimental.vectorial.rendering;

import java.awt.Color;

import es.igosoftware.euclid.utils.GColorUtil;
import es.igosoftware.util.GAssert;


public class GVectorialRenderingAttributes {


   public final boolean _renderLODIgnores;
   public final float   _borderWidth;
   public final Color   _fillColor;
   public final Color   _borderColor;

   public final double  _lodMinSize;
   public final boolean _debugLODRendering;
   public final boolean _renderBounds;
   public final int     _imageWidth;
   public final int     _imageHeight;
   public final Color   _lodColor;


   public GVectorialRenderingAttributes(final boolean renderLODIgnores,
                                        final float borderWidth,
                                        final Color fillColor,
                                        final Color borderColor,
                                        final double lodMinSize,
                                        final boolean debugLODRendering,
                                        final int imageWidth,
                                        final int imageHeight,
                                        final boolean renderBounds) {
      GAssert.isPositiveOrZero(borderWidth, "borderWidth");
      GAssert.isPositive(imageWidth, "imageWidth");
      GAssert.isPositive(imageHeight, "imageHeight");

      _renderLODIgnores = renderLODIgnores;
      _borderWidth = borderWidth;
      _fillColor = fillColor;
      _borderColor = borderColor;
      _lodMinSize = lodMinSize;
      _debugLODRendering = debugLODRendering;
      _imageWidth = imageWidth;
      _imageHeight = imageHeight;
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
      result = prime * result + _imageHeight;
      result = prime * result + _imageWidth;
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
      final GVectorialRenderingAttributes other = (GVectorialRenderingAttributes) obj;
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
      if (_imageHeight != other._imageHeight) {
         return false;
      }
      if (_imageWidth != other._imageWidth) {
         return false;
      }
      return true;
   }


   public String uniqueName() {
      return (_renderLODIgnores ? "t" : "f") + _borderWidth + Integer.toHexString(_fillColor.getRGB())
             + Integer.toHexString(_borderColor.getRGB()) + _lodMinSize + (_debugLODRendering ? "t" : "f")
             + (_renderBounds ? "t" : "f") + Integer.toHexString(_imageWidth) + Integer.toHexString(_imageHeight)
             + Integer.toHexString(_lodColor.getRGB());
   }


   @Override
   public String toString() {
      return "GRenderingAttributes [_renderLODIgnores=" + _renderLODIgnores + ", _borderWidth=" + _borderWidth + ", _fillColor="
             + _fillColor + ", _borderColor=" + _borderColor + ", _lodMinSize=" + _lodMinSize + ", _debugLODRendering="
             + _debugLODRendering + ", _renderBounds=" + _renderBounds + ", _imageWidth=" + _imageWidth + ", _imageHeight="
             + _imageHeight + ", _lodColor=" + _lodColor + "]";
   }

}
