

package es.igosoftware.euclid.experimental.vectorial.rendering.styledgeometries;

import java.awt.Paint;
import java.awt.Stroke;


public class GNullCurve2DStyle
         implements
            ICurve2DStyle {


   public static final GNullCurve2DStyle INSTANCE = new GNullCurve2DStyle();


   private GNullCurve2DStyle() {
   }


   @Override
   public Stroke getBorderStroke() {
      return null;
   }


   @Override
   public Paint getBorderPaint() {
      return null;
   }

}
