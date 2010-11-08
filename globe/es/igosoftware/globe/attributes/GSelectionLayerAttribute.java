package es.igosoftware.globe.attributes;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.EventListener;

import javax.swing.JComboBox;

import es.igosoftware.globe.IGlobeApplication;
import es.igosoftware.globe.IGlobeLayer;
import es.igosoftware.util.GPair;

public abstract class GSelectionLayerAttribute<T>
         extends
            GAbstractLayerAttribute<T> {


   private final Object[] _options;


   public GSelectionLayerAttribute(final String label,
                                   final String propertyName,
                                   final T[] options) {
      super(label, propertyName);

      _options = options;

   }


   public GSelectionLayerAttribute(final String label,
                                   final String propertyName,
                                   final boolean readOnly,
                                   final T[] options) {
      super(label, propertyName, readOnly);

      _options = options;
   }


   @Override
   public GPair<Component, EventListener> createWidget(final IGlobeApplication application,
                                                       final IGlobeLayer layer) {
      final JComboBox widget = new JComboBox(_options);
      widget.setSelectedItem(get());
      if (isReadOnly()) {
         widget.setEnabled(false);
      }
      else {
         widget.addActionListener(new ActionListener() {
            @SuppressWarnings("unchecked")
            @Override
            public void actionPerformed(final ActionEvent e) {
               set((T) widget.getSelectedItem());
               //widget.setBackground(newColor);
            }
         });
      }

      setListener(new IChangeListener() {
         @Override
         public void changed() {
            //widget.setBackground(get(layer));
         }
      });

      final EventListener listener = subscribeToEvents(layer);

      return new GPair<Component, EventListener>(widget, listener);
   }


   @Override
   public void cleanupWidget(final IGlobeLayer layer,
                             final GPair<Component, EventListener> widget) {
      setListener(null);

      unsubscribeFromEvents(layer, widget._second);
   }
}
