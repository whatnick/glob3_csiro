

package es.igosoftware.globe;

import es.igosoftware.euclid.vector.IVector2;
import es.igosoftware.experimental.vectorial.GGlobeVectorial2DRenderingStyle;


public interface IGlobeVector2Layer
         extends
            IGlobeVectorLayer<IVector2> {


   @Override
   public GGlobeVectorial2DRenderingStyle getRenderingStyle();


   public void clearCache();


}
