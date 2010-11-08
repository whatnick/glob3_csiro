package es.igosoftware.globe;

import es.igosoftware.globe.layers.Feature;
import es.igosoftware.globe.layers.GVectorRenderer;

public interface IGlobeVectorLayer
         extends
            IGlobeLayer {


   public GField[] getFields();


   public GVectorRenderer getRenderer();


   public GVectorLayerType getShapeType();


   public Feature[] getFeatures();

}
