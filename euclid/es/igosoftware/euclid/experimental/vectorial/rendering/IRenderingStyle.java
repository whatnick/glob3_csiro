

package es.igosoftware.euclid.experimental.vectorial.rendering;

import es.igosoftware.euclid.colors.IColor;
import es.igosoftware.euclid.experimental.measurement.GLength;
import es.igosoftware.euclid.experimental.measurement.IMeasure;


public interface IRenderingStyle {

   /* general */
   public boolean isDebugRendering();


   public boolean isRenderLODIgnores();


   public double getLODMinSize();


   public IColor getLODColor();


   public String uniqueName();


   /* point style */
   public IMeasure<GLength> getPointSize();


   public IColor getPointColor();


   public float getPointOpacity();


   //   /* curve style */
   //   public IMeasure<GLength> getCurveWidth();
   //
   //
   //   /* surface style */
   //   public IMeasure<GLength> getSurfaceBorderWidth();


}
