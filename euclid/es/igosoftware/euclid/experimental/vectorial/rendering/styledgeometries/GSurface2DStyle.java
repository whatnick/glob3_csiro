

package es.igosoftware.euclid.experimental.vectorial.rendering.styledgeometries;

import java.awt.Paint;


public class GSurface2DStyle
         implements
            ISurface2DStyle {


   private final Paint _surfacePaint;


   public GSurface2DStyle(final Paint surfacePaint) {
      _surfacePaint = surfacePaint;
   }


   @Override
   public Paint getSurfacePaint() {
      return _surfacePaint;
   }


}
