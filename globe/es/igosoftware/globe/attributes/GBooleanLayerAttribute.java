package es.igosoftware.globe.attributes;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.EventListener;

import javax.swing.JCheckBox;

import es.igosoftware.globe.IGlobeApplication;
import es.igosoftware.globe.IGlobeLayer;
import es.igosoftware.util.GPair;


public abstract class GBooleanLayerAttribute
         extends
            GAbstractLayerAttribute<Boolean> {


   public GBooleanLayerAttribute(final String label) {
      this(label, false);
   }


   public GBooleanLayerAttribute(final String label,
                                 final boolean readOnly) {
      super(label, readOnly);
   }


   public GBooleanLayerAttribute(final String label,
                                 final String propertyName) {
      super(label, propertyName);
   }


   public GBooleanLayerAttribute(final String label,
                                 final String propertyName,
                                 final boolean readOnly) {
      super(label, propertyName, readOnly);
   }


   @Override
   public GPair<Component, EventListener> createWidget(final IGlobeApplication application,
                                                       final IGlobeLayer layer) {
      final JCheckBox widget = new JCheckBox();
      widget.setSelected(get());

      if (isReadOnly()) {
         widget.setEnabled(false);
      }
      else {
         widget.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
               set(widget.isSelected());
            }
         });
      }

      setListener(new IChangeListener() {
         @Override
         public void changed() {
            widget.setSelected(get());
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
