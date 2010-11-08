package es.igosoftware.globe.attributes;

import java.awt.Component;
import java.util.EventListener;

import es.igosoftware.globe.IGlobeApplication;
import es.igosoftware.globe.IGlobeLayer;
import es.igosoftware.util.GPair;


public interface ILayerAttribute<T> {

   public static interface IChangeListener {
      public void changed();
   }


   public boolean isVisible();


   public String getLabel();


   public GPair<Component, EventListener> createWidget(final IGlobeApplication application,
                                                       final IGlobeLayer layer);


   public void cleanupWidget(final IGlobeLayer layer,
                             final GPair<Component, EventListener> widget);


   public T get();


   public void set(final T value);


   public boolean isReadOnly();


   public void setListener(final IChangeListener listener);


   public void changed();

}
