

package es.igosoftware.euclid.experimental.vectorial.rendering.styledgeometries;

import java.awt.Paint;
import java.awt.Stroke;


public class GCurve2DStyle
         implements
            ICurve2DStyle {


   private final Stroke _borderStroke;
   private final Paint  _borderPaint;


   public GCurve2DStyle(final Stroke borderStroke,
                        final Paint borderPaint) {
      _borderStroke = borderStroke;
      _borderPaint = borderPaint;
   }


   @Override
   public Stroke getBorderStroke() {
      return _borderStroke;
   }


   @Override
   public Paint getBorderPaint() {
      return _borderPaint;
   }


}
