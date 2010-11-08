package es.igosoftware.globe.layers;

import gov.nasa.worldwind.layers.AbstractLayer;
import gov.nasa.worldwind.render.DrawContext;

import java.util.ArrayList;
import java.util.List;

public class GHUDLayer
         extends
            AbstractLayer {


   private final List<IHUDElement> _elements = new ArrayList<IHUDElement>();


   @Override
   protected void doRender(final DrawContext dc) {
      for (final IHUDElement each : _elements) {
         if (each.isEnable()) {
            dc.addOrderedRenderable(each);
         }
      }
   }


   public void addElement(final IHUDElement orderedRenderable) {
      _elements.add(orderedRenderable);
   }

}
