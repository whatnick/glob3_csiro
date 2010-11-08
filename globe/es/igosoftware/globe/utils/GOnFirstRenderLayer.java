package es.igosoftware.globe.utils;

import gov.nasa.worldwind.layers.AbstractLayer;
import gov.nasa.worldwind.render.DrawContext;


public abstract class GOnFirstRenderLayer
         extends
            AbstractLayer {

   protected abstract void execute(final DrawContext dc);


   @Override
   protected void doRender(final DrawContext dc) {
      dc.getLayers().remove(this);

      execute(dc);
   }
}
