package es.igosoftware.globe.attributes;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.EventListener;

import javax.swing.JTextField;

import es.igosoftware.globe.IGlobeApplication;
import es.igosoftware.globe.IGlobeLayer;
import es.igosoftware.util.GPair;


public abstract class GStringLayerAttribute
         extends
            GAbstractLayerAttribute<String> {


   public GStringLayerAttribute(final String label) {
      this(label, false);
   }


   public GStringLayerAttribute(final String label,
                                final boolean readOnly) {
      super(label, readOnly);
   }


   public GStringLayerAttribute(final String label,
                                final String propertyName) {
      super(label, propertyName);
   }


   public GStringLayerAttribute(final String label,
                                final String propertyName,
                                final boolean readOnly) {
      super(label, propertyName, readOnly);
   }


   @Override
   public GPair<Component, EventListener> createWidget(final IGlobeApplication application,
                                                       final IGlobeLayer layer) {
      final JTextField widget = new JTextField();
      widget.setMinimumSize(new Dimension(100, 20));
      widget.setText(get());

      if (isReadOnly()) {
         widget.setEnabled(false);
      }
      else {
         widget.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
               set(widget.getText());
            }
         });
      }

      setListener(new IChangeListener() {
         @Override
         public void changed() {
            widget.setText(get());
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


   //   private final EventListener subscribeToEvents(final IGlobeLayer _layer) {
   //      final String propertyName = getPropertyName();
   //      if (propertyName == null) {
   //         return null;
   //      }
   //
   //      final PropertyChangeListener listener = new PropertyChangeListener() {
   //         @Override
   //         public void propertyChange(final PropertyChangeEvent evt) {
   //            changed();
   //         }
   //      };
   //      _layer.addPropertyChangeListener(propertyName, listener);
   //      return listener;
   //   }
   //
   //
   //   private final void unsubscribeFromEvents(final IGlobeLayer _layer,
   //                                            final EventListener listener) {
   //      final String propertyName = getPropertyName();
   //      if ((listener == null) || (propertyName == null)) {
   //         return;
   //      }
   //
   //      _layer.removePropertyChangeListener(propertyName, (PropertyChangeListener) listener);
   //   }

}
