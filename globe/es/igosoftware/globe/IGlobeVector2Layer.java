

package es.igosoftware.globe;

import es.igosoftware.euclid.vector.IVector2;
import es.igosoftware.globe.layers.GVector2RenderingTheme;


public interface IGlobeVector2Layer
         extends
            IGlobeVectorLayer<IVector2> {


   public GVector2RenderingTheme getRenderingTheme();


}
