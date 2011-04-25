

package es.igosoftware.euclid.experimental.vectorial.rendering.styledgeometries;

import java.awt.Paint;


public class GSurface2DStyle
         implements
            ISurface2DStyle {


   private final Paint _surfacePaint;
   private final Paint _lodIgnorePaint;


   public GSurface2DStyle(final Paint surfacePaint,
                          final Paint lodIgnorePaint) {
      _surfacePaint = surfacePaint;
      _lodIgnorePaint = lodIgnorePaint;
   }


   @Override
   public Paint getSurfacePaint() {
      return _surfacePaint;
   }


   @Override
   public Paint getLODIgnorePaint() {
      return _lodIgnorePaint;
   }

}
