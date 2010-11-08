package es.igosoftware.globe.layers;

import gov.nasa.worldwind.render.OrderedRenderable;

public interface IHUDElement
         extends
            OrderedRenderable {

   public boolean isEnable();

}
