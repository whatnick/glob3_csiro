

package es.igosoftware.euclid.experimental.vectorial.rendering;

import java.awt.Color;


public abstract class GSymbol {


   public abstract boolean isBiggerThan(final double lodMinSize);


   public abstract void draw(final Color fillColor,
                             final float borderWidth,
                             final Color borderColor,
                             final GVectorialRenderingContext rc);


}
