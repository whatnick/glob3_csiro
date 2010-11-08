package es.igosoftware.globe.attributes;

import java.awt.Component;
import java.util.EventListener;

import javax.swing.JSeparator;
import javax.swing.SwingConstants;

import es.igosoftware.globe.IGlobeApplication;
import es.igosoftware.globe.IGlobeLayer;
import es.igosoftware.util.GPair;

public class GSeparatorAttribute
         implements
            ILayerAttribute<Object> {

   @Override
   public boolean isVisible() {
      return true;
   }


   @Override
   public String getLabel() {
      return null;
   }


   @Override
   public GPair<Component, EventListener> createWidget(final IGlobeApplication application,
                                                       final IGlobeLayer layer) {
      final JSeparator separator = new JSeparator(SwingConstants.HORIZONTAL);
      //      final JLabel separator = new JLabel(" ");
      return new GPair<Component, EventListener>(separator, null);
   }


   @Override
   public void cleanupWidget(final IGlobeLayer layer,
                             final GPair<Component, EventListener> widget) {

   }


   @Override
   public Object get() {
      return null;
   }


   @Override
   public void set(final Object value) {}


   @Override
   public boolean isReadOnly() {
      return true;
   }


   @Override
   public void setListener(final es.igosoftware.globe.attributes.ILayerAttribute.IChangeListener listener) {

   }


   @Override
   public void changed() {

   }
}
