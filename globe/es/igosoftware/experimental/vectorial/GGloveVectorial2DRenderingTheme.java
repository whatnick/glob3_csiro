

package es.igosoftware.experimental.vectorial;

import java.util.Arrays;
import java.util.List;

import es.igosoftware.globe.IGlobeApplication;
import es.igosoftware.globe.IGlobeLayer;
import es.igosoftware.globe.IGlobeRenderingTheme;
import es.igosoftware.globe.IGlobeVector2Layer;
import es.igosoftware.globe.attributes.GStringLayerAttribute;
import es.igosoftware.globe.attributes.ILayerAttribute;
import es.igosoftware.util.GAssert;


public class GGloveVectorial2DRenderingTheme
         implements
            IGlobeRenderingTheme {


   @Override
   public List<? extends ILayerAttribute<?>> getLayerAttributes(final IGlobeApplication application,
                                                                final IGlobeLayer layer) {
      GAssert.isInstanceOf(layer, IGlobeVector2Layer.class, "layer");


      final int _______________Diego_at_work_______________;

      final IGlobeVector2Layer vector2Layer = (IGlobeVector2Layer) layer;


      final GStringLayerAttribute dummy = new GStringLayerAttribute("Rendering Theme for: " + vector2Layer.getName()) {
         @Override
         public void set(final String value) {
         }


         @Override
         public boolean isVisible() {
            return true;
         }


         @Override
         public String get() {
            return "";
         }
      };


      return Arrays.asList(dummy);
   }


}
