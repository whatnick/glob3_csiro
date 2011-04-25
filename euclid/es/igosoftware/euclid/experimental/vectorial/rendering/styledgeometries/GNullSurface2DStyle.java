

package es.igosoftware.euclid.experimental.vectorial.rendering.styledgeometries;

import java.awt.Paint;


public class GNullSurface2DStyle
         implements
            ISurface2DStyle {


   public static final GNullSurface2DStyle INSTANCE = new GNullSurface2DStyle();


   private GNullSurface2DStyle() {
   }


   @Override
   public Paint getSurfacePaint() {
      return null;
   }


}
