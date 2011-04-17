

package es.igosoftware.euclid.experimental.vectorial.rendering;

import java.awt.Color;

import es.igosoftware.util.GAssert;


public class GVectorialRenderingAttributes {


   public final float _borderWidth;
   public final Color _fillColor;
   public final Color _borderColor;


   public GVectorialRenderingAttributes(final float borderWidth,
                                        final Color fillColor,
                                        final Color borderColor) {
      GAssert.isPositiveOrZero(borderWidth, "borderWidth");

      _borderWidth = borderWidth;
      _fillColor = fillColor;
      _borderColor = borderColor;
   }


   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((_borderColor == null) ? 0 : _borderColor.hashCode());
      result = prime * result + Float.floatToIntBits(_borderWidth);
      result = prime * result + ((_fillColor == null) ? 0 : _fillColor.hashCode());
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
      if (_fillColor == null) {
         if (other._fillColor != null) {
            return false;
         }
      }
      else if (!_fillColor.equals(other._fillColor)) {
         return false;
      }
      return true;
   }


   public String uniqueName() {
      return _borderWidth + Integer.toHexString(_fillColor.getRGB()) + Integer.toHexString(_borderColor.getRGB());
   }


   @Override
   public String toString() {
      return "GRenderingAttributes [_borderWidth=" + _borderWidth + ", _fillColor=" + _fillColor + ", _borderColor="
             + _borderColor + "]";
   }

}
