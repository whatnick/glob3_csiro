

package es.igosoftware.euclid.experimental.vectorial.rendering;

import es.igosoftware.euclid.colors.IColor;
import es.igosoftware.euclid.experimental.measurement.GLength;
import es.igosoftware.euclid.experimental.measurement.IMeasure;


public interface IRenderingStyle {


   /* point style */
   public IMeasure<GLength> getPointSize();


   public IColor getPointColor();


   public float getPointOpacity();


   /* curve style */
   public IMeasure<GLength> getCurveWidth();


   /* surface style */
   public IMeasure<GLength> getSurfaceBorderWidth();


}
