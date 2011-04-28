

package es.igosoftware.euclid.experimental.vectorial.rendering.styledgeometries;

import java.awt.Paint;
import java.awt.Stroke;


public interface ICurve2DStyle
         extends
            IStyle {


   public Stroke getBorderStroke();


   public Paint getBorderPaint();


   public boolean isGroupableWith(final ICurve2DStyle that);


}
